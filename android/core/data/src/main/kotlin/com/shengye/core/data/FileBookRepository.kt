package com.shengye.core.data

import com.shengye.contract.Book
import com.shengye.contract.BookRepository
import com.shengye.contract.Chapter
import com.shengye.domain.ChapterSplitter
import com.shengye.domain.Chunker
import com.shengye.domain.StorageLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * 书库落盘实现。所有路径都从 [StorageLayout] 来，不自己拼字符串。
 *
 * @param root 应用私有目录（context.filesDir），测试时传临时目录
 */
class FileBookRepository(private val root: File) : BookRepository {

    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) { block() }

    private fun f(rel: String) = File(root, rel)

    override suspend fun list(): List<Book> = io {
        val booksDir = f("books")
        val dirs = booksDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        dirs.mapNotNull { d ->
            BookJson.decode(Files2.readTextOrNull(File(d, "book.json")) ?: return@mapNotNull null)?.book
        }.sortedWith(compareByDescending<Book> { if (it.pinned) 1 else 0 }.thenByDescending { it.createdAt })
    }

    override suspend fun get(id: String): Book? = io {
        if (!StorageLayout.isValidId(id)) return@io null
        BookJson.decode(Files2.readTextOrNull(f(StorageLayout.bookJson(id))) ?: return@io null)?.book
    }

    override suspend fun readText(id: String): String = io {
        val src = f(StorageLayout.sourceFile(id))
        Files2.readTextOrNull(src) ?: ""
    }

    override suspend fun chapters(id: String): List<Chapter> = io {
        BookJson.decode(Files2.readTextOrNull(f(StorageLayout.bookJson(id))) ?: return@io emptyList())
            ?.chapters ?: emptyList()
    }

    override suspend fun import(path: String): Result<Book> =
        runCatching {
            val src = File(path)
            require(src.isFile) { "找不到文件" }
            importBytes(src.name, src.readBytes()).getOrThrow()
        }

    /**
     * 统一入口：字节进，书出。
     * SAF 选文件给的是 content URI（读不到路径），粘贴区给的是一段字——
     * 两条路最后都到这里，所以解码与分章只写一份。
     */
    suspend fun importBytes(fileName: String, bytes: ByteArray): Result<Book> = io {
        runCatching {
            val text = readSmart(bytes)
            require(text.isNotBlank()) { "文件是空的" }
            createBook(fileName.substringBeforeLast('.').ifBlank { "未命名" }, text)
        }
    }

    /** 粘贴区：没有文件名，用正文前 12 个字当书名（去空白，不然书名会很难看）。 */
    suspend fun importText(text: String): Result<Book> = io {
        runCatching {
            require(text.isNotBlank()) { "没有内容" }
            val title = text.trim().replace(Regex("\\s+"), "").take(12).ifBlank { "片段" }
            createBook(title, text)
        }
    }

    private fun createBook(title: String, text: String): Book {
        val id = newId()
        val now = System.currentTimeMillis()
        Files2.writeTextAtomic(f(StorageLayout.sourceFile(id)), text)

        val chapters = ChapterSplitter.split(text, id)
        val hashes = chapters.map { c ->
            Chunker.split(text.substring(c.startOffset, c.endOffset)).map { Files2.shortHash(it) }
        }
        val book = Book(
            id = id, title = title, sourcePath = StorageLayout.sourceFile(id),
            chars = text.length, chapterCount = chapters.size, createdAt = now,
        )
        Files2.writeTextAtomic(
            f(StorageLayout.bookJson(id)),
            BookJson.encode(StoredBook(book, chapters, hashes, StorageLayout.CHUNK_RULE)),
        )
        return book
    }

    /** 重新分章：原文不动，章表与块指纹全部重算（旧的块音频因此作废，这是对的）。 */
    override suspend fun reSplit(id: String, ruleId: String): List<Chapter> = io {
        val stored = BookJson.decode(Files2.readTextOrNull(f(StorageLayout.bookJson(id))) ?: return@io emptyList())
            ?: return@io emptyList()
        val text = Files2.readTextOrNull(f(StorageLayout.sourceFile(id))) ?: return@io emptyList()

        val chapters = ChapterSplitter.split(text, id)
        val hashes = chapters.map { c ->
            Chunker.split(text.substring(c.startOffset, c.endOffset)).map { Files2.shortHash(it) }
        }
        Files2.writeTextAtomic(
            f(StorageLayout.bookJson(id)),
            BookJson.encode(StoredBook(stored.book.copy(chapterCount = chapters.size), chapters, hashes, ruleId)),
        )
        chapters
    }

    override suspend fun delete(id: String) = io {
        if (!StorageLayout.isValidId(id)) return@io
        f(StorageLayout.bookDir(id)).deleteRecursively()
        f(StorageLayout.audioDir(id)).deleteRecursively()
        Unit
    }

    override suspend fun setPinned(id: String, pinned: Boolean) = io {
        val stored = BookJson.decode(Files2.readTextOrNull(f(StorageLayout.bookJson(id))) ?: return@io)
            ?: return@io
        Files2.writeTextAtomic(
            f(StorageLayout.bookJson(id)),
            BookJson.encode(stored.copy(book = stored.book.copy(pinned = pinned))),
        )
    }

    /** 供 :core:audio 用：拿到某一章的块文本与指纹，续跑时逐块比对。 */
    fun storedChunks(id: String): StoredBook? {
        val text = Files2.readTextOrNull(f(StorageLayout.bookJson(id))) ?: return null
        return BookJson.decode(text)
    }

    private fun newId(): String =
        UUID.randomUUID().toString().replace("-", "").lowercase().take(16)

    /**
     * 中文 txt 十有八九不是 UTF-8。先按 BOM，再试 UTF-8（出现替换字符就当失败），
     * 最后退回 GBK——这比把整本书变成问号强。
     */
    private fun readSmart(bytes: ByteArray): String {
        if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
            return String(bytes, 3, bytes.size - 3, Charsets.UTF_8)
        }
        val utf8 = runCatching { String(bytes, Charsets.UTF_8) }.getOrNull()
        if (utf8 != null && !utf8.contains('\uFFFD')) return utf8
        return runCatching { String(bytes, charset("GBK")) }.getOrDefault(utf8 ?: "")
    }
}

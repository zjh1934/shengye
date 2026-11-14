package com.shengye.core.data

import com.shengye.contract.Book
import com.shengye.contract.Chapter
import org.json.JSONArray
import org.json.JSONObject

/** 落盘的书：元数据 + 章表 + 每章每块的文本指纹。见 contracts/STORAGE.md §2.2。
 *  public：:core:audio 要拿它做续跑比对。 */
data class StoredBook(
    val book: Book,
    val chapters: List<Chapter>,
    val chunkHashes: List<List<String>>,
    val chunkRule: String,
)

/**
 * book.json 的编解码。手写而不是上序列化框架，是为了让文件格式肉眼可读、
 * 出问题能用文本编辑器打开看——这个文件坏了，用户整本书就没了。
 */
internal object BookJson {

    private const val VERSION = 1

    fun encode(s: StoredBook): String {
        val root = JSONObject()
        root.put("v", VERSION)
        root.put("id", s.book.id)
        root.put("title", s.book.title)
        root.put("chars", s.book.chars)
        root.put("createdAt", s.book.createdAt)
        root.put("pinned", s.book.pinned)
        root.put("chunkRule", s.chunkRule)

        val arr = JSONArray()
        s.chapters.forEachIndexed { i, c ->
            val o = JSONObject()
            o.put("i", c.index)
            o.put("t", c.title)
            o.put("s", c.startOffset)
            o.put("e", c.endOffset)
            o.put("c", c.chars)
            val hashes = JSONArray()
            s.chunkHashes.getOrNull(i)?.forEach { hashes.put(it) }
            o.put("h", hashes)
            arr.put(o)
        }
        root.put("chapters", arr)
        return root.toString()
    }

    fun decode(text: String): StoredBook? = runCatching {
        val root = JSONObject(text)
        val id = root.getString("id")
        val chapters = ArrayList<Chapter>()
        val hashes = ArrayList<List<String>>()

        val arr = root.optJSONArray("chapters") ?: JSONArray()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            chapters += Chapter(
                bookId = id,
                index = o.getInt("i"),
                title = o.getString("t"),
                startOffset = o.getInt("s"),
                endOffset = o.getInt("e"),
                chars = o.getInt("c"),
            )
            val h = o.optJSONArray("h") ?: JSONArray()
            hashes += (0 until h.length()).map { h.getString(it) }
        }

        StoredBook(
            book = Book(
                id = id,
                title = root.getString("title"),
                sourcePath = "books/$id/source.txt",
                chars = root.optInt("chars"),
                chapterCount = chapters.size,
                createdAt = root.optLong("createdAt"),
                pinned = root.optBoolean("pinned"),
            ),
            chapters = chapters,
            chunkHashes = hashes,
            chunkRule = root.optString("chunkRule"),
        )
    }.getOrNull()
}

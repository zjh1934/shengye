package com.shengye.domain

/**
 * 存储路径规则（纯逻辑，只产出相对路径，不碰文件系统）。
 * 真源是 contracts/STORAGE.md；改这里必须同步那份文档，否则已存的块会对不上。
 *
 * 为什么放 :core:domain：路径拼错是最容易出、也最难查的一类 bug，
 * 放这儿能用单测穷举；:core:data 只负责按这些路径做 IO。
 */
object StorageLayout {

    /** 分块规则版本。切法一改，这个串必须跟着改，续跑时才能发现「老块不能用了」。 */
    const val CHUNK_RULE = "v1-target120-max200"

    private val HEX16 = Regex("^[0-9a-f]{16}$")

    /** id 只允许 16 位小写十六进制，挡掉 `../` 之类的路径穿越。 */
    fun isValidId(id: String): Boolean = HEX16.matches(id)

    fun requireId(id: String): String {
        require(isValidId(id)) { "非法 id: $id" }
        return id
    }

    // ── 书 ────────────────────────────────────────────────
    fun bookDir(bookId: String) = "books/${requireId(bookId)}"
    fun sourceFile(bookId: String) = "${bookDir(bookId)}/source.txt"
    fun bookJson(bookId: String) = "${bookDir(bookId)}/book.json"

    fun chapterChunkDir(bookId: String, chapterIndex: Int) =
        "${bookDir(bookId)}/chunks/${chapterTag(chapterIndex)}"

    /** 块音频。文件存在 == 这一块做完了（见 STORAGE §2.1）。 */
    fun chunkFile(bookId: String, chapterIndex: Int, chunkIndex: Int) =
        "${chapterChunkDir(bookId, chapterIndex)}/${("%04d").format(chunkIndex)}.wav"

    fun chunkTmpFile(bookId: String, chapterIndex: Int, chunkIndex: Int) =
        "${chunkFile(bookId, chapterIndex, chunkIndex)}.tmp"

    // ── 成品 ──────────────────────────────────────────────
    fun audioDir(bookId: String) = "audio/${requireId(bookId)}"
    fun chapterAudio(bookId: String, chapterIndex: Int) =
        "${audioDir(bookId)}/${chapterTag(chapterIndex)}.m4a"

    fun chapterAudioPart(bookId: String, chapterIndex: Int) =
        "${audioDir(bookId)}/${chapterTag(chapterIndex)}.part.m4a"

    // ── 任务 / 片段 / 模型 ────────────────────────────────
    fun taskJson(taskId: String) = "tasks/${requireId(taskId)}.json"
    fun clipAudio(clipId: String) = "clips/${requireId(clipId)}.m4a"
    fun modelDir(slotId: String) = "models/${requireId(slotId)}"
    fun modelJson(slotId: String) = "${modelDir(slotId)}/slot.json"

    const val PREFS = "prefs.json"

    private fun chapterTag(index: Int): String {
        require(index >= 0) { "章序号不能为负: $index" }
        return "c%03d".format(index)
    }
}

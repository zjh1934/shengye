package com.shengye.domain

/**
 * 分块。断点续跑的地基：**同一段文字，任何时候切出来必须完全一样**。
 * 所以这里只用确定性算法：不随机、不依赖时间、不看系统设置。
 * 一旦块边界变了，已做好的块就对不上，续跑会错位——旧版在这里吃过亏。
 */
object Chunker {

    private val PARA = Regex("\\n\\s*\\n")
    private val SENT_END = setOf('。', '！', '？', '…', '；', '!', '?', ';', '\\n'.first())
    private val SOFT_END = setOf('，', '、', ',', '：', ':', '—', '~')

    /** target 是理想块长，max 是硬上限（含）。 */
    fun split(text: String, target: Int = 120, max: Int = 200): List<String> {
        require(target > 0) { "target 必须为正" }
        require(max >= target) { "max 不能小于 target" }

        val pieces = ArrayList<String>()
        for (para in text.split(PARA)) {
            val p = para.trim()
            if (p.isEmpty()) continue
            for (s in sentences(p)) {
                if (s.length <= max) pieces += s else pieces += chopLong(s, max)
            }
        }

        // 贪心合并到接近 target，避免全是一两句的碎块（碎块会让 TTS 频繁启停、音质发飘）
        val out = ArrayList<String>()
        val buf = StringBuilder()
        for (p in pieces) {
            if (buf.isNotEmpty() && buf.length + p.length > target) {
                out += buf.toString()
                buf.setLength(0)
            }
            buf.append(p)
            if (buf.length >= target) {
                out += buf.toString()
                buf.setLength(0)
            }
        }
        if (buf.isNotEmpty()) out += buf.toString()

        return out.map { it.trim() }.filter { it.isNotEmpty() }
    }

    /** 切句：句末标点留在句子里，收尾的引号括号跟着走。 */
    private fun sentences(para: String): List<String> {
        val out = ArrayList<String>()
        val cur = StringBuilder()
        var i = 0
        while (i < para.length) {
            val c = para[i]
            cur.append(c)
            if (c in SENT_END) {
                // 把紧跟着的 」』”）" 收进同一句
                while (i + 1 < para.length && para[i + 1] in "」』”\"’）)】]") {
                    cur.append(para[i + 1]); i++
                }
                val s = cur.toString().trim()
                if (s.isNotEmpty()) out += s
                cur.setLength(0)
            }
            i++
        }
        val tail = cur.toString().trim()
        if (tail.isNotEmpty()) out += tail
        return out
    }

    /** 超长句：先按逗号类软断点切，单段仍超长才硬切。 */
    private fun chopLong(s: String, max: Int): List<String> {
        val bySoft = ArrayList<String>()
        val cur = StringBuilder()
        for (c in s) {
            cur.append(c)
            if (c in SOFT_END && cur.length >= max / 2) {
                bySoft += cur.toString(); cur.setLength(0)
            }
        }
        if (cur.isNotEmpty()) bySoft += cur.toString()

        val out = ArrayList<String>()
        for (seg in bySoft) {
            if (seg.length <= max) {
                out += seg
            } else {
                var start = 0
                while (start < seg.length) {
                    val end = minOf(start + max, seg.length)
                    out += seg.substring(start, end)
                    start = end
                }
            }
        }
        return out
    }

    /** 块数预估：给「开做之前」显示用，不落盘。 */
    fun estimateChunks(text: String, target: Int = 120, max: Int = 200): Int =
        if (text.isBlank()) 0 else split(text, target, max).size
}

package com.shengye.domain

import com.shengye.contract.Chapter

/**
 * 分章。纯逻辑，可穷举测试。
 *
 * 现实里的 txt 分章极其脏：有的「第一章」有的「第 1 章」有的「Chapter 1」，
 * 有的一章几万字有的一章三行。所以策略是「能识别就按标题切，识别不到就按长度切」，
 * 绝不因为认不出标题就整本当成一章——那样用户没法只做第三章。
 */
object ChapterSplitter {

    /** 汉字数字 + 阿拉伯数字。行首（允许缩进），后面必须跟 章/节/回/卷 之一。
     *  注意不要在这里加 `\b`：中文不是 \w，`章` 与空格之间没有词边界，加了会全部失配。 */
    private val HEADING = Regex(
        "^\\s*(第\\s*[0-9一二三四五六七八九十百千零〇两]{1,8}\\s*[章节回卷]|Chapter\\s+[0-9]{1,4}).*$",
        RegexOption.MULTILINE,
    )

    /** 没有标题时按这个长度切，落在段落边界上。 */
    private const val FALLBACK_CHARS = 4000

    fun split(text: String, bookId: String): List<Chapter> {
        if (text.isBlank()) return emptyList()

        val marks = HEADING.findAll(text).map { it.range.first }.toList()
        val spans = ArrayList<IntRange>()

        if (marks.isEmpty()) {
            spans += fallbackSpans(text)
        } else {
            // 第一个标题之前的内容单独成章，不丢
            if (marks.first() > 0 && text.substring(0, marks.first()).isNotBlank()) {
                spans += 0 until marks.first()
            }
            for ((i, start) in marks.withIndex()) {
                val end = if (i + 1 < marks.size) marks[i + 1] else text.length
                if (end > start) spans += start until end
            }
        }

        return spans.mapIndexed { i, r ->
            Chapter(
                bookId = bookId,
                index = i,
                title = titleOf(text, r, i),
                startOffset = r.first,
                endOffset = r.last + 1,
                chars = r.last + 1 - r.first,
            )
        }
    }

    private fun titleOf(text: String, r: IntRange, i: Int): String {
        val head = text.substring(r.first, minOf(r.last + 1, r.first + 80))
            .lineSequence().firstOrNull()?.trim().orEmpty()
        return if (head.isBlank()) "第 ${i + 1} 部分" else head
    }

    /** 兜底：按 FALLBACK_CHARS 切，尽量落在空行处，不把一段话劈成两半。 */
    private fun fallbackSpans(text: String): List<IntRange> {
        val out = ArrayList<IntRange>()
        var start = 0
        while (start < text.length) {
            var end = minOf(start + FALLBACK_CHARS, text.length)
            if (end < text.length) {
                // 往后找最近的段落分隔（最多再走 20%），找不到就硬切
                val limit = minOf(end + FALLBACK_CHARS / 5, text.length)
                var cut = -1
                var i = end
                while (i < limit) {
                    if (text[i] == '\n' && i + 1 < text.length && text[i + 1] == '\n') {
                        cut = i; break
                    }
                    i++
                }
                if (cut > start) end = cut
            }
            out += start until end
            start = end
        }
        return out
    }

    /** 给界面用：这一章大概多少字。 */
    fun charsOf(text: String, c: Chapter): Int = c.endOffset - c.startOffset
}

package com.shengye.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChapterSplitterTest {

    private val id = "a1b2c3d4e5f60718"

    @Test
    fun `中文数字章标题`() {
        val text = "第一章 起点\n内容一\n第二章 转折\n内容二\n第三章 收尾\n内容三"
        val cs = ChapterSplitter.split(text, id)
        assertEquals(3, cs.size)
        assertEquals("第一章 起点", cs[0].title)
        assertEquals("第二章 转折", cs[1].title)
        assertEquals(0, cs[0].startOffset)
        assertEquals(cs[0].endOffset, cs[1].startOffset)
        assertEquals(text.length, cs.last().endOffset)
    }

    @Test
    fun `阿拉伯数字与带空格的章标题`() {
        val text = "第 1 章 开端\n甲\n第 2 章 发展\n乙"
        val cs = ChapterSplitter.split(text, id)
        assertEquals(2, cs.size)
    }

    @Test
    fun `英文 Chapter`() {
        val text = "Chapter 1\nA\nChapter 2\nB"
        val cs = ChapterSplitter.split(text, id)
        assertEquals(2, cs.size)
    }

    @Test
    fun `第一个标题之前的内容不丢`() {
        val text = "这是一段没有标题的开头。\n第一章 正式开始\n正文"
        val cs = ChapterSplitter.split(text, id)
        assertEquals(2, cs.size)
        assertEquals(0, cs[0].startOffset)
        assertTrue(cs[0].title.startsWith("这是一段没有标题"))
    }

    @Test
    fun `完全没有标题时按长度切`() {
        val body = StringBuilder()
        repeat(300) { body.append("这是一段很普通的文字，用来撑长度。\n\n") }
        val cs = ChapterSplitter.split(body.toString(), id)
        assertTrue("应切成多章，实际 ${cs.size}", cs.size > 1)
        assertEquals(0, cs[0].startOffset)
        assertEquals(body.length, cs.last().endOffset)
        for (i in 1 until cs.size) {
            assertEquals("章之间不能有空隙", cs[i - 1].endOffset, cs[i].startOffset)
        }
    }

    @Test
    fun `空文本不产出章节`() {
        assertTrue(ChapterSplitter.split("", id).isEmpty())
        assertTrue(ChapterSplitter.split("   \n\n  ", id).isEmpty())
    }

    @Test
    fun `每章都有可见标题`() {
        val text = "第一章\n甲\n第二章\n乙"
        for (c in ChapterSplitter.split(text, id)) {
            assertTrue("章标题为空", c.title.isNotBlank())
            assertTrue("章长度必须为正", c.chars > 0)
        }
    }

    @Test
    fun `不会把正文里的 第x章 误判成新章`() {
        val text = "第一章 开端\n他翻到第三章去看。\n第二章 转折\n乙"
        val cs = ChapterSplitter.split(text, id)
        // 「他翻到第三章去看」不在行首，不算标题
        assertEquals(2, cs.size)
    }
}

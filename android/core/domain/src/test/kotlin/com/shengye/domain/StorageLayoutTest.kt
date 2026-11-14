package com.shengye.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageLayoutTest {

    private val id = "a1b2c3d4e5f60718"

    @Test
    fun `书目录与文件`() {
        assertEquals("books/$id", StorageLayout.bookDir(id))
        assertEquals("books/$id/source.txt", StorageLayout.sourceFile(id))
        assertEquals("books/$id/book.json", StorageLayout.bookJson(id))
    }

    @Test
    fun `块文件名补零到四位`() {
        assertEquals(
            "books/$id/chunks/c000/0000.wav",
            StorageLayout.chunkFile(id, 0, 0),
        )
        assertEquals(
            "books/$id/chunks/c012/0345.wav",
            StorageLayout.chunkFile(id, 12, 345),
        )
        assertEquals(
            "books/$id/chunks/c000/0000.wav.tmp",
            StorageLayout.chunkTmpFile(id, 0, 0),
        )
    }

    @Test
    fun `章序号超过三位也不串`() {
        assertEquals("books/$id/chunks/c1234/0001.wav", StorageLayout.chunkFile(id, 1234, 1))
    }

    @Test
    fun `成品与中间态分开`() {
        assertEquals("audio/$id/c003.m4a", StorageLayout.chapterAudio(id, 3))
        assertEquals("audio/$id/c003.part.m4a", StorageLayout.chapterAudioPart(id, 3))
    }

    @Test
    fun `非法 id 一律拒绝`() {
        assertFalse(StorageLayout.isValidId("../../etc/passwd"))
        assertFalse(StorageLayout.isValidId("A1B2C3D4E5F60718"))  // 大写不收
        assertFalse(StorageLayout.isValidId("a1b2c3d4e5f6071"))   // 15 位
        assertFalse(StorageLayout.isValidId("a1b2c3d4e5f607189")) // 17 位
        assertTrue(StorageLayout.isValidId(id))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `路径穿越会被挡住`() {
        StorageLayout.bookDir("../escape")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `负章序号被挡住`() {
        StorageLayout.chunkFile(id, -1, 0)
    }

    @Test
    fun `所有路径都不含上级目录`() {
        val paths = listOf(
            StorageLayout.bookDir(id),
            StorageLayout.sourceFile(id),
            StorageLayout.bookJson(id),
            StorageLayout.chunkFile(id, 0, 0),
            StorageLayout.chapterAudio(id, 0),
            StorageLayout.taskJson(id),
            StorageLayout.clipAudio(id),
            StorageLayout.modelJson(id),
            StorageLayout.PREFS,
        )
        for (p in paths) {
            assertFalse("路径里有 ..: $p", p.contains(".."))
            assertFalse("路径是绝对路径: $p", p.startsWith("/"))
        }
    }
}

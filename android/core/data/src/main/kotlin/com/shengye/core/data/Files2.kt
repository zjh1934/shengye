package com.shengye.core.data

import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * 文件 IO 的两个底层动作：原子写、短哈希。
 *
 * 原子写是「半个 json 让 App 启动报错」的唯一根治办法：
 * 先写 .tmp，落盘，再 rename 覆盖。rename 在同一文件系统内是原子的。
 */
internal object Files2 {

    fun writeAtomic(target: File, bytes: ByteArray) {
        target.parentFile?.mkdirs()
        val tmp = File(target.path + ".tmp")
        FileOutputStream(tmp).use { out ->
            out.write(bytes)
            out.flush()
            out.fd.sync()
        }
        if (!tmp.renameTo(target)) {
            // 某些机型 rename 到已存在文件会失败，退一步：删了再 rename
            target.delete()
            if (!tmp.renameTo(target)) {
                tmp.copyTo(target, overwrite = true)
                tmp.delete()
            }
        }
    }

    fun writeTextAtomic(target: File, text: String) = writeAtomic(target, text.toByteArray())

    fun readTextOrNull(f: File): String? = runCatching { f.readText() }.getOrNull()

    /** 8 字节短哈希。只用来判断「这段文本有没有变」，不做安全用途。 */
    fun shortHash(text: String): String {
        val md = MessageDigest.getInstance("MD5")
        val d = md.digest(text.toByteArray())
        val sb = StringBuilder(16)
        for (i in 0 until 8) sb.append("%02x".format(d[i]))
        return sb.toString()
    }
}

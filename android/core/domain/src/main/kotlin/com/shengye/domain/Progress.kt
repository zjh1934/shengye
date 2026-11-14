package com.shengye.domain

import com.shengye.contract.TaskState

/**
 * ★ 唯一进度口径。全 App 的百分比和那一行运行态只准从这里出。
 * 目录页、任务页、通知、悬浮球都调这里，所以永远同词同数（旧版两套说法由此消灭）。
 */
object Progress {

    /** 唯一的百分比算法：向下取整，未开始就是 0，做完就是 100。 */
    fun pct(done: Int, total: Int): Int {
        if (total <= 0) return 0
        if (done <= 0) return 0
        if (done >= total) return 100
        return done * 100 / total
    }

    /** 一行运行态。每个状态都必须给得出话，不许留空让人猜。 */
    fun line(
        state: TaskState,
        chapterIndex: Int = 0,
        chapterCount: Int = 0,
        doneChunks: Int = 0,
        totalChunks: Int = 0,
        subPhase: String? = null,
        remainMs: Long = 0L,
        queuePosition: Int = 0,
        failMessage: String? = null,
    ): String = when (state) {
        TaskState.QUEUED ->
            if (queuePosition > 0) "排队中 · 第 ${queuePosition} 位" else "排队中"

        TaskState.PREPARING -> "准备中 · 正在分块"

        TaskState.RUNNING -> buildString {
            if (chapterCount > 0 && chapterIndex > 0) append("第 ${chapterIndex + 1}/$chapterCount 章")
            if (!subPhase.isNullOrBlank()) {
                if (isNotEmpty()) append(" · ")
                append(subPhase)
            } else if (remainMs > 0) {
                if (isNotEmpty()) append(" · ")
                append("还剩约 ${humanDuration(remainMs)}")
            }
            if (isEmpty()) append("制作中")
        }

        TaskState.PAUSED -> if (chapterIndex > 0) "已暂停 · 停在第 ${chapterIndex + 1} 章" else "已暂停"

        TaskState.STOPPED -> "已停止 · 已做好的块都留着"

        TaskState.LIMIT_REACHED -> "已达时长上限 · 已做好的块都留着"

        TaskState.FAILED -> if (failMessage.isNullOrBlank()) "失败" else "失败 · $failMessage"

        TaskState.DONE ->
            if (chapterCount > 0) "已完成 · $chapterCount 章" else "已完成"
    }

    /** 粗估剩余时间。样本太少时返回 0（界面就不显示「还剩约」）。 */
    fun etaMs(doneChunks: Int, totalChunks: Int, elapsedMs: Long): Long {
        if (doneChunks <= 0 || totalChunks <= doneChunks || elapsedMs <= 0L) return 0L
        val perChunk = elapsedMs / doneChunks
        return perChunk * (totalChunks - doneChunks)
    }

    /** 给人看的时间：30 秒 / 6 分钟 / 1 小时 20 分。不显示「0 分钟」。 */
    fun humanDuration(ms: Long): String {
        val sec = ms / 1000
        return when {
            sec < 45 -> "不到 1 分钟"
            sec < 3600 -> "${(sec + 30) / 60} 分钟"
            else -> {
                val h = sec / 3600
                val m = (sec % 3600 + 30) / 60
                if (m == 0L) "$h 小时" else "$h 小时 $m 分"
            }
        }
    }
}

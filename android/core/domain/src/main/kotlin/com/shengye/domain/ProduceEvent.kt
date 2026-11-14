package com.shengye.domain

import com.shengye.contract.SubPhase
import com.shengye.contract.TaskState

/** 制作过程中发生的事。只描述「发生了什么」，不带 UI 意图、不带 Android 类型。 */
sealed interface ProduceEvent {

    /** 排队出去、准备开做（分章分块完成后先把总数报上来）。 */
    data class Planned(val chunksTotal: Int) : ProduceEvent

    /** 真正开始跑第一块。 */
    data object Start : ProduceEvent

    data object Pause : ProduceEvent

    /** 用户点「继续制作」/「接着做」。 */
    data object Resume : ProduceEvent

    /** 用户主动停止：保留已做好的块。 */
    data object Stop : ProduceEvent

    /** 用户放弃这个任务并删除。 */
    data object Cancel : ProduceEvent

    /** 失败后重试：只重做红块。 */
    data object Retry : ProduceEvent

    data class ChunkDone(val chapterIndex: Int, val chunkIndex: Int) : ProduceEvent
    data class ChunkRedo(val chapterIndex: Int, val chunkIndex: Int) : ProduceEvent

    /** RUNNING 内部子阶段：拼接、转码。一屏只显示一个。 */
    data class EnterSubPhase(val phase: SubPhase) : ProduceEvent
    data object ExitSubPhase : ProduceEvent

    /** 到了单次时长上限（不是失败，可以接着做）。 */
    data object LimitReached : ProduceEvent

    data class Fail(val message: String) : ProduceEvent

    /** 全部块做完、拼接转码登记都成功。 */
    data object Finish : ProduceEvent
}

/** 一次 reduce 的结果：新任务 + 要不要把这件事告诉外界。
 *  用 sealed 而非 Boolean，方便 platform:service 决定是否更新通知。 */
data class ProduceStep(val task: ProduceTaskView, val effect: ProduceEffect = ProduceEffect.None)

sealed interface ProduceEffect {
    data object None : ProduceEffect
    data object NeedNextChunk : ProduceEffect
    data object NeedJoin : ProduceEffect
    data object NeedEncode : ProduceEffect
    data object NotifyDone : ProduceEffect
    data class NotifyFailed(val message: String) : ProduceEffect
    data object ClearWork : ProduceEffect
}

/** 状态机看到的任务视图：只读快照。domain 不改 Repository，产出新视图交给上层的 Store 写回。 */
data class ProduceTaskView(
    val id: String,
    val state: TaskState,
    val chunksTotal: Int,
    val chunksDone: Int,
    val subPhase: SubPhase?,
    val currentChapter: Int,
    val failMessage: String? = null,
)

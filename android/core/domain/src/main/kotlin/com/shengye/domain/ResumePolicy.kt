package com.shengye.domain

import com.shengye.contract.ResumeAction
import com.shengye.contract.ResumeReason
import com.shengye.contract.TaskState

/**
 * 启动时的续跑判断。**不是所有情况都该弹窗**——用户主动停的、只是想继续排队的，
 * 弹窗只会烦人，用一条常驻可撤销的提示就够（见 contracts/STATE_VOCAB.md）。
 */
object ResumePolicy {

    data class ResumePlan(
        val reason: ResumeReason,
        val action: ResumeAction,
        /** true 才弹窗；false 只显示一条常驻提示，绝不打断。 */
        val interruptive: Boolean,
    )

    /**
     * @param state 上次留下的任务状态，没有任务传 null
     * @param chunksDone 已做好的块数，0 表示一块都没做成
     * @param modelsReady 模型是否齐
     * @param queuePaused 队列是否处于暂停
     */
    fun plan(
        state: TaskState?,
        chunksDone: Int,
        modelsReady: Boolean,
        queuePaused: Boolean,
    ): ResumePlan? {
        if (state == null) return null
        if (state == TaskState.DONE) return null   // 做完了没什么好续的

        // 模型不齐是硬阻塞：先解决它，别的都别谈
        if (!modelsReady) return ResumePlan(ResumeReason.MODELS_MISSING, ResumeAction.VIEW, true)

        // 队列被暂停：任务在等，但不该弹窗，用户自己停的队列自己会开
        if (queuePaused && state != TaskState.RUNNING) {
            return ResumePlan(ResumeReason.QUEUE_PAUSED, ResumeAction.QUEUE, false)
        }

        return when (state) {
            // 上次根本没启动成功：要让人知道，否则会以为软件坏了
            TaskState.QUEUED, TaskState.PREPARING ->
                if (chunksDone == 0) ResumePlan(ResumeReason.NEVER_STARTED, ResumeAction.QUEUE, true)
                else ResumePlan(ResumeReason.INTERRUPTED, ResumeAction.AUTO, false)

            // 跑到一半没了（被杀进程 / 崩溃）：自动接着做，给一条可撤销提示
            TaskState.RUNNING ->
                ResumePlan(ResumeReason.INTERRUPTED, ResumeAction.AUTO, false)

            // 到上限：接着做，同样不打断
            TaskState.LIMIT_REACHED ->
                ResumePlan(ResumeReason.LIMIT_REACHED, ResumeAction.AUTO, false)

            // 用户自己按的停止：绝对不弹窗，任务页里它本来就写着「已停止」
            TaskState.STOPPED ->
                ResumePlan(ResumeReason.USER_STOPPED, ResumeAction.VIEW, false)

            // 失败：要让人看见并决定重试
            TaskState.FAILED ->
                ResumePlan(ResumeReason.INTERRUPTED, ResumeAction.VIEW, true)

            TaskState.PAUSED ->
                ResumePlan(ResumeReason.INTERRUPTED, ResumeAction.VIEW, false)

            TaskState.DONE -> null
        }
    }
}

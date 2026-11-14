package com.shengye.domain

import com.shengye.contract.TaskState

/**
 * 制作状态机。全 App 只有这一处决定「现在是什么状态、下一步能干什么」。
 *
 * 三条铁律：
 *  1. 只认这里定义的转移，别的组合一律原地不动（返回同一个对象，不抛异常、不崩）。
 *  2. chunksDone 只能被 ChunkDone 加、被 ChunkRedo 减，其它事件永远碰不到它。
 *  3. 完成/失败这类终局判断只在这里下，UI 与 Service 都不许自己猜。
 *
 * 纯函数：无 IO、无协程、无 Android。所以能单测穷举。
 */
object ProduceMachine {

    fun reduce(v: ProduceTaskView, e: ProduceEvent): ProduceStep = when (e) {

        is ProduceEvent.Planned -> {
            if (v.state.isTerminal()) step(v)
            else step(v.copy(chunksTotal = e.chunksTotal.coerceAtLeast(0)))
        }

        ProduceEvent.Start -> when (v.state) {
            TaskState.QUEUED, TaskState.PREPARING ->
                step(v.copy(state = TaskState.RUNNING, subPhase = null, failMessage = null),
                    ProduceEffect.NeedNextChunk)
            else -> step(v)
        }

        ProduceEvent.Pause -> when (v.state) {
            // 只有真在跑的才谈得上暂停，避免「已暂停」被再暂停一次
            TaskState.RUNNING -> step(v.copy(state = TaskState.PAUSED, subPhase = null))
            else -> step(v)
        }

        ProduceEvent.Resume -> when (v.state) {
            TaskState.PAUSED, TaskState.STOPPED, TaskState.LIMIT_REACHED, TaskState.FAILED ->
                step(v.copy(state = TaskState.RUNNING, subPhase = null, failMessage = null),
                    ProduceEffect.NeedNextChunk)
            else -> step(v)
        }

        ProduceEvent.Stop -> when (v.state) {
            TaskState.QUEUED, TaskState.PREPARING, TaskState.RUNNING, TaskState.PAUSED ->
                step(v.copy(state = TaskState.STOPPED, subPhase = null))
            else -> step(v)
        }

        ProduceEvent.Cancel -> when (v.state) {
            // 已完成的不给删：音频已经归档，删任务会让人以为音频也没了
            TaskState.DONE -> step(v)
            else -> step(v.copy(state = TaskState.STOPPED, subPhase = null), ProduceEffect.ClearWork)
        }

        ProduceEvent.Retry -> when (v.state) {
            TaskState.FAILED, TaskState.STOPPED, TaskState.LIMIT_REACHED ->
                step(v.copy(state = TaskState.RUNNING, subPhase = null, failMessage = null),
                    ProduceEffect.NeedNextChunk)
            else -> step(v)
        }

        is ProduceEvent.ChunkDone -> {
            // 暂停/停止期间后台迟到的回调不许推进进度
            if (v.state != TaskState.RUNNING) step(v)
            else {
                if (v.chunksTotal > 0 && v.chunksDone >= v.chunksTotal) step(v)
                else {
                    val done = v.chunksDone + 1
                    val next = v.copy(chunksDone = done, currentChapter = e.chapterIndex)
                    step(next, if (v.chunksTotal > 0 && done >= v.chunksTotal)
                        ProduceEffect.NeedJoin else ProduceEffect.NeedNextChunk)
                }
            }
        }

        is ProduceEvent.ChunkRedo -> {
            if (v.state != TaskState.RUNNING) step(v)
            else step(v.copy(chunksDone = (v.chunksDone - 1).coerceAtLeast(0)))
        }

        is ProduceEvent.EnterSubPhase -> when (v.state) {
            TaskState.RUNNING -> step(v.copy(subPhase = e.phase), when (e.phase) {
                com.shengye.contract.SubPhase.JOINING -> ProduceEffect.NeedJoin
                com.shengye.contract.SubPhase.ENCODING -> ProduceEffect.NeedEncode
                com.shengye.contract.SubPhase.REGISTER_FAILED -> ProduceEffect.None
            })
            else -> step(v)
        }

        ProduceEvent.ExitSubPhase -> step(v.copy(subPhase = null))

        ProduceEvent.LimitReached -> when (v.state) {
            TaskState.RUNNING ->
                step(v.copy(state = TaskState.LIMIT_REACHED, subPhase = null))
            else -> step(v)
        }

        is ProduceEvent.Fail -> when (v.state) {
            TaskState.RUNNING, TaskState.PREPARING ->
                step(v.copy(state = TaskState.FAILED, subPhase = null, failMessage = e.message),
                    ProduceEffect.NotifyFailed(e.message))
            else -> step(v)
        }

        ProduceEvent.Finish -> when (v.state) {
            TaskState.RUNNING -> step(
                v.copy(state = TaskState.DONE, subPhase = null,
                    chunksDone = if (v.chunksTotal > 0) v.chunksTotal else v.chunksDone),
                ProduceEffect.NotifyDone,
            )
            else -> step(v)
        }
    }

    private fun step(v: ProduceTaskView, e: ProduceEffect = ProduceEffect.None) = ProduceStep(v, e)
}

/** 终态：不会再自己动，只能靠用户操作离开。 */
fun TaskState.isTerminal(): Boolean = this == TaskState.DONE

/** 是否该显示「有没有在跑」的动画。 */
fun TaskState.isBusy(): Boolean = this == TaskState.RUNNING || this == TaskState.PREPARING

/** 完成后能不能直接听。 */
fun TaskState.isListenable(): Boolean = this == TaskState.DONE

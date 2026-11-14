package com.shengye.contract

/** ★ 页面只发意图、不自己改状态。所有业务动作经 :core:domain 状态机。
 *  这样 UI 层没有文件 IO、没有 while(delay) 轮询，符合 ARCHITECTURE 验收项 A2。 */
sealed interface UiIntent {
    // 我的书
    data class ImportBook(val path: String) : UiIntent
    data class OpenBook(val id: String) : UiIntent
    data class DeleteBook(val id: String) : UiIntent
    // 这一本
    data class ReadChapter(val bookId: String, val index: Int) : UiIntent
    data class ListenChapter(val bookId: String, val index: Int) : UiIntent
    data object OpenProduce : UiIntent
    // 正在做
    data class StartProduce(val bookId: String, val chapters: List<Int>) : UiIntent
    data class Pause(val taskId: String) : UiIntent
    data class ResumeTask(val taskId: String) : UiIntent
    data class Stop(val taskId: String) : UiIntent
    data class Cancel(val taskId: String) : UiIntent
    data class Retry(val taskId: String) : UiIntent
    data class EditConfig(val patch: ProduceConfig) : UiIntent
    // 续跑
    data object ResumeAskPrimary : UiIntent
    data object ResumeAskLater : UiIntent
    data object UndoAutoResume : UiIntent
    // 正在听
    data class Play(val workId: String, val fromMs: Long = 0L) : UiIntent
    data object TogglePlay : UiIntent
    data class Seek(val ms: Long) : UiIntent
    data class Speed(val value: Float) : UiIntent
    data class Mode(val mode: PlayMode) : UiIntent
    // 设置 / 模型
    data class SetTheme(val mode: ThemeMode) : UiIntent
    data class ImportModels(val uris: List<String>) : UiIntent
    data class UnloadModel(val id: String) : UiIntent
    data class SaveMixer(val config: MixerConfig) : UiIntent
}

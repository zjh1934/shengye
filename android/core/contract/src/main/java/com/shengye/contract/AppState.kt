package com.shengye.contract

/** ★ 全 App 唯一状态词表：枚举 + 文案一处定义。
 *  界面 / 通知 / 悬浮球 / 日志对外只准用这里，禁止自己拼词。
 *  逐字对应 contracts/STATE_VOCAB.md；改一个字要过总工程师并同步 roles/*.md。 */

enum class TaskState(val label: String) {
    QUEUED("排队中"), PREPARING("准备中"), RUNNING("制作中"),
    PAUSED("已暂停"), STOPPED("已停止"), LIMIT_REACHED("已达时长上限"),
    FAILED("失败"), DONE("已完成"),
}
enum class ChapterState(val label: String) {
    IDLE("未开始"), BUILDING("制作中"), READY("可听"),
}
enum class ChunkState(val label: String) {
    WAIT("待生成"), SYNTH("合成中"), DONE("已生成"), REDO("需重做"),
}
/** RUNNING 的内部子阶段，不与 TaskState 并列显示。 */
enum class SubPhase(val label: String) {
    JOINING("正在拼接"), ENCODING("正在转码"),
    REGISTER_FAILED("成品登记未成功 · 块已保留"),
}
enum class ResumeReason(val title: String, val line: String, val action: String) {
    INTERRUPTED("上次意外中断", "已做好的块都留着", "接着做"),
    USER_STOPPED("上次是你停的", "已做好的块都留着", "继续制作"),
    MODELS_MISSING("模型没齐", "还差几份", "去导入"),
    LIMIT_REACHED("上次到时长上限", "已做好的块都留着", "继续制作"),
    NEVER_STARTED("上次没启动成功", "已重排到队列", "去任务页"),
    QUEUE_PAUSED("队列已暂停", "有任务在等", "继续队列"),
}
enum class PlayMode(val label: String) { SEQUENTIAL("顺序"), SHUFFLE("随机"), LOOP("循环") }
enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class ResumeAction { AUTO, QUEUE, VIEW }

/** ★ 唯一进度口径：全 App 只有这一处产出百分比。
 *  目录页与任务页都从这里取数，保证同词同数（旧版两套说法的问题由此消灭）。 */
data class AppState(
    val activeTask: ProduceTask? = null,
    val queue: List<ProduceTask> = emptyList(),
    val progressPct: Int = 0,              // 唯一的整本制作进度
    val progressLine: String = "",         // 一行运行态，如「第 2 章 · 还剩约 6 分钟」
    val models: List<ModelSlot> = emptyList(),
    val modelsReady: Boolean = false,
    val playback: PlaybackState? = null,
    val resumeAsk: ResumeAsk? = null,
    val recoveryBar: String? = null,       // AUTO 自动续跑：常驻、可撤销，不用 Toast
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val glassBlurSupported: Boolean = true,
)

data class ResumeAsk(val reason: ResumeReason, val bookTitle: String,
                     val chunksDone: Int, val action: ResumeAction)

data class PlaybackState(val track: PlayTrack?, val playing: Boolean,
                         val positionMs: Long, val speed: Float, val mode: PlayMode,
                         val queueSize: Int, val bookProgressPct: Int)

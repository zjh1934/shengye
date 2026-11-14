package com.shengye.contract

/** 纯 Kotlin 数据模型：无 Android 依赖，路径一律用 String。
 *  依据 CONSTITUTION.md「必须保留的核心功能」。字段变更要过总工程师。 */

data class Book(
    val id: String, val title: String, val sourcePath: String,
    val chars: Int, val chapterCount: Int, val createdAt: Long,
    val pinned: Boolean = false,
)

data class Chapter(
    val bookId: String, val index: Int, val title: String,
    val startOffset: Int, val endOffset: Int, val chars: Int,
)

data class Chunk(
    val id: String, val bookId: String, val chapterIndex: Int,
    val index: Int, val text: String, val chars: Int,
)

/** 一个任务 = 一个制作请求。音频成品不是任务（旧版病根）。 */
data class ProduceTask(
    val id: String, val bookId: String, val bookTitle: String,
    val chapterIndexes: List<Int>, val state: TaskState,
    val config: ProduceConfig, val createdAt: Long,
    val chunksDone: Int = 0, val chunksTotal: Int = 0,
    val subPhase: SubPhase? = null, val resumeReason: ResumeReason? = null,
)

/** 音频成品：与任务分离，独立归档到「我的音频」。 */
data class AudioWork(
    val id: String, val bookId: String, val chapterIndex: Int?,
    val title: String, val path: String, val bytes: Long,
    val durationMs: Long, val createdAt: Long, val kind: AudioKind,
)

enum class AudioKind { CHAPTER_AUDIO, CLIP, SEGMENT }

/** 开做之前的参数摘要：按书可见，不再藏在设置深处。 */
data class ProduceConfig(
    val readingStyle: String, val pace: String, val intensity: String,
    val segmentSilenceMs: Int, val autoJoinChapters: Boolean,
    val seamHandling: Boolean, val outputFormat: String = "m4a",
    val sampleRate: Int = 24000, val bitrateKbps: Int = 64, val channels: Int = 1,
)

data class ModelSlot(val id: String, val name: String, val sizeBytes: Long,
                     val imported: Boolean, val required: Boolean = true)

/** 调音台：结构保留，见 CONSTITUTION「调音台（保留）」。 */
data class MixerConfig(
    val voiceGroup: VoiceGroup, val params: Map<String, Float>,
    val presetId: String?, val referenceAudioPath: String?,
)
enum class VoiceGroup { DESCRIBED, ROLE, REFERENCE }

data class PlayTrack(val workId: String, val title: String, val path: String,
                     val startMs: Long = 0L, val durationMs: Long = 0L)

data class Clip(val id: String, val text: String, val path: String?,
                val state: ClipState, val createdAt: Long)
enum class ClipState { RUNNING, DONE, FAILED, WAITING }

package com.shengye.contract

import kotlinx.coroutines.flow.StateFlow

/** ★ 唯一状态源：页面只读 state + 发意图，不准自己读盘、不准自己轮询、
 *  不准持有第二份进度。实现在 :core:data / :core:domain，UI 只见接口。 */
interface AppStore {
    val state: StateFlow<AppState>
    fun dispatch(intent: UiIntent)
}

/** 契约：模块之间只通过本文件通信。接口签名变更必须过总工程师。 */

interface BookRepository {
    suspend fun list(): List<Book>
    suspend fun get(id: String): Book?
    suspend fun import(path: String): Result<Book>
    suspend fun readText(id: String): String
    suspend fun chapters(id: String): List<Chapter>
    suspend fun reSplit(id: String, ruleId: String): List<Chapter>
    suspend fun delete(id: String)
    suspend fun setPinned(id: String, pinned: Boolean)
}

interface TaskRepository {
    suspend fun submit(bookId: String, chapters: List<Int>, config: ProduceConfig): Result<ProduceTask>
    suspend fun pause(taskId: String)
    suspend fun resume(taskId: String)
    suspend fun stop(taskId: String)
    suspend fun cancel(taskId: String)
    suspend fun retry(taskId: String)
    suspend fun queue(): List<ProduceTask>
    suspend fun chunkGrid(taskId: String): List<Pair<Int, ChunkState>>
}

/** 音频成品与任务分离：这是旧版「把成品当任务列」的结构性修正。 */
interface AudioRepository {
    suspend fun listWorks(bookId: String? = null): List<AudioWork>
    suspend fun listClips(): List<Clip>
    suspend fun export(ids: List<String>, target: String): Result<Int>
    suspend fun cleanSegments(bookId: String)
    suspend fun deleteWork(id: String)
}

interface PlaybackRepository {
    suspend fun play(workId: String, fromMs: Long = 0L)
    suspend fun toggle()
    suspend fun seek(ms: Long)
    suspend fun next(); suspend fun prev()
    suspend fun setSpeed(speed: Float)
    suspend fun setMode(mode: PlayMode)
    suspend fun stop()
    suspend fun queue(): List<PlayTrack>
    suspend fun resumeListening(bookId: String): Long
}

interface ModelRepository {
    suspend fun slots(): List<ModelSlot>
    suspend fun import(uris: List<String>, onProgress: (Long, Long) -> Unit): Result<Unit>
    suspend fun unload(id: String)
    fun synthesisBlockReason(): String?
}

interface SettingsRepository {
    suspend fun theme(): ThemeMode
    suspend fun setTheme(mode: ThemeMode)
    suspend fun produceConfig(): ProduceConfig
    suspend fun setProduceConfig(config: ProduceConfig)
    suspend fun mixerConfig(): MixerConfig
    suspend fun setMixerConfig(config: MixerConfig)
    suspend fun overlayEnabled(): Boolean
}

/** 端侧 TTS 引擎：实现在 :platform:native（JNI），风险最高，总工程师亲自管。 */
interface TtsEngine {
    suspend fun synthesize(chunk: Chunk, config: ProduceConfig, outPath: String): Result<Unit>
    fun cancel()
}

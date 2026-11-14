package com.shengye.core.data

import com.shengye.contract.ChunkState
import com.shengye.contract.ProduceConfig
import com.shengye.contract.ProduceTask
import com.shengye.contract.TaskRepository
import com.shengye.contract.TaskState
import com.shengye.domain.ProduceEvent
import com.shengye.domain.ProduceMachine
import com.shengye.domain.ProduceTaskView
import com.shengye.domain.StorageLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * 任务落盘实现。
 *
 * 关键诚实约束：本 App 当前没有接入端侧 TTS 引擎（platform:tts 未实现）。
 * 所以 submit 只把任务排成 QUEUED 并如实算出块数，**绝不把状态写成 RUNNING、
 * 绝不假装合成成功**。等 platform:service + platform:tts 接上后由服务消费队列，
 * 这个文件不需要改接口。
 */
class FileTaskRepository(
    private val root: File,
    private val books: FileBookRepository,
) : TaskRepository {

    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) { block() }
    private fun f(rel: String) = File(root, rel)

    override suspend fun submit(
        bookId: String,
        chapters: List<Int>,
        config: ProduceConfig,
    ): Result<ProduceTask> = io {
        runCatching {
            val stored = books.storedChunks(bookId) ?: error("找不到这本书")
            require(chapters.isNotEmpty()) { "还没选章" }
            val picked = chapters.distinct().sorted().filter { it in stored.chapters.indices }
            require(picked.isNotEmpty()) { "选中的章不存在" }

            // 直接数落盘时的块指纹，和导入时切块口径完全一致，不重新切一遍
            val total = picked.sumOf { idx -> stored.chunkHashes.getOrNull(idx)?.size ?: 0 }

            val id = UUID.randomUUID().toString().replace("-", "").lowercase().take(16)
            val task = ProduceTask(
                id = id,
                bookId = bookId,
                bookTitle = stored.book.title,
                chapterIndexes = picked,
                state = TaskState.QUEUED,
                config = config,
                createdAt = System.currentTimeMillis(),
                chunksDone = 0,
                chunksTotal = total,
            )
            Files2.writeTextAtomic(f(StorageLayout.taskJson(id)), TaskJson.encode(task))
            task
        }
    }

    override suspend fun queue(): List<ProduceTask> = io {
        val dir = f("tasks")
        val files = dir.listFiles()?.filter { it.isFile && it.name.endsWith(".json") } ?: emptyList()
        files.mapNotNull { TaskJson.decode(Files2.readTextOrNull(it) ?: return@mapNotNull null) }
            .sortedByDescending { it.createdAt }
    }

    /** 状态迁移统一走 domain 的状态机，UI 与服务都不许自己改 state。 */
    private suspend fun move(taskId: String, event: ProduceEvent) = io {
        val t = read(taskId) ?: return@io
        val view = ProduceTaskView(
            id = t.id,
            state = t.state,
            chunksTotal = t.chunksTotal,
            chunksDone = t.chunksDone,
            subPhase = t.subPhase,
            currentChapter = 0,
        )
        val next = ProduceMachine.reduce(view, event).task
        Files2.writeTextAtomic(
            f(StorageLayout.taskJson(taskId)),
            TaskJson.encode(
                t.copy(
                    state = next.state,
                    chunksDone = next.chunksDone,
                    subPhase = next.subPhase,
                    resumeReason = t.resumeReason,
                )
            ),
        )
    }

    override suspend fun pause(taskId: String) = move(taskId, ProduceEvent.Pause)
    override suspend fun resume(taskId: String) = move(taskId, ProduceEvent.Resume)
    override suspend fun stop(taskId: String) = move(taskId, ProduceEvent.Stop)
    override suspend fun retry(taskId: String) = move(taskId, ProduceEvent.Retry)

    /** 取消 = 从队列移除。DONE 不给删（音频已归档，见状态机 Cancel 规则）。 */
    override suspend fun cancel(taskId: String) = io {
        val t = read(taskId) ?: return@io
        if (t.state == TaskState.DONE) return@io
        f(StorageLayout.taskJson(taskId)).delete()
    }

    /** 块网格只在任务详情用。引擎未接，全部如实为「待生成」，不编造已生成的块。 */
    override suspend fun chunkGrid(taskId: String): List<Pair<Int, ChunkState>> = io {
        val t = read(taskId) ?: return@io emptyList()
        (0 until t.chunksTotal).map { it to ChunkState.WAIT }
    }

    private fun read(taskId: String): ProduceTask? =
        TaskJson.decode(Files2.readTextOrNull(f(StorageLayout.taskJson(taskId))) ?: return null)
}

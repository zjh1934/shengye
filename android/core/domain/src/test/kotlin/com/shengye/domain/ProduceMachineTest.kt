package com.shengye.domain

import com.shengye.contract.ResumeAction
import com.shengye.contract.SubPhase
import com.shengye.contract.TaskState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProduceMachineTest {

    private fun view(
        state: TaskState,
        total: Int = 10,
        done: Int = 0,
        chapter: Int = 0,
    ) = ProduceTaskView("t1", state, total, done, null, chapter)

    // ── 正常流转 ────────────────────────────────────────────────

    @Test
    fun `排队到完成一路走通`() {
        var v = view(TaskState.QUEUED, total = 0)
        v = ProduceMachine.reduce(v, ProduceEvent.Planned(3)).task
        assertEquals(3, v.chunksTotal)

        v = ProduceMachine.reduce(v, ProduceEvent.Start).task
        assertEquals(TaskState.RUNNING, v.state)

        repeat(3) { v = ProduceMachine.reduce(v, ProduceEvent.ChunkDone(0, it)).task }
        assertEquals(3, v.chunksDone)
        assertEquals(100, Progress.pct(v.chunksDone, v.chunksTotal))

        v = ProduceMachine.reduce(v, ProduceEvent.EnterSubPhase(SubPhase.JOINING)).task
        assertEquals(SubPhase.JOINING, v.subPhase)

        v = ProduceMachine.reduce(v, ProduceEvent.ExitSubPhase).task
        assertNull(v.subPhase)

        val fin = ProduceMachine.reduce(v, ProduceEvent.Finish)
        assertEquals(TaskState.DONE, fin.task.state)
        assertEquals(ProduceEffect.NotifyDone, fin.effect)
    }

    @Test
    fun `最后一块做完要求拼接`() {
        val v = view(TaskState.RUNNING, total = 2, done = 1)
        val s = ProduceMachine.reduce(v, ProduceEvent.ChunkDone(1, 1))
        assertEquals(ProduceEffect.NeedJoin, s.effect)
        assertEquals(2, s.task.chunksDone)
    }

    // ── 非法转移：原地不动，返回同一个对象，绝不抛 ──────────────

    @Test
    fun `暂停状态下点开始无效`() {
        val v = view(TaskState.PAUSED)
        assertSame(v, ProduceMachine.reduce(v, ProduceEvent.Start).task)
    }

    @Test
    fun `已完成的任务不能再开始`() {
        val v = view(TaskState.DONE, total = 5, done = 5)
        assertSame(v, ProduceMachine.reduce(v, ProduceEvent.Start).task)
        assertSame(v, ProduceMachine.reduce(v, ProduceEvent.Resume).task)
    }

    @Test
    fun `暂停期间后台迟到的块回调不许推进进度`() {
        val v = view(TaskState.PAUSED, total = 10, done = 3)
        val s = ProduceMachine.reduce(v, ProduceEvent.ChunkDone(0, 3))
        assertEquals(3, s.task.chunksDone)
        assertEquals(ProduceEffect.None, s.effect)
    }

    @Test
    fun `停止后块回调也不许推进`() {
        val v = view(TaskState.STOPPED, total = 10, done = 3)
        assertEquals(3, ProduceMachine.reduce(v, ProduceEvent.ChunkDone(0, 4)).task.chunksDone)
    }

    @Test
    fun `做满之后再来的块被丢掉`() {
        val v = view(TaskState.RUNNING, total = 3, done = 3)
        assertEquals(3, ProduceMachine.reduce(v, ProduceEvent.ChunkDone(0, 3)).task.chunksDone)
    }

    @Test
    fun `已完成的任务不许删`() {
        val v = view(TaskState.DONE, total = 5, done = 5)
        val s = ProduceMachine.reduce(v, ProduceEvent.Cancel)
        assertEquals(TaskState.DONE, s.task.state)
        assertEquals(ProduceEffect.None, s.effect)
    }

    @Test
    fun `失败后重试保留已做好的块`() {
        var v = view(TaskState.RUNNING, total = 10, done = 6)
        v = ProduceMachine.reduce(v, ProduceEvent.Fail("解码器崩了")).task
        assertEquals(TaskState.FAILED, v.state)
        assertEquals(6, v.chunksDone)

        v = ProduceMachine.reduce(v, ProduceEvent.Retry).task
        assertEquals(TaskState.RUNNING, v.state)
        assertEquals(6, v.chunksDone)
        assertNull(v.failMessage)
    }

    @Test
    fun `到达时长上限不算失败`() {
        var v = view(TaskState.RUNNING, total = 10, done = 4)
        v = ProduceMachine.reduce(v, ProduceEvent.LimitReached).task
        assertEquals(TaskState.LIMIT_REACHED, v.state)
        assertEquals(4, v.chunksDone)

        v = ProduceMachine.reduce(v, ProduceEvent.Resume).task
        assertEquals(TaskState.RUNNING, v.state)
    }

    @Test
    fun `停止后继续制作能接着做`() {
        var v = view(TaskState.RUNNING, total = 10, done = 5)
        v = ProduceMachine.reduce(v, ProduceEvent.Stop).task
        assertEquals(TaskState.STOPPED, v.state)
        v = ProduceMachine.reduce(v, ProduceEvent.Resume).task
        assertEquals(TaskState.RUNNING, v.state)
        assertEquals(5, v.chunksDone)
    }

    @Test
    fun `取消会要求清理成品`() {
        val v = view(TaskState.RUNNING, total = 10, done = 2)
        assertEquals(ProduceEffect.ClearWork, ProduceMachine.reduce(v, ProduceEvent.Cancel).effect)
    }

    // ── 进度口径 ────────────────────────────────────────────────

    @Test
    fun `百分比边界`() {
        assertEquals(0, Progress.pct(0, 10))
        assertEquals(0, Progress.pct(0, 0))
        assertEquals(66, Progress.pct(4, 6))
        assertEquals(100, Progress.pct(10, 10))
        assertEquals(100, Progress.pct(11, 10))
    }

    @Test
    fun `每个状态都给得出一行话`() {
        for (s in TaskState.entries) {
            val line = Progress.line(s, chapterIndex = 1, chapterCount = 6, doneChunks = 3, totalChunks = 9)
            assertTrue("状态 $s 的运行态为空", line.isNotBlank())
        }
    }

    @Test
    fun `样本不足时不估时间`() {
        assertEquals(0L, Progress.etaMs(0, 10, 5000))
        assertEquals(0L, Progress.etaMs(3, 3, 5000))
        assertTrue(Progress.etaMs(2, 10, 20_000) > 0)
    }

    // ── 分块 ────────────────────────────────────────────────────

    @Test
    fun `分块是确定性的`() {
        val text = "第一句话。第二句话，还有后半句！\n\n第二段开始了，这里有一些内容；然后结束。"
        val a = Chunker.split(text)
        val b = Chunker.split(text)
        assertEquals(a, b)
        assertEquals(a.joinToString(""), b.joinToString(""))
    }

    @Test
    fun `分块不超上限也不出空块`() {
        val text = ("很长的一段话，" .repeat(200))
        val chunks = Chunker.split(text, target = 60, max = 100)
        assertTrue(chunks.isNotEmpty())
        for (c in chunks) {
            assertTrue("块超长: ${c.length}", c.length <= 100)
            assertTrue("出现空块", c.isNotBlank())
        }
    }

    @Test
    fun `空文本分不出块`() {
        assertTrue(Chunker.split("   \n\n  ").isEmpty())
        assertEquals(0, Chunker.estimateChunks(""))
    }

    @Test
    fun `分块不丢字`() {
        val text = "他说：“今天不走了。”然后坐下。\n\n外面下雨了？！她问。"
        val joined = Chunker.split(text).joinToString("")
        val src = text.filterNot { it.isWhitespace() }
        assertEquals(src, joined.filterNot { it.isWhitespace() })
    }

    // ── 续跑策略 ────────────────────────────────────────────────

    @Test
    fun `没有任务不弹任何东西`() {
        assertNull(ResumePolicy.plan(null, 0, true, false))
    }

    @Test
    fun `做完了不弹`() {
        assertNull(ResumePolicy.plan(TaskState.DONE, 10, true, false))
    }

    @Test
    fun `模型不齐优先提示去导入`() {
        val p = ResumePolicy.plan(TaskState.RUNNING, 3, modelsReady = false, queuePaused = false)
        assertNotNull(p)
        assertEquals(ResumeAction.VIEW, p!!.action)
        assertTrue(p.interruptive)
    }

    @Test
    fun `被杀进程后自动续跑但不弹窗`() {
        val p = ResumePolicy.plan(TaskState.RUNNING, 6, true, false)!!
        assertEquals(ResumeAction.AUTO, p.action)
        assertFalse(p.interruptive)
    }

    @Test
    fun `用户自己停的不弹窗`() {
        val p = ResumePolicy.plan(TaskState.STOPPED, 5, true, false)!!
        assertFalse(p.interruptive)
    }

    @Test
    fun `一块没做成要告诉用户没启动成功`() {
        val p = ResumePolicy.plan(TaskState.QUEUED, 0, true, false)!!
        assertTrue(p.interruptive)
        assertEquals(ResumeAction.QUEUE, p.action)
    }
}

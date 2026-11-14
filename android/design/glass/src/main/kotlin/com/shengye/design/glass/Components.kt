package com.shengye.design.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shengye.contract.ChapterState
import com.shengye.contract.ChunkState
import com.shengye.contract.TaskState

/**
 * 通用展示组件。放在 design:glass 而不是各 feature 里，
 * 因为「书行长什么样」只该有一个答案——feature 之间不许互相依赖，就只能放这儿。
 * 只认 contract 的模型与枚举 label，**不自己拼状态词**。
 */

/** 一根进度条。全 App 只有这一种进度样式，不允许页面自己画。 */
@Composable
fun ProgressBar(
    pct: Int,
    modifier: Modifier = Modifier,
    color: Color? = null,
    height: Dp = 5.dp,
) {
    val c = Glass.colors
    val v = pct.coerceIn(0, 100)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(c.hair)
    ) {
        Box(
            Modifier
                .fillMaxWidth(v / 100f)
                .height(height)
                .clip(CircleShape)
                .background(color ?: c.accent)
        )
    }
}

/** 书单行：书名 + 一行元信息 + 右侧状态。 */
@Composable
fun BookRow(
    title: String,
    meta: String,
    chip: String? = null,
    tone: Tone = Tone.NEUTRAL,
    pct: Int? = null,
    onClick: () -> Unit = {},
) {
    val c = Glass.colors
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                color = c.ink,
                fontSize = Tokens.title,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (chip != null) {
                Spacer(Modifier.width(8.dp))
                GlassChip(chip, tone)
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(meta, color = c.sub, fontSize = Tokens.sub, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (pct != null) {
            Spacer(Modifier.height(7.dp))
            ProgressBar(pct, color = if (pct >= 100) c.good else c.accent)
        }
    }
}

/** 任务卡：标题 + 状态胶囊 + 一行运行态 + 一根进度 + 可选动作。 */
@Composable
fun TaskCard(
    title: String,
    state: TaskState,
    line: String,
    pct: Int,
    actions: (@Composable () -> Unit)? = null,
) {
    val c = Glass.colors
    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                color = c.ink,
                fontSize = Tokens.title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            GlassChip(state.label, toneOf(state))
        }
        Spacer(Modifier.height(6.dp))
        Text(line, color = c.sub, fontSize = Tokens.sub, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(9.dp))
        ProgressBar(pct, color = when (state) {
            TaskState.DONE -> c.good
            TaskState.FAILED -> c.bad
            TaskState.LIMIT_REACHED -> c.warn
            else -> c.accent
        })
        if (actions != null) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { actions() }
        }
    }
}

fun toneOf(state: TaskState): Tone = when (state) {
    TaskState.DONE -> Tone.GOOD
    TaskState.RUNNING, TaskState.PREPARING, TaskState.QUEUED -> Tone.WARN
    TaskState.FAILED -> Tone.BAD
    TaskState.LIMIT_REACHED -> Tone.WARN
    TaskState.PAUSED, TaskState.STOPPED -> Tone.NEUTRAL
}

/** 章节行：序号 + 标题 + 章状态。可勾选（勾章开做）。 */
@Composable
fun ChapterRow(
    index: Int,
    title: String,
    state: ChapterState,
    selected: Boolean = false,
    selectable: Boolean = false,
    onClick: () -> Unit = {},
) {
    val c = Glass.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selectable) {
            Box(
                Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selected) c.accent else c.hair),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) Text("✓", color = Color.White, fontSize = Tokens.tiny)
            }
            Spacer(Modifier.width(10.dp))
        }
        Text(
            "${index + 1}",
            color = c.sub,
            fontSize = Tokens.sub,
            modifier = Modifier.width(22.dp),
        )
        Text(
            title,
            color = c.ink,
            fontSize = Tokens.body,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        GlassChip(state.label, when (state) {
            ChapterState.READY -> Tone.GOOD
            ChapterState.BUILDING -> Tone.WARN
            ChapterState.IDLE -> Tone.NEUTRAL
        })
    }
}

/** 迷你播放条：有播放时才有。点条进播放器，点圆钮播放/暂停。 */
@Composable
fun MiniBar(
    title: String,
    subtitle: String,
    playing: Boolean,
    pct: Int,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val c = Glass.colors
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.glassStrong)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = c.ink, fontSize = Tokens.sub, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = c.sub, fontSize = Tokens.tiny, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(c.accentSoft)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center,
            ) {
                Text(if (playing) "❚❚" else "▶", color = c.accent, fontSize = Tokens.tiny)
            }
        }
        Spacer(Modifier.height(6.dp))
        ProgressBar(pct, height = 3.dp)
    }
}

/** 块网格：一屏最多出现一次，只在「正在做」的详情里。 */
@Composable
fun ChunkGrid(states: List<ChunkState>, columns: Int = 10) {
    val c = Glass.colors
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        states.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { s ->
                    Box(
                        Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (s) {
                                    ChunkState.DONE -> c.good.copy(alpha = 0.75f)
                                    ChunkState.SYNTH -> c.warn
                                    ChunkState.REDO -> c.bad.copy(alpha = 0.8f)
                                    ChunkState.WAIT -> c.hair
                                }
                            )
                    )
                }
            }
        }
    }
}

/** 播放控制：上一首 / 播放暂停 / 下一首。图标配文字标签，不做纯图标裸按钮。 */
@Composable
fun PlayControl(
    playing: Boolean,
    onPrev: () -> Unit,
    onToggle: () -> Unit,
    onNext: () -> Unit,
) {
    val c = Glass.colors
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextAction("上一章", c.sub, onPrev)
        Spacer(Modifier.width(14.dp))
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(c.accent)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            Text(if (playing) "❚❚" else "▶", color = Color.White, fontSize = Tokens.title)
        }
        Spacer(Modifier.width(14.dp))
        TextAction("下一章", c.sub, onNext)
    }
}

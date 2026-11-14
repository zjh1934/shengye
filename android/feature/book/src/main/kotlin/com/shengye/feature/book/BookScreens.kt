package com.shengye.feature.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shengye.contract.Chapter
import com.shengye.contract.ChapterState
import com.shengye.design.glass.ChapterRow
import com.shengye.design.glass.Glass
import com.shengye.design.glass.GlassBottomBar
import com.shengye.design.glass.GlassButton
import com.shengye.design.glass.GlassCard
import com.shengye.design.glass.GlassDivider
import com.shengye.design.glass.GlassSmallButton
import com.shengye.design.glass.Tokens

/**
 * 这一本 · 章节列表。
 *
 * 只收数据和回调：章表由 app 层从 core:data 取，本文件不读盘、不查库。
 * 顶部总览只给「一个状态 + 一行运行态 + 一个主动作」，0 进度不画空条。
 * 章节一张卡包多行，不堆卡片。
 */
@Composable
fun BookScreen(
    chapters: List<Chapter>,
    onProduce: () -> Unit,
    onRead: (Int) -> Unit,
) {
    val c = Glass.colors
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Tokens.padScreen)
    ) {
        Spacer(Modifier.height(4.dp))
        GlassCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        ChapterState.IDLE.label,
                        color = c.ink,
                        fontSize = Tokens.title,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "0 / ${chapters.size} 章可听",
                        color = c.sub,
                        fontSize = Tokens.sub,
                    )
                }
                Spacer(Modifier.width(8.dp))
                GlassButton("制作这一本", onClick = onProduce)
            }
        }
        Spacer(Modifier.height(14.dp))
        GlassCard(contentPadding = 2.dp) {
            chapters.forEachIndexed { i, ch ->
                ChapterRow(
                    index = ch.index,
                    title = ch.title,
                    state = ChapterState.IDLE,
                    meta = "${formatChars(ch.chars)} 字",
                    actionText = "阅读",
                    onAction = { onRead(i) },
                    onClick = { onRead(i) },
                )
                if (i < chapters.lastIndex) GlassDivider()
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

/**
 * 这一本 · 勾章多选（只渲染列表）。
 * 上方不写「已选 N 章」，底部动作条实时反映——同一件事只说一遍。
 */
@Composable
fun SelectChaptersScreen(
    chapters: List<Chapter>,
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Tokens.padScreen)
    ) {
        Spacer(Modifier.height(8.dp))
        GlassCard(contentPadding = 2.dp) {
            chapters.forEachIndexed { i, ch ->
                ChapterRow(
                    index = ch.index,
                    title = ch.title,
                    meta = "${formatChars(ch.chars)} 字",
                    selectable = true,
                    selected = i in selected,
                    onClick = { onToggle(i) },
                )
                if (i < chapters.lastIndex) GlassDivider()
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

/** 选章底部条：一处显示「已选 N 章」，一个主按钮。放 Scaffold 的 bottomBar 槽位。 */
@Composable
fun SelectBottomBar(selectedCount: Int, onSubmit: () -> Unit) {
    val c = Glass.colors
    GlassBottomBar {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Tokens.padScreen, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "已选 $selectedCount 章",
                color = c.sub,
                fontSize = Tokens.sub,
                modifier = Modifier.weight(1f),
            )
            GlassButton("开始制作", enabled = selectedCount > 0, onClick = onSubmit)
        }
    }
}

/** 阅读器正文。全屏、无底部 tab；字号由 app 层持有，切章不重置。 */
@Composable
fun ReaderBody(text: String, fontSp: Int) {
    val c = Glass.colors
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Tokens.padScreen)
    ) {
        Spacer(Modifier.height(6.dp))
        text.split(Regex("\\n+")).filter { it.isNotBlank() }.forEach { para ->
            Text(
                para.trim(),
                color = c.ink,
                fontSize = fontSp.sp,
                lineHeight = (fontSp * 1.95f).sp,
                modifier = Modifier.padding(bottom = 15.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

/** 阅读器底部：字号 + 翻章。放 Scaffold 的 bottomBar 槽位，系统栏避让由 GlassBottomBar 负责。 */
@Composable
fun ReaderBottomBar(
    fontSp: Int,
    onFontChange: (Int) -> Unit,
    hasPrev: Boolean,
    hasNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    val c = Glass.colors
    GlassBottomBar {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Tokens.padScreen, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("字号", color = c.sub, fontSize = Tokens.sub, modifier = Modifier.weight(1f))
            GlassSmallButton("A-") { onFontChange((fontSp - 1).coerceIn(12, 24)) }
            Spacer(Modifier.width(8.dp))
            Text("$fontSp", color = c.ink, fontSize = Tokens.sub, fontWeight = FontWeight.SemiBold)
            Text("px", color = c.sub, fontSize = Tokens.tiny)
            Spacer(Modifier.width(8.dp))
            GlassSmallButton("A+") { onFontChange((fontSp + 1).coerceIn(12, 24)) }
        }
        GlassDivider()
        Row(
            Modifier.fillMaxWidth().padding(horizontal = Tokens.padScreen, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GlassSmallButton("上一章") { if (hasPrev) onPrev() }
            GlassSmallButton("下一章") { if (hasNext) onNext() }
        }
    }
}

private fun formatChars(n: Int): String =
    if (n >= 10000) "%.1f 万".format(n / 10000.0) else "%,d".format(n)

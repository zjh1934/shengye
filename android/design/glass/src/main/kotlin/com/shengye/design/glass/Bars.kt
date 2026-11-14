package com.shengye.design.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shengye.contract.ChapterState

/**
 * 二级页顶栏（这一本 / 阅读器 / 选章）。
 *
 * 与首页的区别：有返回、字号克制（不是软件名大写那种）、右侧只留一个真动作。
 * 状态栏在这里吃一次，页面不要再写 statusBarsPadding。
 */
@Composable
fun GlassBackBar(
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    action: (@Composable () -> Unit)? = null,
) {
    val c = Glass.colors
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarInset(true)
            .padding(horizontal = Tokens.padScreen, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Text("‹", color = c.ink, fontSize = Tokens.display)
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = c.ink,
                fontSize = Tokens.title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    color = c.sub,
                    fontSize = Tokens.tiny,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (action != null) {
            Spacer(Modifier.width(8.dp))
            action()
        }
    }
}

/**
 * 底部动作条（选章 / 阅读器）。
 * 底色铺到屏幕最底，内容避让手势条——与 GlassTabs 同一套口径。
 * 用 ColumnScope：选章是一行，阅读器是「字号 + 翻章」两行，一个组件容纳。
 */
@Composable
fun GlassBottomBar(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val c = Glass.colors
    Box(
        Modifier
            .fillMaxWidth()
            .background(c.glassStrong)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .navBarInset(true),
            content = content,
        )
    }
}

/** 章状态胶囊：给阅读器顶栏等非列表处用。词表仍只认 ChapterState.label。 */
@Composable
fun ChapterStateChip(state: ChapterState) {
    GlassChip(state.label, when (state) {
        ChapterState.READY -> Tone.GOOD
        ChapterState.BUILDING -> Tone.WARN
        ChapterState.IDLE -> Tone.NEUTRAL
    })
}

/** 圆角小按钮（A- / A+ / 上一章）。描边、克制，不做实心抢主按钮。 */
@Composable
fun GlassSmallButton(text: String, onClick: () -> Unit) {
    val c = Glass.colors
    val shape = RoundedCornerShape(9.dp)
    Box(
        Modifier
            .clip(shape)
            .background(c.glass)
            .border(1.dp, c.sub.copy(alpha = 0.28f), shape)
            .clickable { onClick() }
            .padding(horizontal = 11.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = c.ink, fontSize = Tokens.sub)
    }
}

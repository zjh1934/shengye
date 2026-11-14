package com.shengye.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.shengye.design.glass.AuroraBackground
import com.shengye.design.glass.BookRow
import com.shengye.design.glass.ButtonKind
import com.shengye.design.glass.Glass
import com.shengye.design.glass.GlassButton
import com.shengye.design.glass.GlassCard
import com.shengye.design.glass.GlassEmpty
import com.shengye.design.glass.GlassScaffold
import com.shengye.design.glass.GlassTabs
import com.shengye.design.glass.ShengyeTheme
import com.shengye.design.glass.Tokens
import com.shengye.design.glass.Tone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 15 起强制 edge-to-edge，显式声明一次，低版本行为也一致：
        // 壁纸铺到状态栏和手势条底下，内容由 design:glass 避让。
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            ShengyeTheme {
                AuroraBackground { Shell() }
            }
        }
    }
}

/**
 * 只做导航接线：四个空间 + 空壳内容。
 * 真实内容由 feature:* 填，本文件永远只有「哪个 tab 显示哪个 feature」。
 */
@Composable
private fun Shell() {
    var tab by remember { mutableIntStateOf(0) }
    GlassScaffold(
        bottomBar = { GlassTabs(selected = tab, onSelect = { tab = it }) },
    ) {
        when (tab) {
            0 -> LibrarySpace()
            1 -> EmptySpace("从「我的书」里挑一本")
            2 -> EmptySpace("还没有在做的书")
            3 -> EmptySpace("还没开始听")
        }
    }
}

/**
 * 我的书。
 *
 * 故意没有页面标题：底部 tab 已经写着「我的书」，顶栏再写一遍是同一个词出现两次。
 * 粘贴区也不写标题——输入框里的「在这里粘贴」已经说明它是什么。
 * 首页只需要两样东西：能粘的地方，和书单。
 */
@Composable
private fun LibrarySpace() {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = Tokens.padScreen)) {
            Spacer(Modifier.height(12.dp))
            PasteArea()
            Spacer(Modifier.height(16.dp))
            GlassCard {
                BookRow(title = "活着", meta = "6 章 · 听完 4 章", chip = "制作中", tone = Tone.WARN, pct = 66)
            }
        }
    }
}

/**
 * 粘贴区。全 App 最重要的入口，位置必须在家门口。
 *
 * 输入框和两个按钮**在同一张玻璃卡里**：分开成「一个框 + 下面飘两个按钮」时，
 * 中间大片留白会把视线扯散，看起来像两个不相干的东西。
 * 框高固定 72dp（约两行半），不按屏幕比例撑——框大了只会显空。
 */
@Composable
private fun PasteArea() {
    val c = Glass.colors
    var text by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(12.dp)

    GlassCard {
        Box(
            Modifier
                .fillMaxWidth()
                .height(Tokens.pasteBox)
                .clip(shape)
                .background(c.hair)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (text.isEmpty()) {
                Text("在这里粘贴…", color = c.sub, fontSize = Tokens.body)
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(color = c.ink, fontSize = Tokens.body),
                cursorBrush = SolidColor(c.accent),
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.height(Tokens.gap))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            GlassButton("导入文件", kind = ButtonKind.GHOST) { }
            Spacer(Modifier.weight(1f))
            GlassButton("合成", enabled = text.isNotBlank()) { }
        }
    }
}

@Composable
private fun EmptySpace(empty: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassEmpty(empty)
    }
}

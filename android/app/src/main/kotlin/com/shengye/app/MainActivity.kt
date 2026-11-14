package com.shengye.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shengye.design.glass.AuroraBackground
import com.shengye.design.glass.Glass
import com.shengye.design.glass.GlassCard
import com.shengye.design.glass.GlassChip
import com.shengye.design.glass.GlassEmpty
import com.shengye.design.glass.GlassTabs
import com.shengye.design.glass.GlassTopBar
import com.shengye.design.glass.ShengyeTheme
import com.shengye.design.glass.TextAction
import com.shengye.design.glass.Tokens
import com.shengye.design.glass.Tone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            when (tab) {
                0 -> LibrarySpace()
                1 -> SimpleSpace("这一本", "从「我的书」里挑一本")
                2 -> SimpleSpace("正在做", "还没有在做的书")
                3 -> SimpleSpace("正在听", "还没在听什么")
            }
        }
        GlassTabs(selected = tab, onSelect = { tab = it })
    }
}

/** 我的书：顶部约 1/3 屏固定留给「粘贴一段 → 合成」，其余是书单。 */
@Composable
private fun LibrarySpace() {
    Column(Modifier.fillMaxSize()) {
        GlassTopBar(title = "我的书", subtitle = "3 本 · 2 段片段")
        Column(
            Modifier
                .padding(horizontal = Tokens.padScreen)
                .fillMaxWidth()
        ) {
            PasteCard(Modifier.heightIn(min = 150.dp))
            Spacer(Modifier.height(Tokens.gap))
            Text(
                "书单",
                color = Glass.colors.sub,
                fontSize = Tokens.sub,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 6.dp),
            )
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("活着", color = Glass.colors.ink, fontSize = Tokens.title, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(3.dp))
                        Text("6 章 · 听完 4 章", color = Glass.colors.sub, fontSize = Tokens.sub)
                    }
                    GlassChip("制作中", Tone.WARN)
                }
            }
        }
    }
}

/** 粘贴区：这是首页最重要的入口，不许藏在二级页面里。 */
@Composable
private fun PasteCard(modifier: Modifier = Modifier) {
    val c = Glass.colors
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text("粘贴一段文字，直接合成", color = c.ink, fontSize = Tokens.title, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(c.hair)
                .border(1.dp, c.hair, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text("在这里粘贴…", color = c.sub, fontSize = Tokens.body)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextAction("导入文件", c.sub) { }
            Spacer(Modifier.width(6.dp))
            TextAction("合成这段", c.accent) { }
        }
    }
}

@Composable
private fun SimpleSpace(title: String, empty: String) {
    Column(Modifier.fillMaxSize()) {
        GlassTopBar(title = title)
        Box(Modifier.padding(horizontal = Tokens.padScreen).fillMaxWidth()) {
            GlassCard { GlassEmpty(empty) }
        }
    }
}

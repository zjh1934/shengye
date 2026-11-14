package com.shengye.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.shengye.contract.Book
import com.shengye.contract.ChapterState
import com.shengye.design.glass.BookRow
import com.shengye.design.glass.ButtonKind
import com.shengye.design.glass.Glass
import com.shengye.design.glass.GlassButton
import com.shengye.design.glass.GlassCard
import com.shengye.design.glass.GlassDivider
import com.shengye.design.glass.GlassEmpty
import com.shengye.design.glass.Tokens

/**
 * 我的书。
 *
 * 这个 Composable **只收数据和回调**：不读盘、不查库、不持有第二份书单。
 * 数据由 app 层从 core:data 取，动作通过参数传回来。
 * 这样它可以在预览里用假数据跑，也可以在真机上接真库，两边长得一模一样。
 *
 * 没有页面标题（tab 已写「我的书」），粘贴区没有标题（placeholder 已说明）。
 */
@Composable
fun LibraryScreen(
    books: List<Book>,
    paste: String,
    onPasteChange: (String) -> Unit,
    importing: Boolean,
    importError: String?,
    onImportFile: () -> Unit,
    onSynthPaste: () -> Unit,
    onOpenBook: (Book) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Tokens.padScreen)
    ) {
        Spacer(Modifier.height(12.dp))
        PasteArea(paste, onPasteChange, importing, onImportFile, onSynthPaste)
        if (importError != null) {
            Spacer(Modifier.height(8.dp))
            Text(importError, color = Glass.colors.bad, fontSize = Tokens.sub)
        }
        Spacer(Modifier.height(16.dp))

        if (books.isEmpty() && !importing) {
            GlassEmpty("还没有书")
        } else {
            GlassCard {
                books.forEachIndexed { i, b ->
                    BookRow(
                        title = b.title,
                        meta = "${b.chapterCount} 章 · ${b.chars} 字",
                        chip = ChapterState.IDLE.label,
                        onClick = { onOpenBook(b) },
                    )
                    if (i < books.lastIndex) GlassDivider()
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

/** 粘贴区：框与两个按钮在同一张卡里。框高固定，不按屏幕撑。 */
@Composable
private fun PasteArea(
    text: String,
    onTextChange: (String) -> Unit,
    busy: Boolean,
    onImportFile: () -> Unit,
    onSynth: () -> Unit,
) {
    val c = Glass.colors
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
                onValueChange = onTextChange,
                textStyle = TextStyle(color = c.ink, fontSize = Tokens.body),
                cursorBrush = SolidColor(c.accent),
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.height(Tokens.gap))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            GlassButton("导入文件", kind = ButtonKind.GHOST, enabled = !busy, onClick = onImportFile)
            Spacer(Modifier.weight(1f))
            GlassButton(
                if (busy) "导入中" else "合成",
                enabled = !busy && text.isNotBlank(),
                onClick = onSynth,
            )
        }
    }
}

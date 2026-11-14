package com.shengye.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.shengye.contract.Book
import com.shengye.core.data.FileBookRepository
import com.shengye.design.glass.AuroraBackground
import com.shengye.design.glass.GlassEmpty
import com.shengye.design.glass.GlassScaffold
import com.shengye.design.glass.GlassTabs
import com.shengye.design.glass.ShengyeTheme
import com.shengye.feature.library.LibraryScreen
import kotlinx.coroutines.launch

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
 * app 层只做接线：持有数据源、把系统能力（选文件）翻译成仓储调用、决定哪个 tab 显示哪个 feature。
 * 界面长什么样在 feature:library，路径与编码在 core:data，本文件不该有第三份逻辑。
 */
@Composable
private fun Shell() {
    val ctx = LocalContext.current
    val repo = remember { FileBookRepository(ctx.filesDir) }
    val scope = rememberCoroutineScope()

    var tab by remember { mutableIntStateOf(0) }
    var books by remember { mutableStateOf(emptyList<Book>()) }
    var paste by remember { mutableStateOf("") }
    var importing by remember { mutableStateOf(false) }
    var importError by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch { books = repo.list() }
    }
    LaunchedEffect(Unit) { refresh() }

    fun import(name: String, bytes: ByteArray) {
        importing = true
        importError = null
        scope.launch {
            val r = repo.importBytes(name, bytes)
            importing = false
            r.onFailure { importError = it.message ?: "导入失败" }
            refresh()
        }
    }

    val pick = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val bytes = runCatching {
                    ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull()
                if (bytes == null) importError = "读不到这个文件"
                else import(displayName(ctx, uri), bytes)
            }
        }
    }

    GlassScaffold(
        bottomBar = { GlassTabs(selected = tab, onSelect = { tab = it }) },
    ) {
        when (tab) {
            0 -> LibraryScreen(
                books = books,
                paste = paste,
                onPasteChange = { paste = it },
                importing = importing,
                importError = importError,
                onImportFile = { pick.launch(arrayOf("text/*")) },
                onSynthPaste = {
                    // TTS 引擎还没接（platform:tts）。这里先落成一条内容，
                    // 不假装合成成功——状态必须说真话。
                    val t = paste
                    paste = ""
                    importing = true
                    importError = null
                    scope.launch {
                        val r = repo.importText(t)
                        importing = false
                        r.onFailure { importError = it.message ?: "合成失败" }
                        refresh()
                    }
                },
                onOpenBook = { },
            )
            1 -> EmptySpace("从「我的书」里挑一本")
            2 -> EmptySpace("还没有在做的书")
            3 -> EmptySpace("还没开始听")
        }
    }
}

@Composable
private fun EmptySpace(empty: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassEmpty(empty)
    }
}

private fun displayName(ctx: Context, uri: Uri): String {
    ctx.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
        if (it.moveToFirst()) {
            val i = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0) return it.getString(i)
        }
    }
    return uri.lastPathSegment?.substringAfterLast('/') ?: "导入的书"
}

package com.shengye.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
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
import com.shengye.contract.Chapter
import com.shengye.contract.ProduceConfig
import com.shengye.contract.ProduceTask
import com.shengye.contract.TaskState
import com.shengye.core.data.FileBookRepository
import com.shengye.core.data.FileTaskRepository
import com.shengye.design.glass.AuroraBackground
import com.shengye.design.glass.Glass
import com.shengye.design.glass.GlassBackBar
import com.shengye.design.glass.GlassEmpty
import com.shengye.design.glass.GlassScaffold
import com.shengye.design.glass.GlassTabs
import com.shengye.design.glass.ShengyeTheme
import com.shengye.design.glass.TextAction
import com.shengye.domain.Progress
import com.shengye.feature.book.BookScreen
import com.shengye.feature.book.ReaderBody
import com.shengye.feature.book.ReaderBottomBar
import com.shengye.feature.book.SelectBottomBar
import com.shengye.feature.book.SelectChaptersScreen
import com.shengye.feature.library.LibraryScreen
import com.shengye.feature.tasks.TaskRow
import com.shengye.feature.tasks.TasksScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 15 起强制 edge-to-edge，壁纸铺到系统栏底下，内容由 design:glass 避让
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)
        setContent {
            ShengyeTheme {
                AuroraBackground { Shell() }
            }
        }
    }
}

/** 「这一本」之上叠的全屏子流程。NONE 就是章节列表本身。 */
private enum class Overlay { NONE, SELECT, READER }

/**
 * app 层只做接线：持有数据源、把系统能力（选文件）翻译成仓储调用、
 * 决定哪个 tab / 哪个子流程显示哪个 feature。界面长什么样在 feature，
 * 路径与编码在 core:data，进度口径在 core:domain，本文件不该有第三份逻辑。
 */
@Composable
private fun Shell() {
    val ctx = LocalContext.current
    val bookRepo = remember { FileBookRepository(ctx.filesDir) }
    val taskRepo = remember { FileTaskRepository(ctx.filesDir, bookRepo) }
    val scope = rememberCoroutineScope()

    var tab by remember { mutableIntStateOf(0) }
    var books by remember { mutableStateOf(emptyList<Book>()) }
    var paste by remember { mutableStateOf("") }
    var importing by remember { mutableStateOf(false) }
    var importError by remember { mutableStateOf<String?>(null) }

    var openBook by remember { mutableStateOf<Book?>(null) }
    var chapters by remember { mutableStateOf(emptyList<Chapter>()) }
    var overlay by remember { mutableStateOf(Overlay.NONE) }
    var selected by remember { mutableStateOf(emptySet<Int>()) }
    var readIndex by remember { mutableIntStateOf(0) }
    var readerText by remember { mutableStateOf("") }
    var fontSp by remember { mutableIntStateOf(16) }
    var tasks by remember { mutableStateOf(emptyList<ProduceTask>()) }

    fun refreshBooks() {
        scope.launch { books = bookRepo.list() }
    }
    fun refreshTasks() {
        scope.launch { tasks = taskRepo.queue() }
    }
    LaunchedEffect(Unit) { refreshBooks(); refreshTasks() }

    fun import(name: String, bytes: ByteArray) {
        importing = true
        importError = null
        scope.launch {
            val r = bookRepo.importBytes(name, bytes)
            importing = false
            r.onFailure { importError = it.message ?: "导入失败" }
            refreshBooks()
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

    fun openThisBook(b: Book) {
        openBook = b
        tab = 1
        overlay = Overlay.NONE
        selected = emptySet()
        scope.launch { chapters = bookRepo.chapters(b.id) }
    }

    fun loadReader(index: Int) {
        val b = openBook ?: return
        readIndex = index
        scope.launch {
            val full = bookRepo.readText(b.id)
            val ch = chapters.getOrNull(index)
            readerText = if (ch == null) "" else full.substring(
                ch.startOffset.coerceAtMost(full.length),
                ch.endOffset.coerceAtMost(full.length),
            )
            overlay = Overlay.READER
        }
    }

    fun submit() {
        val b = openBook ?: return
        val picks = selected.toList()
        if (picks.isEmpty()) return
        scope.launch {
            val r = taskRepo.submit(b.id, picks, defaultProduceConfig())
            r.onFailure { importError = it.message ?: "提交失败" }
            r.onSuccess {
                selected = emptySet()
                overlay = Overlay.NONE
                tab = 2
                refreshTasks()
            }
        }
    }

    // 系统返回键与顶栏返回走同一套：先退子流程，再回书单
    fun goBack() {
        if (overlay != Overlay.NONE) overlay = Overlay.NONE else tab = 0
    }
    BackHandler(enabled = overlay != Overlay.NONE || tab != 0) { goBack() }

    val showBackBar = overlay != Overlay.NONE || (tab == 1 && openBook != null)
    val topTitle = when (overlay) {
        Overlay.SELECT -> "选章制作"
        Overlay.READER -> chapters.getOrNull(readIndex)?.title ?: ""
        Overlay.NONE -> openBook?.title ?: ""
    }
    val topSub = when (overlay) {
        Overlay.READER -> openBook?.title
        Overlay.NONE -> openBook?.let { "${it.chapterCount} 章 · ${formatWan(it.chars)} 字" }
        Overlay.SELECT -> null
    }

    val taskRows = tasks.map { t ->
        val running = t.state != TaskState.QUEUED && t.state != TaskState.PREPARING
        TaskRow(
            id = t.id,
            title = t.bookTitle,
            state = t.state,
            line = if (running) Progress.line(
                state = t.state,
                chapterCount = t.chapterIndexes.size,
                doneChunks = t.chunksDone,
                totalChunks = t.chunksTotal,
                subPhase = t.subPhase?.label,
            ) else "${t.chapterIndexes.size} 章 · ${t.chunksTotal} 块",
            pct = if (running) Progress.pct(t.chunksDone, t.chunksTotal) else null,
        )
    }

    // 显式声明类型，避免条件分支里的 lambda 丢失 @Composable 推断
    val selectAction: (@Composable () -> Unit)? = if (overlay == Overlay.SELECT) {
        { TextAction("全选", Glass.colors.sub) { selected = chapters.indices.toSet() } }
    } else null

    val backBar: (@Composable () -> Unit)? = if (showBackBar) {
        {
            GlassBackBar(
                title = topTitle,
                subtitle = topSub,
                onBack = { goBack() },
                action = selectAction,
            )
        }
    } else null

    GlassScaffold(
        topBar = backBar,
        bottomBar = {
            when (overlay) {
                Overlay.SELECT -> SelectBottomBar(selected.size) { submit() }
                Overlay.READER -> ReaderBottomBar(
                    fontSp = fontSp,
                    onFontChange = { fontSp = it },
                    hasPrev = readIndex > 0,
                    hasNext = readIndex < chapters.lastIndex,
                    onPrev = { loadReader(readIndex - 1) },
                    onNext = { loadReader(readIndex + 1) },
                )
                Overlay.NONE -> GlassTabs(selected = tab, onSelect = { t -> tab = t; overlay = Overlay.NONE })
            }
        },
    ) {
        when {
            overlay == Overlay.READER -> ReaderBody(readerText, fontSp)
            overlay == Overlay.SELECT -> SelectChaptersScreen(
                chapters = chapters,
                selected = selected,
                onToggle = { i -> selected = if (i in selected) selected - i else selected + i },
            )
            tab == 0 -> LibraryScreen(
                books = books,
                paste = paste,
                onPasteChange = { paste = it },
                importing = importing,
                importError = importError,
                onImportFile = { pick.launch(arrayOf("text/*")) },
                onSynthPaste = {
                    // TTS 引擎未接，粘贴内容先落成一条可阅读的书，不假装合成成功
                    val t = paste
                    paste = ""
                    importing = true
                    scope.launch {
                        val r = bookRepo.importText(t)
                        importing = false
                        r.onFailure { importError = it.message ?: "失败" }
                        refreshBooks()
                    }
                },
                onOpenBook = { openThisBook(it) },
            )
            tab == 1 -> if (openBook == null) {
                EmptySpace("从「我的书」里挑一本")
            } else {
                BookScreen(
                    chapters = chapters,
                    onProduce = { overlay = Overlay.SELECT; selected = emptySet() },
                    onRead = { loadReader(it) },
                )
            }
            tab == 2 -> TasksScreen(
                rows = taskRows,
                onCancel = { id -> scope.launch { taskRepo.cancel(id); refreshTasks() } },
            )
            else -> EmptySpace("还没开始听")
        }
    }
}

@Composable
private fun EmptySpace(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GlassEmpty(text)
    }
}

private fun defaultProduceConfig() = ProduceConfig(
    readingStyle = "叙述", pace = "适中", intensity = "自然",
    segmentSilenceMs = 400, autoJoinChapters = true, seamHandling = true,
)

private fun formatWan(n: Int): String =
    if (n >= 10000) "%.1f 万".format(n / 10000.0) else "%,d".format(n)

private fun displayName(ctx: Context, uri: Uri): String {
    ctx.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
        if (it.moveToFirst()) {
            val i = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0) return it.getString(i)
        }
    }
    return uri.lastPathSegment?.substringAfterLast('/') ?: "导入的书"
}

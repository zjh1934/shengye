package com.shengye.feature.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shengye.contract.TaskState
import com.shengye.design.glass.Glass
import com.shengye.design.glass.GlassEmpty
import com.shengye.design.glass.TaskCard
import com.shengye.design.glass.TextAction
import com.shengye.design.glass.Tokens

/**
 * 任务卡要显示的东西，一个纯展示快照。
 * 由 app 层用 domain/Progress 算好再传进来——feature 不依赖 domain，
 * 不许自己拼第二份进度口径。
 */
data class TaskRow(
    val id: String,
    val title: String,
    val state: TaskState,
    val line: String,
    /** null = 不画进度条（排队中 / 准备中画一根 0% 空条是纯噪音）。 */
    val pct: Int?,
)

/**
 * 正在做 · 任务队列。
 * 只收快照和回调。状态词从 TaskState.label 取，不自己拼。
 * 排队中不给「提前」这种单队列下点了等于没反应的按钮。
 */
@Composable
fun TasksScreen(
    rows: List<TaskRow>,
    onCancel: (String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Tokens.padScreen)
    ) {
        Spacer(Modifier.height(12.dp))
        if (rows.isEmpty()) {
            GlassEmpty("还没有在做的书")
        } else {
            rows.forEach { r ->
                TaskCard(
                    title = r.title,
                    state = r.state,
                    line = r.line,
                    pct = r.pct,
                    actions = {
                        TextAction("取消", Glass.colors.sub) { onCancel(r.id) }
                    },
                )
                Spacer(Modifier.height(12.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

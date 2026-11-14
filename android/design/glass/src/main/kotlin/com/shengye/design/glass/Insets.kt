package com.shengye.design.glass

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ★ 系统栏唯一处理点。页面禁止自己写 statusBarsPadding / navigationBarsPadding。
 *
 * 为什么必须集中管：targetSdk 35 起 Android 15 强制 edge-to-edge，窗口不再自动避让。
 * 壁纸要铺到状态栏和手势条底下才像毛玻璃，但**内容**必须避让——
 * 这两件事分开做，做错一边就是标题压时间、tab 被手势条吃掉。
 *
 * 分工：
 * - 壁纸：AuroraBackground 铺满，不管 insets（故意的）
 * - 顶栏：GlassTopBar 自己吃 statusBars
 * - 底栏：GlassTabs 自己吃 navigationBars，且底色铺到屏幕最底
 * - 横屏刘海 / 侧边导航栏：只有 GlassScaffold 在这一层统一处理一次，避免上下两栏各加一遍
 */
@Composable
fun GlassScaffold(
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            // 只处理左右：竖屏三键导航时这里为 0，横屏刘海/侧栏时才生效
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
    ) {
        topBar?.invoke()
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                // 没顶栏的页面（首页这类）由这里补状态栏高度，页面不用自己记
                .then(
                    if (topBar == null) {
                        Modifier.windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                    } else {
                        Modifier
                    }
                )
                // 键盘弹起时内容整体抬起，粘贴框不会被遮
                .imePadding()
        ) {
            content()
        }
        bottomBar?.invoke()
    }
}

/** 顶栏吃状态栏高度。沉浸页（播放器全屏）传 false 自己安排。 */
@Composable
internal fun Modifier.statusBarInset(enabled: Boolean): Modifier =
    if (enabled) windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top)) else this

/** 底栏吃手势条/导航栏高度。只加在内容上，背景另行铺满。 */
@Composable
internal fun Modifier.navBarInset(enabled: Boolean): Modifier =
    if (enabled) windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)) else this

/**
 * 状态栏 / 导航栏图标黑白跟随主题。
 *
 * 浅色壁纸必须用深色图标，深色壁纸必须用浅色图标，否则「时间看不见」。
 * 系统只认 isAppearanceLightXxx，跟 Compose 主题没有自动联动，只能手动同步。
 */
@Composable
internal fun SystemBarAppearance(dark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = remember(view) { view.context.findActivity()?.window } ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

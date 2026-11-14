package com.shengye.design.glass

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/** 当前主题的一套 token。页面一律 `Glass.colors.xxx` 取，禁止直接写 Color(0xFF...)。 */
val LocalGlass = staticCompositionLocalOf { Tokens.Light }

/** 本机能不能真模糊。页面不该关心，只有 design:glass 内部用。 */
val LocalBlurSupported = staticCompositionLocalOf { true }

object Glass {
    val colors: GlassColors
        @Composable get() = LocalGlass.current
}

/**
 * 主题入口。四空间、调音台、弹窗全部包在这里面。
 *
 * @param dark 传 null 跟随系统；true/false 强制（设置页里的「深色 / 浅色 / 跟随系统」）
 */
@Composable
fun ShengyeTheme(
    dark: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val useDark = dark ?: isSystemInDarkTheme()
    val colors = if (useDark) Tokens.Dark else Tokens.Light
    val blur = rememberBlurSupported()

    CompositionLocalProvider(
        LocalGlass provides colors,
        LocalBlurSupported provides blur,
    ) {
        MaterialTheme(
            colorScheme = if (useDark) darkColorScheme() else lightColorScheme(),
            content = content,
        )
    }
}

/**
 * API 31 以下没有系统级 blur，硬做只会掉帧；
 * 省电模式下也主动关掉——省电模式用户要的是续航，不是磨砂。
 * 关掉之后靠 `glassStrong`（更不透明）+ 细边 + 阴影撑层级，功能一个不少。
 */
@Composable
fun rememberBlurSupported(): Boolean {
    val context = LocalContext.current
    return remember(context) { blurSupported(context) }
}

fun blurSupported(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
    val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    return pm?.isPowerSaveMode != true
}

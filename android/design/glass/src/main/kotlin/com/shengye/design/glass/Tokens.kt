package com.shengye.design.glass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ★ 视觉唯一真源。逐项对应 contracts/GLASS_TOKENS.md，页面禁止写死色值 / 圆角 / 字号。
 *
 * 换算说明：契约里的 px 是以 268px 宽设备图为基准（见 GLASS_TOKENS §2 字号行），
 * 真机按 393dp 宽换算，比例约 1.47，所以圆角与字号都比图上数值大一号。
 * 这张表就是那个换算的唯一落点，别在页面里各算各的。
 */
@Immutable
data class GlassColors(
    val accent: Color,
    val accentSoft: Color,
    val good: Color,
    val warn: Color,
    val bad: Color,

    val ink: Color,
    val sub: Color,
    val hair: Color,

    /** 卡片底 */
    val glass: Color,
    /** 降级用：不支持模糊时提高不透明度，靠它撑住层级 */
    val glassStrong: Color,
    val glassEdge: Color,

    val tabInk: Color,

    val wallTop: Color,
    val wallMid: Color,
    val wallBottom: Color,

    val orbA: Color,
    val orbB: Color,
    val orbC: Color,
    val orbD: Color,
    val orbAlpha: Float,

    val isDark: Boolean,
)

object Tokens {

    val Light = GlassColors(
        accent = Color(0xFF4F46E5),
        accentSoft = Color(0xFFEEF0FE),
        good = Color(0xFF0E9F6E),
        warn = Color(0xFFB45309),
        bad = Color(0xFFDC2626),

        ink = Color(0xFF1B2030),
        sub = Color(0xFF6B7280),
        hair = Color(0x1A1B2030),

        glass = Color(0x94FFFFFF),
        glassStrong = Color(0xE0FFFFFF),
        glassEdge = Color(0xD9FFFFFF),

        tabInk = Color(0xFF8A90A6),

        wallTop = Color(0xFFEEF2FF),
        wallMid = Color(0xFFF5F3FF),
        wallBottom = Color(0xFFECFEFF),

        orbA = Color(0xFF7C3AED),
        orbB = Color(0xFFEC4899),
        orbC = Color(0xFF3B82F6),
        orbD = Color(0xFFF59E0B),
        orbAlpha = 0.55f,

        isDark = false,
    )

    val Dark = GlassColors(
        accent = Color(0xFFA5B4FC),
        accentSoft = Color(0xFF313C63),
        good = Color(0xFF34D399),
        warn = Color(0xFFFBBF24),
        bad = Color(0xFFF87171),

        ink = Color(0xFFFFFFFF),
        // 深色下 sub 必须有明度差：旧版把 text / sub 都刷纯白，层级全塌
        sub = Color(0x9EFFFFFF),
        hair = Color(0x1FFFFFFF),

        glass = Color(0x8F181D30),
        glassStrong = Color(0xE0181D30),
        glassEdge = Color(0x29FFFFFF),

        tabInk = Color(0x73FFFFFF),

        wallTop = Color(0xFF0B1020),
        wallMid = Color(0xFF171233),
        wallBottom = Color(0xFF0A1A1F),

        orbA = Color(0xFF6D5BD0),
        orbB = Color(0xFFC2649E),
        orbC = Color(0xFF4F7FD0),
        orbD = Color(0xFFC98B3E),
        orbAlpha = 0.42f,

        isDark = true,
    )

    // 几何
    val radiusCard: Dp = 20.dp
    val radiusDialog: Dp = 24.dp
    val radiusSheet: Dp = 28.dp
    val gap: Dp = 10.dp
    val padCard: Dp = 14.dp
    val padScreen: Dp = 14.dp

    /**
     * 首页粘贴框高度。约两行半正文。
     * 别按屏幕比例设——框撑满只会显空，粘贴这个动作不需要那么大的一块地。
     */
    val pasteBox: Dp = 72.dp

    // 模糊半径（仅 API 31+ 生效）
    val blurWall: Dp = 48.dp
    val blurBar: Dp = 26.dp

    // 字号
    val display = 28.sp      // 页面大标题，一屏只一个
    val title = 17.sp        // 卡片标题
    val body = 15.sp         // 正文
    val sub = 13.sp          // 次要说明、状态行
    val tiny = 11.sp         // 标签、角标
    val number = 24.sp       // 进度环数字
}

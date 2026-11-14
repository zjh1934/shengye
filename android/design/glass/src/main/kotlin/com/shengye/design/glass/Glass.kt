package com.shengye.design.glass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 四空间的统一骨架：壁纸 + 光斑 → 内容 → 底部四格。
 *
 * 毛玻璃怎么来的：壁纸层整体模糊（API 31+），卡片半透明盖上去，视觉上就是磨砂玻璃。
 * 不去截屏做 backdrop blur——那要拿 SurfaceControl，在低端机上必掉帧，得不偿失。
 * 低版本 / 省电模式：壁纸不模糊，但光斑本身就是柔和径向渐变，看起来仍然干净。
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val c = Glass.colors
    val blur = LocalBlurSupported.current

    Box(modifier.fillMaxSize()) {
        Canvas(
            Modifier
                .fillMaxSize()
                .then(
                    if (blur) Modifier.blur(Tokens.blurWall, BlurredEdgeTreatment.Unbounded)
                    else Modifier
                )
        ) {
            drawRect(Brush.verticalGradient(listOf(c.wallTop, c.wallMid, c.wallBottom)))
            val r = size.minDimension
            drawOrb(c.orbA, Offset(size.width * 0.16f, size.height * 0.08f), r * 0.62f, c.orbAlpha)
            drawOrb(c.orbB, Offset(size.width * 0.92f, size.height * 0.26f), r * 0.52f, c.orbAlpha * 0.9f)
            drawOrb(c.orbC, Offset(size.width * 0.10f, size.height * 0.74f), r * 0.58f, c.orbAlpha * 0.85f)
            drawOrb(c.orbD, Offset(size.width * 0.86f, size.height * 0.92f), r * 0.48f, c.orbAlpha * 0.7f)
        }
        content()
    }
}

private fun DrawScope.drawOrb(color: Color, center: Offset, radius: Float, alpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

/** 玻璃卡。整屏只靠它分层，不堆边框。 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: androidx.compose.ui.unit.Dp = Tokens.padCard,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = Glass.colors
    val blur = LocalBlurSupported.current
    val shape = RoundedCornerShape(Tokens.radiusCard)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (c.isDark) 12.dp else 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (c.isDark) 0.5f else 0.12f),
                spotColor = Color.Black.copy(alpha = if (c.isDark) 0.5f else 0.14f),
            )
            .clip(shape)
            .background(if (blur) c.glass else c.glassStrong)
            .border(1.dp, c.glassEdge, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(contentPadding),
        content = content,
    )
}

/**
 * 页面顶栏：大标题 + 可选一行副标题 + 右侧文字动作。禁止纯图标无文字。
 *
 * 默认自己吃状态栏高度。沉浸页（播放器全屏）传 `statusBarInset = false`，
 * 由页面自己安排，避免两处各加一遍变成「标题悬在半空」。
 */
@Composable
fun GlassTopBar(
    title: String,
    subtitle: String? = null,
    statusBarInset: Boolean = true,
    action: (@Composable () -> Unit)? = null,
) {
    val c = Glass.colors
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarInset(statusBarInset)
            .padding(horizontal = Tokens.padScreen, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = c.ink,
                fontSize = Tokens.display,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = c.sub,
                    fontSize = Tokens.sub,
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
 * 底部四空间。文字必须留，图标只是辅助。
 *
 * 底色铺到屏幕最底（盖住手势条区域），文字/点击区避让导航栏——
 * 否则要么底部露出壁纸一道缝，要么字被手势条压住。
 */
@Composable
fun GlassTabs(
    selected: Int,
    onSelect: (Int) -> Unit,
    labels: List<String> = listOf("我的书", "这一本", "正在做", "正在听"),
    navBarInset: Boolean = true,
) {
    val c = Glass.colors
    Box(Modifier.fillMaxWidth().background(c.glassStrong)) {
        Row(
            Modifier
                .fillMaxWidth()
                .navBarInset(navBarInset)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            labels.forEachIndexed { i, label ->
                val on = i == selected
                Column(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(i) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = label,
                        color = if (on) c.accent else c.tabInk,
                        fontSize = Tokens.sub,
                        fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

/** 细分隔线。列表里用，别用卡片堆卡片。 */
@Composable
fun GlassDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Glass.colors.hair)
    )
}

enum class Tone { NEUTRAL, ACCENT, GOOD, WARN, BAD }

/** 状态胶囊。词表在 contract 里，这里只负责把词染成对的颜色。 */
@Composable
fun GlassChip(text: String, tone: Tone = Tone.NEUTRAL) {
    val c = Glass.colors
    val (fg, bg) = when (tone) {
        Tone.ACCENT -> c.accent to c.accentSoft
        Tone.GOOD -> c.good to c.good.copy(alpha = 0.14f)
        Tone.WARN -> c.warn to c.warn.copy(alpha = 0.14f)
        Tone.BAD -> c.bad to c.bad.copy(alpha = 0.14f)
        Tone.NEUTRAL -> c.sub to c.hair
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(text, color = fg, fontSize = Tokens.tiny, maxLines = 1)
    }
}

/**
 * 确认弹窗。按 ACCEPTANCE B7：只说「删什么」+「不可撤销」，不写解释性段落。
 * @param primary 主操作（危险操作时由调用方传 BAD 色调的文案，如「删除」）
 */
@Composable
fun GlassDialog(
    title: String,
    text: String,
    primary: String,
    onPrimary: () -> Unit,
    secondary: String? = "取消",
    onSecondary: (() -> Unit)? = null,
    onDismiss: () -> Unit = {},
) {
    val c = Glass.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .shadow(18.dp, RoundedCornerShape(Tokens.radiusDialog))
                .clip(RoundedCornerShape(Tokens.radiusDialog))
                .background(c.glassStrong)
                .border(1.dp, c.glassEdge, RoundedCornerShape(Tokens.radiusDialog))
                .padding(18.dp),
        ) {
            Text(title, color = c.ink, fontSize = Tokens.title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(text, color = c.sub, fontSize = Tokens.body)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                if (secondary != null) {
                    TextAction(secondary, c.sub) { onSecondary?.invoke() ?: onDismiss() }
                    Spacer(Modifier.width(18.dp))
                }
                TextAction(primary, c.bad) { onPrimary() }
            }
        }
    }
}

/** 文字按钮。统一在这里定点击区域与字号。 */
@Composable
fun TextAction(text: String, color: Color, onClick: () -> Unit) {
    Text(
        text = text,
        color = color,
        fontSize = Tokens.body,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

/**
 * 按钮。一屏最多一个 PRIMARY。
 * GHOST 用于并列的次要动作，两个都是实心会互相抢。
 */
enum class ButtonKind { PRIMARY, GHOST }

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    kind: ButtonKind = ButtonKind.PRIMARY,
    modifier: Modifier = Modifier,
) {
    val c = Glass.colors
    val ghost = kind == ButtonKind.GHOST
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .clip(shape)
            .background(
                when {
                    !enabled -> c.hair
                    ghost -> c.glass
                    else -> c.accent
                }
            )
            .then(if (ghost) Modifier.border(1.dp, c.glassEdge, shape) else Modifier)
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = if (ghost) 16.dp else 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = when {
                !enabled -> c.sub
                ghost -> c.ink
                else -> Color.White
            },
            fontSize = Tokens.body,
            fontWeight = if (ghost) FontWeight.Medium else FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

/** 一行放两个东西：左文右值。设置页、参数摘要反复用。 */
@Composable
fun KeyValueRow(key: String, value: String, valueColor: Color? = null) {
    val c = Glass.colors
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(key, color = c.sub, fontSize = Tokens.body, modifier = Modifier.weight(1f))
        Text(value, color = valueColor ?: c.ink, fontSize = Tokens.body)
    }
}

/** 空态。一句话 + 一个动作，不写教程。 */
@Composable
fun GlassEmpty(text: String, action: (@Composable () -> Unit)? = null) {
    val c = Glass.colors
    Column(
        Modifier.fillMaxWidth().padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text, color = c.sub, fontSize = Tokens.body)
        if (action != null) {
            Spacer(Modifier.height(12.dp))
            action()
        }
    }
}

/** 纵向间距，页面里别写死 dp。 */
@Composable
fun VGap(h: androidx.compose.ui.unit.Dp = Tokens.gap) = Spacer(Modifier.height(h))

@Composable
fun RowScope.HGap(w: androidx.compose.ui.unit.Dp = Tokens.gap) = Spacer(Modifier.width(w))

@Composable
fun IconBox(size: androidx.compose.ui.unit.Dp = 34.dp, content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(Glass.colors.accentSoft),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

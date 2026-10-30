package com.athar.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.athar.app.ui.theme.AtharBackdropTint
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharBackgroundState
import com.athar.app.ui.theme.AtharBackgroundStyle
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════
// ATHAR — Backdrops
//
// Hand-built Islamic backdrops sitting behind app surfaces:
//   ISLAMIC — interconnected Islamic geometric lines: 8-pointed star
//             rosettes (khatam) linked across nodes by diagonal strapwork
//             ribbons and intermediate corner diamonds, in continuous fluid motion.
//   LETTERS — a woven field of isolated Naskh letters in slow drift.
// ══════════════════════════════════════════════════════════════

/**
 * The app-wide backdrop.
 *
 * Draws whichever [AtharBackgroundStyle] is selected. Both styles drift on
 * continuous smooth cycles; SOLID draws nothing and lets the theme's own colour
 * stand edge to edge.
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    tint: Color = AtharBackdropTint,
    alpha: Float = 0.12f,
    animated: Boolean = true,
    cellDp: Float = 68f,
    styleOverride: AtharBackgroundStyle? = null
) {
    val style = styleOverride ?: AtharBackgroundState.current

    val cellPx = with(LocalDensity.current) { cellDp.dp.toPx() }
    val typeface = rememberHurufTypeface()

    val baked = remember { BakedBackdrop() }
    baked.layer = rememberGraphicsLayer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val transition = rememberInfiniteTransition(label = "patternMotion")

    // Continuous smooth diagonal drift along the geometric lattice period
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternPhase"
    )

    // Soft travelling ambient illumination sweeping across the lattice
    val glowPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternGlow"
    )

    val letterPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(26000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternLetterPhase"
    )

    val breathe by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(7200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "patternBreathe"
    )

    key(style) {
        Canvas(modifier = modifier.fillMaxSize()) {
            if (style == AtharBackgroundStyle.SOLID) return@Canvas
            if (size.width <= 0f || size.height <= 0f) return@Canvas

            val bleedX = cellPx
            val bleedY = cellPx * 2f
            val wall = baked.ensure(
                style = style,
                width = size.width,
                height = size.height,
                cellPx = cellPx,
                tint = tint,
                typeface = typeface,
                density = density,
                layoutDirection = layoutDirection,
                bleedX = bleedX,
                bleedY = bleedY
            ) ?: return@Canvas

            val driftX: Float
            val driftY: Float
            if (style == AtharBackgroundStyle.ISLAMIC) {
                if (animated) {
                    driftX = phase * cellPx
                    driftY = phase * (2f * cellPx)
                } else {
                    driftX = 0f
                    driftY = 0f
                }
            } else {
                if (animated) {
                    val radians = letterPhase * PI.toFloat() / 180f
                    driftX = cos(radians) * cellPx * 0.12f
                    driftY = sin(radians) * cellPx * 0.18f
                } else {
                    driftX = 0f
                    driftY = 0f
                }
            }

            val currentBreathe = if (animated) breathe else 1f
            val ink = (alpha * currentBreathe / BAKE_ALPHA).coerceIn(0f, 1f)
            wall.alpha = ink

            translate(left = driftX - bleedX, top = driftY - bleedY) {
                drawLayer(wall)
            }

            // Travelling ambient light sweeping across the interconnected geometric lines
            if (style == AtharBackgroundStyle.ISLAMIC && animated) {
                val sweepX = (0.5f + 0.38f * sin(glowPhase)) * size.width
                val sweepY = (0.34f + 0.20f * cos(glowPhase * 0.7f)) * size.height
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(tint.copy(alpha = alpha * 0.32f), Color.Transparent),
                        center = Offset(sweepX, sweepY),
                        radius = size.width * 0.65f
                    ),
                    blendMode = BlendMode.Plus
                )
            }
        }
    }
}

private const val BAKE_ALPHA = 0.42f

private class BakedBackdrop {
    var layer: GraphicsLayer? = null

    private var style: AtharBackgroundStyle? = null
    private var width = -1f
    private var height = -1f
    private var cellPx = -1f
    private var tint: Color = Color.Unspecified

    fun ensure(
        style: AtharBackgroundStyle,
        width: Float,
        height: Float,
        cellPx: Float,
        tint: Color,
        typeface: android.graphics.Typeface?,
        density: androidx.compose.ui.unit.Density,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        bleedX: Float,
        bleedY: Float
    ): GraphicsLayer? {
        val target = layer ?: return null
        val unchanged = this.style == style &&
            this.cellPx == cellPx &&
            this.tint == tint &&
            this.width == width &&
            this.height == height
        if (unchanged) return target

        val recordedWidth = (width + bleedX * 2f).toInt().coerceAtLeast(1)
        val recordedHeight = (height + bleedY * 2f).toInt().coerceAtLeast(1)
        target.record(
            density = density,
            layoutDirection = layoutDirection,
            size = IntSize(recordedWidth, recordedHeight)
        ) {
            translate(left = bleedX, top = bleedY) {
                when (style) {
                    AtharBackgroundStyle.ISLAMIC ->
                        drawArabesqueLattice(tint, BAKE_ALPHA, cellPx)
                    AtharBackgroundStyle.LETTERS ->
                        drawHurufWeave(tint, BAKE_ALPHA, 0f, typeface, cellPx)
                    AtharBackgroundStyle.SOLID -> Unit
                }
            }
        }

        this.style = style
        this.width = width
        this.height = height
        this.cellPx = cellPx
        this.tint = tint
        return target
    }
}

// ─── Connected Islamic Geometric Lines ─────────────────────────

/**
 * Draws the Arabesque geometric star & strapwork mosaic with interconnected
 * lines and smooth corner gradient falloff.
 */
internal fun DrawScope.drawArabesqueLattice(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float
) {
    if (size.width <= 0f || size.height <= 0f || cellPx <= 0f) return

    val w = size.width
    val h = size.height
    val cols = ceil(w / cellPx).toInt() + 3
    val rows = ceil(h / cellPx).toInt() + 3

    for (r in -2..rows) {
        for (c in -2..cols) {
            val cx = c * cellPx + (if (r % 2 == 0) 0f else cellPx / 2f)
            val cy = r * cellPx

            // Gradient falloff: richer density on top corner, fading smoothly towards base
            val diagonalFactor = ((cx / w) * 0.65f + (1f - (cy / h)) * 0.35f).coerceIn(0f, 1f)
            val localAlpha = baseAlpha * (diagonalFactor * diagonalFactor * 1.35f).coerceIn(0.01f, 1f)

            if (localAlpha > 0.004f) {
                val strokeColor = tint.copy(alpha = localAlpha * 0.76f)
                val highlightColor = tint.copy(alpha = localAlpha * 0.44f)

                // 1. Central 8-pointed star rosette
                drawEightPointedStar(Offset(cx, cy), cellPx * 0.38f, strokeColor, highlightColor)

                // 2. Interlocking diagonal strapwork ribbons connecting nodes
                drawInterlockingRibbons(Offset(cx, cy), cellPx, strokeColor)

                // 3. Intermediate diamonds between stars
                drawCornerDiamonds(Offset(cx + cellPx / 2f, cy), cellPx * 0.11f, strokeColor)
                drawCornerDiamonds(Offset(cx, cy + cellPx / 2f), cellPx * 0.11f, strokeColor)
            }
        }
    }
}

private fun DrawScope.drawEightPointedStar(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    highlightColor: Color
) {
    val strokeWidth = (radius * 0.046f).coerceIn(1.2f, 2.2f)
    val halfSide = radius * 0.7071f

    // Soft embossed inner radiance
    drawCircle(
        color = primaryColor.copy(alpha = primaryColor.alpha * 0.12f),
        radius = radius * 0.72f,
        center = center
    )

    // Two overlapping squares rotated by 45 degrees forming the classic Khatam star
    for (deg in listOf(0f, 45f)) {
        rotate(degrees = deg, pivot = center) {
            val corners = listOf(
                Offset(center.x - halfSide, center.y - halfSide),
                Offset(center.x + halfSide, center.y - halfSide),
                Offset(center.x + halfSide, center.y + halfSide),
                Offset(center.x - halfSide, center.y + halfSide)
            )
            for (i in corners.indices) {
                drawLine(
                    color = primaryColor,
                    start = corners[i],
                    end = corners[(i + 1) % 4],
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }

    // Concentric inner micro-octagon
    val innerR = radius * 0.34f
    val octPts = (0 until 8).map { i ->
        val rad = Math.toRadians(i * 45.0 + 22.5)
        Offset(
            center.x + (innerR * cos(rad)).toFloat(),
            center.y + (innerR * sin(rad)).toFloat()
        )
    }
    for (i in octPts.indices) {
        drawLine(
            color = highlightColor,
            start = octPts[i],
            end = octPts[(i + 1) % 8],
            strokeWidth = strokeWidth * 0.85f,
            cap = StrokeCap.Round
        )
    }

    // Central jewel bead
    drawCircle(
        color = primaryColor,
        radius = (radius * 0.055f).coerceAtLeast(1.5f),
        center = center
    )
}

private fun DrawScope.drawInterlockingRibbons(
    center: Offset,
    cellPx: Float,
    color: Color
) {
    val r = cellPx * 0.38f
    val stroke = (cellPx * 0.015f).coerceIn(1.1f, 2.0f)
    // 8 outward radiating ribbon lines reaching toward adjacent star points
    for (i in 0 until 8) {
        val angle = Math.toRadians(i * 45.0)
        val p1 = Offset(
            center.x + (r * 0.82f * cos(angle)).toFloat(),
            center.y + (r * 0.82f * sin(angle)).toFloat()
        )
        val p2 = Offset(
            center.x + (cellPx * 0.50f * cos(angle)).toFloat(),
            center.y + (cellPx * 0.50f * sin(angle)).toFloat()
        )
        drawLine(
            color = color,
            start = p1,
            end = p2,
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawCornerDiamonds(
    center: Offset,
    size: Float,
    color: Color
) {
    val stroke = (size * 0.28f).coerceIn(1.0f, 1.8f)

    // Subtle soft center glow
    drawCircle(
        color = color.copy(alpha = color.alpha * 0.10f),
        radius = size * 0.8f,
        center = center
    )

    rotate(degrees = 45f, pivot = center) {
        drawRect(
            color = color,
            topLeft = Offset(center.x - size, center.y - size),
            size = Size(size * 2, size * 2),
            style = Stroke(width = stroke)
        )
    }

    // Tiny center bead
    drawCircle(
        color = color,
        radius = (size * 0.18f).coerceAtLeast(1f),
        center = center
    )
}

// Backwards-compatible aliases so any existing callers continue working seamlessly
internal fun DrawScope.drawGirihWall(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    cache: Any? = null
) = drawArabesqueLattice(tint, baseAlpha, cellPx)

internal fun DrawScope.drawKhatamWall(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    cache: Any? = null
) = drawArabesqueLattice(tint, baseAlpha, cellPx)

internal class GirihCache {
    fun motifFor(cellPx: Float): Any = this
}
internal typealias KhatamCache = GirihCache

/** Convenience wrapper: opaque dark base + modern Islamic pattern behind [content]. */
@Composable
fun PatternScaffold(
    modifier: Modifier = Modifier,
    patternAlpha: Float = 0.12f,
    animated: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        IslamicPatternBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = patternAlpha,
            animated = animated
        )
        content()
    }
}

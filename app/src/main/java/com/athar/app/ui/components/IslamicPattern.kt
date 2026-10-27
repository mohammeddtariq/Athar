package com.athar.app.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.athar.app.ui.theme.AtharBackdropTint
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharBackgroundState
import com.athar.app.ui.theme.AtharBackgroundStyle
import kotlin.math.cos
import kotlin.math.sin

/**
 * The app-wide backdrop.
 *
 * Draws whichever [AtharBackgroundStyle] is currently selected — either the
 * geometric star lattice or the Arabic letter weave. Both styles are alive:
 * the lattice drifts and shimmers while the weave scrolls. They share the
 * same hairline ink and diagonal fade so content on top always stays readable,
 * and every surface in the app (screens, dialog scrims, the last-read card)
 * goes through here, so one switch repaints all of them.
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    tint: Color = AtharBackdropTint,
    alpha: Float = 0.12f,
    animated: Boolean = false,
    cellDp: Float = 72f
) {
    val style = AtharBackgroundState.current

    // A solid surface needs no ink at all.
    if (style == AtharBackgroundStyle.SOLID) return

    val density = LocalDensity.current
    val cellPx = with(density) { cellDp.dp.toPx() }
    val typeface = rememberHurufTypeface()

    val transition = rememberInfiniteTransition(label = "patternDrift")

    // The lattice travels one full cell per loop, so the restart lands on an
    // identical frame and the drift reads as continuous motion.
    val latticePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternLatticePhase"
    )
    val letterPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(38000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternLetterPhase"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "patternBreathe"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawBackdrop(
            style = style,
            tint = tint,
            alpha = alpha * breathe,
            cellPx = cellPx,
            letterPhase = letterPhase,
            latticePhase = latticePhase,
            typeface = typeface
        )
    }
}

/** Routes to the selected backdrop. */
private fun DrawScope.drawBackdrop(
    style: AtharBackgroundStyle,
    tint: Color,
    alpha: Float,
    cellPx: Float,
    letterPhase: Float,
    latticePhase: Float,
    typeface: android.graphics.Typeface?
) {
    when (style) {
        AtharBackgroundStyle.ISLAMIC ->
            drawArabesqueLattice(tint, alpha, cellPx, latticePhase)
        AtharBackgroundStyle.LETTERS ->
            drawHurufWeave(tint, alpha, letterPhase, typeface, cellPx)
        AtharBackgroundStyle.SOLID -> Unit
    }
}

/**
 * Draws the Arabesque geometric star & strapwork mosaic with a smooth gradient fade
 * from top-right to bottom-left.
 */
internal fun DrawScope.drawArabesqueLattice(tint: Color, baseAlpha: Float, cellPx: Float, phase: Float) {
    val w = size.width
    val h = size.height
    val cols = (w / cellPx).toInt() + 3
    val rows = (h / cellPx).toInt() + 3

    // Horizontal travel only: one cell per loop, starting and ending on the
    // same frame.
    val driftX = phase * cellPx

    for (r in -1..rows) {
        for (c in -1..cols) {
            val cx = c * cellPx + (if (r % 2 == 0) 0f else cellPx / 2f) - driftX
            val cy = r * cellPx

            // Gradient falloff is anchored to the screen; the glint pattern
            // travels with the lattice so the loop restart is invisible.
            val diagonalFactor = ((cx / w) * 0.7f + (1f - (cy / h)) * 0.3f).coerceIn(0f, 1f)
            val shimmer = 0.72f + 0.28f * sin(
                (cx / cellPx) * 1.9f + (cy / cellPx) * 1.3f
            )
            // The lattice is drawn with hairline ink, so it carries a touch more
            // weight than a plain line grid to stay present behind content. A
            // faint floor keeps the far corner from vanishing completely.
            val localAlpha = baseAlpha *
                (0.32f + diagonalFactor * diagonalFactor * 1.58f) * shimmer

            if (localAlpha > 0.005f) {
                val strokeColor = tint.copy(alpha = localAlpha)
                val highlightColor = tint.copy(alpha = localAlpha * 0.5f)
                val starRadius = cellPx * 0.40f

                // 1. Central 8-pointed star rosette
                drawEightPointedStar(Offset(cx, cy), starRadius, strokeColor, highlightColor)

                // 2. Interlocking diagonal strapwork ribbons connecting nodes
                drawInterlockingRibbons(Offset(cx, cy), cellPx, strokeColor)

                // 3. Intermediate diamonds between stars
                drawCornerDiamonds(Offset(cx + cellPx / 2f, cy), cellPx * 0.12f, strokeColor)
                drawCornerDiamonds(Offset(cx, cy + cellPx / 2f), cellPx * 0.12f, strokeColor)
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
    val strokeWidth = (radius * 0.055f).coerceAtLeast(1.3f)
    val halfSide = radius * 0.707f

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
                drawLine(primaryColor, corners[i], corners[(i + 1) % 4], strokeWidth = strokeWidth)
            }
        }
    }

    // Concentric inner micro-octagon
    val innerR = radius * 0.32f
    val octPts = (0 until 8).map { i ->
        val rad = Math.toRadians((i * 45.0 + 22.5))
        Offset(
            center.x + (innerR * cos(rad)).toFloat(),
            center.y + (innerR * sin(rad)).toFloat()
        )
    }
    for (i in octPts.indices) {
        drawLine(highlightColor, octPts[i], octPts[(i + 1) % 8], strokeWidth = strokeWidth * 0.8f)
    }
}

private fun DrawScope.drawInterlockingRibbons(
    center: Offset,
    cellPx: Float,
    color: Color
) {
    val r = cellPx * 0.40f
    val stroke = (cellPx * 0.020f).coerceAtLeast(1.1f)
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
        drawLine(color, p1, p2, strokeWidth = stroke)
    }
}

private fun DrawScope.drawCornerDiamonds(
    center: Offset,
    size: Float,
    color: Color
) {
    val stroke = (size * 0.36f).coerceAtLeast(1.1f)
    rotate(degrees = 45f, pivot = center) {
        drawRect(
            color = color,
            topLeft = Offset(center.x - size, center.y - size),
            size = androidx.compose.ui.geometry.Size(size * 2, size * 2),
            style = Stroke(width = stroke)
        )
    }
}

/** Convenience wrapper: opaque dark base + modern Islamic pattern behind [content]. */
@Composable
fun PatternScaffold(
    modifier: Modifier = Modifier,
    patternAlpha: Float = 0.12f,
    animated: Boolean = false,
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


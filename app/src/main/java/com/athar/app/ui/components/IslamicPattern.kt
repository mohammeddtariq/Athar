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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharPrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern Islamic Geometric Lattice Pattern:
 * - 8-pointed star (khatam) rosettes with interlocking chevron/strapwork ribbons.
 * - Architectural embossed feel with delicate sage geometry.
 * - Smooth corner/edge gradient fade so text and cards remain effortlessly readable.
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    tint: Color = AtharPrimary,
    alpha: Float = 0.12f,
    animated: Boolean = false,
    cellDp: Float = 72f
) {
    if (!animated) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawArabesqueLattice(tint, alpha, cellPx = cellDp * density, phase = 0f)
        }
        return
    }

    val transition = rememberInfiniteTransition(label = "islamicPatternDrift")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternPhase"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "patternBreathe"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawArabesqueLattice(tint, alpha * breathe, cellPx = cellDp * density, phase = phase)
    }
}

/**
 * Draws the Arabesque geometric star & strapwork mosaic with a smooth gradient fade
 * from top-right to bottom-left.
 */
private fun DrawScope.drawArabesqueLattice(tint: Color, baseAlpha: Float, cellPx: Float, phase: Float) {
    val w = size.width
    val h = size.height
    val cols = (w / cellPx).toInt() + 3
    val rows = (h / cellPx).toInt() + 3

    // Very subtle drift
    val driftX = (phase / 360f) * cellPx * 0.15f
    val driftY = (phase / 360f) * cellPx * 0.10f

    for (r in -1..rows) {
        for (c in -1..cols) {
            val cx = c * cellPx + (if (r % 2 == 0) 0f else cellPx / 2f) - driftX
            val cy = r * cellPx - driftY

            // Calculate gradient falloff: dense on top-right (x -> w, y -> 0), fading towards bottom-left
            // Normalized position along the diagonal (from top-right = 1.0 to bottom-left = 0.0)
            val diagonalFactor = ((cx / w) * 0.7f + (1f - (cy / h)) * 0.3f).coerceIn(0f, 1f)
            // Smooth curve falloff
            val localAlpha = baseAlpha * (diagonalFactor * diagonalFactor * 1.2f).coerceIn(0.01f, 1f)

            if (localAlpha > 0.005f) {
                val strokeColor = tint.copy(alpha = localAlpha)
                val highlightColor = tint.copy(alpha = localAlpha * 0.45f)

                // 1. Central 8-pointed star rosette
                drawEightPointedStar(Offset(cx, cy), cellPx * 0.38f, strokeColor, highlightColor)

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
    val strokeWidth = (radius * 0.045f).coerceAtLeast(1.2f)
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
    val r = cellPx * 0.38f
    val stroke = (cellPx * 0.015f).coerceAtLeast(1f)
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
    val stroke = (size * 0.30f).coerceAtLeast(1f)
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


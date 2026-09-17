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
import androidx.compose.ui.graphics.drawscope.rotate
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharPrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Subtle Islamic eight-fold star lattice, drawn with Canvas so no asset
 * files are needed. Tone-matched to the olive theme; sits behind content.
 *
 * @param animated slow breathing drift so the background feels alive.
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    tint: Color = AtharPrimary,
    alpha: Float = 0.055f,
    animated: Boolean = true,
    cellDp: Float = 92f
) {
    if (!animated) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawLattice(tint.copy(alpha = alpha), cellPx = cellDp * density, phase = 0f)
        }
        return
    }
    val transition = rememberInfiniteTransition(label = "patternDrift")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(48000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        drawLattice(tint.copy(alpha = alpha * breathe), cellPx = cellDp * density, phase = phase)
    }
}

/** Repeating eight-pointed star (khatam) lattice. */
private fun DrawScope.drawLattice(color: Color, cellPx: Float, phase: Float) {
    val cols = (size.width / cellPx).toInt() + 3
    val rows = (size.height / cellPx).toInt() + 3
    // Slow diagonal drift derived from phase.
    val drift = (phase / 360f) * cellPx
    for (r in -1..rows) {
        for (c in -1..cols) {
            val cx = c * cellPx + (if (r % 2 == 0) 0f else cellPx / 2f) - drift * 0.25f
            val cy = r * cellPx - drift * 0.15f
            drawStar(Offset(cx, cy), cellPx * 0.42f, color)
        }
    }
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val stroke = (radius * 0.055f).coerceAtLeast(1f)
    // Khatam: two overlapping squares (0° and 45°) = eight-pointed star.
    for (rot in listOf(0f, 45f)) {
        rotate(degrees = rot, pivot = center) {
            val h = radius * 0.72f
            val corners = listOf(
                Offset(center.x - h, center.y - h),
                Offset(center.x + h, center.y - h),
                Offset(center.x + h, center.y + h),
                Offset(center.x - h, center.y + h)
            )
            for (i in corners.indices) {
                drawLine(color, corners[i], corners[(i + 1) % 4], strokeWidth = stroke)
            }
        }
    }
    // Outer linking diamond (echoes the reference lattice grid).
    rotate(degrees = 45f, pivot = center) {
        val h = radius * 1.02f
        val corners = listOf(
            Offset(center.x - h, center.y - h),
            Offset(center.x + h, center.y - h),
            Offset(center.x + h, center.y + h),
            Offset(center.x - h, center.y + h)
        )
        for (i in corners.indices) {
            drawLine(color, corners[i], corners[(i + 1) % 4], strokeWidth = stroke * 0.7f)
        }
    }
    // Inner octagon hint.
    val pts = (0 until 8).map { i ->
        val a = Math.toRadians((i * 45).toDouble())
        Offset(
            center.x + (radius * 0.30f * cos(a)).toFloat(),
            center.y + (radius * 0.30f * sin(a)).toFloat()
        )
    }
    for (i in pts.indices) {
        drawLine(color, pts[i], pts[(i + 1) % 8], strokeWidth = stroke * 0.8f)
    }
    drawCircle(color, radius = stroke * 0.9f, center = center)
}

/** Convenience wrapper: opaque dark base + pattern behind [content]. */
@Composable
fun PatternScaffold(
    modifier: Modifier = Modifier,
    patternAlpha: Float = 0.07f,
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

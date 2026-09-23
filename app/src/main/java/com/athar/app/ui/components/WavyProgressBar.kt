package com.athar.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import kotlin.math.PI
import kotlin.math.sin

/**
 * Aesthetic wavy progress bar component matching Athar's design system.
 * Features an animated sinusoidal wave for the downloaded portion, a solid
 * thumb at the front apex, and a dimmed linear track with an end dot.
 */
@Composable
fun WavyProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    isIndeterminate: Boolean = false,
    activeColor: Color = AtharPrimary,
    thumbColor: Color = AtharPrimaryLight,
    trackColor: Color = AtharCardBorder,
    endDotColor: Color = AtharCardBorder,
    strokeWidth: Dp = 3.5.dp,
    waveAmplitude: Dp = 4.5.dp,
    wavelength: Dp = 26.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WavyProgressTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavyProgressPhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f

        val strokePx = strokeWidth.toPx()
        val ampPx = waveAmplitude.toPx()
        val waveLenPx = wavelength.toPx()
        val thumbRadiusPx = strokePx * 1.5f
        val endDotRadiusPx = strokePx * 1.0f

        val clampedProgress = if (isIndeterminate) 1f else progress.coerceIn(0f, 1f)
        val activeWidth = (width * clampedProgress).coerceIn(0f, width)

        // 1. Draw inactive linear track and end dot if not fully finished
        if (activeWidth < width) {
            val trackStart = if (activeWidth > 0f) activeWidth + thumbRadiusPx else 0f
            val trackEnd = (width - endDotRadiusPx).coerceAtLeast(trackStart)

            if (trackEnd > trackStart) {
                drawLine(
                    color = trackColor,
                    start = Offset(trackStart, centerY),
                    end = Offset(trackEnd, centerY),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = endDotColor,
                radius = endDotRadiusPx,
                center = Offset(width - endDotRadiusPx, centerY)
            )
        }

        // 2. Draw active wavy path
        if (activeWidth > 0f) {
            val wavePath = Path()
            val stepPx = 3f // Smooth sampling interval
            var currentX = 0f

            val startY = centerY + sin(phase) * ampPx
            wavePath.moveTo(0f, startY)

            while (currentX <= activeWidth) {
                val waveAngle = ((currentX / waveLenPx) * (2 * PI) - phase).toFloat()
                val currentY = centerY + sin(waveAngle) * ampPx
                wavePath.lineTo(currentX, currentY)
                currentX += stepPx
            }

            drawPath(
                path = wavePath,
                color = activeColor,
                style = Stroke(
                    width = strokePx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 3. Draw rounded thumb indicator at the frontier
            if (!isIndeterminate && activeWidth < width) {
                val thumbAngle = ((activeWidth / waveLenPx) * (2 * PI) - phase).toFloat()
                val thumbY = centerY + sin(thumbAngle) * ampPx

                drawCircle(
                    color = thumbColor,
                    radius = thumbRadiusPx,
                    center = Offset(activeWidth, thumbY)
                )
            }
        }
    }
}

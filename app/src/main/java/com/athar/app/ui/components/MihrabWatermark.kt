package com.athar.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Pointed mihrab arch with a nested outline and an eight-pointed star at its heart,
 * used as the recurring ornament on prayer surfaces.
 */
@Composable
fun MihrabWatermark(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f

        val archW = radius * 1.30f
        val archH = radius * 1.60f
        val left = cx - archW / 2f
        val right = cx + archW / 2f
        val bottom = cy + archH / 2f
        val springY = cy + archH * 0.05f
        val apexY = cy - archH / 2f

        val outerArch = Path().apply {
            moveTo(left, bottom)
            lineTo(left, springY)
            cubicTo(left, springY - archH * 0.28f, cx - archW * 0.15f, apexY, cx, apexY)
            cubicTo(cx + archW * 0.15f, apexY, right, springY - archH * 0.28f, right, springY)
            lineTo(right, bottom)
            lineTo(left, bottom)
            close()
        }
        drawPath(outerArch, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

        val inset = 6.dp.toPx()
        val innerLeft = left + inset
        val innerRight = right - inset
        val innerBottom = bottom - inset
        val innerApexY = apexY + inset * 1.3f
        val innerArch = Path().apply {
            moveTo(innerLeft, innerBottom)
            lineTo(innerLeft, springY)
            cubicTo(innerLeft, springY - archH * 0.24f, cx - archW * 0.10f, innerApexY, cx, innerApexY)
            cubicTo(cx + archW * 0.10f, innerApexY, innerRight, springY - archH * 0.24f, innerRight, springY)
            lineTo(innerRight, innerBottom)
            lineTo(innerLeft, innerBottom)
            close()
        }
        drawPath(
            innerArch,
            color.copy(alpha = color.alpha * 0.70f),
            style = Stroke(width = 1.3.dp.toPx(), cap = StrokeCap.Round)
        )

        val starCy = cy + archH * 0.08f
        val starR = radius * 0.32f
        val starPath = Path()
        for (i in 0 until 16) {
            val r = if (i % 2 == 0) starR else starR * 0.50f
            val angle = Math.toRadians(i * 22.5 - 90.0)
            val px = (cx + r * kotlin.math.cos(angle)).toFloat()
            val py = (starCy + r * kotlin.math.sin(angle)).toFloat()
            if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
        }
        starPath.close()
        drawPath(starPath, color)
        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(cx, starCy))
    }
}

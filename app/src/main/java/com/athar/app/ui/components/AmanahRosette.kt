package com.athar.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Scalloped rosette lifted from the Amanah collection artwork: a cloud-edged ring,
 * an inner rule, and an eight-pointed star around a solid seed.
 */
@Composable
fun AmanahRosette(
    modifier: Modifier = Modifier,
    color: Color,
    strokeWidth: Dp = 1.4.dp
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val stroke = strokeWidth.toPx()
        val center = this.center

        val scallops = 18
        val step = (2.0 * Math.PI / scallops).toFloat()
        val bump = radius * 0.085f
        val ringRadius = radius * 0.90f

        val ring = Path()
        for (i in 0 until scallops) {
            val a0 = i * step
            val a1 = (i + 1) * step
            val p0 = Offset(
                center.x + ringRadius * cos(a0),
                center.y + ringRadius * sin(a0)
            )
            val p1 = Offset(
                center.x + ringRadius * cos(a1),
                center.y + ringRadius * sin(a1)
            )
            val c0 = Offset(
                center.x + (ringRadius + bump) * cos(a0 + step * 0.25f),
                center.y + (ringRadius + bump) * sin(a0 + step * 0.25f)
            )
            val c1 = Offset(
                center.x + (ringRadius + bump) * cos(a0 + step * 0.75f),
                center.y + (ringRadius + bump) * sin(a0 + step * 0.75f)
            )
            if (i == 0) ring.moveTo(p0.x, p0.y) else ring.lineTo(p0.x, p0.y)
            ring.cubicTo(c0.x, c0.y, c1.x, c1.y, p1.x, p1.y)
        }
        ring.close()
        drawPath(ring, color = color, style = Stroke(width = stroke))

        drawCircle(
            color = color,
            radius = radius * 0.70f,
            center = center,
            style = Stroke(width = stroke * 0.9f)
        )

        val star = Path()
        val points = 8
        val outer = radius * 0.53f
        val inner = radius * 0.27f
        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) outer else inner
            val angle = Math.PI * i / points - Math.PI / 2.0
            val x = center.x + (r * cos(angle)).toFloat()
            val y = center.y + (r * sin(angle)).toFloat()
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        rotate(degrees = 22.5f, pivot = center) {
            drawPath(star, color = color, style = Stroke(width = stroke * 1.15f))
        }

        drawCircle(color = color, radius = radius * 0.05f, center = center)
    }
}

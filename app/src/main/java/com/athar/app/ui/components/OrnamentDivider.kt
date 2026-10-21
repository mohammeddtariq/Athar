package com.athar.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.athar.app.ui.theme.AtharCardBorder

/** Hairline rule that fades in from both sides around a small diamond rosette. */
@Composable
fun OrnamentDivider(
    modifier: Modifier = Modifier,
    color: Color = AtharCardBorder,
    ornamentColor: Color = color,
    thickness: androidx.compose.ui.unit.Dp = 1.dp,
    ornamentSize: androidx.compose.ui.unit.Dp = 9.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(
            Modifier
                .weight(1f)
                .height(thickness)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, color)))
        )
        Spacer(Modifier.size(7.dp))
        Canvas(Modifier.size(ornamentSize)) {
            val half = size.minDimension / 2f
            rotate(45f) {
                drawRect(
                    color = ornamentColor,
                    topLeft = Offset(center.x - half * 0.72f, center.y - half * 0.72f),
                    size = Size(half * 1.44f, half * 1.44f),
                    style = Stroke(thickness.toPx())
                )
            }
        }
        Spacer(Modifier.size(7.dp))
        Spacer(
            Modifier
                .weight(1f)
                .height(thickness)
                .background(Brush.horizontalGradient(listOf(color, Color.Transparent)))
        )
    }
}

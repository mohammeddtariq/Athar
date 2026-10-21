package com.athar.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-bleed congratulation overlay shown once every dua in the current collection is counted.
 * Shared by the after-prayer dialog on Home and the Duas screen.
 */
@Composable
fun DuaCompletionCelebration(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtharBackground),
        contentAlignment = Alignment.Center
    ) {
        CelebrationSparkles(modifier = Modifier.matchParentSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(AtharPrimary.copy(alpha = 0.16f))
                    .border(1.5.dp, AtharPrimaryLight.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = title,
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                color = AtharTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = body,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(26.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(AtharPrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_alhamdulillah),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = AtharTextOnPrimary
                )
            }
        }
    }
}

@Composable
private fun CelebrationSparkles(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "completionSparkles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparklePhase"
    )

    val gold = Color(0xFFE6D79A)

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height * 0.42f)
        val unit = size.minDimension

        repeat(20) { i ->
            val seed = (i * 0.61803398f) % 1f
            val progress = (phase + seed) % 1f
            val direction = if (i % 2 == 0) 1f else -1f
            val angle = seed * 6.2831855f + progress * 0.9f * direction
            val radius = unit * (0.16f + (i % 5) * 0.055f) + progress * unit * 0.08f
            val x = center.x + cos(angle) * radius
            val y = center.y + sin(angle) * radius * 0.62f
            val alpha = ((1f - progress) * 0.85f).coerceIn(0f, 1f)
            val color = if (i % 3 == 0) gold else AtharPrimaryLight
            val length = 3.5f + (i % 4) * 1.6f
            val sparkleCenter = Offset(x, y)

            drawCircle(
                color = color,
                radius = 1.6f + (i % 3),
                center = sparkleCenter,
                alpha = alpha
            )
            drawLine(
                color = color,
                start = Offset(x - length, y),
                end = Offset(x + length, y),
                strokeWidth = 1.1f,
                alpha = alpha
            )
            drawLine(
                color = color,
                start = Offset(x, y - length),
                end = Offset(x, y + length),
                strokeWidth = 1.1f,
                alpha = alpha
            )
        }
    }
}

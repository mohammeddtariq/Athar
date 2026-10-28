package com.athar.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.delay

/**
 * The wordmark, breathing.
 *
 * It alternates between the wide-set English mark and the calligraphic Arabic
 * one, but the two never slide past each other: the outgoing mark settles back
 * while the incoming one blooms from just under full size, so the header keeps a
 * single steady baseline. A very slow opacity bloom underneath keeps the mark
 * alive while it waits.
 *
 * The Arabic string is deliberately written plain — `أَثَر` — matching the mark
 * used across the app. Stretching it with tatweel distorted the final ر in the
 * Naskh face, which is why it no longer does.
 */
@Composable
fun AtharAnimatedLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary
) {
    var isArabic by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4200)
            isArabic = !isArabic
        }
    }

    val bloom by rememberInfiniteTransition(label = "logoBloom").animateFloat(
        initialValue = 0.88f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBloomAlpha"
    )
    val ink by animateColorAsState(
        targetValue = color,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "logoInk"
    )

    Box(
        modifier = modifier.height(44.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = isArabic,
            transitionSpec = {
                (
                    fadeIn(tween(460, easing = FastOutSlowInEasing)) +
                        scaleIn(
                            initialScale = 0.93f,
                            animationSpec = tween(520, easing = FastOutSlowInEasing)
                        )
                    ).togetherWith(
                    fadeOut(tween(300, easing = FastOutSlowInEasing)) +
                        scaleOut(
                            targetScale = 1.05f,
                            animationSpec = tween(360, easing = FastOutSlowInEasing)
                        )
                )
            },
            label = "AtharLogoTransition"
        ) { arabic ->
            if (arabic) {
                ArabicMark(ink = ink, alpha = bloom)
            } else {
                EnglishMark(ink = ink, alpha = bloom)
            }
        }
    }
}

@Composable
private fun ArabicMark(ink: Color, alpha: Float) {
    Text(
        text = "أَثَر",
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Black,
        fontSize = 31.sp,
        lineHeight = 40.sp,
        color = ink,
        maxLines = 1,
        modifier = Modifier
            .padding(top = 2.dp)
            .graphicsLayer { this.alpha = alpha }
    )
}

@Composable
private fun EnglishMark(ink: Color, alpha: Float) {
    Text(
        text = "A T H A R",
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        lineHeight = 40.sp,
        letterSpacing = 4.sp,
        color = ink,
        maxLines = 1,
        modifier = Modifier.graphicsLayer { this.alpha = alpha }
    )
}

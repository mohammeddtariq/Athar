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
 *
 * @param intervalMillis how long each script holds before the cross-fade; pass
 *   a shorter beat for the opening splash, where the two alternate quickly.
 * @param startArabic begin on the Arabic mark instead of the English one.
 * @param textScale scales the whole mark, so a hero-size version stays crisp
 *   instead of being a scaled-up raster of the header size.
 */
@Composable
fun AtharAnimatedLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary,
    intervalMillis: Long = 4200L,
    startArabic: Boolean = false,
    textScale: Float = 1f
) {
    var isArabic by remember { mutableStateOf(startArabic) }

    LaunchedEffect(intervalMillis) {
        if (intervalMillis <= 0L) return@LaunchedEffect
        while (true) {
            delay(intervalMillis)
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
        modifier = modifier.height(44.dp * textScale),
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
                ArabicMark(ink = ink, alpha = bloom, scale = textScale)
            } else {
                EnglishMark(ink = ink, alpha = bloom, scale = textScale)
            }
        }
    }
}

@Composable
private fun ArabicMark(ink: Color, alpha: Float, scale: Float) {
    Text(
        text = "أَثَر",
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp * scale,
        lineHeight = 44.sp * scale,
        letterSpacing = 1.sp,
        color = ink.copy(alpha = ink.alpha * alpha),
        maxLines = 1,
        softWrap = false,
        modifier = Modifier.padding(top = 4.dp * scale, bottom = 2.dp * scale)
    )
}

@Composable
private fun EnglishMark(ink: Color, alpha: Float, scale: Float) {
    Text(
        text = "A T H A R",
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp * scale,
        lineHeight = 44.sp * scale,
        letterSpacing = 4.sp * scale,
        color = ink.copy(alpha = ink.alpha * alpha),
        maxLines = 1,
        softWrap = false,
        modifier = Modifier.padding(vertical = 4.dp * scale)
    )
}

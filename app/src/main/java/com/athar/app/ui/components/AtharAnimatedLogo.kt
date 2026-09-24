package com.athar.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.delay

/**
 * Animated app logo alternating between wide-spaced English ("A T H A R")
 * and calligraphic Arabic with spreading kashida ("أَثَـــــر").
 */
@Composable
fun AtharAnimatedLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary
) {
    var isArabic by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3600)
            isArabic = !isArabic
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = isArabic,
            transitionSpec = {
                (fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                    slideInVertically(animationSpec = spring(dampingRatio = 0.82f, stiffness = 320f)) { -it / 3 })
                    .togetherWith(
                        fadeOut(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                            slideOutVertically(animationSpec = tween(280, easing = FastOutSlowInEasing)) { it / 3 }
                    )
            },
            label = "AtharLogoTransition"
        ) { arabic ->
            if (arabic) {
                AnimatedArabicLogo(color = color)
            } else {
                AnimatedEnglishLogo(color = color)
            }
        }
    }
}

@Composable
private fun AnimatedArabicLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary
) {
    var kashidaCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        delay(80)
        kashidaCount = 2
        delay(120)
        kashidaCount = 3
        delay(140)
        kashidaCount = 4
        delay(160)
        kashidaCount = 5
    }

    val tatweels = "ـ".repeat(kashidaCount)
    val text = "أَثَ${tatweels}ر"

    Text(
        text = text,
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 1.sp,
        color = color,
        modifier = modifier
    )
}

@Composable
private fun AnimatedEnglishLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary
) {
    val letters = remember { listOf("A", "T", "H", "A", "R") }
    var visibleIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        letters.indices.forEach { i ->
            delay(70)
            visibleIndex = i + 1
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        letters.forEachIndexed { index, char ->
            val isVisible = index < visibleIndex
            val alpha by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
                label = "charAlpha_$index"
            )
            val offsetY by animateFloatAsState(
                targetValue = if (isVisible) 0f else 6f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 360f),
                label = "charOffset_$index"
            )

            Text(
                text = char,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = color,
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .alpha(alpha)
            )
        }
    }
}

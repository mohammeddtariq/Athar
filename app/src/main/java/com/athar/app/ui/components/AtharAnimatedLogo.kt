package com.athar.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
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
 * Animated app logo alternating smoothly between wide-spaced English ("A T H A R")
 * and calligraphic Arabic ("أَثَـــــر") with full unclipped diacritics.
 */
@Composable
fun AtharAnimatedLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary
) {
    var isArabic by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3800)
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
    Text(
        text = "أَثَـــــر",
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 44.sp,
        letterSpacing = 1.sp,
        color = color,
        maxLines = 1,
        modifier = modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun AnimatedEnglishLogo(
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary
) {
    Text(
        text = "A T H A R",
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp,
        lineHeight = 44.sp,
        letterSpacing = 4.sp,
        color = color,
        maxLines = 1,
        modifier = modifier.padding(vertical = 4.dp)
    )
}

package com.athar.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.delay

/**
 * Streaming (typewriter) text effect: reveals [text] character by character
 * with a blinking cursor. Cheap — a single Text recomposing on a timer.
 */
@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight = FontWeight.Medium,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    color: Color = Color.White,
    textAlign: TextAlign = TextAlign.Center,
    charDelayMs: Long = 34L
) {
    var visible by remember(text) { mutableIntStateOf(0) }
    LaunchedEffect(text) {
        visible = 0
        for (i in text.indices) {
            delay(charDelayMs)
            visible = i + 1
        }
        // Hold the full line, then keep the cursor blinking.
        delay(1200)
    }

    val blink = rememberInfiniteTransition(label = "cursorBlink")
    val cursorAlpha by blink.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(530, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor"
    )
    val done = visible >= text.length
    val cursor = if (!done || cursorAlpha > 0.35f) "▍" else " "

    Text(
        text = text.take(visible) + cursor,
        modifier = modifier,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = color,
        textAlign = textAlign
    )
}

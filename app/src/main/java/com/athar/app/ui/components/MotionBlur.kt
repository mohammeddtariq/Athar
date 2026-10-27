package com.athar.app.ui.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Tracks how fast the page is travelling.
 *
 * [onScroll] is fed by the nested-scroll connection; a frame loop bleeds the
 * value back to zero as soon as the finger (or fling) stops, so the effect only
 * lives for the duration of the movement.
 */
class MotionBlurState internal constructor(private val scope: CoroutineScope) {

    var strength by mutableFloatStateOf(0f)
        private set

    private var decaying = false

    fun onScroll(deltaY: Float) {
        val bumped = strength + abs(deltaY) / 320f
        strength = bumped.coerceAtMost(1f)
        if (bumped > 0.01f && !decaying) {
            decaying = true
            scope.launch { decay() }
        }
    }

    private suspend fun decay() {
        while (strength > 0f) {
            withFrameNanos { }
            strength = (strength - 0.055f).coerceAtLeast(0f)
        }
        decaying = false
    }
}

@Composable
fun rememberMotionBlurState(): MotionBlurState {
    val scope = rememberCoroutineScope()
    return remember(scope) { MotionBlurState(scope) }
}

/**
 * Softens the content as it slides under the status bar and the navigation
 * dock. The page itself stays sharp in the middle: only the top and bottom
 * bands are replaced by a blurred copy, fading out towards the centre.
 */
@Composable
fun Modifier.edgeMotionBlur(
    state: MotionBlurState,
    enabled: Boolean = true,
    topBand: Dp = 96.dp,
    bottomBand: Dp = 124.dp,
    maxBlur: Dp = 16.dp
): Modifier {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this

    val layer = rememberGraphicsLayer()

    return drawWithContent {
        drawContent()

        val strength = state.strength
        if (strength <= 0.02f) return@drawWithContent

        layer.record { this@drawWithContent.drawContent() }
        val radius = strength * maxBlur.toPx()
        layer.renderEffect = BlurEffect(radius, radius, TileMode.Clamp)

        val height = size.height
        if (height <= 0f) return@drawWithContent

        val topStop = (topBand.toPx() / height).coerceIn(0.05f, 0.45f)
        val bottomStop = 1f - (bottomBand.toPx() / height).coerceIn(0.05f, 0.45f)
        val mask = if (topStop < bottomStop) {
            Brush.verticalGradient(
                0f to Color.Black,
                topStop to Color.Transparent,
                bottomStop to Color.Transparent,
                1f to Color.Black
            )
        } else {
            Brush.verticalGradient(
                0f to Color.Black,
                0.5f to Color.Transparent,
                1f to Color.Black
            )
        }

        val canvas = drawContext.canvas
        canvas.saveLayer(Rect(Offset.Zero, size), Paint())
        drawLayer(layer)
        drawRect(brush = mask, blendMode = BlendMode.DstIn)
        canvas.restore()
    }
}

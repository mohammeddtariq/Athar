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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.layer.GraphicsLayer
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
        // Sub-pixel jitters are not motion; ignoring them keeps the layer from
        // being recorded for a scroll that barely happened.
        if (abs(deltaY) < 2f) return
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
            strength = (strength - 0.085f).coerceAtLeast(0f)
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
 *
 * Each band is composited on its own, so the layer is only as tall as the band
 * rather than the whole screen — and no full-screen mask pass is needed. That
 * is what keeps a fast fling from stuttering.
 */
@Composable
fun Modifier.edgeMotionBlur(
    state: MotionBlurState,
    enabled: Boolean = true,
    topBand: Dp = 92.dp,
    bottomBand: Dp = 112.dp,
    maxBlur: Dp = 10.dp
): Modifier {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this

    val layer = rememberGraphicsLayer()

    return drawWithContent {
        drawContent()

        val strength = state.strength
        if (strength <= 0.06f) return@drawWithContent

        layer.record { this@drawWithContent.drawContent() }
        val radius = (strength * maxBlur.toPx()).coerceAtLeast(0.5f)
        layer.renderEffect = BlurEffect(radius, radius, TileMode.Clamp)

        val height = size.height
        if (height <= 0f) return@drawWithContent

        val topPx = topBand.toPx().coerceIn(0f, height * 0.32f)
        val bottomPx = bottomBand.toPx().coerceIn(0f, height * 0.32f)

        blurBand(layer, top = 0f, bottom = topPx, fadeInFromTop = true)
        blurBand(layer, top = height - bottomPx, bottom = height, fadeInFromTop = false)
    }
}

/**
 * Draws the blurred layer inside one band only, masked so it dissolves towards
 * the middle of the screen instead of ending on a hard line.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.blurBand(
    layer: GraphicsLayer,
    top: Float,
    bottom: Float,
    fadeInFromTop: Boolean
) {
    val bandHeight = bottom - top
    if (bandHeight <= 0f) return

    val canvas = drawContext.canvas
    canvas.saveLayer(Rect(0f, top, size.width, bottom), Paint())

    drawLayer(layer)

    val mask = if (fadeInFromTop) {
        Brush.verticalGradient(
            0f to Color.Black,
            1f to Color.Transparent,
            startY = top,
            endY = bottom
        )
    } else {
        Brush.verticalGradient(
            0f to Color.Transparent,
            1f to Color.Black,
            startY = top,
            endY = bottom
        )
    }
    drawRect(
        brush = mask,
        topLeft = Offset(0f, top),
        size = Size(size.width, bandHeight),
        blendMode = BlendMode.DstIn
    )

    canvas.restore()
}

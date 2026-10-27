package com.athar.app.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.athar.app.R
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════
// Huruf Weave — the calligraphic backdrop
//
// Instead of scattering synthetic letter outlines, the backdrop is
// inked from a real manuscript glyph: the bismillah ligature drawn
// in the app's own calligraphic face. It appears as a dense, softly
// rotated band across the screen, with two ghosted ligatures above
// and below it. The result reads as a page margin from a manuscript
// rather than a texture of symbols.
// ══════════════════════════════════════════════════════════════

/** U+FDFD — the single-glyph bismillah ligature in `bismillah.ttf`. */
private const val BISMILLAH_GLYPH = "\uFDFD"

/**
 * Loads the manuscript face once. The bundled font contains the full
 * bismillah ligature, so the backdrop never depends on a system font.
 */
@Composable
internal fun rememberCalligraphyTypeface(): Typeface? {
    val context = LocalContext.current
    return remember(context) {
        ResourcesCompat.getFont(context, R.font.bismillah)
    }
}

/**
 * Draws the calligraphic field.
 *
 * The band sits slightly above centre and repeats with a half-step offset,
 * so it feels hand-set rather than tiled. Its ink is kept to a hairline of
 * the accent colour; the ghost ligatures are stroked rather than filled so
 * they dissolve into the background instead of competing with the content.
 */
internal fun DrawScope.drawArabicCalligraphy(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    phase: Float,
    typeface: Typeface?
) {
    if (typeface == null || size.width <= 0f || size.height <= 0f) return

    val w = size.width
    val h = size.height
    val drift = (phase / 360f) * cellPx * 0.24f
    val ink = tint.toArgb()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        color = ink
        isDither = true
    }

    // ── Ghosted ligatures above and below the band ──
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = (cellPx * 0.010f).coerceAtLeast(0.7f)
    paint.textSize = cellPx * 1.72f
    paint.alpha = (255 * baseAlpha * 0.36f).toInt().coerceIn(0, 255)
    val ghostStep = paint.measureText(BISMILLAH_GLYPH) * 0.68f
    listOf(0.17f to -4.5f, 0.80f to 3.5f).forEachIndexed { index, (fraction, angle) ->
        var x = -ghostStep * (0.35f + index * 0.22f) - drift * 0.35f
        val y = h * fraction + sin(index * 1.7f) * cellPx * 0.04f
        while (x < w + ghostStep) {
            drawRotated(paint, BISMILLAH_GLYPH, x, y, angle)
            x += ghostStep
        }
    }

    // ── Dense manuscript band ──
    paint.style = Paint.Style.FILL
    paint.textSize = cellPx * 0.46f
    val bandStep = paint.measureText(BISMILLAH_GLYPH) * 0.90f

    repeat(2) { row ->
        paint.alpha = (255 * baseAlpha * if (row == 0) 1.28f else 0.82f)
            .toInt().coerceIn(0, 255)
        val y = h * 0.455f + row * cellPx * 0.46f
        var x = -bandStep * (0.55f + row * 0.38f) - drift
        while (x < w + bandStep) {
            drawRotated(
                paint = paint,
                text = BISMILLAH_GLYPH,
                x = x,
                y = y,
                angle = if (row == 0) -2.4f else 2.0f
            )
            x += bandStep
        }
    }
}

/** Saves/rotates the native canvas for one glyph, then restores it. */
private fun DrawScope.drawRotated(
    paint: Paint,
    text: String,
    x: Float,
    y: Float,
    angle: Float
) {
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val checkpoint = native.save()
        native.rotate(angle, x, y)
        native.drawText(text, x, y, paint)
        native.restoreToCount(checkpoint)
    }
}

package com.athar.app.ui.components

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.athar.app.R
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Huruf field: a dense wall of single, isolated Naskh letterforms. Each cell
// holds exactly one codepoint, so nothing joins into a word — the glyphs read
// as the loose calligraphic collage the backdrop is meant to be.

/** The letters used, each as its own standalone codepoint. */
private val huruf = listOf(
    "ا", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س",
    "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م",
    "ن", "ه", "و", "ي", "أ", "إ", "آ", "ء", "ؤ", "ئ", "ة"
)

/** Loads the Naskh face the letters are drawn with. */
@Composable
internal fun rememberHurufTypeface(): Typeface? {
    val context = LocalContext.current
    return remember(context) {
        ResourcesCompat.getFont(context, R.font.quran_uthmanic_hafs)
    }
}

/**
 * Paints the letter wall across the whole canvas.
 *
 * The canvas is divided into cells, but the cells are only a starting point:
 * every letter is jittered, turned, and scaled past its own cell so the
 * letterforms touch and overlap the way hand-set calligraphy does. A second
 * scatter of dots and diamonds drops into the gaps, and the whole wall drifts
 * on a loop that stays seamless when it wraps.
 */
internal fun DrawScope.drawHurufWeave(
    tint: Color,
    baseAlpha: Float,
    phase: Float,
    typeface: Typeface?,
    cellPx: Float
) {
    if (typeface == null || size.width <= 0f || size.height <= 0f || cellPx <= 0f) return

    val w = size.width
    val h = size.height
    val radians = phase * PI.toFloat() / 180f
    val ink = tint.toArgb()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
        this.typeface = typeface
        color = ink
        isDither = true
        textAlign = Paint.Align.CENTER
    }

    val columns = ceil(w / (cellPx * 0.94f)).toInt().coerceIn(3, 14)
    val cellW = w / columns
    val cellH = cellW * 1.04f
    val rows = ceil(h / cellH).toInt() + 3

    // A gentle drift that closes the loop cleanly at 360 degrees.
    val driftX = cos(radians) * cellW * 0.12f
    val driftY = sin(radians) * cellH * 0.18f

    for (row in -2 until rows) {
        for (column in 0 until columns) {
            val index = (row + 2) * columns + column
            val r1 = noise(index, 17)
            val r2 = noise(index, 43)
            val r3 = noise(index, 89)
            val r4 = noise(index, 131)
            val r5 = noise(index, 197)

            val letter = huruf[(index * 5 + (r3 * huruf.size).toInt()) % huruf.size]
            val cx = (column + 0.5f) * cellW + (r1 - 0.5f) * cellW * 0.42f + driftX
            val cy = (row + 0.5f) * cellH + (r2 - 0.5f) * cellH * 0.38f + driftY

            // Most letters sit inside their cell; a good number grow across
            // their neighbours, which is what breaks up any hint of a grid.
            val spread = when {
                r5 > 0.88f -> 1.72f
                r5 > 0.62f -> 1.28f
                else -> 0.78f + r4 * 0.34f
            }
            paint.textSize = fitTextSize(paint, letter, cellW * spread, cellH * spread)
            paint.style = if (r3 > 0.80f) Paint.Style.STROKE else Paint.Style.FILL
            paint.strokeWidth = (paint.textSize * 0.018f).coerceAtLeast(0.8f)
            paint.alpha = (255f * baseAlpha * (0.48f + r4 * 0.52f) * 1.6f)
                .toInt()
                .coerceIn(0, 255)

            val angle = (r4 - 0.5f) * 52f + sin(radians + index) * 3.5f
            drawLetter(paint, letter, cx, cy, angle)
        }
    }

    drawHurufAccents(tint, baseAlpha, cellW, cellH, rows, columns, driftX, driftY)
}

/** Small dots and diamonds scattered through the gaps between letters. */
private fun DrawScope.drawHurufAccents(
    tint: Color,
    baseAlpha: Float,
    cellW: Float,
    cellH: Float,
    rows: Int,
    columns: Int,
    driftX: Float,
    driftY: Float
) {
    val baseDot = (cellW * 0.028f).coerceAtLeast(1.2f)

    for (row in -1 until rows) {
        for (column in -1..columns) {
            val index = row * 1024 + column
            val r1 = noise(index, 211)
            val r2 = noise(index, 269)
            val r3 = noise(index, 331)
            if (r3 < 0.52f) continue

            val cx = (column + 0.5f) * cellW + (r1 - 0.5f) * cellW * 0.94f + driftX * 1.35f
            val cy = (row + 0.5f) * cellH + (r2 - 0.5f) * cellH * 0.94f + driftY * 1.35f
            val localAlpha = (baseAlpha * (0.50f + r2 * 0.85f) * 1.5f).coerceIn(0f, 1f)

            if (r3 > 0.82f) {
                val side = baseDot * (2.3f + r1 * 1.9f)
                rotate(degrees = 45f, pivot = Offset(cx, cy)) {
                    drawRect(
                        color = tint.copy(alpha = localAlpha),
                        topLeft = Offset(cx - side / 2f, cy - side / 2f),
                        size = Size(side, side)
                    )
                }
            } else {
                drawCircle(
                    color = tint.copy(alpha = localAlpha * 0.9f),
                    radius = baseDot * (0.8f + r1 * 0.9f),
                    center = Offset(cx, cy)
                )
            }
        }
    }
}

/** Ink bounds cached per letter, measured once at a nominal size. */
private val glyphRatios = HashMap<String, FloatArray>()

/** Sizes [letter] so its ink just fits inside [maxWidth] by [maxHeight]. */
private fun fitTextSize(paint: Paint, letter: String, maxWidth: Float, maxHeight: Float): Float {
    val ratio = glyphRatios.getOrPut(letter) {
        val previous = paint.textSize
        paint.textSize = 100f
        val bounds = Rect()
        paint.getTextBounds(letter, 0, letter.length, bounds)
        paint.textSize = previous
        floatArrayOf(
            bounds.width().toFloat().coerceAtLeast(1f) / 100f,
            bounds.height().toFloat().coerceAtLeast(1f) / 100f
        )
    }
    return min(maxWidth / ratio[0], maxHeight / ratio[1])
}

/** Draws one isolated letter centred on ([x], [y]) with a small rotation. */
private fun DrawScope.drawLetter(
    paint: Paint,
    letter: String,
    x: Float,
    y: Float,
    angle: Float
) {
    val metrics = paint.fontMetrics
    val baseline = y - (metrics.ascent + metrics.descent) / 2f
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val checkpoint = native.save()
        native.rotate(angle, x, y)
        native.drawText(letter, x, baseline, paint)
        native.restoreToCount(checkpoint)
    }
}

/** Deterministic pseudo-random in 0..1, stable across recompositions. */
private fun noise(index: Int, salt: Int): Float {
    var value = index * 374761393 + salt * 668265263
    value = (value xor (value ushr 13)) * 1274126177
    value = value xor (value ushr 16)
    return (value and 0x7FFFFFFF) / 2147483647f
}

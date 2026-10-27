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

/** Wide, sweeping forms that can carry a whole cell on their own. */
private val heroHuruf = listOf(
    "ع", "غ", "ح", "ج", "خ", "س", "ش", "ص", "ض", "ق", "ك", "ل",
    "ن", "ر", "و", "ط", "ظ", "ف"
)

/** Mid-size letterforms that tuck between the heroes. */
private val bodyHuruf = heroHuruf + listOf("ب", "ت", "ث", "د", "ذ", "ز", "ة", "ي")

/** Compact forms and marks that settle into the gaps. */
private val detailHuruf = listOf(
    "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ص",
    "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "ه",
    "و", "ي", "ء", "أ", "إ", "آ", "ؤ", "ئ", "ة"
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
 * The wall is built in three passes so it reads like a hand-set collage: small
 * marks settle into the gaps, mid-size forms fill the space between them, and
 * a layer of large, mostly upright heroes lands on top. Everything is drawn
 * into one flat ink layer, so overlapping letters stay a single tone instead
 * of stacking into bright patches. The whole wall drifts on a seamless loop.
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

    val columns = ceil(w / (cellPx * 0.72f)).toInt().coerceIn(4, 18)
    val cellW = w / columns
    val cellH = cellW * 1.06f
    val rows = ceil(h / cellH).toInt() + 4

    // A gentle drift that closes the loop cleanly at 360 degrees.
    val driftX = cos(radians) * cellW * 0.11f
    val driftY = sin(radians) * cellH * 0.18f

    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val layer = native.saveLayer(
            0f,
            0f,
            w,
            h,
            Paint().apply { alpha = (255f * baseAlpha * 1.7f).toInt().coerceIn(0, 255) }
        )

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            this.typeface = typeface
            color = ink
            isDither = true
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        for (row in -2 until rows) {
            val stagger = (row and 1) * cellW * 0.5f
            for (column in 0 until columns) {
                val index = (row + 2) * columns + column
                val r1 = noise(index, 17)
                val r2 = noise(index, 43)
                val r3 = noise(index, 89)
                val r4 = noise(index, 131)
                val r5 = noise(index, 197)

                val hero = r5 > 0.64f
                val body = r5 > 0.22f

                val letter = when {
                    hero -> heroHuruf[pick(index, 89, heroHuruf.size)]
                    body -> bodyHuruf[pick(index, 197, bodyHuruf.size)]
                    else -> detailHuruf[pick(index, 331, detailHuruf.size)]
                }
                val spread = when {
                    hero -> 1.08f + r4 * 0.24f
                    body -> 0.84f + r4 * 0.22f
                    else -> 0.44f + r4 * 0.20f
                }
                val jitter = when {
                    hero -> 0.30f
                    body -> 0.36f
                    else -> 0.42f
                }
                val stroke = when {
                    hero -> 0.040f
                    body -> 0.035f
                    else -> 0.030f
                }
                val tilt = when {
                    hero -> if (r3 > 0.92f) (r3 - 0.5f) * 70f else (r3 - 0.5f) * 12f
                    body -> (r4 - 0.5f) * 20f
                    else -> (r4 - 0.5f) * 36f
                }

                val cx = (column + 0.5f) * cellW + stagger +
                    (r1 - 0.5f) * cellW * jitter + driftX
                val cy = (row + 0.5f) * cellH +
                    (r2 - 0.5f) * cellH * jitter * 0.9f + driftY

                paint.textSize = fitTextSize(
                    paint,
                    letter,
                    cellW * spread,
                    cellH * spread
                )
                paint.strokeWidth = (paint.textSize * stroke).coerceAtLeast(1f)
                drawLetter(
                    native = native,
                    paint = paint,
                    letter = letter,
                    x = cx,
                    y = cy,
                    angle = tilt + sin(radians + index) * 2.5f
                )
            }
        }

        drawHurufAccents(tint, cellW, cellH, rows, columns, driftX, driftY)

        native.restoreToCount(layer)
    }
}

/** Small dots and diamonds scattered through the gaps between letters. */
private fun DrawScope.drawHurufAccents(
    tint: Color,
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
            if (r3 < 0.44f) continue

            val cx = (column + 0.5f) * cellW + (r1 - 0.5f) * cellW * 0.94f + driftX * 1.35f
            val cy = (row + 0.5f) * cellH + (r2 - 0.5f) * cellH * 0.94f + driftY * 1.35f

            if (r3 > 0.80f) {
                val side = baseDot * (2.3f + r1 * 1.9f)
                rotate(degrees = 45f, pivot = Offset(cx, cy)) {
                    drawRect(
                        color = tint,
                        topLeft = Offset(cx - side / 2f, cy - side / 2f),
                        size = Size(side, side)
                    )
                }
            } else {
                drawCircle(
                    color = tint,
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

/** Picks a letter from [pool] for a cell, stable across recompositions. */
private fun pick(index: Int, salt: Int, size: Int): Int =
    (index * 5 + (noise(index, salt) * size).toInt()) % size

/** Draws one isolated letter centred on ([x], [y]) with a small rotation. */
private fun drawLetter(
    native: android.graphics.Canvas,
    paint: Paint,
    letter: String,
    x: Float,
    y: Float,
    angle: Float
) {
    val metrics = paint.fontMetrics
    val baseline = y - (metrics.ascent + metrics.descent) / 2f
    val checkpoint = native.save()
    native.rotate(angle, x, y)
    native.drawText(letter, x, baseline, paint)
    native.restoreToCount(checkpoint)
}

/** Deterministic pseudo-random in 0..1, stable across recompositions. */
private fun noise(index: Int, salt: Int): Float {
    var value = index * 374761393 + salt * 668265263
    value = (value xor (value ushr 13)) * 1274126177
    value = value xor (value ushr 16)
    return (value and 0x7FFFFFFF) / 2147483647f
}

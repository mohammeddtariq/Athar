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
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

// Huruf weave: a screen-filling field of connected Naskh words that drifts
// slowly so the texture never settles.

/**
 * Words chosen so every glyph joins its neighbours. Drawing real words instead
 * of isolated letters is what gives the weave authentic Naskh shaping.
 */
private val weaveWords = listOf(
    "أثر", "نور", "هدى", "سلام", "صلاة", "ذكر", "قرآن", "رحمة",
    "يقين", "سكينة", "حمد", "شكر", "توبة", "إيمان", "تقوى", "بركة",
    "أمان", "صفاء", "رجاء", "دعاء", "فجر", "شفق", "ليل", "نجم",
    "قمر", "ماء", "نخيل", "صبر", "عفو", "ودود", "كريم", "حكيم"
)

/** Loads the Naskh face the weave is drawn with. */
@Composable
internal fun rememberHurufTypeface(): Typeface? {
    val context = LocalContext.current
    return remember(context) {
        ResourcesCompat.getFont(context, R.font.quran_uthmanic_hafs)
    }
}

/**
 * Paints the weave across the whole canvas.
 *
 * The field is a four-column grid. Every cell holds one word, sized and
 * rotated a little differently, and the whole field sways and bobs so the
 * pattern keeps breathing without ever collapsing into a single band.
 */
internal fun DrawScope.drawHurufWeave(
    tint: Color,
    baseAlpha: Float,
    phase: Float,
    typeface: Typeface?
) {
    if (typeface == null || size.width <= 0f || size.height <= 0f) return

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

    val columns = 4
    val cellW = w / columns
    val cellH = cellW * 0.88f
    val rows = ceil(h / cellH).toInt() + 2

    // A slow bob and sideways sway: both are periodic over the cycle, so the
    // field never snaps when the animation wraps.
    val swayX = cos(radians) * cellW * 0.10f
    val bobY = sin(radians) * cellH * 0.42f

    for (row in -1 until rows) {
        for (column in 0 until columns) {
            val index = (row + 1) * columns + column
            val r1 = noise(index, 11)
            val r2 = noise(index, 37)
            val r3 = noise(index, 71)
            val r4 = noise(index, 103)
            val r5 = noise(index, 149)

            val word = weaveWords[(index * 7 + (r3 * weaveWords.size).toInt()) % weaveWords.size]
            val cx = (column + 0.5f) * cellW + (r1 - 0.5f) * cellW * 0.22f + swayX
            val cy = (row + 0.5f) * cellH + (r2 - 0.5f) * cellH * 0.18f + bobY

            val sizeFactor = 0.40f + r4 * 0.34f
            paint.textSize = cellW * sizeFactor

            // A slow sway around each word's own centre.
            val angle = (r5 - 0.5f) * 12f + sin(radians + index) * 2.2f
            val breathe = cos(radians + index * 0.7f) * cellH * 0.012f

            paint.style = if (r3 > 0.78f) Paint.Style.STROKE else Paint.Style.FILL
            paint.strokeWidth = (paint.textSize * 0.016f).coerceAtLeast(0.7f)
            paint.alpha = (255f * baseAlpha * (0.50f + r4 * 0.60f) * 1.40f)
                .toInt()
                .coerceIn(0, 255)

            drawWord(paint, word, cx, cy + breathe, angle)
        }
    }
}

/** Draws one word centred on ([x], [y]) with a small rotation. */
private fun DrawScope.drawWord(
    paint: Paint,
    text: String,
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
        native.drawText(text, x, baseline, paint)
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

package com.athar.app.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.athar.app.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Huruf weave: a carved wall of oversized outline letters behind a dense,
// drifting band of filled letters.

/** One letterform inside the weave, positioned in normalised space. */
private data class HurufGlyph(
    val text: String,
    val nx: Float,
    val ny: Float,
    val size: Float,
    val rotation: Float,
    val alpha: Float,
    val phase: Float
)

private val wallLetters = listOf(
    "ه", "و", "م", "ن", "ر", "ب", "ي", "ا", "ل", "س", "ق", "ت", "ع", "د", "ح", "ص"
)

private val bandLetters = listOf(
    "هـ", "وم", "نب", "ري", "سل", "قد", "تع", "مح", "صو", "رق", "نا", "يم", "هد", "فر"
)

private val wallLayout: List<HurufGlyph> = run {
    val random = Random(7319)
    buildList {
        for (row in 0 until 5) {
            for (column in 0 until 4) {
                add(
                    HurufGlyph(
                        text = wallLetters.random(random),
                        nx = -0.05f + column * 0.29f + random.nextFloat() * 0.05f,
                        ny = 0.13f + row * 0.205f + (random.nextFloat() - 0.5f) * 0.05f,
                        size = 0.34f + random.nextFloat() * 0.18f,
                        rotation = random.nextFloat() * 32f - 16f,
                        alpha = 0.42f + random.nextFloat() * 0.42f,
                        phase = random.nextFloat() * 360f
                    )
                )
            }
        }
    }
}

private val bandLayout: List<HurufGlyph> = run {
    val random = Random(1907)
    buildList {
        for (index in 0 until 18) {
            add(
                HurufGlyph(
                    text = bandLetters.random(random),
                    nx = index / 18f + (random.nextFloat() - 0.5f) * 0.02f,
                    ny = 0.12f + random.nextFloat() * 0.74f,
                    size = 0.33f + random.nextFloat() * 0.13f,
                    rotation = random.nextFloat() * 14f - 7f,
                    alpha = 0.55f + random.nextFloat() * 0.4f,
                    phase = random.nextFloat() * 360f
                )
            )
        }
    }
}

private val speckLayout: List<HurufGlyph> = run {
    val random = Random(4127)
    buildList {
        for (index in 0 until 14) {
            add(
                HurufGlyph(
                    text = "",
                    nx = random.nextFloat(),
                    ny = random.nextFloat(),
                    size = 0.010f + random.nextFloat() * 0.008f,
                    rotation = 45f,
                    alpha = 0.35f + random.nextFloat() * 0.35f,
                    phase = random.nextFloat() * 360f
                )
            )
        }
    }
}

/** Loads the bundled display face the weave is drawn with. */
@Composable
internal fun rememberHurufTypeface(): Typeface? {
    val context = LocalContext.current
    return remember(context) {
        ResourcesCompat.getFont(context, R.font.thmanyah_serif_display_black)
    }
}

/**
 * Paints the two-layer weave.
 *
 * [phase] is a slowly increasing angle. It nudges every wall letter around its
 * own centre and carries the band across the screen; the band is drawn in
 * three laps so the loop never shows a seam.
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
    val unit = minOf(w, h)
    val radians = phase * PI.toFloat() / 180f
    val ink = tint.toArgb()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        color = ink
        isDither = true
    }

    paint.style = Paint.Style.STROKE
    paint.strokeWidth = (unit * 0.0022f).coerceAtLeast(0.6f)
    speckLayout.forEach { speck ->
        paint.alpha = (255f * baseAlpha * speck.alpha * 1.6f)
            .toInt()
            .coerceIn(0, 255)
        val half = speck.size * unit
        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            val checkpoint = native.save()
            native.rotate(speck.rotation, speck.nx * w, speck.ny * h)
            native.drawRect(
                speck.nx * w - half,
                speck.ny * h - half,
                speck.nx * w + half,
                speck.ny * h + half,
                paint
            )
            native.restoreToCount(checkpoint)
        }
    }

    paint.strokeWidth = (unit * 0.0018f).coerceAtLeast(0.6f)
    wallLayout.forEach { glyph ->
        val sway = sin(radians + glyph.phase) * unit * 0.03f
        val lift = cos(radians * 0.7f + glyph.phase) * unit * 0.02f
        paint.textSize = glyph.size * unit
        paint.alpha = (255f * baseAlpha * glyph.alpha * 0.95f)
            .toInt()
            .coerceIn(0, 255)
        drawRotated(
            paint = paint,
            text = glyph.text,
            x = glyph.nx * w + sway,
            y = glyph.ny * h + lift,
            angle = glyph.rotation + sin(radians * 0.55f + glyph.phase) * 2.0f
        )
    }

    val bandHeight = h * 0.26f
    val bandTop = h * 0.38f
    // A lap is one full screen width, so the wrap point always lands on a copy.
    val bandShift = ((phase / 360f) * w * 1.5f) % w

    clipRect(left = 0f, top = bandTop, right = w, bottom = bandTop + bandHeight) {
        paint.style = Paint.Style.FILL
        bandLayout.forEach { glyph ->
            paint.textSize = glyph.size * unit
            paint.alpha = (255f * baseAlpha * glyph.alpha * 1.9f)
                .toInt()
                .coerceIn(0, 255)
            val breathe = sin(radians * 1.6f + glyph.phase) * unit * 0.012f
            val baseX = glyph.nx * w
            val y = bandTop + glyph.ny * bandHeight + breathe
            val angle = glyph.rotation + sin(radians * 0.9f + glyph.phase) * 1.4f
            listOf(-w, 0f, w).forEach { lap ->
                drawRotated(
                    paint = paint,
                    text = glyph.text,
                    x = baseX + bandShift + lap,
                    y = y,
                    angle = angle
                )
            }
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

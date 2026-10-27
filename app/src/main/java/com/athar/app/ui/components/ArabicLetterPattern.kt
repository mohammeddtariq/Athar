package com.athar.app.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate

// ══════════════════════════════════════════════════════════════
// Arabic Letter Backdrop
//
// The alternative to the geometric lattice: the Arabic alphabet
// itself, drawn as hairline letterforms and scattered across the
// surface in a woven, hand-inked wall. Each glyph is a single
// stroked path so the whole backdrop stays light enough to sit
// under scrolling lists.
// ══════════════════════════════════════════════════════════════

private const val GLYPH_COUNT = 8

/**
 * Glyphs are drawn slightly wider than their cell so neighbouring letters
 * touch and overlap, the way hand-inked calligraphy sits on a wall.
 */
private const val GLYPH_OVERSIZE = 1.18f

/** How each cell is turned, so the weave never falls into a rhythm. */
private val GLYPH_TILT = floatArrayOf(-10f, 6f, 184f, -4f)

/**
 * Builds the eight letterforms (alef, reh, waw, beh, noon, yeh, meem, heh)
 * already scaled to a [cellPx] box. Built once per size, then only translated
 * while drawing.
 */
internal fun buildArabicGlyphs(cellPx: Float): List<Path> {
    val s = cellPx
    fun glyph(block: Path.() -> Unit) = Path().apply(block)

    return listOf(
        // ا — alef: a tall stroke that tails off to the right.
        glyph {
            moveTo(s * 0.54f, s * 0.08f)
            lineTo(s * 0.51f, s * 0.72f)
            quadraticBezierTo(s * 0.49f, s * 0.86f, s * 0.32f, s * 0.89f)
        },
        // ر — reh: a sweeping head with a hook.
        glyph {
            moveTo(s * 0.22f, s * 0.30f)
            quadraticBezierTo(s * 0.64f, s * 0.20f, s * 0.67f, s * 0.55f)
            quadraticBezierTo(s * 0.69f, s * 0.74f, s * 0.50f, s * 0.83f)
        },
        // و — waw: a loop with a descending tail.
        glyph {
            addOval(Rect(s * 0.28f, s * 0.13f, s * 0.70f, s * 0.55f))
            moveTo(s * 0.58f, s * 0.50f)
            quadraticBezierTo(s * 0.76f, s * 0.71f, s * 0.55f, s * 0.90f)
        },
        // ب — beh: a shallow boat with one dot beneath.
        glyph {
            moveTo(s * 0.08f, s * 0.43f)
            quadraticBezierTo(s * 0.50f, s * 0.73f, s * 0.92f, s * 0.43f)
            addRect(Rect(s * 0.46f, s * 0.78f, s * 0.54f, s * 0.86f))
        },
        // ن — noon: a deeper bowl with a dot above.
        glyph {
            moveTo(s * 0.10f, s * 0.36f)
            quadraticBezierTo(s * 0.50f, s * 0.88f, s * 0.90f, s * 0.36f)
            addRect(Rect(s * 0.46f, s * 0.10f, s * 0.54f, s * 0.18f))
        },
        // ي — yeh: a deep bowl with two dots beneath.
        glyph {
            moveTo(s * 0.10f, s * 0.32f)
            quadraticBezierTo(s * 0.50f, s * 0.86f, s * 0.90f, s * 0.32f)
            addRect(Rect(s * 0.35f, s * 0.87f, s * 0.43f, s * 0.95f))
            addRect(Rect(s * 0.57f, s * 0.87f, s * 0.65f, s * 0.95f))
        },
        // م — meem: a closed loop with a hanging tail.
        glyph {
            addOval(Rect(s * 0.26f, s * 0.14f, s * 0.72f, s * 0.58f))
            moveTo(s * 0.62f, s * 0.50f)
            lineTo(s * 0.62f, s * 0.76f)
            quadraticBezierTo(s * 0.60f, s * 0.90f, s * 0.42f, s * 0.90f)
        },
        // ه — heh: a rounded loop with a side stroke.
        glyph {
            addOval(Rect(s * 0.18f, s * 0.30f, s * 0.72f, s * 0.74f))
            moveTo(s * 0.70f, s * 0.26f)
            lineTo(s * 0.70f, s * 0.72f)
        }
    )
}

/**
 * Scatters the letterforms in a staggered grid, fading along the same diagonal
 * as the geometric backdrop so text stays readable on top.
 */
internal fun DrawScope.drawArabicLetterLattice(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    phase: Float,
    glyphs: List<Path>
) {
    val w = size.width
    val h = size.height
    val cols = (w / cellPx).toInt() + 3
    val rows = (h / cellPx).toInt() + 3

    // The same slow drift the geometric backdrop uses.
    val driftX = (phase / 360f) * cellPx * 0.22f
    val driftY = (phase / 360f) * cellPx * 0.14f

    val stroke = (cellPx * 0.030f).coerceAtLeast(1f)
    val dotStroke = (cellPx * 0.022f).coerceAtLeast(0.9f)
    val half = cellPx / 2f

    for (r in -1..rows) {
        for (c in -1..cols) {
            val cx = c * cellPx + (if (r % 2 == 0) 0f else cellPx / 2f) - driftX
            val cy = r * cellPx - driftY

            // Dense towards the top-end corner, fading towards the bottom-start.
            val diagonal = ((cx / w) * 0.75f + (1f - (cy / h)) * 0.25f).coerceIn(0f, 1f)
            val localAlpha = baseAlpha * (diagonal * diagonal * 1.3f).coerceIn(0.01f, 1f)
            if (localAlpha <= 0.005f) continue

            val ink = tint.copy(alpha = localAlpha)
            val index = (((c * 3 + r * 5) % GLYPH_COUNT) + GLYPH_COUNT) % GLYPH_COUNT
            val tilt = GLYPH_TILT[(((c + r) % GLYPH_TILT.size) + GLYPH_TILT.size) % GLYPH_TILT.size]

            translate(left = cx - half, top = cy - half) {
                scale(scale = GLYPH_OVERSIZE, pivot = Offset(half, half)) {
                    rotate(degrees = tilt, pivot = Offset(half, half)) {
                        drawPath(
                            path = glyphs[index],
                            color = ink,
                            style = Stroke(width = stroke)
                        )
                    }
                }
            }

            // Small diamonds in the gaps, echoing the geometric backdrop.
            if ((c + r) % 2 == 0) {
                drawDiamond(
                    center = Offset(cx + cellPx * 0.5f, cy + cellPx * 0.42f),
                    radius = cellPx * 0.055f,
                    color = tint.copy(alpha = localAlpha * 0.75f),
                    strokeWidth = dotStroke
                )
            }
        }
    }
}

private fun DrawScope.drawDiamond(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float
) {
    rotate(degrees = 45f, pivot = center) {
        drawRect(
            color = color,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth)
        )
    }
}

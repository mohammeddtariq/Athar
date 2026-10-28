package com.athar.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.athar.app.ui.theme.AtharBackdropTint
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharBackgroundState
import com.athar.app.ui.theme.AtharBackgroundStyle
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════
// ATHAR — Backdrops
//
// Two hand-built walls sit behind every screen:
//   ISLAMIC — a lit arcade of mihrab niches: pointed arches carved
//             in three steps, a hanging lamp, a keystone star and a
//             dentil cornice. Light travels niche to niche.
//   LETTERS — a woven field of isolated Naskh letters in slow drift.
//
// Both are drawn with hairline-to-mid inks and fade along the same
// diagonal, so text on top always stays readable. Every surface in
// the app (screens, dialogs, floating cards) routes through here, so
// one switch repaints all of them at once.
// ══════════════════════════════════════════════════════════════

/**
 * The app-wide backdrop.
 *
 * Draws whichever [AtharBackgroundStyle] is selected. Both styles drift on
 * their own long cycles; SOLID draws nothing and lets the theme's own colour
 * stand edge to edge.
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    tint: Color = AtharBackdropTint,
    alpha: Float = 0.12f,
    animated: Boolean = false,
    cellDp: Float = 54f
) {
    val style = AtharBackgroundState.current

    // A solid surface needs no ink at all.
    if (style == AtharBackgroundStyle.SOLID) return

    val density = LocalDensity.current
    val cellPx = with(density) { cellDp.dp.toPx() }
    val typeface = rememberHurufTypeface()

    // The arcade's paths are rebuilt only when the size or the cell changes.
    val arcadeCache = remember { ArcadeCache() }

    val transition = rememberInfiniteTransition(label = "patternDrift")

    // One full niche of travel per loop: the restart lands on an identical
    // frame, so the drift reads as continuous motion instead of a jump.
    val arcadePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(38000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arcadePhase"
    )
    // Light travelling across the wall, independent of the drift.
    val glowPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(21000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arcadeGlow"
    )
    val letterPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(26000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "patternLetterPhase"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.86f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "patternBreathe"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawBackdrop(
            style = style,
            tint = tint,
            alpha = alpha * breathe,
            cellPx = cellPx,
            letterPhase = letterPhase,
            arcadePhase = arcadePhase,
            glowPhase = glowPhase,
            typeface = typeface,
            arcadeCache = arcadeCache
        )
    }
}

/** Routes to the selected backdrop. */
private fun DrawScope.drawBackdrop(
    style: AtharBackgroundStyle,
    tint: Color,
    alpha: Float,
    cellPx: Float,
    letterPhase: Float,
    arcadePhase: Float,
    glowPhase: Float,
    typeface: android.graphics.Typeface?,
    arcadeCache: ArcadeCache
) {
    when (style) {
        AtharBackgroundStyle.ISLAMIC ->
            drawMihrabArcade(tint, alpha, cellPx, arcadePhase, glowPhase, arcadeCache)
        AtharBackgroundStyle.LETTERS ->
            drawHurufWeave(tint, alpha, letterPhase, typeface, cellPx)
        AtharBackgroundStyle.SOLID -> Unit
    }
}

// ─── Mihrab arcade ─────────────────────────────────────────────

/** One niche, built once per size change and reused every frame. */
internal class ArchCell(
    val column: Int,
    val row: Int,
    val x: Float,
    val y: Float,
    val outer: Path,
    val middle: Path,
    val recess: Path,
    val inner: Path,
    val star: Path,
    val lamp: Path,
    val foot: Path,
    val dentils: Path,
    val diamond: Path
)

internal class ArcadeGeometry(
    val cells: List<ArchCell>,
    val cellW: Float,
    val cellH: Float,
    val baseOffsetY: Float
)

/** Keeps the built arcade alive across frames and recompositions. */
internal class ArcadeCache {
    private var cellPx = -1f
    private var width = -1
    private var height = -1
    private var geometry: ArcadeGeometry? = null

    internal fun geometryFor(cellPx: Float, width: Float, height: Float): ArcadeGeometry {
        val w = width.toInt()
        val h = height.toInt()
        val cached = geometry
        if (cached != null && this.cellPx == cellPx && this.width == w && this.height == h) {
            return cached
        }
        val built = buildArcade(cellPx, width, height)
        this.cellPx = cellPx
        this.width = w
        this.height = h
        geometry = built
        return built
    }
}

/**
 * Builds the arcade once. Every path is expressed relative to its own niche
 * origin, so the draw pass only has to translate the canvas.
 */
private fun buildArcade(cellPx: Float, width: Float, height: Float): ArcadeGeometry {
    val cellW = cellPx
    val cellH = cellPx * 1.14f
    val columns = ceil(width / cellW).toInt() + 3
    val rows = ceil(height / cellH).toInt() + 2

    val archW = cellW * 0.60f
    val archH = cellH * 0.70f
    val baseY = 0f
    val cells = ArrayList<ArchCell>(columns * rows)

    for (row in -1..rows) {
        for (column in -2..columns) {
            val x = column * cellW + (if (row and 1 == 0) 0f else cellW / 2f)
            val y = row * cellH

            val outer = archPath(0f, baseY, archW, archH, 0f, 0f)
            val middle = archPath(0f, baseY, archW, archH, archW * 0.13f, archH * 0.09f)
            val recess = archPath(0f, baseY, archW, archH, archW * 0.24f, archH * 0.18f)
            val inner = archPath(0f, baseY, archW, archH, archW * 0.34f, archH * 0.27f)

            cells += ArchCell(
                column = column,
                row = row,
                x = x,
                y = y,
                outer = outer,
                middle = middle,
                recess = recess,
                inner = inner,
                star = starPath(0f, baseY - archH * 0.44f, cellW * 0.050f),
                lamp = diamondPath(0f, baseY - archH * 0.24f, cellW * 0.026f, cellW * 0.040f),
                foot = Path().apply {
                    lineTo(-archW * 0.5f, baseY)
                    lineTo(-archW * 0.5f, baseY + cellH * 0.055f)
                    moveTo(archW * 0.5f, baseY)
                    lineTo(archW * 0.5f, baseY + cellH * 0.055f)
                },
                dentils = Path().apply {
                    for (k in 0..3) {
                        val dx = -cellW * 0.30f + k * cellW * 0.20f
                        moveTo(dx, baseY + cellH * 0.006f)
                        lineTo(dx, baseY + cellH * 0.046f)
                    }
                },
                diamond = diamondPath(0f, cellH * 0.055f, cellW * 0.040f, cellW * 0.040f)
            )
        }
    }

    return ArcadeGeometry(
        cells = cells,
        cellW = cellW,
        cellH = cellH,
        baseOffsetY = cellH * 0.50f
    )
}

/** A pointed mihrab arch: two straight haunches and two cubic shoulders to the apex. */
private fun archPath(
    cx: Float,
    baseY: Float,
    w: Float,
    h: Float,
    insetW: Float,
    insetH: Float
): Path {
    val half = w / 2f - insetW
    val spring = baseY - h * 0.42f + insetH
    val apex = baseY - h + insetH * 1.4f
    return Path().apply {
        moveTo(cx - half, baseY)
        lineTo(cx - half, spring)
        cubicTo(
            cx - half, spring - h * 0.30f,
            cx - half * 0.34f, apex + h * 0.06f,
            cx, apex
        )
        cubicTo(
            cx + half * 0.34f, apex + h * 0.06f,
            cx + half, spring - h * 0.30f,
            cx + half, spring
        )
        lineTo(cx + half, baseY)
    }
}

/** Eight-pointed keystone star, stroked as one closed path. */
private fun starPath(cx: Float, cy: Float, radius: Float): Path = Path().apply {
    for (i in 0 until 16) {
        val r = if (i % 2 == 0) radius else radius * 0.46f
        val angle = (PI * i / 8f - PI / 2f).toFloat()
        val px = cx + r * cos(angle)
        val py = cy + r * sin(angle)
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
    close()
}

private fun diamondPath(cx: Float, cy: Float, halfW: Float, halfH: Float): Path = Path().apply {
    moveTo(cx, cy - halfH)
    lineTo(cx + halfW, cy)
    lineTo(cx, cy + halfH)
    lineTo(cx - halfW, cy)
    close()
}

/**
 * Draws the lit arcade.
 *
 * Each niche is a shallow carved recess: a soft fill for depth, an outer
 * outline carried by a wide glow stroke, two recessed arch rings, a keystone
 * star and a hanging lamp. A dentil cornice binds every row into a wall, and a
 * travelling sine wave lifts the inner lines niche by niche, so a warm light
 * appears to move across the stone.
 */
internal fun DrawScope.drawMihrabArcade(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    phase: Float,
    glowPhase: Float,
    cache: ArcadeCache
) {
    if (size.width <= 0f || size.height <= 0f || cellPx <= 0f) return

    val geometry: ArcadeGeometry = cache.geometryFor(cellPx, size.width, size.height)
    val w = size.width
    val h = size.height
    val driftX = phase * geometry.cellW
    val hairline = (cellPx * 0.011f).coerceAtLeast(1f)
    val glowWidth = (cellPx * 0.030f).coerceAtLeast(2f)
    val fine = (cellPx * 0.0075f).coerceAtLeast(1f)
    val fainter = (cellPx * 0.006f).coerceAtLeast(1f)

    for (cell in geometry.cells) {
        val cx = cell.x + driftX
        val cy = cell.y + geometry.baseOffsetY

        // Density fades from the top-end corner towards the bottom-start, so
        // the wall is present without ever fighting the content on top.
        val diagonal = ((cx / w) * 0.62f + (1f - cy / h) * 0.38f).coerceIn(0f, 1f)
        val local = baseAlpha * (0.34f + diagonal * diagonal * 2.1f)
        if (local <= 0.004f) continue

        val glow = 0.55f + 0.45f * sin((cell.column * 0.9f + cell.row * 0.62f) * 1.1f + glowPhase)

        withTransform({ translate(left = cx, top = cy) }) {
            // Recess fill: light pooling at the foot of the niche.
            drawPath(
                path = cell.outer,
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.55f to tint.copy(alpha = local * 0.10f),
                        1f to tint.copy(alpha = local * 0.34f)
                    ),
                    startY = -geometry.cellH * 0.72f,
                    endY = 0f
                ),
                style = androidx.compose.ui.graphics.drawscope.Fill
            )

            // Outer shoulder glow, then the crisp outline on top.
            drawPath(
                path = cell.outer,
                color = tint.copy(alpha = local * 0.30f * glow),
                style = Stroke(width = glowWidth)
            )
            drawPath(
                path = cell.outer,
                color = tint.copy(alpha = local),
                style = Stroke(width = hairline)
            )

            // Carved rings stepping back inside the niche.
            drawPath(
                path = cell.middle,
                color = tint.copy(alpha = local * (0.34f + 0.42f * glow)),
                style = Stroke(width = fine)
            )
            drawPath(
                path = cell.recess,
                color = tint.copy(alpha = local * (0.22f + 0.22f * glow)),
                style = Stroke(width = fainter)
            )
            drawPath(
                path = cell.inner,
                color = tint.copy(alpha = local * (0.16f + 0.14f * glow)),
                style = Stroke(width = fainter)
            )

            drawPath(
                path = cell.star,
                color = tint.copy(alpha = local * (0.38f + 0.40f * glow)),
                style = Stroke(width = fine)
            )
            drawPath(
                path = cell.lamp,
                color = tint.copy(alpha = local * (0.34f + 0.34f * glow)),
                style = Stroke(width = fine)
            )

            // Cornice: the main band, its shadow line and the dentils between.
            drawLine(
                color = tint.copy(alpha = local * 0.85f),
                start = Offset(-geometry.cellW * 0.5f, 0f),
                end = Offset(geometry.cellW * 0.5f, 0f),
                strokeWidth = (cellPx * 0.010f).coerceAtLeast(1f)
            )
            drawLine(
                color = tint.copy(alpha = local * 0.42f),
                start = Offset(-geometry.cellW * 0.5f, geometry.cellH * 0.052f),
                end = Offset(geometry.cellW * 0.5f, geometry.cellH * 0.052f),
                strokeWidth = fainter
            )
            drawPath(
                path = cell.dentils,
                color = tint.copy(alpha = local * 0.55f),
                style = Stroke(width = (cellPx * 0.008f).coerceAtLeast(1f))
            )
            drawPath(
                path = cell.foot,
                color = tint.copy(alpha = local * 0.55f),
                style = Stroke(width = (cellPx * 0.008f).coerceAtLeast(1f))
            )
            drawPath(
                path = cell.diamond,
                color = tint.copy(alpha = local * 0.70f),
                style = Stroke(width = (cellPx * 0.008f).coerceAtLeast(1f))
            )
        }
    }
}

/** Convenience wrapper: opaque dark base + the selected backdrop behind [content]. */
@Composable
fun PatternScaffold(
    modifier: Modifier = Modifier,
    patternAlpha: Float = 0.12f,
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        IslamicPatternBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = patternAlpha,
            animated = animated
        )
        content()
    }
}

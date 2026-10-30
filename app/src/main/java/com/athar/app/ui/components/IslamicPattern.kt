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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
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
//   ISLAMIC — a khatam lattice: an eight-pointed star at every node,
//             built from a pair of rotated squares, with its straps
//             running out to the tips of the neighbouring stars so the
//             field closes into one interlaced geometry, and a small
//             diamond where the diagonals cross.
//   LETTERS — a woven field of isolated Naskh letters in slow drift.
//
// Both are drawn with hairline inks and fade along the same diagonal,
// so text on top always stays readable. Every surface in the app
// (screens, dialogs, floating cards) routes through here, so one
// switch repaints all of them at once.
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
    cellDp: Float = 54f,
    styleOverride: AtharBackgroundStyle? = null
) {
    val style = styleOverride ?: AtharBackgroundState.current

    val cellPx = with(LocalDensity.current) { cellDp.dp.toPx() }
    val typeface = rememberHurufTypeface()

    // The star stamp is rebuilt only when the cell size changes.
    val girihCache = remember { KhatamCache() }

    // The wall is recorded once into a display list and then only moved around.
    // Painting it directly meant rebuilding every star on every frame, on every
    // screen, which is where the stutter came from. Now a frame costs one layer
    // draw.
    val baked = remember { BakedBackdrop() }
    baked.layer = rememberGraphicsLayer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val transition = rememberInfiniteTransition(label = "patternDrift")

    // One full cell of travel per loop: the restart lands on an identical
    // frame, so the drift reads as continuous motion instead of a jump. The
    // stars move on the diagonal, which is the lattice's own direction.
    val girihPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "girihPhase"
    )
    // Light travelling across the wall, independent of the drift.
    val glowPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(21000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "girihGlow"
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

    // The canvas is keyed on the style so switching to solid rebuilds the
    // node instead of retiring one mid-composition. Retiring it used to leave
    // the previous pattern's tiles behind as pale boxes.
    key(style) {
        Canvas(modifier = modifier.fillMaxSize()) {
            if (style == AtharBackgroundStyle.SOLID) return@Canvas
            if (size.width <= 0f || size.height <= 0f) return@Canvas

            val bleed = cellPx
            val wall = baked.ensure(
                style = style,
                width = size.width,
                height = size.height,
                cellPx = cellPx,
                tint = tint,
                typeface = typeface,
                cache = girihCache,
                density = density,
                layoutDirection = layoutDirection,
                bleed = bleed
            ) ?: return@Canvas

            val driftX: Float
            val driftY: Float
            if (style == AtharBackgroundStyle.ISLAMIC) {
                driftX = girihPhase * cellPx
                driftY = girihPhase * cellPx
            } else {
                val radians = letterPhase * PI.toFloat() / 180f
                driftX = cos(radians) * cellPx * 0.12f
                driftY = sin(radians) * cellPx * 0.18f
            }

            val ink = (alpha * breathe / BAKE_ALPHA).coerceIn(0f, 1f)
            wall.alpha = ink
            // The wall is recorded with one cell of bleed on every side so a
            // drifted frame never uncovers a bare strip at the edges.
            translate(left = driftX - bleed, top = driftY - bleed) {
                drawLayer(wall)
            }

            // The stars themselves no longer re-light every frame, so a single
            // additive wash carries the travelling light across them.
            if (style == AtharBackgroundStyle.ISLAMIC) {
                val sweep = (0.5f + 0.5f * sin(glowPhase - PI.toFloat() / 2f)) * size.width
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(tint.copy(alpha = alpha * 0.34f), Color.Transparent),
                        center = Offset(sweep, size.height * 0.34f),
                        radius = size.width * 0.62f
                    ),
                    blendMode = BlendMode.Plus
                )
            }
        }
    }
}

/**
 * Alpha the wall is recorded at. The real opacity is applied when the layer is
 * drawn, so one bake serves every breathe level without re-recording.
 */
private const val BAKE_ALPHA = 0.42f

/**
 * Holds the recorded wall and the parameters it was recorded with, so a size,
 * accent or style change re-records exactly once.
 */
private class BakedBackdrop {
    var layer: GraphicsLayer? = null

    private var style: AtharBackgroundStyle? = null
    private var width = -1f
    private var height = -1f
    private var cellPx = -1f
    private var tint: Color = Color.Unspecified

    fun ensure(
        style: AtharBackgroundStyle,
        width: Float,
        height: Float,
        cellPx: Float,
        tint: Color,
        typeface: android.graphics.Typeface?,
        cache: KhatamCache,
        density: androidx.compose.ui.unit.Density,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        bleed: Float
    ): GraphicsLayer? {
        val target = layer ?: return null
        val unchanged = this.style == style &&
            this.cellPx == cellPx &&
            this.tint == tint &&
            this.width == width &&
            this.height == height
        if (unchanged) return target

        val recordedWidth = (width + bleed * 2f).toInt().coerceAtLeast(1)
        val recordedHeight = (height + bleed * 2f).toInt().coerceAtLeast(1)
        target.record(
            density = density,
            layoutDirection = layoutDirection,
            size = IntSize(recordedWidth, recordedHeight)
        ) {
            translate(left = bleed, top = bleed) {
                when (style) {
                    AtharBackgroundStyle.ISLAMIC ->
                        drawKhatamWall(tint, BAKE_ALPHA, cellPx, cache)
                    AtharBackgroundStyle.LETTERS ->
                        drawHurufWeave(tint, BAKE_ALPHA, 0f, typeface, cellPx)
                    AtharBackgroundStyle.SOLID -> Unit
                }
            }
        }

        this.style = style
        this.width = width
        this.height = height
        this.cellPx = cellPx
        this.tint = tint
        return target
    }
}

// ─── Khatam wall ───────────────────────────────────────────────

/**
 * One node of the lattice, built at the current cell size.
 *
 * The star is the classic khatam: two squares inscribed in the same circle,
 * one turned 45°, whose union is the eight-pointed figure. Both squares keep
 * their own outline, so the star reads as two interlaced frames instead of a
 * silhouette, and the small octagon sits on the notches between the points.
 * Each point then carries a strap out to the point of the star beside it,
 * which is what closes the wall into one geometry rather than a field of
 * separate rosettes.
 */
internal class KhatamMotif(val cell: Float) {
    /** Distance from the node to the tip of a point. */
    val radius = cell * 0.315f

    /** Distance to the notch between two points, which is where the squares met. */
    private val notch = cell * 0.212f

    val star: Path = starPath(radius, notch)
    val inner: Path = polygonPath(8, notch * 0.46f, PI.toFloat() / 8f)

    /** Small diamond sitting where the two diagonals of the cell cross. */
    val diamond: Path = polygonPath(4, cell * 0.080f, PI.toFloat() / 4f, at = Offset(cell * 0.5f, cell * 0.5f))

    val core = cell * 0.011f
    val knot = cell * 0.010f
    val fine = (cell * 0.0046f).coerceAtLeast(1f)
    val ribbon = (cell * 0.0036f).coerceAtLeast(0.9f)

    /** Inset that keeps a strap from touching the ink of its star. */
    val strapInset = cell * 0.022f
    val axisStart = radius + strapInset
    val axisEnd = cell - radius - strapInset
    val diagonalStart = radius * 0.7071f + strapInset
    val diagonalEnd = cell - radius * 0.7071f - strapInset
}

/** Keeps the built node alive across frames and recompositions. */
internal class KhatamCache {
    private var cellPx = -1f
    private var motif: KhatamMotif? = null

    internal fun motifFor(cellPx: Float): KhatamMotif {
        val cached = motif
        if (cached != null && this.cellPx == cellPx) return cached
        val built = KhatamMotif(cellPx)
        this.cellPx = cellPx
        motif = built
        return built
    }
}

private fun polar(r: Float, angle: Float): Offset =
    Offset(r * cos(angle), r * sin(angle))

/** An n-gon whose vertices sit on [radius], first one at [rotation]. */
private fun polygonPath(
    sides: Int,
    radius: Float,
    rotation: Float,
    at: Offset = Offset.Zero
): Path = Path().apply {
    for (k in 0 until sides) {
        val point = polar(radius, rotation + k * 2f * PI.toFloat() / sides) + at
        if (k == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
    }
    close()
}

/**
 * The eight-pointed star: [points] tips on [tip], with a notch on [notch]
 * between each pair. The union of the two rotated squares is exactly this
 * outline, drawn once instead of stroking both squares over each other.
 */
private fun starPath(tip: Float, notch: Float, points: Int = 8): Path = Path().apply {
    for (k in 0 until points * 2) {
        val point = polar(if (k % 2 == 0) tip else notch, k * PI.toFloat() / points)
        if (k == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
    }
    close()
}

/**
 * Draws the khatam wall.
 *
 * Each node stamps one eight-pointed star, its inner octagon and a bead at the
 * centre, then runs straps to the four stars beside it and to the two on the
 * diagonals, so the field interlocks. A diamond sits where the diagonal straps
 * cross, and a bead where the axis straps do, so the wall has two scales of
 * ornament instead of one repeated shape.
 *
 * Density fades from the top-end corner towards the bottom-start, and each cell
 * carries a small, fixed variation in weight, so the wall never reads as flat
 * wallpaper under the content.
 */
internal fun DrawScope.drawKhatamWall(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    cache: KhatamCache
) {
    if (size.width <= 0f || size.height <= 0f || cellPx <= 0f) return

    val motif = cache.motifFor(cellPx)
    val w = size.width
    val h = size.height
    val columns = ceil(w / cellPx).toInt() + 2
    val rows = ceil(h / cellPx).toInt() + 2
    val half = cellPx * 0.5f

    for (row in -2..rows) {
        for (column in -2..columns) {
            val cx = column * cellPx
            val cy = row * cellPx

            val diagonal = ((cx / w) * 0.62f + (1f - cy / h) * 0.38f).coerceIn(0f, 1f)
            val lit = 0.88f + 0.12f * sin(column * 0.9f + row * 0.62f)
            val local = baseAlpha * (0.58f + diagonal * 1.62f) * lit
            if (local <= 0.004f) continue

            withTransform({ translate(left = cx, top = cy) }) {
                // Straps first, so the stars sit over their own joints.
                if (motif.axisEnd > motif.axisStart) {
                    val axis = tint.copy(alpha = local * 0.24f)
                    drawLine(
                        color = axis,
                        start = Offset(motif.axisStart, 0f),
                        end = Offset(motif.axisEnd, 0f),
                        strokeWidth = motif.ribbon
                    )
                    drawLine(
                        color = axis,
                        start = Offset(0f, motif.axisStart),
                        end = Offset(0f, motif.axisEnd),
                        strokeWidth = motif.ribbon
                    )
                }
                if (motif.diagonalEnd > motif.diagonalStart) {
                    val diag = tint.copy(alpha = local * 0.15f)
                    drawLine(
                        color = diag,
                        start = Offset(motif.diagonalStart, motif.diagonalStart),
                        end = Offset(motif.diagonalEnd, motif.diagonalEnd),
                        strokeWidth = motif.ribbon
                    )
                    drawLine(
                        color = diag,
                        start = Offset(-motif.diagonalStart, motif.diagonalStart),
                        end = Offset(-motif.diagonalEnd, motif.diagonalEnd),
                        strokeWidth = motif.ribbon
                    )
                }

                // A faint wash inside the star, so it reads as lit stone rather
                // than an empty outline.
                drawPath(
                    path = motif.star,
                    color = tint.copy(alpha = local * 0.085f)
                )
                drawPath(
                    path = motif.star,
                    color = tint.copy(alpha = local * 0.72f),
                    style = Stroke(width = motif.fine)
                )
                drawPath(
                    path = motif.inner,
                    color = tint.copy(alpha = local * 0.36f),
                    style = Stroke(width = motif.fine)
                )
                drawCircle(
                    color = tint.copy(alpha = local * 0.66f),
                    radius = motif.core,
                    center = Offset.Zero
                )

                // Beads where the axis straps meet, and a diamond at the
                // crossing of the diagonals, one cell further out.
                drawCircle(
                    color = tint.copy(alpha = local * 0.34f),
                    radius = motif.knot,
                    center = Offset(half, 0f)
                )
                drawCircle(
                    color = tint.copy(alpha = local * 0.34f),
                    radius = motif.knot,
                    center = Offset(0f, half)
                )
                drawPath(
                    path = motif.diamond,
                    color = tint.copy(alpha = local * 0.36f),
                    style = Stroke(width = motif.fine)
                )
            }
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

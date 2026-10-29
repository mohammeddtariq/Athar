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
import androidx.compose.ui.graphics.drawscope.Fill
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
//   ISLAMIC — a woven girih field: eight-fold khatam medallions cut
//             into kite facets, each ringed by a sixteen-point
//             corona, with small stars filling the gaps. Neighbouring
//             medallions overlap, so the outlines braid into one
//             continuous lattice.
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
    cellDp: Float = 54f,
    styleOverride: AtharBackgroundStyle? = null
) {
    val style = styleOverride ?: AtharBackgroundState.current

    val cellPx = with(LocalDensity.current) { cellDp.dp.toPx() }
    val typeface = rememberHurufTypeface()

    // The medallion stamp is rebuilt only when the cell size changes.
    val girihCache = remember { GirihCache() }

    // The wall is recorded once into a display list and then only moved around.
    // Painting it directly meant rebuilding every medallion on every frame, on
    // every screen, which is where the stutter came from. Now a frame costs one
    // layer draw.
    val baked = remember { BakedBackdrop() }
    baked.layer = rememberGraphicsLayer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val transition = rememberInfiniteTransition(label = "patternDrift")

    // One full cell of travel per loop: the restart lands on an identical
    // frame, so the drift reads as continuous motion instead of a jump. The
    // medallions move on the diagonal, which is the lattice's own direction.
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
                girihCache = girihCache,
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

            // The medallions themselves no longer re-light every frame, so a
            // single additive wash carries the travelling light across them.
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
        girihCache: GirihCache,
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
                        drawGirihWall(tint, BAKE_ALPHA, cellPx, girihCache)
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

// ─── Girih wall ────────────────────────────────────────────────

/**
 * One medallion's paths, built at the current cell size. Every cell on the
 * wall is this same stamp, so the field costs one build and a translate per
 * cell instead of a rebuild per frame.
 */
internal class GirihMotif(val cell: Float) {
    private val radius = cell * 0.60f
    private val valley = radius * 0.62f
    private val inner = radius * 0.36f

    val corona: Path
    val facets: List<Path>
    val outerStar: Path
    val innerStar: Path
    val octagon: Path
    val innerOctagon: Path
    val core: Path
    val gapStar: Path

    val hair = (cell * 0.0085f).coerceAtLeast(1f)
    val fine = (cell * 0.0055f).coerceAtLeast(1f)
    val dot = (cell * 0.011f).coerceAtLeast(1f)

    init {
        corona = Path().apply {
            for (k in 0 until 32) {
                val r = when {
                    k % 4 == 0 -> radius * 1.06f
                    k % 2 == 0 -> radius * 0.94f
                    else -> radius * 0.88f
                }
                val angle = (PI * k / 16f - PI / 2f).toFloat()
                val px = r * cos(angle)
                val py = r * sin(angle)
                if (k == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }

        facets = List(8) { k ->
            val angle = -PI.toFloat() / 2f + k * PI.toFloat() / 4f
            val tip = polar(radius, angle)
            val left = polar(valley, angle + PI.toFloat() / 8f)
            val right = polar(valley, angle - PI.toFloat() / 8f)
            val near = polar(inner, angle)
            Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(left.x, left.y)
                lineTo(near.x, near.y)
                lineTo(right.x, right.y)
                close()
            }
        }

        outerStar = starPath(0f, 0f, radius, valley)
        innerStar = starPath(0f, 0f, radius * 0.93f, valley * 0.93f)
        octagon = polygonPath(8, inner, 0f)
        innerOctagon = polygonPath(8, inner * 0.62f, PI.toFloat() / 8f)
        core = starPath(0f, 0f, inner * 0.38f, inner * 0.22f)
        gapStar = starPath(cell * 0.5f, cell * 0.5f, cell * 0.10f, cell * 0.058f)
    }
}

/** Keeps the built medallion alive across frames and recompositions. */
internal class GirihCache {
    private var cellPx = -1f
    private var motif: GirihMotif? = null

    internal fun motifFor(cellPx: Float): GirihMotif {
        val cached = motif
        if (cached != null && this.cellPx == cellPx) return cached
        val built = GirihMotif(cellPx)
        this.cellPx = cellPx
        motif = built
        return built
    }
}

private fun polar(r: Float, angle: Float): Offset =
    Offset(r * cos(angle), r * sin(angle))

/** An n-gon whose vertices sit on [radius], first one at [rotation]. */
private fun polygonPath(sides: Int, radius: Float, rotation: Float): Path = Path().apply {
    for (k in 0 until sides) {
        val angle = rotation + k * 2f * PI.toFloat() / sides
        val point = polar(radius, angle)
        if (k == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
    }
    close()
}

/** Eight-pointed star, stroked as one closed path. */
private fun starPath(cx: Float, cy: Float, rOut: Float, rIn: Float): Path = Path().apply {
    for (i in 0 until 16) {
        val r = if (i % 2 == 0) rOut else rIn
        val angle = (PI * i / 8f - PI / 2f).toFloat()
        val px = cx + r * cos(angle)
        val py = cy + r * sin(angle)
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
    close()
}

/**
 * Draws the girih wall.
 *
 * Each cell stamps one khatam medallion: a sixteen-point corona, eight kite
 * facets, the star outline, an octagon core with its own small star, and a
 * star in the gap between medallions. The medallions are wider than their
 * cell, so the outlines cross and braid. A slow diagonal light keeps the
 * field from reading as flat wallpaper.
 */
internal fun DrawScope.drawGirihWall(
    tint: Color,
    baseAlpha: Float,
    cellPx: Float,
    cache: GirihCache
) {
    if (size.width <= 0f || size.height <= 0f || cellPx <= 0f) return

    val motif = cache.motifFor(cellPx)
    val w = size.width
    val h = size.height
    val columns = ceil(w / cellPx).toInt() + 2
    val rows = ceil(h / cellPx).toInt() + 2

    for (row in -2..rows) {
        for (column in -2..columns) {
            val cx = column * cellPx
            val cy = row * cellPx

            // Density fades from the top-end corner towards the bottom-start,
            // so the wall is present without ever fighting the content on top.
            val diagonal = ((cx / w) * 0.62f + (1f - cy / h) * 0.38f).coerceIn(0f, 1f)
            val lit = 0.62f + 0.38f * sin(column * 0.9f + row * 0.62f)
            val local = baseAlpha * (0.58f + diagonal * 1.75f) * lit
            if (local <= 0.004f) continue

            withTransform({ translate(left = cx, top = cy) }) {
                // Kite facets: alternating fills give the star its cut-stone
                // depth without any gradient work.
                for (k in 0 until 8) {
                    val facet = motif.facets[k]
                    drawPath(
                        path = facet,
                        color = tint.copy(alpha = local * if (k % 2 == 0) 0.055f else 0.022f),
                        style = Fill
                    )
                    drawPath(
                        path = facet,
                        color = tint.copy(alpha = local * 0.24f),
                        style = Stroke(width = motif.fine)
                    )
                }

                drawPath(
                    path = motif.corona,
                    color = tint.copy(alpha = local * 0.22f),
                    style = Stroke(width = motif.fine)
                )
                drawPath(
                    path = motif.outerStar,
                    color = tint.copy(alpha = local * 0.58f),
                    style = Stroke(width = motif.hair)
                )
                drawPath(
                    path = motif.innerStar,
                    color = tint.copy(alpha = local * 0.14f),
                    style = Stroke(width = motif.fine)
                )
                drawPath(
                    path = motif.octagon,
                    color = tint.copy(alpha = local * 0.50f),
                    style = Stroke(width = motif.hair)
                )
                drawPath(
                    path = motif.octagon,
                    color = tint.copy(alpha = local * 0.04f),
                    style = Fill
                )
                drawPath(
                    path = motif.innerOctagon,
                    color = tint.copy(alpha = local * 0.20f),
                    style = Stroke(width = motif.fine)
                )
                drawPath(
                    path = motif.core,
                    color = tint.copy(alpha = local * 0.16f),
                    style = Fill
                )
                drawPath(
                    path = motif.core,
                    color = tint.copy(alpha = local * 0.34f),
                    style = Stroke(width = motif.fine)
                )
                drawCircle(
                    color = tint.copy(alpha = local * 0.55f),
                    radius = motif.dot,
                    center = Offset.Zero
                )
                drawPath(
                    path = motif.gapStar,
                    color = tint.copy(alpha = local * 0.34f),
                    style = Stroke(width = motif.hair)
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

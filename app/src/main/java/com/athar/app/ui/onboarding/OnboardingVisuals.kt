package com.athar.app.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.components.drawArabicCalligraphy
import com.athar.app.ui.components.rememberCalligraphyTypeface
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryDeep
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import kotlinx.coroutines.delay
import kotlin.math.max

// ══════════════════════════════════════════════════════════════
// Setup flow — living surface
//
// The setup screens share one continuously animated surface: slow
// drifting accent auroras behind a breathing lattice, a progress
// rail that stretches as you advance, and content that settles in
// as it arrives. Everything is driven by infinite transitions, so
// the screen keeps breathing on every step without a visible loop.
// ══════════════════════════════════════════════════════════════

/**
 * The setup flow's shared atmosphere.
 *
 * A deep, almost-black stage: one narrow halo above, a second resting on the
 * bottom edge, a ghosted mihrab arch and a slowly turning rosette. The scrim
 * and vignette deliberately sit *over* the light so every theme — including
 * the pale olive one — stays dark and the content never floats on a blur.
 * Each step shifts the halo and the rosette so the pages feel related without
 * looking identical.
 */
@Composable
fun AuroraSetupBackdrop(
    modifier: Modifier = Modifier,
    step: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "setupAtmosphere")

    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(26000, easing = LinearEasing), RepeatMode.Reverse),
        label = "atmosphereDrift"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "atmosphereBreathe"
    )
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(96000, easing = LinearEasing), RepeatMode.Restart),
        label = "atmosphereSpin"
    )
    val typeface = rememberCalligraphyTypeface()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val focusX = w * (0.18f + 0.11f * (step % 4)) + w * 0.04f * drift

            // A single, restrained halo along the top edge.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AtharPrimary.copy(alpha = 0.10f * breathe),
                        Color.Transparent
                    ),
                    center = Offset(focusX, -h * 0.04f),
                    radius = w * 0.72f
                ),
                radius = w * 0.72f,
                center = Offset(focusX, -h * 0.04f)
            )

            // A darker pool of the theme colour at the very bottom.
            val lowerCenter = Offset(w * (0.82f - 0.12f * drift), h * 1.06f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AtharPrimaryDeep.copy(alpha = 0.24f * breathe),
                        Color.Transparent
                    ),
                    center = lowerCenter,
                    radius = w * 0.92f
                ),
                radius = w * 0.92f,
                center = lowerCenter
            )

            // The quiet mihrab that anchors the flow.
            val archW = w * 0.56f
            val archBottom = h * 0.99f
            val archTop = h * 0.52f
            val left = size.width / 2f - archW / 2f
            val right = size.width / 2f + archW / 2f
            val springY = archTop + (archBottom - archTop) * 0.34f
            val arch = Path().apply {
                moveTo(left, archBottom)
                lineTo(left, springY)
                cubicTo(left, springY - (archBottom - archTop) * 0.28f, left + archW * 0.22f, archTop, size.width / 2f, archTop)
                cubicTo(right - archW * 0.22f, archTop, right, springY - (archBottom - archTop) * 0.28f, right, springY)
                lineTo(right, archBottom)
            }
            drawPath(
                path = arch,
                color = AtharPrimary.copy(alpha = 0.11f + 0.035f * breathe),
                style = Stroke(width = 1.1.dp.toPx())
            )

            // A turning rosette in the upper-end corner, like a manuscript seal.
            val rosetteCenter = Offset(w * 0.88f, h * 0.13f)
            rotate(degrees = spin + step * 18f, pivot = rosetteCenter) {
                val r = 42.dp.toPx()
                drawCircle(
                    color = AtharPrimary.copy(alpha = 0.12f),
                    radius = r,
                    center = rosetteCenter,
                    style = Stroke(width = 1.dp.toPx())
                )
                for (deg in listOf(0f, 45f)) {
                    rotate(degrees = deg, pivot = rosetteCenter) {
                        drawRect(
                            color = AtharPrimary.copy(alpha = 0.10f),
                            topLeft = Offset(rosetteCenter.x - r * 0.70f, rosetteCenter.y - r * 0.70f),
                            size = Size(r * 1.4f, r * 1.4f),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }
            }

            // Hairline ornament rule under the status bar.
            val ruleY = h * 0.105f
            drawLine(
                color = AtharPrimary.copy(alpha = 0.16f),
                start = Offset(w * 0.16f, ruleY),
                end = Offset(w * 0.84f, ruleY),
                strokeWidth = 1.dp.toPx()
            )
            rotate(degrees = 45f, pivot = Offset(w / 2f, ruleY)) {
                drawRect(
                    color = AtharPrimary.copy(alpha = 0.28f),
                    topLeft = Offset(w / 2f - 2.5.dp.toPx(), ruleY - 2.5.dp.toPx()),
                    size = Size(5.dp.toPx(), 5.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // A whisper of calligraphy through the middle.
            drawArabicCalligraphy(
                tint = AtharPrimaryLight,
                baseAlpha = 0.016f,
                cellPx = 72.dp.toPx(),
                phase = 0f,
                typeface = typeface
            )

            // The scrim that keeps every theme genuinely dark.
            drawRect(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.48f to AtharBackground.copy(alpha = 0.42f),
                        1f to Color.Black.copy(alpha = 0.66f)
                    )
                )
            )

            // Edge vignette, so the light never spills into the page margins.
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        AtharBackground.copy(alpha = 0.72f)
                    ),
                    center = Offset(w / 2f, h * 0.46f),
                    radius = max(w, h) * 0.72f
                )
            )
        }

        IslamicPatternBackground(
            modifier = Modifier.matchParentSize(),
            alpha = 0.014f + 0.012f * breathe,
            animated = false
        )

        content()
    }
}

/**
 * Slim progress rail: the active step stretches into a pill and keeps a
 * soft pulse, completed steps stay lit, upcoming ones recede.
 */
@Composable
fun SetupStepRail(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "setupRailPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "railPulse"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(total) { index ->
            val isActive = index == current
            val isDone = index < current

            val width by animateDpAsState(
                targetValue = if (isActive) 28.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "railWidth$index"
            )
            val target = when {
                isActive -> AtharPrimaryLight
                isDone -> AtharPrimary.copy(alpha = 0.65f)
                else -> AtharCardBorder
            }
            val color by animateColorAsState(
                targetValue = target,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "railColor$index"
            )

            Box(
                modifier = Modifier
                    .size(width = width, height = 8.dp)
                    .graphicsLayer {
                        alpha = if (isActive) 0.72f + 0.28f * pulse else 1f
                    }
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

/**
 * Settles an element in on arrival: it lifts, fades and eases up to full scale,
 * offset by [index] so a screen arrives as a stagger rather than one solid block.
 * Elements sharing an index move together as a unit.
 */
@Composable
fun Modifier.revealOnEnter(
    index: Int,
    baseDelayMillis: Int = 60,
    stepDelayMillis: Int = 70
): Modifier {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((baseDelayMillis + index * stepDelayMillis).toLong())
        revealed = true
    }

    val progress by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "reveal$index"
    )

    return this.graphicsLayer {
        alpha = progress.coerceIn(0f, 1f)
        translationY = (1f - progress) * 38.dp.toPx()
        scaleX = 0.94f + 0.06f * progress
        scaleY = 0.94f + 0.06f * progress
    }
}

/**
 * Soft accent glow drawn behind an element, breathing on a slow cycle.
 * Apply outermost so the reveal animates the halo in with its content.
 */
@Composable
fun Modifier.breathingHalo(
    color: Color = AtharPrimary,
    radius: Dp = 132.dp,
    minAlpha: Float = 0.12f,
    maxAlpha: Float = 0.30f
): Modifier {
    val transition = rememberInfiniteTransition(label = "haloGlow")
    val scale by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(4600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "haloGlowScale"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(tween(4600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "haloGlowAlpha"
    )

    return this.drawBehind {
        val radiusPx = radius.toPx() * scale
        val middle = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = glowAlpha), Color.Transparent),
                center = middle,
                radius = radiusPx
            ),
            radius = radiusPx,
            center = middle
        )
    }
}

/**
 * Circular badge with two ripples expanding out of it and a dashed ring
 * rotating slowly — used for the notification and battery steps.
 */
@Composable
fun IconHalo(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 108.dp
) {
    val transition = rememberInfiniteTransition(label = "iconHalo")
    val rippleA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3400, easing = LinearEasing), RepeatMode.Restart),
        label = "rippleA"
    )
    val rippleB by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(3400, easing = LinearEasing, delayMillis = 1700),
            RepeatMode.Restart
        ),
        label = "rippleB"
    )
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(22000, easing = LinearEasing), RepeatMode.Restart),
        label = "haloSpin"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(3800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "haloBreathe"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val middle = center
            val maxRadius = this.size.minDimension / 2f

            listOf(rippleA, rippleB).forEach { progress ->
                drawCircle(
                    color = AtharPrimary.copy(alpha = 0.30f * (1f - progress)),
                    radius = maxRadius * (0.62f + 0.38f * progress),
                    center = middle,
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }

            val ringRadius = maxRadius * 0.88f
            rotate(degrees = spin, pivot = middle) {
                drawArc(
                    color = AtharPrimary.copy(alpha = 0.45f),
                    startAngle = 0f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = Offset(middle.x - ringRadius, middle.y - ringRadius),
                    size = Size(ringRadius * 2f, ringRadius * 2f),
                    style = Stroke(
                        width = 1.6.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 15f))
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .size(size * 0.64f)
                .graphicsLayer {
                    scaleX = breathe
                    scaleY = breathe
                }
                .background(AtharPrimary.copy(alpha = 0.14f), CircleShape)
                .border(1.dp, AtharPrimary.copy(alpha = 0.42f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(size * 0.32f)
            )
        }
    }
}

/**
 * Primary setup action with a slow light sweep travelling across it, so the
 * call to action reads as the brightest, most alive thing on the step.
 */
@Composable
fun SetupPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    shape: Shape = RoundedCornerShape(27.dp)
) {
    val transition = rememberInfiniteTransition(label = "setupButtonSheen")
    val sweep by transition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            tween(3600, easing = FastOutSlowInEasing, delayMillis = 700),
            RepeatMode.Restart
        ),
        label = "sheenSweep"
    )
    val container by animateColorAsState(
        targetValue = if (enabled) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "setupButtonContainer"
    )

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = container,
                contentColor = AtharTextOnPrimary,
                disabledContainerColor = AtharCardBorder,
                disabledContentColor = AtharTextSecondary.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = text,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }

        if (enabled) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val head = size.width * sweep
                val band = size.width * 0.20f
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        startX = head - band,
                        endX = head + band
                    )
                )
            }
        }
    }
}

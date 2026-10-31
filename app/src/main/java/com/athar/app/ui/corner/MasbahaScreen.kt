package com.athar.app.ui.corner

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.VolumeKeyRouter
import com.athar.app.data.formatDigits
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimaryMuted
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/** The three tasbih a masbaha is strung for, in the order they are shown. */
enum class Dhikr(val arabic: String, val english: String) {
    SUBHAN("سُبْحَانَ اللَّهِ", "Subhan Allah"),
    HAMD("الْحَمْدُ لِلَّهِ", "Alhamdu lillah"),
    AKBAR("اللَّهُ أَكْبَرُ", "Allahu Akbar")
}

private const val BEADS = 33
private const val CYCLE = 33

/**
 * A masbaha worked like the real thing: the beads travel one step for every
 * tasbih, a finger can turn them, and the volume keys count without looking.
 * Each dhikr keeps its own tally, so switching between them never mixes the
 * three.
 */
@Composable
fun MasbahaScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }
    val haptics = LocalHapticFeedback.current

    val storedCounts by prefs.masbahaCounts.collectAsState(initial = null)
    val hapticsOn by prefs.masbahaHaptics.collectAsState(initial = true)
    val soundOn by prefs.masbahaSound.collectAsState(initial = true)
    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)

    var counts by remember { mutableStateOf<List<Int>?>(null) }
    LaunchedEffect(storedCounts) {
        if (counts == null) counts = storedCounts
    }
    val live = counts ?: storedCounts ?: List(Dhikr.entries.size) { 0 }

    var selected by remember { mutableIntStateOf(0) }
    var celebration by remember { mutableIntStateOf(0) }

    val tone = remember { MasbahaTone() }
    DisposableEffect(Unit) {
        onDispose { tone.release() }
    }

    fun step(amount: Int) {
        if (amount <= 0) return
        val base = counts ?: live
        val before = base[selected]
        val after = before + amount
        counts = base.toMutableList().also { it[selected] = after }
        scope.launch { prefs.setMasbahaCount(selected, after) }
        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (soundOn) tone.tick()
        if (after / CYCLE > before / CYCLE) celebration++
    }

    fun reset() {
        val base = counts ?: live
        counts = base.toMutableList().also { it[selected] = 0 }
        scope.launch { prefs.setMasbahaCount(selected, 0) }
        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        if (soundOn) tone.tick()
    }

    val volumeStep = rememberUpdatedState { step(1) }
    DisposableEffect(Unit) {
        VolumeKeyRouter.bind { volumeStep.value() }
        onDispose { VolumeKeyRouter.unbind() }
    }

    val count = live[selected]
    val cycle = if (count == 0) 0 else ((count - 1) % CYCLE) + 1

    Box(modifier = Modifier.fillMaxSize().background(AtharBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBack
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = AtharTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.masbaha_title),
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = AtharTextPrimary
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(44.dp))
            }

            Text(
                text = stringResource(R.string.masbaha_subtitle),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(6.dp))

            DhikrWheel(
                selected = selected,
                onSelect = { selected = it },
                onStep = {
                    if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (soundOn) tone.tick()
                }
            )

            MasbahaRing(
                count = count,
                cycle = cycle,
                numberStyle = numberStyle,
                onCount = { amount -> step(amount) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlPill(
                    icon = Icons.Rounded.Vibration,
                    label = stringResource(R.string.masbaha_haptics),
                    active = hapticsOn,
                    onClick = {
                        val next = !hapticsOn
                        if (next) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { prefs.setMasbahaHaptics(next) }
                    },
                    modifier = Modifier.weight(1f)
                )
                ControlPill(
                    icon = if (soundOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                    label = stringResource(R.string.masbaha_sound),
                    active = soundOn,
                    onClick = {
                        val next = !soundOn
                        if (next) tone.tick()
                        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { prefs.setMasbahaSound(next) }
                    },
                    modifier = Modifier.weight(1f)
                )
                ControlPill(
                    icon = Icons.Rounded.Refresh,
                    label = stringResource(R.string.masbaha_reset),
                    active = false,
                    onClick = { reset() },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(R.string.masbaha_hint),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 10.5.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Spacer(Modifier.height(96.dp))
        }

        MasbahaSparks(
            trigger = celebration,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * The wheel of the three dhikr. Dragging it the way the iOS alarm picker turns
 * settles one dhikr under the other, ticking as it passes each one.
 */
@Composable
private fun DhikrWheel(
    selected: Int,
    onSelect: (Int) -> Unit,
    onStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 58.dp
    val itemPx = with(LocalDensity.current) { itemHeight.toPx() }
    var offset by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val settle by animateFloatAsState(
        targetValue = if (dragging) offset else 0f,
        animationSpec = if (dragging) snap() else spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "dhikrWheelSettle"
    )
    val selectRef = rememberUpdatedState(onSelect)
    val stepRef = rememberUpdatedState(onStep)
    val selectedRef = rememberUpdatedState(selected)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * 3)
            .clipToBounds()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    offset = settle
                    dragging = true
                    var accum = offset
                    var working = selectedRef.value
                    var lastY = down.position.y
                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val dy = change.position.y - lastY
                            lastY = change.position.y
                            accum += dy
                            while (accum > itemPx / 2f) {
                                if (working > 0) {
                                    working -= 1
                                    accum -= itemPx
                                    stepRef.value()
                                    selectRef.value(working)
                                } else {
                                    accum = itemPx / 2f
                                    break
                                }
                            }
                            while (accum < -itemPx / 2f) {
                                if (working < Dhikr.entries.lastIndex) {
                                    working += 1
                                    accum += itemPx
                                    stepRef.value()
                                    selectRef.value(working)
                                } else {
                                    accum = -itemPx / 2f
                                    break
                                }
                            }
                            offset = accum
                            change.consume()
                        }
                    } finally {
                        dragging = false
                    }
                }
            }
    ) {
        for (i in Dhikr.entries.indices) {
            val dy = (i - selected) * itemPx + settle
            val closeness = (1f - abs(dy) / itemPx).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset { IntOffset(0, dy.roundToInt()) }
                    .height(itemHeight)
                    .graphicsLayer {
                        val scale = 0.80f + 0.28f * closeness
                        scaleX = scale
                        scaleY = scale
                        alpha = 0.28f + 0.72f * closeness
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Dhikr.entries[i].arabic,
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        color = if (i == selected) AtharTextPrimary else AtharTextSecondary,
                        maxLines = 1
                    )
                    Text(
                        text = Dhikr.entries[i].english,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.5.sp,
                        color = if (i == selected) AtharPrimaryLight else AtharTextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun MasbahaRing(
    count: Int,
    cycle: Int,
    numberStyle: NumberStylePreference,
    onCount: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stepRad = (2f * PI / BEADS).toFloat()
    val halfTurn = PI.toFloat()
    val fullTurn = (2 * PI).toFloat()
    val rotation by animateFloatAsState(
        targetValue = -count * stepRad,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 220f),
        label = "masbahaRotation"
    )
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            pulse.snapTo(0.965f)
            pulse.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 380f))
        }
    }
    val transition = rememberInfiniteTransition(label = "masbahaBreathe")
    val breathe by transition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.012f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masbahaBreathe"
    )
    val countRef = rememberUpdatedState(onCount)

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = breathe * pulse.value
                    scaleY = breathe * pulse.value
                }
                .pointerInput(BEADS) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val center = Offset(size.width / 2f, size.height / 2f)
                        var last = angleOf(down.position, center)
                        var accum = 0f
                        var travelled = 0f
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val angle = angleOf(change.position, center)
                            var delta = angle - last
                            if (delta > halfTurn) delta -= fullTurn
                            if (delta < -halfTurn) delta += fullTurn
                            last = angle
                            accum += delta
                            travelled += abs(delta)
                            val steps = (accum / stepRad).toInt()
                            if (steps != 0) {
                                accum -= steps * stepRad
                                countRef.value(abs(steps))
                            }
                            change.consume()
                        }
                        if (travelled < 0.03f) countRef.value(1)
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension * 0.35f
            val beadRadius = (2f * PI * radius / BEADS).toFloat() * 0.40f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AtharPrimary.copy(alpha = 0.12f), Color.Transparent),
                    center = center,
                    radius = radius * 1.9f
                ),
                radius = radius * 1.9f,
                center = center
            )

            drawCircle(
                color = AtharPrimaryMuted.copy(alpha = 0.35f),
                radius = radius,
                center = center,
                style = Stroke(width = 1.4.dp.toPx())
            )
            drawCircle(
                color = AtharCardBorder.copy(alpha = 0.55f),
                radius = radius * 0.72f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            if (cycle > 0) {
                val ring = radius * 0.86f
                drawArc(
                    color = AtharPrimaryLight.copy(alpha = 0.55f),
                    startAngle = -90f,
                    sweepAngle = 360f * cycle / CYCLE,
                    useCenter = false,
                    topLeft = Offset(center.x - ring, center.y - ring),
                    size = Size(ring * 2f, ring * 2f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            for (i in 0 until BEADS) {
                val angle = (-halfTurn / 2f + i * stepRad + rotation).toDouble()
                val position = Offset(
                    x = center.x + cos(angle).toFloat() * radius,
                    y = center.y + sin(angle).toFloat() * radius
                )
                val active = i == ((count % BEADS) + BEADS) % BEADS

                if (active) {
                    drawCircle(
                        color = AtharPrimaryLight.copy(alpha = 0.26f),
                        radius = beadRadius * 2.1f,
                        center = position
                    )
                }
                drawCircle(
                    color = Color.Black.copy(alpha = 0.34f),
                    radius = beadRadius,
                    center = position + Offset(0f, beadRadius * 0.32f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AtharPrimaryLight,
                            if (active) AtharPrimaryLight else AtharPrimary,
                            AtharPrimary.copy(alpha = 0.92f)
                        ),
                        center = position - Offset(beadRadius * 0.34f, beadRadius * 0.34f),
                        radius = beadRadius * 1.7f
                    ),
                    radius = beadRadius,
                    center = position
                )
                drawCircle(
                    color = AtharCardBorder.copy(alpha = 0.45f),
                    radius = beadRadius,
                    center = position,
                    style = Stroke(width = 0.9.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.34f),
                    radius = beadRadius * 0.27f,
                    center = position - Offset(beadRadius * 0.3f, beadRadius * 0.3f)
                )
            }

            val imam = Offset(center.x, center.y + radius + beadRadius * 2.4f)
            drawLine(
                color = AtharPrimaryMuted.copy(alpha = 0.8f),
                start = Offset(center.x, center.y + radius),
                end = imam,
                strokeWidth = 1.4.dp.toPx()
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.34f),
                radius = beadRadius * 1.55f,
                center = imam + Offset(0f, beadRadius * 0.32f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AtharPrimaryLight, AtharPrimary, AtharPrimary.copy(alpha = 0.92f)),
                    center = imam - Offset(beadRadius * 0.4f, beadRadius * 0.4f),
                    radius = beadRadius * 2.2f
                ),
                radius = beadRadius * 1.5f,
                center = imam
            )
            drawCircle(
                color = AtharCardBorder.copy(alpha = 0.5f),
                radius = beadRadius * 1.5f,
                center = imam,
                style = Stroke(width = 1.dp.toPx())
            )

            val tassel = imam + Offset(0f, beadRadius * 1.4f)
            drawLine(
                color = AtharPrimaryMuted,
                start = tassel,
                end = tassel + Offset(0f, beadRadius * 1.8f),
                strokeWidth = 1.4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = AtharPrimaryLight,
                radius = beadRadius * 0.42f,
                center = tassel + Offset(0f, beadRadius * 2.1f)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDigits(count.toString(), numberStyle),
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 54.sp,
                color = AtharTextPrimary
            )
            Text(
                text = stringResource(
                    R.string.masbaha_cycle,
                    formatDigits(cycle.toString(), numberStyle),
                    formatDigits(CYCLE.toString(), numberStyle)
                ),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = AtharPrimaryLight
            )
        }
    }
}

@Composable
private fun ControlPill(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border by animateColorAsState(
        targetValue = if (active) AtharPrimary.copy(alpha = 0.75f) else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pillBorder"
    )
    val background by animateColorAsState(
        targetValue = if (active) AtharPrimary.copy(alpha = 0.14f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pillBackground"
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .background(background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) AtharPrimaryLight else AtharTextSecondary,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = label,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (active) AtharPrimaryLight else AtharTextSecondary,
            maxLines = 1
        )
    }
}

/**
 * A one-shot burst of sparks for a completed cycle. It draws over the page but
 * never covers it or takes a touch, so counting can carry on underneath.
 */
@Composable
private fun MasbahaSparks(trigger: Int, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }
    var running by remember { mutableStateOf(false) }
    LaunchedEffect(trigger) {
        if (trigger <= 0) return@LaunchedEffect
        running = true
        progress.snapTo(0f)
        progress.animateTo(1f, tween(1500, easing = LinearEasing))
        running = false
    }
    if (!running) return

    val seed = trigger
    Canvas(modifier = modifier) {
        val p = progress.value
        val center = Offset(size.width / 2f, size.height * 0.52f)
        val random = Random(seed)
        val gold = Color(0xFFE6D79A)

        repeat(46) { i ->
            val angle = random.nextFloat().toDouble() * 2.0 * PI
            val speed = 0.3f + random.nextFloat() * 0.7f
            val distance = size.minDimension * 0.08f + p * size.minDimension * 0.52f * speed
            val x = center.x + cos(angle).toFloat() * distance
            val y = center.y + sin(angle).toFloat() * distance * 0.92f
            val alpha = ((1f - p) * 0.9f).coerceIn(0f, 1f)
            val color = if (i % 3 == 0) gold else AtharPrimaryLight
            val length = 2.4f + (i % 4) * 1.5f

            drawCircle(color = color, radius = 1.2f + (i % 3), center = Offset(x, y), alpha = alpha)
            drawLine(
                color = color,
                start = Offset(x - length, y),
                end = Offset(x + length, y),
                strokeWidth = 1f,
                alpha = alpha
            )
            drawLine(
                color = color,
                start = Offset(x, y - length),
                end = Offset(x, y + length),
                strokeWidth = 1f,
                alpha = alpha
            )
        }

        drawCircle(
            color = AtharPrimaryLight.copy(alpha = (1f - p) * 0.35f),
            radius = size.minDimension * 0.10f + p * size.minDimension * 0.34f,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

private fun angleOf(point: Offset, center: Offset): Float =
    atan2(point.y - center.y, point.x - center.x)

/** A short, soft bead click; the whole masbaha shares one generator. */
private class MasbahaTone {
    private var generator: ToneGenerator? = runCatching {
        ToneGenerator(AudioManager.STREAM_MUSIC, 26)
    }.getOrNull()

    fun tick() {
        runCatching { generator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35) }
    }

    fun release() {
        runCatching { generator?.release() }
        generator = null
    }
}

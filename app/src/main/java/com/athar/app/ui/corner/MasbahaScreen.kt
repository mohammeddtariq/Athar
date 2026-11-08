package com.athar.app.ui.corner

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
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
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.VolumeKeyRouter
import com.athar.app.data.formatDigits
import com.athar.app.ui.theme.AtharAccentInk
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * The three tasbih a masbaha is strung for, each paired with a short ayah on
 * the greatness of Allah. Both the name and the ayah are carried in the two
 * languages so the screen can stay purely in whichever one the app is set to.
 */
enum class Dhikr(
    val arabic: String,
    val english: String,
    val arabicQuote: String,
    val englishQuote: String
) {
    SUBHAN(
        "سُبْحَانَ اللَّهِ",
        "Subhan Allah",
        "لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        "To Him belongs all in the heavens and the earth — the Most High, the Most Great."
    ),
    HAMD(
        "الْحَمْدُ لِلَّهِ",
        "Alhamdu lillah",
        "وَمَا بِكُم مِّن نِّعْمَةٍ فَمِنَ اللَّهِ",
        "Whatever blessing you have is from Allah."
    ),
    AKBAR(
        "اللَّهُ أَكْبَرُ",
        "Allahu Akbar",
        "وَهُوَ الْكَبِيرُ الْمُتَعَالِ",
        "He is the Most Great, the Most High."
    )
}

private const val BEADS = 33
private const val CYCLE = 33

/**
 * A masbaha strung the whole width of the page: thirty-three white beads on a
 * gently wrinkled string, floating and breathing in place. A finger turns the
 * beads, a tap counts one, and the volume keys count without looking. Each
 * dhikr keeps its own tally, so switching between them never mixes the three.
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
    val language by prefs.selectedLanguage.collectAsState(initial = "ar")
    val isArabic = language == "ar"

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        // Short screens get a tighter wheel so the masbaha keeps as much room as
        // possible; tall ones give the dhikr names their full size.
        val wheelItemHeight = (maxHeight * 0.084f).coerceIn(60.dp, 76.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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
                        fontSize = 25.sp,
                        color = AtharTextPrimary
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(44.dp))
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
                        .padding(top = 1.dp)
                )

                Spacer(Modifier.height(4.dp))

                DhikrWheel(
                    selected = selected,
                    isArabic = isArabic,
                    itemHeight = wheelItemHeight,
                    onSelect = { selected = it },
                    onStep = {
                        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (soundOn) tone.tick()
                    }
                )
            }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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

            Spacer(Modifier.height(88.dp))
        }

        MasbahaSparks(
            trigger = celebration,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * The wheel of the three dhikr. Dragging it the way the iOS alarm picker turns
 * settles one dhikr under the other, ticking as it passes each one. The chosen
 * dhikr carries its ayah underneath, in the language the app is set to.
 */
@Composable
private fun DhikrWheel(
    selected: Int,
    isArabic: Boolean,
    itemHeight: Dp,
    onSelect: (Int) -> Unit,
    onStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemPx = with(LocalDensity.current) { itemHeight.toPx() }
    val nameSize = if (itemHeight >= 70.dp) 24.sp else 20.sp
    val quoteSize = if (itemHeight >= 70.dp) 11.sp else 9.5.sp
    val quoteLines = if (itemHeight >= 70.dp) 2 else 1

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
            .pointerInput(itemPx) {
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
            val dhikr = Dhikr.entries[i]
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset { IntOffset(0, dy.roundToInt()) }
                    .height(itemHeight)
                    .graphicsLayer {
                        val scale = 0.82f + 0.26f * closeness
                        scaleX = scale
                        scaleY = scale
                        alpha = 0.26f + 0.74f * closeness
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isArabic) dhikr.arabic else dhikr.english,
                        fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = nameSize,
                        color = if (i == selected) AtharTextPrimary else AtharTextSecondary,
                        maxLines = 1
                    )
                    if (closeness > 0.55f) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) dhikr.arabicQuote else dhikr.englishQuote,
                            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = quoteSize,
                            lineHeight = if (isArabic) 18.sp else 14.sp,
                            color = AtharAccentInk.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            maxLines = quoteLines,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * The masbaha itself: a ring of white pearl beads on a wrinkled string that
 * fills the space it is given, drifting and breathing as it floats. The tally
 * sits in the empty middle, and the active bead glows.
 */
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
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 170f),
        label = "masbahaRotation"
    )
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            pulse.snapTo(0.972f)
            pulse.animateTo(1f, spring(dampingRatio = 0.32f, stiffness = 360f))
        }
    }

    val drift = rememberInfiniteTransition(label = "masbahaFloat")
    val driftX by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masbahaDriftX"
    )
    val driftY by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masbahaDriftY"
    )
    val sway by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masbahaSway"
    )
    val breathe by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = FastOutSlowInEasing),
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
                    translationX = driftX * 5.dp.toPx()
                    translationY = driftY * 7.dp.toPx()
                    rotationZ = sway * 1.2f
                    val scale = (0.997f + 0.007f * breathe) * pulse.value
                    scaleX = scale
                    scaleY = scale
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
            val minDim = size.minDimension
            val beadRadius = minDim * 0.0435f
            val radius = minDim * 0.5f - beadRadius * 1.05f

            // A soft halo so the white sibha lifts off the dark page.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.07f), Color.Transparent),
                    center = center,
                    radius = radius * 1.35f
                ),
                radius = radius * 1.35f,
                center = center
            )

            val points = ArrayList<Offset>(BEADS)
            val radii = FloatArray(BEADS)
            for (i in 0 until BEADS) {
                val angle = (-halfTurn / 2f + i * stepRad + rotation).toDouble()
                // A little unevenness in radius and bead size, so the loop reads
                // as hand-strung rather than machine-perfect.
                val wobble = 1f +
                    0.013f * sin(i * 2.3f + breathe * 1.4f) +
                    0.008f * cos(i * 1.7f)
                val r = radius * wobble
                points += Offset(
                    x = center.x + cos(angle).toFloat() * r,
                    y = center.y + sin(angle).toFloat() * r
                )
                radii[i] = beadRadius * (0.93f + 0.09f * sin(i * 1.9f + 1.1f))
            }

            // The string, drawn as one closed curve through every bead. Each
            // span bows a hair off the straight line, alternating sides, so the
            // thread reads as hand-strung and slightly wrinkled.
            val string = Path()
            string.moveTo(points[0].x, points[0].y)
            for (i in 0 until BEADS) {
                val from = points[i]
                val to = points[(i + 1) % BEADS]
                val dx = to.x - from.x
                val dy = to.y - from.y
                val len = kotlin.math.hypot(dx, dy).coerceAtLeast(0.001f)
                val wrinkle = beadRadius * 0.16f * sin(i * 2.7f + 0.8f)
                val control = Offset(
                    x = (from.x + to.x) / 2f - dy / len * wrinkle,
                    y = (from.y + to.y) / 2f + dx / len * wrinkle
                )
                string.quadraticBezierTo(control.x, control.y, to.x, to.y)
            }
            string.close()
            drawPath(
                path = string,
                color = Color.Black.copy(alpha = 0.38f),
                style = Stroke(width = 3.4.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = string,
                color = Color(0xFFF3EFE7).copy(alpha = 0.9f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )

            val active = ((count % BEADS) + BEADS) % BEADS
            for (i in 0 until BEADS) {
                val point = points[i]
                val br = radii[i]
                if (i == active) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.40f), Color.Transparent),
                            center = point,
                            radius = br * 2.4f
                        ),
                        radius = br * 2.4f,
                        center = point
                    )
                }
                // Contact shadow.
                drawCircle(
                    color = Color.Black.copy(alpha = 0.30f),
                    radius = br * 0.98f,
                    center = point + Offset(0f, br * 0.24f)
                )
                // Pearl body.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8F4EC),
                            Color(0xFFDED6C7)
                        ),
                        center = point - Offset(br * 0.34f, br * 0.36f),
                        radius = br * 1.55f
                    ),
                    radius = br,
                    center = point
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = br,
                    center = point,
                    style = Stroke(width = 0.7.dp.toPx())
                )
                // Specular glint.
                drawCircle(
                    color = Color.White.copy(alpha = 0.92f),
                    radius = br * 0.22f,
                    center = point - Offset(br * 0.30f, br * 0.32f)
                )
            }
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
                color = AtharAccentInk.copy(alpha = 0.9f)
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
            val color = if (i % 3 == 0) gold else Color.White
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
            color = Color.White.copy(alpha = (1f - p) * 0.30f),
            radius = size.minDimension * 0.10f + p * size.minDimension * 0.34f,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

private fun angleOf(point: Offset, center: Offset): Float =
    atan2(point.y - center.y, point.x - center.x)

/**
 * The bead tick: a very short, dry click built from a fast-decaying high tone
 * and a noise transient, closer to a clock tick than a beep. One static
 * [AudioTrack] is written once and replayed from the top on every tap.
 */
private class MasbahaTone {
    private val track: AudioTrack? = runCatching {
        val samples = buildTick()
        val created = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        created.write(samples, 0, samples.size)
        created
    }.getOrNull()

    fun tick() {
        val audio = track ?: return
        runCatching {
            if (audio.playState == AudioTrack.PLAYSTATE_PLAYING) audio.stop()
            audio.reloadStaticData()
            audio.play()
        }
    }

    fun release() {
        runCatching { track?.release() }
    }

    private companion object {
        const val SAMPLE_RATE = 44100

        fun buildTick(): ShortArray {
            val length = (SAMPLE_RATE * 0.028f).toInt()
            val samples = ShortArray(length)
            val random = Random(7)
            for (i in 0 until length) {
                val t = i / SAMPLE_RATE.toFloat()
                val body = sin(2.0 * PI * 2600.0 * t) * 0.55 +
                    sin(2.0 * PI * 3900.0 * t) * 0.22
                val bodyEnv = exp(-t * 170f)
                val click = (random.nextFloat() * 2f - 1f) * exp(-t * 520f) * 0.35f
                val warm = sin(2.0 * PI * 880.0 * t) * exp(-t * 95f) * 0.10
                val value = (body * bodyEnv + click + warm) * 0.30f
                samples[i] = (value * Short.MAX_VALUE)
                    .toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
            return samples
        }
    }
}

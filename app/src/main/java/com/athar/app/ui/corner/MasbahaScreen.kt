package com.athar.app.ui.corner

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Vibration
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
import com.athar.app.ui.theme.AtharPrimaryDeep
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
 * The three tasbih a masbaha is strung for, in the order the wheel presents
 * them. Each carries a short ayah in both languages so the screen can stay
 * purely in whichever language the app is set to.
 *
 * [storeIndex] keeps the persisted tally slots stable even though the display
 * order changed; counts are stored Subhan, Hamd, Akbar.
 */
enum class Dhikr(
    val storeIndex: Int,
    val arabic: String,
    val english: String,
    val arabicQuote: String,
    val englishQuote: String
) {
    HAMD(
        1,
        "الْحَمْدُ لِلَّهِ",
        "Alhamdulillah",
        "وَمَا بِكُم مِّن نِّعْمَةٍ فَمِنَ اللَّهِ",
        "Whatever blessing you have is from Allah."
    ),
    AKBAR(
        2,
        "اللَّهُ أَكْبَرُ",
        "Allahu Akbar",
        "وَهُوَ الْكَبِيرُ الْمُتَعَالِ",
        "He is the Most Great, the Most High."
    ),
    SUBHAN(
        0,
        "سُبْحَانَ اللَّهِ",
        "Subhan Allah",
        "لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        "To Him belongs all in the heavens and the earth — the Most High, the Most Great."
    )
}

private const val BEADS = 33
private const val CYCLE = 33

/**
 * Al-Yaqut — the ruby. A small, hand-strung masbaha floating on the page: a
 * loop of thirty-three ivory pearls with brass dividers every eleven, an imam
 * bead and a short tassel hanging from the bottom. The tally sits in the empty
 * middle, the chosen dhikr sits above it, and everything else is kept clear of
 * the floating dock so nothing collides with the navigation bar.
 */
@Composable
fun MasbahaScreen(
    onBack: () -> Unit = {},
    bottomInset: Dp = 104.dp
) {
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

    val tick = remember { MasbahaTick() }
    DisposableEffect(Unit) {
        onDispose { tick.release() }
    }

    fun step(amount: Int) {
        if (amount <= 0) return
        val slot = Dhikr.entries[selected].storeIndex
        val base = counts ?: live
        val before = base[slot]
        val after = before + amount
        counts = base.toMutableList().also { it[slot] = after }
        scope.launch { prefs.setMasbahaCount(slot, after) }
        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (soundOn) tick.tick()
        if (after / CYCLE > before / CYCLE) celebration++
    }

    fun reset() {
        val slot = Dhikr.entries[selected].storeIndex
        val base = counts ?: live
        counts = base.toMutableList().also { it[slot] = 0 }
        scope.launch { prefs.setMasbahaCount(slot, 0) }
        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        if (soundOn) tick.tick()
    }

    val volumeStep = rememberUpdatedState { step(1) }
    DisposableEffect(Unit) {
        VolumeKeyRouter.bind { volumeStep.value() }
        onDispose { VolumeKeyRouter.unbind() }
    }

    val count = live[Dhikr.entries[selected].storeIndex]
    val cycle = if (count == 0) 0 else ((count - 1) % CYCLE) + 1

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        // Short screens get a tighter drum so the sibha keeps as much room as
        // possible; tall ones give the dhikr names their full size.
        val wheelItemHeight = (maxHeight * 0.066f).coerceIn(46.dp, 56.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            YaqutHeader(
                isArabic = isArabic,
                onBack = onBack,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(2.dp))

            DhikrWheel(
                selected = selected,
                isArabic = isArabic,
                itemHeight = wheelItemHeight,
                onSelect = {
                    if (it != selected) {
                        selected = it
                        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                },
                onStep = {
                    if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (soundOn) tick.tick()
                },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            DhikrAyah(selected = selected, isArabic = isArabic)

            // The sibha is the subject of the screen, so it keeps the full
            // width: no side padding, nothing sitting above it but the drum.
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val diameter = minOf(maxWidth, maxHeight).coerceIn(140.dp, 440.dp)
                SibhaRing(
                    count = count,
                    cycle = cycle,
                    numberStyle = numberStyle,
                    onCount = { amount -> step(amount) },
                    contentShift = diameter * 0.07f,
                    modifier = Modifier.size(diameter)
                )
            }

            Text(
                text = stringResource(R.string.masbaha_hint),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 9.5.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(6.dp))

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
                    icon = if (soundOn) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                    label = stringResource(R.string.masbaha_sound),
                    active = soundOn,
                    onClick = {
                        val next = !soundOn
                        if (next) tick.tick()
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

            Spacer(Modifier.height(bottomInset + 8.dp))
        }

        MasbahaSparks(
            trigger = celebration,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * The Al-Yaqut wordmark: only the name, in whichever language the app is set
 * to. No gem, no second script.
 */
@Composable
private fun YaqutHeader(
    isArabic: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
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
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = if (isArabic) 25.sp else 20.sp,
            letterSpacing = if (isArabic) 0.sp else 2.sp,
            color = AtharTextPrimary
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.size(44.dp))
    }
}

/**
 * The clock-like drum of the three dhikr. Dragging it the way an alarm picker
 * turns settles one dhikr under the other, ticking as it passes each one.
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
    val nameSize = if (itemHeight >= 52.dp) 24.sp else 21.sp

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
            .height(itemHeight * 2.7f)
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
                }
            }
        }
    }
}

/**
 * The ayah that belongs to whichever dhikr is settled in the drum, in the
 * language the app is set to. Two lines are always reserved so the sibha below
 * never jumps as the drum turns.
 */
@Composable
private fun DhikrAyah(selected: Int, isArabic: Boolean) {
    Crossfade(
        targetState = selected,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "dhikrAyah"
    ) { index ->
        val dhikr = Dhikr.entries[index]
        Text(
            text = if (isArabic) dhikr.arabicQuote else dhikr.englishQuote,
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = if (isArabic) 11.5.sp else 9.5.sp,
            lineHeight = if (isArabic) 18.sp else 12.5.sp,
            color = AtharAccentInk.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 2.dp)
        )
    }
}

/**
 * The masbaha itself. A loop of thirty-three pearls strung on a slightly
 * wrinkled thread, with brass spacers every eleven, an imam bead and a short
 * tassel at the bottom. Dragging around the loop turns the beads one step per
 * bead passed; tapping counts one. The tally sits in the empty middle.
 */
@Composable
private fun SibhaRing(
    count: Int,
    cycle: Int,
    numberStyle: NumberStylePreference,
    onCount: (Int) -> Unit,
    contentShift: Dp,
    modifier: Modifier = Modifier
) {
    val stepRad = (2f * PI / BEADS).toFloat()
    val halfTurn = PI.toFloat()
    val fullTurn = (2 * PI).toFloat()
    val rotation by animateFloatAsState(
        targetValue = count * stepRad,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 170f),
        label = "sibhaRotation"
    )
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            pulse.snapTo(0.972f)
            pulse.animateTo(1f, spring(dampingRatio = 0.32f, stiffness = 360f))
        }
    }

    val drift = rememberInfiniteTransition(label = "sibhaFloat")
    val driftX by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sibhaDriftX"
    )
    val driftY by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sibhaDriftY"
    )
    val sway by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sibhaSway"
    )
    val breathe by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sibhaBreathe"
    )
    val countRef = rememberUpdatedState(onCount)

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = driftX * 3.dp.toPx()
                    translationY = driftY * 4.dp.toPx()
                    rotationZ = sway * 0.9f
                    val scale = (0.996f + 0.008f * breathe) * pulse.value
                    scaleX = scale
                    scaleY = scale
                }
                .pointerInput(BEADS) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val center = Offset(size.width / 2f, size.height * 0.42f)
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
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val minDim = size.minDimension
            val beadRadius = minDim * 0.032f
            val radius = minDim * 0.40f

            // Soft halo so the pearls lift off the dark page.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AtharPrimaryLight.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.55f
                ),
                radius = radius * 1.55f,
                center = center
            )

            val points = ArrayList<Offset>(BEADS)
            val radii = FloatArray(BEADS)
            for (i in 0 until BEADS) {
                val angle = (-halfTurn / 2f + i * stepRad + rotation).toDouble()
                val wobble = 1f +
                    0.012f * sin(i * 2.3f + breathe * 1.4f) +
                    0.007f * cos(i * 1.7f)
                val r = radius * wobble
                points += Offset(
                    x = center.x + cos(angle).toFloat() * r,
                    y = center.y + sin(angle).toFloat() * r
                )
                radii[i] = beadRadius * (0.93f + 0.09f * sin(i * 1.9f + 1.1f))
            }

            // The thread, one closed curve through every pearl, bowing a hair
            // off the straight line so it reads as hand-strung.
            val string = Path()
            string.moveTo(points[0].x, points[0].y)
            for (i in 0 until BEADS) {
                val from = points[i]
                val to = points[(i + 1) % BEADS]
                val dx = to.x - from.x
                val dy = to.y - from.y
                val len = kotlin.math.hypot(dx, dy).coerceAtLeast(0.001f)
                val wrinkle = beadRadius * 0.15f * sin(i * 2.7f + 0.8f)
                val control = Offset(
                    x = (from.x + to.x) / 2f - dy / len * wrinkle,
                    y = (from.y + to.y) / 2f + dx / len * wrinkle
                )
                string.quadraticTo(control.x, control.y, to.x, to.y)
            }
            string.close()
            drawPath(
                path = string,
                color = Color.Black.copy(alpha = 0.40f),
                style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = string,
                color = Color(0xFFF3EFE7).copy(alpha = 0.9f),
                style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
            )

            // The imam bead and tassel hang from the bottom of the loop.
            drawImamAndTassel(
                center = center,
                radius = radius,
                beadRadius = beadRadius
            )

            val active = ((-count % BEADS) + BEADS) % BEADS
            for (i in 0 until BEADS) {
                val point = points[i]
                val br = radii[i]
                val spacer = i == 11 || i == 22
                if (i == active) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AtharPrimaryLight.copy(alpha = 0.55f),
                                Color.Transparent
                            ),
                            center = point,
                            radius = br * 2.8f
                        ),
                        radius = br * 2.8f,
                        center = point
                    )
                }
                // Contact shadow.
                drawCircle(
                    color = Color.Black.copy(alpha = 0.30f),
                    radius = br * 0.98f,
                    center = point + Offset(0f, br * 0.24f)
                )
                if (spacer) {
                    // A small brass divider after every eleven pearls.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AtharPrimaryLight,
                                AtharPrimary,
                                AtharPrimaryDeep
                            ),
                            center = point - Offset(br * 0.3f, br * 0.32f),
                            radius = br * 1.5f
                        ),
                        radius = br * 0.78f,
                        center = point
                    )
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.28f),
                        radius = br * 0.78f,
                        center = point,
                        style = Stroke(width = 0.7.dp.toPx())
                    )
                } else {
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
                    drawCircle(
                        color = Color.White.copy(alpha = 0.92f),
                        radius = br * 0.22f,
                        center = point - Offset(br * 0.30f, br * 0.32f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -contentShift),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDigits(count.toString(), numberStyle),
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 56.sp,
                lineHeight = 60.sp,
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
                fontSize = 10.sp,
                color = AtharTextSecondary.copy(alpha = 0.85f)
            )
        }
    }
}

/** The imam bead, its cap and a short fan of tassel threads. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawImamAndTassel(
    center: Offset,
    radius: Float,
    beadRadius: Float
) {
    val topY = center.y + radius + beadRadius * 0.6f
    val imamWidth = beadRadius * 1.35f
    val imamHeight = beadRadius * 1.8f
    val imamCenter = Offset(center.x, topY + imamHeight / 2f)

    // The thread that carries the loop down into the imam bead.
    drawLine(
        color = Color(0xFFF3EFE7).copy(alpha = 0.9f),
        start = Offset(center.x, center.y + radius),
        end = Offset(center.x, topY),
        strokeWidth = 1.4.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Contact shadow under the imam.
    drawOval(
        color = Color.Black.copy(alpha = 0.32f),
        topLeft = Offset(
            imamCenter.x - imamWidth / 2f,
            imamCenter.y - imamHeight / 2f + beadRadius * 0.18f
        ),
        size = androidx.compose.ui.geometry.Size(imamWidth, imamHeight)
    )
    drawOval(
        brush = Brush.linearGradient(
            colors = listOf(
                AtharPrimaryLight,
                AtharPrimary,
                AtharPrimaryDeep
            ),
            start = Offset(imamCenter.x - imamWidth / 2f, imamCenter.y - imamHeight / 2f),
            end = Offset(imamCenter.x + imamWidth / 2f, imamCenter.y + imamHeight / 2f)
        ),
        topLeft = Offset(imamCenter.x - imamWidth / 2f, imamCenter.y - imamHeight / 2f),
        size = androidx.compose.ui.geometry.Size(imamWidth, imamHeight)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.35f),
        topLeft = Offset(
            imamCenter.x - imamWidth * 0.26f,
            imamCenter.y - imamHeight * 0.30f
        ),
        size = androidx.compose.ui.geometry.Size(imamWidth * 0.24f, imamHeight * 0.30f)
    )

    val capY = imamCenter.y + imamHeight / 2f
    drawCircle(
        color = AtharPrimaryLight.copy(alpha = 0.9f),
        radius = beadRadius * 0.42f,
        center = Offset(center.x, capY)
    )
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = beadRadius * 0.42f,
        center = Offset(center.x, capY),
        style = Stroke(width = 0.7.dp.toPx())
    )

    // Tassel: a knot and five threads fanning gently.
    val knot = Offset(center.x, capY + beadRadius * 0.55f)
    drawCircle(
        color = AtharPrimaryDeep,
        radius = beadRadius * 0.55f,
        center = knot
    )
    drawCircle(
        color = AtharPrimary.copy(alpha = 0.9f),
        radius = beadRadius * 0.55f,
        center = knot,
        style = Stroke(width = 1.dp.toPx())
    )

    val threadTop = knot.y + beadRadius * 0.25f
    val threadLength = beadRadius * 2.0f
    for (i in 0..4) {
        val spread = (i - 2) / 2f
        val endX = center.x + spread * beadRadius * 1.4f
        val endY = threadTop + threadLength * (1f - abs(spread) * 0.18f)
        val path = Path().apply {
            moveTo(center.x, threadTop)
            quadraticTo(
                center.x + spread * beadRadius * 0.5f,
                threadTop + threadLength * 0.55f,
                endX,
                endY
            )
        }
        drawPath(
            path = path,
            color = AtharPrimary.copy(alpha = 0.82f),
            style = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round)
        )
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
        val center = Offset(size.width / 2f, size.height * 0.56f)
        val random = Random(seed)
        val gold = AtharPrimaryLight

        repeat(46) { i ->
            val angle = random.nextFloat().toDouble() * 2.0 * PI
            val speed = 0.3f + random.nextFloat() * 0.7f
            val distance = size.minDimension * 0.06f + p * size.minDimension * 0.36f * speed
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
            radius = size.minDimension * 0.08f + p * size.minDimension * 0.26f,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

private fun angleOf(point: Offset, center: Offset): Float =
    atan2(point.y - center.y, point.x - center.x)

/**
 * The bead tick: a dry, mechanical click and nothing else. A tiny noise
 * transient with a very short high ping, the way a timer clicks — no warm
 * body, no beep. One static [AudioTrack] is written once and replayed.
 */
private class MasbahaTick {
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
            val length = (SAMPLE_RATE * 0.011f).toInt()
            val samples = ShortArray(length)
            val random = Random(7)
            var previousNoise = 0f
            for (i in 0 until length) {
                val t = i / SAMPLE_RATE.toFloat()
                val raw = random.nextFloat() * 2f - 1f
                // One-pole high-pass, so the transient is dry and clicky.
                val noise = raw - previousNoise * 0.35f
                previousNoise = raw
                val transient = noise * exp(-t * 900f) * 0.62f
                val ping = sin(2.0 * PI * 3200.0 * t) * exp(-t * 620f) * 0.16f
                val value = (transient + ping) * 0.62f
                samples[i] = (value * Short.MAX_VALUE)
                    .toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
            }
            return samples
        }
    }
}

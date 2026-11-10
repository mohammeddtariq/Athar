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
import androidx.compose.material.icons.rounded.SwapHoriz
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
import androidx.compose.ui.graphics.PathMeasure
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
import com.athar.app.notifications.MasbahaNotificationManager
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
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

/** The hanging strand carries the full thirty-three, not a shorthand. */
private const val STRAND_BEADS = BEADS

/** How far past each edge the strand runs, so it reads as endless. */
private const val STRAND_OVERHANG = 0.11f

/** The faces of an ivory pearl. */
private val StrandPearlLight = Color(0xFFFFFFFF)
private val StrandPearlMid = Color(0xFFF6F2EA)
private val StrandPearlDeep = Color(0xFFDCD5C8)

/** The two sibha the screen can wear; the choice is kept in preferences. */
private enum class SibhaStyle(val id: String) {
    CLASSIC("classic"),
    STRAND("strand");

    companion object {
        fun fromId(id: String?): SibhaStyle = entries.firstOrNull { it.id == id } ?: CLASSIC

        fun next(current: SibhaStyle): SibhaStyle =
            if (current == CLASSIC) STRAND else CLASSIC
    }
}

// The loop is a teardrop: round at the top, gathering to a point at the
// bottom where the imam bead and its tassel hang.
private const val LOOP_TOP = 0.05f
private const val LOOP_BOTTOM = 0.762f
private const val LOOP_CENTER_Y = (LOOP_TOP + LOOP_BOTTOM) / 2f

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
    val storedStyle by prefs.masbahaStyle.collectAsState(initial = SibhaStyle.CLASSIC.id)
    val storedSelected by prefs.masbahaSelected.collectAsState(initial = 0)
    val sibhaStyle = SibhaStyle.fromId(storedStyle)
    val hapticsOn by prefs.masbahaHaptics.collectAsState(initial = true)
    val soundOn by prefs.masbahaSound.collectAsState(initial = true)
    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)
    val language by prefs.selectedLanguage.collectAsState(initial = "ar")
    val isArabic = language == "ar"

    var counts by remember { mutableStateOf<List<Int>?>(null) }
    LaunchedEffect(storedCounts) {
        val incoming = storedCounts ?: return@LaunchedEffect
        // A burst of taps writes several absolute values; adopting each echo
        // would drag the tally backwards between writes. Wait for the writes
        // to settle, then take whatever the store actually holds — which also
        // picks up counts made from the notification.
        delay(320)
        if (incoming != counts) counts = incoming
    }
    val live = counts ?: storedCounts ?: List(Dhikr.entries.size) { 0 }

    var selected by remember { mutableIntStateOf(storedSelected) }
    var celebration by remember { mutableIntStateOf(0) }

    // The notification can switch dhikr while the screen is open.
    LaunchedEffect(storedSelected) {
        if (selected != storedSelected) selected = storedSelected
    }

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
        scope.launch {
            prefs.setMasbahaCount(slot, after)
            MasbahaNotificationManager.update(context)
        }
        if (hapticsOn) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (soundOn) tick.tick()
        if (after / CYCLE > before / CYCLE) celebration++
    }

    fun reset() {
        val slot = Dhikr.entries[selected].storeIndex
        val base = counts ?: live
        counts = base.toMutableList().also { it[slot] = 0 }
        scope.launch {
            prefs.setMasbahaCount(slot, 0)
            MasbahaNotificationManager.update(context)
        }
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
                onChangeStyle = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (soundOn) tick.tick()
                    scope.launch { prefs.setMasbahaStyle(SibhaStyle.next(sibhaStyle).id) }
                },
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
                        scope.launch {
                            prefs.setMasbahaSelected(it)
                            MasbahaNotificationManager.update(context)
                        }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Crossfade(
                    targetState = sibhaStyle,
                    animationSpec = tween(320, easing = FastOutSlowInEasing),
                    label = "sibhaStyle"
                ) { active ->
                    when (active) {
                        SibhaStyle.CLASSIC -> SibhaRing(
                            count = count,
                            cycle = cycle,
                            numberStyle = numberStyle,
                            onCount = { amount -> step(amount) },
                            modifier = Modifier.fillMaxSize()
                        )
                        SibhaStyle.STRAND -> SibhaStrand(
                            count = count,
                            cycle = cycle,
                            numberStyle = numberStyle,
                            onCount = { amount -> step(amount) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
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
    onChangeStyle: () -> Unit,
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
        // Swaps the sibha for the other one the screen knows.
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onChangeStyle
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.SwapHoriz,
                contentDescription = stringResource(R.string.masbaha_change_style),
                tint = AtharPrimaryLight.copy(alpha = 0.85f),
                modifier = Modifier.size(21.dp)
            )
        }
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
    // The neighbouring dhikr sit closer than a full drum step would put them:
    // a clock face with the selected name under the eye, not a gap around it.
    val stepPx = itemPx * 0.74f
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
            .height(itemHeight * 2.25f)
            .clipToBounds()
            .pointerInput(stepPx) {
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
                            while (accum > stepPx / 2f) {
                                if (working > 0) {
                                    working -= 1
                                    accum -= stepPx
                                    stepRef.value()
                                    selectRef.value(working)
                                } else {
                                    accum = stepPx / 2f
                                    break
                                }
                            }
                            while (accum < -stepPx / 2f) {
                                if (working < Dhikr.entries.lastIndex) {
                                    working += 1
                                    accum += stepPx
                                    stepRef.value()
                                    selectRef.value(working)
                                } else {
                                    accum = -stepPx / 2f
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
            val dy = (i - selected) * stepPx + settle
            val closeness = (1f - abs(dy) / stepPx).coerceIn(0f, 1f)
            val dhikr = Dhikr.entries[i]
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset { IntOffset(0, dy.roundToInt()) }
                    .height(itemHeight)
                    .graphicsLayer {
                        val scale = 0.86f + 0.22f * closeness
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
 * The masbaha itself: a teardrop loop of thirty-three ivory pearls that is
 * round at the top and gathers to a point at the bottom, where the imam bead
 * and its tassel hang. The loop never spins; the lit pearl walks around it as
 * the tally climbs, and the count sits in the empty middle. Dragging around
 * the loop counts one step per bead passed; tapping counts one.
 */
@Composable
private fun SibhaRing(
    count: Int,
    cycle: Int,
    numberStyle: NumberStylePreference,
    onCount: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            pulse.snapTo(0.976f)
            pulse.animateTo(1f, spring(dampingRatio = 0.34f, stiffness = 360f))
        }
    }

    val drift = rememberInfiniteTransition(label = "sibhaBreath")
    val breathe by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sibhaBreathe"
    )
    val countRef = rememberUpdatedState(onCount)

    BoxWithConstraints(modifier = modifier) {
        val readoutShift = maxHeight * (LOOP_CENTER_Y - 0.5f)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val scale = (0.998f + 0.004f * breathe) * pulse.value
                    scaleX = scale
                    scaleY = scale
                }
                .pointerInput(BEADS) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val loop = teardropPath(size.width.toFloat(), size.height.toFloat())
                        val measure = PathMeasure().apply { setPath(loop, false) }
                        val loopLength = measure.length.coerceAtLeast(1f)
                        val stepDistance = loopLength / BEADS
                        var last = measure.nearestDistance(down.position)
                        var accum = 0f
                        var travelled = 0f
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            val here = measure.nearestDistance(change.position)
                            var delta = here - last
                            if (delta > loopLength / 2f) delta -= loopLength
                            if (delta < -loopLength / 2f) delta += loopLength
                            last = here
                            accum += delta
                            travelled += abs(delta)
                            val steps = (accum / stepDistance).toInt()
                            if (steps != 0) {
                                accum -= steps * stepDistance
                                countRef.value(abs(steps))
                            }
                            change.consume()
                        }
                        if (travelled < loopLength * 0.02f) countRef.value(1)
                    }
                }
        ) {
            val loop = teardropPath(size.width, size.height)
            val measure = PathMeasure().apply { setPath(loop, false) }
            val loopLength = measure.length.coerceAtLeast(1f)
            val beadRadius = (loopLength / BEADS * 0.50f)
                .coerceIn(size.minDimension * 0.017f, size.minDimension * 0.066f)
            val tip = measure.getPosition(0f)

            // Soft halo so the pearls lift off the dark page.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AtharPrimaryLight.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height * LOOP_CENTER_Y),
                    radius = size.minDimension * 0.52f
                ),
                radius = size.minDimension * 0.52f,
                center = Offset(size.width / 2f, size.height * LOOP_CENTER_Y)
            )

            // The thread the pearls are strung on.
            drawPath(
                path = loop,
                color = Color.Black.copy(alpha = 0.42f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = loop,
                color = Color(0xFFF3EFE7).copy(alpha = 0.88f),
                style = Stroke(width = 1.3.dp.toPx(), cap = StrokeCap.Round)
            )

            // The imam bead and tassel hang from the point of the teardrop.
            drawImamAndTassel(anchor = tip, beadRadius = beadRadius)

            val points = ArrayList<Offset>(BEADS)
            for (i in 0 until BEADS) {
                // Half a step in, so no pearl sits on the point where the
                // imam thread leaves the loop.
                points += measure.getPosition((i + 0.5f) * loopLength / BEADS)
            }
            val active = if (cycle <= 0) -1 else (cycle - 1) % BEADS

            for (i in 0 until BEADS) {
                val point = points[i]
                val br = beadRadius * (0.95f + 0.05f * sin(i * 1.9f + 1.1f))
                val spacer = i == 11 || i == 22
                val isActive = i == active
                if (isActive) {
                    val glowRadius = br * (2.4f + 0.4f * breathe)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AtharPrimaryLight.copy(alpha = 0.50f),
                                AtharPrimary.copy(alpha = 0.16f),
                                Color.Transparent
                            ),
                            center = point,
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = point
                    )
                }
                // Contact shadow.
                drawCircle(
                    color = Color.Black.copy(alpha = 0.32f),
                    radius = br * 0.98f,
                    center = point + Offset(0f, br * 0.22f)
                )
                if (spacer) {
                    // A divider after every eleven pearls, in the theme colour.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AtharPrimaryLight,
                                AtharPrimary,
                                AtharPrimaryDeep
                            ),
                            center = point - Offset(br * 0.30f, br * 0.32f),
                            radius = br * 1.5f
                        ),
                        radius = br * 0.82f,
                        center = point
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.30f),
                        radius = br * 0.82f,
                        center = point,
                        style = Stroke(width = 0.7.dp.toPx())
                    )
                } else {
                    // Pearl body.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                StrandPearlLight,
                                StrandPearlMid,
                                StrandPearlDeep
                            ),
                            center = point - Offset(br * 0.34f, br * 0.36f),
                            radius = br * 1.55f
                        ),
                        radius = br,
                        center = point
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = if (isActive) 0.95f else 0.55f),
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
                .offset(y = readoutShift),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CountReadout(count = count, cycle = cycle, numberStyle = numberStyle)
        }
    }
}

/** The tally and where it stands in the current cycle. */
@Composable
private fun CountReadout(
    count: Int,
    cycle: Int,
    numberStyle: NumberStylePreference
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

/**
 * The other sibha: one endless strand of white pearls that runs in from beyond
 * the left edge and out past the right, bowing gently upward through the middle
 * of the page the way a masbaha rests between two fingers. A beam of white
 * light rides from pearl to pearl as the cycle fills, every pearl breathes on
 * its own beat, and the whole strand floats a hair so it never sits still.
 * Dragging along the strand counts one bead per bead crossed; tapping counts one.
 */
@Composable
private fun SibhaStrand(
    count: Int,
    cycle: Int,
    numberStyle: NumberStylePreference,
    onCount: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            pulse.snapTo(0.986f)
            pulse.animateTo(1f, spring(dampingRatio = 0.38f, stiffness = 320f))
        }
    }

    val drift = rememberInfiniteTransition(label = "strandDrift")
    // A breath that travels along the strand, pearl by pearl.
    val breath by drift.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "strandBreath"
    )
    // A slow sideways float, so the strand reads as hung rather than printed.
    val float by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strandFloat"
    )
    // The beam glides to its new pearl instead of jumping to it.
    val beam by animateFloatAsState(
        targetValue = cycle.toFloat(),
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "strandBeam"
    )
    val countRef = rememberUpdatedState(onCount)

    BoxWithConstraints(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(STRAND_BEADS) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        val overhang = size.width * STRAND_OVERHANG
                        val span = size.width + overhang * 2f
                        fun indexAt(x: Float): Int =
                            (((x + overhang) / span) * (STRAND_BEADS - 1))
                                .roundToInt()
                                .coerceIn(0, STRAND_BEADS - 1)

                        var last = indexAt(down.position.x)
                        var lastPoint = down.position
                        var travelled = 0f
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            change.consume()
                            if (!change.pressed) break
                            val point = change.position
                            travelled += (point - lastPoint).getDistance()
                            lastPoint = point
                            val index = indexAt(point.x)
                            if (index != last) {
                                countRef.value(abs(index - last))
                                last = index
                            }
                        }
                        if (travelled < 6.dp.toPx()) countRef.value(1)
                    }
                }
        ) {
            val overhang = size.width * STRAND_OVERHANG
            val left = -overhang + float * 2.4.dp.toPx()
            val span = size.width + overhang * 2f
            val spacing = span / (STRAND_BEADS - 1)
            val beadR = spacing * 0.60f
            val baseY = size.height * 0.735f
            val lift = size.height * 0.058f

            val points = ArrayList<Offset>(STRAND_BEADS)
            for (i in 0 until STRAND_BEADS) {
                val t = i / (STRAND_BEADS - 1f)
                points += Offset(
                    x = left + spacing * i,
                    y = baseY - lift * sin(PI * t.toDouble()).toFloat()
                )
            }

            // The strand's shadow keeps it resting on the page.
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.30f), Color.Transparent),
                    center = Offset(size.width / 2f, baseY + beadR * 1.1f),
                    radius = size.width * 0.62f
                ),
                topLeft = Offset(left, baseY - beadR * 0.2f),
                size = androidx.compose.ui.geometry.Size(span, beadR * 3.2f)
            )

            // The thread, bowing with the strand.
            val thread = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 0 until points.lastIndex) {
                    val from = points[i]
                    val to = points[i + 1]
                    quadraticTo(
                        (from.x + to.x) / 2f,
                        (from.y + to.y) / 2f + beadR * 0.10f,
                        to.x,
                        to.y
                    )
                }
            }
            drawPath(
                path = thread,
                color = Color.Black.copy(alpha = 0.42f),
                style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = thread,
                color = Color(0xFFF3EFE7).copy(alpha = 0.55f),
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
            )

            // Where the beam is, interpolated between two pearls.
            val beamPos = (beam - 1f).coerceIn(0f, STRAND_BEADS - 1f)
            val hasBeam = cycle > 0
            val beamLow = floor(beamPos).toInt().coerceIn(0, STRAND_BEADS - 1)
            val beamHigh = (beamLow + 1).coerceAtMost(STRAND_BEADS - 1)
            val beamT = beamPos - beamLow
            val beamPoint = Offset(
                x = points[beamLow].x + (points[beamHigh].x - points[beamLow].x) * beamT,
                y = points[beamLow].y + (points[beamHigh].y - points[beamLow].y) * beamT
            )

            if (hasBeam) {
                // The shaft of light the beam carries, standing on the strand.
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.05f),
                            Color.White.copy(alpha = 0.20f),
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = beamPoint.y - beadR * 4.6f,
                        endY = beamPoint.y + beadR * 2.4f
                    ),
                    topLeft = Offset(beamPoint.x - beadR * 0.85f, beamPoint.y - beadR * 4.6f),
                    size = androidx.compose.ui.geometry.Size(beadR * 1.7f, beadR * 7f)
                )
            }

            for (i in 0 until STRAND_BEADS) {
                val point = points[i]
                // Every pearl breathes on its own offset, so the strand ripples.
                val breathe = sin(2.0 * PI * (breath + i * 0.075)).toFloat()
                val r = beadR * (1f + 0.05f * breathe) * pulse.value
                val spacer = i == 11 || i == 22
                val near = if (hasBeam) {
                    (1f - abs(i - beamPos) / 1.7f).coerceIn(0f, 1f)
                } else {
                    0f
                }

                if (near > 0f) {
                    val glow = r * (2.6f + 0.5f * breathe)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.40f * near),
                                AtharPrimaryLight.copy(alpha = 0.16f * near),
                                Color.Transparent
                            ),
                            center = point,
                            radius = glow
                        ),
                        radius = glow,
                        center = point
                    )
                }

                drawCircle(
                    color = Color.Black.copy(alpha = 0.32f),
                    radius = r * 0.98f,
                    center = point + Offset(0f, r * 0.22f)
                )
                if (spacer) {
                    // A divider after every eleven pearls, in the theme colour.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AtharPrimaryLight,
                                AtharPrimary,
                                AtharPrimaryDeep
                            ),
                            center = point - Offset(r * 0.30f, r * 0.32f),
                            radius = r * 1.5f
                        ),
                        radius = r * 0.86f,
                        center = point
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.40f),
                        radius = r * 0.86f,
                        center = point,
                        style = Stroke(width = 0.6.dp.toPx())
                    )
                } else {
                    // White pearl, lit from the upper left.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                StrandPearlLight,
                                StrandPearlMid,
                                StrandPearlDeep
                            ),
                            center = point - Offset(r * 0.34f, r * 0.38f),
                            radius = r * 1.85f
                        ),
                        radius = r,
                        center = point
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f + 0.35f * near),
                        radius = r,
                        center = point,
                        style = Stroke(width = 0.6.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.92f),
                        radius = r * 0.20f,
                        center = point - Offset(r * 0.32f, r * 0.36f)
                    )
                }
            }

            if (hasBeam) {
                // A bright core where the beam stands, so the eye lands on it.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.55f),
                            Color.Transparent
                        ),
                        center = beamPoint,
                        radius = beadR * 2.2f
                    ),
                    radius = beadR * 2.2f,
                    center = beamPoint
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = maxHeight * 0.24f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CountReadout(count = count, cycle = cycle, numberStyle = numberStyle)
        }
    }
}

/**
 * The head of the piece. The point of the teardrop gathers into a short
 * thread, the imam bead, its cap and a knot, and a fan of tassel threads falls
 * below it — the tail of a masbaha held the right way up.
 * [anchor] is the bottom tip of the loop.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawImamAndTassel(
    anchor: Offset,
    beadRadius: Float
) {
    val imamWidth = beadRadius * 1.35f
    val imamHeight = beadRadius * 2.05f
    val imamTop = anchor.y + beadRadius * 0.60f
    val imamCenter = Offset(anchor.x, imamTop + imamHeight / 2f)
    val imamBottom = imamTop + imamHeight

    // The thread that carries the loop down into the imam bead.
    drawLine(
        color = Color(0xFFF3EFE7).copy(alpha = 0.9f),
        start = anchor,
        end = Offset(anchor.x, imamTop),
        strokeWidth = 1.4.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Contact shadow under the imam.
    drawOval(
        color = Color.Black.copy(alpha = 0.32f),
        topLeft = Offset(imamCenter.x - imamWidth / 2f, imamTop + beadRadius * 0.20f),
        size = androidx.compose.ui.geometry.Size(imamWidth, imamHeight)
    )
    drawOval(
        brush = Brush.linearGradient(
            colors = listOf(
                AtharPrimaryLight,
                AtharPrimary,
                AtharPrimaryDeep
            ),
            start = Offset(imamCenter.x - imamWidth / 2f, imamTop),
            end = Offset(imamCenter.x + imamWidth / 2f, imamBottom)
        ),
        topLeft = Offset(imamCenter.x - imamWidth / 2f, imamTop),
        size = androidx.compose.ui.geometry.Size(imamWidth, imamHeight)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.35f),
        topLeft = Offset(imamCenter.x - imamWidth * 0.26f, imamTop + imamHeight * 0.16f),
        size = androidx.compose.ui.geometry.Size(imamWidth * 0.24f, imamHeight * 0.28f)
    )

    val cap = Offset(anchor.x, imamBottom + beadRadius * 0.32f)
    drawCircle(
        color = AtharPrimaryLight.copy(alpha = 0.9f),
        radius = beadRadius * 0.40f,
        center = cap
    )
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = beadRadius * 0.40f,
        center = cap,
        style = Stroke(width = 0.7.dp.toPx())
    )

    // The knot the threads are gathered into.
    val knot = Offset(anchor.x, cap.y + beadRadius * 0.60f)
    drawCircle(
        color = AtharPrimaryDeep,
        radius = beadRadius * 0.50f,
        center = knot
    )
    drawCircle(
        color = AtharPrimary.copy(alpha = 0.9f),
        radius = beadRadius * 0.50f,
        center = knot,
        style = Stroke(width = 1.dp.toPx())
    )

    val threadTop = knot.y + beadRadius * 0.22f
    val threadLength = beadRadius * 2.1f
    for (i in 0..4) {
        val spread = (i - 2) / 2f
        val endX = anchor.x + spread * beadRadius * 1.5f
        val endY = threadTop + threadLength * (1f - abs(spread) * 0.18f)
        val path = Path().apply {
            moveTo(anchor.x, threadTop)
            quadraticTo(
                anchor.x + spread * beadRadius * 0.5f,
                threadTop + threadLength * 0.55f,
                endX,
                endY
            )
        }
        drawPath(
            path = path,
            color = AtharPrimary.copy(alpha = 0.80f),
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

/**
 * The teardrop the pearls are strung on: round at the top, gathering to a
 * point at the bottom where the imam bead hangs.
 */
private fun teardropPath(width: Float, height: Float): Path {
    val cx = width / 2f
    val top = height * LOOP_TOP
    val bottom = height * LOOP_BOTTOM
    val body = (bottom - top).coerceAtLeast(1f)
    val cy = top + body * 0.40f
    val rx = minOf(width * 0.462f, body * 0.462f)
    return Path().apply {
        moveTo(cx, bottom)
        cubicTo(
            cx - rx * 0.62f, bottom - body * 0.14f,
            cx - rx * 1.02f, cy + body * 0.14f,
            cx - rx, cy
        )
        cubicTo(
            cx - rx, top + (cy - top) * 0.32f,
            cx - rx * 0.60f, top,
            cx, top
        )
        cubicTo(
            cx + rx * 0.60f, top,
            cx + rx, top + (cy - top) * 0.32f,
            cx + rx, cy
        )
        cubicTo(
            cx + rx * 1.02f, cy + body * 0.14f,
            cx + rx * 0.62f, bottom - body * 0.14f,
            cx, bottom
        )
        close()
    }
}

/** The distance along the loop of the point on it closest to [point]. */
private fun PathMeasure.nearestDistance(point: Offset, samples: Int = 220): Float {
    val total = length.coerceAtLeast(1f)
    var best = Float.MAX_VALUE
    var bestDistance = 0f
    for (i in 0 until samples) {
        val d = total * i / samples
        val candidate = getPosition(d)
        val dx = candidate.x - point.x
        val dy = candidate.y - point.y
        val sq = dx * dx + dy * dy
        if (sq < best) {
            best = sq
            bestDistance = d
        }
    }
    return bestDistance
}

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

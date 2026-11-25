package com.athar.app.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.athar.app.R
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.components.MihrabWatermark
import com.athar.app.ui.components.OrnamentDivider
import com.athar.app.ui.components.ScratchPanel
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val deckTimeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

private val CardHeight = 152.dp
private val CollapsedPeek = 44.dp
private val FannedPeek = 76.dp

private const val HINT_PREFS = "athar_ui_hints"
private const val HINT_SEEN_KEY = "prayer_deck_hint_seen"

/** Degrees of turn per pixel of horizontal drag, once a card is picked up. */
private const val SPIN_PER_PIXEL = 0.6f

/**
 * Wallet-style stack of today's prayer times. Cards rest as an overlapping
 * pile; dragging fans them out, and tapping one lifts it above the deck.
 *
 * A lifted card can be turned a full 360 degrees by dragging it sideways, the
 * way a card behaves in the hand. Its back is the app's own backdrop with the
 * Athar wordmark printed on it.
 */
@Composable
fun PrayerWalletDeck(
    rows: List<PrayerRow>,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val hintPrefs = remember(context) { context.getSharedPreferences(HINT_PREFS, 0) }
    var showGestureHint by remember { mutableStateOf(!hintPrefs.getBoolean(HINT_SEEN_KEY, false)) }
    var focusedKey by rememberSaveable { mutableStateOf<String?>(null) }
    var fanTarget by remember { mutableFloatStateOf(0f) }
    // Cards that have been turned onto their back and squared up. Their turn
    // gesture is switched off until they are put back down, so a hand on the
    // foil scratches instead of spinning the card it is printed on.
    val backFacing = remember { mutableStateMapOf<String, Boolean>() }
    val revealedAyat = remember { mutableStateMapOf<String, Boolean>() }
    val scope = rememberCoroutineScope()
    val fan by animateFloatAsState(
        targetValue = fanTarget,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "deckFan"
    )
    val peek = CollapsedPeek + (FannedPeek - CollapsedPeek) * fan

    val glow = rememberInfiniteTransition(label = "deckGlow")
    val glowAlpha by glow.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.46f,
        animationSpec = infiniteRepeatable(tween(2600), repeatMode = RepeatMode.Reverse),
        label = "deckGlowAlpha"
    )

    var lastFanHaptic by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(fanTarget) {
        if (lastFanHaptic < 0.5f && fanTarget >= 0.5f) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        lastFanHaptic = fanTarget
    }

    val toggleFocus: (String) -> Unit = { key ->
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        focusedKey = if (focusedKey == key) null else key
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(CardHeight + peek * (rows.size - 1).coerceAtLeast(0) + 16.dp)
            .then(
                // While a card is lifted, the deck itself holds still: the fan
                // is an outward gesture, and it must never fight the scratch.
                if (focusedKey == null) {
                    Modifier.draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            fanTarget = (fanTarget + delta / 320f).coerceIn(0f, 1f)
                            if (showGestureHint && delta > 2f) {
                                showGestureHint = false
                                hintPrefs.edit().putBoolean(HINT_SEEN_KEY, true).apply()
                            }
                        },
                        onDragStopped = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            fanTarget = if (fanTarget > 0.45f) 1f else 0f
                        }
                    )
                } else {
                    Modifier
                }
            )
    ) {
        rows.forEachIndexed { index, row ->
            val isFocused = focusedKey == row.key
            val lift by animateDpAsState(
                targetValue = if (isFocused) (-18).dp else 0.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                label = "deckCardLift"
            )
            val cardScale by animateFloatAsState(
                targetValue = if (isFocused) 1.035f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                label = "deckCardScale"
            )

            // How far the card has been turned, in degrees. Free to pass 360.
            var spin by remember(row.key) { mutableFloatStateOf(0f) }
            var settleJob by remember(row.key) { mutableStateOf<Job?>(null) }
            val flipped = backFacing[row.key] == true

            // Releasing the card turns it back to face front.
            LaunchedEffect(isFocused) {
                settleJob?.cancel()
                if (!isFocused && spin != 0f) {
                    backFacing[row.key] = false
                    animate(
                        initialValue = spin,
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) { value, _ -> spin = value }
                }
            }

            PrayerWalletCard(
                row = row,
                isArabic = isArabic,
                numberStyle = numberStyle,
                isFocused = isFocused,
                glowAlpha = if (row.isNext) glowAlpha else 0f,
                spin = spin,
                ayahRevealed = revealedAyat[row.key] == true,
                onAyahRevealed = { revealedAyat[row.key] = true },
                onCardTap = { toggleFocus(row.key) },
                modifier = Modifier
                    .offset(y = peek * (rows.lastIndex - index) + lift)
                    .zIndex(if (isFocused) 100f else (rows.size - index).toFloat())
                    .scale(cardScale)
                    .then(
                        if (isFocused && !flipped) {
                            Modifier.draggable(
                                orientation = Orientation.Horizontal,
                                state = rememberDraggableState { delta ->
                                    spin += delta * SPIN_PER_PIXEL
                                },
                                onDragStarted = { settleJob?.cancel() },
                                onDragStopped = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // Square the card up to whichever face it is nearest.
                                    val target = (spin / 180f).roundToInt() * 180f
                                    val normalised = ((target % 360f) + 360f) % 360f
                                    backFacing[row.key] = normalised > 90f && normalised < 270f
                                    settleJob = scope.launch {
                                        animate(
                                            initialValue = spin,
                                            targetValue = target,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        ) { value, _ -> spin = value }
                                    }
                                }
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { toggleFocus(row.key) }
                    )
            )

            if (showGestureHint) {
                DeckGestureHint(
                    isArabic = isArabic,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 42.dp)
                )
            }
        }
    }
}

@Composable
private fun DeckGestureHint(
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "deckHint")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "deckHintDrift"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtharBackground.copy(alpha = 0.86f))
            .border(1.dp, AtharCardBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.TouchApp,
            contentDescription = null,
            tint = AtharPrimaryLight.copy(alpha = 0.65f + drift * 0.35f),
            modifier = Modifier
                .size(18.dp)
                .offset(y = (drift * 5).dp)
        )
        Text(
            text = stringResource(R.string.home_deck_hint),
            color = AtharTextSecondary,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
        )
    }
}

/**
 * One prayer card. [spin] is applied as a real 3D turn, so past a quarter turn
 * the back face takes over, corrected by 180 degrees so it never reads mirrored.
 */
@Composable
private fun PrayerWalletCard(
    row: PrayerRow,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    isFocused: Boolean,
    glowAlpha: Float,
    spin: Float,
    ayahRevealed: Boolean,
    onAyahRevealed: () -> Unit,
    onCardTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    val glowElevation = if (isFocused) 26.dp else if (row.isNext) 18.dp else 5.dp

    val normalised = ((spin % 360f) + 360f) % 360f
    val showingBack = normalised > 90f && normalised < 270f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .graphicsLayer {
                rotationY = spin
                cameraDistance = 16f * density
            }
            .shadow(
                elevation = glowElevation,
                shape = shape,
                spotColor = AtharPrimaryLight.copy(alpha = if (isFocused) 0.35f else glowAlpha),
                ambientColor = Color.Black.copy(alpha = 0.55f)
            )
            .clip(shape)
            .border(
                width = if (isFocused || row.isNext) 1.2.dp else 1.dp,
                color = when {
                    isFocused -> AtharPrimaryLight.copy(alpha = 0.85f)
                    row.isNext -> AtharPrimary.copy(alpha = 0.45f + glowAlpha * 0.5f)
                    else -> AtharCardBorder
                },
                shape = shape
            )
    ) {
        if (showingBack) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                PrayerCardBack(
                    row = row,
                    isArabic = isArabic,
                    revealed = ayahRevealed,
                    onRevealed = onAyahRevealed,
                    onCardTap = onCardTap
                )
            }
        } else {
            PrayerCardFront(
                row = row,
                isArabic = isArabic,
                numberStyle = numberStyle,
                isFocused = isFocused,
                glowAlpha = glowAlpha
            )
        }
    }
}

@Composable
private fun PrayerCardFront(
    row: PrayerRow,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    isFocused: Boolean,
    glowAlpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (row.isNext) AtharPrimarySubtle else AtharCardSurface,
                        AtharBackground
                    )
                )
            )
    ) {
        MihrabWatermark(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(112.dp)
                .alpha(if (row.isNext) 0.13f else 0.07f),
            color = AtharPrimaryLight
        )

        if (isFocused) {
            // Keep the hint on the physical bottom-left of the card in every
            // language. In RTL, BottomStart/start padding would mirror it to
            // the right, so swap to the end side to land on the left again.
            val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
            FlipHint(
                modifier = Modifier
                    .align(if (isRtl) Alignment.BottomEnd else Alignment.BottomStart)
                    .padding(
                        start = if (isRtl) 0.dp else 20.dp,
                        end = if (isRtl) 20.dp else 0.dp,
                        bottom = 14.dp
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(row.nameResId),
                        color = AtharTextPrimary,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    if (row.isNext) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.home_next_prayer),
                            color = AtharPrimaryLight,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                Text(
                    text = formatDigits(row.time.format(deckTimeFmt), numberStyle),
                    color = if (row.isNext) AtharPrimaryLight else AtharTextPrimary,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp
                )
            }

            Spacer(Modifier.weight(1f))

            OrnamentDivider(
                color = if (row.isNext) AtharPrimaryLight.copy(alpha = 0.55f) else AtharCardBorder,
                ornamentColor = if (row.isNext) AtharPrimaryLight.copy(alpha = 0.85f) else AtharCardBorder
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = stringResource(noteFor(row.key)),
                color = if (row.isNext) AtharTextPrimary.copy(alpha = 0.92f) else AtharTextSecondary,
                fontFamily = if (isArabic) ThmanyahSerifText else ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = if (isFocused) 20.dp else 0.dp)
            )
        }
    }
}

/**
 * The reverse of a prayer card: the app's own backdrop — whichever the user
 * chose — with the Athar wordmark, then a foil panel the user scratches away
 * to reveal an ayah chosen for that prayer.
 */
@Composable
private fun PrayerCardBack(
    row: PrayerRow,
    isArabic: Boolean,
    revealed: Boolean,
    onRevealed: () -> Unit,
    onCardTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AtharPrimarySubtle, AtharBackground)
                )
            )
    ) {
        IslamicPatternBackground(
            modifier = Modifier.matchParentSize(),
            alpha = 0.20f,
            animated = true,
            cellDp = 46f
        )

        // Soft vignette so the wordmark always sits on calm ground.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            AtharBackground.copy(alpha = 0.62f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "أَثَر",
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                lineHeight = 30.sp,
                color = AtharPrimaryLight,
                textAlign = TextAlign.Center
            )
            Text(
                text = "A T H A R",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp,
                letterSpacing = 4.5.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            ScratchAyah(
                row = row,
                isArabic = isArabic,
                revealed = revealed,
                onRevealed = onRevealed,
                onCardTap = onCardTap,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(62.dp)
            )
        }
    }
}

/**
 * The prayer card's scratch-off ayah, printed on the shared foil panel.
 */
@Composable
private fun ScratchAyah(
    row: PrayerRow,
    isArabic: Boolean,
    revealed: Boolean,
    onRevealed: () -> Unit,
    onCardTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ayah = remember(row.key) { ayahFor(row.key) }
    ScratchPanel(
        arabic = ayah.arabic,
        reference = if (isArabic) ayah.referenceAr else ayah.referenceEn,
        subtitle = if (isArabic) null else ayah.meaning,
        revealed = revealed,
        onRevealed = onRevealed,
        onTap = onCardTap,
        modifier = modifier,
        referenceFontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
        foilLabel = if (isArabic) "اخدش · " else "SCRATCH · "
    )
}

private data class AyahReveal(
    val arabic: String,
    val referenceAr: String,
    val referenceEn: String,
    val meaning: String
)

private fun ayahFor(key: String): AyahReveal = when (key) {
    "fajr" -> AyahReveal(
        arabic = "وَقُرْآنَ الْفَجْرِ ۖ إِنَّ قُرْآنَ الْفَجْرِ كَانَ مَشْهُودًا",
        referenceAr = "الإسراء ٧٨",
        referenceEn = "Al-Isra 78",
        meaning = "And the recitation of dawn — indeed, the recitation of dawn is ever witnessed."
    )
    "sunrise" -> AyahReveal(
        arabic = "وَسَبِّحْ بِحَمْدِ رَبِّكَ قَبْلَ طُلُوعِ الشَّمْسِ",
        referenceAr = "ق ٣٩",
        referenceEn = "Qaf 39",
        meaning = "And glorify the praise of your Lord before the rising of the sun."
    )
    "dhuhr" -> AyahReveal(
        arabic = "أَقِمِ الصَّلَاةَ لِدُلُوكِ الشَّمْسِ",
        referenceAr = "الإسراء ٧٨",
        referenceEn = "Al-Isra 78",
        meaning = "Establish prayer at the decline of the sun."
    )
    "asr" -> AyahReveal(
        arabic = "حَافِظُوا عَلَى الصَّلَوَاتِ وَالصَّلَاةِ الْوُسْطَىٰ وَقُومُوا لِلَّهِ قَانِتِينَ",
        referenceAr = "البقرة ٢٣٨",
        referenceEn = "Al-Baqarah 238",
        meaning = "Guard strictly the prayers, especially the middle prayer, and stand before Allah devoutly obedient."
    )
    "maghrib" -> AyahReveal(
        arabic = "فَسُبْحَانَ اللَّهِ حِينَ تُمْسُونَ",
        referenceAr = "الروم ١٧",
        referenceEn = "Ar-Rum 17",
        meaning = "So glory be to Allah when you enter the evening."
    )
    "isha" -> AyahReveal(
        arabic = "وَمِنَ اللَّيْلِ فَسَبِّحْهُ وَإِدْبَارَ النُّجُومِ",
        referenceAr = "الطور ٤٩",
        referenceEn = "At-Tur 49",
        meaning = "And glorify Him in the night and after the stars decline."
    )
    else -> AyahReveal(
        arabic = "وَمِنَ اللَّيْلِ فَسَبِّحْهُ وَإِدْبَارَ النُّجُومِ",
        referenceAr = "الطور ٤٩",
        referenceEn = "At-Tur 49",
        meaning = "And glorify Him in the night and after the stars decline."
    )
}

/** Invites the sideways turn, with a slow swing rather than a static glyph. */
@Composable
private fun FlipHint(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "flipHint")
    val swing by transition.animateFloat(
        initialValue = -22f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flipHintSwing"
    )

    Icon(
        imageVector = Icons.AutoMirrored.Rounded.RotateRight,
        contentDescription = null,
        tint = AtharPrimaryLight.copy(alpha = 0.8f),
        modifier = modifier
            .size(17.dp)
            .graphicsLayer { rotationZ = swing }
    )
}

private fun noteFor(key: String): Int = when (key) {
    "fajr" -> R.string.home_prayer_note_fajr
    "sunrise" -> R.string.home_prayer_note_sunrise
    "dhuhr" -> R.string.home_prayer_note_dhuhr
    "asr" -> R.string.home_prayer_note_asr
    "maghrib" -> R.string.home_prayer_note_maghrib
    else -> R.string.home_prayer_note_isha
}

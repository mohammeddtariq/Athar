package com.athar.app.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.athar.app.data.AppPreferences
import com.athar.app.data.AsmaNames
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
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** How much of the buried card stays visible under the one on top. */
private val StackPeek = 46.dp

/** How long a name stays on the card before the next one arrives. */
private const val NAME_CYCLE_MS = 7000L

/** How long the two cards hold their places before trading, when asked to. */
private const val ALTERNATE_MS = 12000L

/** Degrees of turn per pixel of horizontal drag, matched to the prayer deck. */
private const val ASMA_SPIN_PER_PIXEL = 0.6f

/**
 * The home hero, as a two-card pile: the next prayer card and the card of the
 * names of Allah. They behave like the prayer wallet below — the buried card
 * peeks out at the bottom and a tap on that lip brings it to the top, while the
 * card on top can be turned over by dragging it sideways.
 *
 * The names card can also trade places with the prayer card on a timer, if the
 * reader asks it to from the card itself.
 */
@Composable
fun AsmaHeroStack(
    nextKey: String,
    nextTime: LocalTime,
    isTomorrow: Boolean,
    numberStyle: NumberStylePreference,
    language: String,
    cityLabel: String?,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == "ar"
    val context = LocalContext.current
    val prefs = remember(context) { AppPreferences(context.applicationContext) }
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val autoCycle by prefs.asmaAutoCycle.collectAsState(initial = true)
    val autoAlternate by prefs.asmaAutoAlternate.collectAsState(initial = false)

    var asmaInFront by rememberSaveable { mutableStateOf(false) }
    var heroHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val heroHeightDp = with(density) { heroHeight.toDp() }

    LaunchedEffect(autoAlternate) {
        if (!autoAlternate) return@LaunchedEffect
        while (true) {
            delay(ALTERNATE_MS)
            asmaInFront = !asmaInFront
        }
    }

    val heroOffset by animateDpAsState(
        targetValue = if (asmaInFront) StackPeek else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "heroStackOffset"
    )
    val asmaOffset by animateDpAsState(
        targetValue = if (asmaInFront) 0.dp else StackPeek,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "asmaStackOffset"
    )

    val swapCards: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        asmaInFront = !asmaInFront
    }

    Box(modifier = modifier.fillMaxWidth()) {
        if (heroHeight > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeightDp)
                    .padding(horizontal = 14.dp)
                    .offset(y = asmaOffset)
                    .zIndex(if (asmaInFront) 1f else 0f)
            ) {
                AsmaCard(
                    isArabic = isArabic,
                    numberStyle = numberStyle,
                    autoCycle = autoCycle,
                    autoAlternate = autoAlternate,
                    onToggleAutoCycle = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        scope.launch { prefs.setAsmaAutoCycle(!autoCycle) }
                    },
                    onToggleAutoAlternate = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        scope.launch { prefs.setAsmaAutoAlternate(!autoAlternate) }
                    },
                    inFront = asmaInFront,
                    onPutDown = { if (asmaInFront) asmaInFront = false },
                    modifier = Modifier.fillMaxSize()
                )
                if (!asmaInFront) {
                    // Only the lip of the buried card answers a tap, so the
                    // prayer card on top keeps its own touches.
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(StackPeek)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = swapCards
                            )
                    )
                }
            }
        }

        Column(modifier = Modifier.zIndex(if (asmaInFront) 0f else 1f)) {
            NextPrayerCard(
                nextKey = nextKey,
                nextTime = nextTime,
                isTomorrow = isTomorrow,
                numberStyle = numberStyle,
                language = language,
                cityLabel = cityLabel,
                onLocationClick = onLocationClick,
                modifier = Modifier
                    .offset(y = heroOffset)
                    .onSizeChanged { heroHeight = it.height }
            )
            Spacer(
                modifier = Modifier
                    .height(StackPeek)
                    .then(
                        if (asmaInFront) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = swapCards
                            )
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

/**
 * One card of the names. [inFront] is the pile position: while the card is
 * buried its gestures are off and only the lip answers, so it can never steal a
 * touch from the card resting on it.
 */
@Composable
private fun AsmaCard(
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    autoCycle: Boolean,
    autoAlternate: Boolean,
    onToggleAutoCycle: () -> Unit,
    onToggleAutoAlternate: () -> Unit,
    inFront: Boolean,
    onPutDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var index by rememberSaveable { mutableIntStateOf(0) }
    var spin by rememberSaveable { mutableFloatStateOf(0f) }
    // The face the card settled on. Kept apart from the live rotation angle so
    // the drag gesture is never torn out from under the finger when the card
    // passes the quarter turn.
    var backFacing by rememberSaveable { mutableStateOf(false) }
    var revealed by rememberSaveable { mutableStateOf(false) }
    var settleJob by remember { mutableStateOf<Job?>(null) }

    val normalised = ((spin % 360f) + 360f) % 360f
    val showingBack = normalised > 90f && normalised < 270f

    // A different name means a different ayah under the foil.
    LaunchedEffect(index) { revealed = false }

    // The names only turn over while the card is face up and on top.
    LaunchedEffect(autoCycle, inFront, backFacing, index) {
        if (!autoCycle || !inFront || backFacing) return@LaunchedEffect
        delay(NAME_CYCLE_MS)
        index = (index + 1) % AsmaNames.size
    }

    // Leaving the top of the pile squares the card back up, face out; arriving
    // at the top always brings a name the reader has not just seen.
    LaunchedEffect(inFront) {
        settleJob?.cancel()
        if (!inFront) {
            backFacing = false
            if (spin != 0f) {
                animate(
                    initialValue = spin,
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { value, _ -> spin = value }
            }
        } else {
            index = (index + 1) % AsmaNames.size
        }
    }

    val turnOver: () -> Unit = {
        val target = if (showingBack) 0f else 180f
        backFacing = !showingBack
        settleJob?.cancel()
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

    val onTap: () -> Unit = {
        if (showingBack) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            turnOver()
        } else {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            index = (index + 1) % AsmaNames.size
        }
    }

    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = spin
                cameraDistance = 16f * density
            }
            .shadow(
                elevation = if (inFront) 20.dp else 10.dp,
                shape = shape,
                spotColor = AtharPrimaryLight.copy(alpha = if (inFront) 0.26f else 0.14f),
                ambientColor = Color.Black.copy(alpha = 0.60f)
            )
            .clip(shape)
            .border(
                width = 1.dp,
                color = AtharPrimary.copy(alpha = 0.42f),
                shape = shape
            )
            .then(
                if (inFront) {
                    Modifier.draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            spin += delta * ASMA_SPIN_PER_PIXEL
                        },
                        onDragStarted = { settleJob?.cancel() },
                        onDragStopped = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val target = (spin / 180f).roundToInt() * 180f
                            val targetNormalised = ((target % 360f) + 360f) % 360f
                            backFacing = targetNormalised > 90f && targetNormalised < 270f
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
            .then(
                if (inFront) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTap
                    )
                } else {
                    Modifier
                }
            )
    ) {
        if (showingBack) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                AsmaCardBack(
                    index = index,
                    isArabic = isArabic,
                    revealed = revealed,
                    onRevealed = { revealed = true },
                    onTap = onPutDown
                )
            }
        } else {
            AsmaCardFront(
                index = index,
                isArabic = isArabic,
                numberStyle = numberStyle,
                autoCycle = autoCycle,
                autoAlternate = autoAlternate,
                onToggleAutoCycle = onToggleAutoCycle,
                onToggleAutoAlternate = onToggleAutoAlternate,
                showFlipHint = inFront
            )
        }
    }
}

@Composable
private fun AsmaCardFront(
    index: Int,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    autoCycle: Boolean,
    autoAlternate: Boolean,
    onToggleAutoCycle: () -> Unit,
    onToggleAutoAlternate: () -> Unit,
    showFlipHint: Boolean
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
        MihrabWatermark(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(176.dp)
                .alpha(0.10f),
            color = AtharPrimaryLight
        )

        // A quiet halo so the name sits in its own light.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AtharPrimary.copy(alpha = 0.14f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isArabic) "أسماء الله الحسنى" else "The Most Beautiful Names",
                        color = AtharPrimaryLight,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = if (isArabic) {
                                "الاسم ${formatDigits("${index + 1}", numberStyle)} من ${formatDigits("99", numberStyle)}"
                            } else {
                                "Name ${formatDigits("${index + 1}", numberStyle)} of ${formatDigits("99", numberStyle)}"
                            },
                            color = AtharTextSecondary,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 9.sp
                        )
                        if (showFlipHint) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.RotateRight,
                                contentDescription = null,
                                tint = AtharPrimaryLight.copy(alpha = 0.55f),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CardToggleChip(
                        active = autoCycle,
                        icon = if (autoCycle) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        description = if (isArabic) "إيقاف التبديل التلقائي للأسماء" else "Pause the names",
                        onClick = onToggleAutoCycle
                    )
                    CardToggleChip(
                        active = autoAlternate,
                        icon = Icons.Rounded.Autorenew,
                        description = if (isArabic) "التبديل التلقائي مع بطاقة الصلاة" else "Trade places with the prayer card",
                        onClick = onToggleAutoAlternate
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (fadeIn(tween(520)) + slideInVertically(tween(520)) { it / 5 })
                        .togetherWith(fadeOut(tween(260)) + slideOutVertically(tween(260)) { -it / 5 })
                },
                label = "asmaName",
                modifier = Modifier.fillMaxWidth()
            ) { target ->
                val shown = AsmaNames[target]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = shown.arabic,
                        fontFamily = ThmanyahSerifText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = nameFontSize(shown.arabic),
                        lineHeight = nameFontSize(shown.arabic) * 1.45f,
                        color = AtharTextPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = shown.transliteration,
                        color = AtharPrimaryLight.copy(alpha = 0.85f),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.2.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            OrnamentDivider(
                color = AtharPrimaryLight.copy(alpha = 0.30f),
                ornamentColor = AtharPrimaryLight.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(7.dp))

            Crossfade(
                targetState = index,
                animationSpec = tween(420),
                label = "asmaLine"
            ) { target ->
                val shown = AsmaNames[target]
                Text(
                    text = if (isArabic) shown.lineAr else shown.lineEn,
                    color = AtharTextPrimary.copy(alpha = 0.92f),
                    fontFamily = if (isArabic) ThmanyahSerifText else ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AsmaCardBack(
    index: Int,
    isArabic: Boolean,
    revealed: Boolean,
    onRevealed: () -> Unit,
    onTap: () -> Unit
) {
    val name = AsmaNames[index]
    // Long ayat are shown as the passage that carries the name; short ones are
    // printed whole.
    val ayahText = if (name.ayah.length <= 150) name.ayah else name.phrase

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
                fontSize = 24.sp,
                lineHeight = 28.sp,
                color = AtharPrimaryLight,
                textAlign = TextAlign.Center
            )
            Text(
                text = "A T H A R",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                letterSpacing = 4.5.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(7.dp))
            ScratchPanel(
                arabic = ayahText,
                reference = if (isArabic) name.referenceAr else name.referenceEn,
                subtitle = if (isArabic) null else name.transliteration,
                revealed = revealed,
                onRevealed = onRevealed,
                onTap = onTap,
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .height(104.dp),
                arabicFontFamily = QuranUthmanicHafs,
                arabicFontWeight = FontWeight.Normal,
                arabicFontSize = 13.sp,
                arabicLineHeight = 20.sp,
                arabicMaxLines = 4,
                referenceFontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                foilLabel = if (isArabic) "اخدش · " else "SCRATCH · "
            )
        }
    }
}

/** A small round switch that lives on the card itself. */
@Composable
private fun CardToggleChip(
    active: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
                if (active) AtharPrimary.copy(alpha = 0.22f) else AtharCardSurface.copy(alpha = 0.75f)
            )
            .border(
                1.dp,
                if (active) AtharPrimary.copy(alpha = 0.55f) else AtharCardBorder,
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (active) AtharPrimaryLight else AtharTextSecondary,
            modifier = Modifier.size(15.dp)
        )
    }
}

/** Big enough to carry the name, small enough that the long ones still fit. */
private fun nameFontSize(arabic: String): TextUnit = when {
    arabic.length <= 7 -> 46.sp
    arabic.length <= 11 -> 38.sp
    arabic.length <= 16 -> 30.sp
    else -> 24.sp
}

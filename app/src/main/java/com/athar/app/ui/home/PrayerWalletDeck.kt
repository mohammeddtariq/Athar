package com.athar.app.ui.home

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.athar.app.R
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.ui.components.MihrabWatermark
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

private val deckTimeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

private val CardHeight = 152.dp
private val CollapsedPeek = 44.dp
private val FannedPeek = 76.dp

private const val HINT_PREFS = "athar_ui_hints"
private const val HINT_SEEN_KEY = "prayer_deck_hint_seen"

/**
 * Wallet-style stack of today's prayer times. Cards rest as an overlapping pile;
 * dragging fans them out, and tapping one lifts it above the deck.
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(CardHeight + peek * (rows.size - 1).coerceAtLeast(0) + 16.dp)
            .draggable(
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
                    focusedKey = null
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

            PrayerWalletCard(
                row = row,
                isArabic = isArabic,
                numberStyle = numberStyle,
                isFocused = isFocused,
                glowAlpha = if (row.isNext) glowAlpha else 0f,
                modifier = Modifier
                    .offset(y = peek * (rows.lastIndex - index) + lift)
                    .zIndex(if (isFocused) 100f else (rows.size - index).toFloat())
                    .scale(cardScale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            focusedKey = if (isFocused) null else row.key
                        }
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

@Composable
private fun PrayerWalletCard(
    row: PrayerRow,
    isArabic: Boolean,
    numberStyle: NumberStylePreference,
    isFocused: Boolean,
    glowAlpha: Float,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    val glowElevation = if (isFocused) 26.dp else if (row.isNext) 18.dp else 5.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .shadow(
                elevation = glowElevation,
                shape = shape,
                spotColor = AtharPrimaryLight.copy(alpha = if (isFocused) 0.35f else glowAlpha),
                ambientColor = Color.Black.copy(alpha = 0.55f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (row.isNext) AtharPrimarySubtle else AtharCardSurface,
                        AtharBackground
                    )
                )
            )
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
        MihrabWatermark(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(112.dp)
                .alpha(if (row.isNext) 0.13f else 0.07f),
            color = AtharPrimaryLight
        )

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

            OrnamentDivider(isNext = row.isNext)

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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun OrnamentDivider(isNext: Boolean) {
    val color = if (isNext) AtharPrimaryLight.copy(alpha = 0.55f) else AtharCardBorder
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, color)))
        )
        Spacer(Modifier.size(7.dp))
        Canvas(Modifier.size(9.dp)) {
            val half = size.minDimension / 2f
            rotate(45f) {
                drawRect(
                    color = color,
                    topLeft = Offset(center.x - half * 0.72f, center.y - half * 0.72f),
                    size = Size(half * 1.44f, half * 1.44f),
                    style = Stroke(1.dp.toPx())
                )
            }
        }
        Spacer(Modifier.size(7.dp))
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(color, Color.Transparent)))
        )
    }
}

private fun noteFor(key: String): Int = when (key) {
    "fajr" -> R.string.home_prayer_note_fajr
    "sunrise" -> R.string.home_prayer_note_sunrise
    "dhuhr" -> R.string.home_prayer_note_dhuhr
    "asr" -> R.string.home_prayer_note_asr
    "maghrib" -> R.string.home_prayer_note_maghrib
    else -> R.string.home_prayer_note_isha
}

package com.athar.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.CalcMethod
import com.athar.app.data.DayPrayers
import com.athar.app.data.MadhabOption
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.fallbackDayPrayers
import com.athar.app.data.findNextPrayer
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharGradientEnd
import com.athar.app.ui.theme.AtharGradientStart
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharTheme
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class PrayerRow(val key: String, val nameResId: Int, val time: LocalTime, val isNext: Boolean = false)

private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

@Composable
fun HomeScreen(onOpenSettings: () -> Unit = {}) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context.applicationContext) }

    val lat by prefs.latitude.collectAsState(initial = null)
    val lng by prefs.longitude.collectAsState(initial = null)
    val city by prefs.cityLabel.collectAsState(initial = null)
    val methodId by prefs.calcMethodId.collectAsState(initial = "MWL")
    val madhabId by prefs.madhabId.collectAsState(initial = "SHAFI")
    val notifMaster by prefs.notificationsMaster.collectAsState(initial = false)

    // Minute-granularity clock for the "next" highlight — cheap, recomposes rarely.
    var nowMinute by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMinute = LocalTime.now()
            delay(30_000)
        }
    }

    val day: DayPrayers = remember(lat, lng, methodId, madhabId) {
        if (lat != null && lng != null) {
            runCatching {
                computeDayPrayers(
                    lat!!, lng!!,
                    date = LocalDate.now(),
                    method = CalcMethod.fromId(methodId),
                    madhab = MadhabOption.fromId(madhabId)
                )
            }.getOrNull() ?: fallbackDayPrayers()
        } else {
            fallbackDayPrayers()
        }
    }

    val next = remember(day, nowMinute) { findNextPrayer(day, nowMinute) }
    val isFriday = LocalDate.now().dayOfWeek == DayOfWeek.FRIDAY
    val dhuhrLabel = if (isFriday) R.string.home_friday_prayer else R.string.home_prayer_dhuhr

    val rows = listOf(
        PrayerRow("fajr", R.string.home_prayer_fajr, day.fajr, next.key == "fajr"),
        PrayerRow("sunrise", R.string.home_prayer_sunrise, day.sunrise),
        PrayerRow("dhuhr", dhuhrLabel, day.dhuhr, next.key == "dhuhr"),
        PrayerRow("asr", R.string.home_prayer_asr, day.asr, next.key == "asr"),
        PrayerRow("maghrib", R.string.home_prayer_maghrib, day.maghrib, next.key == "maghrib"),
        PrayerRow("isha", R.string.home_prayer_isha, day.isha, next.key == "isha")
    )

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(60)
        isVisible = true
    }

    // Solid dark base — no full-screen pattern (pattern lives only on the card + settings).
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) { -30 }
                ) {
                    TopBar(
                        cityLabel = city,
                        notificationsOn = notifMaster,
                        onBellClick = onOpenSettings,
                        onLocationClick = onOpenSettings
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, 80)) + slideInVertically(
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) { 50 }
                ) {
                    NextPrayerCard(
                        nextKey = next.key,
                        nextTime = next.time,
                        isTomorrow = next.isTomorrow
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, 200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.home_prayers_title),
                            color = AtharTextPrimary,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            itemsIndexed(rows) { index, row ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(350, 250 + index * 50)) +
                        slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { 30 }
                ) {
                    PrayerItem(row)
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    cityLabel: String?,
    notificationsOn: Boolean,
    onBellClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(AtharCardSurface)
                .border(1.dp, AtharCardBorder, RoundedCornerShape(18.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onLocationClick
                )
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                cityLabel ?: stringResource(R.string.home_location_not_set),
                color = AtharTextPrimary,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AtharCardSurface)
                .border(1.dp, AtharCardBorder, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBellClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (notificationsOn) Icons.Outlined.Notifications else Icons.Outlined.NotificationsOff,
                contentDescription = null,
                tint = if (notificationsOn) AtharPrimaryLight else AtharTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Featured Next Prayer card with the Islamic lattice baked in.
 * The per-second countdown lives here so only the card ticks —
 * the rest of the screen stays still. "Time Remaining" and the
 * countdown pill sit together in one row.
 */
@Composable
fun NextPrayerCard(
    nextKey: String,
    nextTime: LocalTime,
    isTomorrow: Boolean
) {
    // Second ticker scoped to this card only.
    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1000)
        }
    }

    val scale = remember { Animatable(0.95f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val nameRes = when (nextKey) {
        "fajr" -> R.string.home_prayer_fajr
        "dhuhr" -> {
            if (LocalDate.now().dayOfWeek == DayOfWeek.FRIDAY) R.string.home_friday_prayer
            else R.string.home_prayer_dhuhr
        }
        "asr" -> R.string.home_prayer_asr
        "maghrib" -> R.string.home_prayer_maghrib
        else -> R.string.home_prayer_isha
    }
    val prayerName = stringResource(nameRes)

    val remaining = remember(nextTime, now, isTomorrow) {
        val targetSecs = nextTime.toSecondOfDay().toLong()
        val nowSecs = now.toSecondOfDay().toLong()
        val diff = if (isTomorrow || targetSecs <= nowSecs) {
            (24 * 3600 - nowSecs) + targetSecs
        } else {
            targetSecs - nowSecs
        }
        Duration.ofSeconds(diff)
    }
    val remainingText = String.format(
        "%02d:%02d:%02d",
        remaining.toHours(),
        remaining.toMinutesPart(),
        remaining.toSecondsPart()
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .scale(scale.value)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(AtharGradientStart, AtharGradientEnd)
                )
            )
    ) {
        // Static pattern layer inside the card.
        IslamicPatternBackground(
            modifier = Modifier.matchParentSize(),
            alpha = 0.10f,
            animated = false,
            cellDp = 64f
        )
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isTomorrow) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AtharPrimary.copy(alpha = 0.16f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            stringResource(R.string.home_tomorrow),
                            color = AtharPrimaryLight,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    stringResource(R.string.home_next_prayer),
                    color = AtharPrimaryLight,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                prayerName,
                color = AtharTextPrimary,
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                stringResource(R.string.home_prayer_iqamah, prayerName),
                color = AtharTextSecondary,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                nextTime.format(timeFmt),
                color = AtharPrimaryLight,
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Time Remaining label + countdown pill together in one row.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.home_time_remaining),
                    color = AtharPrimaryLight,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AtharPrimary.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        remainingText,
                        color = AtharPrimaryLight,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
        // Breathing glow border isolated in its own overlay —
        // only this thin layer recomposes each pulse frame.
        CardGlowBorder()
    }
}

/** Isolated pulsing border overlay so the card content never recomposes for it. */
@Composable
private fun BoxScope.CardGlowBorder() {
    val transition = rememberInfiniteTransition(label = "borderPulse")
    val borderAlpha by transition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    Spacer(
        modifier = Modifier
            .matchParentSize()
            .border(
                1.dp,
                AtharPrimary.copy(alpha = borderAlpha * 0.40f),
                RoundedCornerShape(20.dp)
            )
    )
}

@Composable
fun PrayerItem(row: PrayerRow) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 3.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (row.isNext) {
                    Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(AtharPrimarySubtle.copy(alpha = 0.6f), AtharCardSurface)
                            )
                        )
                        .border(1.dp, AtharPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                } else {
                    Modifier
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(14.dp))
                }
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                row.time.format(timeFmt),
                color = if (row.isNext) AtharPrimaryLight else AtharTextSecondary,
                fontFamily = ThmanyahSans,
                fontWeight = if (row.isNext) FontWeight.Black else FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(row.nameResId),
                    color = AtharTextPrimary,
                    fontFamily = ThmanyahSans,
                    fontWeight = if (row.isNext) FontWeight.Black else FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (row.isNext) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(AtharPrimaryLight, CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AtharTheme {
        Box(Modifier.background(AtharBackground)) {
            NextPrayerCard("maghrib", LocalTime.of(19, 6), false)
        }
    }
}

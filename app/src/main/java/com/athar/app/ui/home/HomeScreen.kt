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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.LocationHelper
import com.athar.app.data.CalcMethod
import com.athar.app.data.DayPrayers
import com.athar.app.data.MadhabOption
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.computeDayPrayers
import com.athar.app.data.fallbackDayPrayers
import com.athar.app.data.findNextPrayer
import com.athar.app.data.formatDigits
import com.athar.app.ui.components.AtharAnimatedLogo
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharGradientEnd
import com.athar.app.ui.theme.AtharGradientStart
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.corner.Dua
import com.athar.app.ui.corner.afterPrayerFajrDuas
import com.athar.app.ui.corner.afterPrayerMaghribDuas
import com.athar.app.ui.corner.afterPrayerOtherDuas
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharTheme
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class PrayerRow(val key: String, val nameResId: Int, val time: LocalTime, val isNext: Boolean = false)

private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit = {},
    onOpenNotifications: () -> Unit = onOpenSettings,
    onNavigateToDuas: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context.applicationContext) }

    val lat by prefs.latitude.collectAsState(initial = null)
    val lng by prefs.longitude.collectAsState(initial = null)
    val city by prefs.cityLabel.collectAsState(initial = null)
    val selectedLanguage by prefs.selectedLanguage.collectAsState(initial = "ar")
    val methodId by prefs.calcMethodId.collectAsState(initial = "MWL")
    val madhabId by prefs.madhabId.collectAsState(initial = "SHAFI")
    val notifMaster by prefs.notificationsMaster.collectAsState(initial = false)
    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)

    var showAfterPrayerDialog by remember { mutableStateOf(false) }

    // Dynamic resolution of device location in the selected language
    var resolvedCity by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(lat, lng, selectedLanguage) {
        if (lat != null && lng != null) {
            val resolved = LocationHelper.resolveCityName(context, lat!!, lng!!, selectedLanguage)
            if (resolved != null) {
                resolvedCity = resolved
                if (city != resolved) {
                    prefs.setLocation(lat!!, lng!!, resolved)
                }
            } else if (city == null || city == "موقعي" || city.equals("My location", ignoreCase = true)) {
                val fallback = if (selectedLanguage == "en") "My Location" else "موقعي"
                resolvedCity = fallback
                if (city != fallback) {
                    prefs.setLocation(lat!!, lng!!, fallback)
                }
            }
        }
    }

    val currentCityLabel = resolvedCity ?: city?.let {
        if (it == "موقعي" && selectedLanguage == "en") "My Location"
        else if (it.equals("My location", ignoreCase = true) && selectedLanguage == "ar") "موقعي"
        else it
    }

    // Minute-granularity clock for the "next" highlight — cheap, recomposes rarely.
    var nowMinute by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMinute = LocalTime.now()
            delay(30_000)
        }
    }

    val day: DayPrayers = remember(lat, lng, methodId, madhabId, LocalDate.now()) {
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

    val isVisible = true

    // Dark base with the Islamic lattice (Quran stays pure black).
    PatternScaffold {
        if (showAfterPrayerDialog) {
            AfterPrayerDuaDialog(
                nextKey = next.key,
                isFriday = isFriday,
                selectedLanguage = selectedLanguage,
                numberStyle = numberStyle,
                onDismiss = { showAfterPrayerDialog = false },
                onNavigateToDuas = {
                    showAfterPrayerDialog = false
                    onNavigateToDuas()
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TopBar(
                cityLabel = currentCityLabel,
                onLocationClick = onOpenSettings,
                onAfterPrayerClick = { showAfterPrayerDialog = true }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 150.dp)
            ) {
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
                        isTomorrow = next.isTomorrow,
                        numberStyle = numberStyle
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
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.home_prayers_title),
                            color = AtharTextPrimary,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )

                        // Notification Settings Pill
                        Row(
                            modifier = Modifier
                                .height(30.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(AtharCardSurface)
                                .border(1.dp, AtharCardBorder, RoundedCornerShape(15.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onOpenNotifications
                                )
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (notifMaster) Icons.Outlined.Notifications else Icons.Outlined.NotificationsOff,
                                contentDescription = null,
                                tint = if (notifMaster) AtharPrimaryLight else AtharTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                stringResource(R.string.home_notification_settings),
                                color = AtharTextPrimary,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            itemsIndexed(rows) { index, row ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(350, 250 + index * 50)) +
                        slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { 30 }
                ) {
                    PrayerItem(row, numberStyle = numberStyle)
                }
            }
        }
    }
}
}

@Composable
private fun TopBar(
    cityLabel: String?,
    onLocationClick: () -> Unit,
    onAfterPrayerClick: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 20.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AtharAnimatedLogo(
                modifier = Modifier.padding(start = 6.dp)
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Location Pill
                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(15.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onLocationClick
                        )
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = AtharPrimaryLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        cityLabel ?: stringResource(R.string.home_location_not_set),
                        color = AtharTextPrimary,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 2. After-Prayer Duas Pill
                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(15.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onAfterPrayerClick
                        )
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_prayer_hands),
                        contentDescription = null,
                        tint = AtharPrimaryLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        stringResource(R.string.home_after_prayer_adhkar),
                        color = AtharTextPrimary,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
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
    isTomorrow: Boolean,
    numberStyle: NumberStylePreference = NumberStylePreference.WESTERN
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
    val hours = remaining.toHours()
    val mins = remaining.toMinutesPart()
    val secs = remaining.toSecondsPart()
    val rawRemainingText = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
    val remainingText = formatDigits(rawRemainingText, numberStyle)

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
                formatDigits(nextTime.format(timeFmt), numberStyle),
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
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtharPrimary.copy(alpha = 0.20f))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            remainingText,
                            color = AtharPrimaryLight,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
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
fun PrayerItem(
    row: PrayerRow,
    numberStyle: NumberStylePreference = NumberStylePreference.WESTERN
) {
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
                formatDigits(row.time.format(timeFmt), numberStyle),
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

/**
 * Floating modal for reciting the supplications recommended after the currently
 * active prayer, featuring swiping cards, counting, and navigation to the full collection.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AfterPrayerDuaDialog(
    nextKey: String,
    isFriday: Boolean,
    selectedLanguage: String,
    numberStyle: NumberStylePreference,
    onDismiss: () -> Unit,
    onNavigateToDuas: () -> Unit
) {
    val isArabic = selectedLanguage == "ar"
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val (prayerTitleAr, prayerTitleEn, duas) = remember(nextKey, isFriday) {
        when (nextKey) {
            "sunrise", "dhuhr" -> Triple(
                "صلاة الفجر",
                "Fajr Prayer",
                afterPrayerFajrDuas
            )
            "asr" -> Triple(
                if (isFriday) "صلاة الجمعة" else "صلاة الظهر",
                if (isFriday) "Friday Prayer" else "Dhuhr Prayer",
                afterPrayerOtherDuas
            )
            "maghrib" -> Triple(
                "صلاة العصر",
                "Asr Prayer",
                afterPrayerOtherDuas
            )
            "isha" -> Triple(
                "صلاة المغرب",
                "Maghrib Prayer",
                afterPrayerMaghribDuas
            )
            else -> Triple(
                "صلاة العشاء",
                "Isha Prayer",
                afterPrayerOtherDuas
            )
        }
    }

    val headerTitle = if (isArabic) {
        "$prayerTitleAr • أذكار بعد الصلاة"
    } else {
        "$prayerTitleEn • After Prayer Adhkar"
    }

    var counts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val pagerState = rememberPagerState(pageCount = { duas.size })

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(AtharBackground)
                .border(1.dp, AtharCardBorder, RoundedCornerShape(24.dp))
        ) {
            IslamicPatternBackground(
                modifier = Modifier.fillMaxSize(),
                alpha = 0.08f,
                animated = false
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Header with close button and centered title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AtharCardSurface)
                            .border(1.dp, AtharCardBorder, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            tint = AtharTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = headerTitle,
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = AtharTextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(Modifier.size(36.dp))
                }

                Spacer(Modifier.height(12.dp))

                // Card Swipe Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val dua = duas[page]
                    val count = counts[dua.id] ?: 0
                    val target = dua.repeat
                    val isDone = count >= target

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(AtharCardSurface.copy(alpha = 0.95f))
                            .border(
                                width = 1.dp,
                                color = if (isDone) AtharPrimary.copy(alpha = 0.65f) else AtharCardBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .then(
                                if (isDone) {
                                    Modifier.shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        spotColor = AtharPrimaryLight.copy(alpha = 0.25f)
                                    )
                                } else Modifier
                            )
                            .combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    if (count < target) {
                                        val nextCount = count + 1
                                        counts = counts + (dua.id to nextCount)
                                        haptics.performHapticFeedback(
                                            if (nextCount >= target) HapticFeedbackType.LongPress
                                            else HapticFeedbackType.TextHandleMove
                                        )
                                    }
                                },
                                onLongClick = {
                                    if (count > 0) {
                                        counts = counts + (dua.id to 0)
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                            )
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Index and Source row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AtharPrimary.copy(alpha = 0.16f))
                                        .border(1.dp, AtharPrimary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 9.dp, vertical = 4.dp)
                                ) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = "${formatDigits((page + 1).toString(), numberStyle)} / ${formatDigits(duas.size.toString(), numberStyle)}",
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = AtharPrimaryLight
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AtharPrimary.copy(alpha = 0.12f))
                                        .padding(horizontal = 9.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = dua.source,
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = AtharPrimaryLight
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Scrollable dua text
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = dua.arabic,
                                    fontFamily = ThmanyahSerifText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    lineHeight = 34.sp,
                                    color = AtharTextPrimary,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = dua.translation,
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = AtharTextSecondary,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                val noteText = if (isArabic) dua.noteAr ?: dua.noteEn else dua.noteEn ?: dua.noteAr
                                if (!noteText.isNullOrBlank()) {
                                    Spacer(Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AtharPrimary.copy(alpha = 0.10f))
                                            .border(
                                                width = 1.dp,
                                                color = AtharPrimaryLight.copy(alpha = 0.35f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 7.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.Info,
                                                contentDescription = null,
                                                tint = AtharPrimaryLight,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = noteText,
                                                fontFamily = ThmanyahSans,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp,
                                                color = AtharTextPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Repetition Progress bar
                            if (target > 1 && count > 0) {
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(AtharCardBorder)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((count.toFloat() / target).coerceAtMost(1f))
                                            .height(3.dp)
                                            .background(if (isDone) AtharPrimaryLight else AtharPrimary)
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Counter action row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (count == 0) {
                                        if (isArabic) "اضغط للعدّ" else "Tap to count"
                                    } else if (isDone) {
                                        if (isArabic) "اكتمل الذكر" else "Completed"
                                    } else {
                                        if (isArabic) "استمر في العد" else "Keep counting"
                                    },
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.5.sp,
                                    color = if (isDone) AtharPrimaryLight else AtharTextSecondary.copy(alpha = 0.7f)
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isDone) AtharPrimary
                                            else AtharPrimary.copy(alpha = 0.22f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isDone) AtharPrimaryLight else AtharPrimary.copy(alpha = 0.45f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = if (count == 0) formatDigits("×$target", numberStyle)
                                                   else if (isDone) "${formatDigits(target.toString(), numberStyle)} ✓"
                                                   else "${formatDigits(count.toString(), numberStyle)} / ${formatDigits(target.toString(), numberStyle)}",
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.5.sp,
                                            color = if (isDone) AtharTextOnPrimary else AtharPrimaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Navigation controls (Previous, Page Indicator, Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val canPrev = pagerState.currentPage > 0
                    val canNext = pagerState.currentPage < duas.size - 1

                    // Previous Button
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (canPrev) AtharCardSurface else AtharCardSurface.copy(alpha = 0.4f))
                            .border(1.dp, if (canPrev) AtharCardBorder else AtharCardBorder.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .clickable(
                                enabled = canPrev,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    if (canPrev) {
                                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                    }
                                }
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dua_previous),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (canPrev) AtharTextPrimary else AtharTextSecondary.copy(alpha = 0.4f)
                        )
                    }

                    // Indicator
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "${formatDigits((pagerState.currentPage + 1).toString(), numberStyle)} / ${formatDigits(duas.size.toString(), numberStyle)}",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AtharPrimaryLight
                        )
                    }

                    // Next Button
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (canNext) AtharCardSurface else AtharCardSurface.copy(alpha = 0.4f))
                            .border(1.dp, if (canNext) AtharCardBorder else AtharCardBorder.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .clickable(
                                enabled = canNext,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    if (canNext) {
                                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                    }
                                }
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dua_next),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (canNext) AtharTextPrimary else AtharTextSecondary.copy(alpha = 0.4f)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Bottom Action Buttons: Close & Open Duas Tab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(AtharCardSurface)
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(22.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dua_action_close),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AtharTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.4f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(AtharPrimary)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onNavigateToDuas
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.dua_action_open_duas),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = AtharTextOnPrimary
                        )
                    }
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

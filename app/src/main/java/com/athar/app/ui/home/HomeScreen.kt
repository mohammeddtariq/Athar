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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.athar.app.R
import com.athar.app.ui.theme.*
import kotlinx.coroutines.delay

data class PrayerTime(val nameResId: Int, val time: String, val isCurrent: Boolean = false)

val samplePrayers = listOf(
    PrayerTime(R.string.home_prayer_fajr, "5:08"),
    PrayerTime(R.string.home_prayer_sunrise, "6:36"),
    PrayerTime(R.string.home_friday_prayer, "13:52"),
    PrayerTime(R.string.home_prayer_asr, "16:32"),
    PrayerTime(R.string.home_prayer_maghrib, "19:06", true),
    PrayerTime(R.string.home_prayer_isha, "20:33")
)

@Composable
fun HomeScreen() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(60)
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ─── Top Bar ───
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { -30 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(AtharCardSurface)
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(18.dp))
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
                            stringResource(R.string.home_location_label),
                            color = AtharTextPrimary,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    // Notification bell
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AtharCardSurface)
                            .border(1.dp, AtharCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = AtharTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ─── Featured Next Prayer Card ───
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500, 80)) + slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { 50 }
            ) {
                NextPrayerCard()
            }
        }

        // ─── Prayer Times Section Header ───
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🕌", fontSize = 15.sp)
                }
            }
        }

        // ─── Prayer List with fluid staggered animation ───
        itemsIndexed(samplePrayers) { index, prayer ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(350, 250 + index * 50)) +
                        slideInVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) { 30 }
            ) {
                PrayerItem(prayer)
            }
        }
    }
}

/**
 * Featured Next Prayer card with subtle ambient pulsing border and refined typography sizing.
 */
@Composable
fun NextPrayerCard() {
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

    // Ambient breathing glow on card border
    val infiniteTransition = rememberInfiniteTransition(label = "borderPulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val maghribName = stringResource(R.string.home_prayer_maghrib)

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
            .border(
                1.dp,
                AtharPrimary.copy(alpha = borderAlpha * 0.40f),
                RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            // Top row — label + star badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(AtharPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = AtharPrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
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

            // Prayer name — refined serif display
            Text(
                maghribName,
                color = AtharTextPrimary,
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subtitle
            Text(
                stringResource(R.string.home_prayer_iqamah, maghribName),
                color = AtharTextSecondary,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Time display — refined size
            Text(
                "19:06",
                color = AtharPrimaryLight,
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // View details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.ChevronLeft,
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    stringResource(R.string.home_view_details),
                    color = AtharPrimaryLight,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PrayerItem(prayer: PrayerTime) {
    val prayerName = stringResource(prayer.nameResId)

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 3.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (prayer.isCurrent) {
                    Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(AtharPrimarySubtle.copy(alpha = 0.5f), AtharCardSurface)
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
            // Time on left
            Text(
                prayer.time,
                color = if (prayer.isCurrent) AtharPrimaryLight else AtharTextSecondary,
                fontFamily = ThmanyahSans,
                fontWeight = if (prayer.isCurrent) FontWeight.Black else FontWeight.Bold,
                fontSize = 14.sp
            )

            // Name on right
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    prayerName,
                    color = if (prayer.isCurrent) AtharTextPrimary else AtharTextPrimary,
                    fontFamily = ThmanyahSans,
                    fontWeight = if (prayer.isCurrent) FontWeight.Black else FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (prayer.isCurrent) {
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
        HomeScreen()
    }
}

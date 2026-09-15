package com.athar.app.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.athar.app.R
import com.athar.app.ui.theme.*

data class PrayerTime(val nameResId: Int, val time: String, val isCurrent: Boolean = false)

val samplePrayers = listOf(
    PrayerTime(R.string.home_prayer_fajr, "5:08 AM"),
    PrayerTime(R.string.home_prayer_sunrise, "6:36 AM"),
    PrayerTime(R.string.home_friday_prayer, "1:52 PM"),
    PrayerTime(R.string.home_prayer_asr, "4:32 PM"),
    PrayerTime(R.string.home_prayer_maghrib, "7:06 PM", true),
    PrayerTime(R.string.home_prayer_isha, "8:33 PM")
)

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = AtharCardSurface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.home_location_label),
                        color = AtharPrimary,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AtharPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Prayer Arc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(280.dp)) {
                // Background arc
                drawArc(
                    color = AtharCardSurface,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Round)
                )
                // Progress arc — sage accent
                drawArc(
                    color = AtharPrimary,
                    startAngle = 180f,
                    sweepAngle = 120f, // Progress to current prayer
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val maghribName = stringResource(R.string.home_prayer_maghrib)
                Text(
                    stringResource(R.string.home_prayer_iqamah, maghribName),
                    color = AtharTextSecondary,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    maghribName,
                    color = AtharPrimary,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "7:06 PM",
                    color = AtharPrimaryLight,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Prayer List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(samplePrayers) { prayer ->
                PrayerItem(prayer)
            }
        }
    }
}

@Composable
fun PrayerItem(prayer: PrayerTime) {
    val prayerName = stringResource(prayer.nameResId)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (prayer.isCurrent) AtharPrimary.copy(alpha = 0.08f) else AtharCardSurface.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (prayer.isCurrent) AtharPrimary else AtharNavIconInactive
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    prayer.time,
                    color = if (prayer.isCurrent) AtharPrimary else AtharTextPrimary,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                prayerName,
                color = if (prayer.isCurrent) AtharPrimary else AtharTextPrimary,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge
            )
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

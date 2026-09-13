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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.athar.app.ui.theme.AtharTheme
import com.athar.app.ui.theme.AtharBlack
import com.athar.app.ui.theme.AtharCardGray
import com.athar.app.ui.theme.AtharGold

data class PrayerTime(val name: String, val time: String, val isCurrent: Boolean = false)

val samplePrayers = listOf(
    PrayerTime("الفجر", "5:08 ص"),
    PrayerTime("الشروق", "6:36 ص"),
    PrayerTime("الجمعة", "13:52 م"),
    PrayerTime("العصر", "4:32 م"),
    PrayerTime("المغرب", "7:06 م", true),
    PrayerTime("العشاء", "8:33 م")
)

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBlack)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = AtharCardGray,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("العبور", color = AtharGold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = AtharGold, modifier = Modifier.size(16.dp))
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
                drawArc(
                    color = Color.DarkGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = AtharGold,
                    startAngle = 180f,
                    sweepAngle = 120f, // Progress to current prayer
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("إقامة المغرب", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                Text("المغرب", color = AtharGold, style = MaterialTheme.typography.titleLarge)
                Text("7:06 م", color = AtharGold, style = MaterialTheme.typography.displayLarge)
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (prayer.isCurrent) AtharGold.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = if (prayer.isCurrent) AtharGold else Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(prayer.time, color = if (prayer.isCurrent) AtharGold else Color.White, style = MaterialTheme.typography.bodyLarge)
            }
            Text(prayer.name, color = if (prayer.isCurrent) AtharGold else Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
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

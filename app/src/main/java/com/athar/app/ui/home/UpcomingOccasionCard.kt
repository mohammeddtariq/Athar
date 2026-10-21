package com.athar.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.HijriDateHelper
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.OccasionInstance
import com.athar.app.data.formatDigits
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans

@Composable
fun UpcomingOccasionCard(
    occasion: OccasionInstance,
    language: String,
    numberStyle: NumberStylePreference
) {
    val isArabic = language == "ar"
    val title = if (isArabic) occasion.occasion.titleAr else occasion.occasion.titleEn
    val note = if (isArabic) occasion.occasion.noteAr else occasion.occasion.noteEn
    val hijri = remember(occasion, isArabic, numberStyle) {
        HijriDateHelper.formatHijriDate(occasion.date, isArabic, numberStyle)
    }
    val gregorian = remember(occasion, isArabic, numberStyle) {
        HijriDateHelper.formatGregorianDate(occasion.date, isArabic, numberStyle)
    }
    val countdown = if (occasion.daysAway == 0L) {
        stringResource(R.string.occasion_today)
    } else {
        pluralStringResource(
            R.plurals.occasion_in_days,
            occasion.daysAway.toInt(),
            formatDigits(occasion.daysAway.toString(), numberStyle)
        )
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .fillMaxWidth()
            .background(AtharCardSurface.copy(alpha = 0.85f), RoundedCornerShape(18.dp))
            .border(1.dp, AtharCardBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(AtharPrimary.copy(alpha = 0.16f), CircleShape)
                .border(1.dp, AtharPrimary.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Star,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp,
                color = AtharTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "$hijri · $gregorian",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = AtharTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = note,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = AtharTextSecondary.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .background(AtharPrimary.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
                .border(1.dp, AtharPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(horizontal = 9.dp, vertical = 5.dp)
        ) {
            Text(
                text = countdown,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                color = AtharPrimaryLight
            )
        }
    }
}

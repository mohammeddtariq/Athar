package com.athar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans

val SCHOOL_IDS = listOf("HANAFI", "MALIKI", "SHAFII", "HANBALI")

fun schoolDisplayName(id: String): Int = when (id) {
    "HANAFI" -> R.string.school_hanafi
    "MALIKI" -> R.string.school_maliki
    "HANBALI" -> R.string.school_hanbali
    else -> R.string.school_shafii
}

/**
 * School picker (Hanafi / Maliki / Shafii / Hanbali) + "Hanafi Asr" switch,
 * with the explanatory note. Shared by onboarding and settings.
 */
@Composable
fun SchoolSelector(
    schoolId: String,
    onPickSchool: (String) -> Unit,
    showTitle: Boolean = true
) {
    if (showTitle) {
        Text(
            text = stringResource(R.string.settings_school),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = AtharTextPrimary
        )
        Spacer(Modifier.height(8.dp))
    }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SCHOOL_IDS.forEach { id ->
            val selected = schoolId == id
            val border by animateColorAsState(
                targetValue = if (selected) AtharPrimary else AtharCardBorder,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "schoolBorder"
            )
            val bg by animateColorAsState(
                targetValue = if (selected) AtharPrimary.copy(alpha = 0.12f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "schoolBg"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, border, RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onPickSchool(id) }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(schoolDisplayName(id)),
                        fontFamily = ThmanyahSans,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                        fontSize = 13.5.sp,
                        color = if (selected) AtharPrimaryLight else AtharTextPrimary
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(AtharPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Check, null,
                                tint = AtharTextOnPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hanafi Asr switch (mirrors the school: on = Hanafi, off = Shafii)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.hanafi_asr),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.5.sp,
                        color = AtharTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.hanafi_asr_sub),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = AtharTextSecondary
                    )
                }
                Switch(
                    checked = schoolId == "HANAFI",
                    onCheckedChange = { on -> onPickSchool(if (on) "HANAFI" else "SHAFII") },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AtharTextOnPrimary,
                        checkedTrackColor = AtharPrimary,
                        uncheckedThumbColor = AtharTextSecondary,
                        uncheckedTrackColor = AtharCardBorder
                    )
                )
            }
        }

        Text(
            text = stringResource(R.string.school_note),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            color = AtharTextSecondary
        )
    }
}

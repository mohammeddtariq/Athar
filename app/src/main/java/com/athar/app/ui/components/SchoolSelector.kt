package com.athar.app.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans

/**
 * Asr school setting. Shafii, Maliki and Hanbali all share the same Asr
 * time, so the only real choice is the "Hanafi Asr" toggle (larger shadow
 * length = later Asr), with the explanatory note underneath.
 * Shared by onboarding and settings.
 */
@Composable
fun HanafiAsrSetting(
    isHanafi: Boolean,
    onToggle: (Boolean) -> Unit,
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                .background(
                    if (isHanafi) AtharPrimary.copy(alpha = 0.12f)
                    else androidx.compose.ui.graphics.Color.Transparent
                )
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
                    checked = isHanafi,
                    onCheckedChange = onToggle,
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

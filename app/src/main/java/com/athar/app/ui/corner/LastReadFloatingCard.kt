package com.athar.app.ui.corner

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay

/**
 * Centered floating card displayed above the bottom navigation dock.
 * Reminds the user of their exact last read position (Surah and page)
 * with a quick action to resume reading seamlessly.
 */
@Composable
fun LastReadFloatingCard(
    surahName: String,
    pageNumber: Int,
    numberStyle: NumberStylePreference,
    modifier: Modifier = Modifier,
    onContinueReading: () -> Unit,
    onDismiss: () -> Unit
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val pageNumberStr = formatDigits(pageNumber.toString(), numberStyle)

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 420.dp)
            .shadow(elevation = 14.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x33000000))
            .clip(RoundedCornerShape(20.dp))
            .background(AtharCardSurface)
            .border(1.dp, AtharCardBorder, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onContinueReading
            )
    ) {
        IslamicPatternBackground(
            modifier = Modifier.matchParentSize(),
            alpha = 0.05f,
            animated = false
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Icon Container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AtharPrimarySubtle)
                    .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_quran_book),
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Information Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.quran_last_read_title),
                        fontFamily = ThmanyahSans,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AtharTextSecondary
                    )
                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = AtharTextSecondary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.quran_last_read_page, pageNumberStr),
                        fontFamily = ThmanyahSans,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AtharPrimaryLight
                    )
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = surahName,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = AtharTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // Continue Reading Action Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AtharPrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onContinueReading
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.quran_last_read_continue),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = AtharTextOnPrimary
                    )
                    Icon(
                        imageVector = if (isRtl) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = AtharTextOnPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // Close button
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = AtharTextSecondary.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

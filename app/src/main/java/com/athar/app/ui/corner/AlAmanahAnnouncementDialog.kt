package com.athar.app.ui.corner

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import java.util.Locale

/**
 * Centered floating Announcement Dialog for "Al-Amanah" (الأَمَنَة).
 *
 * Introducing the serene sanctuary recitation player with synced verses,
 * smooth scrolling, offline playback, and listening insights.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlAmanahAnnouncementDialog(
    onExplore: () -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Live glowing sage/emerald transition
    val infiniteTransition = rememberInfiniteTransition(label = "amanahGlow")
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amanahGlowPhase"
    )

    val borderAlpha = 0.45f + (glowPhase * 0.45f)
    val glowBrush = Brush.linearGradient(
        colors = listOf(
            AtharPrimaryLight.copy(alpha = borderAlpha),
            Color(0xFFB5DB88).copy(alpha = borderAlpha * 0.90f),
            AtharPrimary.copy(alpha = borderAlpha * 0.65f),
            Color(0xFF8EB878).copy(alpha = borderAlpha * 0.85f),
            AtharPrimaryLight.copy(alpha = borderAlpha)
        ),
        start = Offset(0f, 0f),
        end = Offset(800f * glowPhase, 600f)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .widthIn(max = 400.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AtharCardSurface)
                    .border(
                        width = 1.2.dp,
                        brush = glowBrush,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                // Background pattern watermark
                IslamicPatternBackground(
                    modifier = Modifier.matchParentSize(),
                    alpha = 0.05f,
                    animated = false
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ─── TOP BAR: QUICK EXPLORE PILL & DISMISS "X" ───
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(AtharPrimary.copy(alpha = 0.18f))
                                .border(1.dp, AtharPrimary.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onExplore
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isArabic) "استمع الآن" else "Listen Now",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AtharPrimaryLight
                            )
                            Icon(
                                imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = AtharPrimaryLight,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Dismiss "X" Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = if (isArabic) "إغلاق" else "Close",
                                tint = AtharTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ─── ICON BADGE ───
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AtharPrimarySubtle)
                            .border(1.dp, AtharPrimary.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            tint = Color(0xFFB5DB88),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // ─── MAIN TITLE ───
                    if (isArabic) {
                        Text(
                            text = "نُقَدِّمُ لَكُمْ",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = AtharTextSecondary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "الأَمَنَة",
                            fontFamily = ThmanyahSerifDisplay,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = AtharTextPrimary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "Introducing",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = AtharTextSecondary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Al-Amanah (الأَمَنَة)",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = AtharTextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // ─── QURANIC ORIGIN ───
                    Text(
                        text = if (isArabic) {
                            "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾ • الرعد: ٢٨"
                        } else {
                            "\"Verily, in the remembrance of Allah do hearts find rest.\" • Ar-Ra'd: 28"
                        },
                        fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = if (isArabic) 13.sp else 11.5.sp,
                        color = Color(0xFFB5DB88),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // ─── CORE BIO (Poetic couplet) ───
                    Text(
                        text = if (isArabic) {
                            "«أَمَانَة: لِعَقْلٍ لَا يَهْدَأ، يَلْقَى فِيهَا بَيْنَ كُلِّ آيَةٍ سَلَامًا مَعَ كَلَامِ الله»"
                        } else {
                            "\"Al-Amanah: A sanctuary for a restless mind, bringing stillness between every verse with the words of Allah.\""
                        },
                        fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = if (isArabic) 12.5.sp else 11.5.sp,
                        color = AtharPrimaryLight.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center,
                        lineHeight = if (isArabic) 20.sp else 16.5.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    // ─── FEATURE DESCRIPTION ───
                    Text(
                        text = if (isArabic) {
                            "مساحةُ استماعٍ هادئةٌ لتلاواتٍ مُختارةٍ من نخبةٍ من القرّاء، مع تتبّعٍ متزامنٍ للآيات، وقوائمَ روحانيّة، وتشغيلٍ في الخلفيّةِ ومؤقّتِ نوم، وإحصاءاتِ استماعٍ لطيفة."
                        } else {
                            "A serene listening space with curated recitations by renowned reciters, synchronized verse tracking, spiritual playlists, background playback, a sleep timer and gentle listening insights."
                        },
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = AtharTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(22.dp))

                    // ─── BOTTOM EXPLORE ACTION BUTTON ───
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AtharPrimary)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onExplore
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isArabic) "ادخل إلى الأَمَنَة" else "Enter Al-Amanah",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AtharTextOnPrimary
                            )
                            Icon(
                                imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = AtharTextOnPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

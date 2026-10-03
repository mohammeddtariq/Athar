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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import java.util.Locale

/**
 * Centered floating App Announcement Dialog.
 *
 * Designed as a generic and extensible modal for major app updates and feature announcements.
 * Currently highlights the flagship feature "Al-Dirayah" (الدِّرَايَة).
 *
 * Design features:
 * - Clean solid dark card surface with no distracting background patterns.
 * - Live glowing flowing gold border matching the feature card.
 * - Classical Riqaah calligraphy with full tashkeel (الدِّرَايَة) and phonetic guide (Ad-Dirāyah).
 * - Ornate poetic contemplation couplet and five classical exegeses badges.
 * - Athar signature sage-green explore button (matching the onboarding setup theme).
 * - Dual action buttons at the top: Try Now and Close.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppAnnouncementDialog(
    onExplore: () -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Live glowing gold animation matching the Dirayah card
    val infiniteTransition = rememberInfiniteTransition(label = "announcementGoldGlow")
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "announcementGlowPhase"
    )

    val borderAlpha = 0.45f + (glowPhase * 0.45f)
    val glowBrush = Brush.linearGradient(
        colors = listOf(
            AtharPrimaryLight.copy(alpha = borderAlpha),
            Color(0xFFE5C158).copy(alpha = borderAlpha * 0.90f),
            AtharPrimary.copy(alpha = borderAlpha * 0.65f),
            Color(0xFFF3D279).copy(alpha = borderAlpha * 0.85f),
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
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 22.dp, vertical = 24.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF111510))
                    .border(
                        width = 1.3.dp,
                        brush = glowBrush,
                        shape = RoundedCornerShape(26.dp)
                    )
                    .shadow(
                        elevation = 28.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = Color(0xFFE5C158).copy(alpha = 0.35f),
                        ambientColor = Color.Black
                    )
            ) {
                // Islamic geometric watermark pattern strictly inside this announcement card
                IslamicPatternBackground(
                    modifier = Modifier.matchParentSize(),
                    alpha = 0.07f,
                    animated = false
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ─── TOP BAR: DUAL ACTION BUTTONS (TRY NOW & DISMISS) ───
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Quick Explore Pill with Arrow
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
                                text = if (isArabic) "جرّبها الآن" else "Try It Out",
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

                    Spacer(Modifier.height(14.dp))

                    // ─── ICON & TAFSIR BADGE ───
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AtharPrimary.copy(alpha = 0.22f))
                            .border(1.2.dp, Color(0xFFE5C158).copy(alpha = 0.70f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFFF3D279),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // "تفسير" / "Tafsir" Label
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtharPrimary.copy(alpha = 0.25f))
                            .border(0.7.dp, AtharPrimaryLight.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 2.5.dp)
                    ) {
                        Text(
                            text = if (isArabic) "تَفْسِيرُ القُرْآنِ الكَرِيم" else "Quranic Exegesis & Tafsir",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AtharPrimaryLight,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // ─── MAIN TITLE (Riqaah / Classical Calligraphic Style) ───
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
                            text = "الدِّرَايَة",
                            fontFamily = ThmanyahSerifDisplay,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color(0xFFF3D279),
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
                            text = "Al-Dirayah (Ad-Dirāyah)",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp,
                            color = Color(0xFFF3D279),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ─── POETIC COUPLET ───
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1A2218))
                            .border(0.8.dp, Color(0xFFE5C158).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) {
                                "«تَأَمَّلْ كَلَامَ اللهِ تَلْقَ هِدَايَةً • وَدِرَايَةُ التَّنْزِيلِ نُورُ البَصَائِرِ»"
                            } else {
                                "\"Contemplate the words of Allāh to find true guidance; for understanding the Revelation is the illumination of hearts.\""
                            },
                            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = if (isArabic) 13.5.sp else 12.sp,
                            color = Color(0xFFE8E5DD),
                            textAlign = TextAlign.Center,
                            lineHeight = if (isArabic) 22.sp else 18.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ─── FEATURE BIO ───
                    Text(
                        text = if (isArabic) {
                            "بوابتكم لتدبر آيات الذكر الحكيم عبر أمهات كتب التفسير الخمسة المعتمدة، بتجربة استحضار عصرية دقيقة وواضحة."
                        } else {
                            "Your gateway to deep reflection on the Holy Quran, bringing together the five classical authoritative exegeses in an elegant, modern experience."
                        },
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.5.sp,
                        color = Color(0xFFC5C9C0),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // ─── 5 CLASSICAL BOOKS CHIPS ───
                    val classicalBooks = if (isArabic) {
                        listOf("السعدي", "ابن كثير", "الطبري", "القرطبي", "الميسر")
                    } else {
                        listOf("Al-Sa'di", "Ibn Kathir", "Al-Tabari", "Al-Qurtubi", "Al-Muyassar")
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        classicalBooks.forEach { book ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1C241A))
                                    .border(0.6.dp, Color(0xFFE5C158).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = book,
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFDDD8CE)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // ─── BOTTOM SAGE GREEN EXPLORE ACTION BUTTON (MATCHING SETUP "BEGIN" THEME) ───
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AtharPrimary)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onExplore
                            )
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isArabic) "استكشف الدِّرَايَة الآن" else "Explore Al-Dirayah Now",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = AtharTextOnPrimary
                            )
                            Icon(
                                imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = AtharTextOnPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

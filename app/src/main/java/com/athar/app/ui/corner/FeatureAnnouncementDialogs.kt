package com.athar.app.ui.corner

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimaryMuted
import com.athar.app.ui.theme.AtharPrimarySubtle
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharThemeId
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import java.util.Locale

/**
 * Announcement for the Tawasheeh collection in Al-Amanah.
 *
 * A quiet, poetic introduction to the tawasheeh recordings, pointing at the
 * playlist that now shares the Quran player.
 */
@Composable
fun TawasheehAnnouncementDialog(
    onExplore: () -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    AnnouncementFrame(
        pillLabel = if (isArabic) "استمع الآن" else "Listen Now",
        onPill = onExplore,
        onDismiss = onDismiss,
        isArabic = isArabic
    ) {
        IconBadge(
            icon = Icons.Rounded.LibraryMusic,
            tint = AtharPrimaryLight,
            background = AtharPrimarySubtle
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = if (isArabic) "نُقَدِّمُ لَكُمْ" else "Introducing",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = AtharTextSecondary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = if (isArabic) "التَّوَاشِيحُ وَالإِبْتِهَالَات" else "Tawasheeh & Supplications",
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = if (isArabic) 25.sp else 20.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(14.dp))

        // A couplet written for the collection: voices turning yearning into
        // light, without naming any singer.
        Text(
            text = if (isArabic) {
                "«إِذَا حَنَّتِ الأَصْوَاتُ فِي اللَّيْلِ تَنَفَّسَتْ\n" +
                    "بِأَنْغَامِهَا الأَرْوَاحُ وَانْجَلَّ حُزْنُهَا»"
            } else {
                "\"When the voices yearn through the night,\n" +
                    "souls draw breath from their melody, and sorrow lifts.\""
            },
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = if (isArabic) 14.sp else 12.5.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center,
            lineHeight = if (isArabic) 26.sp else 19.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (isArabic) {
                "قائمةٌ كاملةٌ من التواشيح والإبتهالات، في المشغّل نفسه الذي تعرفه: غلافٌ هادئ، وقائمةُ تشغيلٍ تُسحب، وتنزيلٌ للاستماع بلا اتصال."
            } else {
                "A complete collection of tawasheeh and supplications, in the player you already know: a calm cover, a draggable queue and offline downloads."
            },
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(22.dp))

        PrimaryAction(
            label = if (isArabic) "ادخل إلى التواشيح" else "Enter Tawasheeh",
            isArabic = isArabic,
            onClick = onExplore
        )
    }
}

/**
 * Announcement for the new theming engine. The preview inside the dialog is
 * live: tapping a theme animates the mock screen's colours so the user can try
 * the palettes before opening the settings section.
 */
@Composable
fun ThemeEngineAnnouncementDialog(
    onExplore: () -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    var preview by remember { mutableStateOf(AtharThemeId.entries.first()) }

    AnnouncementFrame(
        pillLabel = if (isArabic) "جرّب الآن" else "Try it",
        onPill = onExplore,
        onDismiss = onDismiss,
        isArabic = isArabic
    ) {
        IconBadge(
            icon = Icons.Rounded.Palette,
            tint = AtharPrimaryLight,
            background = AtharPrimarySubtle
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = if (isArabic) "جَدِيدٌ فِي أَثَر" else "New in Athar",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = AtharTextSecondary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = if (isArabic) "مُحَرِّكُ الأَثْوَاب" else "The Theming Engine",
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = if (isArabic) 25.sp else 20.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = if (isArabic) {
                "أربعةُ أثوابٍ تُغَيِّرُ لونَ أثرَ كلّه، مع نغماتِ لونٍ تُكملُ الطابع. جرّبها هنا قبل أن تحفظها."
            } else {
                "Four palettes repaint all of Athar, each with accent tones that complete the mood. Try them right here before you keep one."
            },
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(16.dp))

        ThemePreview(
            selected = preview,
            isArabic = isArabic,
            onPick = { preview = it }
        )

        Spacer(Modifier.height(22.dp))

        PrimaryAction(
            label = if (isArabic) "افتح إعدادات المظهر" else "Open Theme Settings",
            isArabic = isArabic,
            onClick = onExplore
        )
    }
}

/** A live miniature of the app, recoloured whenever a theme is tapped. */
@Composable
private fun ThemePreview(
    selected: AtharThemeId,
    isArabic: Boolean,
    onPick: (AtharThemeId) -> Unit
) {
    val palette = selected.palette
    val spec = tween<Color>(durationMillis = 520, easing = FastOutSlowInEasing)
    val background by animateColorAsState(palette.background, spec, label = "previewBackground")
    val surface by animateColorAsState(palette.cardSurface, spec, label = "previewSurface")
    val border by animateColorAsState(palette.cardBorder, spec, label = "previewBorder")
    val primary by animateColorAsState(palette.primary, spec, label = "previewPrimary")
    val onPrimary by animateColorAsState(palette.textOnPrimary, spec, label = "previewOnPrimary")
    val textPrimary by animateColorAsState(palette.textPrimary, spec, label = "previewTextPrimary")
    val textSecondary by animateColorAsState(palette.textSecondary, spec, label = "previewTextSecondary")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(background)
                .border(1.dp, border, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(primary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "أَثَر" else "ATHAR",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = textPrimary
                        )
                        Text(
                            text = selected.let { if (isArabic) it.nameAr else it.nameEn },
                            fontFamily = ThmanyahSans,
                            fontSize = 9.sp,
                            color = textSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 26.dp, height = 14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(primary.copy(alpha = 0.85f))
                    )
                }

                Spacer(Modifier.height(12.dp))

                // A prayer card, drawn with the theme's own surfaces.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(surface)
                        .border(1.dp, border, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(width = 3.dp, height = 30.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(primary)
                        )
                        Spacer(Modifier.width(9.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(width = 74.dp, height = 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(textPrimary.copy(alpha = 0.75f))
                            )
                            Spacer(Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 48.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(textSecondary.copy(alpha = 0.65f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(primary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(onPrimary)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // A bottom dock pill in the accent colour.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(primary.copy(alpha = 0.18f))
                        .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 42.dp, height = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(primary)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AtharThemeId.entries.forEach { theme ->
                val isSelected = theme == selected
                val ring by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = 0.55f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "themeRing"
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(
                            width = (1 + ring * 1.5f).dp,
                            color = if (isSelected) theme.palette.primaryLight else Color.White.copy(alpha = 0.14f),
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onPick(theme) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size((17 + ring * 3).dp)
                            .clip(CircleShape)
                            .background(theme.palette.primary)
                    )
                }
            }
        }
    }
}

/** The floating announcement card, shared by every feature dialog. */
@Composable
private fun AnnouncementFrame(
    pillLabel: String,
    onPill: () -> Unit,
    onDismiss: () -> Unit,
    isArabic: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    val infiniteTransition = rememberInfiniteTransition(label = "announcementGlow")
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
            AtharPrimary.copy(alpha = borderAlpha * 0.90f),
            AtharPrimary.copy(alpha = borderAlpha * 0.65f),
            AtharPrimaryMuted.copy(alpha = borderAlpha * 0.85f),
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
                IslamicPatternBackground(
                    modifier = Modifier.matchParentSize(),
                    alpha = 0.05f,
                    animated = false
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                                    onClick = onPill
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = pillLabel,
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

                    content()
                }
            }
        }
    }
}

@Composable
private fun IconBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    background: Color
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, AtharPrimary.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun PrimaryAction(
    label: String,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AtharPrimary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
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

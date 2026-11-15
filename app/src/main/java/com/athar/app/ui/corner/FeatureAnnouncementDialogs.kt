package com.athar.app.ui.corner

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.theme.AtharAccentTone
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
import com.athar.app.ui.theme.ink
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Announcement for the Tawasheeh collection in Al-Amanah.
 *
 * A quiet, poetic introduction to the Naqshabandy recordings, pointing at the
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

        // A later couplet from "Mawlaya", the one the collection's listeners
        // remember: who else sees the heart, and all creation is a shadow.
        Text(
            text = if (isArabic) {
                "«مَنْ لِي سِوَاكَ؟ وَمَنْ سِوَاكَ يَرَى قَلْبِي وَيَسْمَعُهُ؟ " +
                    "كُلُّ الْخَلَائِقِ ظِلٌّ فِي يَدِ الصَّمَدِ»"
            } else {
                "\"Who is there for me but You? Who but You sees my heart and hears it? " +
                    "All creation is a shadow in the hand of the Eternal.\""
            },
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Italic,
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
 * Announcement for the new theming system. The preview walks through every
 * palette on its own until the user touches it, then it follows their taps:
 * theme, accent tone, and a full miniature of the app recoloured live.
 */
@Composable
fun ThemeEngineAnnouncementDialog(
    onExplore: () -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    var preview by remember { mutableStateOf(AtharThemeId.entries.first()) }
    var accent by remember { mutableStateOf(AtharAccentTone.NATIVE) }
    var touched by remember { mutableStateOf(false) }

    // Until the user takes over, the preview tours the palettes so the dialog
    // shows the whole range at a glance.
    LaunchedEffect(touched) {
        if (touched) return@LaunchedEffect
        while (true) {
            delay(2400L)
            val next = (preview.ordinal + 1) % AtharThemeId.entries.size
            preview = AtharThemeId.entries[next]
            accent = AtharAccentTone.NATIVE
        }
    }

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

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (isArabic) "جَدِيدٌ فِي أَثَر" else "NEW IN ATHAR",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            letterSpacing = 1.6.sp
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (isArabic) "أَثْوَابُ أَثَر" else "A New Look for Athar",
            fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = if (isArabic) 26.sp else 20.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = if (isArabic) 34.sp else 26.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (isArabic) {
                "أربعةُ أثوابٍ تُعيدُ رسمَ أثرَ كلّه، ولكلِّ ثوبٍ نغماتُ لونٍ تُكملُ طابعه. جرّبها هنا قبل أن تحفظها."
            } else {
                "Four palettes repaint all of Athar, each with accent tones that complete its mood. Try them here before you keep one."
            },
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth(0.94f)
        )

        Spacer(Modifier.height(18.dp))

        ThemePreview(
            selected = preview,
            accent = accent,
            isArabic = isArabic,
            onPickTheme = {
                touched = true
                preview = it
                accent = AtharAccentTone.NATIVE
            },
            onPickAccent = {
                touched = true
                accent = it
            }
        )

        Spacer(Modifier.height(18.dp))

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
    accent: AtharAccentTone,
    isArabic: Boolean,
    onPickTheme: (AtharThemeId) -> Unit,
    onPickAccent: (AtharAccentTone) -> Unit
) {
    val palette = selected.paletteFor(accent)
    val spec = tween<Color>(durationMillis = 520, easing = FastOutSlowInEasing)
    val background by animateColorAsState(palette.background, spec, label = "previewBackground")
    val surface by animateColorAsState(palette.cardSurface, spec, label = "previewSurface")
    val border by animateColorAsState(palette.cardBorder, spec, label = "previewBorder")
    val primary by animateColorAsState(palette.primary, spec, label = "previewPrimary")
    val primaryDeep by animateColorAsState(palette.primaryDeep, spec, label = "previewPrimaryDeep")
    val onPrimary by animateColorAsState(palette.textOnPrimary, spec, label = "previewOnPrimary")
    val textPrimary by animateColorAsState(palette.textPrimary, spec, label = "previewTextPrimary")
    val textSecondary by animateColorAsState(palette.textSecondary, spec, label = "previewTextSecondary")
    val ink by animateColorAsState(palette.ink, spec, label = "previewInk")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // The chosen theme and tone, named outside the mock so the artwork
        // itself stays clean and the labels line up with the swatches.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = selected.let { if (isArabic) it.nameAr else it.nameEn },
                    fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AtharTextPrimary
                )
                Text(
                    text = selected.moodFor(accent, isArabic),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = AtharTextSecondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                selected.accentTones.forEach { tone ->
                    val chosen = tone == accent
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (chosen) 1.5.dp else 1.dp,
                                color = if (chosen) ink else Color.White.copy(alpha = 0.16f),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onPickAccent(tone) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (chosen) 15.dp else 13.dp)
                                .clip(CircleShape)
                                .background(tone.swatch)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        // The mock screen: a gradient wall, a hero ayah card, an action row and
        // the floating dock, all painted with the chosen palette.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(background, primaryDeep.copy(alpha = 0.35f), background)
                    )
                )
                .border(1.dp, border, RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(onPrimary)
                        )
                    }
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = if (isArabic) "أَثَر" else "ATHAR",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = textPrimary
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(width = 30.dp, height = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primary.copy(alpha = 0.16f))
                            .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    )
                }

                Spacer(Modifier.height(9.dp))

                // Hero card: today's ayah set in the theme's own serif.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(primaryDeep.copy(alpha = 0.85f), surface)
                            )
                        )
                        .border(1.dp, primary.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 11.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(width = 2.5.dp, height = 34.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(primary)
                        )
                        Spacer(Modifier.width(9.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "آيَةُ اليَوْم" else "TODAY'S AYAH",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.sp,
                                color = ink,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = if (isArabic) {
                                    "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ"
                                } else {
                                    "In remembrance, hearts find rest"
                                },
                                fontFamily = if (isArabic) ThmanyahSerifDisplay else ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = if (isArabic) 11.sp else 9.sp,
                                color = textPrimary,
                                maxLines = 2
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // A filled action tile.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(primary)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(width = 34.dp, height = 5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(onPrimary.copy(alpha = 0.85f))
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 22.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(onPrimary.copy(alpha = 0.55f))
                            )
                        }
                    }
                    // A quiet card beside it.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(surface)
                            .border(1.dp, border, RoundedCornerShape(11.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(primary.copy(alpha = 0.22f))
                                    .border(1.dp, primary.copy(alpha = 0.55f), CircleShape)
                            )
                            Spacer(Modifier.width(7.dp))
                            Column {
                                Box(
                                    modifier = Modifier
                                        .size(width = 28.dp, height = 5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(textPrimary.copy(alpha = 0.72f))
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(width = 18.dp, height = 4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(textSecondary.copy(alpha = 0.62f))
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Floating dock.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(surface.copy(alpha = 0.92f))
                        .border(1.dp, border, RoundedCornerShape(13.dp))
                        .padding(horizontal = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(4) { index ->
                        val active = index == 0
                        Box(
                            modifier = Modifier
                                .size(width = if (active) 22.dp else 7.dp, height = 7.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (active) primary else textSecondary.copy(alpha = 0.55f)
                                )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Theme chips: named, aligned, and easy to hit.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AtharThemeId.entries.forEach { theme ->
                val chosen = theme == selected
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (chosen) theme.palette.primarySubtle else Color.White.copy(alpha = 0.03f)
                        )
                        .border(
                            width = if (chosen) 1.4.dp else 1.dp,
                            color = if (chosen) theme.palette.primary else Color.White.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(11.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onPickTheme(theme) }
                        )
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(theme.palette.primary)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = theme.let { if (isArabic) it.nameAr else it.nameEn },
                        fontFamily = ThmanyahSans,
                        fontWeight = if (chosen) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 9.sp,
                        color = if (chosen) AtharTextPrimary else AtharTextSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (isArabic) "اضغط على ثوبٍ أو نغمةٍ لمعاينتها" else "Tap a palette or a tone to preview it",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = AtharTextSecondary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
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

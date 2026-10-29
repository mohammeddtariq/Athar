package com.athar.app.ui.share

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.components.GirihCache
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.components.drawGirihWall
import com.athar.app.ui.components.drawHurufWeave
import com.athar.app.ui.components.quranAwareAnnotated
import com.athar.app.ui.components.rememberHurufTypeface
import com.athar.app.ui.components.rememberQuranPassages
import com.athar.app.ui.theme.AtharAccentTone
import com.athar.app.ui.theme.AtharBackgroundStyle
import com.athar.app.ui.theme.AtharPalette
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharSurface
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharThemeId
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import com.athar.app.ui.theme.ink
import kotlinx.coroutines.launch
import android.widget.Toast
import com.athar.app.R

/**
 * What a shared card is made of.
 *
 * A passage, its translation and source, plus the wording of the footer. The
 * backdrop and the palette are chosen in the sheet, not here.
 */
data class ShareCardSpec(
    val arabic: String,
    val translation: String?,
    val note: String?,
    val source: String,
    val footerLabel: String,
    val subject: String,
    val chooserTitle: String
)

/**
 * Mirrors the card the user is about to share and lets them pick its look.
 *
 * The preview is the real card — the image is captured straight off the same
 * composable that is on screen, so what is shown is exactly what is sent: the
 * chosen backdrop drawn in the chosen palette, with any Quranic verse already
 * set in the mushaf face.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ShareCardSheet(
    spec: ShareCardSpec,
    isArabic: Boolean,
    initialTheme: AtharThemeId,
    initialTone: AtharAccentTone,
    initialStyle: AtharBackgroundStyle,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val layer = rememberGraphicsLayer()

    var theme by remember { mutableStateOf(initialTheme) }
    var tone by remember { mutableStateOf(initialTone) }
    var style by remember { mutableStateOf(initialStyle) }
    val palette = remember(theme, tone) { theme.paletteFor(tone) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtharSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .height(4.dp)
                    .width(38.dp)
                    .clip(CircleShape)
                    .background(AtharTextSecondary.copy(alpha = 0.35f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp)
        ) {
            Text(
                text = if (isArabic) "مشاركة الصورة" else "Share as an image",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isArabic) "اختر الخلفية واللون قبل المشاركة."
                else "Pick the backdrop and the colour before sending.",
                fontFamily = ThmanyahSans,
                fontSize = 12.sp,
                color = AtharTextSecondary
            )

            Spacer(Modifier.height(14.dp))

            // The live card: captured exactly as it appears here.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, palette.cardBorder, RoundedCornerShape(18.dp))
                    .drawWithContent {
                        layer.record { this@drawWithContent.drawContent() }
                        drawLayer(layer)
                    }
            ) {
                ShareCard(
                    spec = spec,
                    isArabic = isArabic,
                    palette = palette,
                    style = style
                )
            }

            Spacer(Modifier.height(18.dp))

            SectionLabel(if (isArabic) "الخلفية" else "Backdrop")
            Spacer(Modifier.height(8.dp))
            BackdropRow(
                selected = style,
                palette = palette,
                isArabic = isArabic,
                onPick = {
                    if (it != style) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        style = it
                    }
                }
            )

            Spacer(Modifier.height(18.dp))

            SectionLabel(if (isArabic) "اختر أثرك" else "Choose your Athar")
            Spacer(Modifier.height(8.dp))
            ThemeRow(
                selected = theme,
                isArabic = isArabic,
                onPick = { picked ->
                    if (picked != theme) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        theme = picked
                        if (picked.accentTones.none { it == tone }) tone = AtharAccentTone.GOLD
                    }
                }
            )

            if (theme.accentTones.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                AccentRow(
                    tones = theme.accentTones,
                    selected = tone,
                    isArabic = isArabic,
                    onPick = { picked ->
                        if (picked != tone) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            tone = picked
                        }
                    }
                )
            }

            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(palette.accentInkOrPrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            scope.launch {
                                val shared = runCatching {
                                    val bitmap = layer.toImageBitmap().asAndroidBitmap()
                                    ShareCardRenderer.shareBitmap(
                                        context = context,
                                        bitmap = bitmap,
                                        subject = spec.subject,
                                        chooserTitle = spec.chooserTitle
                                    )
                                }.getOrDefault(false)
                                if (!shared) {
                                    Toast.makeText(context, R.string.share_failed, Toast.LENGTH_SHORT).show()
                                } else {
                                    onDismiss()
                                }
                            }
                        }
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = null,
                    tint = palette.textOnPrimary,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = if (isArabic) "مشاركة" else "Share",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = palette.textOnPrimary
                )
            }
        }
    }
}

/** The accent as a fill, falling back to the palette's own primary. */
private val AtharPalette.accentInkOrPrimary: Color
    get() = if (accentInk == Color.Unspecified) primary else primary

/**
 * The exported card itself.
 *
 * Laid out once, drawn once, then captured — so the sheet's preview, the shared
 * PNG and the on-screen design can never drift apart.
 */
@Composable
private fun ShareCard(
    spec: ShareCardSpec,
    isArabic: Boolean,
    palette: AtharPalette,
    style: AtharBackgroundStyle
) {
    val passages = rememberQuranPassages(spec.arabic)
    val body = remember(spec.arabic, passages) {
        quranAwareAnnotated(spec.arabic, passages, quranSize = 22.sp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.background)
    ) {
        IslamicPatternBackground(
            modifier = Modifier.matchParentSize(),
            tint = palette.ink,
            alpha = 0.17f,
            styleOverride = style
        )

        // A drawn frame keeps the card feeling printed rather than clipped.
        Canvas(modifier = Modifier.matchParentSize()) {
            val inset = 12.dp.toPx()
            drawRoundRect(
                color = palette.ink.copy(alpha = 0.30f),
                topLeft = Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(
                    size.width - inset * 2f,
                    size.height - inset * 2f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "أَثَر",
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Black,
                fontSize = 34.sp,
                color = palette.primaryLight
            )
            Text(
                text = "A T H A R",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp,
                letterSpacing = 4.5.sp,
                color = palette.textSecondary
            )

            Spacer(Modifier.height(16.dp))
            Rule(tint = palette.ink, alpha = 0.35f)
            Spacer(Modifier.height(16.dp))

            Text(
                text = body,
                fontFamily = ThmanyahSerifText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                lineHeight = 36.sp,
                color = palette.textPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Rule(tint = palette.ink, alpha = 0.28f)
            Spacer(Modifier.height(14.dp))

            spec.translation?.takeIf { it.isNotBlank() }?.let { translation ->
                Text(
                    text = translation,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
            }

            spec.note?.takeIf { it.isNotBlank() }?.let { note ->
                Text(
                    text = note,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = palette.primaryLight.copy(alpha = 0.85f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = spec.source.ifBlank { if (isArabic) "أثر" else "Athar" },
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = palette.primaryLight.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (isArabic) "أَثَر · ${spec.footerLabel}" else "Athar · ${spec.footerLabel}",
                fontFamily = ThmanyahSans,
                fontSize = 9.5.sp,
                color = palette.textSecondary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/** A hairline with a small diamond at its centre. */
@Composable
private fun Rule(tint: Color, alpha: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        val midY = size.height / 2f
        val gap = 12.dp.toPx()
        drawLine(
            color = tint.copy(alpha = alpha),
            start = Offset(0f, midY),
            end = Offset(size.width / 2f - gap, midY),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = tint.copy(alpha = alpha),
            start = Offset(size.width / 2f + gap, midY),
            end = Offset(size.width, midY),
            strokeWidth = 1.dp.toPx()
        )
        val d = 3.5.dp.toPx()
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width / 2f, midY - d)
                lineTo(size.width / 2f + d, midY)
                lineTo(size.width / 2f, midY + d)
                lineTo(size.width / 2f - d, midY)
                close()
            },
            color = tint.copy(alpha = alpha * 1.6f)
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Black,
        fontSize = 12.5.sp,
        color = AtharTextPrimary
    )
}

/** Three tiles previewing each backdrop in the palette that is being shared. */
@Composable
private fun BackdropRow(
    selected: AtharBackgroundStyle,
    palette: AtharPalette,
    isArabic: Boolean,
    onPick: (AtharBackgroundStyle) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val typeface = rememberHurufTypeface()
    val girihCache = remember { GirihCache() }
    val density = LocalDensity.current
    val cellPx = with(density) { 20.dp.toPx() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AtharBackgroundStyle.entries.forEach { style ->
            val isSelected = style == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (!isSelected) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPick(style)
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(palette.background)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) palette.ink else palette.cardBorder,
                            shape = RoundedCornerShape(13.dp)
                        )
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawBackdropPreview(style, palette.ink, cellPx, typeface, girihCache)
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(15.dp)
                                .clip(CircleShape)
                                .background(palette.ink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = palette.textOnPrimary,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = if (isArabic) style.nameAr else style.nameEn,
                    fontFamily = ThmanyahSans,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    fontSize = 9.5.sp,
                    color = if (isSelected) palette.ink else AtharTextSecondary,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun DrawScope.drawBackdropPreview(
    style: AtharBackgroundStyle,
    tint: Color,
    cellPx: Float,
    typeface: android.graphics.Typeface?,
    girihCache: GirihCache
) {
    when (style) {
        AtharBackgroundStyle.ISLAMIC -> drawGirihWall(
            tint = tint,
            baseAlpha = 0.30f,
            cellPx = cellPx,
            cache = girihCache
        )

        AtharBackgroundStyle.LETTERS -> drawHurufWeave(
            tint = tint,
            baseAlpha = 0.28f,
            phase = 0f,
            typeface = typeface,
            cellPx = cellPx * 1.8f
        )

        AtharBackgroundStyle.SOLID -> drawRect(
            brush = Brush.radialGradient(
                colors = listOf(tint.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width * 0.22f, size.height * 0.18f),
                radius = size.maxDimension * 0.9f
            )
        )
    }
}

/** One tile per theme, each painted in its own colours. */
@Composable
private fun ThemeRow(
    selected: AtharThemeId,
    isArabic: Boolean,
    onPick: (AtharThemeId) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AtharThemeId.entries.forEach { theme ->
            val isSelected = theme == selected
            val preview = theme.paletteFor(AtharAccentTone.GOLD)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (!isSelected) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPick(theme)
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(preview.background)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) preview.ink else preview.cardBorder,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 18.dp else 14.dp)
                            .clip(CircleShape)
                            .background(preview.ink)
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = if (isArabic) theme.nameAr else theme.nameEn,
                    fontFamily = ThmanyahSans,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    fontSize = 9.5.sp,
                    color = if (isSelected) AtharPrimaryLight else AtharTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AccentRow(
    tones: List<AtharAccentTone>,
    selected: AtharAccentTone,
    isArabic: Boolean,
    onPick: (AtharAccentTone) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tones.forEach { tone ->
            val isSelected = tone == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) tone.swatch.copy(alpha = 0.14f) else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) tone.swatch.copy(alpha = 0.85f) else AtharSurface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (!isSelected) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPick(tone)
                            }
                        }
                    )
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(tone.swatch)
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = if (isArabic) tone.labelAr else tone.labelEn,
                    fontFamily = ThmanyahSans,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isSelected) AtharTextPrimary else AtharTextSecondary
                )
            }
        }
    }
}


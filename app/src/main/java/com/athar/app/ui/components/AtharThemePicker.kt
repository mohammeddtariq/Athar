package com.athar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPalette
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharThemeId
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay

/**
 * The theme chooser: one card per theme, each previewing the theme in
 * miniature. Picking a card bounces it, sends a burst of light out of its
 * swatch and names the colour in both languages.
 */
@Composable
fun AtharThemePicker(
    selected: AtharThemeId,
    isArabic: Boolean,
    onPick: (AtharThemeId) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AtharThemeId.entries.forEach { theme ->
            ThemeChoiceCard(
                theme = theme,
                isSelected = theme == selected,
                isArabic = isArabic,
                onClick = { onPick(theme) }
            )
        }
    }
}

@Composable
private fun ThemeChoiceCard(
    theme: AtharThemeId,
    isSelected: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    val palette = theme.palette
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(18.dp)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.985f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "themeCardScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) palette.primary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeCardBorder"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) palette.primary.copy(alpha = 0.10f) else AtharCardSurface,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeCardContainer"
    )

    // One light burst, replayed every time this card becomes the active theme.
    val burst = remember { Animatable(1f) }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            burst.snapTo(0f)
            burst.animateTo(1f, tween(durationMillis = 980, easing = LinearEasing))
        } else {
            burst.snapTo(1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(containerColor)
            .border(if (isSelected) 1.4.dp else 1.dp, borderColor, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (!isSelected) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                }
            )
    ) {
        if (isSelected) {
            Canvas(modifier = Modifier.matchParentSize()) {
                // Read in the draw phase so the burst never recomposes the card.
                val progress = burst.value
                if (progress < 1f) {
                    val radius = size.width * (0.16f + 1.05f * progress)
                    drawCircle(
                        color = palette.primary.copy(alpha = 0.16f * (1f - progress)),
                        radius = radius,
                        center = Offset(size.width * 0.16f, size.height / 2f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeSwatch(
                palette = palette,
                isSelected = isSelected
            )
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) theme.nameAr else theme.nameEn,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.5.sp,
                    color = if (isSelected) palette.primaryLight else AtharTextPrimary
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = if (isArabic) theme.nameEn else theme.nameAr,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = if (isSelected) palette.primaryMuted else AtharTextSecondary
                )
                Spacer(Modifier.height(5.dp))
                SwatchDots(palette = palette)
            }
            Spacer(Modifier.size(10.dp))
            SelectionMark(
                isSelected = isSelected,
                accent = palette.primary,
                ink = palette.textOnPrimary
            )
        }
    }
}

/** Miniature of the app drawn in that theme's own colours. */
@Composable
private fun ThemeSwatch(
    palette: AtharPalette,
    isSelected: Boolean
) {
    val shape = RoundedCornerShape(12.dp)
    val transition = rememberInfiniteTransition(label = "themeSwatch")
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2600, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "themeSwatchShimmer"
    )

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(shape)
            .background(palette.background)
            .border(
                width = if (isSelected) 1.4.dp else 1.dp,
                color = if (isSelected) palette.primary.copy(alpha = 0.75f) else palette.cardBorder,
                shape = shape
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Header line.
            drawRoundRect(
                color = palette.primary.copy(alpha = 0.9f),
                topLeft = Offset(w * 0.18f, h * 0.16f),
                size = Size(w * 0.40f, h * 0.075f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            // Card block.
            drawRoundRect(
                color = palette.cardSurface,
                topLeft = Offset(w * 0.13f, h * 0.33f),
                size = Size(w * 0.74f, h * 0.24f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = palette.primary.copy(alpha = 0.5f),
                topLeft = Offset(w * 0.20f, h * 0.40f),
                size = Size(w * 0.34f, h * 0.06f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Floating dock.
            drawRoundRect(
                color = palette.navbarBg,
                topLeft = Offset(w * 0.15f, h * 0.66f),
                size = Size(w * 0.70f, h * 0.20f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawCircle(
                color = palette.navPillSelected,
                radius = w * 0.075f,
                center = Offset(w * 0.35f, h * 0.76f)
            )
            drawCircle(
                color = palette.primary.copy(alpha = 0.55f),
                radius = w * 0.05f,
                center = Offset(w * 0.62f, h * 0.76f)
            )

            // A slow sheen so an idle card still looks alive.
            if (isSelected) {
                val head = w * (shimmer * 1.6f - 0.3f)
                drawRect(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        startX = head - w * 0.35f,
                        endX = head + w * 0.35f
                    )
                )
            }
        }
    }
}

/** Three chips sampling the theme's background, accent and surface. */
@Composable
private fun SwatchDots(palette: AtharPalette) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        listOf(
            palette.background,
            palette.primary,
            palette.surface
        ).forEach { color ->
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(0.8.dp, palette.cardBorder, CircleShape)
            )
        }
    }
}

@Composable
private fun SelectionMark(
    isSelected: Boolean,
    accent: Color,
    ink: Color
) {
    val checkScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "themeCheckScale"
    )
    val ringColor by animateColorAsState(
        targetValue = if (isSelected) accent else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "themeCheckRing"
    )

    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(if (isSelected) accent else Color.Transparent)
            .border(1.dp, ringColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (checkScale > 0.01f) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = ink,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = checkScale
                        scaleY = checkScale
                        alpha = checkScale.coerceIn(0f, 1f)
                    }
            )
        }
    }
}

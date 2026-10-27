package com.athar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharBackgroundStyle
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharSurface
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.ThmanyahSans

/**
 * Side-by-side chooser for the app's backdrop, each card drawing the real
 * pattern at preview scale so the two options are never abstract.
 */
@Composable
fun AtharBackgroundPicker(
    selected: AtharBackgroundStyle,
    isArabic: Boolean,
    onPick: (AtharBackgroundStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AtharBackgroundStyle.entries.forEach { style ->
            BackgroundChoiceCard(
                style = style,
                isSelected = style == selected,
                isArabic = isArabic,
                onClick = { onPick(style) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BackgroundChoiceCard(
    style: AtharBackgroundStyle,
    isSelected: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(16.dp)
    val density = LocalDensity.current

    val previewCellPx = with(density) {
        when (style) {
            AtharBackgroundStyle.ISLAMIC -> 22.dp.toPx()
            AtharBackgroundStyle.LETTERS -> 30.dp.toPx()
        }
    }
    val glyphs = remember(previewCellPx) { buildArabicGlyphs(previewCellPx) }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.98f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "backgroundCardScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "backgroundCardBorder"
    )

    Box(
        modifier = modifier
            .height(114.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(AtharSurface)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, shape)
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
        Canvas(modifier = Modifier.matchParentSize()) {
            when (style) {
                AtharBackgroundStyle.ISLAMIC -> drawArabesqueLattice(
                    tint = AtharPrimaryLight,
                    baseAlpha = 0.32f,
                    cellPx = previewCellPx,
                    phase = 0f
                )

                AtharBackgroundStyle.LETTERS -> drawArabicLetterLattice(
                    tint = AtharPrimaryLight,
                    baseAlpha = 0.36f,
                    cellPx = previewCellPx,
                    phase = 0f,
                    glyphs = glyphs
                )
            }
        }

        // Keeps the label legible over a dense pattern.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(46.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, AtharSurface.copy(alpha = 0.94f))
                    )
                )
        )

        Text(
            text = if (isArabic) style.nameAr else style.nameEn,
            fontFamily = ThmanyahSans,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            fontSize = 11.5.sp,
            color = if (isSelected) AtharTextPrimary else AtharTextPrimary.copy(alpha = 0.72f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 9.dp, end = 10.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(AtharPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = AtharTextOnPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

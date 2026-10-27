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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharBackgroundStyle
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharSurface
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.ThmanyahSans

/**
 * Chooser for the app's backdrop: the two hand-drawn patterns side by side,
 * with a third, quieter card underneath for a completely solid surface.
 */
@Composable
fun AtharBackgroundPicker(
    selected: AtharBackgroundStyle,
    isArabic: Boolean,
    onPick: (AtharBackgroundStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeface = rememberHurufTypeface()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(AtharBackgroundStyle.ISLAMIC, AtharBackgroundStyle.LETTERS).forEach { style ->
                BackgroundChoiceCard(
                    style = style,
                    isSelected = style == selected,
                    isArabic = isArabic,
                    typeface = typeface,
                    onClick = { onPick(style) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        SolidChoiceCard(
            isSelected = selected == AtharBackgroundStyle.SOLID,
            isArabic = isArabic,
            onClick = { onPick(AtharBackgroundStyle.SOLID) }
        )
    }
}

@Composable
private fun BackgroundChoiceCard(
    style: AtharBackgroundStyle,
    isSelected: Boolean,
    isArabic: Boolean,
    typeface: android.graphics.Typeface?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(16.dp)
    val density = LocalDensity.current

    val previewCellPx = with(density) {
        when (style) {
            AtharBackgroundStyle.ISLAMIC -> 22.dp.toPx()
            AtharBackgroundStyle.LETTERS -> 38.dp.toPx()
            AtharBackgroundStyle.SOLID -> 22.dp.toPx()
        }
    }

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
            .height(118.dp)
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

                AtharBackgroundStyle.LETTERS -> drawHurufWeave(
                    tint = AtharPrimaryLight,
                    baseAlpha = 0.28f,
                    phase = 0f,
                    typeface = typeface,
                    cellPx = previewCellPx
                )

                AtharBackgroundStyle.SOLID -> drawSolidPreview()
            }
        }

        // Keeps the label legible over a dense pattern.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
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
            lineHeight = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
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

/**
 * A full-width, quiet card for the solid option. Its preview is the theme's
 * own background rather than a pattern, so the difference is immediately clear.
 */
@Composable
private fun SolidChoiceCard(
    isSelected: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(16.dp)
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "solidCardBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(AtharBackground)
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSolidPreview()
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(AtharBackground)
                    .border(1.dp, AtharCardBorder, RoundedCornerShape(9.dp))
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = if (isArabic) AtharBackgroundStyle.SOLID.nameAr
                    else AtharBackgroundStyle.SOLID.nameEn,
                    fontFamily = ThmanyahSans,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AtharTextPrimary
                )
                Text(
                    text = if (isArabic) "بلا نقش، لون واحد من حافة إلى حافة."
                    else "No pattern. One colour, edge to edge.",
                    fontFamily = ThmanyahSans,
                    fontSize = 10.5.sp,
                    color = AtharTextPrimary.copy(alpha = 0.55f)
                )
            }
            Box(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AtharPrimary else Color.Transparent)
                    .border(1.dp, if (isSelected) AtharPrimary else AtharCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = AtharTextOnPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/** A soft accent wash over the theme's own background. */
private fun DrawScope.drawSolidPreview() {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(AtharPrimary.copy(alpha = 0.14f), Color.Transparent),
            center = Offset(size.width * 0.18f, size.height * 0.10f),
            radius = size.maxDimension * 0.85f
        ),
        radius = size.maxDimension * 0.85f,
        center = Offset(size.width * 0.18f, size.height * 0.10f)
    )
}

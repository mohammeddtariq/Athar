package com.athar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharAccentInk
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharIconStyle
import com.athar.app.ui.theme.ThmanyahSans

/**
 * Launcher icon colour picker. Every swatch shows the real recoloured stock
 * artwork on its matching plate, so the choice on the home screen is never a
 * surprise.
 */
@Composable
fun AtharIconPicker(
    selected: AtharIconStyle,
    isArabic: Boolean,
    onPick: (AtharIconStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AtharIconStyle.entries.forEach { style ->
            IconSwatch(
                style = style,
                isSelected = style == selected,
                label = if (isArabic) style.nameAr else style.nameEn,
                onClick = { onPick(style) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun IconSwatch(
    style: AtharIconStyle,
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconSwatchScale"
    )
    val ringColor by animateColorAsState(
        targetValue = if (isSelected) AtharAccentInk else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "iconSwatchRing"
    )

    Column(
        modifier = modifier
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
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(RoundedCornerShape(15.dp))
                .background(colorResource(style.plateRes))
                .border(
                    width = if (isSelected) 1.6.dp else 1.dp,
                    color = ringColor,
                    shape = RoundedCornerShape(15.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // The stock artwork is padded for the launcher's safe zone, which
            // leaves the mark floating small inside a swatch. Enlarging it here
            // fills the tile the way the launcher mask does on the home screen.
            Image(
                painter = painterResource(style.foregroundRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.46f
                        scaleY = 1.46f
                    }
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(AtharAccentInk),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = AtharTextOnPrimary,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontFamily = ThmanyahSans,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            fontSize = 10.sp,
            color = if (isSelected) AtharPrimaryLight else AtharTextSecondary,
            maxLines = 1
        )
    }
}

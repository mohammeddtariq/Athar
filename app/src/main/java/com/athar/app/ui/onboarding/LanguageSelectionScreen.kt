package com.athar.app.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharOutline
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryDark
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay

/**
 * First-launch language selection screen.
 * Minimal, elegant, dark green-grey backdrop with two language cards.
 */
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AtharBackground,
                        AtharCardSurface.copy(alpha = 0.5f),
                        AtharBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bismillah
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = AtharPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // App name
            Text(
                text = "أَثَر",
                fontFamily = ThmanyahSerifDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = 56.sp,
                color = AtharPrimaryLight,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Athar",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // "Choose Your Language" — shown in both languages
            Text(
                text = "اختر لغتك",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = AtharTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose Your Language",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Language Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LanguageCard(
                    languageName = "العربية",
                    languageSubtitle = "Arabic",
                    isSelected = selectedLanguage == "ar",
                    onClick = { selectedLanguage = "ar" },
                    modifier = Modifier.weight(1f)
                )
                LanguageCard(
                    languageName = "English",
                    languageSubtitle = "الإنجليزية",
                    isSelected = selectedLanguage == "en",
                    onClick = { selectedLanguage = "en" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "يمكنك تغييرها لاحقاً  •  You can change this later",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = AtharTextSecondary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Continue Button
            val buttonEnabled = selectedLanguage != null
            val buttonBg by animateColorAsState(
                targetValue = if (buttonEnabled) AtharPrimary else AtharOutline,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "buttonBg"
            )
            val buttonTextColor by animateColorAsState(
                targetValue = if (buttonEnabled) AtharTextOnPrimary else AtharTextSecondary,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "buttonText"
            )

            Button(
                onClick = { selectedLanguage?.let { onLanguageSelected(it) } },
                enabled = buttonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBg,
                    contentColor = buttonTextColor,
                    disabledContainerColor = AtharOutline,
                    disabledContentColor = AtharTextSecondary.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = if (selectedLanguage == "ar") "متابعة" else "Continue",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun LanguageCard(
    languageName: String,
    languageSubtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AtharPrimary else AtharOutline,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "borderColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) AtharPrimary.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bgColor"
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "elevation"
    )

    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Check indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(AtharPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = AtharTextOnPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }

            Text(
                text = languageName,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isSelected) AtharPrimaryLight else AtharTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = languageSubtitle,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

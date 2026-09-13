package com.athar.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.athar.app.R

/**
 * THMANYAH FONT INTEGRATION
 * 
 * 1. Place your font files in res/font
 * 2. Rename them to thmanyah_regular.ttf and thmanyah_bold.ttf
 * 3. Uncomment the lines below to activate
 */

// Default to system font if files are missing to prevent build errors
val ThmanyahText = FontFamily.Default
val ThmanyahDisplay = FontFamily.Default

/* 
// UNCOMMENT THIS SECTION AFTER ADDING THE FILES:
val ThmanyahText = FontFamily(
    Font(R.font.thmanyah_regular, FontWeight.Normal),
    Font(R.font.thmanyah_bold, FontWeight.Bold)
)

// Optional: Use 'thmanyahserifdisplay' for titles
val ThmanyahDisplay = FontFamily(
    Font(R.font.thmanyah_display_bold, FontWeight.Bold)
)
*/

val Typography = Typography(
    // Large titles (e.g., Prayer Time on Home)
    displayLarge = TextStyle(
        fontFamily = ThmanyahDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ThmanyahText,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Standard body text
    bodyLarge = TextStyle(
        fontFamily = ThmanyahText,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Small labels and buttons
    labelSmall = TextStyle(
        fontFamily = ThmanyahText,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

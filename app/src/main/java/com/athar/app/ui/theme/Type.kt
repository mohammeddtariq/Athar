package com.athar.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.athar.app.R

/**
 * THMANYAH FONT FAMILY
 *
 * Three font families from the Thmanyah typeface:
 * - ThmanyahSans: Primary UI font for body text, labels, and buttons
 * - ThmanyahSerifDisplay: For large display titles and headings
 * - ThmanyahSerifText: For readable body serif text (Quran references, etc.)
 */

val ThmanyahSans = FontFamily(
    Font(R.font.thmanyah_sans_light, FontWeight.Light),
    Font(R.font.thmanyah_sans_regular, FontWeight.Normal),
    Font(R.font.thmanyah_sans_medium, FontWeight.Medium),
    Font(R.font.thmanyah_sans_bold, FontWeight.Bold),
    Font(R.font.thmanyah_sans_black, FontWeight.Black)
)

val ThmanyahSerifDisplay = FontFamily(
    Font(R.font.thmanyah_serif_display_light, FontWeight.Light),
    Font(R.font.thmanyah_serif_display_regular, FontWeight.Normal),
    Font(R.font.thmanyah_serif_display_medium, FontWeight.Medium),
    Font(R.font.thmanyah_serif_display_bold, FontWeight.Bold),
    Font(R.font.thmanyah_serif_display_black, FontWeight.Black)
)

val ThmanyahSerifText = FontFamily(
    Font(R.font.thmanyah_serif_text_light, FontWeight.Light),
    Font(R.font.thmanyah_serif_text_regular, FontWeight.Normal),
    Font(R.font.thmanyah_serif_text_medium, FontWeight.Medium),
    Font(R.font.thmanyah_serif_text_bold, FontWeight.Bold),
    Font(R.font.thmanyah_serif_text_black, FontWeight.Black)
)

val Typography = Typography(
    // ─── Display styles (Serif Display) ───
    displayLarge = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),

    // ─── Headline styles (Serif Display) ───
    headlineLarge = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // ─── Title styles (Sans) ───
    titleLarge = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ─── Body styles (Sans) ───
    bodyLarge = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),

    // ─── Label styles (Sans) ───
    labelLarge = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)

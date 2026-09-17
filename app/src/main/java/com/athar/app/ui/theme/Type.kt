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
 * Refined, slightly more compact typography sizing:
 * - ThmanyahSans: Primary UI font — used BOLD/BLACK for wide, authoritative feel
 * - ThmanyahSerifDisplay: Large display titles and headings
 * - ThmanyahSerifText: Readable serif body (Quran, etc.)
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

val QuranUthmanicHafs = FontFamily(
    Font(R.font.quran_uthmanic_hafs, FontWeight.Normal)
)

val QuranSurahNames = FontFamily(
    Font(R.font.quran_surah_names, FontWeight.Normal)
)

val Typography = Typography(
    // ─── Display styles — Refined Sizes ───
    displayLarge = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Black,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),

    // ─── Headline styles ───
    headlineLarge = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ThmanyahSerifDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Black,
        fontSize = 19.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.sp
    ),

    // ─── Title styles ───
    titleLarge = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Black,
        fontSize = 19.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),

    // ─── Body styles ───
    bodyLarge = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.3.sp
    ),

    // ─── Label styles ───
    labelLarge = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ThmanyahSans,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    )
)

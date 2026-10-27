package com.athar.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════
// ATHAR — Themes
//
// The app is dark-first. Every colour it draws lives in one
// [AtharPalette]; a theme is one instance of that record. The
// rest of the app keeps referring to plain names like
// `AtharPrimary`, which now read from the active palette, so
// switching a theme repaints the whole app without touching a
// single call site.
// ══════════════════════════════════════════════════════════════

/**
 * The complete colour set for one theme.
 *
 * Accents are split the same way everywhere: [primary] is the saturated
 * accent used for fills and glowing details, [primaryLight] is the near-white
 * variant used for text and hairlines on dark surfaces, and [textOnPrimary]
 * is the dark ink that sits on top of [primary] fills.
 */
data class AtharPalette(
    // ─── Backgrounds ───
    val background: Color,
    val surface: Color,
    val cardSurface: Color,
    val cardBorder: Color,
    val cardGlow: Color,

    // ─── Accent ───
    val primary: Color,
    val primaryMuted: Color,
    val primaryLight: Color,
    val primarySubtle: Color,
    val primaryDeep: Color,

    // ─── Text ───
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textOnPrimary: Color,

    // ─── Floating dock ───
    val navbarBg: Color,
    val navbarBorder: Color,
    val navPillSelected: Color,
    val navPillBorder: Color,
    val navIconActive: Color,
    val navIconInactive: Color,
    val navGlow: Color,

    // ─── Service accents ───
    val emerald: Color,
    val amber: Color,
    val terracotta: Color,
    val teal: Color,
    val lavender: Color,

    // ─── Lines & gradients ───
    val outline: Color,
    val outlineVariant: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

/**
 * Optional secondary accent offered by some themes.
 *
 * Burgundy is the only theme that currently ships a choice: antique gold or
 * warm ivory. Every other theme resolves to [GOLD], which simply keeps its
 * own palette untouched.
 */
enum class AtharAccentTone(
    val id: String,
    val labelAr: String,
    val labelEn: String
) {
    GOLD("GOLD", "ذهبي", "Gold"),
    IVORY("IVORY", "عاجي", "Ivory");

    companion object {
        fun fromId(id: String?): AtharAccentTone =
            entries.firstOrNull { it.id == id } ?: GOLD
    }
}

/**
 * The selectable themes, in the order they are offered.
 *
 * [nameAr] / [nameEn] are the names shown on the chooser cards.
 */
enum class AtharThemeId(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val palette: AtharPalette
) {
    /** Deep olive-black with muted pistachio — the original Athar look. */
    OLIVE(
        id = "OLIVE",
        nameAr = "زيتوني",
        nameEn = "Olive",
        palette = AtharPalette(
            background = Color(0xFF0A0C08),
            surface = Color(0xFF10130D),
            cardSurface = Color(0xFF151A11),
            cardBorder = Color(0xFF1E281B),
            cardGlow = Color(0xFF1A2214),
            primary = Color(0xFFC9D8B4),
            primaryMuted = Color(0xFF7C9070),
            primaryLight = Color(0xFFE6EEDA),
            primarySubtle = Color(0xFF1A2115),
            primaryDeep = Color(0xFF2A3325),
            textPrimary = Color(0xFFEBEFE3),
            textSecondary = Color(0xFF93A086),
            textMuted = Color(0xFF4A5A43),
            textOnPrimary = Color(0xFF141A10),
            navbarBg = Color(0xFF2A3322),
            navbarBorder = Color(0xFF3E4C30),
            navPillSelected = Color(0xFF455531),
            navPillBorder = Color(0xFF647A48),
            navIconActive = Color(0xFFE6EEDA),
            navIconInactive = Color(0xFFA4AA9C),
            navGlow = Color(0xFFBCE484),
            emerald = Color(0xFF417858),
            amber = Color(0xFFA1854B),
            terracotta = Color(0xFF935A40),
            teal = Color(0xFF35756A),
            lavender = Color(0xFF6A5D82),
            outline = Color(0xFF1E281B),
            outlineVariant = Color(0xFF12170E),
            gradientStart = Color(0xFF1B2413),
            gradientEnd = Color(0xFF10130D)
        )
    ),

    /** Deep wine base warmed by antique gold. */
    BURGUNDY(
        id = "BURGUNDY",
        nameAr = "عُنّابي",
        nameEn = "Burgundy",
        palette = AtharPalette(
            background = Color(0xFF090406),
            surface = Color(0xFF14080C),
            cardSurface = Color(0xFF1D0B11),
            cardBorder = Color(0xFF3A1520),
            cardGlow = Color(0xFF2B0E17),
            primary = Color(0xFFD9A45C),
            primaryMuted = Color(0xFF906C36),
            primaryLight = Color(0xFFF2DCA6),
            primarySubtle = Color(0xFF2C0E16),
            primaryDeep = Color(0xFF5A1526),
            textPrimary = Color(0xFFF5EBE7),
            textSecondary = Color(0xFFB29696),
            textMuted = Color(0xFF6B4A50),
            textOnPrimary = Color(0xFF1B1008),
            navbarBg = Color(0xFF2B0912),
            navbarBorder = Color(0xFF521426),
            navPillSelected = Color(0xFF4E1020),
            navPillBorder = Color(0xFF7C2338),
            navIconActive = Color(0xFFF2DCA6),
            navIconInactive = Color(0xFFC0A0A5),
            navGlow = Color(0xFFE8B96B),
            emerald = Color(0xFF4A7F63),
            amber = Color(0xFFA1854B),
            terracotta = Color(0xFFA85F4A),
            teal = Color(0xFF3B7A72),
            lavender = Color(0xFF7A6588),
            outline = Color(0xFF3A1520),
            outlineVariant = Color(0xFF17090D),
            gradientStart = Color(0xFF2C0E16),
            gradientEnd = Color(0xFF14080C)
        )
    ),

    /** Roasted coffee brown, also warmed by gold. */
    BROWN(
        id = "BROWN",
        nameAr = "بُنّي",
        nameEn = "Brown",
        palette = AtharPalette(
            background = Color(0xFF0B0805),
            surface = Color(0xFF130F0A),
            cardSurface = Color(0xFF1B1510),
            cardBorder = Color(0xFF2E2419),
            cardGlow = Color(0xFF251C13),
            primary = Color(0xFFC9A25C),
            primaryMuted = Color(0xFF8C7038),
            primaryLight = Color(0xFFEBD6A4),
            primarySubtle = Color(0xFF221A11),
            primaryDeep = Color(0xFF3A2C1C),
            textPrimary = Color(0xFFF3EBE0),
            textSecondary = Color(0xFFA79680),
            textMuted = Color(0xFF5E5142),
            textOnPrimary = Color(0xFF1A1208),
            navbarBg = Color(0xFF2A2015),
            navbarBorder = Color(0xFF41311F),
            navPillSelected = Color(0xFF493824),
            navPillBorder = Color(0xFF6B5538),
            navIconActive = Color(0xFFEBD6A4),
            navIconInactive = Color(0xFFADA18E),
            navGlow = Color(0xFFE0BE7C),
            emerald = Color(0xFF4A7A58),
            amber = Color(0xFFA1854B),
            terracotta = Color(0xFF9A5C3C),
            teal = Color(0xFF3A7368),
            lavender = Color(0xFF736582),
            outline = Color(0xFF2E2419),
            outlineVariant = Color(0xFF16110B),
            gradientStart = Color(0xFF221A11),
            gradientEnd = Color(0xFF130F0A)
        )
    ),

    /** True black panels with a pure white accent, for OLED displays. */
    AMOLED(
        id = "AMOLED",
        nameAr = "أسود نقي",
        nameEn = "AMOLED",
        palette = AtharPalette(
            background = Color(0xFF000000),
            surface = Color(0xFF050505),
            cardSurface = Color(0xFF0A0A0A),
            cardBorder = Color(0xFF1C1C1C),
            cardGlow = Color(0xFF141414),
            primary = Color(0xFFFFFFFF),
            primaryMuted = Color(0xFF9A9A9A),
            primaryLight = Color(0xFFFFFFFF),
            primarySubtle = Color(0xFF141414),
            primaryDeep = Color(0xFF2A2A2A),
            textPrimary = Color(0xFFF7F7F7),
            textSecondary = Color(0xFF9E9E9E),
            textMuted = Color(0xFF4F4F4F),
            textOnPrimary = Color(0xFF0A0A0A),
            navbarBg = Color(0xFF101010),
            navbarBorder = Color(0xFF2A2A2A),
            navPillSelected = Color(0xFF2A2A2A),
            navPillBorder = Color(0xFF4A4A4A),
            navIconActive = Color(0xFFFFFFFF),
            navIconInactive = Color(0xFF8E8E8E),
            navGlow = Color(0xFFFFFFFF),
            emerald = Color(0xFF2E7D5B),
            amber = Color(0xFFA1854B),
            terracotta = Color(0xFF9C5A3E),
            teal = Color(0xFF2F7A70),
            lavender = Color(0xFF6E5F8A),
            outline = Color(0xFF1C1C1C),
            outlineVariant = Color(0xFF0F0F0F),
            gradientStart = Color(0xFF171717),
            gradientEnd = Color(0xFF050505)
        )
    );

    /** A short, localized mood line shown under the theme name. */
    val moodAr: String
        get() = when (this) {
            OLIVE -> "فستقي وحبر"
            BURGUNDY -> "ذهب عتيق وعُنّاب"
            BROWN -> "قهوة وذهب محمّص"
            AMOLED -> "سواد نقي وضوء"
        }

    val moodEn: String
        get() = when (this) {
            OLIVE -> "Pistachio & ink"
            BURGUNDY -> "Antique gold & garnet"
            BROWN -> "Roasted coffee & gold"
            AMOLED -> "True black & light"
        }

    /** The accent choices this theme exposes on its card. */
    val accentTones: List<AtharAccentTone>
        get() = if (this == BURGUNDY) AtharAccentTone.entries else emptyList()

    /**
     * Resolves the palette for a chosen accent. Only Burgundy has a second
     * variant; every other theme ignores the tone.
     */
    fun paletteFor(accent: AtharAccentTone): AtharPalette =
        if (this == BURGUNDY && accent == AtharAccentTone.IVORY) {
            palette.copy(
                primary = Color(0xFFF2E6D8),
                primaryMuted = Color(0xFFB69C8D),
                primaryLight = Color(0xFFFFFAF3),
                primarySubtle = Color(0xFF2B0D16),
                primaryDeep = Color(0xFF52101F),
                textOnPrimary = Color(0xFF2A0910),
                navIconActive = Color(0xFFFFF6EC),
                navGlow = Color(0xFFF3DFCD),
                navPillBorder = Color(0xFF8A4356)
            )
        } else {
            palette
        }

    /** Mood line for a specific accent choice. */
    fun moodFor(accent: AtharAccentTone, isArabic: Boolean): String =
        if (this == BURGUNDY && accent == AtharAccentTone.IVORY) {
            if (isArabic) "عاجي وعُنّاب" else "Ivory & garnet"
        } else {
            if (isArabic) moodAr else moodEn
        }

    companion object {
        /** Falls back to the original look for unknown or missing stored ids. */
        fun fromId(id: String?): AtharThemeId =
            entries.firstOrNull { it.id == id } ?: OLIVE

        val default: AtharThemeId = OLIVE
    }
}

/**
 * Holds the palette the whole app is currently drawn with. Backed by snapshot
 * state, so every `Athar*` colour below stays reactive on its own.
 */
object AtharThemeState {
    var current: AtharPalette by mutableStateOf(AtharThemeId.default.palette)
        internal set

    /** The theme the palette was resolved from. */
    var themeId: AtharThemeId by mutableStateOf(AtharThemeId.default)
        internal set

    /** The accent variant the palette was resolved with. */
    var accentTone: AtharAccentTone by mutableStateOf(AtharAccentTone.GOLD)
        internal set

    internal fun apply(theme: AtharThemeId, accent: AtharAccentTone) {
        themeId = theme
        accentTone = accent
        current = theme.paletteFor(accent)
    }
}

// ─── Core Backgrounds ───
val AtharBackground: Color get() = AtharThemeState.current.background
val AtharSurface: Color get() = AtharThemeState.current.surface
val AtharCardSurface: Color get() = AtharThemeState.current.cardSurface
val AtharCardBorder: Color get() = AtharThemeState.current.cardBorder
val AtharCardGlow: Color get() = AtharThemeState.current.cardGlow

// ─── Accent ───
val AtharPrimary: Color get() = AtharThemeState.current.primary
val AtharPrimaryMuted: Color get() = AtharThemeState.current.primaryMuted
val AtharPrimaryLight: Color get() = AtharThemeState.current.primaryLight
val AtharPrimarySubtle: Color get() = AtharThemeState.current.primarySubtle
val AtharPrimaryDeep: Color get() = AtharThemeState.current.primaryDeep

/** Backdrop ink: Burgundy always draws its wall in antique gold. */
val AtharBackdropTint: Color
    get() = if (AtharThemeState.themeId == AtharThemeId.BURGUNDY) {
        AtharThemeId.BURGUNDY.palette.primary
    } else {
        AtharPrimary
    }

/** Brighter backdrop ink for small previews. */
val AtharBackdropHighlight: Color
    get() = if (AtharThemeState.themeId == AtharThemeId.BURGUNDY) {
        AtharThemeId.BURGUNDY.palette.primaryLight
    } else {
        AtharPrimaryLight
    }

// ─── Text Hierarchy ───
val AtharTextPrimary: Color get() = AtharThemeState.current.textPrimary
val AtharTextSecondary: Color get() = AtharThemeState.current.textSecondary
val AtharTextMuted: Color get() = AtharThemeState.current.textMuted
val AtharTextOnPrimary: Color get() = AtharThemeState.current.textOnPrimary

// ─── Navigation Bar (Floating Capsule Dock) ───
val AtharNavbarBg: Color get() = AtharThemeState.current.navbarBg
val AtharNavbarBorder: Color get() = AtharThemeState.current.navbarBorder
val AtharNavPillSelected: Color get() = AtharThemeState.current.navPillSelected
val AtharNavPillBorder: Color get() = AtharThemeState.current.navPillBorder
val AtharNavIconActive: Color get() = AtharThemeState.current.navIconActive
val AtharNavIconInactive: Color get() = AtharThemeState.current.navIconInactive
val AtharNavGlow: Color get() = AtharThemeState.current.navGlow

// ─── Featured Service Card Accents ───
val AtharEmerald: Color get() = AtharThemeState.current.emerald
val AtharAmber: Color get() = AtharThemeState.current.amber
val AtharTerracotta: Color get() = AtharThemeState.current.terracotta
val AtharTeal: Color get() = AtharThemeState.current.teal
val AtharLavender: Color get() = AtharThemeState.current.lavender

// ─── Dividers & Borders ───
val AtharOutline: Color get() = AtharThemeState.current.outline
val AtharOutlineVariant: Color get() = AtharThemeState.current.outlineVariant

// ─── Gradients for Featured Cards ───
val AtharGradientStart: Color get() = AtharThemeState.current.gradientStart
val AtharGradientEnd: Color get() = AtharThemeState.current.gradientEnd

// ─── Legacy aliases for backward compatibility ───
@Deprecated("Use AtharBackground", replaceWith = ReplaceWith("AtharBackground"))
val AtharBlack: Color get() = AtharBackground
@Deprecated("Use AtharSurface", replaceWith = ReplaceWith("AtharSurface"))
val AtharDarkGray: Color get() = AtharSurface
@Deprecated("Use AtharCardSurface", replaceWith = ReplaceWith("AtharCardSurface"))
val AtharCardGray: Color get() = AtharCardSurface
@Deprecated("Use AtharPrimary", replaceWith = ReplaceWith("AtharPrimary"))
val AtharGold: Color get() = AtharPrimary
@Deprecated("Use AtharPrimaryMuted", replaceWith = ReplaceWith("AtharPrimaryMuted"))
val AtharMutedGold: Color get() = AtharPrimaryMuted
@Deprecated("Use AtharTextPrimary", replaceWith = ReplaceWith("AtharTextPrimary"))
val AtharWhite: Color get() = AtharTextPrimary
@Deprecated("Use AtharEmerald", replaceWith = ReplaceWith("AtharEmerald"))
val AtharGreen: Color get() = AtharEmerald
@Deprecated("Use AtharTeal", replaceWith = ReplaceWith("AtharTeal"))
val AtharBlue: Color get() = AtharTeal
@Deprecated("Use AtharTerracotta", replaceWith = ReplaceWith("AtharTerracotta"))
val AtharRed: Color get() = AtharTerracotta
@Deprecated("Use AtharLavender", replaceWith = ReplaceWith("AtharLavender"))
val AtharPurple: Color get() = AtharLavender

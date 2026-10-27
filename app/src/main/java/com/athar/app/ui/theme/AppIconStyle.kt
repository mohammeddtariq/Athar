package com.athar.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Launcher icon colourways. Each one mirrors a theme so the icon on the home
 * screen belongs to the same palette as the app behind it.
 *
 * [alias] must match the `activity-alias` name declared in the manifest; the
 * launcher only ever shows the enabled alias.
 */
enum class AtharIconStyle(
    val id: String,
    val theme: AtharThemeId,
    val alias: String,
    val nameAr: String,
    val nameEn: String
) {
    OLIVE("OLIVE", AtharThemeId.OLIVE, "MainActivityOlive", "زيتوني", "Olive"),
    BURGUNDY("BURGUNDY", AtharThemeId.BURGUNDY, "MainActivityBurgundy", "عُنّابي", "Burgundy"),
    BROWN("BROWN", AtharThemeId.BROWN, "MainActivityBrown", "بُنّي", "Brown"),
    AMOLED("AMOLED", AtharThemeId.AMOLED, "MainActivityAmoled", "أسود نقي", "AMOLED");

    /** Ink colour of the icon plate, matching the theme's background. */
    val plate: Color
        get() = theme.paletteFor(AtharAccentTone.GOLD).background

    /** Colour of the calligraphic mark and wordmark on the icon. */
    val ink: Color
        get() = theme.paletteFor(AtharAccentTone.GOLD).primaryLight

    companion object {
        fun fromId(id: String?): AtharIconStyle? =
            entries.firstOrNull { it.id == id }

        /** The icon that belongs to [theme] when the user has not picked another. */
        fun forTheme(theme: AtharThemeId): AtharIconStyle =
            entries.firstOrNull { it.theme == theme } ?: OLIVE
    }
}

package com.athar.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * The decorative surface drawn behind the whole app.
 *
 * Both patterns share the same rules — hairline ink, dense towards the top-end
 * corner and fading out towards the bottom-start — so switching between them
 * never changes how readable the content on top is. [SOLID] leaves the theme's
 * own background untouched, which is the only true-black option for AMOLED.
 */
enum class AtharBackgroundStyle(
    val id: String,
    val nameAr: String,
    val nameEn: String
) {
    /** Interlocking eight-point star rosettes with strapwork ribbons. */
    ISLAMIC(
        id = "ISLAMIC",
        nameAr = "مشربية أثر",
        nameEn = "Athar lattice"
    ),

    /** Flowing bismillah calligraphy, inked like a manuscript margin. */
    LETTERS(
        id = "LETTERS",
        nameAr = "نسيج الحروف",
        nameEn = "Huruf weave"
    ),

    /** No pattern at all — just the theme's own colour, edge to edge. */
    SOLID(
        id = "SOLID",
        nameAr = "لون صافٍ",
        nameEn = "Solid colour"
    );

    companion object {
        /** Falls back to the geometric backdrop for unknown or missing ids. */
        fun fromId(id: String?): AtharBackgroundStyle =
            entries.firstOrNull { it.id == id } ?: ISLAMIC

        val default: AtharBackgroundStyle = ISLAMIC
    }
}

/**
 * The backdrop style the app currently draws. Backed by snapshot state so
 * changing it repaints every screen without a restart.
 */
object AtharBackgroundState {
    var current: AtharBackgroundStyle by mutableStateOf(AtharBackgroundStyle.default)
        internal set
}

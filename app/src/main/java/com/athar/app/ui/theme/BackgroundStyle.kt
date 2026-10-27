package com.athar.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * The decorative pattern drawn behind the whole app.
 *
 * Both styles follow the same rules — hairline ink, dense towards the top-end
 * corner and fading out towards the bottom-start — so switching between them
 * never changes how readable the content on top is.
 */
enum class AtharBackgroundStyle(
    val id: String,
    val nameAr: String,
    val nameEn: String
) {
    /** Interlocking eight-point star rosettes with strapwork ribbons. */
    ISLAMIC(
        id = "ISLAMIC",
        nameAr = "رموز إسلامية",
        nameEn = "Islamic symbols"
    ),

    /** Flowing Arabic letterforms scattered across the surface. */
    LETTERS(
        id = "LETTERS",
        nameAr = "حروف عربية",
        nameEn = "Arabic letters"
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

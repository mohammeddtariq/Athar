package com.athar.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "athar_preferences")

/**
 * Single source of truth for user preferences.
 * Kept in one DataStore file ("athar_preferences") shared with [LanguagePreferences].
 */
class AppPreferences(private val context: Context) {

    companion object {
        // Language / onboarding (mirrors keys in LanguagePreferences for interop)
        private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")

        // Location
        private val LAT_KEY = doublePreferencesKey("latitude")
        private val LNG_KEY = doublePreferencesKey("longitude")
        private val CITY_KEY = stringPreferencesKey("city_label")
        private val LOCATION_SET_KEY = booleanPreferencesKey("location_set")

        // Calculation (method + school)
        private val METHOD_KEY = stringPreferencesKey("calc_method") // e.g. "MWL"
        private val MADHAB_KEY = stringPreferencesKey("madhab")      // "SHAFI" | "HANAFI"
        private val SCHOOL_KEY = stringPreferencesKey("school")      // "HANAFI"|"MALIKI"|"SHAFII"|"HANBALI"

        // Notifications
        private val NOTIF_MASTER_KEY = booleanPreferencesKey("notif_master")
        private val NOTIF_PREFIX = "notif_prayer_"

        // Number formatting preference
        private val NUMBER_STYLE_KEY = stringPreferencesKey("number_style")

        // Quran Appearance & Audio
        private val QURAN_THEME_KEY = stringPreferencesKey("quran_theme_mode")
        private val QURAN_FONT_SCALE_KEY = doublePreferencesKey("quran_font_scale")
        private val QURAN_RECITER_KEY = stringPreferencesKey("quran_reciter")
        private val QURAN_LAYOUT_MODE_KEY = stringPreferencesKey("quran_layout_mode")

        // Quran Last Read Position
        private val LAST_READ_SURAH_NUMBER_KEY = intPreferencesKey("last_read_surah_number")
        private val LAST_READ_SURAH_NAME_AR_KEY = stringPreferencesKey("last_read_surah_name_ar")
        private val LAST_READ_SURAH_NAME_EN_KEY = stringPreferencesKey("last_read_surah_name_en")
        private val LAST_READ_PAGE_NUMBER_KEY = intPreferencesKey("last_read_page_number")
        private val LAST_READ_TIMESTAMP_KEY = longPreferencesKey("last_read_timestamp")
    }

    val selectedLanguage: Flow<String> = context.dataStore.data.map { it[LANGUAGE_KEY] ?: "ar" }
    val numberStyle: Flow<NumberStylePreference> = context.dataStore.data.map {
        NumberStylePreference.fromId(it[NUMBER_STYLE_KEY] ?: "western")
    }
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED_KEY] ?: false }

    val latitude: Flow<Double?> = context.dataStore.data.map { it[LAT_KEY] }
    val longitude: Flow<Double?> = context.dataStore.data.map { it[LNG_KEY] }
    val cityLabel: Flow<String?> = context.dataStore.data.map { it[CITY_KEY] }
    val isLocationSet: Flow<Boolean> = context.dataStore.data.map { it[LOCATION_SET_KEY] ?: false }

    val calcMethodId: Flow<String> = context.dataStore.data.map { it[METHOD_KEY] ?: "MWL" }
    val madhabId: Flow<String> = context.dataStore.data.map { it[MADHAB_KEY] ?: "SHAFI" }
    val schoolId: Flow<String> = context.dataStore.data.map { it[SCHOOL_KEY] ?: "SHAFII" }

    val notificationsMaster: Flow<Boolean> = context.dataStore.data.map { it[NOTIF_MASTER_KEY] ?: false }

    val quranThemeMode: Flow<QuranThemeMode> = context.dataStore.data.map {
        QuranThemeMode.fromId(it[QURAN_THEME_KEY] ?: "AMOLED")
    }
    val quranFontScale: Flow<Float> = context.dataStore.data.map {
        (it[QURAN_FONT_SCALE_KEY] ?: 1.0).toFloat()
    }
    val quranReciter: Flow<QuranReciter> = context.dataStore.data.map {
        QuranReciter.fromId(it[QURAN_RECITER_KEY] ?: "minshawi")
    }
    val quranLayoutMode: Flow<QuranLayoutMode> = context.dataStore.data.map {
        QuranLayoutMode.fromId(it[QURAN_LAYOUT_MODE_KEY] ?: "TEXT")
    }

    val lastReadSurahNumber: Flow<Int?> = context.dataStore.data.map { it[LAST_READ_SURAH_NUMBER_KEY] }
    val lastReadSurahNameAr: Flow<String?> = context.dataStore.data.map { it[LAST_READ_SURAH_NAME_AR_KEY] }
    val lastReadSurahNameEn: Flow<String?> = context.dataStore.data.map { it[LAST_READ_SURAH_NAME_EN_KEY] }
    val lastReadPageNumber: Flow<Int?> = context.dataStore.data.map { it[LAST_READ_PAGE_NUMBER_KEY] }
    val lastReadTimestamp: Flow<Long?> = context.dataStore.data.map { it[LAST_READ_TIMESTAMP_KEY] }

    fun prayerNotificationEnabled(prayerKey: String): Flow<Boolean> =
        context.dataStore.data.map { it[booleanPreferencesKey("$NOTIF_PREFIX$prayerKey")] ?: true }

    suspend fun setLanguageAndCompleteOnboarding(languageCode: String) {
        context.dataStore.edit {
            it[LANGUAGE_KEY] = languageCode
            it[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { it[ONBOARDING_COMPLETED_KEY] = true }
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = languageCode }
    }

    suspend fun setLocation(lat: Double, lng: Double, city: String) {
        context.dataStore.edit {
            it[LAT_KEY] = lat
            it[LNG_KEY] = lng
            it[CITY_KEY] = city
            it[LOCATION_SET_KEY] = true
        }
    }

    suspend fun clearLocation() {
        context.dataStore.edit {
            it.remove(LAT_KEY)
            it.remove(LNG_KEY)
            it.remove(CITY_KEY)
            it[LOCATION_SET_KEY] = false
        }
    }

    suspend fun setCalcMethod(methodId: String) {
        context.dataStore.edit { it[METHOD_KEY] = methodId }
    }

    suspend fun setMadhab(madhabId: String) {
        context.dataStore.edit { it[MADHAB_KEY] = madhabId }
    }

    /**
     * School picker (Hanafi / Maliki / Shafii / Hanbali).
     * Only Hanafi changes the Asr time (larger shadow length);
     * the other three share the standard Asr. Madhab is kept in sync.
     */
    suspend fun setSchool(schoolId: String) {
        context.dataStore.edit {
            it[SCHOOL_KEY] = schoolId
            it[MADHAB_KEY] = if (schoolId == "HANAFI") "HANAFI" else "SHAFI"
        }
    }

    /** "Hanafi Asr" switch: on = Hanafi school, off = back to Shafii. */
    suspend fun setHanafiAsr(enabled: Boolean) {
        setSchool(if (enabled) "HANAFI" else "SHAFII")
    }

    suspend fun setNotificationsMaster(enabled: Boolean) {
        context.dataStore.edit { it[NOTIF_MASTER_KEY] = enabled }
    }

    suspend fun setPrayerNotification(prayerKey: String, enabled: Boolean) {
        context.dataStore.edit { it[booleanPreferencesKey("$NOTIF_PREFIX$prayerKey")] = enabled }
    }

    suspend fun setQuranThemeMode(mode: QuranThemeMode) {
        context.dataStore.edit { it[QURAN_THEME_KEY] = mode.id }
    }

    suspend fun setQuranFontScale(scale: Float) {
        context.dataStore.edit { it[QURAN_FONT_SCALE_KEY] = scale.toDouble() }
    }

    suspend fun setQuranReciter(reciter: QuranReciter) {
        context.dataStore.edit { it[QURAN_RECITER_KEY] = reciter.id }
    }

    suspend fun setQuranLayoutMode(mode: QuranLayoutMode) {
        context.dataStore.edit { it[QURAN_LAYOUT_MODE_KEY] = mode.id }
    }

    suspend fun setNumberStyle(style: NumberStylePreference) {
        context.dataStore.edit { it[NUMBER_STYLE_KEY] = style.id }
    }

    suspend fun saveLastReadPosition(
        surahNumber: Int,
        surahNameAr: String,
        surahNameEn: String,
        pageNumber: Int
    ) {
        context.dataStore.edit {
            it[LAST_READ_SURAH_NUMBER_KEY] = surahNumber
            it[LAST_READ_SURAH_NAME_AR_KEY] = surahNameAr
            it[LAST_READ_SURAH_NAME_EN_KEY] = surahNameEn
            it[LAST_READ_PAGE_NUMBER_KEY] = pageNumber
            it[LAST_READ_TIMESTAMP_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun clearLastReadPosition() {
        context.dataStore.edit {
            it.remove(LAST_READ_SURAH_NUMBER_KEY)
            it.remove(LAST_READ_SURAH_NAME_AR_KEY)
            it.remove(LAST_READ_SURAH_NAME_EN_KEY)
            it.remove(LAST_READ_PAGE_NUMBER_KEY)
            it.remove(LAST_READ_TIMESTAMP_KEY)
        }
    }
}

enum class NumberStylePreference(val id: String) {
    WESTERN("western"),
    ARABIC_INDIC("arabic");

    companion object {
        fun fromId(id: String): NumberStylePreference = entries.firstOrNull { it.id == id } ?: WESTERN
    }
}

fun formatDigits(text: String, style: NumberStylePreference): String {
    return when (style) {
        NumberStylePreference.WESTERN -> text.map { c ->
            if (c in '٠'..'٩') ('0' + (c - '٠')) else c
        }.joinToString("")
        NumberStylePreference.ARABIC_INDIC -> text.map { c ->
            if (c in '0'..'9') ('٠' + (c - '0')) else c
        }.joinToString("")
    }
}

/** Quran Reciters (Sheikh Mohamed Siddiq Al-Minshawi & Sheikh Mishary Alafasy) */
enum class QuranReciter(
    val id: String,
    val arabicName: String,
    val englishName: String,
    val baseUrl: String
) {
    MINSHAWI(
        id = "minshawi",
        arabicName = "الشيخ محمد صديق المنشاوي (مرتل)",
        englishName = "Mohamed Siddiq Al-Minshawi",
        baseUrl = "https://server10.mp3quran.net/minsh"
    ),
    ALAFASY(
        id = "alafasy",
        arabicName = "الشيخ مشاري راشد العفاسي",
        englishName = "Mishary Rashid Alafasy",
        baseUrl = "https://server8.mp3quran.net/afs"
    );

    companion object {
        fun fromId(id: String): QuranReciter = entries.firstOrNull { it.id == id } ?: MINSHAWI
    }
}

/** Quran Reader Layout Modes: Traditional Text Flow (Default) vs Vector Mushaf Pages (Beta) */
enum class QuranLayoutMode(val id: String) {
    TEXT("TEXT"),
    PAGES_SVG("PAGES_SVG");

    companion object {
        fun fromId(id: String): QuranLayoutMode = entries.firstOrNull { it.id == id } ?: TEXT
    }
}

/** 3 Quran Appearance Modes: Pure AMOLED Black, App Dark Olive, and Light Paper */
enum class QuranThemeMode(val id: String) {
    AMOLED("AMOLED"),
    DARK_OLIVE("DARK_OLIVE"),
    LIGHT("LIGHT");

    companion object {
        fun fromId(id: String): QuranThemeMode = entries.firstOrNull { it.id == id } ?: AMOLED
    }
}

/** Calculation methods offered in setup & settings (subset of the Adhan library). */
enum class CalcMethod(val id: String) {
    MWL("MWL"),
    EGYPTIAN("EGYPTIAN"),
    KARACHI("KARACHI"),
    UMM_AL_QURA("UMM_AL_QURA"),
    ISNA("ISNA"),
    MOON_SIGHTING("MOON_SIGHTING");

    companion object {
        fun fromId(id: String): CalcMethod = entries.firstOrNull { it.id == id } ?: MWL
    }
}

enum class MadhabOption(val id: String) {
    SHAFI("SHAFI"),
    HANAFI("HANAFI");

    companion object {
        fun fromId(id: String): MadhabOption = entries.firstOrNull { it.id == id } ?: SHAFI
    }
}

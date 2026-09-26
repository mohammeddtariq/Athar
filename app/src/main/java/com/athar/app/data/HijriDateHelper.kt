package com.athar.app.data

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/**
 * Helper to compute and format the Islamic (Hijri) calendar date
 * respecting localized month names, active language, and number styles.
 */
object HijriDateHelper {

    private val ARABIC_MONTHS = listOf(
        "المحرّم",
        "صفر",
        "ربيع الأول",
        "ربيع الآخر",
        "جمادى الأولى",
        "جمادى الآخرة",
        "رجب",
        "شعبان",
        "رمضان",
        "شوّال",
        "ذو القعدة",
        "ذو الحجة"
    )

    private val ENGLISH_MONTHS = listOf(
        "Muharram",
        "Safar",
        "Rabi' al-Awwal",
        "Rabi' al-Thani",
        "Jumada al-Ula",
        "Jumada al-Akhirah",
        "Rajab",
        "Sha'ban",
        "Ramadan",
        "Shawwal",
        "Dhu al-Qi'dah",
        "Dhu al-Hijjah"
    )

    fun formatHijriDate(
        date: LocalDate = LocalDate.now(),
        isArabic: Boolean = true,
        numberStyle: NumberStylePreference = NumberStylePreference.WESTERN
    ): String {
        return try {
            val hijrah = HijrahDate.from(date)
            val day = hijrah.get(ChronoField.DAY_OF_MONTH)
            val month = hijrah.get(ChronoField.MONTH_OF_YEAR)
            val year = hijrah.get(ChronoField.YEAR)

            val monthIndex = (month - 1).coerceIn(0, 11)
            val formattedDay = formatDigits(day.toString(), numberStyle)
            val formattedYear = formatDigits(year.toString(), numberStyle)

            if (isArabic) {
                val monthName = ARABIC_MONTHS[monthIndex]
                "$formattedDay $monthName $formattedYear هـ"
            } else {
                val monthName = ENGLISH_MONTHS[monthIndex]
                "$formattedDay $monthName $formattedYear AH"
            }
        } catch (_: Exception) {
            ""
        }
    }
}

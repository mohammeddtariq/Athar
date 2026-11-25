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

    private val ENGLISH_MONTHS_SHORT = listOf(
        "Muh.",
        "Saf.",
        "Rab. I",
        "Rab. II",
        "Jum. I",
        "Jum. II",
        "Raj.",
        "Sha.",
        "Ram.",
        "Shaw.",
        "Dhu Q.",
        "Dhu H."
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

    /**
     * Compact Hijri date for narrow surfaces such as home-screen widgets.
     * Arabic keeps its full month names (they already fit), while English
     * switches to short month labels so the line never truncates.
     */
    fun formatHijriDateCompact(
        date: LocalDate = LocalDate.now(),
        isArabic: Boolean = true,
        numberStyle: NumberStylePreference = NumberStylePreference.WESTERN
    ): String {
        if (isArabic) return formatHijriDate(date, isArabic = true, numberStyle = numberStyle)
        return try {
            val hijrah = HijrahDate.from(date)
            val day = hijrah.get(ChronoField.DAY_OF_MONTH)
            val month = hijrah.get(ChronoField.MONTH_OF_YEAR)
            val year = hijrah.get(ChronoField.YEAR)

            val monthIndex = (month - 1).coerceIn(0, 11)
            val formattedDay = formatDigits(day.toString(), numberStyle)
            val formattedYear = formatDigits(year.toString(), numberStyle)
            "$formattedDay ${ENGLISH_MONTHS_SHORT[monthIndex]} $formattedYear AH"
        } catch (_: Exception) {
            ""
        }
    }

    private val ARABIC_GREGORIAN_MONTHS = listOf(
        "يناير",
        "فبراير",
        "مارس",
        "أبريل",
        "مايو",
        "يونيو",
        "يوليو",
        "أغسطس",
        "سبتمبر",
        "أكتوبر",
        "نوفمبر",
        "ديسمبر"
    )

    private val ENGLISH_GREGORIAN_MONTHS = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    private val ENGLISH_GREGORIAN_MONTHS_SHORT = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    fun formatGregorianDate(
        date: LocalDate = LocalDate.now(),
        isArabic: Boolean = true,
        numberStyle: NumberStylePreference = NumberStylePreference.WESTERN
    ): String {
        return try {
            val day = date.dayOfMonth
            val month = date.monthValue
            val year = date.year

            val monthIndex = (month - 1).coerceIn(0, 11)
            val formattedDay = formatDigits(day.toString(), numberStyle)
            val formattedYear = formatDigits(year.toString(), numberStyle)

            if (isArabic) {
                val monthName = ARABIC_GREGORIAN_MONTHS[monthIndex]
                "$formattedDay $monthName $formattedYear م"
            } else {
                val monthName = ENGLISH_GREGORIAN_MONTHS[monthIndex]
                "$formattedDay $monthName $formattedYear"
            }
        } catch (_: Exception) {
            ""
        }
    }

    /** Compact Gregorian date for narrow widget lines. */
    fun formatGregorianDateCompact(
        date: LocalDate = LocalDate.now(),
        isArabic: Boolean = true,
        numberStyle: NumberStylePreference = NumberStylePreference.WESTERN
    ): String {
        if (isArabic) return formatGregorianDate(date, isArabic = true, numberStyle = numberStyle)
        return try {
            val monthIndex = (date.monthValue - 1).coerceIn(0, 11)
            val formattedDay = formatDigits(date.dayOfMonth.toString(), numberStyle)
            val formattedYear = formatDigits(date.year.toString(), numberStyle)
            "$formattedDay ${ENGLISH_GREGORIAN_MONTHS_SHORT[monthIndex]} $formattedYear"
        } catch (_: Exception) {
            ""
        }
    }
}

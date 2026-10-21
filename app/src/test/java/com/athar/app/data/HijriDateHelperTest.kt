package com.athar.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HijriDateHelperTest {

    @Test
    fun gregorianToHijri_knownDates() {
        assertEquals(
            "1 Ramadan 1447 AH",
            HijriDateHelper.formatHijriDate(LocalDate.of(2026, 2, 18), isArabic = false)
        )
        assertEquals(
            "1 Shawwal 1447 AH",
            HijriDateHelper.formatHijriDate(LocalDate.of(2026, 3, 20), isArabic = false)
        )
        assertEquals(
            "10 Dhu al-Hijjah 1447 AH",
            HijriDateHelper.formatHijriDate(LocalDate.of(2026, 5, 27), isArabic = false)
        )
        assertEquals(
            "1 Muharram 1448 AH",
            HijriDateHelper.formatHijriDate(LocalDate.of(2026, 6, 16), isArabic = false)
        )
    }

    @Test
    fun arabicFormatting_usesLocalizedMonthAndSuffix() {
        assertEquals(
            "1 رمضان 1447 هـ",
            HijriDateHelper.formatHijriDate(LocalDate.of(2026, 2, 18), isArabic = true)
        )
        assertEquals(
            "١ رمضان ١٤٤٧ هـ",
            HijriDateHelper.formatHijriDate(
                LocalDate.of(2026, 2, 18),
                isArabic = true,
                numberStyle = NumberStylePreference.ARABIC_INDIC
            )
        )
        assertEquals(
            "١٢ شوّال ١٤٤٧ هـ",
            HijriDateHelper.formatHijriDate(
                LocalDate.of(2026, 3, 31),
                isArabic = true,
                numberStyle = NumberStylePreference.ARABIC_INDIC
            )
        )
    }

    @Test
    fun gregorianFormatting_matchesLanguageAndNumberStyle() {
        assertEquals(
            "21 October 2026",
            HijriDateHelper.formatGregorianDate(LocalDate.of(2026, 10, 21), isArabic = false)
        )
        assertEquals(
            "٢١ أكتوبر ٢٠٢٦ م",
            HijriDateHelper.formatGregorianDate(
                LocalDate.of(2026, 10, 21),
                isArabic = true,
                numberStyle = NumberStylePreference.ARABIC_INDIC
            )
        )
    }

    @Test
    fun formattedHijriDate_staysValidForAYearOfDates() {
        var date = LocalDate.of(2026, 1, 1)
        repeat(366) {
            val formatted = HijriDateHelper.formatHijriDate(date, isArabic = false)
            assertTrue("blank for $date", formatted.isNotBlank())
            assertTrue("missing AH for $date: $formatted", formatted.endsWith(" AH"))
            assertTrue("missing year for $date: $formatted", formatted.contains("14"))
            date = date.plusDays(1)
        }
    }
}

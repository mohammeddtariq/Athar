package com.athar.app

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun versionRegex_matchesCorrectly() {
        val regex = Regex("""^v?(\d+\.\d+(?:\.\d+)?)-beta\.?(\d+)?$""", RegexOption.IGNORE_CASE)
        
        val match1 = regex.matchEntire("1.0.0-beta.2")
        assertNotNull(match1)
        assertEquals("1.0.0", match1!!.groupValues[1])
        assertEquals("2", match1.groupValues[2])

        val matchBeta1 = regex.matchEntire("1.0.1-beta.1")
        assertNotNull(matchBeta1)
        assertEquals("1.0.1", matchBeta1!!.groupValues[1])
        assertEquals("1", matchBeta1.groupValues[2])

        val match2 = regex.matchEntire("1.0.0-beta")
        assertNotNull(match2)
        assertEquals("1.0.0", match2!!.groupValues[1])
        assertEquals("", match2.groupValues[2])

        val match3 = regex.matchEntire("1.0.0")
        assertNull(match3)
    }

    @Test
    fun hijriDate_formatsCorrectly() {
        val date = java.time.LocalDate.of(2026, 9, 26)
        val arEastern = com.athar.app.data.HijriDateHelper.formatHijriDate(
            date = date,
            isArabic = true,
            numberStyle = com.athar.app.data.NumberStylePreference.ARABIC_INDIC
        )
        val enWestern = com.athar.app.data.HijriDateHelper.formatHijriDate(
            date = date,
            isArabic = false,
            numberStyle = com.athar.app.data.NumberStylePreference.WESTERN
        )
        assertTrue(arEastern.contains("هـ"))
        assertTrue(enWestern.contains("AH"))
    }
}
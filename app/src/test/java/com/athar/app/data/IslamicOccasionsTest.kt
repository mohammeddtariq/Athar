package com.athar.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class IslamicOccasionsTest {

    @Test
    fun ramadanStartMatchesTheHijriHelper() {
        val next = IslamicOccasions.next(LocalDate.of(2026, 2, 17))
        assertNotNull(next)
        assertEquals("First day of Ramadan", next!!.occasion.titleEn)
        assertEquals(LocalDate.of(2026, 2, 18), next.date)
        assertEquals(1L, next.daysAway)
        assertEquals(
            HijriDateHelper.formatHijriDate(next.date, isArabic = false),
            "1 Ramadan 1447 AH"
        )
    }

    @Test
    fun eidAlFitrFollowsRamadan() {
        val next = IslamicOccasions.next(LocalDate.of(2026, 3, 19))
        assertNotNull(next)
        assertEquals("Eid al-Fitr", next!!.occasion.titleEn)
        assertEquals(LocalDate.of(2026, 3, 20), next.date)
    }

    @Test
    fun arafahAndAdhaAreConsecutive() {
        val eidWeek = IslamicOccasions.upcoming(LocalDate.of(2026, 5, 20), searchDays = 30, limit = 4)
            .map { it.occasion.titleEn to it.date }
        assertTrue(eidWeek.contains("Day of Arafah" to LocalDate.of(2026, 5, 26)))
        assertTrue(eidWeek.contains("Eid al-Adha" to LocalDate.of(2026, 5, 27)))
    }

    @Test
    fun upcomingIsChronologicalAndRespectsLimit() {
        val list = IslamicOccasions.upcoming(LocalDate.of(2026, 1, 1), searchDays = 400, limit = 6)
        assertEquals(6, list.size)
        assertEquals(list.map { it.date }.sorted(), list.map { it.date })
        assertEquals(list.map { it.daysAway }.sorted(), list.map { it.daysAway })
        assertEquals(0, IslamicOccasions.upcoming(LocalDate.of(2026, 1, 1), searchDays = 400, limit = 0).size)
    }

    @Test
    fun nextOccasionIsNeverMoreThanAYearAway() {
        var date = LocalDate.of(2026, 1, 1)
        repeat(24) {
            val next = IslamicOccasions.next(date)
            assertNotNull("no occasion found after $date", next)
            assertTrue(next!!.date.isAfter(date) || next.date == date)
            assertTrue("too far from $date", next.daysAway <= 400)
            date = date.plusDays(30)
        }
    }

    @Test
    fun everyOccasionDateResolvesToItsOwnHijriDay() {
        for (occasion in IslamicOccasions.all) {
            val found = IslamicOccasions.upcoming(LocalDate.of(2026, 1, 1), searchDays = 400, limit = 60)
                .filter { it.occasion == occasion }
            assertTrue("${occasion.titleEn} missing in the next 400 days", found.isNotEmpty())
            val instance = found.first()
            val hijri = java.time.chrono.HijrahDate.from(instance.date)
            assertEquals(occasion.hijriMonth, hijri.get(java.time.temporal.ChronoField.MONTH_OF_YEAR))
            assertEquals(occasion.hijriDay, hijri.get(java.time.temporal.ChronoField.DAY_OF_MONTH))
            assertTrue(instance.hijriYear in 1447..1449)
        }
    }
}

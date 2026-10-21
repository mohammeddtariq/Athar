package com.athar.app.data

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.TimeZone

class PrayerTimesCalculatorTest {

    private lateinit var originalZone: TimeZone

    @Before
    fun setUp() {
        originalZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"))
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(originalZone)
    }

    @Test
    fun cairoSummerSolstice_matchesKnownTimes() {
        val p = computeDayPrayers(30.0444, 31.2357, LocalDate.of(2026, 6, 15))
        assertEquals(LocalTime.of(4, 17), p.fajr)
        assertEquals(LocalTime.of(5, 53), p.sunrise)
        assertEquals(LocalTime.of(12, 57), p.dhuhr)
        assertEquals(LocalTime.of(16, 31), p.asr)
        assertEquals(LocalTime.of(19, 58), p.maghrib)
        assertEquals(LocalTime.of(21, 28), p.isha)
        assertEquals(LocalDate.of(2026, 6, 15), p.date)
    }

    @Test
    fun cairoDstChangeDay_keepsPrayersOrdered() {
        for (date in listOf(LocalDate.of(2026, 3, 29), LocalDate.of(2026, 10, 25))) {
            val p = computeDayPrayers(30.0444, 31.2357, date)
            assertEquals(date, p.date)
            val times = p.ordered().map { it.second }
            assertEquals(times.sorted(), times)
        }
    }

    @Test
    fun prayersStayOrderedAcrossTheYear() {
        for (day in 1..12) {
            val date = LocalDate.of(2026, day, 15)
            for (method in CalcMethod.entries) {
                val p = computeDayPrayers(24.7136, 46.6753, date, method = method)
                val times = p.ordered().map { it.second }
                assertEquals("$method on $date", times.sorted(), times)
            }
        }
    }

    @Test
    fun hanafiAsrIsLaterThanShafiAsr() {
        val shafi = computeDayPrayers(30.0444, 31.2357, LocalDate.of(2026, 6, 15), madhab = MadhabOption.SHAFI)
        val hanafi = computeDayPrayers(30.0444, 31.2357, LocalDate.of(2026, 6, 15), madhab = MadhabOption.HANAFI)
        assertEquals(shafi.dhuhr, hanafi.dhuhr)
        assertTrue(hanafi.asr.isAfter(shafi.asr))
    }

    @Test
    fun extremeLatitude_returnsFallbackForTheRequestedDay() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Oslo"))
        for (date in listOf(LocalDate.of(2026, 6, 21), LocalDate.of(2026, 12, 21))) {
            val p = computeDayPrayers(69.6492, 18.9553, date)
            assertEquals(date, p.date)
            val times = p.ordered().map { it.second }
            assertEquals(times.sorted(), times)
        }
    }

    @Test
    fun fallbackDayPrayers_carriesTheRequestedDate() {
        val date = LocalDate.of(2027, 2, 1)
        assertEquals(date, fallbackDayPrayers(date).date)
        assertEquals(fallbackDayPrayers().date, LocalDate.now())
    }

    @Test
    fun findNextPrayer_usesTodayUntilIshaThenTomorrowFajr() {
        val day = DayPrayers(
            fajr = LocalTime.of(5, 0),
            sunrise = LocalTime.of(6, 0),
            dhuhr = LocalTime.of(12, 0),
            asr = LocalTime.of(15, 0),
            maghrib = LocalTime.of(18, 0),
            isha = LocalTime.of(19, 30),
            date = LocalDate.of(2026, 6, 15)
        )

        findNextPrayer(day, LocalTime.of(4, 59)).let {
            assertEquals("fajr", it.key)
            assertFalse(it.isTomorrow)
        }
        findNextPrayer(day, LocalTime.of(5, 0)).let {
            assertEquals("fajr", it.key)
            assertFalse(it.isTomorrow)
        }
        findNextPrayer(day, LocalTime.of(6, 30)).let {
            assertEquals("dhuhr", it.key)
            assertFalse(it.isTomorrow)
        }
        findNextPrayer(day, LocalTime.of(12, 30)).let {
            assertEquals("asr", it.key)
        }
        findNextPrayer(day, LocalTime.of(19, 30)).let {
            assertEquals("isha", it.key)
            assertFalse(it.isTomorrow)
        }
        findNextPrayer(day, LocalTime.of(22, 0)).let {
            assertEquals("fajr", it.key)
            assertTrue(it.isTomorrow)
        }
    }

    @Test
    fun findNextPrayer_neverSelectsSunrise() {
        val day = DayPrayers(
            fajr = LocalTime.of(5, 0),
            sunrise = LocalTime.of(6, 0),
            dhuhr = LocalTime.of(12, 0),
            asr = LocalTime.of(15, 0),
            maghrib = LocalTime.of(18, 0),
            isha = LocalTime.of(19, 30),
            date = LocalDate.of(2026, 6, 15)
        )
        val next = findNextPrayer(day, LocalTime.of(5, 30))
        assertEquals("dhuhr", next.key)
        assertNull(day.ordered().firstOrNull { it.first == next.key && it.second == day.sunrise })
    }

    @Test
    fun prayerDateToday_rollsOverToTomorrow() {
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val todayDate = Calendar.getInstance().apply { time = prayerDateToday(LocalTime.of(5, 30)) }
        val tomorrowDate = Calendar.getInstance().apply { time = prayerDateToday(LocalTime.of(5, 30), true) }

        assertEquals(today.get(Calendar.DAY_OF_YEAR), todayDate.get(Calendar.DAY_OF_YEAR))
        assertEquals(tomorrow.get(Calendar.DAY_OF_YEAR), tomorrowDate.get(Calendar.DAY_OF_YEAR))
        assertEquals(5, todayDate.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, todayDate.get(Calendar.MINUTE))
        assertEquals(0, todayDate.get(Calendar.SECOND))
        assertEquals(0, todayDate.get(Calendar.MILLISECOND))
    }
}

package com.athar.app.data

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.HighLatitudeRule
import com.batoulapps.adhan.data.DateComponents
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

/**
 * Prayer-time calculations powered by the Batoulapps Adhan library (MIT).
 * See README credits. All app code here is our own independent implementation.
 */

data class DayPrayers(
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,
    val date: LocalDate = LocalDate.now()
) {
    fun ordered(): List<Pair<String, LocalTime>> = listOf(
        "fajr" to fajr,
        "sunrise" to sunrise,
        "dhuhr" to dhuhr,
        "asr" to asr,
        "maghrib" to maghrib,
        "isha" to isha
    )
}

data class NextPrayer(val key: String, val time: LocalTime, val isTomorrow: Boolean = false)

fun calcMethodParams(method: CalcMethod, madhab: MadhabOption): CalculationParameters {
    val base = when (method) {
        CalcMethod.MWL -> CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters()
        CalcMethod.EGYPTIAN -> CalculationMethod.EGYPTIAN.getParameters()
        CalcMethod.KARACHI -> CalculationMethod.KARACHI.getParameters()
        CalcMethod.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA.getParameters()
        CalcMethod.ISNA -> CalculationMethod.NORTH_AMERICA.getParameters()
        CalcMethod.MOON_SIGHTING -> CalculationMethod.MOON_SIGHTING_COMMITTEE.getParameters()
    }
    base.madhab = if (madhab == MadhabOption.HANAFI) Madhab.HANAFI else Madhab.SHAFI
    return base
}

fun computeDayPrayers(
    lat: Double,
    lng: Double,
    date: LocalDate = LocalDate.now(),
    method: CalcMethod = CalcMethod.MWL,
    madhab: MadhabOption = MadhabOption.SHAFI
): DayPrayers {
    val coordinates = Coordinates(lat, lng)
    val components = DateComponents(date.year, date.monthValue, date.dayOfMonth)
    val zone = ZoneId.systemDefault()

    fun readTimes(params: CalculationParameters): DayPrayers? {
        val times = PrayerTimes(coordinates, components, params)
        // Fields are null when the library can't converge (e.g. extreme latitudes).
        val fajr = times.fajr ?: return null
        val sunrise = times.sunrise ?: return null
        val dhuhr = times.dhuhr ?: return null
        val asr = times.asr ?: return null
        val maghrib = times.maghrib ?: return null
        val isha = times.isha ?: return null
        fun Date.toLocal(): LocalTime =
            this.toInstant().atZone(zone).toLocalTime().withSecond(0).withNano(0)
        return DayPrayers(
            fajr = fajr.toLocal(),
            sunrise = sunrise.toLocal(),
            dhuhr = dhuhr.toLocal(),
            asr = asr.toLocal(),
            maghrib = maghrib.toLocal(),
            isha = isha.toLocal(),
            date = date
        )
    }

    // Mid-summer and mid-winter at high latitudes have no astronomical twilight,
    // so fall back to the twilight-angle rule before giving up on the whole day.
    return readTimes(calcMethodParams(method, madhab))
        ?: readTimes(
            calcMethodParams(method, madhab).apply {
                highLatitudeRule = HighLatitudeRule.TWILIGHT_ANGLE
            }
        )
        ?: fallbackDayPrayers(date)
}

/** Fallback static times used before a location is set (matches previous design). */
fun fallbackDayPrayers(date: LocalDate = LocalDate.now()): DayPrayers = DayPrayers(
    fajr = LocalTime.of(5, 8),
    sunrise = LocalTime.of(6, 36),
    dhuhr = LocalTime.of(13, 52),
    asr = LocalTime.of(16, 32),
    maghrib = LocalTime.of(19, 6),
    isha = LocalTime.of(20, 33),
    date = date
)

fun findNextPrayer(today: DayPrayers, now: LocalTime = LocalTime.now()): NextPrayer {
    // Skip sunrise for "next prayer" purposes (not a prayer), but keep ordering otherwise.
    val candidates = listOf(
        "fajr" to today.fajr,
        "dhuhr" to today.dhuhr,
        "asr" to today.asr,
        "maghrib" to today.maghrib,
        "isha" to today.isha
    )
    for ((key, time) in candidates) {
        if (!time.isBefore(now)) return NextPrayer(key, time)
    }
    return NextPrayer("fajr", today.fajr, isTomorrow = true)
}

fun prayerDateToday(time: LocalTime, tomorrow: Boolean = false): Date {
    val cal = Calendar.getInstance()
    if (tomorrow) cal.add(Calendar.DAY_OF_YEAR, 1)
    cal.set(Calendar.HOUR_OF_DAY, time.hour)
    cal.set(Calendar.MINUTE, time.minute)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

package com.athar.app.data

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

data class IslamicOccasion(
    val hijriMonth: Int,
    val hijriDay: Int,
    val titleAr: String,
    val titleEn: String,
    val noteAr: String,
    val noteEn: String
)

data class OccasionInstance(
    val occasion: IslamicOccasion,
    val date: LocalDate,
    val hijriYear: Int,
    val daysAway: Long
)

/**
 * The recurring Hijri dates Muslims commonly mark. Dates are resolved with the
 * platform Umm al-Qura calendar, matching [HijriDateHelper].
 */
object IslamicOccasions {

    val all: List<IslamicOccasion> = listOf(
        IslamicOccasion(1, 1, "رأس السنة الهجرية", "Islamic New Year", "بداية العام الهجري", "The start of the Hijri year"),
        IslamicOccasion(1, 10, "يوم عاشوراء", "Ashura", "يُستحب صيامه", "Fasting is recommended"),
        IslamicOccasion(3, 12, "المولد النبوي", "Mawlid al-Nabi", "ذكرى مولد النبي ﷺ", "The Prophet's birthday ﷺ"),
        IslamicOccasion(7, 27, "الإسراء والمعراج", "Isra and Mi'raj", "ليلة الإسراء والمعراج", "The night journey"),
        IslamicOccasion(8, 15, "ليلة النصف من شعبان", "Mid-Sha'ban", "ليلة النصف من شعبان", "The middle night of Sha'ban"),
        IslamicOccasion(9, 1, "بداية رمضان", "First day of Ramadan", "غرة شهر رمضان", "The first day of Ramadan"),
        IslamicOccasion(9, 27, "ليلة القدر (المرجّحة)", "Laylat al-Qadr (likely)", "الليلة المرجّحة لليلة القدر", "The most likely night of Qadr"),
        IslamicOccasion(10, 1, "عيد الفطر", "Eid al-Fitr", "أول أيام عيد الفطر", "The first day of Eid al-Fitr"),
        IslamicOccasion(12, 9, "يوم عرفة", "Day of Arafah", "يُستحب صيامه لغير الحاج", "Fasting is recommended for non-pilgrims"),
        IslamicOccasion(12, 10, "عيد الأضحى", "Eid al-Adha", "أول أيام عيد الأضحى", "The first day of Eid al-Adha")
    )

    /**
     * Occasions falling between [from] and [from] + [searchDays], in chronological order.
     * The scan is day-by-day so it never needs a hand-maintained Gregorian table.
     */
    fun upcoming(
        from: LocalDate = LocalDate.now(),
        searchDays: Long = 400,
        limit: Int = 6
    ): List<OccasionInstance> {
        if (limit <= 0) return emptyList()
        val result = mutableListOf<OccasionInstance>()
        var offset = 0L
        while (offset <= searchDays && result.size < limit) {
            val date = from.plusDays(offset)
            val hijri = HijrahDate.from(date)
            val month = hijri.get(ChronoField.MONTH_OF_YEAR)
            val day = hijri.get(ChronoField.DAY_OF_MONTH)
            all.asSequence()
                .filter { it.hijriMonth == month && it.hijriDay == day }
                .forEach { occasion ->
                    result += OccasionInstance(
                        occasion = occasion,
                        date = date,
                        hijriYear = hijri.get(ChronoField.YEAR),
                        daysAway = offset
                    )
                }
            offset++
        }
        return result
    }

    fun next(from: LocalDate = LocalDate.now()): OccasionInstance? =
        upcoming(from = from, searchDays = 400, limit = 1).firstOrNull()
}

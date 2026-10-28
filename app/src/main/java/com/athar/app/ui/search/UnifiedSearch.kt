package com.athar.app.ui.search

import android.content.Context
import com.athar.app.data.DuaLibraryRepository
import com.athar.app.data.LibraryChapter
import com.athar.app.data.LibraryDua
import com.athar.app.data.QuranRepository
import com.athar.app.data.passageScore
import com.athar.app.data.search
import com.athar.app.data.searchWords
import com.athar.app.ui.corner.SurahMeta
import com.athar.app.ui.corner.allSurahs

data class SurahSearchHit(val surah: SurahMeta)

data class VerseSearchHit(val surah: SurahMeta, val ayah: Int, val text: String)

data class DuaSearchHit(
    val chapter: LibraryChapter,
    val duaIndex: Int?,
    val dua: LibraryDua?
)

data class UnifiedSearchResults(
    val surahs: List<SurahSearchHit> = emptyList(),
    val verses: List<VerseSearchHit> = emptyList(),
    val duas: List<DuaSearchHit> = emptyList()
) {
    val total: Int get() = surahs.size + verses.size + duas.size
}

/**
 * Offline search across the whole Quran (surah names and verse text) and the
 * Hisn al-Muslim library. Works on the bundled assets only — no network.
 */
suspend fun unifiedSearch(
    context: Context,
    rawQuery: String,
    limitPerGroup: Int = 12
): UnifiedSearchResults {
    val query = rawQuery.trim()
    if (query.length < 2) return UnifiedSearchResults()

    val words = searchWords(query)
    val english = query.lowercase()
    if (words.isEmpty() && english.length < 2) return UnifiedSearchResults()

    // Surah names: a match on the Arabic name, the English name or the number.
    val surahHits = ArrayList<Pair<SurahSearchHit, Float>>()
    for (surah in allSurahs) {
        val arabicScore = passageScore(
            words,
            searchWords("${surah.arabicName} ${surah.englishTranslation}")
        )
        val englishScore = when {
            surah.englishName.lowercase().contains(english) -> 1f
            surah.englishTranslation.lowercase().contains(english) -> 0.9f
            surah.number.toString() == query -> 1f
            else -> 0f
        }
        val score = maxOf(arabicScore, englishScore)
        if (score >= 0.62f) surahHits.add(SurahSearchHit(surah) to score)
    }
    val surahs = surahHits
        .sortedByDescending { it.second }
        .take(limitPerGroup)
        .map { it.first }

    // Verses: score every ayah, then keep the strongest. When nothing clears
    // the bar, the closest handful are still shown rather than nothing at all.
    val verseHits = ArrayList<Pair<VerseSearchHit, Float>>()
    for (surah in allSurahs) {
        val ayahs = QuranRepository.getSurahVerses(context, surah.number) ?: continue
        for (ayah in ayahs) {
            val score = passageScore(words, searchWords(ayah.text))
            if (score >= 0.30f) {
                verseHits.add(VerseSearchHit(surah, ayah.number, ayah.text) to score)
            }
        }
    }
    val ranked = verseHits.sortedByDescending { it.second }
    val verses = if ((ranked.firstOrNull()?.second ?: 0f) >= 0.58f) {
        ranked.filter { it.second >= 0.58f }.take(limitPerGroup).map { it.first }
    } else {
        // Nothing cleared the bar: show the closest few anyway, so a typo still
        // lands the reader near what they were looking for.
        ranked.take(minOf(5, limitPerGroup)).map { it.first }
    }

    val libraryHits = DuaLibraryRepository.load(context).search(query, limitPerGroup)
    val duas = libraryHits.map { hit ->
        DuaSearchHit(chapter = hit.chapter, duaIndex = hit.duaIndex, dua = hit.dua)
    }

    return UnifiedSearchResults(surahs = surahs, verses = verses, duas = duas)
}

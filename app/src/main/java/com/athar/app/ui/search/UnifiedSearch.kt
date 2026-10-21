package com.athar.app.ui.search

import android.content.Context
import com.athar.app.data.DuaLibraryRepository
import com.athar.app.data.LibraryChapter
import com.athar.app.data.LibraryDua
import com.athar.app.data.QuranRepository
import com.athar.app.data.normalizeArabicForSearch
import com.athar.app.data.search
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

    val normalized = normalizeArabicForSearch(query)
    val words = normalized.split(' ').filter { it.isNotBlank() }
    if (words.isEmpty()) return UnifiedSearchResults()

    val surahs = allSurahs.asSequence()
        .filter { surah ->
            val haystack = normalizeArabicForSearch(
                listOf(
                    surah.arabicName,
                    surah.englishName,
                    surah.englishTranslation,
                    surah.number.toString()
                ).joinToString(" ")
            )
            words.all { haystack.contains(it) }
        }
        .take(limitPerGroup)
        .map { SurahSearchHit(it) }
        .toList()

    val verses = mutableListOf<VerseSearchHit>()
    for (surah in allSurahs) {
        val ayahs = QuranRepository.getSurahVerses(context, surah.number) ?: continue
        for (ayah in ayahs) {
            val haystack = normalizeArabicForSearch(ayah.text)
            if (words.all { haystack.contains(it) }) {
                verses += VerseSearchHit(surah, ayah.number, ayah.text)
                if (verses.size >= limitPerGroup) break
            }
        }
        if (verses.size >= limitPerGroup) break
    }

    val libraryHits = DuaLibraryRepository.load(context).search(query, limitPerGroup)
    val duas = libraryHits.map { hit ->
        DuaSearchHit(chapter = hit.chapter, duaIndex = hit.duaIndex, dua = hit.dua)
    }

    return UnifiedSearchResults(surahs = surahs, verses = verses, duas = duas)
}

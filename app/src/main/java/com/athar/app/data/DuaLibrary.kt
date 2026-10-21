package com.athar.app.data

import android.content.Context
import org.json.JSONObject

/**
 * One supplication from the bundled Hisn al-Muslim library.
 * [reference] is the Arabic hadith citation from the printed book, when known.
 */
data class LibraryDua(
    val id: String,
    val arabic: String,
    val translation: String,
    val repeat: Int,
    val reference: String?
)

/**
 * One chapter (باب) of Hisn al-Muslim: 132 chapters, 267 supplications.
 */
data class LibraryChapter(
    val number: Int,
    val titleAr: String,
    val titleEn: String,
    val duas: List<LibraryDua>
) {
    val totalDuas: Int get() = duas.size
}

class DuaLibrary(
    val chapters: List<LibraryChapter>,
    val attribution: String
) {
    val totalDuas: Int by lazy { chapters.sumOf { it.duas.size } }
}

/** A hit from [search]: a chapter title match ([duaIndex] null) or a dua body match. */
data class LibrarySearchHit(
    val chapter: LibraryChapter,
    val duaIndex: Int?,
    val dua: LibraryDua?
)

private val ARABIC_DIACRITICS = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")
private val NON_ARABIC_LETTERS = Regex("[^\\u0621-\\u064A\\s]")
private val EXTRA_SPACES = Regex("\\s+")

/** Folds tashkeel, alef/ya/ta-marbuta variants and punctuation so Arabic queries match loosely. */
internal fun normalizeArabicForSearch(text: String): String {
    var s = text.replace(ARABIC_DIACRITICS, "")
    s = s.replace('\u0623', '\u0627').replace('\u0625', '\u0627').replace('\u0622', '\u0627')
        .replace('\u0649', '\u064A').replace('\u0629', '\u0647')
    s = NON_ARABIC_LETTERS.replace(s, " ")
    return EXTRA_SPACES.replace(s, " ").trim()
}

/**
 * Offline search across chapter titles and dua bodies, in Arabic and English.
 * Chapter title hits come first, then dua hits in book order.
 */
fun DuaLibrary.search(query: String, limit: Int = 60): List<LibrarySearchHit> {
    val raw = query.trim()
    if (raw.isEmpty()) return emptyList()
    val english = raw.lowercase()
    val arabic = normalizeArabicForSearch(raw)
    val hits = ArrayList<LibrarySearchHit>()
    for (chapter in chapters) {
        if (hits.size >= limit) break
        val titleAr = normalizeArabicForSearch(chapter.titleAr)
        val titleEn = chapter.titleEn.lowercase()
        val titleHit = (arabic.isNotEmpty() && titleAr.contains(arabic)) || titleEn.contains(english)
        if (titleHit) {
            hits.add(LibrarySearchHit(chapter = chapter, duaIndex = null, dua = null))
            if (hits.size >= limit) break
        }
        chapter.duas.forEachIndexed { index, dua ->
            if (hits.size >= limit) return@forEachIndexed
            val matches =
                (arabic.isNotEmpty() && normalizeArabicForSearch(dua.arabic).contains(arabic)) ||
                    dua.translation.lowercase().contains(english)
            if (matches) {
                hits.add(LibrarySearchHit(chapter = chapter, duaIndex = index, dua = dua))
            }
        }
    }
    return hits
}

/**
 * Loads the bundled, offline Hisn al-Muslim catalogue from
 * assets/duas/hisn_al_muslim.json. Parsed once and cached for the process lifetime.
 *
 * Sources: OpenDua dataset v0.0.4 (Arabic + English recitations, CC BY 4.0),
 * Islamic Pro Azkar API (Arabic chapter titles and repeat counts, MIT) and
 * HisnElMuslim (Arabic hadith references, MIT).
 */
object DuaLibraryRepository {

    private const val ASSET = "duas/hisn_al_muslim.json"

    @Volatile
    private var cached: DuaLibrary? = null

    fun load(context: Context): DuaLibrary {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: read(context.applicationContext).also { cached = it }
        }
    }

    private fun read(context: Context): DuaLibrary {
        val raw = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val attribution = root.optString("attribution", "")
        val chaptersArr = root.getJSONArray("chapters")
        val chapters = ArrayList<LibraryChapter>(chaptersArr.length())

        for (i in 0 until chaptersArr.length()) {
            val ch = chaptersArr.getJSONObject(i)
            val number = ch.optInt("number", i + 1)
            val duasArr = ch.optJSONArray("duas")
            val duas = ArrayList<LibraryDua>(duasArr?.length() ?: 0)
            if (duasArr != null) {
                for (j in 0 until duasArr.length()) {
                    val d = duasArr.getJSONObject(j)
                    duas.add(
                        LibraryDua(
                            id = "lib_${number}_${j + 1}",
                            arabic = d.optString("arabic", ""),
                            translation = d.optString("translation", ""),
                            repeat = d.optInt("repeat", 1).coerceAtLeast(1),
                            reference = d.optString("reference", "").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }
            chapters.add(
                LibraryChapter(
                    number = number,
                    titleAr = ch.optString("titleAr", ""),
                    titleEn = ch.optString("titleEn", ""),
                    duas = duas
                )
            )
        }
        chapters.sortBy { it.number }
        return DuaLibrary(chapters = chapters, attribution = attribution)
    }
}

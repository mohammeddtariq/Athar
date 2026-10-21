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

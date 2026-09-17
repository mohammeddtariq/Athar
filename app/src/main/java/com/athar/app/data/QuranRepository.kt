package com.athar.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class QuranVerse(
    val number: Int,
    val text: String
)

/**
 * Full surah text provider with offline bundling and caching.
 *
 * Text source: the Quran.com API (Quran Foundation), Madani mushaf in Uthmani
 * script — bundled offline in assets/quran/ and cached to internal storage.
 */
object QuranRepository {

    private const val ENDPOINT = "https://api.quran.com/api/v4/quran/verses/uthmani"

    private val ARABIC_INDIC = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    fun Int.toArabicIndic(): String = this.toString().map { c ->
        if (c in '0'..'9') ARABIC_INDIC[c - '0'] else c
    }.joinToString("")

    /**
     * Maps surah number (1..114) to the Unicode private-use codepoint (0xE901..0xE972)
     * for the authentic Quran Surah Names calligraphy font.
     */
    fun getSurahTitleGlyph(surahNumber: Int): String {
        val code = 0xE900 + surahNumber.coerceIn(1, 114)
        return code.toChar().toString()
    }

    private fun cacheFile(context: Context, number: Int): File =
        File(context.filesDir, "quran_ch_$number.txt")

    fun isCached(context: Context, number: Int): Boolean =
        runCatching { cacheFile(context, number).takeIf { it.exists() } != null }.getOrDefault(false)

    /**
     * Returns structured list of verses for [number], from assets first, then cache, then network.
     */
    suspend fun getSurahVerses(context: Context, number: Int): List<QuranVerse>? =
        withContext(Dispatchers.IO) {
            // 1. Bundled offline assets
            runCatching {
                context.assets.open("quran/ch$number.json").bufferedReader().use { it.readText() }
            }.getOrNull()?.let { body ->
                runCatching { parseVersesList(body) }.getOrNull()?.let { return@withContext it }
            }

            // 2. filesDir cache from a previous fetch
            val cached: String? = runCatching {
                val f = cacheFile(context, number)
                if (f.exists()) f.readText().takeIf { it.isNotBlank() } else null
            }.getOrNull()
            if (cached != null) {
                runCatching { parseVersesList(cached) }.getOrNull()?.let { return@withContext it }
            }

            // 3. Network, then persist raw json
            val freshJson: String? = runCatching { fetchChapterJson(number) }.getOrNull()
            if (freshJson != null) {
                runCatching { cacheFile(context, number).writeText(freshJson) }
                return@withContext runCatching { parseVersesList(freshJson) }.getOrNull()
            }

            null
        }

    /**
     * Returns the formatted display text for [number] with ornate ayah markers (\u06DD).
     */
    suspend fun getSurahText(context: Context, number: Int): String? =
        withContext(Dispatchers.IO) {
            val verses = getSurahVerses(context, number) ?: return@withContext null
            formatVersesText(number, verses)
        }

    fun formatVersesText(chapter: Int, verses: List<QuranVerse>): String {
        val sb = StringBuilder()
        for (i in verses.indices) {
            val v = verses[i]
            sb.append(v.text)
            // Authentic ornate Arabic end of ayah symbol (\u06DD) followed by Arabic numerals
            sb.append(" \u06DD").append(v.number.toArabicIndic())
            if (i < verses.lastIndex) sb.append(" ")
        }
        return sb.toString()
    }

    private fun fetchChapterJson(number: Int): String {
        val url = URL("$ENDPOINT?chapter_number=$number&per_page=300")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 15000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
        }
        try {
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("HTTP ${conn.responseCode}")
            }
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    private fun parseVersesList(body: String): List<QuranVerse> {
        val json = JSONObject(body)
        val verses = json.getJSONArray("verses")
        val list = ArrayList<QuranVerse>(verses.length())
        for (i in 0 until verses.length()) {
            val v = verses.getJSONObject(i)
            val num = when {
                v.has("verse_number") -> v.getInt("verse_number")
                v.has("verse_key") -> v.getString("verse_key").substringAfter(':').toIntOrNull() ?: (i + 1)
                else -> i + 1
            }
            val text = v.getString("text_uthmani").trim()
            list.add(QuranVerse(num, text))
        }
        return list
    }
}

package com.athar.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

data class QuranVerse(
    val number: Int,
    val text: String
)

data class VerseChunk(
    val chunkIndex: Int,
    val startVerse: Int,
    val endVerse: Int,
    val verses: List<QuranVerse>
)

/**
 * Full surah text provider with offline bundling and memory caching.
 *
 * Text source: the Quran.com API (Quran Foundation), Madani mushaf in Uthmani
 * script — bundled offline in assets/quran/ and cached to memory and disk.
 */
object QuranRepository {

    private const val ENDPOINT = "https://api.quran.com/api/v4/quran/verses/uthmani"

    private val ARABIC_INDIC = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    private val memoryCache = ConcurrentHashMap<Int, List<QuranVerse>>()

    fun Int.toArabicIndic(): String = this.toString().map { c ->
        if (c in '0'..'9') ARABIC_INDIC[c - '0'] else c
    }.joinToString("")

    /**
     * 100% verified mapping from Surah number (1..114) to exact Unicode Private-Use codepoint
     * in quran_surah_names.ttf. (0xE903 is the ornamental "سورة" ligature).
     */
    private val SURAH_GLYPHS = intArrayOf(
        0xE904, 0xE905, 0xE906, 0xE907, 0xE908, 0xE90B, 0xE90C, 0xE90D, 0xE90E, 0xE90F, // 1..10
        0xE910, 0xE911, 0xE912, 0xE913, 0xE914, 0xE915, 0xE916, 0xE917, 0xE918, 0xE919, // 11..20
        0xE91A, 0xE91B, 0xE91C, 0xE91D, 0xE91E, 0xE91F, 0xE920, 0xE921, 0xE922, 0xE923, // 21..30
        0xE924, 0xE925, 0xE926, 0xE92E, 0xE92F, 0xE930, 0xE931, 0xE909, 0xE90A, 0xE927, // 31..40
        0xE928, 0xE929, 0xE92A, 0xE92B, 0xE92C, 0xE92D, 0xE932, 0xE902, 0xE933, 0xE934, // 41..50
        0xE935, 0xE936, 0xE937, 0xE938, 0xE939, 0xE93A, 0xE93B, 0xE93C, 0xE900, 0xE901, // 51..60
        0xE941, 0xE942, 0xE943, 0xE944, 0xE945, 0xE946, 0xE947, 0xE948, 0xE949, 0xE94A, // 61..70
        0xE94B, 0xE94C, 0xE94D, 0xE94E, 0xE94F, 0xE950, 0xE951, 0xE952, 0xE93D, 0xE93E, // 71..80
        0xE93F, 0xE940, 0xE953, 0xE954, 0xE955, 0xE956, 0xE957, 0xE958, 0xE959, 0xE95A, // 81..90
        0xE95B, 0xE95C, 0xE95D, 0xE95E, 0xE95F, 0xE960, 0xE961, 0xE962, 0xE963, 0xE964, // 91..100
        0xE965, 0xE966, 0xE967, 0xE968, 0xE969, 0xE96A, 0xE96B, 0xE96C, 0xE96D, 0xE96E, // 101..110
        0xE96F, 0xE970, 0xE971, 0xE972                                                    // 111..114
    )

    fun getSurahTitleGlyph(surahNumber: Int): String {
        val code = if (surahNumber in 1..114) SURAH_GLYPHS[surahNumber - 1] else 0xE904
        return code.toChar().toString()
    }

    /**
     * Returns the full calligraphic title: surah name glyph + ornamental "سورة" (0xE903).
     * Because PUA characters default to LTR bidi classification, placing nameGlyph on the left
     * and 0xE903 on the right causes the shaper to position 0xE903 ("سورة") on the far right
     * and the name on the left — perfectly rendering as "سُورَةُ [اسم السورة]" in RTL Arabic!
     */
    fun getSurahFullTitleGlyphs(surahNumber: Int): String {
        val nameGlyph = getSurahTitleGlyph(surahNumber)
        return "$nameGlyph\uE903"
    }

    private fun cacheFile(context: Context, number: Int): File =
        File(context.filesDir, "quran_ch_$number.txt")

    fun isCached(context: Context, number: Int): Boolean =
        memoryCache.containsKey(number) ||
            runCatching { cacheFile(context, number).takeIf { it.exists() } != null }.getOrDefault(false)

    /**
     * Returns structured list of verses for [number], from memory first, then assets, then cache, then network.
     */
    suspend fun getSurahVerses(context: Context, number: Int): List<QuranVerse>? =
        withContext(Dispatchers.IO) {
            // 0. Fast in-memory cache
            memoryCache[number]?.let { return@withContext it }

            // 1. Bundled offline assets (100% reliable offline source with multiple classloader fallbacks)
            val assetJson = runCatching {
                val am = context.assets ?: context.applicationContext.assets
                am.open("quran/ch$number.json").bufferedReader().use { it.readText() }
            }.getOrElse {
                runCatching {
                    context.applicationContext.assets.open("quran/ch$number.json").bufferedReader().use { it.readText() }
                }.getOrElse {
                    runCatching {
                        QuranRepository::class.java.classLoader
                            ?.getResourceAsStream("assets/quran/ch$number.json")
                            ?.bufferedReader()?.use { it.readText() }
                    }.getOrNull()
                }
            }

            if (!assetJson.isNullOrBlank()) {
                val parsed = runCatching { parseVersesList(assetJson) }.getOrNull()
                if (!parsed.isNullOrEmpty()) {
                    memoryCache[number] = parsed
                    return@withContext parsed
                }
            }

            // 2. filesDir cache from a previous fetch
            val f = cacheFile(context, number)
            if (f.exists()) {
                val cached = runCatching { f.readText().takeIf { it.isNotBlank() } }.getOrNull()
                if (cached != null) {
                    val parsed = runCatching { parseVersesList(cached) }.getOrNull()
                    if (!parsed.isNullOrEmpty()) {
                        memoryCache[number] = parsed
                        return@withContext parsed
                    } else {
                        // Purge corrupted/invalid legacy cache
                        runCatching { f.delete() }
                    }
                }
            }

            // 3. Network fallback, then persist raw json
            val freshJson: String? = runCatching { fetchChapterJson(number) }.getOrNull()
            if (!freshJson.isNullOrBlank()) {
                val parsed = runCatching { parseVersesList(freshJson) }.getOrNull()
                if (!parsed.isNullOrEmpty()) {
                    runCatching { cacheFile(context, number).writeText(freshJson) }
                    memoryCache[number] = parsed
                    return@withContext parsed
                }
            }

            null
        }

    /**
     * Returns formatted text for [number] with ornate ayah markers (\u06DD).
     */
    suspend fun getSurahText(context: Context, number: Int): String? =
        withContext(Dispatchers.IO) {
            val verses = getSurahVerses(context, number) ?: return@withContext null
            formatVersesText(number, verses)
        }

    /**
     * Groups verses into manageable chunks for ultra-fast, non-blocking Compose rendering.
     * Prevents Android UI thread freeze on large Surahs like Al-Baqarah (286 ayahs).
     */
    suspend fun getSurahVerseChunks(context: Context, number: Int, chunkSize: Int = 6): List<VerseChunk>? =
        withContext(Dispatchers.IO) {
            val verses = getSurahVerses(context, number) ?: return@withContext null
            chunkVerses(verses, chunkSize)
        }

    /**
     * Loads surah verses grouped by canonical Madani Mushaf pages (1..604).
     */
    suspend fun getSurahPageChunks(context: Context, number: Int): List<QuranPageChunk>? =
        withContext(Dispatchers.IO) {
            val verses = getSurahVerses(context, number) ?: return@withContext null
            QuranPages.getSurahPageChunks(number, verses)
        }

    fun chunkVerses(verses: List<QuranVerse>, chunkSize: Int = 18): List<VerseChunk> {
        if (verses.isEmpty()) return emptyList()
        // Surahs with 25 or fewer verses (e.g. Al-Fatihah, Al-Ikhlas, etc.) stay in a single continuous chunk
        // to flow naturally like authentic printed mushaf pages without abrupt breaks.
        val effectiveSize = if (verses.size <= 25) verses.size else chunkSize
        return verses.chunked(effectiveSize).mapIndexed { idx, chunk ->
            VerseChunk(
                chunkIndex = idx,
                startVerse = chunk.first().number,
                endVerse = chunk.last().number,
                verses = chunk
            )
        }
    }

    fun formatVersesText(chapter: Int, verses: List<QuranVerse>): String {
        val sb = StringBuilder()
        for (i in verses.indices) {
            val v = verses[i]
            sb.append(v.text)
            sb.append(" (").append(v.number.toArabicIndic()).append(")")
            if (i < verses.lastIndex) sb.append(" ")
        }
        return sb.toString()
    }

    private fun fetchChapterJson(number: Int): String {
        val url = URL("$ENDPOINT?chapter_number=$number&per_page=300")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
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

package com.athar.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Full surah text provider.
 *
 * Text source: the Quran.com API (Quran Foundation), Madani mushaf in Uthmani
 * script — fetched per chapter and cached to internal storage, so each surah
 * works offline after its first load. No API key required.
 */
object QuranRepository {

    private const val ENDPOINT = "https://api.quran.com/api/v4/quran/verses/uthmani"

    private val ARABIC_INDIC = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    fun Int.toArabicIndic(): String = this.toString().map { c ->
        if (c in '0'..'9') ARABIC_INDIC[c - '0'] else c
    }.joinToString("")

    private fun cacheFile(context: Context, number: Int): File =
        File(context.filesDir, "quran_ch_$number.txt")

    fun isCached(context: Context, number: Int): Boolean =
        runCatching { cacheFile(context, number).takeIf { it.exists() } != null }.getOrDefault(false)

    /**
     * Returns the display text for [number] (verses joined with ornate ayah
     * markers), from cache first, then network. Null when offline & uncached.
     */
    suspend fun getSurahText(context: Context, number: Int): String? =
        withContext(Dispatchers.IO) {
            val cached: String? = runCatching {
                val f = cacheFile(context, number)
                if (f.exists()) f.readText().takeIf { it.isNotBlank() } else null
            }.getOrNull()
            if (cached != null) {
                return@withContext cached
            }

            val fresh: String = runCatching { fetchChapter(number) }.getOrNull()
                ?: return@withContext null

            runCatching { cacheFile(context, number).writeText(fresh) }
            fresh
        }

    private fun fetchChapter(number: Int): String {
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
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            return parseVerses(number, body)
        } finally {
            conn.disconnect()
        }
    }

    private fun parseVerses(chapter: Int, body: String): String {
        val verses = JSONObject(body).getJSONArray("verses")
        val sb = StringBuilder()
        // Real mushafs open every surah (except 1 and 9) with the Basmala.
        if (chapter != 1 && chapter != 9) {
            sb.append("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\n\n")
        }
        for (i in 0 until verses.length()) {
            val v = verses.getJSONObject(i)
            val num = v.getInt("verse_number")
            val text = v.getString("text_uthmani").trim()
            sb.append(text)
            sb.append(" ﴿").append(num.toArabicIndic()).append("﴾")
            if (i < verses.length() - 1) sb.append(" ")
        }
        return sb.toString()
    }
}

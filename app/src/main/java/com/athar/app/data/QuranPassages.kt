package com.athar.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 * A stretch of a supplication that is, word for word, a verse of the Quran.
 *
 * [start] and [end] are indices into the *original* string, so callers can
 * slice it without having to reproduce the normalisation used to find the
 * match. [text] is the canonical Uthmani wording, which is what should be shown
 * for that stretch.
 */
data class QuranPassage(
    val start: Int,
    val end: Int,
    val surah: Int,
    val ayah: Int,
    val text: String
) {
    /**
     * The opening basmala. It carries ayah number 1 only inside al-Fatihah;
     * standing at the head of a quoted surah it is not numbered at all.
     */
    val isBasmala: Boolean get() = surah == 1 && ayah == 1
}

/**
 * Finds the verses of the Quran inside a piece of prose.
 *
 * The Hisn al-Muslim library quotes ayat in the middle of ordinary supplications
 * — al-Ikhlas, al-Falaq and an-Nas after the prayer, a line of al-Fatihah in the
 * morning adhkar, and so on. Those stretches deserve to be read in the Uthmani
 * face and marked with their ayah number, but the library carries no marker
 * saying which words are Quranic.
 *
 * Matching is done on bare letters — tashkeel, alef variants, ta-marbuta,
 * punctuation and spaces are all folded away — so a quote matches regardless of
 * how the library spelled it. The index is built once from the bundled mushaf
 * and every result is cached per text.
 */
object QuranPassages {

    /** Shorter runs would match half the language; verses below this are skipped. */
    private const val MIN_LETTERS = 8

    private class Verse(
        val surah: Int,
        val ayah: Int,
        val letters: String,
        val text: String
    )

    @Volatile
    private var verses: List<Verse>? = null

    @Volatile
    private var byPrefix: Map<String, List<Int>> = emptyMap()

    private val cache = ConcurrentHashMap<String, List<QuranPassage>>()

    /** True once the mushaf index has been built. */
    val isReady: Boolean get() = verses != null

    /** Builds the index off the main thread. Safe to call repeatedly. */
    suspend fun warm(context: Context) {
        if (verses != null) return
        withContext(Dispatchers.IO) { ensureIndexed(context.applicationContext) }
    }

    /**
     * Returns the Quranic stretches of [text], in reading order and never
     * overlapping. Empty when the text is prose throughout or the index has not
     * been warmed yet.
     */
    fun find(text: String): List<QuranPassage> {
        val index = verses ?: return emptyList()
        cache[text]?.let { return it }
        val result = match(text, index)
        cache[text] = result
        return result
    }

    private fun match(text: String, index: List<Verse>): List<QuranPassage> {
        if (text.isBlank()) return emptyList()
        val (letters, map) = lettersWithMap(text)
        if (letters.length < MIN_LETTERS) return emptyList()

        val candidateIds = LinkedHashSet<Int>()
        for (i in 0..letters.length - 3) {
            byPrefix[letters.substring(i, i + 3)]?.let { candidateIds.addAll(it) }
        }
        if (candidateIds.isEmpty()) return emptyList()

        val found = ArrayList<QuranPassage>()
        for (id in candidateIds) {
            val verse = index[id]
            if (verse.letters.length > letters.length) continue
            // A supplication may quote the same verse twice — the three
            // basmalas before al-Ikhlas, al-Falaq and an-Nas, for instance —
            // so every occurrence is marked, not just the first.
            var at = letters.indexOf(verse.letters)
            while (at >= 0) {
                // The tashkeel of the last letter sits after it in the string,
                // so take it with the verse; otherwise it is left floating
                // beside the ayah marker.
                var end = map[at + verse.letters.length - 1] + 1
                while (end < text.length && isMark(text[end])) end++
                found += QuranPassage(
                    start = map[at],
                    end = end,
                    surah = verse.surah,
                    ayah = verse.ayah,
                    text = verse.text
                )
                at = letters.indexOf(verse.letters, at + 1)
            }
        }
        if (found.isEmpty()) return emptyList()

        // Longest first, so a full verse wins over a shorter verse that happens
        // to sit inside it, then keep only non-overlapping stretches.
        found.sortWith(compareBy({ it.start }, { -(it.end - it.start) }))
        val accepted = ArrayList<QuranPassage>(found.size)
        var cursor = -1
        for (passage in found) {
            if (passage.start >= cursor) {
                accepted += passage
                cursor = passage.end
            }
        }
        return accepted
    }

    private fun ensureIndexed(context: Context) {
        if (verses != null) return
        synchronized(this) {
            if (verses != null) return

            val built = ArrayList<Verse>(6300)
            val prefix = HashMap<String, MutableList<Int>>()
            for (surah in 1..114) {
                val json = runCatching {
                    context.assets.open("quran/ch$surah.json")
                        .bufferedReader().use { it.readText() }
                }.getOrNull() ?: continue
                val root = runCatching { JSONObject(json) }.getOrNull() ?: continue
                val array = root.optJSONArray("verses") ?: continue
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    val ayah = obj.optString("verse_key").substringAfter(':').toIntOrNull()
                        ?: (i + 1)
                    val raw = obj.optString("text_uthmani").ifBlank {
                        obj.optString("text")
                    }.trim()
                    if (raw.isEmpty()) continue
                    val letters = lettersOnly(raw)
                    if (letters.length < MIN_LETTERS) continue
                    val id = built.size
                    built += Verse(surah = surah, ayah = ayah, letters = letters, text = raw)
                    prefix.getOrPut(letters.substring(0, 3)) { ArrayList() }.add(id)
                }
            }
            byPrefix = prefix
            verses = built
        }
    }

    // ─── Letter folding ───

    private val DIACRITICS = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")

    /** True for a combining mark, which belongs to the letter before it. */
    private fun isMark(c: Char): Boolean =
        c in '\u0610'..'\u061A' ||
            c in '\u064B'..'\u065F' ||
            c == '\u0670' ||
            c in '\u06D6'..'\u06ED'

    /** Folds one character to its bare letter, or null when it carries no letter. */
    private fun fold(c: Char): Char? = when {
        c == '\u0640' -> null
        c in '\u0621'..'\u064A' -> when (c) {
            '\u0623', '\u0625', '\u0622' -> '\u0627'
            '\u0649' -> '\u064A'
            '\u0629' -> '\u0647'
            '\u0624' -> '\u0648'
            '\u0626' -> '\u064A'
            else -> c
        }
        c == '\u0671' -> '\u0627'
        else -> null
    }

    private fun lettersOnly(text: String): String {
        val stripped = DIACRITICS.replace(text, "")
        val sb = StringBuilder(stripped.length)
        for (c in stripped) fold(c)?.let { sb.append(it) }
        return sb.toString()
    }

    /**
     * The bare letters of [text], plus a map from each letter's position back to
     * the character it came from in [text].
     */
    private fun lettersWithMap(text: String): Pair<String, IntArray> {
        val sb = StringBuilder(text.length)
        val map = IntArray(text.length)
        var count = 0
        for (i in text.indices) {
            fold(text[i])?.let { letter ->
                sb.append(letter)
                if (count < map.size) map[count] = i
                count++
            }
        }
        return sb.toString() to map.copyOf(count)
    }
}

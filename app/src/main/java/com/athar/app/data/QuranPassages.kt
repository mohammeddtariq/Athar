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

    /**
     * How much of the spelling is folded away when looking for a verse.
     *
     * The library and the mushaf disagree on orthography, not on wording: the
     * rasm writes `ٱلسَّمَـٰوَٰتِ` where the library has `السماوات`, and
     * `يَئُودُهُ` where it has `يَؤُودُهُ`. Matching only the exact letters left
     * Ayat al-Kursi broken in two — the reader saw the opening phrase marked as
     * verse 2. Each shape is tried in turn, so the loosest one that still finds
     * the whole verse wins, and [EXACT] stays the first choice.
     */
    private enum class Shape { EXACT, BARE, SKELETON }

    private val SHAPES = listOf(Shape.EXACT, Shape.BARE, Shape.SKELETON)

    /** One folded form of a string, plus where each folded letter came from. */
    private class Scan(val text: String, val map: IntArray)

    private class Verse(
        val surah: Int,
        val ayah: Int,
        val keys: Array<String>,
        val text: String
    ) {
        /** The alef-less key, which is the one used to name a missing verse. */
        val bare: String get() = keys[Shape.BARE.ordinal]
    }

    @Volatile
    private var verses: List<Verse>? = null

    /** Every three-letter run of every key shape, pointing at its verse. */
    @Volatile
    private var byPrefix: Map<String, List<Int>> = emptyMap()

    /** Alef-less keys that belong to exactly one verse, and so to one number. */
    @Volatile
    private var byBare: Map<String, Int> = emptyMap()

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
        val scans = SHAPES.map { scan(text, it) }
        if (scans[0].text.length < MIN_LETTERS) return emptyList()

        val candidateIds = LinkedHashSet<Int>()
        for (form in scans) {
            for (i in 0..form.text.length - 3) {
                byPrefix[form.text.substring(i, i + 3)]?.let { candidateIds.addAll(it) }
            }
        }
        if (candidateIds.isEmpty()) return emptyList()

        val found = ArrayList<Found>()
        for (id in candidateIds) {
            val verse = index[id]
            val tried = HashSet<String>(3)
            for (shape in SHAPES) {
                val key = verse.keys[shape.ordinal]
                // A verse with no alefs or hamza seats has the same key in every
                // shape; asking once is enough.
                if (!tried.add(key)) continue
                addOccurrences(key, scans[shape.ordinal], verse, text, shape.ordinal, found)
            }
        }
        if (found.isEmpty()) return emptyList()

        // Longest first, then the tightest shape, so a whole verse beats the
        // opening phrase of it that happens to start a character earlier — the
        // alef folded out of the first word shifts the start by one. Every
        // stretch that overlaps one already kept is dropped.
        found.sortWith(
            compareByDescending<Found> { it.passage.end - it.passage.start }
                .thenBy { it.shape }
                .thenBy { it.passage.start }
        )
        val accepted = ArrayList<QuranPassage>(found.size)
        for (candidate in found) {
            val passage = candidate.passage
            val overlaps = accepted.any { it.start < passage.end && passage.start < it.end }
            if (!overlaps) accepted += passage
        }
        accepted.sortBy { it.start }
        return fillGaps(text, accepted)
    }

    /**
     * Adds every occurrence of [key] in [form], mapping back to the original
     * string through the form's map. Shared by all three shapes.
     */
    /** A candidate passage and how loosely it had to be matched. */
    private class Found(val passage: QuranPassage, val shape: Int)

    private fun addOccurrences(
        key: String,
        form: Scan,
        verse: Verse,
        text: String,
        shape: Int,
        found: MutableList<Found>
    ) {
        if (key.length < MIN_LETTERS || key.length > form.text.length) return
        var at = form.text.indexOf(key)
        while (at >= 0) {
            // The tashkeel of the last letter sits after it in the string,
            // so take it with the verse; otherwise it is left floating
            // beside the ayah marker.
            var end = form.map[at + key.length - 1] + 1
            while (end < text.length && isMark(text[end])) end++
            found += Found(
                passage = QuranPassage(
                    start = form.map[at],
                    end = end,
                    surah = verse.surah,
                    ayah = verse.ayah,
                    text = verse.text
                ),
                shape = shape
            )
            at = form.text.indexOf(key, at + 1)
        }
    }

    /**
     * Fills a gap that is exactly one verse, spelled with an alef the mushaf
     * leaves out — `النفاثات` against `ٱلنَّفَّـٰثَـٰتِ`.
     *
     * Only a gap between two verses already matched is considered, and only
     * when the whole gap reduces to one verse whose alef-less key is unique in
     * the mushaf. A number is never guessed: a missing marker reads as prose,
     * a wrong number would misquote the Quran.
     */
    private fun fillGaps(text: String, accepted: List<QuranPassage>): List<QuranPassage> {
        if (accepted.size < 2) return accepted
        val out = ArrayList<QuranPassage>(accepted.size + 1)
        out += accepted.first()
        for (i in 1 until accepted.size) {
            fillOne(text, accepted[i - 1].end, accepted[i].start)?.let { out += it }
            out += accepted[i]
        }
        return out
    }

    private fun fillOne(text: String, start: Int, end: Int): QuranPassage? {
        if (end <= start) return null
        val bare = scan(text.substring(start, end), Shape.BARE).text
        if (bare.length < MIN_LETTERS) return null
        val id = byBare[bare] ?: return null
        val verse = verses?.getOrNull(id) ?: return null
        return QuranPassage(
            start = start,
            end = end,
            surah = verse.surah,
            ayah = verse.ayah,
            text = verse.text
        )
    }

    private fun ensureIndexed(context: Context) {
        if (verses != null) return
        synchronized(this) {
            if (verses != null) return

            val built = ArrayList<Verse>(6300)
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
                    val keys = Array(SHAPES.size) { shape -> scan(raw, SHAPES[shape]).text }
                    if (keys[Shape.EXACT.ordinal].length < MIN_LETTERS) continue
                    built += Verse(surah = surah, ayah = ayah, keys = keys, text = raw)
                }
            }

            // Every key shape goes into one prefix index: a quote written in the
            // rasm and one written in modern spelling both find their verse.
            val prefix = HashMap<String, MutableList<Int>>()
            for (id in built.indices) {
                for (key in built[id].keys) {
                    if (key.length < MIN_LETTERS) continue
                    prefix.getOrPut(key.substring(0, 3)) { ArrayList() }.add(id)
                }
            }
            byPrefix = prefix

            // Alef-less keys are only usable when they point at a single verse,
            // so a number can never come from an ambiguous key.
            val bare = HashMap<String, Int>()
            val clashes = HashSet<String>()
            for (id in built.indices) {
                val key = built[id].bare
                if (key.length < MIN_LETTERS) continue
                if (bare.put(key, id) != null) clashes += key
            }
            for (key in clashes) bare.remove(key)
            byBare = bare

            verses = built
        }
    }

    // ─── Letter folding ───

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

    /**
     * [text] reduced to the letters of [shape], plus a map from each letter's
     * position back to the character it came from.
     */
    private fun scan(text: String, shape: Shape): Scan {
        val sb = StringBuilder(text.length)
        val map = IntArray(text.length)
        var count = 0
        for (i in text.indices) {
            val c = text[i]
            // Skeleton: the hamza and its seats are written differently by the
            // rasm — `يَئُودُهُ` against `يَؤُودُهُ` — so they are dropped here.
            if (shape == Shape.SKELETON &&
                (c == '\u0621' || c == '\u0624' || c == '\u0626')
            ) {
                continue
            }
            val letter = fold(c) ?: continue
            if (shape != Shape.EXACT && letter == '\u0627') continue
            sb.append(letter)
            if (count < map.size) map[count] = i
            count++
        }
        return Scan(sb.toString(), map.copyOf(count))
    }
}

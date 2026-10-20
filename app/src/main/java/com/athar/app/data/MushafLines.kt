package com.athar.app.data

import android.content.Context
import android.util.LruCache
import org.json.JSONObject
import java.util.Locale

/**
 * One token of a mushaf page line: a word or the ornate end marker of a verse.
 */
data class MushafToken(
    val verse: Int,
    val text: String,
    val isEndMarker: Boolean
)

/**
 * Authentically typeset line segmentation of a Madani Mushaf page (1..604).
 * [lines] is indexed by slot: lines[0] == mushaf line 1 ... lines[14] == mushaf line 15.
 * [suwar] lists the surah numbers whose banner starts on this page, in slot order.
 */
class MushafPageLines(
    val page: Int,
    val lines: List<List<MushafToken>>,
    val suwar: List<Int> = emptyList()
)

/**
 * Loads the bundled per-word, per-line mushaf layout data from assets/mushaf_lines/.
 * The data was generated from the Quran.com madani mushaf model, whose line
 * segmentation mirrors the official KFGQPC Madinah mushaf typesetting.
 */
object MushafLineRepository {

    private const val MAX_PAGE = 604

    private val cache = LruCache<Int, MushafPageLines?>(28)

    fun load(context: Context, page: Int): MushafPageLines? {
        if (page !in 1..MAX_PAGE) return null
        cache.get(page)?.let { return it }

        val parsed = runCatching {
            val ctx = context.applicationContext
            val fileName = String.format(Locale.US, "mushaf_lines/p%03d.json", page)
            val raw = ctx.assets.open(fileName).bufferedReader().use { it.readText() }
            val json = JSONObject(raw)
            val slotArr = json.getJSONArray("l")
            val lines = ArrayList<List<MushafToken>>(slotArr.length())
            for (i in 0 until slotArr.length()) {
                val tokensArr = slotArr.getJSONArray(i)
                val tokens = ArrayList<MushafToken>(tokensArr.length())
                for (j in 0 until tokensArr.length()) {
                    val entry = tokensArr.getJSONArray(j)
                    val text = entry.getString(1)
                    tokens.add(
                        MushafToken(
                            verse = entry.getInt(0),
                            text = text,
                            isEndMarker = text == "*"
                        )
                    )
                }
                lines.add(tokens)
            }
            val suwar = ArrayList<Int>()
            json.optJSONArray("sw")?.let { swArr ->
                for (i in 0 until swArr.length()) {
                    suwar.add(swArr.getInt(i))
                }
            }
            MushafPageLines(page, lines, suwar)
        }.getOrNull()

        cache.put(page, parsed)
        return parsed
    }
}

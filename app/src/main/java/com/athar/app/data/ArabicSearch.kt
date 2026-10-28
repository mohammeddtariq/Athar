package com.athar.app.data

// ══════════════════════════════════════════════════════════════
// ATHAR — Search folding
//
// Arabic is typed without tashkeel, with a different hamza, with a
// ta-marbuta where the text has a ha, and with the occasional slip.
// Everything here exists so a reader can type what they remember and
// still find the ayah.
// ══════════════════════════════════════════════════════════════

private val ARABIC_MARKS = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")
private val NON_ARABIC = Regex("[^\\u0621-\\u064A\\s]")
private val EXTRA_SPACES = Regex("\\s+")

/**
 * Folds a string to the letters people actually type.
 *
 * Tashkeel, Quranic marks and tatweel are dropped; alef wasla, the alef
 * variants, alef maqsura, ta-marbuta, hamza carriers and the Persian letters
 * are all brought onto one form.
 */
internal fun foldForSearch(text: String): String {
    val stripped = ARABIC_MARKS.replace(text, "")
    val folded = StringBuilder(stripped.length)
    for (c in stripped) {
        folded.append(
            when (c) {
                '\u0623', '\u0625', '\u0622', '\u0671' -> '\u0627'
                '\u0649', '\u0626', '\u06CC' -> '\u064A'
                '\u0629' -> '\u0647'
                '\u0624' -> '\u0648'
                '\u06A9' -> '\u0643'
                else -> c
            }
        )
    }
    return EXTRA_SPACES.replace(NON_ARABIC.replace(folded, " "), " ").trim()
}

/** The folded words of [text], ready to compare. */
internal fun searchWords(text: String): List<String> =
    foldForSearch(text).split(' ').filter { it.isNotEmpty() }

/**
 * How well one typed word matches one word of a text, from 0 to 1.
 *
 * Exact containment is a perfect match, a prefix is nearly perfect, and beyond
 * that the words are compared with a small edit budget that grows with the
 * length of what was typed — one slip on a short word, two on a long one.
 */
internal fun wordScore(query: String, candidate: String): Float {
    if (query.isEmpty() || candidate.isEmpty()) return 0f
    if (candidate.contains(query)) return 1f
    if (candidate.length >= 3 && query.contains(candidate)) {
        return 0.82f * candidate.length.toFloat() / query.length
    }

    val budget = when (query.length) {
        in 0..2 -> 0
        in 3..4 -> 1
        in 5..7 -> 2
        else -> 3
    }
    if (budget == 0) return 0f
    if (kotlin.math.abs(query.length - candidate.length) > budget) return 0f

    val distance = editDistance(query, candidate, budget)
    if (distance > budget) return 0f
    return (1f - distance * 0.18f).coerceAtLeast(0.35f)
}

/**
 * How well a whole query matches a passage, from 0 to 1.
 *
 * Each typed word is scored against its best partner in the passage and the
 * scores are averaged, so a few right words with one slip still surface the
 * passage instead of excluding it.
 */
internal fun passageScore(queryWords: List<String>, candidateWords: List<String>): Float {
    if (queryWords.isEmpty() || candidateWords.isEmpty()) return 0f
    var total = 0f
    for (query in queryWords) {
        var best = 0f
        for (candidate in candidateWords) {
            val score = wordScore(query, candidate)
            if (score > best) best = score
            if (best >= 1f) break
        }
        total += best
    }
    return total / queryWords.size
}

/** Levenshtein distance, abandoned as soon as it exceeds [limit]. */
internal fun editDistance(a: String, b: String, limit: Int): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length
    if (kotlin.math.abs(a.length - b.length) > limit) return limit + 1

    var previous = IntArray(b.length + 1) { it }
    var current = IntArray(b.length + 1)
    for (i in 1..a.length) {
        current[0] = i
        var rowMin = current[0]
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            current[j] = minOf(
                current[j - 1] + 1,
                previous[j] + 1,
                previous[j - 1] + cost
            )
            if (current[j] < rowMin) rowMin = current[j]
        }
        if (rowMin > limit) return limit + 1
        val swap = previous
        previous = current
        current = swap
    }
    return previous[b.length]
}

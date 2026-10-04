package com.athar.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

data class VerseWordTiming(
    val wordIndex: Int,
    val startMs: Long,
    val endMs: Long
)

data class VerseTiming(
    val verseKey: String,
    val verseNumber: Int,
    val startMs: Long,
    val endMs: Long,
    val segments: List<VerseWordTiming>
)

data class ChapterRecitationTiming(
    val chapterId: Int,
    val reciterId: Int,
    val verses: List<VerseTiming>,
    val audioUrl: String? = null
) {
    /**
     * Finds the verse currently active at [positionMs].
     */
    fun findActiveVerse(positionMs: Long): VerseTiming? {
        if (verses.isEmpty()) return null
        if (positionMs < verses.first().startMs) return null
        return verses.firstOrNull { positionMs in it.startMs..it.endMs }
            ?: verses.lastOrNull { positionMs >= it.endMs }?.takeIf { positionMs <= it.endMs + 3000L }
    }

    /**
     * Finds the 1-based word index active at [positionMs] within [verse].
     */
    fun findActiveWordIndex(verse: VerseTiming, positionMs: Long): Int? {
        if (verse.segments.isEmpty()) return null

        val firstSeg = verse.segments.first()
        // If position is before the first word of this verse begins, do not highlight any word
        if (positionMs < firstSeg.startMs) return null

        // 1. Direct hit inside word segment [startMs .. endMs)
        val direct = verse.segments.firstOrNull { positionMs >= it.startMs && positionMs < it.endMs }
        if (direct != null) return direct.wordIndex

        // If at the exact end boundary of the last segment in this verse
        val lastSeg = verse.segments.last()
        if (positionMs in lastSeg.startMs..lastSeg.endMs) return lastSeg.wordIndex

        // 2. If position is after the very last word of this verse by >150ms (verse end breath/pause)
        if (positionMs > lastSeg.endMs + 150L) {
            return null
        }

        // 3. In between word N and word N+1 (inter-word gap/pause):
        // Carry the highlight on the finished word until the next word begins.
        // This eliminates highlight flicker and perceived delay between words.
        val lastFinished = verse.segments.lastOrNull { positionMs >= it.endMs }
        if (lastFinished != null) {
            val nextSeg = verse.segments.firstOrNull { it.startMs > lastFinished.endMs }
            if (nextSeg != null && positionMs < nextSeg.startMs) {
                return lastFinished.wordIndex
            }
        }

        return null
    }
}

object QuranRecitationSyncRepository {

    private val memoryCache = ConcurrentHashMap<String, ChapterRecitationTiming>()

    suspend fun getChapterTiming(
        context: Context,
        reciter: QuranReciter,
        chapter: Int
    ): ChapterRecitationTiming? = withContext(Dispatchers.IO) {
        val cacheKey = "${reciter.id}_$chapter"
        memoryCache[cacheKey]?.let { return@withContext it }

        val dir = File(context.filesDir, "quran_timing").apply { if (!exists()) mkdirs() }
        val file = File(dir, "timing_${reciter.id}_$chapter.json")

        if (file.exists()) {
            val json = runCatching { file.readText() }.getOrNull()
            if (!json.isNullOrBlank()) {
                val parsed = parseTimingJson(chapter, reciter.quranComId, json)
                if (parsed != null && parsed.verses.isNotEmpty()) {
                    memoryCache[cacheKey] = parsed
                    return@withContext parsed
                }
            }
        }

        // Fetch from Quran.com API with query parameters
        val fresh = fetchTimingFromApi(reciter.quranComId, chapter)
        if (fresh != null) {
            val parsed = parseTimingJson(chapter, reciter.quranComId, fresh)
            if (parsed != null && parsed.verses.isNotEmpty()) {
                runCatching { file.writeText(fresh) }
                memoryCache[cacheKey] = parsed
                return@withContext parsed
            }
        }

        null
    }

    /**
     * Fallback for completely offline usage where timings have not been cached yet.
     * Divides [totalDurationMs] proportionally across verses and words.
     */
    fun synthesizeOfflineTiming(
        chapter: Int,
        reciterId: Int,
        verses: List<QuranVerse>,
        totalDurationMs: Long
    ): ChapterRecitationTiming {
        if (verses.isEmpty() || totalDurationMs <= 0L) {
            return ChapterRecitationTiming(chapter, reciterId, emptyList())
        }

        val wordCounts = verses.map { v ->
            v.text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size.coerceAtLeast(1)
        }
        val totalWords = wordCounts.sum().coerceAtLeast(1)

        var currentOffset = 0L
        val list = ArrayList<VerseTiming>(verses.size)

        for (i in verses.indices) {
            val v = verses[i]
            val wc = wordCounts[i]
            val verseDuration = ((wc.toDouble() / totalWords) * totalDurationMs).toLong().coerceAtLeast(1000L)
            val vStart = currentOffset
            val vEnd = (vStart + verseDuration).coerceAtMost(totalDurationMs)
            currentOffset = vEnd

            val segs = ArrayList<VerseWordTiming>(wc)
            val wordDuration = (verseDuration / wc).coerceAtLeast(200L)
            for (w in 1..wc) {
                val wStart = vStart + (w - 1) * wordDuration
                val wEnd = if (w == wc) vEnd else (wStart + wordDuration)
                segs.add(VerseWordTiming(w, wStart, wEnd))
            }

            list.add(
                VerseTiming(
                    verseKey = "$chapter:${v.number}",
                    verseNumber = v.number,
                    startMs = vStart,
                    endMs = vEnd,
                    segments = segs
                )
            )
        }

        return ChapterRecitationTiming(chapter, reciterId, list)
    }

    private fun fetchTimingFromApi(reciterId: Int, chapter: Int): String? {
        val endpoint = "https://api.quran.com/api/v4/chapter_recitations/$reciterId/$chapter?segments=true"
        return runCatching {
            val url = URI.create(endpoint).toURL()
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "AtharApp/1.0.5")
            }
            try {
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    null
                }
            } finally {
                conn.disconnect()
            }
        }.getOrNull()
    }

    private fun parseTimingJson(chapter: Int, reciterId: Int, jsonStr: String): ChapterRecitationTiming? {
        return runCatching {
            val root = JSONObject(jsonStr)
            val audioFile = root.optJSONObject("audio_file") ?: return null
            val audioUrl = audioFile.optString("audio_url").ifBlank { null }
            val timestamps = audioFile.optJSONArray("timestamps") ?: return null

            val verses = ArrayList<VerseTiming>(timestamps.length())
            for (i in 0 until timestamps.length()) {
                val obj = timestamps.getJSONObject(i)
                val key = obj.optString("verse_key", "$chapter:${i + 1}")
                val verseNum = key.substringAfter(':').toIntOrNull() ?: (i + 1)
                val from = obj.optLong("timestamp_from", 0L)
                val to = obj.optLong("timestamp_to", from + 3000L)

                val segmentsArr = obj.optJSONArray("segments")
                val segments = ArrayList<VerseWordTiming>()
                if (segmentsArr != null) {
                    for (j in 0 until segmentsArr.length()) {
                        val seg = segmentsArr.optJSONArray(j) ?: continue
                        if (seg.length() < 3) continue // Skip malformed/truncated arrays like [1]
                        val wordIdx = seg.optInt(0, -1)
                        val start = seg.optDouble(1, -1.0).toLong()
                        val end = seg.optDouble(2, -1.0).toLong()
                        if (wordIdx > 0 && start >= 0 && end > start) {
                            segments.add(VerseWordTiming(wordIdx, start, end))
                        }
                    }
                }

                verses.add(
                    VerseTiming(
                        verseKey = key,
                        verseNumber = verseNum,
                        startMs = from,
                        endMs = to,
                        segments = segments
                    )
                )
            }

            ChapterRecitationTiming(chapter, reciterId, verses, audioUrl)
        }.getOrNull()
    }
}

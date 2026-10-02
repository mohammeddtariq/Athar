package com.athar.app.data

import android.content.Context
import com.athar.app.ui.corner.allSurahs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

enum class TafsirEdition(
    val id: String,
    val arabicName: String,
    val englishName: String,
    val bookTitleArabic: String,
    val bookTitleEnglish: String,
    val authorArabic: String,
    val authorEnglish: String,
    val slug: String,
    val englishSlug: String,
    val descriptionArabic: String,
    val descriptionEnglish: String,
    val quranComId: Int,
    val quranComEnId: Int?
) {
    SAADI(
        id = "saadi",
        arabicName = "تفسير السعدي",
        englishName = "Tafsir Al-Sa'di",
        bookTitleArabic = "تيسير الكريم الرحمن في تفسير كلام المنان",
        bookTitleEnglish = "Taysir al-Karim ar-Rahman",
        authorArabic = "الشيخ عبد الرحمن بن ناصر السعدي",
        authorEnglish = "Sheikh Abdur-Rahman as-Sa'di",
        slug = "ar-tafsir-as-saadi",
        englishSlug = "en-tazkirul-quran",
        descriptionArabic = "من التفاسير المعاصرة الميسرة، ويتميز بأسلوبه السهل الواضح البعيد عن التعقيد، وهو مناسب جداً للمبتدئين.",
        descriptionEnglish = "A contemporary, accessible commentary known for its clarity and straightforward spiritual style.",
        quranComId = 91,
        quranComEnId = 817
    ),
    IBN_KATHIR(
        id = "ibn_kathir",
        arabicName = "تفسير ابن كثير",
        englishName = "Tafsir Ibn Kathir",
        bookTitleArabic = "تفسير القرآن العظيم",
        bookTitleEnglish = "Tafsir al-Qur'an al-'Azim",
        authorArabic = "الحافظ عماد الدين إسماعيل بن كثير",
        authorEnglish = "Hafiz Ibn Kathir",
        slug = "ar-tafsir-ibn-kathir",
        englishSlug = "en-tafisr-ibn-kathir",
        descriptionArabic = "أشهرها وأعظمها عناية بتفسير الآيات بالقرآن، والأحاديث النبوية، وآثار السلف.",
        descriptionEnglish = "The most celebrated traditional commentary, explaining the Quran by Quran, authentic Hadiths, and Salaf narrations.",
        quranComId = 14,
        quranComEnId = 169
    ),
    TABARI(
        id = "tabari",
        arabicName = "تفسير الطبري",
        englishName = "Tafsir Al-Tabari",
        bookTitleArabic = "جامع البيان في تأويل القرآن",
        bookTitleEnglish = "Jami' al-Bayan fi Ta'wil al-Qur'an",
        authorArabic = "الإمام محمد بن جرير الطبري",
        authorEnglish = "Imam Muhammad ibn Jarir al-Tabari",
        slug = "ar-tafsir-al-tabari",
        englishSlug = "en-tafsir-maarif-ul-quran",
        descriptionArabic = "من أقدم وأهم كتب التفسير بالمأثور، ويعتمد على نقل أقوال الصحابة والتابعين والأسانيد.",
        descriptionEnglish = "One of the earliest and most authoritative commentaries, based on Sahaba and Tabi'in narrations with full chains of transmission.",
        quranComId = 15,
        quranComEnId = 168
    ),
    QURTUBI(
        id = "qurtubi",
        arabicName = "تفسير القرطبي",
        englishName = "Tafsir Al-Qurtubi",
        bookTitleArabic = "الجامع لأحكام القرآن",
        bookTitleEnglish = "Al-Jami' li-Ahkam al-Qur'an",
        authorArabic = "الإمام أبو عبد الله محمد بن أحمد القرطبي",
        authorEnglish = "Imam Al-Qurtubi",
        slug = "ar-tafseer-al-qurtubi",
        englishSlug = "en-tafsir-maarif-ul-quran",
        descriptionArabic = "يركز بشكل أساسي على الأحكام الفقهية واستنباطها من الآيات مع العناية باللغة والإعراب.",
        descriptionEnglish = "Focuses predominantly on legal rulings (Ahkam), jurisprudence derivation, Arabic linguistics, and grammar.",
        quranComId = 90,
        quranComEnId = 168
    ),
    MUYASSAR(
        id = "muyassar",
        arabicName = "التفسير الميسر",
        englishName = "Al-Tafsir Al-Muyassar",
        bookTitleArabic = "التفسير الميسر",
        bookTitleEnglish = "Al-Tafsir Al-Muyassar",
        authorArabic = "نخبة من العلماء بإشراف مجمع الملك فهد",
        authorEnglish = "King Fahd Quran Complex",
        slug = "ar-tafsir-muyassar",
        englishSlug = "en-al-jalalayn",
        descriptionArabic = "تفسير وجيز ميسر للآيات صادر عن مجمع الملك فهد، صِيغ بعبارات واضحة وسهلة في متناول الجميع.",
        descriptionEnglish = "A concise, accessible commentary published by the King Fahd Complex with clear, simplified phrasing.",
        quranComId = 16,
        quranComEnId = null
    )
}

data class AyahTafsir(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val verseTextArabic: String,
    val arabicTafsir: String,
    val englishTafsir: String,
    val englishTranslation: String,
    val englishTransliteration: String = "",
    val edition: TafsirEdition
)


object TafsirRepository {

    private val memoryCache = ConcurrentHashMap<String, AyahTafsir>()
    private var seedFatihahJson: JSONObject? = null

    /**
     * Cleans raw HTML/markdown markup into clean, readable text.
     */
    fun cleanTafsirText(text: String): String {
        return text
            .replace(Regex("(?i)<br\\s*/?>"), "\n")
            .replace(Regex("(?i)</p>"), "\n\n")
            .replace(Regex("(?i)</div>"), "\n")
            .replace(Regex("(?i)</h[1-6]>"), "\n\n")
            .replace(Regex("<[^>]*>"), "")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&#39;", "'")
            .replace("&#x27;", "'")
            .replace("&nbsp;", " ")
            .replace("[[", "«")
            .replace("]]", "»")
            .replace(Regex("\\*\\s*\\*\\s*\\*?"), "")
            .replace(Regex("#{1,6}\\s*"), "")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()
    }

    /**
     * Isolates the commentary specifically for [ayahNumber] from multi-verse commentaries (e.g. {1} ... {2} ...).
     */
    fun extractSingleAyahTafsir(rawText: String, ayahNumber: Int): String {
        if (rawText.isBlank()) return rawText

        var cleaned = rawText
        // If ayah > 1, strip general Surah virtue headers and intros that shouldn't appear on subsequent ayahs
        if (ayahNumber > 1) {
            val introPointers = listOf(
                "What has been mentioned about the Virtues of this Surah",
                "The Virtues of Surat",
                "Which was revealed in",
                "ذِكْرُ مَا وَرَدَ فِي فَضْلِهَا",
                "ذكر ما ورد في فضلها",
                "وَهِيَ مَكِّيَّةٌ فِي قَوْلِ جَمِيعِ الْمُفَسِّرِينَ",
                "وهي مكية في قول جميع المفسرين"
            )
            for (ptr in introPointers) {
                val idx = cleaned.indexOf(ptr)
                if (idx != -1) {
                    val nextHeading = Regex("""(?:\n|^)\s*(?:[A-Z][a-zA-Z\s]{4,}:|\([0-9٠-٩]+\)|\{[0-9٠-٩]+\}|القول في تأويل|وقوله|\(\d+\.|\(\d+\))""")
                    val match = nextHeading.find(cleaned, startIndex = idx + ptr.length)
                    if (match != null) {
                        cleaned = cleaned.substring(match.range.first).trim()
                    }
                }
            }
        }

        fun normalizeDigit(ch: Char): Char = when (ch) {
            '٠' -> '0'; '١' -> '1'; '٢' -> '2'; '٣' -> '3'; '٤' -> '4'
            '٥' -> '5'; '٦' -> '6'; '٧' -> '7'; '٨' -> '8'; '٩' -> '9'
            else -> ch
        }

        fun parseNum(str: String): Int? {
            val normalized = str.map { normalizeDigit(it) }.joinToString("")
            return normalized.toIntOrNull()
        }

        // Match occurrences of {N}, [N], (N), or (N. ) at line start or paragraph start
        val markerRegex = Regex("""(?:\n|^)\s*(?:\{|\[|\()([0-9٠-٩]+)(?:\.|\}|\/|\))""")
        val allMarkers = markerRegex.findAll(cleaned).toList()

        if (allMarkers.size > 1) {
            val targetIdx = allMarkers.indexOfFirst { match ->
                parseNum(match.groupValues[1]) == ayahNumber
            }

            if (targetIdx != -1) {
                val targetMarker = allMarkers[targetIdx]
                val startPos = targetMarker.range.first
                val endPos = if (targetIdx + 1 < allMarkers.size) {
                    allMarkers[targetIdx + 1].range.first
                } else {
                    cleaned.length
                }
                val extracted = cleaned.substring(startPos, endPos).trim()
                if (extracted.isNotBlank()) return extracted
            }
        }

        // For Ayah 1 in multi-section texts, check if there's a specific section header
        if (ayahNumber == 1) {
            val discIdx = cleaned.indexOf("The Discussion of the Individual Letters")
            if (discIdx != -1) {
                val nextMarker = markerRegex.find(cleaned, startIndex = discIdx)
                val endPos = nextMarker?.range?.first ?: cleaned.length
                val extracted = cleaned.substring(discIdx, endPos).trim()
                if (extracted.isNotBlank()) return extracted
            }
        }

        return cleaned
    }

    /**
     * Loads the bundled offline seed JSON for Surah Al-Fatihah.
     */
    private fun getSeedFatihah(context: Context): JSONObject? {
        if (seedFatihahJson != null) return seedFatihahJson
        return try {
            val am = context.assets ?: context.applicationContext.assets
            val jsonStr = am.open("tafsir/seed_fatihah.json").bufferedReader().use { it.readText() }
            JSONObject(jsonStr).also { seedFatihahJson = it }
        } catch (_: Exception) {
            null
        }
    }

    private fun cacheDir(context: Context): File {
        // Automatically cleanup legacy corrupted v1 cache
        runCatching {
            val legacy = File(context.filesDir, "tafsir_cache")
            if (legacy.exists()) legacy.deleteRecursively()
        }
        return File(context.filesDir, "tafsir_cache_v3").apply { mkdirs() }
    }

    private fun cacheFile(context: Context, edition: TafsirEdition, surah: Int, ayah: Int): File {
        return File(cacheDir(context), "${edition.id}_${surah}_${ayah}.json")
    }

    /**
     * Retrieves the Tafsir commentary and translation for a specific [surah] and [ayah].
     * Order of resolution:
     * 1. In-memory cache
     * 2. Bundled offline seed data (for Surah 1)
     * 3. On-disk persistent cache
     * 4. Multi-tier network fetch (CDN -> Raw Git -> Quran.com API)
     */
    suspend fun getAyahTafsir(
        context: Context,
        surah: Int,
        ayah: Int,
        edition: TafsirEdition
    ): Result<AyahTafsir> = withContext(Dispatchers.IO) {
        val cacheKey = "${edition.id}_${surah}_$ayah"

        // 1. In-memory cache
        memoryCache[cacheKey]?.let { return@withContext Result.success(it) }

        // Find verse text from Quran repository
        val surahMeta = allSurahs.firstOrNull { it.number == surah }
        val surahVerses = QuranRepository.getSurahVerses(context, surah)
        val verseText = surahVerses?.firstOrNull { it.number == ayah }?.text ?: ""
        val surahAr = surahMeta?.arabicName ?: "الفاتحة"
        val surahEn = surahMeta?.englishName ?: "Al-Fatihah"

        // 2. Bundled offline seed for Surah Al-Fatihah (Surah 1)
        if (surah == 1) {
            val seed = getSeedFatihah(context)
            if (seed != null) {
                try {
                    val editionKey = when (edition) {
                        TafsirEdition.SAADI -> "saadi"
                        TafsirEdition.IBN_KATHIR -> "ibn_kathir"
                        TafsirEdition.TABARI -> "tabari"
                        TafsirEdition.QURTUBI -> "qurtubi"
                        TafsirEdition.MUYASSAR -> "saadi"
                    }
                    val edList = seed.optJSONArray(editionKey)
                    val enList = seed.optJSONArray("en_ibn_kathir")
                    val transObj = seed.optJSONObject("en_translation")
                    val translitObj = seed.optJSONObject("en_transliteration")

                    var arTafsirText: String? = null
                    if (edList != null) {
                        for (i in 0 until edList.length()) {
                            val item = edList.getJSONObject(i)
                            if (item.optInt("ayah") == ayah) {
                                arTafsirText = item.optString("text")
                                break
                            }
                        }
                    }

                    var enTafsirText: String? = null
                    if (enList != null) {
                        for (i in 0 until enList.length()) {
                            val item = enList.getJSONObject(i)
                            if (item.optInt("ayah") == ayah) {
                                enTafsirText = item.optString("text")
                                break
                            }
                        }
                    }

                    val enTranslation = transObj?.optString(ayah.toString(), "") ?: ""
                    val enTransliteration = translitObj?.optString(ayah.toString(), "") ?: ""

                    if (!arTafsirText.isNullOrEmpty()) {
                        val result = AyahTafsir(
                            surahNumber = surah,
                            ayahNumber = ayah,
                            surahNameArabic = surahAr,
                            surahNameEnglish = surahEn,
                            verseTextArabic = verseText,
                            arabicTafsir = cleanTafsirText(arTafsirText),
                            englishTafsir = cleanTafsirText(enTafsirText ?: enTranslation),
                            englishTranslation = cleanTafsirText(enTranslation),
                            englishTransliteration = enTransliteration,
                            edition = edition
                        )
                        memoryCache[cacheKey] = result
                        return@withContext Result.success(result)
                    }
                } catch (_: Exception) {}
            }
        }

        // 3. On-disk persistent cache
        val diskFile = cacheFile(context, edition, surah, ayah)
        if (diskFile.exists() && diskFile.length() > 0) {
            try {
                val json = JSONObject(diskFile.readText())
                val result = AyahTafsir(
                    surahNumber = json.getInt("surah"),
                    ayahNumber = json.getInt("ayah"),
                    surahNameArabic = json.optString("surah_ar", surahAr),
                    surahNameEnglish = json.optString("surah_en", surahEn),
                    verseTextArabic = json.optString("verse_text", verseText),
                    arabicTafsir = extractSingleAyahTafsir(json.getString("ar_tafsir"), ayah),
                    englishTafsir = extractSingleAyahTafsir(json.optString("en_tafsir", ""), ayah),
                    englishTranslation = json.optString("en_trans", ""),
                    englishTransliteration = json.optString("en_transliteration", ""),
                    edition = edition
                )
                memoryCache[cacheKey] = result
                return@withContext Result.success(result)
            } catch (_: Exception) {
                diskFile.delete()
            }
        }

        // 4. Multi-tier network fetch
        try {
            // 1. Fetch Arabic Tafsir: Primary is Quran.com API (curated, clean per-ayah!)
            var arText: String? = null
            val quranComUrl = "https://api.quran.com/api/v4/tafsirs/${edition.quranComId}/by_ayah/$surah:$ayah"
            arText = fetchQuranComTafsir(quranComUrl)

            // Tier B: jsDelivr CDN fallback
            if (arText.isNullOrEmpty()) {
                val cdnUrl = "https://cdn.jsdelivr.net/gh/spa5k/tafsir_api@main/tafsir/${edition.slug}/$surah/$ayah.json"
                arText = fetchTextFromUrl(cdnUrl)
            }

            // Tier C: GitHub Raw fallback
            if (arText.isNullOrEmpty()) {
                val gitUrl = "https://raw.githubusercontent.com/spa5k/tafsir_api/main/tafsir/${edition.slug}/$surah/$ayah.json"
                arText = fetchTextFromUrl(gitUrl)
            }

            if (arText.isNullOrEmpty()) {
                return@withContext Result.failure(IllegalStateException("Tafsir not found"))
            }

            // 2. Fetch English Tafsir matching selected edition
            var enText: String? = null
            if (edition.quranComEnId != null) {
                val quranComEnUrl = "https://api.quran.com/api/v4/tafsirs/${edition.quranComEnId}/by_ayah/$surah:$ayah"
                enText = fetchQuranComTafsir(quranComEnUrl)
            }
            if (enText.isNullOrEmpty()) {
                val enSlug = edition.englishSlug
                enText = fetchTextFromUrl("https://cdn.jsdelivr.net/gh/spa5k/tafsir_api@main/tafsir/$enSlug/$surah/$ayah.json")
                    ?: fetchTextFromUrl("https://raw.githubusercontent.com/spa5k/tafsir_api/main/tafsir/$enSlug/$surah/$ayah.json")
            }
            if (enText.isNullOrEmpty()) {
                enText = fetchTextFromUrl("https://cdn.jsdelivr.net/gh/spa5k/tafsir_api@main/tafsir/en-al-jalalayn/$surah/$ayah.json")
            }

            // Fetch English Translation & Transliteration (Pronunciation in English letters)
            var verseDetails = fetchQuranComVerseDetails(surah, ayah)
            var enTrans = verseDetails.translation
            if (enTrans.isBlank()) {
                enTrans = fetchQuranComTranslation("https://api.quran.com/api/v4/verses/by_key/$surah:$ayah?translations=85")
                    ?: fetchQuranComTranslation("https://api.quran.com/api/v4/verses/by_key/$surah:$ayah?translations=131")
                    ?: ""
            }

            val cleanedAr = extractSingleAyahTafsir(cleanTafsirText(arText), ayah)
            val cleanedEn = extractSingleAyahTafsir(cleanTafsirText(enText ?: enTrans), ayah)
            val cleanedTrans = cleanTafsirText(enTrans)
            val translit = verseDetails.transliteration

            val tafsirObj = AyahTafsir(
                surahNumber = surah,
                ayahNumber = ayah,
                surahNameArabic = surahAr,
                surahNameEnglish = surahEn,
                verseTextArabic = verseText,
                arabicTafsir = cleanedAr,
                englishTafsir = if (cleanedEn.isNotBlank()) cleanedEn else cleanedTrans,
                englishTranslation = cleanedTrans,
                englishTransliteration = translit,
                edition = edition
            )

            // Save to disk
            runCatching {
                val outJson = JSONObject().apply {
                    put("surah", surah)
                    put("ayah", ayah)
                    put("surah_ar", surahAr)
                    put("surah_en", surahEn)
                    put("verse_text", verseText)
                    put("ar_tafsir", cleanedAr)
                    put("en_tafsir", cleanedEn)
                    put("en_trans", cleanedTrans)
                    put("en_transliteration", translit)
                    put("edition", edition.id)
                }
                diskFile.writeText(outJson.toString())
            }

            memoryCache[cacheKey] = tafsirObj
            Result.success(tafsirObj)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun fetchTextFromUrl(urlString: String): String? {
        return try {
            val conn = URI.create(urlString).toURL().openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Athar-Android")
                connectTimeout = 7000
                readTimeout = 7000
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return null
            val content = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(content)
            json.optString("text").ifBlank { null }
        } catch (_: Exception) {
            null
        }
    }

    private fun fetchQuranComTafsir(urlString: String): String? {
        return try {
            val conn = URI.create(urlString).toURL().openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Athar-Android")
                connectTimeout = 7000
                readTimeout = 7000
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return null
            val content = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(content)
            val tafsirObj = json.optJSONObject("tafsir")
            tafsirObj?.optString("text")?.ifBlank { null }
        } catch (_: Exception) {
            null
        }
    }

    private data class QuranVerseDetails(
        val translation: String,
        val transliteration: String
    )

    private fun fetchQuranComVerseDetails(surah: Int, ayah: Int): QuranVerseDetails {
        val urlString = "https://api.quran.com/api/v4/verses/by_key/$surah:$ayah?words=true&translations=85"
        return try {
            val conn = URI.create(urlString).toURL().openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Athar-Android")
                connectTimeout = 7000
                readTimeout = 7000
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return QuranVerseDetails("", "")
            val content = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(content)
            val verse = json.optJSONObject("verse")

            // Translation (M.A.S. Abdel Haleem, id 85)
            val transArray = verse?.optJSONArray("translations")
            val translation = transArray?.optJSONObject(0)?.optString("text")?.ifBlank { "" } ?: ""

            // Transliteration word-by-word
            val words = verse?.optJSONArray("words")
            val transliterationWords = mutableListOf<String>()
            if (words != null) {
                for (i in 0 until words.length()) {
                    val wordObj = words.optJSONObject(i) ?: continue
                    val charType = wordObj.optString("char_type_name")
                    if (charType == "word") {
                        val transObj = wordObj.optJSONObject("transliteration")
                        val text = transObj?.optString("text")?.trim().orEmpty()
                        if (text.isNotEmpty()) {
                            transliterationWords.add(text)
                        }
                    }
                }
            }
            val transliteration = transliterationWords.joinToString(" ")
            QuranVerseDetails(translation, transliteration)
        } catch (_: Exception) {
            QuranVerseDetails("", "")
        }
    }

    private fun fetchQuranComTranslation(urlString: String): String? {
        return try {
            val conn = URI.create(urlString).toURL().openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Athar-Android")
                connectTimeout = 6000
                readTimeout = 6000
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return null
            val content = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(content)
            val verse = json.optJSONObject("verse")
            val transArray = verse?.optJSONArray("translations")
            transArray?.optJSONObject(0)?.optString("text")?.ifBlank { null }
        } catch (_: Exception) {
            null
        }
    }
}


package com.athar.app

import com.athar.app.data.TafsirEdition
import com.athar.app.data.TafsirRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TafsirRepositoryTest {

    @Test
    fun tafsirEditions_haveCorrectTafsirAppSlugs() {
        assertEquals("saadi", TafsirEdition.SAADI.tafsirAppSlug)
        assertEquals("ibn-katheer", TafsirEdition.IBN_KATHIR.tafsirAppSlug)
        assertEquals("tabari", TafsirEdition.TABARI.tafsirAppSlug)
        assertEquals("qurtubi", TafsirEdition.QURTUBI.tafsirAppSlug)
        assertEquals("muyassar", TafsirEdition.MUYASSAR.tafsirAppSlug)
    }

    @Test
    fun tafsirEditions_haveValidFiveEditions() {
        assertEquals(5, TafsirEdition.entries.size)
        assertTrue(TafsirEdition.entries.contains(TafsirEdition.SAADI))
        assertTrue(TafsirEdition.entries.contains(TafsirEdition.IBN_KATHIR))
        assertTrue(TafsirEdition.entries.contains(TafsirEdition.TABARI))
        assertTrue(TafsirEdition.entries.contains(TafsirEdition.QURTUBI))
        assertTrue(TafsirEdition.entries.contains(TafsirEdition.MUYASSAR))
    }

    @Test
    fun cleanTafsirText_replacesDoubleBracketsAndStripsHtml() {
        val raw = "<p>قال الله تعالى: ﴿الْحَمْدُ لِلَّهِ﴾ [[المسند (٥/١٧٨) .]]</p><br/>&quot;حديث شريف&quot;"
        val cleaned = TafsirRepository.cleanTafsirText(raw)

        assertFalse(cleaned.contains("<p>"))
        assertFalse(cleaned.contains("</p>"))
        assertFalse(cleaned.contains("<br/>"))
        assertTrue(cleaned.contains("[المسند (٥/١٧٨) .]"))
        assertFalse(cleaned.contains("[["))
        assertFalse(cleaned.contains("]]"))
        assertTrue(cleaned.contains("\"حديث شريف\""))
    }

    @Test
    fun extractSingleAyahTafsir_stripsSurahIntroForAyahGreaterThanOne() {
        val textWithIntro = """
            ذِكْرُ مَا وَرَدَ فِي فَضْلِهَا
            روى أحمد في مسنده عن أبي الدرداء حديث الدجال.
            * * *
            ﴿لِيُنْذِرَ بَأْسًا شَدِيدًا مِنْ لَدُنْهُ﴾ أي لمن خالفه
        """.trimIndent()

        val extracted = TafsirRepository.extractSingleAyahTafsir(textWithIntro, ayahNumber = 2)
        assertFalse(extracted.contains("ذِكْرُ مَا وَرَدَ فِي فَضْلِهَا"))
        assertTrue(extracted.contains("﴿لِيُنْذِرَ بَأْسًا شَدِيدًا مِنْ لَدُنْهُ﴾"))
    }

    @Test
    fun extractSingleAyahTafsir_preservesCompleteCommentaryWhenSingleAyah() {
        val pureAyahTafsir = "﴿الْحَمْدُ لِلَّهِ﴾ هو: الثناء على الله بصفات الكمال."
        val extracted = TafsirRepository.extractSingleAyahTafsir(pureAyahTafsir, ayahNumber = 2)
        assertEquals(pureAyahTafsir, extracted)
    }
}

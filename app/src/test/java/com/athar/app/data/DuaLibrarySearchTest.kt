package com.athar.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DuaLibrarySearchTest {

    private val chapter = LibraryChapter(
        number = 27,
        titleAr = "أذكار الصباح والمساء",
        titleEn = "Words of remembrance for morning and evening",
        duas = listOf(
            LibraryDua(
                id = "lib_27_1",
                arabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيطَانِ الرَّجِيمِ",
                translation = "I seek refuge in Allah from Satan, the rejected.",
                repeat = 1,
                reference = null
            ),
            LibraryDua(
                id = "lib_27_2",
                arabic = "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
                translation = "Allah - there is no deity except Him, the Ever-Living.",
                repeat = 1,
                reference = null
            )
        )
    )

    private val library = DuaLibrary(chapters = listOf(chapter), attribution = "")

    @Test
    fun emptyQueryReturnsNothing() {
        assertTrue(library.search("").isEmpty())
        assertTrue(library.search("   ").isEmpty())
    }

    @Test
    fun arabicQueryIgnoresDiacriticsAndAlefVariants() {
        val hits = library.search("اعوذ بالله")
        assertEquals(1, hits.size)
        assertEquals(0, hits.first().duaIndex)
    }

    @Test
    fun englishQueryMatchesTranslationIgnoringCase() {
        val hits = library.search("ever-living")
        assertEquals(1, hits.size)
        assertEquals(1, hits.first().duaIndex)
    }

    @Test
    fun chapterTitleMatchComesFirst() {
        val hits = library.search("morning")
        assertNull(hits.first().duaIndex)
        assertEquals(27, hits.first().chapter.number)
    }

    @Test
    fun limitIsRespected() {
        val hits = library.search("allah", limit = 1)
        assertEquals(1, hits.size)
    }
}

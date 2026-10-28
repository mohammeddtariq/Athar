package com.athar.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArabicSearchTest {

    private val ayah = "إِنَّ مَعَ الْعُسْرِ يُسْرًا"

    private fun score(query: String): Float =
        passageScore(searchWords(query), searchWords(ayah))

    @Test
    fun foldingDropsTashkeelAndUnifiesAlef() {
        assertEquals("ان مع العسر يسري", foldForSearch("إِنَّ مَعَ ٱلْعُسْرِ يُسْرَى"))
    }

    @Test
    fun foldingUnifiesTaMarbutaAndHamzaCarriers() {
        assertEquals("صلاه مومنه", foldForSearch("صلاة مؤمنة"))
    }

    @Test
    fun typingWithoutTashkeelScoresPerfectly() {
        assertEquals(1f, score("ان مع العسر يسرا"), 0.001f)
    }

    @Test
    fun aSingleTypoStillMatches() {
        assertTrue("a misspelt word should still land", score("ان مع العسر يسري") >= 0.9f)
    }

    @Test
    fun unrelatedTextScoresLow() {
        assertTrue(score("الكهف") < 0.3f)
    }

    @Test
    fun containmentBeatsEditDistance() {
        assertEquals(1f, wordScore("عسر", "العسر"), 0.001f)
        assertEquals(1f, wordScore("يسر", "يسرا"), 0.001f)
        assertTrue(wordScore("يسري", "يسرا") > 0.8f)
    }

    @Test
    fun shortWordsAreNotFuzzed() {
        assertEquals(0f, wordScore("من", "ما"), 0.001f)
    }

    @Test
    fun editDistanceRespectsItsBudget() {
        assertEquals(1, editDistance("يسرا", "يسري", 2))
        assertEquals(4, editDistance("قل", "الكهف", 4))
        assertTrue("an over-budget pair is abandoned", editDistance("قل", "الكهف", 2) > 2)
    }
}

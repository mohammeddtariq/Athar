package com.athar.app

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun versionRegex_matchesCorrectly() {
        val regex = Regex("""^v?(\d+\.\d+(?:\.\d+)?)-beta\.?(\d+)?$""", RegexOption.IGNORE_CASE)
        
        val match1 = regex.matchEntire("1.0.0-beta.2")
        assertNotNull(match1)
        assertEquals("1.0.0", match1!!.groupValues[1])
        assertEquals("2", match1.groupValues[2])

        val match2 = regex.matchEntire("1.0.0-beta")
        assertNotNull(match2)
        assertEquals("1.0.0", match2!!.groupValues[1])
        assertEquals("", match2.groupValues[2])

        val match3 = regex.matchEntire("1.0.0")
        assertNull(match3)
    }
}
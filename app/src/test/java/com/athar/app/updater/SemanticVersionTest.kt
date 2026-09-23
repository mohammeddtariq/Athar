package com.athar.app.updater

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SemanticVersionTest {

    @Test
    fun parse_validVersions() {
        val v1 = SemanticVersion.parse("1.0.1-beta.1")
        assertNotNull(v1)
        assertEquals(1, v1!!.major)
        assertEquals(0, v1.minor)
        assertEquals(1, v1.patch)
        assertEquals("beta", v1.preReleaseType)
        assertEquals(1, v1.preReleaseNumber)

        val v2 = SemanticVersion.parse("v1.0.1-beta.2")
        assertNotNull(v2)
        assertEquals(1, v2!!.major)
        assertEquals(0, v2.minor)
        assertEquals(1, v2.patch)
        assertEquals("beta", v2.preReleaseType)
        assertEquals(2, v2.preReleaseNumber)

        val v3 = SemanticVersion.parse("v2.0.0")
        assertNotNull(v3)
        assertEquals(2, v3!!.major)
        assertEquals(0, v3.minor)
        assertEquals(0, v3.patch)
        assertEquals(null, v3.preReleaseType)
    }

    @Test
    fun comparison_betaProgression() {
        val current = SemanticVersion.parse("1.0.1-beta.1")!!
        val nextBeta = SemanticVersion.parse("1.0.1-beta.2")!!
        assertTrue("Next beta should be newer than current beta", nextBeta > current)

        val stableRelease = SemanticVersion.parse("1.0.1")!!
        assertTrue("Stable release should be newer than pre-release", stableRelease > current)

        val nextPatch = SemanticVersion.parse("1.0.2-beta.1")!!
        assertTrue("Next patch beta should be newer than current", nextPatch > current)

        val nextMinor = SemanticVersion.parse("1.1.0")!!
        assertTrue("Next minor should be newer than current", nextMinor > current)
    }

    @Test
    fun comparison_equalityAndOlder() {
        val v1 = SemanticVersion.parse("v1.0.1-beta.1")!!
        val v2 = SemanticVersion.parse("1.0.1-beta.1")!!
        assertEquals(0, v1.compareTo(v2))

        val older = SemanticVersion.parse("1.0.0-beta.2")!!
        assertTrue("Older version should be smaller", older < v1)
    }
}

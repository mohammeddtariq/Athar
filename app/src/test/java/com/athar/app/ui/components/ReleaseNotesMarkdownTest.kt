package com.athar.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesMarkdownTest {

    private val sample = """
        # Release 1.0.8-beta

        > *"A sanctuary for a restless mind"*
        > *«أمانة لعقلٍ لا يهدأ»*

        # What changed?

        ### ✨ New Features

        - **Offline library**: 267 supplications bundled with the app.  
          Runs entirely offline, no network needed.
          - **Chapters**: Browse all 132 chapters.
          - **Counters**: Keep your dhikr count per dua.
        - **Smart reminders**: Jumu'ah reminders.

        ---

        ### 🐛 Fixes

        - Faster startup &amp; smoother scrolling.<br>Less memory used.

        1. First step
        2. Second step with `inline code` and a [link](https://example.com)
    """.trimIndent()

    private fun blocks() = parseMarkdown(sample)

    @Test
    fun headingsAreParsedByLevel() {
        val headings = blocks().filterIsInstance<MarkdownBlock.Heading>()
        assertEquals(4, headings.size)
        assertEquals(1, headings[0].level)
        assertEquals("Release 1.0.8-beta", headings[0].text)
        assertEquals(1, headings[1].level)
        assertEquals(3, headings[2].level)
        assertEquals("✨ New Features", headings[2].text)
        assertEquals(3, headings[3].level)
        assertEquals("🐛 Fixes", headings[3].text)
    }

    @Test
    fun quotedLinesMergeIntoOneBlock() {
        val quotes = blocks().filterIsInstance<MarkdownBlock.Quote>()
        assertEquals(1, quotes.size)
        assertTrue(quotes[0].text.contains("restless mind"))
        assertTrue(quotes[0].text.contains("أمانة"))
        assertTrue(quotes[0].text.startsWith("*"))
    }

    @Test
    fun bulletsKeepDepthAndMergeWrappedLines() {
        val bullets = blocks().filterIsInstance<MarkdownBlock.Bullet>()
        assertEquals(5, bullets.size)
        assertEquals(0, bullets[0].depth)
        assertTrue(bullets[0].text.contains("Offline library"))
        assertTrue(bullets[0].text.contains("Runs entirely offline"))
        assertEquals(1, bullets[1].depth)
        assertTrue(bullets[1].text.contains("Chapters"))
        assertEquals(1, bullets[2].depth)
        assertEquals(0, bullets[3].depth)
        assertTrue(bullets[3].text.contains("Smart reminders"))
    }

    @Test
    fun horizontalRulesBecomeDividers() {
        assertEquals(1, blocks().count { it is MarkdownBlock.Rule })
    }

    @Test
    fun entitiesAndHtmlBreaksAreNormalized() {
        val text = blocks().filterIsInstance<MarkdownBlock.Bullet>().last().text
        assertTrue(text.contains("Faster startup & smoother scrolling."))
        assertTrue(text.contains("Less memory used."))
        assertTrue(!text.contains("<br>"))
    }

    @Test
    fun paragraphAfterBlankLineDoesNotMergeIntoPreviousBullet() {
        val parsed = parseMarkdown("- First item\n\nA separate paragraph.")
        assertEquals(2, parsed.size)
        assertTrue(parsed[0] is MarkdownBlock.Bullet)
        assertTrue(parsed[1] is MarkdownBlock.Paragraph)
    }

    @Test
    fun orderedListItemsAreParsed() {
        val items = blocks().filterIsInstance<MarkdownBlock.Numbered>()
        assertEquals(2, items.size)
        assertEquals("1", items[0].number)
        assertEquals("First step", items[0].text)
        assertTrue(items[1].text.contains("`inline code`"))
        assertTrue(items[1].text.contains("[link](https://example.com)"))
    }

    @Test
    fun emptyAndBlankInputProduceNoBlocks() {
        assertTrue(parseMarkdown("").isEmpty())
        assertTrue(parseMarkdown("\n\n   \n").isEmpty())
    }
}

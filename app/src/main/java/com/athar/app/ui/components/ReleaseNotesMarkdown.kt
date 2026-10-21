package com.athar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans

/**
 * Minimal GitHub-flavoured Markdown renderer for release notes.
 *
 * Covers what maintainers actually write in release bodies: headings, bold/italic,
 * inline code, links, bullet and numbered lists (including nesting), blockquotes,
 * horizontal rules and the handful of inline HTML tags GitHub allows.
 */
@Composable
fun ReleaseNotesMarkdown(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val blocks = remember(markdown) { parseMarkdown(markdown) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    if (block.level == 1) Spacer(Modifier.height(4.dp))
                    MarkdownDirection(block.text) {
                        Text(
                            text = inlineMarkdown(block.text),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = when (block.level) {
                                1 -> 16.sp
                                2 -> 14.sp
                                3 -> 12.5.sp
                                else -> 12.sp
                            },
                            lineHeight = 21.sp,
                            color = if (block.level <= 2) AtharPrimaryLight else AtharTextPrimary
                        )
                    }
                }

                is MarkdownBlock.Paragraph -> {
                    MarkdownDirection(block.text) {
                        Text(
                            text = inlineMarkdown(block.text),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = AtharTextPrimary.copy(alpha = 0.92f)
                        )
                    }
                }

                is MarkdownBlock.Bullet -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (block.depth * 14).dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = if (block.depth == 0) "•" else "◦",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = if (block.depth == 0) 12.sp else 11.sp,
                            color = if (block.depth == 0) AtharPrimary else AtharPrimaryLight,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Spacer(Modifier.width(7.dp))
                        MarkdownDirection(block.text) {
                            Text(
                                text = inlineMarkdown(block.text),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = if (block.depth == 0) 11.5.sp else 11.sp,
                                lineHeight = 17.sp,
                                color = AtharTextPrimary.copy(alpha = if (block.depth == 0) 0.92f else 0.8f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                is MarkdownBlock.Numbered -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (block.depth * 14).dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${block.number}.",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = AtharPrimary,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        MarkdownDirection(block.text) {
                            Text(
                                text = inlineMarkdown(block.text),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.5.sp,
                                lineHeight = 17.sp,
                                color = AtharTextPrimary.copy(alpha = 0.92f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                is MarkdownBlock.Quote -> {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(18.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(AtharPrimary.copy(alpha = 0.55f))
                        )
                        Spacer(Modifier.width(8.dp))
                        MarkdownDirection(block.text) {
                            Text(
                                text = inlineMarkdown(block.text),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic,
                                fontSize = 11.5.sp,
                                lineHeight = 17.sp,
                                color = AtharTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                is MarkdownBlock.Code -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtharCardBorder.copy(alpha = 0.35f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = block.text,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            lineHeight = 15.sp,
                            color = AtharTextPrimary
                        )
                    }
                }

                MarkdownBlock.Rule -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(1.dp)
                            .background(AtharCardBorder)
                    )
                }
            }
        }
    }
}

/**
 * Picks the paragraph direction from the first strong character, mirroring what
 * a Markdown viewer does. Release notes mix English prose with Arabic names, so
 * "any Arabic character" (as [LocalizedTextDirection] does) would flip whole
 * English paragraphs to RTL.
 */
@Composable
private fun MarkdownDirection(text: String, content: @Composable () -> Unit) {
    val direction = remember(text) {
        val cleaned = text.replace(Regex("""[`*_~\[\]()#>|]"""), "")
        when {
            cleaned.any { it.isStrongArabic() } -> LayoutDirection.Rtl
            cleaned.any { it.isLetter() } -> LayoutDirection.Ltr
            else -> LayoutDirection.Ltr
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        content()
    }
}

private fun Char.isStrongArabic(): Boolean {
    val code = code
    return code in 0x0600..0x06FF ||
        code in 0x0750..0x077F ||
        code in 0x08A0..0x08FF ||
        code in 0xFB50..0xFDFF ||
        code in 0xFE70..0xFEFF
}

internal sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    data class Bullet(val depth: Int, val text: String) : MarkdownBlock
    data class Numbered(val depth: Int, val number: String, val text: String) : MarkdownBlock
    data class Quote(val text: String) : MarkdownBlock
    data class Code(val text: String) : MarkdownBlock
    data object Rule : MarkdownBlock
}

private val HEADING = Regex("""^\s{0,3}(#{1,6})\s+(.*)$""")
private val BULLET = Regex("""^(\s*)[-*+]\s+(.*)$""")
private val NUMBERED = Regex("""^(\s*)(\d{1,3})[.)]\s+(.*)$""")
private val RULE = Regex("""^\s{0,3}([-*_])(\s*\1){2,}\s*$""")
private val FENCE = Regex("""^\s*```.*$""")

internal fun parseMarkdown(raw: String): List<MarkdownBlock> {
    val normalized = raw
        .replace("\r\n", "\n")
        .replace("\r", "\n")
        .replace(Regex("(?i)<br\\s*/?>"), "\n")

    val blocks = ArrayList<MarkdownBlock>()
    val codeBuffer = ArrayList<String>()
    var inCode = false
    var paragraph = StringBuilder()
    var paragraphIsQuote = false
    var afterBlankLine = true

    fun flushParagraph() {
        if (paragraph.isEmpty()) return
        val text = normalizeInlineHtml(paragraph.toString()).trim()
        paragraph = StringBuilder()
        if (text.isEmpty()) return
        blocks.add(if (paragraphIsQuote) MarkdownBlock.Quote(text) else MarkdownBlock.Paragraph(text))
        paragraphIsQuote = false
    }

    for (rawLine in normalized.split('\n')) {
        val line = rawLine.trimEnd()

        if (FENCE.matches(line)) {
            if (inCode) {
                blocks.add(MarkdownBlock.Code(codeBuffer.joinToString("\n")))
                codeBuffer.clear()
                inCode = false
            } else {
                flushParagraph()
                inCode = true
            }
            afterBlankLine = false
            continue
        }
        if (inCode) {
            codeBuffer.add(rawLine)
            continue
        }

        val trimmed = line.trim()
        when {
            trimmed.isEmpty() -> {
                afterBlankLine = true
                flushParagraph()
                continue
            }

            RULE.matches(line) -> {
                flushParagraph()
                // A rule of dashes right after a paragraph would already have been
                // consumed as a setext underline; release notes don't rely on that.
                blocks.add(MarkdownBlock.Rule)
            }

            HEADING.matches(line) -> {
                flushParagraph()
                val match = HEADING.find(line)!!
                val level = match.groupValues[1].length
                val text = normalizeInlineHtml(match.groupValues[2]).trim()
                if (text.isNotEmpty()) blocks.add(MarkdownBlock.Heading(level, text))
            }

            trimmed.startsWith(">") -> {
                val quoteText = trimmed.removePrefix(">").removePrefix(" ").trim()
                if (paragraphIsQuote && paragraph.isNotEmpty()) {
                    paragraph.append(' ').append(quoteText)
                } else {
                    flushParagraph()
                    paragraphIsQuote = true
                    paragraph.append(quoteText)
                }
            }

            BULLET.matches(line) -> {
                flushParagraph()
                val match = BULLET.find(line)!!
                val depth = leadingDepth(match.groupValues[1])
                val text = normalizeInlineHtml(match.groupValues[2]).trim()
                if (text.isNotEmpty()) blocks.add(MarkdownBlock.Bullet(depth, text))
            }

            NUMBERED.matches(line) -> {
                flushParagraph()
                val match = NUMBERED.find(line)!!
                val depth = leadingDepth(match.groupValues[1])
                val number = match.groupValues[2]
                val text = normalizeInlineHtml(match.groupValues[3]).trim()
                if (text.isNotEmpty()) blocks.add(MarkdownBlock.Numbered(depth, number, text))
            }

            else -> {
                // Wrapped text of the previous list item or quote: Markdown's lazy
                // continuation, with or without indentation.
                val last = blocks.lastOrNull()
                val isContinuation = !afterBlankLine && (
                    last is MarkdownBlock.Bullet ||
                        last is MarkdownBlock.Numbered ||
                        last is MarkdownBlock.Quote
                    )
                if (isContinuation) {
                    val extra = normalizeInlineHtml(trimmed).trim()
                    if (extra.isNotEmpty()) {
                        blocks[blocks.lastIndex] = when (last) {
                            is MarkdownBlock.Bullet -> last.copy(text = joinText(last.text, extra))
                            is MarkdownBlock.Numbered -> last.copy(text = joinText(last.text, extra))
                            is MarkdownBlock.Quote -> last.copy(text = joinText(last.text, extra))
                            else -> last
                        }
                    }
                } else {
                    if (paragraph.isNotEmpty()) paragraph.append(' ')
                    paragraph.append(trimmed)
                }
            }
        }
        afterBlankLine = false
    }
    if (inCode && codeBuffer.isNotEmpty()) blocks.add(MarkdownBlock.Code(codeBuffer.joinToString("\n")))
    flushParagraph()
    return blocks
}

private fun joinText(current: String, extra: String): String = current.trimEnd() + " " + extra

/** Two-space (or tab) indentation steps GitHub uses for nested list items. */
private fun leadingDepth(indent: String): Int {
    var spaces = 0
    for (ch in indent) spaces += if (ch == '\t') 2 else 1
    return (spaces / 2).coerceIn(0, 3)
}

private fun normalizeInlineHtml(text: String): String {
    var out = text
        .replace(Regex("""<img[^>]*>"""), "")
        .replace(Regex("""</?[a-zA-Z][^>]*>"""), "")
    out = out
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
    return out.replace(Regex("[ \\t]{2,}"), " ")
}

/**
 * Renders inline markdown (bold, italics, strikethrough, inline code, links)
 * into a styled [AnnotatedString].
 */
private fun inlineMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    appendInline(text)
}

private fun AnnotatedString.Builder.appendInline(text: String) {
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end > i + 2) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInline(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(text[i]); i++
                }
            }

            text.startsWith("__", i) -> {
                val end = text.indexOf("__", i + 2)
                if (end > i + 2) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInline(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(text[i]); i++
                }
            }

            text.startsWith("~~", i) -> {
                val end = text.indexOf("~~", i + 2)
                if (end > i + 2) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        appendInline(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(text[i]); i++
                }
            }

            text[i] == '`' -> {
                val end = text.indexOf('`', i + 1)
                if (end > i) {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            color = AtharPrimaryLight
                        )
                    ) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text[i]); i++
                }
            }

            text[i] == '*' || text[i] == '_' -> {
                val marker = text[i]
                val end = findClosing(text, i + 1, marker)
                if (end > i + 1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        appendInline(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text[i]); i++
                }
            }

            text[i] == '[' -> {
                val labelEnd = text.indexOf(']', i + 1)
                val urlStart = if (labelEnd != -1 && labelEnd + 1 < text.length && text[labelEnd + 1] == '(') labelEnd + 2 else -1
                val urlEnd = if (urlStart != -1) text.indexOf(')', urlStart) else -1
                if (labelEnd > i + 1 && urlEnd > urlStart) {
                    val label = text.substring(i + 1, labelEnd)
                    val url = text.substring(urlStart, urlEnd)
                        .trim()
                        .substringBefore(' ')
                        .removeSurrounding("<", ">")
                    if (url.startsWith("http", ignoreCase = true)) {
                        withLink(
                            LinkAnnotation.Url(
                                url = url,
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = AtharPrimaryLight,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            )
                        ) {
                            appendInline(label)
                        }
                    } else {
                        appendInline(label)
                    }
                    i = urlEnd + 1
                } else {
                    append(text[i]); i++
                }
            }

            else -> {
                append(text[i]); i++
            }
        }
    }
}

private fun findClosing(text: String, from: Int, marker: Char): Int {
    var j = from
    while (j < text.length) {
        if (text[j] == marker) {
            // Don't treat the start of a ** bold run as an italic close.
            if (j + 1 < text.length && text[j + 1] == marker) {
                j += 2
                continue
            }
            return j
        }
        j++
    }
    return -1
}

package com.athar.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.athar.app.data.QuranPassage
import com.athar.app.data.QuranPassages
import com.athar.app.data.QuranRepository
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSerifText

/**
 * The Quranic passages of [text], resolving once the mushaf index is warm.
 *
 * The index is built on first use; until it is ready this returns empty, so the
 * text simply renders in the prose face for a frame or two rather than blocking.
 */
@Composable
fun rememberQuranPassages(text: String): List<QuranPassage> {
    val context = LocalContext.current
    var ready by remember { mutableStateOf(QuranPassages.isReady) }

    LaunchedEffect(text, ready) {
        if (!ready) {
            QuranPassages.warm(context)
            ready = QuranPassages.isReady
        }
    }

    return remember(text, ready) {
        if (ready) QuranPassages.find(text) else emptyList()
    }
}

/**
 * A body of Arabic that may quote the Quran.
 *
 * Verses are swapped for their canonical Uthmani wording, set in the mushaf
 * face used by the reader and closed with the ornate ayah number — so a
 * supplication reads as prose with the Quran quoted in it, rather than as one
 * undifferentiated block.
 */
@Composable
fun QuranAwareText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary,
    fontSize: TextUnit = 22.sp,
    lineHeight: TextUnit = 42.sp,
    textAlign: TextAlign = TextAlign.End
) {
    val passages = rememberQuranPassages(text)
    val annotated = remember(text, passages) {
        quranAwareAnnotated(text, passages, quranSize = 26.sp)
    }

    Text(
        text = annotated,
        modifier = modifier,
        fontFamily = ThmanyahSerifText,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = color,
        textAlign = textAlign
    )
}

/** Muted accent used for the ayah marker itself. */
@Composable
fun ayahMarkerColor(): Color = AtharPrimaryLight.copy(alpha = 0.9f)

/**
 * Builds [text] with every Quranic passage reset in the mushaf face and
 * followed by its ayah number in the ornate brackets the mushaf uses.
 *
 * The prose between verses is kept word for word, minus the rosettes it used to
 * separate ayat with — those would otherwise survive next to the ayah number as
 * a pair of empty ornaments.
 */
fun quranAwareAnnotated(
    text: String,
    passages: List<QuranPassage>,
    quranSize: TextUnit = 26.sp
): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    for (passage in passages) {
        if (passage.start > cursor) append(proseGap(text.substring(cursor, passage.start)))
        withStyle(
            SpanStyle(
                fontFamily = QuranUthmanicHafs,
                fontSize = quranSize,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(passage.text)
            append(" ")
            append("\uFD3F")
            append(with(QuranRepository) { passage.ayah.toArabicIndic() })
            append("\uFD3E")
        }
        cursor = passage.end
    }
    if (cursor < text.length) append(proseGap(text.substring(cursor)))
}

/** Drops the rosette the prose used between ayat, and the spaces around it. */
private fun proseGap(gap: String): String = gap.replace(VERSE_SEPARATOR, " ")

private val VERSE_SEPARATOR = Regex("[ \\t]*\u06DE[ \\t]*")

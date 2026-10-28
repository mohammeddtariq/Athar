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
 * face used by the reader, and closed with the ornate ayah marker and the verse
 * number — so a supplication reads as prose with the Quran quoted in it, rather
 * than as one undifferentiated block.
 */
@Composable
fun QuranAwareText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtharTextPrimary,
    fontSize: TextUnit = 21.sp,
    lineHeight: TextUnit = 40.sp,
    textAlign: TextAlign = TextAlign.End
) {
    val passages = rememberQuranPassages(text)
    val annotated = remember(text, passages) {
        buildAnnotatedString {
            var cursor = 0
            for (passage in passages) {
                if (passage.start > cursor) {
                    append(text.substring(cursor, passage.start))
                }
                withStyle(SpanStyle(fontFamily = QuranUthmanicHafs, fontSize = 22.sp)) {
                    append(passage.text)
                    append(" ")
                    append("\u06DD")
                    append(with(QuranRepository) { passage.ayah.toArabicIndic() })
                }
                cursor = passage.end
            }
            if (cursor < text.length) append(text.substring(cursor))
        }
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

/** Convenience for plain annotated output, used by the share renderer. */
fun buildQuranAware(text: String, passages: List<QuranPassage>): AnnotatedString =
    buildAnnotatedString {
        var cursor = 0
        for (passage in passages) {
            if (passage.start > cursor) append(text.substring(cursor, passage.start))
            withStyle(SpanStyle(fontFamily = QuranUthmanicHafs, fontSize = 22.sp)) {
                append(passage.text)
                append(" \u06DD")
                append(with(QuranRepository) { passage.ayah.toArabicIndic() })
            }
            cursor = passage.end
        }
        if (cursor < text.length) append(text.substring(cursor))
    }

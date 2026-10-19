package com.athar.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val ArabicBlock = '\u0600'..'\u06FF'

/**
 * Renders [content] using the paragraph direction that matches the dominant script of [text].
 *
 * Dual-language screens frequently show English translations inside an Arabic (RTL) host.
 * Without an explicit direction the trailing punctuation of an LTR run can drift to the
 * visual beginning of the line. Choosing the direction from the text itself keeps sentences
 * and their punctuation intact regardless of the surrounding layout.
 */
@Composable
fun LocalizedTextDirection(
    text: String,
    content: @Composable () -> Unit
) {
    val direction = remember(text) {
        if (text.any { it in ArabicBlock }) LayoutDirection.Rtl else LayoutDirection.Ltr
    }
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        content()
    }
}

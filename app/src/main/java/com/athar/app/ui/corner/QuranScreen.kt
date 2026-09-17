package com.athar.app.ui.corner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifText

/**
 * Built-in Quran reader — pure AMOLED black, mushaf-style page,
 * matching the reference screenshot (surah pill header, centered
 * Uthmani text, page divider, floating controls).
 */
@Composable
fun QuranScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var openSurah by remember { mutableStateOf<SurahMeta?>(null) }
    var fontScale by remember { mutableFloatStateOf(1f) }

    val filtered = remember(query) {
        if (query.isBlank()) allSurahs
        else allSurahs.filter {
            it.arabicName.contains(query.trim()) ||
                it.englishName.contains(query.trim(), ignoreCase = true) ||
                it.number.toString() == query.trim()
        }
    }

    val current = openSurah
    if (current != null) {
        SurahReader(
            surah = current,
            fontScale = fontScale,
            onFontScale = { fontScale = it.coerceIn(0.8f, 1.6f) },
            onBackToList = { openSurah = null },
            onNext = {
                val idx = allSurahs.indexOf(current)
                if (idx in 0 until allSurahs.lastIndex) openSurah = allSurahs[idx + 1]
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back acts as list toggle here — keep symmetric with reader header
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1E18))
                    .border(1.dp, Color(0xFF262B24), CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = AtharTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1A1E18))
                    .border(1.dp, Color(0xFF262B24), RoundedCornerShape(22.dp))
                    .padding(horizontal = 26.dp, vertical = 10.dp)
            ) {
                Text(
                    stringResource(R.string.quran_title),
                    fontFamily = ThmanyahSerifText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = AtharTextPrimary
                )
            }
            Spacer(Modifier.size(44.dp))
        }

        // Search
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF101310))
                .border(1.dp, Color(0xFF232820), RoundedCornerShape(24.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.End
                ),
                cursorBrush = SolidColor(AtharPrimaryLight),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            stringResource(R.string.quran_search),
                            fontFamily = ThmanyahSans,
                            fontSize = 14.sp,
                            color = AtharTextSecondary.copy(alpha = 0.6f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    inner()
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 110.dp)
        ) {
            items(filtered, key = { it.number }) { surah ->
                val readable = readableSurahText.containsKey(surah.number)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0D0F0C))
                        .border(1.dp, Color(0xFF1C211A), RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { openSurah = surah }
                        )
                        .padding(horizontal = 16.dp, vertical = 13.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Number badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF161A14)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${surah.number}",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AtharTextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (readable) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(AtharPrimaryLight, CircleShape)
                                    )
                                    Spacer(Modifier.width(7.dp))
                                }
                                Text(
                                    "سُورَةُ ${surah.arabicName}",
                                    fontFamily = ThmanyahSerifText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = AtharTextPrimary,
                                    textAlign = TextAlign.End
                                )
                            }
                            Text(
                                "${surah.englishName} • " + stringResource(
                                    R.string.quran_ayahs,
                                    surah.ayahs
                                ),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = AtharTextSecondary,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahReader(
    surah: SurahMeta,
    fontScale: Float,
    onFontScale: (Float) -> Unit,
    onBackToList: () -> Unit,
    onNext: () -> Unit
) {
    val text = readableSurahText[surah.number]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        // Top bar: list | surah pill | next
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReaderCircleButton(
                icon = { Icon(Icons.Rounded.List, null, tint = AtharTextPrimary, modifier = Modifier.size(20.dp)) },
                onClick = onBackToList
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1A1E18))
                    .border(1.dp, Color(0xFF262B24), RoundedCornerShape(22.dp))
                    .padding(horizontal = 30.dp, vertical = 9.dp)
            ) {
                Text(
                    "سُورَةُ ${surah.arabicName}",
                    fontFamily = ThmanyahSerifText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    color = AtharTextPrimary
                )
            }
            ReaderCircleButton(
                icon = {
                    Text(
                        "→",
                        color = AtharTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                onClick = onNext
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 24.dp, end = 24.dp, top = 8.dp, bottom = 130.dp
            )
        ) {
            item {
                if (text != null) {
                    Text(
                        text,
                        fontFamily = ThmanyahSerifText,
                        fontWeight = FontWeight.Normal,
                        fontSize = (23 * fontScale).sp,
                        lineHeight = (44 * fontScale).sp,
                        color = Color(0xFFF2F4EE),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        stringResource(R.string.quran_coming),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = AtharTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp)
                    )
                }
            }
            item {
                // Page divider with surah number
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 26.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0xFF22261F))
                    )
                    Text(
                        "  ${surah.number}  ",
                        fontFamily = ThmanyahSans,
                        fontSize = 12.sp,
                        color = AtharTextSecondary
                    )
                    Box(
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0xFF22261F))
                    )
                }
            }
        }
    }

    // Floating controls (font size - / reset / +)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 112.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF141712).copy(alpha = 0.92f))
                .border(1.dp, Color(0xFF262B24), RoundedCornerShape(28.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReaderCircleButton(
                icon = { Icon(Icons.Rounded.FormatSize, null, tint = AtharTextPrimary, modifier = Modifier.size(20.dp)) },
                onClick = { onFontScale(fontScale - 0.1f) }
            )
            ReaderCircleButton(
                icon = {
                    Text(
                        "أ",
                        color = AtharTextPrimary,
                        fontFamily = ThmanyahSerifText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                onClick = { onFontScale(1f) }
            )
            ReaderCircleButton(
                icon = {
                    Text(
                        "+",
                        color = AtharTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                onClick = { onFontScale(fontScale + 0.1f) }
            )
        }
    }
}

@Composable
private fun ReaderCircleButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1E18))
            .border(1.dp, Color(0xFF2A3026), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

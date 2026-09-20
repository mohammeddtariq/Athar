package com.athar.app.ui.corner

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.QuranReciter
import com.athar.app.data.QuranRepository
import com.athar.app.data.QuranRepository.toArabicIndic
import com.athar.app.data.QuranThemeMode
import com.athar.app.data.VerseChunk
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.QuranBismillah
import com.athar.app.ui.theme.QuranSurahNames
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.ui.theme.ThmanyahSerifText
import kotlinx.coroutines.launch

/**
 * 3 Appearance Mode Color Schemes for the Quran Reader:
 * 1. AMOLED: Pure pitch-black Mushaf (#000000)
 * 2. DARK_OLIVE: Athar signature dark sage (#0A0C08) without pattern symbols
 * 3. LIGHT: Classic Mushaf cream paper (#FBF9F4)
 */
data class QuranReaderColors(
    val background: Color,
    val text: Color,
    val ayahMarker: Color,
    val pillBg: Color,
    val pillBorder: Color,
    val pillText: Color,
    val circleButtonBg: Color,
    val circleButtonBorder: Color,
    val circleButtonIcon: Color,
    val dividerLine: Color,
    val dividerText: Color,
    val floatingPillBg: Color,
    val floatingPillBorder: Color,
    val floatingPillItemBg: Color,
    val floatingPillItemIcon: Color,
    val floatingPillActiveIcon: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val searchBg: Color,
    val searchBorder: Color,
    val isLight: Boolean
)

val AmoledReaderColors = QuranReaderColors(
    background = Color(0xFF000000),
    text = Color(0xFFF7F8F5),
    ayahMarker = Color(0xFFB4BCB0),
    pillBg = Color(0xFF141614),
    pillBorder = Color(0xFF282C24),
    pillText = Color(0xFFF7F8F5),
    circleButtonBg = Color(0xFF141614),
    circleButtonBorder = Color(0xFF282C24),
    circleButtonIcon = Color(0xFFF7F8F5),
    dividerLine = Color(0xFF222620),
    dividerText = Color(0xFF7A8276),
    floatingPillBg = Color(0xFF161815).copy(alpha = 0.95f),
    floatingPillBorder = Color(0xFF2C3227),
    floatingPillItemBg = Color(0xFF232720),
    floatingPillItemIcon = Color(0xFFEDEFEA),
    floatingPillActiveIcon = AtharPrimary,
    cardBg = Color(0xFF141712),
    cardBorder = Color(0xFF2A3026),
    searchBg = Color(0xFF101310),
    searchBorder = Color(0xFF232820),
    isLight = false
)

val OliveReaderColors = QuranReaderColors(
    background = Color(0xFF0A0C08),
    text = Color(0xFFEDEFEA),
    ayahMarker = Color(0xFF8E9B86),
    pillBg = Color(0xFF141812),
    pillBorder = Color(0xFF22281D),
    pillText = Color(0xFFEDEFEA),
    circleButtonBg = Color(0xFF141812),
    circleButtonBorder = Color(0xFF22281D),
    circleButtonIcon = Color(0xFFEDEFEA),
    dividerLine = Color(0xFF1E231B),
    dividerText = Color(0xFF6E7866),
    floatingPillBg = Color(0xFF141812).copy(alpha = 0.95f),
    floatingPillBorder = Color(0xFF242A20),
    floatingPillItemBg = Color(0xFF1D221A),
    floatingPillItemIcon = Color(0xFFEDEFEA),
    floatingPillActiveIcon = AtharPrimary,
    cardBg = Color(0xFF121510),
    cardBorder = Color(0xFF22281D),
    searchBg = Color(0xFF0F120D),
    searchBorder = Color(0xFF1E241A),
    isLight = false
)

val LightReaderColors = QuranReaderColors(
    background = Color(0xFFFBF9F4),
    text = Color(0xFF1A1D18),
    ayahMarker = Color(0xFF4E5846),
    pillBg = Color(0xFFEFECE4),
    pillBorder = Color(0xFFDFD9CC),
    pillText = Color(0xFF1A1D18),
    circleButtonBg = Color(0xFFEFECE4),
    circleButtonBorder = Color(0xFFDFD9CC),
    circleButtonIcon = Color(0xFF1A1D18),
    dividerLine = Color(0xFFDFD9CC),
    dividerText = Color(0xFF868277),
    floatingPillBg = Color(0xFFEFECE4).copy(alpha = 0.96f),
    floatingPillBorder = Color(0xFFDFD9CC),
    floatingPillItemBg = Color(0xFFDFD9CC),
    floatingPillItemIcon = Color(0xFF1A1D18),
    floatingPillActiveIcon = Color(0xFF2D4B26),
    cardBg = Color(0xFFF4F0E6),
    cardBorder = Color(0xFFDED8C9),
    searchBg = Color(0xFFF2EEE4),
    searchBorder = Color(0xFFDDD7C8),
    isLight = true
)

fun getQuranColors(mode: QuranThemeMode): QuranReaderColors = when (mode) {
    QuranThemeMode.AMOLED -> AmoledReaderColors
    QuranThemeMode.DARK_OLIVE -> OliveReaderColors
    QuranThemeMode.LIGHT -> LightReaderColors
}

private enum class QuranTabIndex {
    SURAHS, JUZ
}

/**
 * Built-in Quran reader screen matching the Islamic Mushaf reference design:
 * - Authentic Uthmani Hafs calligraphy text with ornate \u06DD ayah markers.
 * - Calligraphic Surah titles via QuranSurahNames font (0-indexed exact match).
 * - Dual-tab index: Surahs (1..114) and Juz (1..30) in exact canonical order.
 * - 3 Appearance Modes: AMOLED, Dark Olive (app theme), and Light Mushaf Paper.
 * - Floating bottom capsule with TT (font scale/weight), Palette (theme switcher), and Play (recitation audio).
 */
@Composable
fun QuranScreen(
    onBack: () -> Unit = {},
    onReadingModeChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appPrefs = remember { AppPreferences(context.applicationContext) }

    val savedThemeMode by appPrefs.quranThemeMode.collectAsState(initial = QuranThemeMode.AMOLED)
    val savedFontScale by appPrefs.quranFontScale.collectAsState(initial = 1.0f)
    val savedReciter by appPrefs.quranReciter.collectAsState(initial = QuranReciter.MINSHAWI)

    var themeMode by remember(savedThemeMode) { mutableStateOf(savedThemeMode) }
    var fontScale by remember(savedFontScale) { mutableFloatStateOf(savedFontScale) }
    var reciter by remember(savedReciter) { mutableStateOf(savedReciter) }
    var fontBold by remember { mutableStateOf(false) }

    var openSurah by remember { mutableStateOf<SurahMeta?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(QuranTabIndex.SURAHS) }

    LaunchedEffect(openSurah) {
        onReadingModeChanged(openSurah != null)
    }

    DisposableEffect(Unit) {
        onDispose {
            onReadingModeChanged(false)
        }
    }

    val colors = remember(themeMode) { getQuranColors(themeMode) }

    val currentSurah = openSurah
    if (currentSurah != null) {
        SurahReader(
            surah = currentSurah,
            themeMode = themeMode,
            colors = colors,
            fontScale = fontScale,
            fontBold = fontBold,
            reciter = reciter,
            onThemeChange = { newMode ->
                themeMode = newMode
                scope.launch { appPrefs.setQuranThemeMode(newMode) }
            },
            onFontScaleChange = { newScale ->
                val clamped = newScale.coerceIn(0.75f, 1.6f)
                fontScale = clamped
                scope.launch { appPrefs.setQuranFontScale(clamped) }
            },
            onReciterChange = { newReciter ->
                reciter = newReciter
                scope.launch { appPrefs.setQuranReciter(newReciter) }
            },
            onToggleBold = { fontBold = !fontBold },
            onBackToList = { openSurah = null },
            onSelectSurah = { openSurah = it },
            onNextSurah = {
                val idx = allSurahs.indexOfFirst { it.number == currentSurah.number }
                if (idx in 0 until allSurahs.lastIndex) {
                    openSurah = allSurahs[idx + 1]
                }
            }
        )
        return
    }

    // Surah & Juz Index Screen
    SurahListScreen(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        selectedTab = selectedTab,
        onTabSelect = { selectedTab = it },
        colors = colors,
        onBack = onBack,
        onSelectSurah = { openSurah = it }
    )
}

@Composable
private fun SurahListScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedTab: QuranTabIndex,
    onTabSelect: (QuranTabIndex) -> Unit,
    colors: QuranReaderColors,
    onBack: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val filteredSurahs = remember(query) {
        if (query.isBlank()) allSurahs
        else allSurahs.filter {
            it.arabicName.contains(query.trim()) ||
                it.englishName.contains(query.trim(), ignoreCase = true) ||
                it.englishTranslation.contains(query.trim(), ignoreCase = true) ||
                it.number.toString() == query.trim() ||
                it.juz.toString() == query.trim()
        }
    }

    val filteredJuz = remember(query) {
        if (query.isBlank()) allJuz
        else allJuz.filter {
            it.arabicName.contains(query.trim()) ||
                it.englishName.contains(query.trim(), ignoreCase = true) ||
                it.number.toString() == query.trim()
        }
    }

    PatternScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBack
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = AtharTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(AtharCardSurface)
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(24.dp))
                        .padding(horizontal = 26.dp, vertical = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.quran_title),
                        fontFamily = ThmanyahSerifText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = AtharTextPrimary
                    )
                }

                Spacer(Modifier.size(46.dp))
            }

            // Dual Tab Bar: "السور" (Surahs 1..114) and "الأجزاء" (Juz 1..30)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AtharCardSurface)
                    .border(1.dp, AtharCardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val surahsActive = selectedTab == QuranTabIndex.SURAHS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (surahsActive) AtharPrimary.copy(alpha = 0.22f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (surahsActive) AtharPrimaryLight.copy(alpha = 0.6f) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelect(QuranTabIndex.SURAHS) }
                        )
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.quran_tab_surahs) + " (114)",
                        fontFamily = ThmanyahSans,
                        fontWeight = if (surahsActive) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.5.sp,
                        color = if (surahsActive) AtharPrimaryLight else AtharTextSecondary
                    )
                }

                val juzActive = selectedTab == QuranTabIndex.JUZ
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (juzActive) AtharPrimary.copy(alpha = 0.22f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (juzActive) AtharPrimaryLight.copy(alpha = 0.6f) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelect(QuranTabIndex.JUZ) }
                        )
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.quran_tab_juz) + " (30)",
                        fontFamily = ThmanyahSans,
                        fontWeight = if (juzActive) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.5.sp,
                        color = if (juzActive) AtharPrimaryLight else AtharTextSecondary
                    )
                }
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AtharCardSurface)
                    .border(1.dp, AtharCardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp, vertical = 11.dp)
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = AtharTextPrimary,
                        textAlign = if (isRtl) TextAlign.Start else TextAlign.Start,
                        textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr
                    ),
                    cursorBrush = SolidColor(AtharPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                stringResource(R.string.quran_search),
                                fontFamily = ThmanyahSans,
                                fontSize = 14.sp,
                                color = AtharTextSecondary,
                                textAlign = if (isRtl) TextAlign.Start else TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        inner()
                    }
                )
            }

            Spacer(Modifier.height(4.dp))

            // Content List (seamless with IslamicPatternBackground)
            if (selectedTab == QuranTabIndex.SURAHS) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        SurahCardItem(
                            surah = surah,
                            colors = colors,
                            isRtl = isRtl,
                            onClick = { onSelectSurah(surah) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp)
                ) {
                    items(filteredJuz, key = { it.number }) { juz ->
                        JuzCardItem(
                            juz = juz,
                            colors = colors,
                            isRtl = isRtl,
                            onClick = {
                                val startSurah = allSurahs.firstOrNull { it.number == juz.startSurahNumber }
                                    ?: allSurahs.first()
                                onSelectSurah(startSurah)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahCardItem(
    surah: SurahMeta,
    colors: QuranReaderColors,
    isRtl: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtharCardSurface)
            .border(1.dp, AtharCardBorder, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number Badge (start side)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(AtharBackground.copy(alpha = 0.65f))
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isRtl) surah.number.toArabicIndic() else "${surah.number}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AtharPrimaryLight
                    )
                }

                // Surah Name (English + Translation)
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = surah.englishName,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AtharTextPrimary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${stringResource(R.string.quran_juz_label, surah.juz)} • " +
                            stringResource(R.string.quran_ayahs, surah.ayahs) + " • " +
                            (if (surah.revelationType == RevelationType.MECCAN)
                                stringResource(R.string.quran_revelation_meccan)
                            else
                                stringResource(R.string.quran_revelation_medinan)),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = AtharTextSecondary
                    )
                }
            }

            // Arabic Calligraphy + Name (end side)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = QuranRepository.getSurahTitleGlyph(surah.number),
                    fontFamily = QuranSurahNames,
                    fontSize = 28.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.End
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = "سورة " + surah.arabicName,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AtharTextSecondary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun JuzCardItem(
    juz: JuzMeta,
    colors: QuranReaderColors,
    isRtl: Boolean,
    onClick: () -> Unit
) {
    val startSurah = remember(juz.startSurahNumber) {
        allSurahs.firstOrNull { it.number == juz.startSurahNumber }
    }

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtharCardSurface)
            .border(1.dp, AtharCardBorder, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(AtharBackground.copy(alpha = 0.65f))
                        .border(1.dp, AtharCardBorder, RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isRtl) juz.number.toArabicIndic() else "${juz.number}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AtharPrimaryLight
                    )
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = juz.englishName,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AtharTextPrimary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = (startSurah?.englishName ?: "") + " • " +
                            stringResource(R.string.quran_page_label, juz.startPage),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = AtharTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = juz.arabicName,
                    fontFamily = ThmanyahSerifDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = AtharTextPrimary,
                    textAlign = TextAlign.End
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "يبدأ من سورة ${startSurah?.arabicName ?: ""}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = AtharTextSecondary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun AyahEndMedallion(
    number: Int,
    fontScale: Float,
    color: Color
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_ayah_end),
            contentDescription = null,
            tint = color,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = number.toArabicIndic(),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = if (number < 10) (10.5f * fontScale).sp
                      else if (number < 100) (9f * fontScale).sp
                      else (7.5f * fontScale).sp,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SurahReader(
    surah: SurahMeta,
    themeMode: QuranThemeMode,
    colors: QuranReaderColors,
    fontScale: Float,
    fontBold: Boolean,
    reciter: QuranReciter,
    onThemeChange: (QuranThemeMode) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onReciterChange: (QuranReciter) -> Unit,
    onToggleBold: () -> Unit,
    onBackToList: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit,
    onNextSurah: () -> Unit
) {
    val context = LocalContext.current
    var verseChunks by remember(surah.number) { mutableStateOf<List<VerseChunk>?>(null) }
    var loadFailed by remember(surah.number) { mutableStateOf(false) }
    var attempt by remember(surah.number) { mutableIntStateOf(0) }

    // Next surah metadata for Netflix-style card
    val nextSurah = remember(surah.number) {
        val idx = allSurahs.indexOfFirst { it.number == surah.number }
        if (idx in 0 until allSurahs.lastIndex) allSurahs[idx + 1] else null
    }

    // Floating panels state
    var showFontPanel by remember { mutableStateOf(false) }
    var showThemePanel by remember { mutableStateOf(false) }
    var showReciterPanel by remember { mutableStateOf(false) }

    // Audio recitation state
    var isPlaying by remember(surah.number) { mutableStateOf(false) }
    var isAudioLoading by remember(surah.number) { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val listState = rememberLazyListState()

    // Release audio player when leaving screen or changing surah
    DisposableEffect(surah.number) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (_: Exception) {}
            isPlaying = false
            isAudioLoading = false
        }
    }

    // Release or reload player when reciter changes
    LaunchedEffect(reciter) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
        isPlaying = false
        isAudioLoading = false
    }

    fun toggleAudio() {
        if (isPlaying) {
            try {
                mediaPlayer?.pause()
                isPlaying = false
            } catch (_: Exception) {}
            return
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.start()
                isPlaying = true
            } catch (_: Exception) {}
            return
        }

        // Initialize and stream recitation from selected reciter (Minshawi / Alafasy)
        isAudioLoading = true
        val surah3Digit = surah.number.toString().padStart(3, '0')
        val audioUrl = "${reciter.baseUrl}/$surah3Digit.mp3"

        val player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener {
                isAudioLoading = false
                isPlaying = true
                it.start()
            }
            setOnCompletionListener {
                isPlaying = false
            }
            setOnErrorListener { _, _, _ ->
                isAudioLoading = false
                isPlaying = false
                true
            }
        }
        mediaPlayer = player

        try {
            player.setDataSource(audioUrl)
            player.prepareAsync()
        } catch (_: Exception) {
            isAudioLoading = false
            isPlaying = false
        }
    }

    // Load surah verse chunks instantly (memory cache -> assets -> network)
    LaunchedEffect(surah.number, attempt) {
        verseChunks = null
        loadFailed = false

        try {
            val chunks = QuranRepository.getSurahVerseChunks(
                context = context,
                number = surah.number,
                chunkSize = 6
            )
            verseChunks = chunks
            loadFailed = chunks.isNullOrEmpty()
            if (!chunks.isNullOrEmpty()) {
                try {
                    listState.scrollToItem(0)
                } catch (_: Exception) {}
            }
        } catch (e: Throwable) {
            android.util.Log.e("QuranScreen", "Error loading surah ${surah.number}", e)
            loadFailed = true
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ─── TOP BAR (Exact match to reference photo media_1789851426749.jpg) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: 3-line list icon button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(colors.circleButtonBg)
                        .border(1.dp, colors.circleButtonBorder, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBackToList
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(colors.circleButtonIcon)
                            )
                        }
                    }
                }

                // Center: Calligraphic Surah Name Pill with ornamental "سورة" prefix (media_1789851426749.jpg)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(colors.pillBg)
                        .border(1.dp, colors.pillBorder, RoundedCornerShape(24.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBackToList
                        )
                        .padding(horizontal = 28.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = QuranRepository.getSurahFullTitleGlyphs(surah.number),
                        fontFamily = QuranSurahNames,
                        fontSize = 28.sp,
                        color = colors.pillText,
                        textAlign = TextAlign.Center
                    )
                }

                // Right: Next surah arrow button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(colors.circleButtonBg)
                        .border(1.dp, colors.circleButtonBorder, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onNextSurah
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Next Surah",
                        tint = colors.circleButtonIcon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ─── READER BODY ───
            when {
                verseChunks != null -> {
                    val chunks = verseChunks!!
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 22.dp,
                            end = 22.dp,
                            top = 16.dp,
                            bottom = 130.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Calligraphic Basmala ligature for surahs other than 1 and 9
                        if (surah.number != 1 && surah.number != 9) {
                            item(key = "basmala_${surah.number}") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, bottom = 26.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_bismillah),
                                        contentDescription = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        tint = colors.text,
                                        modifier = Modifier
                                            .fillMaxWidth(0.95f)
                                            .height(52.dp)
                                    )
                                }
                            }
                        }

                        // Chunked verses with authentic single Ayah rosette medallions (InlineTextContent)
                        items(chunks, key = { "chunk_${surah.number}_${it.chunkIndex}" }) { chunk ->
                            val sizeSp = (26 * fontScale).sp
                            val inlineContent = remember(chunk, fontScale, colors.ayahMarker) {
                                chunk.verses.associate { verse ->
                                    "ayah_${verse.number}" to InlineTextContent(
                                        Placeholder(
                                            width = sizeSp,
                                            height = sizeSp,
                                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                        )
                                    ) {
                                        AyahEndMedallion(
                                            number = verse.number,
                                            fontScale = fontScale,
                                            color = colors.ayahMarker
                                        )
                                    }
                                }
                            }

                            val annotated = remember(chunk) {
                                buildAnnotatedString {
                                    for (i in chunk.verses.indices) {
                                        val verse = chunk.verses[i]
                                        append(verse.text)
                                        append("\u00A0")
                                        appendInlineContent("ayah_${verse.number}", " (${verse.number}) ")
                                        if (i < chunk.verses.lastIndex) {
                                            append(" ")
                                        }
                                    }
                                }
                            }

                            Text(
                                text = annotated,
                                inlineContent = inlineContent,
                                fontFamily = QuranUthmanicHafs,
                                fontWeight = if (fontBold) FontWeight.Bold else FontWeight.Normal,
                                fontSize = (23 * fontScale).sp,
                                lineHeight = (44 * fontScale).sp,
                                color = colors.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        // Surah Divider (matches reference photo: ─── 1 ───)
                        item(key = "divider_${surah.number}") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 28.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(0.8.dp)
                                        .background(colors.dividerLine)
                                )
                                Text(
                                    text = "  ${surah.number}  ",
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = colors.dividerText
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(0.8.dp)
                                        .background(colors.dividerLine)
                                )
                            }
                        }

                        // Netflix-Style Next Surah Card
                        if (nextSurah != null) {
                            item(key = "next_card_${nextSurah.number}") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 12.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(colors.cardBg)
                                        .border(1.dp, colors.cardBorder, RoundedCornerShape(22.dp))
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = null,
                                            tint = AtharPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.quran_next_surah_title),
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = AtharPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Spacer(Modifier.height(14.dp))

                                    // Calligraphic pill of next surah
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(colors.pillBg)
                                            .border(1.dp, colors.pillBorder, RoundedCornerShape(24.dp))
                                            .padding(horizontal = 24.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = QuranRepository.getSurahFullTitleGlyphs(nextSurah.number),
                                            fontFamily = QuranSurahNames,
                                            fontSize = 26.sp,
                                            color = colors.pillText,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    // Details subtitle
                                    Text(
                                        text = "${nextSurah.englishName} • ${stringResource(R.string.quran_ayahs, nextSurah.ayahs)} • " +
                                            (if (nextSurah.revelationType == RevelationType.MECCAN)
                                                stringResource(R.string.quran_revelation_meccan)
                                            else
                                                stringResource(R.string.quran_revelation_medinan)),
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = colors.dividerText,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(Modifier.height(18.dp))

                                    // Action button (Netflix-style next button)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(AtharPrimary)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = { onSelectSurah(nextSurah) }
                                            )
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.quran_go_to_next, nextSurah.arabicName),
                                                fontFamily = ThmanyahSans,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.5.sp,
                                                color = Color.Black
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                loadFailed -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(R.string.quran_offline),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = colors.dividerText,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(AtharPrimary)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { attempt++ }
                                    )
                                    .padding(horizontal = 28.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.quran_retry),
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = AtharPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                stringResource(R.string.quran_loading),
                                fontFamily = ThmanyahSans,
                                fontSize = 13.sp,
                                color = colors.dividerText
                            )
                        }
                    }
                }
            }
        }

        // ─── FLOATING BOTTOM CONTROLS (Exact match to reference photo) ───
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Font panel popup
            AnimatedVisibility(
                visible = showFontPanel,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.floatingPillBg)
                        .border(1.dp, colors.floatingPillBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(220.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.quran_font_size),
                                fontFamily = ThmanyahSans,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.text
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (fontBold) colors.floatingPillActiveIcon.copy(alpha = 0.2f) else colors.floatingPillItemBg)
                                    .border(
                                        1.dp,
                                        if (fontBold) colors.floatingPillActiveIcon else colors.floatingPillBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(onClick = onToggleBold)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    stringResource(R.string.quran_font_bold),
                                    fontFamily = ThmanyahSans,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (fontBold) colors.floatingPillActiveIcon else colors.floatingPillItemIcon
                                )
                            }
                        }
                        Slider(
                            value = fontScale,
                            onValueChange = onFontScaleChange,
                            valueRange = 0.75f..1.5f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = if (colors.isLight) colors.text else AtharPrimary,
                                activeTrackColor = if (colors.isLight) colors.text else AtharPrimary,
                                inactiveTrackColor = colors.dividerLine
                            )
                        )
                    }
                }
            }

            // Theme switch popup
            AnimatedVisibility(
                visible = showThemePanel,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.floatingPillBg)
                        .border(1.dp, colors.floatingPillBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuranThemeMode.entries.forEach { mode ->
                            val selected = themeMode == mode
                            val label = when (mode) {
                                QuranThemeMode.AMOLED -> stringResource(R.string.quran_theme_amoled)
                                QuranThemeMode.DARK_OLIVE -> stringResource(R.string.quran_theme_olive)
                                QuranThemeMode.LIGHT -> stringResource(R.string.quran_theme_light)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) colors.floatingPillActiveIcon.copy(alpha = 0.2f)
                                        else colors.floatingPillItemBg
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) colors.floatingPillActiveIcon else colors.floatingPillBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onThemeChange(mode)
                                        showThemePanel = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selected) {
                                        Icon(
                                            Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = colors.floatingPillActiveIcon,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = label,
                                        fontFamily = ThmanyahSans,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) colors.floatingPillActiveIcon else colors.floatingPillItemIcon
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reciter Selection Popup Panel
            AnimatedVisibility(
                visible = showReciterPanel,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.floatingPillBg)
                        .border(1.dp, colors.floatingPillBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.width(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quran_reciter_title),
                            fontFamily = ThmanyahSans,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.dividerText,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        QuranReciter.entries.forEach { r ->
                            val selected = reciter == r
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) colors.floatingPillActiveIcon.copy(alpha = 0.2f)
                                        else colors.floatingPillItemBg
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) colors.floatingPillActiveIcon else colors.floatingPillBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onReciterChange(r)
                                        showReciterPanel = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = r.arabicName,
                                            fontFamily = ThmanyahSans,
                                            fontSize = 13.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected) colors.floatingPillActiveIcon else colors.floatingPillItemIcon
                                        )
                                        Text(
                                            text = r.englishName,
                                            fontFamily = ThmanyahSans,
                                            fontSize = 10.5.sp,
                                            color = colors.dividerText
                                        )
                                    }
                                    if (selected) {
                                        Spacer(Modifier.width(8.dp))
                                        Icon(
                                            Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = colors.floatingPillActiveIcon,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Capsule Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.floatingPillBg)
                    .border(1.dp, colors.floatingPillBorder, RoundedCornerShape(28.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // TT (Font size and bold)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (showFontPanel) colors.floatingPillActiveIcon.copy(alpha = 0.2f) else colors.floatingPillItemBg)
                            .clickable {
                                showFontPanel = !showFontPanel
                                showThemePanel = false
                                showReciterPanel = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TT",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (showFontPanel) colors.floatingPillActiveIcon else colors.floatingPillItemIcon
                        )
                    }

                    // Palette (Theme mode)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (showThemePanel) colors.floatingPillActiveIcon.copy(alpha = 0.2f) else colors.floatingPillItemBg)
                            .clickable {
                                showThemePanel = !showThemePanel
                                showFontPanel = false
                                showReciterPanel = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Palette,
                            contentDescription = "Theme",
                            tint = if (showThemePanel) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Reciter (Sheikh Al-Minshawi / Sheikh Alafasy)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (showReciterPanel) colors.floatingPillActiveIcon.copy(alpha = 0.2f) else colors.floatingPillItemBg)
                            .clickable {
                                showReciterPanel = !showReciterPanel
                                showFontPanel = false
                                showThemePanel = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.GraphicEq,
                            contentDescription = stringResource(R.string.quran_reciter_title),
                            tint = if (showReciterPanel) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Play / Pause Recitation
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colors.floatingPillItemBg)
                            .clickable {
                                showFontPanel = false
                                showThemePanel = false
                                showReciterPanel = false
                                toggleAudio()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isAudioLoading -> {
                                CircularProgressIndicator(
                                    color = colors.floatingPillActiveIcon,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            isPlaying -> {
                                Icon(
                                    Icons.Rounded.Pause,
                                    contentDescription = "Pause",
                                    tint = colors.floatingPillActiveIcon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    Icons.Rounded.PlayArrow,
                                    contentDescription = "Play",
                                    tint = colors.floatingPillItemIcon,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

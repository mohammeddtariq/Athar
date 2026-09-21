package com.athar.app.ui.corner

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.graphics.Picture
import android.util.LruCache
import androidx.activity.compose.BackHandler
import java.util.Locale
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.QuranPageChunk
import com.athar.app.data.QuranPages
import com.athar.app.data.QuranReciter
import com.athar.app.data.QuranRepository
import com.athar.app.data.QuranRepository.toArabicIndic
import com.athar.app.data.QuranThemeMode
import com.athar.app.data.QuranLayoutMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.athar.app.data.VerseChunk
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharNavGlow
import com.athar.app.ui.theme.AtharNavbarBg
import com.athar.app.ui.theme.AtharNavbarBorder
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
    val surahHeaderBorder: Color,
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
    floatingPillBg = Color(0xFF161815),
    floatingPillBorder = Color(0xFF2C3227),
    floatingPillItemBg = Color(0xFF232720),
    floatingPillItemIcon = Color(0xFFEDEFEA),
    floatingPillActiveIcon = AtharPrimary,
    cardBg = Color(0xFF141712),
    cardBorder = Color(0xFF2A3026),
    searchBg = Color(0xFF101310),
    searchBorder = Color(0xFF232820),
    surahHeaderBorder = Color(0xFF727A71),
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
    floatingPillBg = AtharNavbarBg,
    floatingPillBorder = AtharNavbarBorder,
    floatingPillItemBg = Color(0xFF1D221A),
    floatingPillItemIcon = Color(0xFFEDEFEA),
    floatingPillActiveIcon = AtharPrimary,
    cardBg = Color(0xFF121510),
    cardBorder = Color(0xFF22281D),
    searchBg = Color(0xFF0F120D),
    searchBorder = Color(0xFF1E241A),
    surahHeaderBorder = Color(0xFF5D7B54),
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
    floatingPillBg = Color(0xFFEFECE4),
    floatingPillBorder = Color(0xFFDFD9CC),
    floatingPillItemBg = Color(0xFFDFD9CC),
    floatingPillItemIcon = Color(0xFF1A1D18),
    floatingPillActiveIcon = Color(0xFF2D4B26),
    cardBg = Color(0xFFF4F0E6),
    cardBorder = Color(0xFFDED8C9),
    searchBg = Color(0xFFF2EEE4),
    searchBorder = Color(0xFFDDD7C8),
    surahHeaderBorder = Color(0xFF8B7355),
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
    val savedLayoutMode by appPrefs.quranLayoutMode.collectAsState(initial = QuranLayoutMode.TEXT)

    var themeMode by remember(savedThemeMode) { mutableStateOf(savedThemeMode) }
    var fontScale by remember(savedFontScale) { mutableFloatStateOf(savedFontScale) }
    var reciter by remember(savedReciter) { mutableStateOf(savedReciter) }
    var fontBold by remember { mutableStateOf(false) }
    var layoutMode by remember(savedLayoutMode) { mutableStateOf(savedLayoutMode) }

    var openSurah by remember { mutableStateOf<SurahMeta?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(QuranTabIndex.SURAHS) }

    var lastBackTime by remember { mutableLongStateOf(0L) }
    var showDoubleBackToast by remember { mutableStateOf(false) }

    LaunchedEffect(showDoubleBackToast) {
        if (showDoubleBackToast) {
            kotlinx.coroutines.delay(2000)
            showDoubleBackToast = false
        }
    }

    // Intercept system back gestures when inside a Surah:
    // First swipe shows confirmation toast, second swipe within 2s returns to Quran main tab screen
    BackHandler(enabled = openSurah != null) {
        val now = System.currentTimeMillis()
        if (now - lastBackTime < 2000L) {
            showDoubleBackToast = false
            openSurah = null
        } else {
            lastBackTime = now
            showDoubleBackToast = true
        }
    }

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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (currentSurah != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                SurahReader(
                    surah = currentSurah,
                    themeMode = themeMode,
                    colors = colors,
                    fontScale = fontScale,
                    fontBold = fontBold,
                    reciter = reciter,
                    layoutMode = layoutMode,
                    onLayoutModeChange = { newMode ->
                        layoutMode = newMode
                        scope.launch { appPrefs.setQuranLayoutMode(newMode) }
                    },
                    onThemeChange = { newMode ->
                        themeMode = newMode
                        scope.launch { appPrefs.setQuranThemeMode(newMode) }
                    },
                    onFontScaleChange = { newScale ->
                        val clamped = newScale.coerceIn(0.70f, 2.0f)
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

                // Double back confirmation floating popup
                AnimatedVisibility(
                    visible = showDoubleBackToast,
                    enter = fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 2 },
                    exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 2 },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 95.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (colors.isLight) Color(0xFF1E241A) else Color(0xFF161A14))
                            .border(1.dp, AtharPrimary.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quran_back_press_again),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
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
    }
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
            // Top Bar (Matches Qibla screen: clean, uncircled, 22.sp FontWeight.Black)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
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
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    stringResource(R.string.quran_title),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = AtharTextPrimary
                )

                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(44.dp))
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
        // Enforce constant LTR order across all app languages:
        // Left = Number Badge + English Subtitles
        // Right = Arabic Calligraphy + Uthmanic Name
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Surah Number Badge & English Name (Left side)
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
                            text = surah.number.toArabicIndic(),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AtharPrimaryLight
                        )
                    }

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

                // Arabic Calligraphy + Unified Uthmanic Name (Right side)
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
}

@Composable
private fun JuzCardItem(
    juz: JuzMeta,
    colors: QuranReaderColors,
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
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Badge & English Name
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
                            text = juz.number.toArabicIndic(),
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

                // Right: Arabic Name
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = juz.arabicName,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
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
            fontSize = if (number < 10) (9.5f * fontScale).sp
                      else if (number < 100) (8f * fontScale).sp
                      else (6.5f * fontScale).sp,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuranPageDivider(
    pageNumber: Int,
    colors: QuranReaderColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.8.dp)
                .background(colors.dividerLine)
        )
        Text(
            text = "  —  ${pageNumber.toArabicIndic()}  —  ",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
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

@Composable
private fun SurahHeaderBanner(
    surahNumber: Int,
    colors: QuranReaderColors,
    themeMode: QuranThemeMode = QuranThemeMode.AMOLED,
    modifier: Modifier = Modifier
) {
    // The frame supplied with the Mushaf artwork is deliberately a compact,
    // self-contained heading. Letting it fill the reader width distorts both the
    // ornament and the calligraphy, especially on large phones.
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val bannerWidth = minOf(maxWidth - 24.dp, 300.dp)
        Box(
            modifier = Modifier
                .width(bannerWidth)
                .aspectRatio(687f / 79f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_surah_banner_frame),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    when {
                        colors.isLight -> Color(0xFF6E553F)
                        themeMode == QuranThemeMode.DARK_OLIVE -> Color(0xFF8E9B86)
                        else -> Color(0xFFC8CEC6)
                    }
                ),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = QuranRepository.getSurahFullTitleGlyphs(surahNumber),
                fontFamily = QuranSurahNames,
                fontWeight = FontWeight.Normal,
                fontSize = 21.sp,
                lineHeight = 21.sp,
                color = if (colors.isLight) Color(0xFF1A1D18) else Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 72.dp)
                    .offset(y = (-1).dp)
            )
        }
    }
}

private data class MushafPageRenderData(
    val picture: Picture,
    val surahHeaderYFractions: List<Float>
)

private val mushafPageCache = LruCache<String, MushafPageRenderData>(24)

/**
 * Renders a single Ligature Basd Mushaf page exactly as supplied by the
 * bundled SVG layout, using AndroidSvg for native Canvas rendering.
 *
 * The SVG paths already include the Uthmani glyph shapes, surah headers,
 * Bismillah, ayah medallions, line breaks and page spacing — no WebView needed.
 */
@Composable
private fun LigatureMushafPage(
    pageNumber: Int,
    colors: QuranReaderColors,
    themeMode: QuranThemeMode,
    fontScale: Float,
    fontBold: Boolean,
    showSurahFrame: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pageFileName = remember(pageNumber) { pageNumber.toString().padStart(3, '0') + ".svg" }

    // Ink and marker colors based on theme
    val ink = if (colors.isLight) "#1A1D18" else "#F7F8F5"
    val markerInk = when (themeMode) {
        QuranThemeMode.AMOLED -> "#B4BCB0"
        QuranThemeMode.DARK_OLIVE -> "#8E9B86"
        QuranThemeMode.LIGHT -> "#4E5846"
    }
    val strokeWidth = if (fontBold) "0.24" else "0"

    var isError by remember(pageFileName, ink, markerInk, strokeWidth) { mutableStateOf(false) }

    val renderData by produceState<MushafPageRenderData?>(initialValue = null, pageFileName, ink, markerInk, strokeWidth) {
        value = withContext(Dispatchers.IO) {
            try {
                isError = false
                val cacheKey = "${pageFileName}_${ink}_${markerInk}_${strokeWidth}"
                val cached = mushafPageCache.get(cacheKey)
                if (cached != null) return@withContext cached

                val rawSvg = context.assets.open("mushaf/$pageFileName").bufferedReader().use { it.readText() }
                val cleanSvg = rawSvg.trimStart('\uFEFF')
                    .replace("""id="md-page-outer"""", """id="md-page-outer" display="none" visibility="hidden"""")

                // Find Y centers of any surah title groups on this page
                val headerYFractions = mutableListOf<Float>()
                val surahGroups = Regex("""<g\s+id="[^"]+"[^>]*data-type="surah-name">(.*?)</g>\s*</g>""", RegexOption.DOT_MATCHES_ALL)
                    .findAll(cleanSvg)
                for (match in surahGroups) {
                    val body = match.groupValues[1]
                    val ys = Regex("""M\s*[-+]?\d*\.?\d+[\s,]+([-+]?\d*\.?\d+)""").findAll(body)
                        .mapNotNull { it.groupValues[1].toFloatOrNull() }
                        .toList()
                    if (ys.isNotEmpty()) {
                        val avgY = ys.average().toFloat()
                        headerYFractions.add(avgY / 547.09f)
                    }
                }

                val cssBlock = """
                    <style>
                        path { fill: $ink; stroke: $ink; stroke-width: $strokeWidth; stroke-linejoin: round; }
                        [data-type="aya-mark"] path { fill: $markerInk; stroke: $markerInk; }
                        #md-page-outer { display: none; visibility: hidden; }
                        #md-page-outer path { display: none; visibility: hidden; }
                    </style>
                """.trimIndent()

                val svgStart = cleanSvg.indexOf("<svg")
                val styledSvg = if (svgStart != -1) {
                    val svgTagEnd = cleanSvg.indexOf('>', svgStart)
                    if (svgTagEnd != -1) {
                        cleanSvg.substring(0, svgTagEnd + 1) + "\n" + cssBlock + "\n" + cleanSvg.substring(svgTagEnd + 1)
                    } else cleanSvg
                } else cleanSvg

                val svg = com.caverock.androidsvg.SVG.getFromString(styledSvg)
                svg.documentWidth = 382.68f
                svg.documentHeight = 547.09f
                val pic = svg.renderToPicture()

                if (pic != null) {
                    val data = MushafPageRenderData(pic, headerYFractions)
                    mushafPageCache.put(cacheKey, data)
                    data
                } else {
                    isError = true
                    null
                }
            } catch (e: Throwable) {
                android.util.Log.e("LigatureMushafPage", "Error rendering SVG page $pageFileName", e)
                isError = true
                null
            }
        }
    }

    val pageScale = fontScale.coerceIn(0.70f, 2.0f)
    val horizontalScrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val containerWidth = maxWidth
        val scaledWidth = containerWidth * pageScale
        val scaledHeight = scaledWidth * (547.09f / 382.68f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (pageScale > 1.05f) Modifier.horizontalScroll(horizontalScrollState) else Modifier),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .width(scaledWidth)
                    .height(scaledHeight),
                contentAlignment = Alignment.TopCenter
            ) {
                val data = renderData
                if (data != null) {
                    // 1. Medina Mushaf Vector Page (SVG drawn directly on Canvas via hardware-accelerated Picture)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawIntoCanvas { canvas ->
                            val scaleX = size.width / 382.68f
                            val scaleY = size.height / 547.09f
                            canvas.nativeCanvas.save()
                            canvas.nativeCanvas.scale(scaleX, scaleY)
                            canvas.nativeCanvas.drawPicture(data.picture)
                            canvas.nativeCanvas.restore()
                        }
                    }

                    // 2. Ornate Surah Header Cartouche Frame(s) mathematically centered on surah title(s)
                    for (yFraction in data.surahHeaderYFractions) {
                        val frameCenterY = scaledHeight * yFraction
                        val frameWidth = scaledWidth * (314f / 382.68f)
                        val frameHeight = frameWidth * (79f / 687f)
                        val frameTop = frameCenterY - (frameHeight / 2f)

                        Image(
                            painter = painterResource(R.drawable.ic_surah_banner_frame),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colors.surahHeaderBorder),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(frameWidth)
                                .height(frameHeight)
                                .offset(y = frameTop)
                        )
                    }
                } else if (isError) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تعذر تحميل الصفحة $pageNumber",
                            fontFamily = ThmanyahSans,
                            fontSize = 12.sp,
                            color = colors.dividerText
                        )
                    }
                } else {
                    // Loading placeholder with identical aspect ratio to prevent layout jump
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colors.ayahMarker,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
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
    layoutMode: QuranLayoutMode,
    onLayoutModeChange: (QuranLayoutMode) -> Unit,
    onThemeChange: (QuranThemeMode) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onReciterChange: (QuranReciter) -> Unit,
    onToggleBold: () -> Unit,
    onBackToList: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit,
    onNextSurah: () -> Unit
)  {
    val context = LocalContext.current
    var pageChunks by remember(surah.number) { mutableStateOf<List<QuranPageChunk>?>(null) }
    var loadFailed by remember(surah.number) { mutableStateOf(false) }
    var attempt by remember(surah.number) { mutableIntStateOf(0) }

    // Next surah metadata for Netflix-style card
    val nextSurah = remember(surah.number) {
        val idx = allSurahs.indexOfFirst { it.number == surah.number }
        if (idx in 0 until allSurahs.lastIndex) allSurahs[idx + 1] else null
    }

    val isArabic = remember { Locale.getDefault().language == "ar" }
    val surahDisplayName = remember(surah.number, isArabic) {
        if (isArabic) "سورة ${surah.arabicName}" else "Surah ${surah.englishName}"
    }

    // Floating panels state
    var showFontPanel by remember { mutableStateOf(false) }
    var showThemePanel by remember { mutableStateOf(false) }
    var showReciterPanel by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var dropdownQuery by remember { mutableStateOf("") }
    var dropdownTab by remember { mutableStateOf(QuranTabIndex.SURAHS) }

    // Audio recitation state
    var isPlaying by remember(surah.number) { mutableStateOf(false) }
    var isAudioLoading by remember(surah.number) { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    val listState = rememberLazyListState()

    // Scroll state & scroll-aware floating bars
    var areBarsVisible by remember { mutableStateOf(true) }
    var showControlsHint by rememberSaveable { mutableStateOf(true) }
    var showIndexHint by rememberSaveable { mutableStateOf(true) }
    var showNavHint by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(surah.number) {
        kotlinx.coroutines.delay(6500)
        showControlsHint = false
        showIndexHint = false
        showNavHint = false
    }

    var showJuzInTopBar by remember(surah.number) { mutableStateOf(false) }
    LaunchedEffect(surah.number) {
        while (true) {
            kotlinx.coroutines.delay(3500)
            showJuzInTopBar = !showJuzInTopBar
        }
    }

    // Pinch-to-zoom: dynamically scales font size with two-finger gesture
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        if (zoomChange != 1f) {
            val newScale = (fontScale * zoomChange).coerceIn(0.70f, 2.0f)
            onFontScaleChange(newScale)
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val dy = available.y
                if (dy < -6f) {
                    areBarsVisible = false
                    showFontPanel = false
                    showThemePanel = false
                    showReciterPanel = false
                    showDropdownMenu = false
                    showIndexHint = false
                    showNavHint = false
                    showControlsHint = false
                } else if (dy > 6f) {
                    areBarsVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val isNearTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset <= 30
        }
    }
    val shouldShowBars = areBarsVisible || isNearTop

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

    // Load surah verses chunked by authentic Mushaf pages
    LaunchedEffect(surah.number, attempt) {
        pageChunks = null
        loadFailed = false

        try {
            val chunks = QuranRepository.getSurahPageChunks(
                context = context,
                number = surah.number
            )
            pageChunks = chunks
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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .nestedScroll(nestedScrollConnection)
                .transformable(state = transformableState)
        ) {
            // ─── READER BODY (Verses scroll underneath the floating top bar) ───
            when {
                pageChunks != null -> {
                    val chunks = pageChunks ?: emptyList()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                areBarsVisible = !areBarsVisible
                            },
                        contentPadding = PaddingValues(
                            start = 14.dp,
                            end = 14.dp,
                            top = 95.dp,
                            bottom = 130.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (layoutMode == QuranLayoutMode.TEXT) {
                            // ─── DESIGN 1 (DEFAULT): Traditional Uthmanic Text Flow ───
                            // Bismillah header (all surahs except Surah 9 At-Tawbah)
                            if (surah.number != 9) {
                                item(key = "bismillah_${surah.number}") {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "\uFDFD", // ﷽ authentic sweeping calligraphy from bismillah.ttf
                                            fontFamily = QuranBismillah,
                                            fontWeight = if (fontBold) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = (42 * fontScale).sp,
                                            color = if (colors.isLight) colors.text.copy(alpha = 0.85f) else Color(0xFFCBD2C8),
                                            style = TextStyle(
                                                shadow = if (fontBold) Shadow(
                                                    color = (if (colors.isLight) colors.text.copy(alpha = 0.85f) else Color(0xFFCBD2C8)).copy(alpha = 0.5f),
                                                    offset = Offset(0.35f, 0.35f),
                                                    blurRadius = 0.5f
                                                ) else null
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        // Delicate divider line with center dot
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 48.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(0.6.dp)
                                                    .background(colors.dividerLine.copy(alpha = 0.6f))
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 10.dp)
                                                    .size(3.5.dp)
                                                    .clip(CircleShape)
                                                    .background(colors.dividerText.copy(alpha = 0.5f))
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(0.6.dp)
                                                    .background(colors.dividerLine.copy(alpha = 0.6f))
                                            )
                                        }
                                    }
                                }
                            }

                            // Each chunk represents one authentic Madani Mushaf Page
                            items(chunks, key = { "text_page_${surah.number}_${it.pageNumber}" }) { chunk ->
                                val sizeSp = (22 * fontScale).sp
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
                                            append("\u202F")
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
                                    fontSize = (22 * fontScale).sp,
                                    lineHeight = (42 * fontScale).sp,
                                    color = colors.text,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        textDirection = TextDirection.Rtl,
                                        shadow = if (fontBold) Shadow(
                                            color = colors.text.copy(alpha = 0.5f),
                                            offset = Offset(0.35f, 0.35f),
                                            blurRadius = 0.5f
                                        ) else null
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = 520.dp)
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                )

                                // Page Divider (indicates this page ended in the Mushaf — Arabic-Indic numerals only)
                                QuranPageDivider(
                                    pageNumber = chunk.pageNumber,
                                    colors = colors
                                )
                            }
                        } else {
                            // ─── DESIGN 2 (BETA): Vector Mushaf Pages ───
                            items(chunks, key = { "svg_page_${surah.number}_${it.pageNumber}" }) { chunk ->
                                LigatureMushafPage(
                                    pageNumber = chunk.pageNumber,
                                    colors = colors,
                                    themeMode = themeMode,
                                    fontScale = fontScale,
                                    fontBold = fontBold,
                                    showSurahFrame = chunk.verses.firstOrNull()?.number == 1,
                                    modifier = Modifier
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

                                    // Surah name pill in wide bold font
                                    val nextSurahDisplayName = if (isArabic) "سورة ${nextSurah.arabicName}" else "Surah ${nextSurah.englishName}"
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(colors.pillBg)
                                            .border(
                                                1.dp,
                                                if (colors.isLight) AtharPrimary.copy(alpha = 0.35f) else AtharPrimaryLight.copy(alpha = 0.35f),
                                                RoundedCornerShape(24.dp)
                                            )
                                            .padding(horizontal = 22.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = nextSurahDisplayName,
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = colors.pillText
                                        )
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    // Details subtitle
                                    val nextSurahSubName = if (isArabic) "سورة ${nextSurah.arabicName}" else nextSurah.englishName
                                    Text(
                                        text = "$nextSurahSubName • ${stringResource(R.string.quran_ayahs, nextSurah.ayahs)} • " +
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

                                    // Action button (Next button)
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
                                            val nextSurahName = if (isArabic) nextSurah.arabicName else nextSurah.englishName
                                            Text(
                                                text = stringResource(R.string.quran_go_to_next, nextSurahName),
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
                            .fillMaxSize()
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
                        modifier = Modifier.fillMaxSize(),
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

            // ─── FLOATING TOP BAR (Frosted gradient background, hides on scroll down) ───
            AnimatedVisibility(
                visible = shouldShowBars,
                enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { -it },
                exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    colors.background,
                                    colors.background.copy(alpha = 0.96f),
                                    colors.background.copy(alpha = 0.85f),
                                    Color.Transparent
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val hasPrevious = surah.number > 1
                        val hasNext = surah.number < 114

                        // 100% Mathematically Centered Top Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 1. Top Left: Previous Surah Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (hasPrevious) colors.circleButtonBg else colors.circleButtonBg.copy(alpha = 0.35f))
                                    .border(
                                        1.dp,
                                        if (hasPrevious) (if (colors.isLight) AtharPrimary.copy(alpha = 0.35f) else AtharPrimaryLight.copy(alpha = 0.35f))
                                        else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable(
                                        enabled = hasPrevious,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            showNavHint = false
                                            if (hasPrevious) {
                                                onSelectSurah(allSurahs[surah.number - 2])
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.quran_previous_surah),
                                    tint = if (hasPrevious) (if (colors.isLight) AtharPrimary else AtharPrimaryLight)
                                           else colors.dividerText.copy(alpha = 0.35f),
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // 2. Center: Surah & Juz Name pill (alternating animation, tapping opens Quran Index dropdown)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(19.dp))
                                    .background(colors.circleButtonBg)
                                    .border(
                                        1.dp,
                                        if (colors.isLight) AtharPrimary.copy(alpha = 0.35f) else AtharPrimaryLight.copy(alpha = 0.35f),
                                        RoundedCornerShape(19.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            showIndexHint = false
                                            showDropdownMenu = !showDropdownMenu
                                        }
                                    )
                                    .padding(horizontal = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(
                                    targetState = showJuzInTopBar,
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(380)) + slideInVertically(animationSpec = tween(380)) { height -> height / 2 })
                                            .togetherWith(fadeOut(animationSpec = tween(280)) + slideOutVertically(animationSpec = tween(280)) { height -> -height / 2 })
                                    },
                                    label = "TopBarSurahJuzPill"
                                ) { isJuz ->
                                    val textToShow = if (isJuz) {
                                        if (isArabic) {
                                            val j = allJuz.firstOrNull { it.number == surah.juz }
                                            val jName = j?.arabicName?.substringBefore(" (") ?: "الجزء ${surah.juz.toArabicIndic()}"
                                            jName
                                        } else {
                                            "Juz ${surah.juz}"
                                        }
                                    } else {
                                        if (isArabic) "سُورَةُ ${surah.arabicName}" else "Surah ${surah.englishName}"
                                    }

                                    Text(
                                        text = textToShow,
                                        fontFamily = QuranUthmanicHafs,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = colors.pillText,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }

                            // 3. Top Right: Next Surah Button (replaces menu button)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (hasNext) colors.circleButtonBg else colors.circleButtonBg.copy(alpha = 0.35f))
                                    .border(
                                        1.dp,
                                        if (hasNext) (if (colors.isLight) AtharPrimary.copy(alpha = 0.35f) else AtharPrimaryLight.copy(alpha = 0.35f))
                                        else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable(
                                        enabled = hasNext,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            showNavHint = false
                                            if (hasNext) {
                                                onSelectSurah(allSurahs[surah.number])
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = stringResource(R.string.quran_next_surah),
                                    tint = if (hasNext) (if (colors.isLight) AtharPrimary else AtharPrimaryLight)
                                           else colors.dividerText.copy(alpha = 0.35f),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        // Floating Hints Row underneath top bar if visible
                        if (showIndexHint || showNavHint) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                            ) {
                                if (showNavHint && hasPrevious) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (colors.isLight) AtharPrimary else AtharPrimaryLight)
                                            .clickable { showNavHint = false }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_previous_surah),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (colors.isLight) Color.White else Color.Black
                                        )
                                    }
                                }

                                if (showIndexHint) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (colors.isLight) AtharPrimary else AtharPrimaryLight)
                                            .clickable { showIndexHint = false }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_index_hint),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (colors.isLight) Color.White else Color.Black
                                        )
                                    }
                                }

                                if (showNavHint && hasNext) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (colors.isLight) AtharPrimary else AtharPrimaryLight)
                                            .clickable { showNavHint = false }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_next_surah),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (colors.isLight) Color.White else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ─── IN-READER DROPDOWN QURAN MENU OVERLAY ───
            AnimatedVisibility(
                visible = showDropdownMenu,
                enter = fadeIn(tween(220)) + slideInVertically(spring(dampingRatio = 0.85f, stiffness = 380f)) { -it / 3 },
                exit = fadeOut(tween(180)) + slideOutVertically(spring(dampingRatio = 0.95f, stiffness = 420f)) { -it / 3 },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showDropdownMenu = false }
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.82f)
                            .clip(RoundedCornerShape(26.dp))
                            .background(colors.cardBg)
                            .border(1.2.dp, colors.cardBorder, RoundedCornerShape(26.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Menu Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.quran_menu_title),
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = colors.text
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Main Index Button
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(colors.pillBg)
                                            .border(1.dp, colors.pillBorder, RoundedCornerShape(14.dp))
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = {
                                                    showDropdownMenu = false
                                                    onBackToList()
                                                }
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_menu_full_index),
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = colors.floatingPillActiveIcon
                                        )
                                    }

                                    // Close button
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(colors.pillBg)
                                            .border(1.dp, colors.pillBorder, CircleShape)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = { showDropdownMenu = false }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Rounded.Close,
                                            contentDescription = "Close",
                                            tint = colors.text,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Search bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colors.searchBg)
                                    .border(1.dp, colors.searchBorder, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                BasicTextField(
                                    value = dropdownQuery,
                                    onValueChange = { dropdownQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.5.sp,
                                        color = colors.text,
                                        textAlign = TextAlign.Start,
                                        textDirection = TextDirection.ContentOrRtl
                                    ),
                                    cursorBrush = SolidColor(AtharPrimary),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { inner ->
                                        if (dropdownQuery.isEmpty()) {
                                            Text(
                                                stringResource(R.string.quran_search),
                                                fontFamily = ThmanyahSans,
                                                fontSize = 13.sp,
                                                color = colors.dividerText,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        inner()
                                    }
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // Dual Tabs inside Dropdown: Surahs / Juz
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.searchBg)
                                    .border(1.dp, colors.searchBorder, RoundedCornerShape(14.dp))
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val sActive = dropdownTab == QuranTabIndex.SURAHS
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (sActive) colors.floatingPillActiveIcon.copy(alpha = 0.22f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (sActive) colors.floatingPillActiveIcon.copy(alpha = 0.6f) else Color.Transparent,
                                            RoundedCornerShape(11.dp)
                                        )
                                        .clickable { dropdownTab = QuranTabIndex.SURAHS }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.quran_tab_surahs) + " (114)",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = if (sActive) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.5.sp,
                                        color = if (sActive) colors.floatingPillActiveIcon else colors.dividerText
                                    )
                                }

                                val jActive = dropdownTab == QuranTabIndex.JUZ
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (jActive) colors.floatingPillActiveIcon.copy(alpha = 0.22f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (jActive) colors.floatingPillActiveIcon.copy(alpha = 0.6f) else Color.Transparent,
                                            RoundedCornerShape(11.dp)
                                        )
                                        .clickable { dropdownTab = QuranTabIndex.JUZ }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.quran_tab_juz) + " (30)",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = if (jActive) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.5.sp,
                                        color = if (jActive) colors.floatingPillActiveIcon else colors.dividerText
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Filtered items
                            val menuFilteredSurahs = remember(dropdownQuery) {
                                if (dropdownQuery.isBlank()) allSurahs
                                else allSurahs.filter {
                                    it.arabicName.contains(dropdownQuery.trim()) ||
                                        it.englishName.contains(dropdownQuery.trim(), ignoreCase = true) ||
                                        it.number.toString() == dropdownQuery.trim()
                                }
                            }
                            val menuFilteredJuz = remember(dropdownQuery) {
                                if (dropdownQuery.isBlank()) allJuz
                                else allJuz.filter {
                                    it.arabicName.contains(dropdownQuery.trim()) ||
                                        it.englishName.contains(dropdownQuery.trim(), ignoreCase = true) ||
                                        it.number.toString() == dropdownQuery.trim()
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (dropdownTab == QuranTabIndex.SURAHS) {
                                    items(menuFilteredSurahs, key = { it.number }) { itemSurah ->
                                        val isCurrent = itemSurah.number == surah.number
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isCurrent) colors.floatingPillActiveIcon.copy(alpha = 0.15f) else colors.searchBg)
                                                .border(
                                                    1.dp,
                                                    if (isCurrent) colors.floatingPillActiveIcon.copy(alpha = 0.6f) else colors.searchBorder,
                                                    RoundedCornerShape(14.dp)
                                                )
                                                .clickable {
                                                    onSelectSurah(itemSurah)
                                                    showDropdownMenu = false
                                                }
                                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                        ) {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    // Left: badge + english name
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        Text(
                                                            text = itemSurah.number.toArabicIndic(),
                                                            fontFamily = ThmanyahSans,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = if (isCurrent) colors.floatingPillActiveIcon else colors.dividerText
                                                        )
                                                        Text(
                                                            text = itemSurah.englishName,
                                                            fontFamily = ThmanyahSans,
                                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                            fontSize = 14.sp,
                                                            color = colors.text
                                                        )
                                                    }

                                                    // Right: Arabic name
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        if (isCurrent) {
                                                            Icon(
                                                                Icons.Rounded.Check,
                                                                contentDescription = null,
                                                                tint = colors.floatingPillActiveIcon,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                        Text(
                                                            text = "سُورَةُ ${itemSurah.arabicName}",
                                                            fontFamily = QuranUthmanicHafs,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = if (isCurrent) colors.floatingPillActiveIcon else colors.text
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    items(menuFilteredJuz, key = { it.number }) { itemJuz ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(colors.searchBg)
                                                .border(1.dp, colors.searchBorder, RoundedCornerShape(14.dp))
                                                .clickable {
                                                    val startSurah = allSurahs.firstOrNull { it.number == itemJuz.startSurahNumber }
                                                        ?: allSurahs.first()
                                                    onSelectSurah(startSurah)
                                                    showDropdownMenu = false
                                                }
                                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                        ) {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = itemJuz.englishName,
                                                        fontFamily = ThmanyahSans,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 13.5.sp,
                                                        color = colors.text
                                                    )
                                                    Text(
                                                        text = itemJuz.arabicName,
                                                        fontFamily = ThmanyahSans,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = colors.text
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ─── FLOATING BOTTOM CONTROLS (Hides on scroll down, with hint tooltip) ───
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hint tooltip on first entry
                AnimatedVisibility(
                    visible = showControlsHint && shouldShowBars,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (colors.isLight) AtharPrimary else AtharPrimaryLight)
                            .clickable { showControlsHint = false }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quran_reader_controls_hint),
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (colors.isLight) Color.White else Color.Black
                        )
                    }
                }

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
                            modifier = Modifier.width(240.dp)
                        ) {
                            // Layout Mode Switcher inside Font Panel
                            Text(
                                text = stringResource(R.string.quran_layout_style),
                                fontFamily = ThmanyahSans,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.dividerText,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.floatingPillItemBg)
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                val isTextSelected = layoutMode == QuranLayoutMode.TEXT
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (isTextSelected) colors.floatingPillBg else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isTextSelected) colors.floatingPillActiveIcon.copy(alpha = 0.5f) else Color.Transparent,
                                            RoundedCornerShape(11.dp)
                                        )
                                        .clickable { onLayoutModeChange(QuranLayoutMode.TEXT) }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_layout_text),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isTextSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isTextSelected) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = stringResource(R.string.quran_layout_default),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = if (isTextSelected) colors.floatingPillActiveIcon.copy(alpha = 0.7f) else colors.dividerText.copy(alpha = 0.6f),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }

                                val isPagesSelected = layoutMode == QuranLayoutMode.PAGES_SVG
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (isPagesSelected) colors.floatingPillBg else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isPagesSelected) colors.floatingPillActiveIcon.copy(alpha = 0.5f) else Color.Transparent,
                                            RoundedCornerShape(11.dp)
                                        )
                                        .clickable { onLayoutModeChange(QuranLayoutMode.PAGES_SVG) }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_layout_pages),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isPagesSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isPagesSelected) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (colors.isLight) Color(0xFFC26E28).copy(alpha = 0.15f) else Color(0xFFE58E3A).copy(alpha = 0.2f))
                                                .border(
                                                    0.5.dp,
                                                    if (colors.isLight) Color(0xFFC26E28).copy(alpha = 0.4f) else Color(0xFFE58E3A).copy(alpha = 0.45f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.quran_layout_beta),
                                                fontFamily = ThmanyahSans,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (colors.isLight) Color(0xFFC26E28) else Color(0xFFE58E3A),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(colors.floatingPillItemBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${(fontScale * 100).toInt()}%",
                                            fontFamily = ThmanyahSans,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.floatingPillActiveIcon
                                        )
                                    }
                                }
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
                                valueRange = 0.70f..2.0f,
                                steps = 13,
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

                // Floating Dock with Labels (Matching AtharNavBar styling)
                AnimatedVisibility(
                    visible = shouldShowBars,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it },
                    exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it }
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 18.dp,
                                shape = RoundedCornerShape(36.dp),
                                spotColor = AtharNavGlow.copy(alpha = 0.35f),
                                ambientColor = Color.Black.copy(alpha = 0.65f)
                            )
                            .clip(RoundedCornerShape(36.dp))
                            .background(colors.floatingPillBg)
                            .border(
                                width = 1.2.dp,
                                color = colors.floatingPillBorder,
                                shape = RoundedCornerShape(36.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                            ) {
                                // 1. التلاوة (Recitation)
                                ReaderDockItem(
                                    label = stringResource(R.string.quran_nav_recitation),
                                    active = isPlaying || isAudioLoading,
                                    colors = colors,
                                    onClick = {
                                        showFontPanel = false
                                        showThemePanel = false
                                        showReciterPanel = false
                                        toggleAudio()
                                    }
                                ) { tint ->
                                    when {
                                        isAudioLoading -> {
                                            CircularProgressIndicator(
                                                color = tint,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        isPlaying -> {
                                            Icon(
                                                Icons.Rounded.Pause,
                                                contentDescription = "Pause",
                                                tint = tint,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        else -> {
                                            Icon(
                                                Icons.Rounded.PlayArrow,
                                                contentDescription = "Play",
                                                tint = tint,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }

                                // 2. القارئ (Reciter)
                                ReaderDockItem(
                                    label = stringResource(R.string.quran_nav_reciter),
                                    active = showReciterPanel,
                                    colors = colors,
                                    onClick = {
                                        showReciterPanel = !showReciterPanel
                                        showFontPanel = false
                                        showThemePanel = false
                                    }
                                ) { tint ->
                                    Icon(
                                        Icons.Rounded.GraphicEq,
                                        contentDescription = stringResource(R.string.quran_reciter_title),
                                        tint = tint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // 3. المظهر (Theme / Appearance)
                                ReaderDockItem(
                                    label = stringResource(R.string.quran_nav_theme),
                                    active = showThemePanel,
                                    colors = colors,
                                    onClick = {
                                        showThemePanel = !showThemePanel
                                        showFontPanel = false
                                        showReciterPanel = false
                                    }
                                ) { tint ->
                                    Icon(
                                        Icons.Rounded.Palette,
                                        contentDescription = "Theme",
                                        tint = tint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // 4. الخط (Font / Text size)
                                ReaderDockItem(
                                    label = stringResource(R.string.quran_nav_font),
                                    active = showFontPanel,
                                    colors = colors,
                                    onClick = {
                                        showFontPanel = !showFontPanel
                                        showThemePanel = false
                                        showReciterPanel = false
                                    }
                                ) { tint ->
                                    Text(
                                        text = "TT",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = tint
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderDockItem(
    label: String,
    active: Boolean,
    colors: QuranReaderColors,
    onClick: () -> Unit,
    iconContent: @Composable (Color) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "readerDockScale"
    )

    val pillBackground by animateColorAsState(
        targetValue = if (active) colors.floatingPillActiveIcon.copy(alpha = 0.20f) else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "readerDockBg"
    )

    val pillBorderColor by animateColorAsState(
        targetValue = if (active) colors.floatingPillActiveIcon.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "readerDockBorder"
    )

    val contentTint by animateColorAsState(
        targetValue = if (active) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "readerDockTint"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(pillBackground)
            .border(
                width = if (active) 1.dp else 0.dp,
                color = pillBorderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                iconContent(contentTint)
            }
            Spacer(Modifier.height(2.5.dp))
            Text(
                text = label,
                fontFamily = ThmanyahSans,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.5.sp,
                color = contentTint,
                textAlign = TextAlign.Center
            )
        }
    }
}


package com.athar.app.ui.corner

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.graphics.Picture
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.LruCache
import android.widget.Toast
import androidx.activity.compose.BackHandler
import java.util.Locale
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import kotlin.math.absoluteValue
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.rounded.WifiOff
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.TextLayoutResult
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
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.formatDigits
import com.athar.app.data.QuranPageChunk
import com.athar.app.data.QuranPages
import com.athar.app.data.QuranReciter
import com.athar.app.data.QuranRepository
import com.athar.app.data.QuranRepository.toArabicIndic
import com.athar.app.data.QuranVerse
import com.athar.app.data.QuranThemeMode
import com.athar.app.data.QuranLayoutMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import com.athar.app.data.ChapterRecitationTiming
import com.athar.app.data.QuranRecitationSyncRepository
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
import com.athar.app.ui.theme.QuranIndoPak
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

private fun isNetworkAvailable(context: android.content.Context): Boolean {
    return runCatching {
        val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }.getOrDefault(false)
}

/**
 * Holy Quran reader screen with Mushaf presentation:
 * - Authentic Uthmani Hafs calligraphy text with ornate \u06DD ayah markers.
 * - Calligraphic Surah titles via QuranSurahNames font.
 * - Dual-tab index: Surahs (1..114) and Juz (1..30) in canonical order.
 * - 3 Appearance Modes: AMOLED, Dark Olive, and Light Mushaf Paper.
 * - Floating bottom capsule with typography, theme switcher, and audio controls.
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
    var targetPageToScroll by remember { mutableStateOf<Int?>(null) }
    var isLastReadDismissed by rememberSaveable { mutableStateOf(false) }

    val lastReadSurahNum by appPrefs.lastReadSurahNumber.collectAsState(initial = null)
    val lastReadSurahAr by appPrefs.lastReadSurahNameAr.collectAsState(initial = null)
    val lastReadSurahEn by appPrefs.lastReadSurahNameEn.collectAsState(initial = null)
    val lastReadPageNum by appPrefs.lastReadPageNumber.collectAsState(initial = null)
    val numberStyle by appPrefs.numberStyle.collectAsState(initial = com.athar.app.data.NumberStylePreference.WESTERN)

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(QuranTabIndex.SURAHS) }
    var tafsirTargetAyah by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showTafsirWithSurahIndex by remember { mutableStateOf(false) }

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
            targetPageToScroll = null
        } else {
            lastBackTime = now
            showDoubleBackToast = true
        }
    }

    LaunchedEffect(openSurah, tafsirTargetAyah, showTafsirWithSurahIndex) {
        onReadingModeChanged(openSurah != null || tafsirTargetAyah != null || showTafsirWithSurahIndex)
    }

    DisposableEffect(Unit) {
        onDispose {
            onReadingModeChanged(false)
        }
    }

    val colors = remember(themeMode) { getQuranColors(themeMode) }

    val isTafsirOpen = showTafsirWithSurahIndex || tafsirTargetAyah != null
    val backgroundBlurRadius by animateDpAsState(
        targetValue = if (isTafsirOpen) 18.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "tafsirBgBlur"
    )

    val currentSurah = openSurah
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (currentSurah != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (backgroundBlurRadius > 0.dp) Modifier.blur(backgroundBlurRadius) else Modifier)
            ) {
                SurahReader(
                    surah = currentSurah,
                    themeMode = themeMode,
                    colors = colors,
                    fontScale = fontScale,
                    fontBold = fontBold,
                    reciter = reciter,
                    layoutMode = layoutMode,
                    initialPageNumber = targetPageToScroll,
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
                    onBackToList = {
                        openSurah = null
                        targetPageToScroll = null
                    },
                    onSelectSurah = {
                        targetPageToScroll = null
                        openSurah = it
                    },
                    onNextSurah = {
                        val idx = allSurahs.indexOfFirst { it.number == currentSurah.number }
                        if (idx in 0 until allSurahs.lastIndex) {
                            targetPageToScroll = null
                            openSurah = allSurahs[idx + 1]
                        }
                    },
                    onOpenTafsir = { s, a -> tafsirTargetAyah = Pair(s, a) }
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
            // Surah & Juz Index Screen with centered floating Last Read card above nav bar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (backgroundBlurRadius > 0.dp) Modifier.blur(backgroundBlurRadius) else Modifier)
            ) {
                SurahListScreen(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    selectedTab = selectedTab,
                    onTabSelect = { selectedTab = it },
                    colors = colors,
                    lastReadSurahNum = lastReadSurahNum,
                    onBack = onBack,
                    onSelectSurah = {
                        targetPageToScroll = null
                        openSurah = it
                    },
                    onOpenTafsir = { s, a -> tafsirTargetAyah = Pair(s, a) },
                    onOpenTafsirIndex = { showTafsirWithSurahIndex = true }
                )

                val lastSurah = remember(lastReadSurahNum) {
                    allSurahs.firstOrNull { it.number == lastReadSurahNum }
                }
                val isArabic = remember { Locale.getDefault().language == "ar" }
                if (lastSurah != null && lastReadPageNum != null && !isLastReadDismissed && searchQuery.isBlank()) {
                    val displayName = if (isArabic) "سورة ${lastSurah.arabicName}" else "Surah ${lastSurah.englishName}"
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(250)) + slideInVertically(spring(dampingRatio = 0.85f, stiffness = 350f)) { it },
                        exit = fadeOut(tween(200)) + slideOutVertically(spring(dampingRatio = 0.85f, stiffness = 350f)) { it },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 88.dp)
                    ) {
                        LastReadFloatingCard(
                            surahName = displayName,
                            pageNumber = lastReadPageNum!!,
                            numberStyle = numberStyle,
                            onContinueReading = {
                                targetPageToScroll = lastReadPageNum
                                openSurah = lastSurah
                            },
                            onDismiss = {
                                isLastReadDismissed = true
                            }
                        )
                    }
                }
            }
        }

        if (showTafsirWithSurahIndex) {
            AlDirayahSheet(
                initialSurahNumber = lastReadSurahNum ?: 1,
                initialAyahNumber = 1,
                startWithSurahIndex = true,
                numberStyle = numberStyle,
                onDismiss = { showTafsirWithSurahIndex = false }
            )
        } else {
            val targetAyah = tafsirTargetAyah
            if (targetAyah != null) {
                AlDirayahSheet(
                    initialSurahNumber = targetAyah.first,
                    initialAyahNumber = targetAyah.second,
                    startWithSurahIndex = false,
                    numberStyle = numberStyle,
                    onDismiss = { tafsirTargetAyah = null }
                )
            }
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
    lastReadSurahNum: Int? = null,
    onBack: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit,
    onOpenTafsir: (surahNumber: Int, ayahNumber: Int) -> Unit = { _, _ -> },
    onOpenTafsirIndex: () -> Unit = {}
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

            // "Al-Dirayah" (الدِّرَايَة) Animated Glow Feature Bar Tab
            if (query.isBlank()) {
                AlDirayahGlowCard(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    onClick = onOpenTafsirIndex
                )
            }

            Spacer(Modifier.height(4.dp))

            // Content List (seamless with IslamicPatternBackground)
            if (selectedTab == QuranTabIndex.SURAHS) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 220.dp)
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
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 220.dp)
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
    colors: QuranReaderColors,
    layoutMode: QuranLayoutMode = QuranLayoutMode.TEXT
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val dividerLabel = if (layoutMode == QuranLayoutMode.INDOPAK_13_LINES) {
        if (isArabic) "مصحف ١٣ سطر • ص ${pageNumber.toArabicIndic()}" else "13-Line Mushaf • p. $pageNumber"
    } else {
        pageNumber.toArabicIndic()
    }
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
            text = "  —  $dividerLabel  —  ",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.5.sp,
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
 * Renders a single ligature-based Mushaf page using the bundled SVG layout,
 * with native Canvas rendering via AndroidSVG.
 *
 * The SVG paths already include the Uthmani glyph shapes, surah headers,
 * Bismillah, ayah medallions, line breaks and page spacing.
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

private val QURAN_STOP_MARK_REGEX = Regex("^[\\u0615-\\u061A\\u06D6-\\u06ED\\uF600-\\uF8FF\\s]+$")

data class QuranVerseToken(
    val token: String,
    val isStopMark: Boolean,
    val wordIndex: Int
)

fun parseVerseTokens(text: String): List<QuranVerseToken> {
    val tokens = text.split(" ").filter { it.isNotBlank() }
    var currentWordIdx = 0
    return tokens.map { tok ->
        val isStop = QURAN_STOP_MARK_REGEX.matches(tok.trim())
        if (isStop) {
            QuranVerseToken(tok, isStopMark = true, wordIndex = 0)
        } else {
            currentWordIdx++
            QuranVerseToken(tok, isStopMark = false, wordIndex = currentWordIdx)
        }
    }
}

@Composable
private fun IndoPak13LinePageCard(
    chunk: QuranPageChunk,
    surah: SurahMeta,
    colors: QuranReaderColors,
    fontScale: Float,
    fontBold: Boolean,
    isPlaying: Boolean,
    activeVerseNumber: Int?,
    activeWordIndex: Int?,
    numberStylePref: NumberStylePreference,
    onActiveWordPosition: (Float) -> Unit,
    onAyahClick: ((Int) -> Unit)? = null
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val isFirstPageOfSurah = chunk.verses.any { it.number == 1 }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var contentTopOffsetPx by remember { mutableFloatStateOf(0f) }

    // Precalculate inline content for ayah end medallions
    val inlineContent = remember(chunk.verses, fontScale, colors.ayahMarker, activeVerseNumber, isPlaying) {
        val map = mutableMapOf<String, InlineTextContent>()
        val sizeSp = (22 * fontScale).sp
        for (v in chunk.verses) {
            val isThisVerseActive = isPlaying && (v.number == activeVerseNumber)
            map["ayah_${v.number}"] = InlineTextContent(
                Placeholder(
                    width = sizeSp,
                    height = sizeSp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                AyahEndMedallion(
                    number = v.number,
                    fontScale = fontScale,
                    color = if (isThisVerseActive) (if (colors.isLight) AtharPrimary else Color(0xFFC9D8B4)) else colors.ayahMarker
                )
            }
        }
        map
    }

    // Build the continuous flowing text of the page with active word highlighting and verse character ranges
    val (annotatedText, activeWordCharOffset, verseCharRanges) = remember(
        chunk.verses,
        isPlaying,
        activeVerseNumber,
        activeWordIndex,
        colors.text,
        colors.isLight
    ) {
        var charOffset = -1
        val ranges = mutableListOf<Pair<Int, IntRange>>()
        val builder = androidx.compose.ui.text.AnnotatedString.Builder()

        for (vIdx in chunk.verses.indices) {
            val verse = chunk.verses[vIdx]
            val vStart = builder.length
            val isThisVerseActive = isPlaying && (verse.number == activeVerseNumber)

            if (!isPlaying) {
                builder.append(verse.text)
            } else if (!isThisVerseActive) {
                builder.withStyle(SpanStyle(color = colors.text.copy(alpha = 0.40f))) {
                    builder.append(verse.text)
                }
            } else {
                val tokens = parseVerseTokens(verse.text)
                val currentWIdx = activeWordIndex ?: 0
                val highlightColor = if (colors.isLight) Color(0xFF163212) else Color(0xFFFFFFFF)
                val bgHighlight = if (colors.isLight) AtharPrimary.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.16f)

                for (w in tokens.indices) {
                    val tokenItem = tokens[w]
                    if (tokenItem.isStopMark) {
                        builder.withStyle(SpanStyle(color = colors.text.copy(alpha = 0.65f))) {
                            builder.append(tokenItem.token)
                        }
                    } else {
                        val word1Based = tokenItem.wordIndex
                        when {
                            word1Based == currentWIdx -> {
                                charOffset = builder.length
                                builder.withStyle(
                                    SpanStyle(
                                        color = highlightColor,
                                        fontWeight = FontWeight.Bold,
                                        background = bgHighlight,
                                        shadow = if (!colors.isLight) Shadow(
                                            color = Color.Black.copy(alpha = 0.75f),
                                            offset = Offset(0f, 1f),
                                            blurRadius = 2f
                                        ) else null
                                    )
                                ) {
                                    builder.append(tokenItem.token)
                                }
                            }
                            word1Based < currentWIdx -> {
                                builder.withStyle(
                                    SpanStyle(
                                        color = colors.text,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    builder.append(tokenItem.token)
                                }
                            }
                            else -> {
                                builder.withStyle(
                                    SpanStyle(
                                        color = colors.text.copy(alpha = 0.70f),
                                        fontWeight = FontWeight.Normal
                                    )
                                ) {
                                    builder.append(tokenItem.token)
                                }
                            }
                        }
                    }
                    if (w < tokens.lastIndex) {
                        builder.append(" ")
                    }
                }
            }

            builder.append("\u202F")
            builder.appendInlineContent("ayah_${verse.number}", " (${verse.number}) ")
            val vEnd = builder.length
            ranges.add(verse.number to (vStart until vEnd))
            if (vIdx < chunk.verses.lastIndex) {
                builder.append(" ")
            }
        }
        Triple(builder.toAnnotatedString(), charOffset, ranges)
    }

    // Outer Mushaf Page Container Frame
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 540.dp)
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (colors.isLight) Color(0xFFFBFBF9) else colors.circleButtonBg.copy(alpha = 0.40f)
            )
            .border(
                width = 1.2.dp,
                color = if (colors.isLight) colors.dividerLine.copy(alpha = 0.75f) else colors.dividerLine.copy(alpha = 0.40f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── PAGE HEADER: Authentic 13-Line IndoPak Header (Right: Juz Name & Number, Left: Surah Name & Number) ───
            val firstAyahNum = chunk.verses.firstOrNull()?.number ?: 1
            val juzInfo = remember(surah.number, firstAyahNum) {
                getIndoPakJuzForVerse(surah.number, firstAyahNum)
            }
            val juzNumStr = formatDigits(juzInfo.number.toString(), numberStylePref)
            val surahNumStr = formatDigits(surah.number.toString(), numberStylePref)

            val rightHeaderTitle = if (isArabic) {
                "${juzInfo.arabicTitle} $juzNumStr"
            } else {
                "${juzInfo.englishTitle} $juzNumStr"
            }

            val leftHeaderTitle = if (isArabic) {
                "${surah.arabicName} $surahNumStr"
            } else {
                "${surah.englishName} $surahNumStr"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rightHeaderTitle,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = colors.dividerText
                )
                Text(
                    text = leftHeaderTitle,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = colors.dividerText
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 10.dp),
                thickness = 1.dp,
                color = colors.dividerLine.copy(alpha = 0.65f)
            )

            // Optional Surah Header Banner + Bismillah if this page starts the Surah
            if (isFirstPageOfSurah && surah.number != 9) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ",
                        fontFamily = QuranIndoPak,
                        fontWeight = if (fontBold) FontWeight.Bold else FontWeight.Normal,
                        fontSize = (25 * fontScale).sp,
                        color = if (colors.isLight) colors.text.copy(alpha = 0.85f) else Color(0xFFCBD2C8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        thickness = 0.8.dp,
                        color = colors.dividerLine.copy(alpha = 0.5f)
                    )
                }
            }

            // ─── 13-LINE RULED CONTENT AREA (Every line has a ruled horizontal line) ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        contentTopOffsetPx = coords.positionInParent().y
                    }
                    .pointerInput(chunk.verses, verseCharRanges) {
                        detectTapGestures { tapOffset ->
                            val layout = textLayoutResult ?: return@detectTapGestures
                            val tappedCharOffset = layout.getOffsetForPosition(tapOffset)
                            val targetVerse = verseCharRanges.find { tappedCharOffset in it.second }?.first
                                ?: chunk.verses.firstOrNull()?.number
                            if (targetVerse != null && onAyahClick != null) {
                                onAyahClick(targetVerse)
                            }
                        }
                    }
                    .drawBehind {
                        val layout = textLayoutResult ?: return@drawBehind
                        val lineStroke = 0.85.dp.toPx()
                        val lineColor = colors.dividerLine.copy(alpha = 0.50f)
                        val actualLines = layout.lineCount

                        // Draw ruled horizontal line beneath every rendered line
                        for (i in 0 until actualLines) {
                            val lineBottom = layout.getLineBottom(i)
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, lineBottom),
                                end = Offset(size.width, lineBottom),
                                strokeWidth = lineStroke
                            )
                        }

                        // For partial pages with fewer than 13 lines, draw remaining blank ruled lines to complete the 13 lines grid
                        if (actualLines < 13 && actualLines > 0) {
                            val avgLineH = layout.getLineBottom(actualLines - 1) / actualLines.toFloat()
                            var lastBottom = layout.getLineBottom(actualLines - 1)
                            for (i in actualLines until 13) {
                                lastBottom += avgLineH
                                drawLine(
                                    color = lineColor.copy(alpha = 0.28f),
                                    start = Offset(0f, lastBottom),
                                    end = Offset(size.width, lastBottom),
                                    strokeWidth = lineStroke
                                )
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val hasActiveVerse = isPlaying && chunk.verses.any { it.number == activeVerseNumber }
                Text(
                    text = annotatedText,
                    inlineContent = inlineContent,
                    fontFamily = QuranIndoPak,
                    fontWeight = if (fontBold) FontWeight.Bold else FontWeight.Normal,
                    fontSize = (21 * fontScale).sp,
                    lineHeight = (46 * fontScale).sp,
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
                    onTextLayout = { layoutResult ->
                        textLayoutResult = layoutResult
                        if (hasActiveVerse && activeWordCharOffset >= 0 && activeWordCharOffset < layoutResult.layoutInput.text.length) {
                            val line = layoutResult.getLineForOffset(activeWordCharOffset)
                            val lineTop = layoutResult.getLineTop(line)
                            val lineBottom = layoutResult.getLineBottom(line)
                            val wordCenterY = (lineTop + lineBottom) / 2f
                            onActiveWordPosition(contentTopOffsetPx + wordCenterY)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(10.dp))

            // ─── PAGE FOOTER: Page Number in Mushaf Style ───
            val pageNumStr = formatDigits(chunk.pageNumber.toString(), numberStylePref)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pageNumStr,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colors.dividerText
                )
            }
        }
    }
}

sealed class QuranTextItem {
    data class BismillahItem(val surahNumber: Int) : QuranTextItem()
    data class VerseItem(val verse: QuranVerse, val pageNumber: Int) : QuranTextItem()
    data class PageDividerItem(val pageNumber: Int) : QuranTextItem()
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
    initialPageNumber: Int? = null,
    onLayoutModeChange: (QuranLayoutMode) -> Unit,
    onThemeChange: (QuranThemeMode) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onReciterChange: (QuranReciter) -> Unit,
    onToggleBold: () -> Unit,
    onBackToList: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit,
    onNextSurah: () -> Unit,
    onOpenTafsir: (surahNumber: Int, ayahNumber: Int) -> Unit = { _, _ -> }
)  {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appPrefs = remember { AppPreferences(context.applicationContext) }
    val numberStylePref by appPrefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)
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

    // Recitation Synchronization & Syllable/Word Highlight State
    var chapterTiming by remember(surah.number, reciter) { mutableStateOf<ChapterRecitationTiming?>(null) }
    var currentPlaybackMs by remember { mutableLongStateOf(0L) }
    var activeVerseNumber by remember { mutableStateOf<Int?>(null) }
    var activeWordIndex by remember { mutableStateOf<Int?>(null) }
    var activeWordOffsetYInItem by remember { mutableFloatStateOf(0f) }
    var autoScrollEnabled by rememberSaveable { mutableStateOf(true) }
    var areBarsVisible by remember { mutableStateOf(true) }
    var lastUserInteractionTime by remember { mutableLongStateOf(0L) }
    var audioAlertMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(audioAlertMessage) {
        if (audioAlertMessage != null) {
            kotlinx.coroutines.delay(4000)
            audioAlertMessage = null
        }
    }

    val listState = rememberLazyListState()

    // Detect user manual dragging to pause auto-scroll temporarily and reveal bars when reciting
    val isUserDragging by listState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(isUserDragging) {
        if (isUserDragging) {
            lastUserInteractionTime = System.currentTimeMillis()
            if (isPlaying && !areBarsVisible) {
                areBarsVisible = true
            }
        }
    }

    // Build flat sequence of text items (Bismillah, Verses, Mushaf Page Dividers) for per-ayah scrolling
    val textItems = remember(pageChunks, surah.number) {
        val chunks = pageChunks ?: return@remember emptyList()
        val items = ArrayList<QuranTextItem>()
        if (surah.number != 9) {
            items.add(QuranTextItem.BismillahItem(surah.number))
        }
        for (chunk in chunks) {
            for (verse in chunk.verses) {
                items.add(QuranTextItem.VerseItem(verse, chunk.pageNumber))
            }
            items.add(QuranTextItem.PageDividerItem(chunk.pageNumber))
        }
        items
    }

    // Preload recitation timing for current surah & reciter
    LaunchedEffect(surah.number, reciter) {
        val timing = QuranRecitationSyncRepository.getChapterTiming(context, reciter, surah.number)
        chapterTiming = timing
    }

    // High-frequency playback position sampling & active word/verse resolution
    LaunchedEffect(isPlaying, chapterTiming) {
        if (!isPlaying) {
            currentPlaybackMs = 0L
            activeVerseNumber = null
            activeWordIndex = null
            activeWordOffsetYInItem = 0f
            return@LaunchedEffect
        }
        while (isActive && isPlaying) {
            try {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    val pos = player.currentPosition.toLong()
                    currentPlaybackMs = pos
                    val timing = chapterTiming
                    if (timing != null) {
                        // Compensate audio buffer / hardware latency with reciter-calibrated lead offset
                        // to ensure word highlight syncs instantaneously with the spoken recitation without delay.
                        val effectivePos = (pos + reciter.syncLeadMs).coerceAtLeast(0L)
                        val activeVerse = timing.findActiveVerse(effectivePos)
                        val vNum = activeVerse?.verseNumber
                        val wIdx = if (activeVerse != null) timing.findActiveWordIndex(activeVerse, effectivePos) else null
                        if (activeVerseNumber != vNum) {
                            activeVerseNumber = vNum
                            activeWordOffsetYInItem = 0f
                        }
                        if (activeWordIndex != wIdx) {
                            activeWordIndex = wIdx
                        }
                    }
                }
            } catch (_: Exception) {}
            delay(16)
        }
    }

    // Silky smooth continuous auto-scroll following the active word indicator position
    LaunchedEffect(activeVerseNumber, activeWordIndex, activeWordOffsetYInItem, autoScrollEnabled, isPlaying) {
        val vNum = activeVerseNumber ?: return@LaunchedEffect
        if (!autoScrollEnabled || !isPlaying) return@LaunchedEffect
        val now = System.currentTimeMillis()
        if (now - lastUserInteractionTime < 2800L) return@LaunchedEffect

        if (layoutMode == QuranLayoutMode.TEXT) {
            val targetIdx = textItems.indexOfFirst { it is QuranTextItem.VerseItem && it.verse.number == vNum }
            if (targetIdx >= 0) {
                try {
                    val layoutInfo = listState.layoutInfo
                    val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
                    if (viewportHeight <= 0) return@LaunchedEffect

                    val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == targetIdx }
                    val targetFocalY = viewportHeight * 0.28f

                    if (visibleItem != null) {
                        // Exact position of the active word in the viewport
                        val wordViewportY = visibleItem.offset + activeWordOffsetYInItem
                        val scrollDelta = wordViewportY - targetFocalY

                        // Glide smoothly when the word advances down past focal threshold
                        if (scrollDelta > 28f || scrollDelta < -60f) {
                            listState.animateScrollBy(
                                value = scrollDelta,
                                animationSpec = tween<Float>(
                                    durationMillis = 550,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    } else {
                        // Verse not visible in current viewport, bring it into reading focal zone
                        listState.animateScrollToItem(
                            index = targetIdx,
                            scrollOffset = -(targetFocalY.toInt())
                        )
                    }
                } catch (_: Exception) {}
            }
        } else if (layoutMode == QuranLayoutMode.INDOPAK_13_LINES) {
            val chunks = pageChunks ?: return@LaunchedEffect
            val chunkIdx = chunks.indexOfFirst { chunk -> chunk.verses.any { it.number == vNum } }
            if (chunkIdx >= 0) {
                try {
                    val layoutInfo = listState.layoutInfo
                    val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
                    if (viewportHeight <= 0) return@LaunchedEffect

                    val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == chunkIdx }
                    val targetFocalY = viewportHeight * 0.28f

                    if (visibleItem != null) {
                        // Exact position of the active word in the 13-line page card
                        val wordViewportY = visibleItem.offset + activeWordOffsetYInItem
                        val scrollDelta = wordViewportY - targetFocalY

                        if (scrollDelta > 28f || scrollDelta < -60f) {
                            listState.animateScrollBy(
                                value = scrollDelta,
                                animationSpec = tween<Float>(
                                    durationMillis = 550,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    } else {
                        listState.animateScrollToItem(
                            index = chunkIdx,
                            scrollOffset = -(targetFocalY.toInt())
                        )
                    }
                } catch (_: Exception) {}
            }
        } else {
            val chunks = pageChunks ?: return@LaunchedEffect
            val chunkIdx = chunks.indexOfFirst { chunk -> chunk.verses.any { it.number == vNum } }
            if (chunkIdx >= 0) {
                try {
                    val layoutInfo = listState.layoutInfo
                    val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
                    if (viewportHeight <= 0) return@LaunchedEffect

                    val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == chunkIdx }
                    val targetFocalY = viewportHeight * 0.22f

                    if (visibleItem != null) {
                        val chunk = chunks[chunkIdx]
                        val verseIdxInChunk = chunk.verses.indexOfFirst { it.number == vNum }.coerceAtLeast(0)
                        val wordFraction = ((activeWordIndex ?: 1).toFloat() / 20f).coerceIn(0f, 1f)
                        val pageProgress = (verseIdxInChunk.toFloat() + wordFraction) / chunk.verses.size.coerceAtLeast(1)
                        val wordViewportY = visibleItem.offset + (visibleItem.size * pageProgress)
                        val scrollDelta = wordViewportY - targetFocalY

                        if (scrollDelta > 32f || scrollDelta < -65f) {
                            listState.animateScrollBy(
                                value = scrollDelta,
                                animationSpec = tween<Float>(
                                    durationMillis = 600,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    } else {
                        listState.animateScrollToItem(
                            index = chunkIdx,
                            scrollOffset = -(targetFocalY.toInt())
                        )
                    }
                } catch (_: Exception) {}
            }
        }
    }

    // Scroll state & scroll-aware floating bars
    var showControlsHint by rememberSaveable { mutableStateOf(true) }
    var showIndexHint by rememberSaveable { mutableStateOf(true) }
    var showNavHint by rememberSaveable { mutableStateOf(true) }

    // Recitation Full-Screen Mode: Tools vanish when reciting starts; reappear on touch/drag; auto-vanish after 4s
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            areBarsVisible = false
            showFontPanel = false
            showThemePanel = false
            showReciterPanel = false
            showDropdownMenu = false
            showControlsHint = false
            showIndexHint = false
            showNavHint = false
        } else {
            areBarsVisible = true
        }
    }

    LaunchedEffect(isPlaying, areBarsVisible, lastUserInteractionTime, showFontPanel, showThemePanel, showReciterPanel, showDropdownMenu) {
        if (isPlaying && areBarsVisible && !showFontPanel && !showThemePanel && !showReciterPanel && !showDropdownMenu) {
            delay(4000)
            areBarsVisible = false
        }
    }

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
                lastUserInteractionTime = System.currentTimeMillis()
                if (isPlaying) {
                    if (dy.absoluteValue > 6f && !areBarsVisible) {
                        areBarsVisible = true
                    }
                } else {
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
    val shouldShowBars = if (isPlaying) areBarsVisible else (areBarsVisible || isNearTop)

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
            activeVerseNumber = null
            activeWordIndex = null
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
        activeVerseNumber = null
        activeWordIndex = null
    }

    fun toggleAudio() {
        if (isPlaying) {
            try {
                mediaPlayer?.pause()
                isPlaying = false
                areBarsVisible = true
            } catch (_: Exception) {}
            return
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.start()
                isPlaying = true
                areBarsVisible = false
            } catch (_: Exception) {}
            return
        }

        // Verify internet connection before attempting to stream recitation
        if (!isNetworkAvailable(context)) {
            val msg = context.getString(R.string.quran_audio_requires_internet)
            audioAlertMessage = msg
            isAudioLoading = false
            isPlaying = false
            return
        }

        // Initialize and stream recitation from selected reciter
        isAudioLoading = true
        val player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener { mp ->
                isAudioLoading = false
                isPlaying = true
                areBarsVisible = false
                mp.start()
                if (chapterTiming == null) {
                    scope.launch {
                        val verses = QuranRepository.getSurahVerses(context, surah.number) ?: emptyList()
                        chapterTiming = QuranRecitationSyncRepository.synthesizeOfflineTiming(
                            chapter = surah.number,
                            reciterId = reciter.quranComId,
                            verses = verses,
                            totalDurationMs = mp.duration.toLong()
                        )
                    }
                }
            }
            setOnCompletionListener {
                isPlaying = false
                areBarsVisible = true
                activeVerseNumber = null
                activeWordIndex = null
            }
            setOnErrorListener { mp, _, _ ->
                val fallbackUrl = reciter.getFallbackAudioUrl(surah.number)
                val fallbackSuccess = runCatching {
                    mp.reset()
                    mp.setDataSource(fallbackUrl)
                    mp.prepareAsync()
                }.isSuccess
                if (!fallbackSuccess) {
                    isAudioLoading = false
                    isPlaying = false
                    val msg = context.getString(R.string.quran_audio_network_error)
                    audioAlertMessage = msg
                }
                true
            }
        }
        mediaPlayer = player

        try {
            val primaryUrl = chapterTiming?.audioUrl?.ifBlank { null } ?: reciter.getPrimaryAudioUrl(surah.number)
            player.setDataSource(primaryUrl)
            player.prepareAsync()
        } catch (_: Exception) {
            try {
                player.reset()
                player.setDataSource(reciter.getFallbackAudioUrl(surah.number))
                player.prepareAsync()
            } catch (_: Exception) {
                isAudioLoading = false
                isPlaying = false
                val msg = context.getString(R.string.quran_audio_network_error)
                audioAlertMessage = msg
            }
        }
    }

    var isPositionRestored by remember(surah.number) { mutableStateOf(initialPageNumber == null) }

    // Load surah verses chunked by authentic Mushaf pages
    LaunchedEffect(surah.number, layoutMode, attempt) {
        pageChunks = null
        loadFailed = false

        try {
            val chunks = if (layoutMode == QuranLayoutMode.INDOPAK_13_LINES) {
                QuranRepository.getSurah13LinePageChunks(
                    context = context,
                    number = surah.number
                )
            } else {
                QuranRepository.getSurahPageChunks(
                    context = context,
                    number = surah.number
                )
            }
            pageChunks = chunks
            loadFailed = chunks.isNullOrEmpty()
        } catch (e: Throwable) {
            android.util.Log.e("QuranScreen", "Error loading surah ${surah.number}", e)
            loadFailed = true
        }
    }

    // Scroll to initial page when chunks become available
    LaunchedEffect(pageChunks, initialPageNumber, layoutMode) {
        val chunks = pageChunks
        if (!chunks.isNullOrEmpty() && initialPageNumber != null && !isPositionRestored) {
            if (layoutMode == QuranLayoutMode.TEXT) {
                val targetIdx = textItems.indexOfFirst {
                    (it is QuranTextItem.VerseItem && it.pageNumber == initialPageNumber) ||
                    (it is QuranTextItem.PageDividerItem && it.pageNumber == initialPageNumber)
                }
                if (targetIdx >= 0) {
                    try {
                        listState.scrollToItem(targetIdx)
                    } catch (_: Exception) {}
                }
            } else {
                val chunkIdx = chunks.indexOfFirst { it.pageNumber == initialPageNumber }
                if (chunkIdx >= 0) {
                    try {
                        listState.scrollToItem(chunkIdx)
                    } catch (_: Exception) {}
                }
            }
            isPositionRestored = true
        } else if (chunks != null && initialPageNumber == null) {
            isPositionRestored = true
        }
    }

    val currentVisiblePage by remember {
        derivedStateOf {
            if (layoutMode == QuranLayoutMode.TEXT) {
                if (textItems.isNotEmpty()) {
                    val idx = listState.firstVisibleItemIndex.coerceIn(0, textItems.lastIndex)
                    when (val item = textItems[idx]) {
                        is QuranTextItem.VerseItem -> item.pageNumber
                        is QuranTextItem.PageDividerItem -> item.pageNumber
                        is QuranTextItem.BismillahItem -> {
                            val firstVerse = textItems.filterIsInstance<QuranTextItem.VerseItem>().firstOrNull()
                            firstVerse?.pageNumber ?: QuranPages.getPageForVerse(surah.number, 1)
                        }
                    }
                } else {
                    QuranPages.getPageForVerse(surah.number, 1)
                }
            } else if (layoutMode == QuranLayoutMode.INDOPAK_13_LINES) {
                val chunks = pageChunks
                if (!chunks.isNullOrEmpty()) {
                    val firstIdx = listState.firstVisibleItemIndex
                    val chunkIdx = firstIdx.coerceIn(0, chunks.lastIndex)
                    chunks[chunkIdx].pageNumber
                } else {
                    QuranPages.get13LinePageForVerse(surah.number, 1)
                }
            } else {
                val chunks = pageChunks
                if (!chunks.isNullOrEmpty()) {
                    val firstIdx = listState.firstVisibleItemIndex
                    val chunkIdx = firstIdx.coerceIn(0, chunks.lastIndex)
                    chunks[chunkIdx].pageNumber
                } else {
                    QuranPages.getPageForVerse(surah.number, 1)
                }
            }
        }
    }

    LaunchedEffect(surah.number, currentVisiblePage, isPositionRestored, pageChunks) {
        if (pageChunks != null && isPositionRestored) {
            appPrefs.saveLastReadPosition(
                surahNumber = surah.number,
                surahNameAr = surah.arabicName,
                surahNameEn = surah.englishName,
                pageNumber = currentVisiblePage
            )
        }
    }

    DisposableEffect(surah.number) {
        onDispose {
            if (pageChunks != null && isPositionRestored) {
                scope.launch {
                    appPrefs.saveLastReadPosition(
                        surahNumber = surah.number,
                        surahNameAr = surah.arabicName,
                        surahNameEn = surah.englishName,
                        pageNumber = currentVisiblePage
                    )
                }
            }
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
                                lastUserInteractionTime = System.currentTimeMillis()
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
                        when (layoutMode) {
                            QuranLayoutMode.TEXT -> {
                                // ─── DESIGN 1 (DEFAULT): Traditional Uthmanic Text Flow with Per-Ayah Items ───
                            items(
                                count = textItems.size,
                                key = { idx ->
                                    when (val item = textItems[idx]) {
                                        is QuranTextItem.BismillahItem -> "bismillah_${item.surahNumber}"
                                        is QuranTextItem.VerseItem -> "verse_${surah.number}_${item.verse.number}"
                                        is QuranTextItem.PageDividerItem -> "divider_${surah.number}_${item.pageNumber}"
                                    }
                                }
                            ) { idx ->
                                when (val item = textItems[idx]) {
                                    is QuranTextItem.BismillahItem -> {
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
                                    is QuranTextItem.PageDividerItem -> {
                                        QuranPageDivider(
                                            pageNumber = item.pageNumber,
                                            colors = colors,
                                            layoutMode = layoutMode
                                        )
                                    }
                                    is QuranTextItem.VerseItem -> {
                                        val verse = item.verse
                                        val isThisVerseActive = isPlaying && (verse.number == activeVerseNumber)
                                        val sizeSp = (22 * fontScale).sp

                                        val inlineContent = remember(verse.number, fontScale, colors.ayahMarker, isThisVerseActive) {
                                            mapOf(
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
                                                        color = if (isThisVerseActive) (if (colors.isLight) AtharPrimary else Color(0xFFC9D8B4)) else colors.ayahMarker
                                                    )
                                                }
                                            )
                                        }

                                        val (annotated, activeWordCharOffset) = remember(
                                            verse.text,
                                            verse.number,
                                            isPlaying,
                                            isThisVerseActive,
                                            activeWordIndex,
                                            colors.text,
                                            colors.isLight
                                        ) {
                                            var charOffset = -1
                                            val builder = androidx.compose.ui.text.AnnotatedString.Builder()
                                            if (!isPlaying) {
                                                builder.append(verse.text)
                                            } else if (!isThisVerseActive) {
                                                builder.withStyle(SpanStyle(color = colors.text.copy(alpha = 0.35f))) {
                                                    builder.append(verse.text)
                                                }
                                            } else {
                                                val tokens = parseVerseTokens(verse.text)
                                                val currentWIdx = activeWordIndex ?: 0
                                                val highlightColor = if (colors.isLight) Color(0xFF163212) else Color(0xFFFFFFFF)
                                                val bgHighlight = if (colors.isLight) AtharPrimary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.14f)

                                                for (w in tokens.indices) {
                                                    val tokenItem = tokens[w]
                                                    if (tokenItem.isStopMark) {
                                                        builder.withStyle(SpanStyle(color = colors.text.copy(alpha = 0.65f))) {
                                                            builder.append(tokenItem.token)
                                                        }
                                                    } else {
                                                        val word1Based = tokenItem.wordIndex
                                                        when {
                                                            word1Based == currentWIdx -> {
                                                                charOffset = builder.length
                                                                builder.withStyle(
                                                                    SpanStyle(
                                                                        color = highlightColor,
                                                                        fontWeight = FontWeight.Bold,
                                                                        background = bgHighlight,
                                                                        shadow = if (!colors.isLight) Shadow(
                                                                            color = Color.Black.copy(alpha = 0.75f),
                                                                            offset = Offset(0f, 1f),
                                                                            blurRadius = 2f
                                                                        ) else null
                                                                    )
                                                                ) {
                                                                    builder.append(tokenItem.token)
                                                                }
                                                            }
                                                            word1Based < currentWIdx -> {
                                                                builder.withStyle(
                                                                    SpanStyle(
                                                                        color = colors.text,
                                                                        fontWeight = FontWeight.SemiBold
                                                                    )
                                                                ) {
                                                                    builder.append(tokenItem.token)
                                                                }
                                                            }
                                                            else -> {
                                                                builder.withStyle(
                                                                    SpanStyle(
                                                                        color = colors.text.copy(alpha = 0.70f),
                                                                        fontWeight = FontWeight.Normal
                                                                    )
                                                                ) {
                                                                    builder.append(tokenItem.token)
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (w < tokens.lastIndex) {
                                                        builder.append(" ")
                                                    }
                                                }
                                            }

                                            builder.append("\u202F")
                                            builder.appendInlineContent("ayah_${verse.number}", " (${verse.number}) ")
                                            builder.toAnnotatedString() to charOffset
                                        }

                                        val activeVerseBorder = if (colors.isLight) {
                                            AtharPrimary.copy(alpha = 0.35f)
                                        } else {
                                            Color.White.copy(alpha = 0.25f)
                                        }
                                        val activeVerseBg = if (colors.isLight) {
                                            Color(0xFFF2F6F0).copy(alpha = 0.70f)
                                        } else {
                                            Color.White.copy(alpha = 0.05f)
                                        }

                                        val activeVerseModifier = if (isThisVerseActive) {
                                            Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(activeVerseBg)
                                                .border(1.dp, activeVerseBorder, RoundedCornerShape(16.dp))
                                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                        } else {
                                            Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        }

                                        val density = LocalDensity.current
                                        val verticalPadPx = remember(isThisVerseActive, density) {
                                            with(density) { if (isThisVerseActive) 10.dp.toPx() else 6.dp.toPx() }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .widthIn(max = 520.dp)
                                                .then(activeVerseModifier)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null,
                                                    onClick = { onOpenTafsir(surah.number, verse.number) }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
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
                                                onTextLayout = { layoutResult ->
                                                    if (isThisVerseActive && activeWordCharOffset >= 0 && activeWordCharOffset < layoutResult.layoutInput.text.length) {
                                                        val line = layoutResult.getLineForOffset(activeWordCharOffset)
                                                        val lineTop = layoutResult.getLineTop(line)
                                                        val lineBottom = layoutResult.getLineBottom(line)
                                                        val wordCenterY = (lineTop + lineBottom) / 2f
                                                        activeWordOffsetYInItem = verticalPadPx + wordCenterY
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        QuranLayoutMode.INDOPAK_13_LINES -> {
                                // ─── DESIGN 3: 13-Line IndoPak Mushaf Pages with Horizontal Ruled Lines ───
                                items(
                                    items = chunks,
                                    key = { "indopak_page_${surah.number}_${it.pageNumber}" }
                                ) { chunk ->
                                    IndoPak13LinePageCard(
                                        chunk = chunk,
                                        surah = surah,
                                        colors = colors,
                                        fontScale = fontScale,
                                        fontBold = fontBold,
                                        isPlaying = isPlaying,
                                        activeVerseNumber = activeVerseNumber,
                                        activeWordIndex = activeWordIndex,
                                        numberStylePref = numberStylePref,
                                        onActiveWordPosition = { y ->
                                            activeWordOffsetYInItem = y
                                        },
                                        onAyahClick = { ayahNumber ->
                                            onOpenTafsir(surah.number, ayahNumber)
                                        }
                                    )
                                }
                            }
                            QuranLayoutMode.PAGES_SVG -> {
                                // ─── DESIGN 2 (BETA): Vector Mushaf Pages ───
                                items(chunks, key = { "svg_page_${surah.number}_${it.pageNumber}" }) { chunk ->
                                    LigatureMushafPage(
                                        pageNumber = chunk.pageNumber,
                                        colors = colors,
                                        themeMode = themeMode,
                                        fontScale = fontScale,
                                        fontBold = fontBold,
                                        showSurahFrame = chunk.verses.firstOrNull()?.number == 1,
                                        modifier = Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                val firstAyah = chunk.verses.firstOrNull()?.number ?: 1
                                                onOpenTafsir(surah.number, firstAyah)
                                            }
                                        )
                                    )
                                }
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

                        // Floating Hints Row underneath top bar if visible (suppressed when reciting)
                        if (!isPlaying && (showIndexHint || showNavHint)) {
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
                    visible = !isPlaying && showControlsHint && shouldShowBars,
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
                            modifier = Modifier.width(295.dp)
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
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                        .padding(vertical = 8.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_layout_text),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.sp,
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
                                        .padding(vertical = 8.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_layout_pages),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.sp,
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
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
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

                                val isIndoPakSelected = layoutMode == QuranLayoutMode.INDOPAK_13_LINES
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (isIndoPakSelected) colors.floatingPillBg else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isIndoPakSelected) colors.floatingPillActiveIcon.copy(alpha = 0.5f) else Color.Transparent,
                                            RoundedCornerShape(11.dp)
                                        )
                                        .clickable { onLayoutModeChange(QuranLayoutMode.INDOPAK_13_LINES) }
                                        .padding(vertical = 8.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.quran_layout_indopak_13),
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.sp,
                                            fontWeight = if (isIndoPakSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isIndoPakSelected) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (colors.isLight) AtharPrimary.copy(alpha = 0.12f) else AtharPrimaryLight.copy(alpha = 0.18f))
                                                .border(
                                                    0.5.dp,
                                                    if (colors.isLight) AtharPrimary.copy(alpha = 0.35f) else AtharPrimaryLight.copy(alpha = 0.4f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.quran_layout_indopak_badge),
                                                fontFamily = ThmanyahSans,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (colors.isLight) AtharPrimary else AtharPrimaryLight,
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

                // Auto-scroll indicator & toggle when reciting
                AnimatedVisibility(
                    visible = shouldShowBars && isPlaying,
                    enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                    exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.floatingPillBg.copy(alpha = 0.95f))
                            .border(
                                1.dp,
                                if (autoScrollEnabled) colors.floatingPillActiveIcon.copy(alpha = 0.5f) else colors.floatingPillBorder,
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { autoScrollEnabled = !autoScrollEnabled }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (autoScrollEnabled) colors.floatingPillActiveIcon else colors.dividerText.copy(alpha = 0.5f))
                            )
                            Text(
                                text = if (isArabic) {
                                    if (autoScrollEnabled) "التمرير التلقائي مفعّل" else "التمرير التلقائي متوقف"
                                } else {
                                    if (autoScrollEnabled) "Auto-scroll On" else "Auto-scroll Paused"
                                },
                                fontFamily = ThmanyahSans,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (autoScrollEnabled) colors.floatingPillActiveIcon else colors.dividerText
                            )
                        }
                    }
                }

                // Floating bottom controls dock
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

            // Network warning banner when reciting requires internet
            AnimatedVisibility(
                visible = audioAlertMessage != null,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp, start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (colors.isLight) Color(0xFF1E241A) else Color(0xFF181E16))
                        .border(1.2.dp, Color(0xFFE57373).copy(alpha = 0.65f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WifiOff,
                            contentDescription = null,
                            tint = Color(0xFFFF8A80),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = audioAlertMessage ?: "",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ─── FLOATING TAFSIR "AL-MA'RIFAH" ACTION BUTTON (Apple Music lyrics translate style) ───
            AnimatedVisibility(
                visible = shouldShowBars,
                enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 2 },
                exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 2 },
                modifier = Modifier
                    .align(if (isArabic) Alignment.BottomStart else Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(bottom = 96.dp, start = 18.dp, end = 18.dp)
            ) {
                AlDirayahFloatingButton(
                    onClick = {
                        val currentAyahTarget = activeVerseNumber ?: run {
                            when (layoutMode) {
                                QuranLayoutMode.TEXT -> {
                                    val item = textItems.getOrNull(listState.firstVisibleItemIndex)
                                    (item as? QuranTextItem.VerseItem)?.verse?.number ?: 1
                                }
                                QuranLayoutMode.INDOPAK_13_LINES, QuranLayoutMode.PAGES_SVG -> {
                                    pageChunks?.getOrNull(listState.firstVisibleItemIndex)?.verses?.firstOrNull()?.number ?: 1
                                }
                            }
                        }
                        onOpenTafsir(surah.number, currentAyahTarget)
                    }
                )
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


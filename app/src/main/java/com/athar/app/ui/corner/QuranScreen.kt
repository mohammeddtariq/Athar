package com.athar.app.ui.corner

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.QuranRepository
import com.athar.app.data.QuranThemeMode
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.QuranSurahNames
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSans
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

/**
 * Built-in Quran reader screen matching the Islamic Mushaf reference design:
 * - Authentic Uthmani Hafs calligraphy text with ornate \u06DD ayah markers.
 * - Calligraphic Surah titles via QuranSurahNames font.
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

    var themeMode by remember(savedThemeMode) { mutableStateOf(savedThemeMode) }
    var fontScale by remember(savedFontScale) { mutableFloatStateOf(savedFontScale) }
    var fontBold by remember { mutableStateOf(false) }

    var openSurah by remember { mutableStateOf<SurahMeta?>(null) }
    var searchQuery by remember { mutableStateOf("") }

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
            onThemeChange = { newMode ->
                themeMode = newMode
                scope.launch { appPrefs.setQuranThemeMode(newMode) }
            },
            onFontScaleChange = { newScale ->
                val clamped = newScale.coerceIn(0.75f, 1.6f)
                fontScale = clamped
                scope.launch { appPrefs.setQuranFontScale(clamped) }
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

    // Surah List Index View
    SurahListScreen(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        colors = colors,
        onBack = onBack,
        onSelectSurah = { openSurah = it }
    )
}

@Composable
private fun SurahListScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    colors: QuranReaderColors,
    onBack: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit
) {
    val filtered = remember(query) {
        if (query.isBlank()) allSurahs
        else allSurahs.filter {
            it.arabicName.contains(query.trim()) ||
                it.englishName.contains(query.trim(), ignoreCase = true) ||
                it.number.toString() == query.trim()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
                    .background(colors.circleButtonBg)
                    .border(1.dp, colors.circleButtonBorder, CircleShape)
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
                    tint = colors.circleButtonIcon,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.pillBg)
                    .border(1.dp, colors.pillBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 26.dp, vertical = 8.dp)
            ) {
                Text(
                    stringResource(R.string.quran_title),
                    fontFamily = ThmanyahSerifText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = colors.pillText
                )
            }

            Spacer(Modifier.size(46.dp))
        }

        // Search Bar
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(colors.searchBg)
                .border(1.dp, colors.searchBorder, RoundedCornerShape(22.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colors.text,
                    textAlign = TextAlign.End
                ),
                cursorBrush = SolidColor(if (colors.isLight) colors.text else AtharPrimary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            stringResource(R.string.quran_search),
                            fontFamily = ThmanyahSans,
                            fontSize = 14.sp,
                            color = colors.dividerText,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    inner()
                }
            )
        }

        Spacer(Modifier.height(4.dp))

        // Surah Items List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 120.dp)
        ) {
            items(filtered, key = { it.number }) { surah ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBg)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectSurah(surah) }
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Surah Number Pill
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(colors.circleButtonBg)
                                .border(1.dp, colors.circleButtonBorder, RoundedCornerShape(11.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${surah.number}",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.dividerText
                            )
                        }

                        // Right: Calligraphic Surah Name and Info
                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = QuranRepository.getSurahTitleGlyph(surah.number),
                                    fontFamily = QuranSurahNames,
                                    fontSize = 26.sp,
                                    color = colors.pillText
                                )
                            }
                            Text(
                                text = "${surah.englishName} • " + stringResource(R.string.quran_ayahs, surah.ayahs),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = colors.dividerText,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

private val AYAH_MARKER_REGEX = Regex("\u06DD[٠-٩]+")

@Composable
private fun SurahReader(
    surah: SurahMeta,
    themeMode: QuranThemeMode,
    colors: QuranReaderColors,
    fontScale: Float,
    fontBold: Boolean,
    onThemeChange: (QuranThemeMode) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onToggleBold: () -> Unit,
    onBackToList: () -> Unit,
    onSelectSurah: (SurahMeta) -> Unit,
    onNextSurah: () -> Unit
) {
    val context = LocalContext.current
    var displayText by remember(surah.number) { mutableStateOf<String?>(null) }
    var loadFailed by remember(surah.number) { mutableStateOf(false) }
    var attempt by remember(surah.number) { mutableIntStateOf(0) }

    // Next surah preview for continuous Mushaf reading
    val nextSurah = remember(surah.number) {
        val idx = allSurahs.indexOfFirst { it.number == surah.number }
        if (idx in 0 until allSurahs.lastIndex) allSurahs[idx + 1] else null
    }
    var nextSurahText by remember(surah.number) { mutableStateOf<String?>(null) }

    // Floating panels state
    var showFontPanel by remember { mutableStateOf(false) }
    var showThemePanel by remember { mutableStateOf(false) }

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

        // Initialize and stream recitation (Mishary Rashid Alafasy)
        isAudioLoading = true
        val surah3Digit = surah.number.toString().padStart(3, '0')
        val audioUrl = "https://server8.mp3quran.net/afs/$surah3Digit.mp3"

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

    // Load surah text (bundled assets -> disk cache -> network)
    LaunchedEffect(surah.number, attempt) {
        displayText = null
        loadFailed = false
        listState.scrollToItem(0)

        val text = QuranRepository.getSurahText(context.applicationContext, surah.number)
            ?: readableSurahText[surah.number]?.replace("﴿", "\u06DD")?.replace("﴾", "")
        displayText = text
        loadFailed = displayText == null

        // Preload next surah text for seamless continuous mushaf reading
        if (nextSurah != null) {
            nextSurahText = QuranRepository.getSurahText(context.applicationContext, nextSurah.number)
        } else {
            nextSurahText = null
        }
    }

    // Build annotated string with styled ayah rosettes (\u06DD)
    val annotated = remember(displayText, colors.ayahMarker) {
        displayText?.let { raw ->
            buildAnnotatedString {
                var cursor = 0
                for (match in AYAH_MARKER_REGEX.findAll(raw)) {
                    append(raw.substring(cursor, match.range.first))
                    withStyle(SpanStyle(color = colors.ayahMarker)) {
                        append(match.value)
                    }
                    cursor = match.range.last + 1
                }
                append(raw.substring(cursor))
            }
        }
    }

    val nextAnnotated = remember(nextSurahText, colors.ayahMarker) {
        nextSurahText?.let { raw ->
            buildAnnotatedString {
                var cursor = 0
                for (match in AYAH_MARKER_REGEX.findAll(raw)) {
                    append(raw.substring(cursor, match.range.first))
                    withStyle(SpanStyle(color = colors.ayahMarker)) {
                        append(match.value)
                    }
                    cursor = match.range.last + 1
                }
                append(raw.substring(cursor))
            }
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
            // ─── TOP BAR (Exact match to reference screenshot) ───
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

                // Center: Calligraphic Surah Name Pill
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
                        text = QuranRepository.getSurahTitleGlyph(surah.number),
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
                annotated != null -> {
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
                                Text(
                                    text = "\uFDFD", // ﷽ calligraphic ligature
                                    fontFamily = QuranUthmanicHafs,
                                    fontSize = (34 * fontScale).sp,
                                    color = colors.text,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 22.dp)
                                )
                            }
                        }

                        // Main Surah Verses
                        item(key = "text_${surah.number}") {
                            Text(
                                text = annotated,
                                fontFamily = QuranUthmanicHafs,
                                fontWeight = if (fontBold) FontWeight.Bold else FontWeight.Normal,
                                fontSize = (24 * fontScale).sp,
                                lineHeight = (48 * fontScale).sp,
                                color = colors.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Surah Divider (matches reference screenshot: ─── 1 ───)
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

                        // Next Surah Header & Content (Continuous Mushaf Flow)
                        if (nextSurah != null) {
                            item(key = "next_header_${nextSurah.number}") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Next Surah Pill
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(colors.pillBg)
                                            .border(1.dp, colors.pillBorder, RoundedCornerShape(24.dp))
                                            .clickable { onSelectSurah(nextSurah) }
                                            .padding(horizontal = 28.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = QuranRepository.getSurahTitleGlyph(nextSurah.number),
                                            fontFamily = QuranSurahNames,
                                            fontSize = 28.sp,
                                            color = colors.pillText,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    // Next Surah Basmala (except 9)
                                    if (nextSurah.number != 9) {
                                        Spacer(Modifier.height(18.dp))
                                        Text(
                                            text = "\uFDFD",
                                            fontFamily = QuranUthmanicHafs,
                                            fontSize = (34 * fontScale).sp,
                                            color = colors.text,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }

                            if (nextAnnotated != null) {
                                item(key = "next_text_${nextSurah.number}") {
                                    Text(
                                        text = nextAnnotated,
                                        fontFamily = QuranUthmanicHafs,
                                        fontWeight = if (fontBold) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = (24 * fontScale).sp,
                                        lineHeight = (48 * fontScale).sp,
                                        color = colors.text,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectSurah(nextSurah) }
                                    )
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

        // ─── FLOATING BOTTOM CAPSULE (Exact match to reference screenshot) ───
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 26.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Font Adjustment Panel
                AnimatedVisibility(
                    visible = showFontPanel,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                ) {
                    FontAdjustmentPanel(
                        fontScale = fontScale,
                        fontBold = fontBold,
                        colors = colors,
                        onFontScaleChange = onFontScaleChange,
                        onToggleBold = onToggleBold
                    )
                }

                // Theme Mode Switcher Panel
                AnimatedVisibility(
                    visible = showThemePanel,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                ) {
                    ThemeSelectorPanel(
                        currentMode = themeMode,
                        colors = colors,
                        onSelectMode = {
                            onThemeChange(it)
                            showThemePanel = false
                        }
                    )
                }

                // Main Capsule Pill (TT | Palette | Play)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(colors.floatingPillBg)
                        .border(1.dp, colors.floatingPillBorder, RoundedCornerShape(32.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. TT Font Size Button
                        CapsuleCircleButton(
                            onClick = {
                                showFontPanel = !showFontPanel
                                if (showFontPanel) showThemePanel = false
                            },
                            selected = showFontPanel,
                            colors = colors
                        ) {
                            Text(
                                "TT",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (showFontPanel) colors.floatingPillActiveIcon else colors.floatingPillItemIcon
                            )
                        }

                        // 2. Palette Theme Switcher Button
                        CapsuleCircleButton(
                            onClick = {
                                showThemePanel = !showThemePanel
                                if (showThemePanel) showFontPanel = false
                            },
                            selected = showThemePanel,
                            colors = colors
                        ) {
                            Icon(
                                Icons.Rounded.Palette,
                                contentDescription = "Theme",
                                tint = if (showThemePanel) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 3. Play / Pause Recitation Button
                        CapsuleCircleButton(
                            onClick = { toggleAudio() },
                            selected = isPlaying,
                            colors = colors
                        ) {
                            if (isAudioLoading) {
                                CircularProgressIndicator(
                                    color = colors.floatingPillActiveIcon,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = if (isPlaying) colors.floatingPillActiveIcon else colors.floatingPillItemIcon,
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

@Composable
private fun CapsuleCircleButton(
    onClick: () -> Unit,
    selected: Boolean = false,
    colors: QuranReaderColors,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
                if (selected) colors.floatingPillActiveIcon.copy(alpha = 0.2f)
                else colors.floatingPillItemBg
            )
            .border(
                1.dp,
                if (selected) colors.floatingPillActiveIcon.copy(alpha = 0.6f)
                else Color.Transparent,
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun FontAdjustmentPanel(
    fontScale: Float,
    fontBold: Boolean,
    colors: QuranReaderColors,
    onFontScaleChange: (Float) -> Unit,
    onToggleBold: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(colors.cardBg)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease Font
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.floatingPillItemBg)
                .clickable { onFontScaleChange(fontScale - 0.1f) },
            contentAlignment = Alignment.Center
        ) {
            Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.floatingPillItemIcon)
        }

        // Percentage Indicator
        Text(
            text = "${(fontScale * 100).toInt()}%",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = colors.text
        )

        // Increase Font
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.floatingPillItemBg)
                .clickable { onFontScaleChange(fontScale + 0.1f) },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.floatingPillItemIcon)
        }

        // Bold Toggle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (fontBold) colors.floatingPillActiveIcon.copy(alpha = 0.25f)
                    else colors.floatingPillItemBg
                )
                .border(
                    1.dp,
                    if (fontBold) colors.floatingPillActiveIcon else Color.Transparent,
                    CircleShape
                )
                .clickable { onToggleBold() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "B",
                fontFamily = ThmanyahSerifText,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = if (fontBold) colors.floatingPillActiveIcon else colors.floatingPillItemIcon
            )
        }
    }
}

@Composable
private fun ThemeSelectorPanel(
    currentMode: QuranThemeMode,
    colors: QuranReaderColors,
    onSelectMode: (QuranThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(colors.cardBg)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode 1: Pure AMOLED
        ThemeChip(
            label = stringResource(R.string.quran_theme_amoled),
            selected = currentMode == QuranThemeMode.AMOLED,
            chipBg = Color(0xFF000000),
            chipBorder = Color(0xFF333333),
            textColor = Color.White,
            activeColor = AtharPrimary,
            onClick = { onSelectMode(QuranThemeMode.AMOLED) }
        )

        // Mode 2: App Dark Olive/Sage
        ThemeChip(
            label = stringResource(R.string.quran_theme_olive),
            selected = currentMode == QuranThemeMode.DARK_OLIVE,
            chipBg = Color(0xFF0A0C08),
            chipBorder = Color(0xFF242A20),
            textColor = Color(0xFFEDEFEA),
            activeColor = AtharPrimary,
            onClick = { onSelectMode(QuranThemeMode.DARK_OLIVE) }
        )

        // Mode 3: Light Mushaf Paper
        ThemeChip(
            label = stringResource(R.string.quran_theme_light),
            selected = currentMode == QuranThemeMode.LIGHT,
            chipBg = Color(0xFFFBF9F4),
            chipBorder = Color(0xFFDFD9CC),
            textColor = Color(0xFF1A1D18),
            activeColor = Color(0xFF2D4B26),
            onClick = { onSelectMode(QuranThemeMode.LIGHT) }
        )
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    chipBg: Color,
    chipBorder: Color,
    textColor: Color,
    activeColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(chipBg)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) activeColor else chipBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selected) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = label,
            fontFamily = ThmanyahSans,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

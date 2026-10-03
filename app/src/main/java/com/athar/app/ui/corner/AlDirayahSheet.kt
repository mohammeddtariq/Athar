package com.athar.app.ui.corner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.data.AyahTafsir
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.QuranRepository
import com.athar.app.data.TafsirEdition
import com.athar.app.data.TafsirRepository
import com.athar.app.data.formatDigits
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifText
import java.util.Locale

private sealed interface TafsirUiState {
    data object Loading : TafsirUiState
    data class Success(val tafsir: AyahTafsir) : TafsirUiState
    data class Error(val message: String, val isOffline: Boolean = false) : TafsirUiState
}

/**
 * "Al-Dirayah" (الدِّرَايَة) Floating Tafsir & Contemplation Modal Sheet.
 *
 * Features:
 * - Fluid entrance animation with semi-transparent blurred backdrop.
 * - Strict bilingual commentary isolation (Arabic and English).
 * - Segmented language switcher to seamlessly toggle commentary and pronunciation.
 * - Classical 5-book Tafsir switcher (Al-Sa'di, Ibn Kathir, Al-Tabari, Al-Qurtubi, Al-Muyassar).
 * - Fullscreen expand/collapse toggle for deep contemplation.
 * - Searchable 114-Surah index menu when opened from main tab or on demand.
 * - Previous/Next Ayah steppers matching authentic LTR/RTL reading directions.
 */
@Composable
fun AlDirayahSheet(
    initialSurahNumber: Int = 1,
    initialAyahNumber: Int = 1,
    startWithSurahIndex: Boolean = false,
    numberStyle: NumberStylePreference = NumberStylePreference.WESTERN,
    onDismiss: () -> Unit,
    onNavigateToAyah: ((surah: Int, ayah: Int) -> Unit)? = null
) {
    val context = LocalContext.current

    var currentSurah by remember { mutableIntStateOf(initialSurahNumber.coerceIn(1, 114)) }
    val surahMeta = remember(currentSurah) {
        allSurahs.firstOrNull { it.number == currentSurah } ?: allSurahs[0]
    }

    var currentAyah by remember {
        mutableIntStateOf(initialAyahNumber.coerceIn(1, surahMeta.ayahs))
    }

    // Keep ayah clamped when surah changes
    LaunchedEffect(currentSurah) {
        currentAyah = currentAyah.coerceIn(1, surahMeta.ayahs)
    }

    var selectedEdition by remember { mutableStateOf(TafsirEdition.SAADI) }
    var isEnglishMode by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(startWithSurahIndex) }
    var showSurahPicker by remember { mutableStateOf(startWithSurahIndex) }
    var uiState by remember { mutableStateOf<TafsirUiState>(TafsirUiState.Loading) }

    val effectiveArabic = !isEnglishMode
    val sheetLayoutDirection = if (effectiveArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Fetch Tafsir whenever surah, ayah, or edition changes
    LaunchedEffect(currentSurah, currentAyah, selectedEdition) {
        uiState = TafsirUiState.Loading
        val result = TafsirRepository.getAyahTafsir(
            context = context,
            surah = currentSurah,
            ayah = currentAyah,
            edition = selectedEdition
        )
        uiState = result.fold(
            onSuccess = { TafsirUiState.Success(it) },
            onFailure = {
                val isOffline = !isNetworkAvailable(context)
                TafsirUiState.Error(
                    message = it.localizedMessage ?: "Error loading Tafsir",
                    isOffline = isOffline
                )
            }
        )
    }

    BackHandler {
        if (showSurahPicker && !startWithSurahIndex) {
            showSurahPicker = false
        } else {
            onDismiss()
        }
    }

    var currentVerseText by remember { mutableStateOf("") }
    LaunchedEffect(currentSurah, currentAyah) {
        val verses = QuranRepository.getSurahVerses(context, currentSurah)
        currentVerseText = verses?.firstOrNull { it.number == currentAyah }?.text ?: ""
    }

    val cardPaddingH by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 14.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "cardPaddingH"
    )
    val cardPaddingV by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 24.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "cardPaddingV"
    )
    val cardCornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 26.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "cardCornerRadius"
    )
    val cardHeightFraction by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.84f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "cardHeightFraction"
    )

    // Backdrop Scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (startWithSurahIndex) {
                    SolidColor(Color(0xFF070B06).copy(alpha = 0.95f))
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070B06).copy(alpha = 0.70f),
                            Color(0xFF030502).copy(alpha = 0.85f)
                        )
                    )
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating Sheet Card
        Box(
            modifier = Modifier
                .padding(horizontal = cardPaddingH, vertical = cardPaddingV)
                .fillMaxWidth()
                .fillMaxHeight(cardHeightFraction)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Prevent taps inside sheet from dismissing
                )
                .clip(RoundedCornerShape(cardCornerRadius))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF141C13).copy(alpha = 0.92f),
                            Color(0xFF0C120B).copy(alpha = 0.96f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            AtharPrimaryLight.copy(alpha = 0.40f),
                            Color(0xFF222B1E).copy(alpha = 0.30f)
                        )
                    ),
                    shape = RoundedCornerShape(cardCornerRadius)
                )
                .shadow(elevation = 28.dp, shape = RoundedCornerShape(cardCornerRadius))
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides sheetLayoutDirection) {
                if (showSurahPicker) {
                    SurahPickerView(
                        isArabic = effectiveArabic,
                        isExpanded = isExpanded,
                        onSelectSurah = { selectedNum ->
                            currentSurah = selectedNum
                            currentAyah = 1
                            showSurahPicker = false
                            isExpanded = true
                        },
                        onClose = {
                            if (startWithSurahIndex) {
                                onDismiss()
                            } else {
                                showSurahPicker = false
                            }
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (isExpanded) Modifier.statusBarsPadding() else Modifier.padding(top = 8.dp))
                            .navigationBarsPadding()
                    ) {
                        // Top Drag Handle & Controls Bar (Sticky)
                        AlDirayahTopBar(
                            isExpanded = isExpanded,
                            isEnglishMode = isEnglishMode,
                            isArabic = effectiveArabic,
                            onToggleExpand = { isExpanded = !isExpanded },
                            onSelectLanguageMode = { isEnglishMode = it },
                            onClose = onDismiss
                        )

                        // Classical 5-Book Tafsir Switcher
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 4.dp)
                        ) {
                            TafsirBooksBar(
                                selectedEdition = selectedEdition,
                                isArabic = effectiveArabic,
                                onSelectEdition = { selectedEdition = it }
                            )
                        }

                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = Color(0xFF222B1E),
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        // Scrollable Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 18.dp)
                        ) {
                            // Sacred Ayah Card
                            val activeTafsir = (uiState as? TafsirUiState.Success)?.tafsir
                            SacredAyahCard(
                                surahMeta = surahMeta,
                                ayahNumber = currentAyah,
                                verseText = currentVerseText.ifEmpty {
                                    activeTafsir?.verseTextArabic ?: ""
                                },
                                englishTranslation = activeTafsir?.englishTranslation.orEmpty(),
                                englishTransliteration = activeTafsir?.englishTransliteration.orEmpty(),
                                isEnglishMode = isEnglishMode,
                                isArabic = effectiveArabic,
                                onOpenSurahPicker = { showSurahPicker = true }
                            )

                            Spacer(Modifier.height(14.dp))

                            // Tafsir Content Section (Single isolated commentary card matching design)
                            AnimatedContent(
                                targetState = uiState,
                                transitionSpec = {
                                    fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                                },
                                label = "tafsirContent"
                            ) { state ->
                                when (state) {
                                    is TafsirUiState.Loading -> {
                                        TafsirLoadingView(isArabic = effectiveArabic)
                                    }
                                    is TafsirUiState.Error -> {
                                        TafsirErrorView(
                                            isArabic = effectiveArabic,
                                            errorMessage = state.message,
                                            isOffline = state.isOffline,
                                            onRetry = {
                                                selectedEdition = selectedEdition
                                            }
                                        )
                                    }
                                    is TafsirUiState.Success -> {
                                        TafsirContentView(
                                            tafsir = state.tafsir,
                                            isEnglishMode = isEnglishMode
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(18.dp))
                        }

                        // Bottom Navigation & Actions Bar
                        AlDirayahBottomBar(
                            currentSurah = currentSurah,
                            currentAyah = currentAyah,
                            maxAyahs = surahMeta.ayahs,
                            numberStyle = numberStyle,
                            isArabic = effectiveArabic,
                            onPrevAyah = {
                                if (currentAyah > 1) {
                                    currentAyah -= 1
                                    onNavigateToAyah?.invoke(currentSurah, currentAyah)
                                } else if (currentSurah > 1) {
                                    currentSurah -= 1
                                    val prevMeta = allSurahs.firstOrNull { it.number == currentSurah } ?: allSurahs[0]
                                    currentAyah = prevMeta.ayahs
                                    onNavigateToAyah?.invoke(currentSurah, currentAyah)
                                }
                            },
                            onNextAyah = {
                                if (currentAyah < surahMeta.ayahs) {
                                    currentAyah += 1
                                    onNavigateToAyah?.invoke(currentSurah, currentAyah)
                                } else if (currentSurah < 114) {
                                    currentSurah += 1
                                    currentAyah = 1
                                    onNavigateToAyah?.invoke(currentSurah, currentAyah)
                                }
                            },
                            onCopy = {
                                val state = uiState
                                if (state is TafsirUiState.Success) {
                                    val textToCopy = buildString {
                                        append("﴿ ")
                                        append(state.tafsir.verseTextArabic)
                                        append(" ﴾ [")
                                        append(state.tafsir.surahNameArabic)
                                        append(": ")
                                        append(state.tafsir.ayahNumber)
                                        append("]\n\n")
                                        if (effectiveArabic) {
                                            append(state.tafsir.edition.arabicName)
                                            append(":\n")
                                            append(state.tafsir.arabicTafsir)
                                        } else {
                                            append(state.tafsir.edition.englishName)
                                            append(":\n")
                                            append(state.tafsir.englishTafsir)
                                        }
                                        append("\n\n— Athar • أثـر")
                                    }
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Athar Tafsir", textToCopy))
                                    Toast.makeText(
                                        context,
                                        if (effectiveArabic) "تم نسخ التفسير إلى الحافظة" else "Tafsir copied to clipboard",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onShare = {
                                val state = uiState
                                if (state is TafsirUiState.Success) {
                                    val shareText = buildString {
                                        append("﴿ ")
                                        append(state.tafsir.verseTextArabic)
                                        append(" ﴾ [")
                                        append(state.tafsir.surahNameArabic)
                                        append(": ")
                                        append(state.tafsir.ayahNumber)
                                        append("]\n\n")
                                        if (effectiveArabic) {
                                            append(state.tafsir.edition.arabicName)
                                            append(":\n")
                                            append(state.tafsir.arabicTafsir)
                                        } else {
                                            append(state.tafsir.edition.englishName)
                                            append(":\n")
                                            append(state.tafsir.englishTafsir)
                                        }
                                        append("\n\n— Athar • أثـر")
                                    }
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Tafsir: ${state.tafsir.surahNameArabic}")
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Tafsir"))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen searchable 114-Surah index menu.
 */
@Composable
private fun SurahPickerView(
    isArabic: Boolean,
    isExpanded: Boolean = true,
    onSelectSurah: (surahNumber: Int) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) allSurahs
        else allSurahs.filter {
            it.arabicName.contains(searchQuery.trim()) ||
            it.englishName.contains(searchQuery.trim(), ignoreCase = true) ||
            it.number.toString() == searchQuery.trim() ||
            formatDigits(it.number.toString(), NumberStylePreference.ARABIC_INDIC).contains(searchQuery.trim())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (isExpanded) Modifier.statusBarsPadding() else Modifier.padding(top = 10.dp))
            .navigationBarsPadding()
    ) {
        // Drag Handle
        Box(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 6.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(4.5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.22f))
            )
        }

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) "الدِّرَايَة • فهرس السور" else "Al Dirayah • Surah Index",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (isArabic) "اختر سورة لعرض تفسيرها آية بآية" else "Select a surah for verse-by-verse commentary",
                    fontFamily = ThmanyahSans,
                    fontSize = 12.sp,
                    color = AtharTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        // Search Box
        Box(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF161E14))
                .border(1.dp, Color(0xFF283623), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        textDirection = if (isArabic) TextDirection.Rtl else TextDirection.Ltr
                    ),
                    cursorBrush = SolidColor(AtharPrimaryLight),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = if (isArabic) "ابحث باسم السورة أو رقمها…" else "Search surah by name or number…",
                                fontFamily = ThmanyahSans,
                                fontSize = 13.5.sp,
                                color = AtharTextSecondary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Start
                            )
                        }
                        inner()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { searchQuery = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear",
                            tint = AtharTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // Surah List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredSurahs, key = { it.number }) { surah ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF141912))
                        .border(1.dp, Color(0xFF243021), RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectSurah(surah.number) }
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Number Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AtharPrimary.copy(alpha = 0.20f))
                                .border(0.8.dp, AtharPrimaryLight.copy(alpha = 0.40f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isArabic) formatDigits(surah.number.toString(), NumberStylePreference.ARABIC_INDIC)
                                       else surah.number.toString(),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = AtharPrimaryLight
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        // Names & Details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "سورة ${surah.arabicName}" else "Surah ${surah.englishName}",
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = Color(0xFFF3F7F2)
                                )
                                Text(
                                    text = if (isArabic) surah.englishName else surah.arabicName,
                                    fontFamily = if (isArabic) ThmanyahSans else QuranUthmanicHafs,
                                    fontSize = 12.sp,
                                    color = AtharTextSecondary.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            val typeLabel = if (isArabic) surah.revelationType.arabicLabel else surah.revelationType.englishLabel
                            val ayahsLabel = if (isArabic) "${formatDigits(surah.ayahs.toString(), NumberStylePreference.ARABIC_INDIC)} آية"
                                             else "${surah.ayahs} Verses"
                            Text(
                                text = "$typeLabel • $ayahsLabel",
                                fontFamily = ThmanyahSans,
                                fontSize = 11.5.sp,
                                color = AtharTextSecondary
                            )
                        }

                        Icon(
                            imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = AtharPrimaryLight.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top control bar with drag indicator, title branding, segmented language switcher, expand/collapse, and close.
 */
@Composable
private fun AlDirayahTopBar(
    isExpanded: Boolean,
    isEnglishMode: Boolean,
    isArabic: Boolean,
    onToggleExpand: () -> Unit,
    onSelectLanguageMode: (isEnglish: Boolean) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag Handle Pill
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(4.5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.22f))
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Title Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AtharPrimary.copy(alpha = 0.16f))
                    .border(1.dp, AtharPrimaryLight.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 9.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (isArabic) "الدِّرَايَة" else "Al Dirayah",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    color = AtharPrimaryLight
                )
            }

            Spacer(Modifier.weight(1f))

            // Segmented Language Switcher (Crystal clear active state)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF161E14))
                    .border(1.dp, Color(0xFF283623), RoundedCornerShape(14.dp))
                    .padding(2.5.dp)
            ) {
                val arActive = !isEnglishMode
                val enActive = isEnglishMode

                // Arabic Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (arActive) AtharPrimary else Color.Transparent)
                        .border(
                            width = if (arActive) 1.dp else 0.dp,
                            color = if (arActive) AtharPrimaryLight.copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(11.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectLanguageMode(false) }
                        )
                        .padding(horizontal = 9.dp, vertical = 4.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "عربي",
                        fontFamily = ThmanyahSans,
                        fontWeight = if (arActive) FontWeight.Black else FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = if (arActive) Color.White else AtharTextSecondary.copy(alpha = 0.75f)
                    )
                }

                // English Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (enActive) AtharPrimary else Color.Transparent)
                        .border(
                            width = if (enActive) 1.dp else 0.dp,
                            color = if (enActive) AtharPrimaryLight.copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(11.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectLanguageMode(true) }
                        )
                        .padding(horizontal = 9.dp, vertical = 4.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "English",
                        fontFamily = ThmanyahSans,
                        fontWeight = if (enActive) FontWeight.Black else FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = if (enActive) Color.White else AtharTextSecondary.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Expand / Collapse Fullscreen Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggleExpand
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
                    contentDescription = "Toggle Expand",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.width(6.dp))

            // Close Button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

/**
 * Sacred Ayah header card displaying the calligraphic verse text and surah selector.
 */
@Composable
private fun SacredAyahCard(
    surahMeta: SurahMeta,
    ayahNumber: Int,
    verseText: String,
    englishTranslation: String = "",
    englishTransliteration: String = "",
    isEnglishMode: Boolean,
    isArabic: Boolean,
    onOpenSurahPicker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF162014).copy(alpha = 0.75f))
            .border(1.dp, Color(0xFF283624).copy(alpha = 0.65f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ALWAYS Persistent Arabic header for Sacred Ayah
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Surah Pill with quick switch action (always Arabic)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AtharPrimary.copy(alpha = 0.18f))
                        .border(0.8.dp, AtharPrimaryLight.copy(alpha = 0.40f), RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenSurahPicker
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "سورة ${surahMeta.arabicName}",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AtharPrimaryLight
                        )
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = "Change Surah",
                            tint = AtharPrimaryLight.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "•",
                    color = AtharTextSecondary.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )

                // Ayah pill (always Arabic-Indic numerals)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "الآية ${formatDigits(ayahNumber.toString(), NumberStylePreference.ARABIC_INDIC)}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            if (verseText.isNotBlank()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "\u202B﴿ $verseText ﴾\u202C",
                    fontFamily = QuranUthmanicHafs,
                    fontSize = 22.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFF2F6F0),
                    style = TextStyle(
                        textDirection = TextDirection.Rtl,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // English Mode additions: Pronunciation (transliteration in English letters) & Translation
        if (isEnglishMode) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                if (englishTransliteration.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFF263322))
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE5C158).copy(alpha = 0.14f))
                            .border(0.6.dp, Color(0xFFE5C158).copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Pronunciation",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = Color(0xFFE5C158)
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = englishTransliteration,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.5.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFE8EFE5),
                        style = TextStyle(
                            textDirection = TextDirection.Ltr,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
                    )
                }

                if (englishTranslation.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    if (englishTransliteration.isBlank()) {
                        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFF263322))
                        Spacer(Modifier.height(10.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AtharPrimary.copy(alpha = 0.16f))
                            .border(0.6.dp, AtharPrimaryLight.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Translation (Saheeh Int.)",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = AtharPrimaryLight
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = englishTranslation,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFCAD7C8),
                        style = TextStyle(
                            textDirection = TextDirection.Ltr,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * Classical 5-Book Tafsir switcher pills matching Athar's authentic styling.
 */
@Composable
private fun TafsirBooksBar(
    selectedEdition: TafsirEdition,
    isArabic: Boolean,
    onSelectEdition: (TafsirEdition) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TafsirEdition.entries.forEach { edition ->
                val isSelected = edition == selectedEdition
                val animatedBg by animateColorAsState(
                    targetValue = if (isSelected) AtharPrimary.copy(alpha = 0.30f) else Color(0xFF171D15),
                    animationSpec = tween(180),
                    label = "chipBg"
                )
                val animatedBorder by animateColorAsState(
                    targetValue = if (isSelected) AtharPrimaryLight else Color(0xFF283424),
                    animationSpec = tween(180),
                    label = "chipBorder"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(animatedBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = animatedBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .then(
                            if (isSelected) {
                                Modifier.shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = AtharPrimaryLight.copy(alpha = 0.35f)
                                )
                            } else Modifier
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectEdition(edition) }
                        )
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) edition.arabicName else edition.englishName,
                        fontFamily = ThmanyahSans,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = if (isSelected) AtharPrimaryLight else AtharTextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // Brief authentic description of the active book
        Text(
            text = if (isArabic) selectedEdition.descriptionArabic else selectedEdition.descriptionEnglish,
            fontFamily = ThmanyahSans,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = AtharTextSecondary.copy(alpha = 0.75f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

/**
 * Main Tafsir text view displaying commentary with dimmed card styling matching target design.
 * Features:
 * - Single isolated card strictly for the selected language.
 * - Green dot bullet • with book edition and language badge.
 * - Words before ':' styled prominent white.
 * - Words after ':' styled sage green.
 * - '{...}' Quran tokens styled in gold.
 * - '«...»' Hadith quotes styled in soft emerald.
 */
@Composable
private fun TafsirContentView(
    tafsir: AyahTafsir,
    isEnglishMode: Boolean
) {
    val isAr = !isEnglishMode
    val containerShape = RoundedCornerShape(20.dp)

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr,
        LocalContentColor provides Color(0xFFBACABA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(containerShape)
                .background(Color(0xFF131B11).copy(alpha = 0.70f))
                .border(1.dp, Color(0xFF263322).copy(alpha = 0.60f), containerShape)
                .padding(18.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bullet + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AtharPrimaryLight)
                    )
                    Text(
                        text = if (isAr) "التفسير بالعربية • ${tafsir.edition.arabicName}"
                               else "English Commentary • ${tafsir.edition.englishName}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFE2EEE0)
                    )
                }

                // Language Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF192217))
                        .border(0.7.dp, Color(0xFF283624), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isAr) "العربية" else "English",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = AtharPrimaryLight
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFF1E281B))
            Spacer(Modifier.height(14.dp))

            // Commentary Text
            val textToDisplay = if (isAr) tafsir.arabicTafsir else tafsir.englishTafsir
            val styledText = remember(textToDisplay) {
                buildStyledTafsirAnnotatedString(textToDisplay)
            }

            Text(
                text = styledText,
                fontFamily = if (isAr) ThmanyahSerifText else ThmanyahSans,
                fontSize = if (isAr) 18.sp else 15.sp,
                lineHeight = if (isAr) 34.sp else 24.sp,
                color = Color(0xFFBACABA),
                style = TextStyle(
                    color = Color(0xFFBACABA),
                    textAlign = TextAlign.Start,
                    textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Formats Tafsir commentary:
 * - Words before ':' are bold and prominent ivory white (#FFFFFF)
 * - Words after ':' are normal weight reading sage (#BACABA)
 * - Ayah/verse tokens '{...}' and '﴿...﴾' are highlighted in warm amber gold (#E5C158)
 * - Hadith quotes '«...»' are styled in soft emerald
 */
private fun buildStyledTafsirAnnotatedString(text: String): AnnotatedString {
    if (text.isBlank()) return AnnotatedString("")
    return try {
        buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { index, rawLine ->
                val line = rawLine.trim()
                if (line.isNotEmpty()) {
                    val isFootnoteHeader = line.startsWith("───────────────") || line.startsWith("الهوامش والتخريج:")
                    val isHeading = line.startsWith("وهي مكية") || line.startsWith("وهي مدنية") ||
                                    line.startsWith("سورة ") || line.startsWith("تفسير سورة") ||
                                    line.startsWith("القول في") || line.startsWith("*") ||
                                    line.startsWith("The Discussion of") || line.startsWith("The Virtues of")

                    val hasBracketColon = line.contains("[") && line.contains("]") &&
                            line.indexOf(':') > line.indexOf('[') && line.indexOf(':') < line.indexOf(']')
                    val colonIdx = if (hasBracketColon) -1 else line.indexOf(':')

                    if (isFootnoteHeader) {
                        appendStyledTafsirChunk(
                            text = line,
                            baseColor = AtharPrimaryLight,
                            baseWeight = FontWeight.Bold
                        )
                    } else if (colonIdx in 1..44 && colonIdx < line.length - 1) {
                        val prefix = line.substring(0, colonIdx + 1)
                        val suffix = line.substring(colonIdx + 1)

                        appendStyledTafsirChunk(
                            text = prefix,
                            baseColor = Color(0xFFFFFFFF),
                            baseWeight = FontWeight.Bold
                        )
                        appendStyledTafsirChunk(
                            text = suffix,
                            baseColor = Color(0xFFBACABA),
                            baseWeight = FontWeight.Normal
                        )
                    } else if (colonIdx in 1..44 && colonIdx == line.length - 1) {
                        appendStyledTafsirChunk(
                            text = line,
                            baseColor = Color(0xFFFFFFFF),
                            baseWeight = FontWeight.Bold
                        )
                    } else {
                        appendStyledTafsirChunk(
                            text = line,
                            baseColor = if (isHeading) Color(0xFFFFFFFF) else Color(0xFFBACABA),
                            baseWeight = if (isHeading) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                if (index < lines.size - 1) {
                    withStyle(SpanStyle(color = Color(0xFFBACABA))) {
                        append("\n")
                    }
                }
            }
        }
    } catch (_: Exception) {
        buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFFBACABA))) {
                append(text)
            }
        }
    }
}

private fun AnnotatedString.Builder.appendStyledTafsirChunk(
    text: String,
    baseColor: Color,
    baseWeight: FontWeight = FontWeight.Normal
) {
    if (text.isEmpty()) return
    try {
        val tokenRegex = Regex("""(\{[^}]+\}|«[^»]+»|﴿[^﴾]+﴾|\([0-9٠-٩]+\)|\[[0-9٠-٩]+\])""")
        var lastIdx = 0
        val matches = tokenRegex.findAll(text).toList()

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1

            if (start > lastIdx && lastIdx < text.length) {
                val nonToken = text.substring(lastIdx, minOf(start, text.length))
                withStyle(SpanStyle(color = baseColor, fontWeight = baseWeight)) {
                    append(nonToken)
                }
            }

            val token = match.value
            when {
                token.startsWith("{") && token.endsWith("}") -> {
                    withStyle(SpanStyle(color = Color(0xFFE5C158), fontWeight = FontWeight.SemiBold)) {
                        append(token)
                    }
                }
                token.startsWith("﴿") && token.endsWith("﴾") -> {
                    withStyle(SpanStyle(color = Color(0xFFE5C158), fontWeight = FontWeight.SemiBold)) {
                        append(token)
                    }
                }
                token.startsWith("«") && token.endsWith("»") -> {
                    withStyle(SpanStyle(color = AtharPrimaryLight, fontWeight = FontWeight.Medium)) {
                        append(token)
                    }
                }
                token.startsWith("(") && token.endsWith(")") -> {
                    withStyle(SpanStyle(color = Color(0xFFE5C158), fontWeight = FontWeight.Bold)) {
                        append(token)
                    }
                }
                token.startsWith("[") && token.endsWith("]") -> {
                    withStyle(SpanStyle(color = AtharPrimaryLight, fontWeight = FontWeight.SemiBold)) {
                        append(token)
                    }
                }
                else -> {
                    withStyle(SpanStyle(color = baseColor, fontWeight = baseWeight)) {
                        append(token)
                    }
                }
            }
            lastIdx = maxOf(lastIdx, end)
        }

        if (lastIdx < text.length) {
            withStyle(SpanStyle(color = baseColor, fontWeight = baseWeight)) {
                append(text.substring(lastIdx))
            }
        }
    } catch (_: Exception) {
        withStyle(SpanStyle(color = baseColor, fontWeight = baseWeight)) {
            append(text)
        }
    }
}

/**
 * Serene loading state with soft pulsing glow indicator.
 */
@Composable
private fun TafsirLoadingView(isArabic: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF161C14))
            .border(1.dp, Color(0xFF263022), RoundedCornerShape(20.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = AtharPrimaryLight,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = if (isArabic) "جاري استحضار معاني التفسير المباركة…" else "Loading contemplative commentary…",
            fontFamily = ThmanyahSans,
            fontSize = 12.5.sp,
            color = AtharTextSecondary.copy(alpha = alpha)
        )
    }
}

/**
 * Error state with retry action button and offline internet detection.
 */
@Composable
private fun TafsirErrorView(
    isArabic: Boolean,
    errorMessage: String,
    isOffline: Boolean = false,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF161C14))
            .border(1.dp, Color(0xFF263022), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isOffline) Icons.Rounded.WifiOff else Icons.Rounded.Refresh,
            contentDescription = null,
            tint = if (isOffline) Color(0xFFFF8A80) else AtharPrimaryLight,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (isOffline) {
                if (isArabic) "لا يوجد اتصال بالإنترنت" else "No Internet Connection"
            } else {
                if (isArabic) "تعذر استحضار التفسير حالياً" else "Unable to load commentary right now"
            },
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isOffline) {
                if (isArabic) "يتطلب استحضار التفسير اتصالاً نشطاً بالإنترنت" else "Fetching Quranic commentary requires an active internet connection"
            } else {
                if (isArabic) "يرجى التحقق من اتصالك بالإنترنت والمحاولة مجدداً" else "Please check your network connection and try again"
            },
            fontFamily = ThmanyahSans,
            fontSize = 11.5.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AtharPrimary.copy(alpha = 0.22f))
                .border(1.dp, AtharPrimaryLight.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRetry
                )
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isArabic) "إعادة المحاولة" else "Retry",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = AtharPrimaryLight
            )
        }
    }
}

/**
 * Bottom actions bar with Prev/Next Ayah steppers, copy, and share actions.
 * Authentically follows reading direction:
 * In Arabic (RTL): [السابق ->] on the right, [<- التالي] on the left.
 * In English (LTR): [<- Previous] on the left, [Next ->] on the right.
 */
@Composable
private fun AlDirayahBottomBar(
    currentSurah: Int,
    currentAyah: Int,
    maxAyahs: Int,
    numberStyle: NumberStylePreference,
    isArabic: Boolean,
    onPrevAyah: () -> Unit,
    onNextAyah: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141C13).copy(alpha = 0.85f))
                .border(width = 0.8.dp, color = Color(0xFF222B1E).copy(alpha = 0.60f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Ayah Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onPrevAyah
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (!isArabic) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Previous Ayah",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = if (isArabic) "السابق" else "Previous",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                if (isArabic) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Previous Ayah",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Ayah stepper counter indicator: Arabic-Indic numerals
            Text(
                text = "${formatDigits(currentAyah.toString(), NumberStylePreference.ARABIC_INDIC)} / ${formatDigits(maxAyahs.toString(), NumberStylePreference.ARABIC_INDIC)}",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = AtharPrimaryLight
            )

            Spacer(Modifier.weight(1f))

            // Copy Action
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCopy
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = "Copy Tafsir",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Share Action
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onShare
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "Share Tafsir",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Next Ayah Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AtharPrimary.copy(alpha = 0.22f))
                    .border(1.dp, AtharPrimaryLight.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNextAyah
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (isArabic) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Next Ayah",
                        tint = AtharPrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = if (isArabic) "التالي" else "Next",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AtharPrimaryLight
                )
                if (!isArabic) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Next Ayah",
                        tint = AtharPrimaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * "Al-Dirayah" (الدِّرَايَة) Glow Feature Tab Card.
 * Shown prominently in the Quran screen index with an animated flowing emerald-gold border glow.
 */
@Composable
fun AlDirayahGlowCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val infiniteTransition = rememberInfiniteTransition(label = "marifahGlow")
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPhase"
    )

    val borderAlpha = 0.40f + (glowPhase * 0.45f)
    val glowBrush = Brush.linearGradient(
        colors = listOf(
            AtharPrimaryLight.copy(alpha = borderAlpha),
            Color(0xFFE5C158).copy(alpha = borderAlpha * 0.85f),
            AtharPrimary.copy(alpha = borderAlpha * 0.6f),
            AtharPrimaryLight.copy(alpha = borderAlpha)
        ),
        start = Offset(0f, 0f),
        end = Offset(600f * glowPhase, 300f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141A12))
            .border(
                width = 1.2.dp,
                brush = glowBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glowing Book Icon Pill
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AtharPrimary.copy(alpha = 0.20f))
                    .border(1.dp, AtharPrimaryLight.copy(alpha = 0.50f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Titles
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isArabic) "الدِّرَايَة • Al Dirayah" else "Al Dirayah • الدِّرَايَة",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.5.sp,
                        color = Color.White
                    )
                    // Glow badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AtharPrimary.copy(alpha = 0.25f))
                            .border(0.6.dp, AtharPrimaryLight.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (isArabic) "تفسير" else "Tafsir",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = AtharPrimaryLight
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (isArabic) "تفسير وتدبر آيات الذكر الحكيم بالتفاسير المعتمدة" else "Contemplate verses with authentic commentary editions",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.5.sp,
                    color = AtharTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // Action Chevron Pill
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Open Tafsir",
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Floating circular Tafsir action button inside the Quran Reader.
 * Styled like Apple Music's lyrics translate floating circular button.
 */
@Composable
fun AlDirayahFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = AtharPrimary.copy(alpha = 0.50f),
                ambientColor = Color.Black.copy(alpha = 0.70f)
            )
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xE6141A12))
            .border(
                width = 1.2.dp,
                color = AtharPrimaryLight.copy(alpha = pulseAlpha),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
            contentDescription = if (isArabic) "الدِّرَايَة • تفسير الآية" else "Al Dirayah • Verse Tafsir",
            tint = AtharPrimaryLight,
            modifier = Modifier.size(20.dp)
        )
    }
}

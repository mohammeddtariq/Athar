package com.athar.app.ui.corner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.athar.app.R
import com.athar.app.data.AyahTafsir
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.QuranRepository
import com.athar.app.data.TafsirEdition
import com.athar.app.data.TafsirRepository
import com.athar.app.data.formatDigits
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharSurface
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifText
import kotlinx.coroutines.launch
import java.util.Locale

private sealed interface TafsirUiState {
    data object Loading : TafsirUiState
    data class Success(val tafsir: AyahTafsir) : TafsirUiState
    data class Error(val message: String) : TafsirUiState
}

/**
 * "Al-Ma'rifah" (المعرفة) Floating Tafsir & Contemplation Modal Sheet.
 *
 * Features:
 * - Fluid entrance animation with semi-transparent blurred backdrop.
 * - Bilingual commentary presentation (Arabic and English).
 * - Segmented language switcher to seamlessly toggle commentary and pronunciation.
 * - Modern 4-book Tafsir switcher (Al-Sa'di, Ibn Kathir, Al-Tabari, Al-Qurtubi).

 * - Fullscreen expand/collapse toggle for deep contemplation.
 * - Previous/Next Ayah steppers, copy & share actions.
 */
@Composable
fun AlMarifahSheet(
    initialSurahNumber: Int = 1,
    initialAyahNumber: Int = 1,
    numberStyle: NumberStylePreference = NumberStylePreference.WESTERN,
    onDismiss: () -> Unit,
    onNavigateToAyah: ((surah: Int, ayah: Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val isArabic = remember { Locale.getDefault().language == "ar" }

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
    var isExpanded by remember { mutableStateOf(false) }
    var uiState by remember { mutableStateOf<TafsirUiState>(TafsirUiState.Loading) }

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
            onFailure = { TafsirUiState.Error(it.localizedMessage ?: "Error loading Tafsir") }
        )
    }

    BackHandler {
        onDismiss()
    }

    var currentVerseText by remember { mutableStateOf("") }
    LaunchedEffect(currentSurah, currentAyah) {
        val verses = QuranRepository.getSurahVerses(context, currentSurah)
        currentVerseText = verses?.firstOrNull { it.number == currentAyah }?.text ?: ""
    }

    val sheetHeightFraction by animateFloatAsState(
        targetValue = if (isExpanded) 0.96f else 0.72f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "sheetHeight"
    )

    // Backdrop Scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Sheet Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightFraction)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Prevent taps inside sheet from dismissing
                )
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF111510))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AtharPrimaryLight.copy(alpha = 0.40f),
                            AtharCardBorder.copy(alpha = 0.20f)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // Top Drag Handle & Controls Bar (Sticky)
                AlMarifahTopBar(
                    isExpanded = isExpanded,
                    isEnglishMode = isEnglishMode,
                    isArabic = isArabic,
                    onToggleExpand = { isExpanded = !isExpanded },
                    onSelectLanguageMode = { isEnglishMode = it },
                    onClose = onDismiss
                )

                // Sticky 4-Book Tafsir Switcher
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                ) {
                    TafsirBooksBar(
                        selectedEdition = selectedEdition,
                        isArabic = isArabic,
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
                        isArabic = isArabic
                    )

                    Spacer(Modifier.height(14.dp))

                    // Tafsir Content Section
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                        },
                        label = "tafsirContent"
                    ) { state ->
                        when (state) {
                            is TafsirUiState.Loading -> {
                                TafsirLoadingView(isArabic = isArabic)
                            }
                            is TafsirUiState.Error -> {
                                TafsirErrorView(
                                    isArabic = isArabic,
                                    errorMessage = state.message,
                                    onRetry = {
                                        // Trigger reload
                                        selectedEdition = selectedEdition
                                    }
                                )
                            }
                            is TafsirUiState.Success -> {
                                TafsirContentView(
                                    tafsir = state.tafsir,
                                    isEnglishMode = isEnglishMode,
                                    isArabic = isArabic
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }

                // Bottom Navigation & Actions Bar
                AlMarifahBottomBar(
                    currentSurah = currentSurah,
                    currentAyah = currentAyah,
                    maxAyahs = surahMeta.ayahs,
                    numberStyle = numberStyle,
                    isArabic = isArabic,
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
                                append(state.tafsir.edition.arabicName)
                                append(":\n")
                                append(state.tafsir.arabicTafsir)
                                if (state.tafsir.englishTafsir.isNotBlank()) {
                                    append("\n\nEnglish:\n")
                                    append(state.tafsir.englishTafsir)
                                }
                                append("\n\n— Athar • أثـر")
                            }
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("Athar Tafsir", textToCopy))
                            Toast.makeText(
                                context,
                                if (isArabic) "تم نسخ التفسير إلى الحافظة" else "Tafsir copied to clipboard",
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
                                append(state.tafsir.edition.arabicName)
                                append(":\n")
                                append(state.tafsir.arabicTafsir)
                                if (state.tafsir.englishTafsir.isNotBlank()) {
                                    append("\n\nEnglish:\n")
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

/**
 * Top control bar with drag indicator, title branding, language reverse swap, expand/collapse, and close.
 */
@Composable
private fun AlMarifahTopBar(
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
                    text = if (isArabic) "المعرفة" else "Al-Ma'rifah",
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
 * Sacred Ayah header card displaying the calligraphic verse text.
 */
@Composable
private fun SacredAyahCard(
    surahMeta: SurahMeta,
    ayahNumber: Int,
    verseText: String,
    englishTranslation: String = "",
    englishTransliteration: String = "",
    isEnglishMode: Boolean,
    isArabic: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141912))
            .border(1.dp, Color(0xFF263322), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reference pill: e.g. "سورة الفاتحة • الآية ١" (Ayah number always in Arabic-Indic style)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AtharPrimary.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isArabic) "سورة ${surahMeta.arabicName}" else "Surah ${surahMeta.englishName}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AtharPrimaryLight
                )
            }

            Text(
                text = "•",
                color = AtharTextSecondary.copy(alpha = 0.5f),
                fontSize = 12.sp
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isArabic) "الآية ${formatDigits(ayahNumber.toString(), NumberStylePreference.ARABIC_INDIC)}"
                           else "Ayah ${formatDigits(ayahNumber.toString(), NumberStylePreference.ARABIC_INDIC)}",
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
                text = "﴿ $verseText ﴾",
                fontFamily = QuranUthmanicHafs,
                fontSize = 22.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color(0xFFF2F6F0),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // English Mode additions: Pronunciation (transliteration in English letters) & Translation
        if (isEnglishMode) {
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
                        text = "Translation",
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
                )
            }
        }
    }
}


/**
 * 4-Book Tafsir switcher pills matching Athar's Duas category styling.
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

        // Brief authentic description of the active book (from the uploaded screenshot)
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
 * Formats Tafsir commentary:
 * - Words before ':' are bold and prominent ivory tint
 * - Words after ':' are normal weight reading sage
 * - Ayah/verse tokens '{...}' are highlighted in warm amber gold
 * - Hadith quotes '«...»' are styled in soft green
 */
private fun buildStyledTafsirAnnotatedString(text: String): AnnotatedString {
    if (text.isBlank()) return AnnotatedString("")
    return try {
        buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { index, rawLine ->
                val line = rawLine.trim()
                if (line.isNotEmpty()) {
                    val colonIdx = line.indexOf(':')
                    if (colonIdx != -1 && colonIdx < line.length - 1) {
                        val prefix = line.substring(0, colonIdx + 1)
                        val suffix = line.substring(colonIdx + 1)

                        appendStyledTafsirChunk(
                            text = prefix,
                            baseColor = Color(0xFFFFFFFF)
                        )
                        appendStyledTafsirChunk(
                            text = suffix,
                            baseColor = Color(0xFFBACABA)
                        )
                    } else if (colonIdx != -1 && colonIdx == line.length - 1) {
                        appendStyledTafsirChunk(
                            text = line,
                            baseColor = Color(0xFFFFFFFF)
                        )
                    } else {
                        val isHeading = line.startsWith("وهي مكية") || line.startsWith("وهي مدنية") ||
                                        line.startsWith("سورة ") || line.startsWith("تفسير سورة")
                        appendStyledTafsirChunk(
                            text = line,
                            baseColor = if (isHeading) Color(0xFFE2EEE0) else Color(0xFFBACABA)
                        )
                    }
                }
                if (index < lines.size - 1) {
                    append("\n")
                }
            }
        }
    } catch (_: Exception) {
        AnnotatedString(text)
    }
}

private fun AnnotatedString.Builder.appendStyledTafsirChunk(
    text: String,
    baseColor: Color
) {
    if (text.isEmpty()) return
    try {
        val tokenRegex = Regex("(\\{[^}]+}|«[^»]+»)")
        var lastIdx = 0
        val matches = tokenRegex.findAll(text)

        for (match in matches) {
            val start = match.range.first.coerceIn(0, text.length)
            val end = (match.range.last + 1).coerceIn(0, text.length)

            if (start > lastIdx) {
                withStyle(SpanStyle(color = baseColor)) {
                    append(text.substring(lastIdx, start))
                }
            }

            val token = match.value
            if (token.startsWith("{") && token.endsWith("}")) {
                withStyle(SpanStyle(color = Color(0xFFE5C158))) {
                    append(token)
                }
            } else if (token.startsWith("«") && token.endsWith("»")) {
                withStyle(SpanStyle(color = AtharPrimaryLight)) {
                    append(token)
                }
            } else {
                withStyle(SpanStyle(color = baseColor)) {
                    append(token)
                }
            }
            lastIdx = end
        }

        if (lastIdx < text.length) {
            withStyle(SpanStyle(color = baseColor)) {
                append(text.substring(lastIdx))
            }
        }
    } catch (_: Exception) {
        append(text)
    }
}

/**
 * Main Tafsir text view displaying commentary with dimmed card styling and clean headers.
 */
@Composable
private fun TafsirContentView(
    tafsir: AyahTafsir,
    isEnglishMode: Boolean,
    isArabic: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!isEnglishMode) {
            // ══════════════════════════════════════════════════════════
            // ─── CARD 1: ARABIC COMMENTARY ───
            // ══════════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141912))
                    .border(1.dp, Color(0xFF263322), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                // Header: Edition & Author (Full width, zero overlap)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AtharPrimary.copy(alpha = 0.20f))
                            .border(0.8.dp, AtharPrimaryLight.copy(alpha = 0.50f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                            contentDescription = null,
                            tint = AtharPrimaryLight,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tafsir.edition.bookTitleArabic,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFFF4F7F2)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = tafsir.edition.authorArabic,
                            fontFamily = ThmanyahSans,
                            fontSize = 12.sp,
                            color = AtharPrimaryLight.copy(alpha = 0.90f)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(thickness = 0.8.dp, color = Color(0xFF243020))
                Spacer(Modifier.height(14.dp))

                // Arabic Commentary Text (Strictly RTL) with styled text before/after ':'
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val styledText = remember(tafsir.arabicTafsir) {
                        buildStyledTafsirAnnotatedString(tafsir.arabicTafsir)
                    }
                    Text(
                        text = styledText,
                        fontFamily = ThmanyahSerifText,
                        fontSize = 18.5.sp,
                        lineHeight = 35.sp,
                        style = TextStyle(
                            textAlign = TextAlign.Start,
                            textDirection = TextDirection.Rtl
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ══════════════════════════════════════════════════════════
            // ─── CARD 2: ENGLISH COMMENTARY (Secondary) ───
            // ══════════════════════════════════════════════════════════
            if (tafsir.englishTafsir.isNotBlank() && tafsir.englishTafsir != tafsir.englishTranslation) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF10150E))
                        .border(1.dp, Color(0xFF222B1E), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AtharTextSecondary.copy(alpha = 0.70f))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "English Commentary",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AtharTextSecondary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        val styledEnText = remember(tafsir.englishTafsir) {
                            buildStyledTafsirAnnotatedString(tafsir.englishTafsir)
                        }
                        Text(
                            text = styledEnText,
                            fontFamily = ThmanyahSans,
                            fontSize = 14.5.sp,
                            lineHeight = 23.sp,
                            style = TextStyle(
                                textAlign = TextAlign.Start,
                                textDirection = TextDirection.Ltr
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // ══════════════════════════════════════════════════════════
            // ─── CARD 1: ENGLISH COMMENTARY ───
            // ══════════════════════════════════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141912))
                    .border(1.dp, Color(0xFF263322), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                // Header: Edition & Author (Full width, zero overlap)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AtharPrimary.copy(alpha = 0.20f))
                            .border(0.8.dp, AtharPrimaryLight.copy(alpha = 0.50f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                            contentDescription = null,
                            tint = AtharPrimaryLight,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tafsir.edition.englishName,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFFF4F7F2)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = tafsir.edition.authorEnglish,
                            fontFamily = ThmanyahSans,
                            fontSize = 12.sp,
                            color = AtharPrimaryLight.copy(alpha = 0.90f)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(thickness = 0.8.dp, color = Color(0xFF243020))
                Spacer(Modifier.height(14.dp))

                // English Commentary Text (Strictly LTR)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    val styledEnText = remember(tafsir.englishTafsir) {
                        buildStyledTafsirAnnotatedString(tafsir.englishTafsir)
                    }
                    Text(
                        text = styledEnText,
                        fontFamily = ThmanyahSans,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        style = TextStyle(
                            textAlign = TextAlign.Start,
                            textDirection = TextDirection.Ltr
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ══════════════════════════════════════════════════════════
            // ─── CARD 2: ARABIC COMMENTARY (Secondary) ───
            // ══════════════════════════════════════════════════════════
            if (tafsir.arabicTafsir.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF10150E))
                        .border(1.dp, Color(0xFF222B1E), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AtharTextSecondary.copy(alpha = 0.70f))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "التفسير بالعربية • ${tafsir.edition.arabicName}",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AtharTextSecondary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Arabic Commentary Text (Strictly RTL)
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        val styledArText = remember(tafsir.arabicTafsir) {
                            buildStyledTafsirAnnotatedString(tafsir.arabicTafsir)
                        }
                        Text(
                            text = styledArText,
                            fontFamily = ThmanyahSerifText,
                            fontSize = 16.5.sp,
                            lineHeight = 31.sp,
                            style = TextStyle(
                                textAlign = TextAlign.Start,
                                textDirection = TextDirection.Rtl
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
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
 * Error state with retry action button.
 */
@Composable
private fun TafsirErrorView(
    isArabic: Boolean,
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF161C14))
            .border(1.dp, Color(0xFF263022), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isArabic) "تعذر استحضار التفسير حالياً" else "Unable to load commentary right now",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = Color.White
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isArabic) "يرجى التحقق من اتصالك بالإنترنت" else "Please check your network connection",
            fontFamily = ThmanyahSans,
            fontSize = 11.5.sp,
            color = AtharTextSecondary
        )
        Spacer(Modifier.height(12.dp))
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
 */
@Composable
private fun AlMarifahBottomBar(
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF131711))
            .border(width = 0.8.dp, color = Color(0xFF222B1E))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev Ayah Button
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
            Icon(
                imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowForward else Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Previous Ayah",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isArabic) "السابق" else "Previous",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }

        Spacer(Modifier.weight(1f))

        // Ayah stepper counter indicator: Always in Arabic-Indic numerals
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
            Text(
                text = if (isArabic) "التالي" else "Next",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = AtharPrimaryLight
            )
            Icon(
                imageVector = if (isArabic) Icons.AutoMirrored.Rounded.ArrowBack else Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Next Ayah",
                tint = AtharPrimaryLight,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * "Al-Ma'rifah" (المعرفة) Glow Feature Tab Card.
 * Shown prominently in the Quran screen index with an animated flowing emerald-gold border glow.
 */
@Composable
fun AlMarifahGlowCard(
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
                        text = if (isArabic) "المعرفة • Al-Ma'rifah" else "Al-Ma'rifah • المعرفة",
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
                    text = if (isArabic) "تفسير وتدبر آيات الذكر الحكيم بأربعة تفاسير معتمدة" else "Contemplate verses with 4 classical commentary editions",
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
fun AlMarifahFloatingButton(
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
            contentDescription = if (isArabic) "المعرفة • تفسير الآية" else "Al-Ma'rifah • Verse Tafsir",
            tint = AtharPrimaryLight,
            modifier = Modifier.size(20.dp)
        )
    }
}


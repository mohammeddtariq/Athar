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
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.R
import com.athar.app.data.AyahTafsir
import com.athar.app.data.NumberStylePreference
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
 * - Bilingual commentary presentation with Arabic as primary hero language.
 * - "Google Translate" style Hero Swap button to seamlessly reverse primary language.
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
    var isEnglishHero by remember { mutableStateOf(false) }
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
                // Top Drag Handle & Controls Bar
                AlMarifahTopBar(
                    isExpanded = isExpanded,
                    isEnglishHero = isEnglishHero,
                    isArabic = isArabic,
                    onToggleExpand = { isExpanded = !isExpanded },
                    onSwapHero = { isEnglishHero = !isEnglishHero },
                    onClose = onDismiss
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp)
                ) {
                    // Sacred Ayah Card
                    SacredAyahCard(
                        surahMeta = surahMeta,
                        ayahNumber = currentAyah,
                        verseText = (uiState as? TafsirUiState.Success)?.tafsir?.verseTextArabic ?: "",
                        isArabic = isArabic,
                        numberStyle = numberStyle
                    )

                    Spacer(Modifier.height(14.dp))

                    // 4-Book Tafsir Switcher
                    TafsirBooksBar(
                        selectedEdition = selectedEdition,
                        isArabic = isArabic,
                        onSelectEdition = { selectedEdition = it }
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
                                    isEnglishHero = isEnglishHero,
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
                            val prevMeta = allSurahs.first { it.number == currentSurah }
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
    isEnglishHero: Boolean,
    isArabic: Boolean,
    onToggleExpand: () -> Unit,
    onSwapHero: () -> Unit,
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AtharPrimary.copy(alpha = 0.16f))
                    .border(1.dp, AtharPrimaryLight.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = AtharPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isArabic) "المعرفة • تَفْسِير" else "Al-Ma'rifah • Tafsir",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = AtharPrimaryLight
                )
            }

            Spacer(Modifier.weight(1f))

            // Google Translate Style Hero Swap Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSwapHero
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SwapHoriz,
                    contentDescription = "Swap Hero Language",
                    tint = if (isEnglishHero) AtharPrimaryLight else Color(0xFFE2E8DF),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (!isEnglishHero) "عربي ⇄ En" else "En ⇄ عربي",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = if (isEnglishHero) AtharPrimaryLight else Color(0xFFE2E8DF)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Expand / Collapse Fullscreen Button
            Box(
                modifier = Modifier
                    .size(36.dp)
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
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            // Close Button
            Box(
                modifier = Modifier
                    .size(36.dp)
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
                    modifier = Modifier.size(18.dp)
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
    isArabic: Boolean,
    numberStyle: NumberStylePreference
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF171D15))
            .border(1.dp, Color(0xFF283424), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reference pill: e.g. "سورة الفاتحة • الآية ١"
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
                    text = if (isArabic) "الآية ${formatDigits(ayahNumber.toString(), numberStyle)}"
                           else "Ayah ${formatDigits(ayahNumber.toString(), numberStyle)}",
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
 * Main Tafsir text view displaying Arabic and English with Hero Language styling.
 */
@Composable
private fun TafsirContentView(
    tafsir: AyahTafsir,
    isEnglishHero: Boolean,
    isArabic: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF161C14))
            .border(1.dp, Color(0xFF263022), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        if (!isEnglishHero) {
            // ─── ARABIC IS HERO (Default) ───
            // Arabic Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AtharPrimaryLight)
                )
                Text(
                    text = if (isArabic) "التفسير المعتمد • ${tafsir.edition.arabicName}"
                           else "Authentic Commentary • ${tafsir.edition.englishName}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AtharPrimaryLight
                )
            }

            Spacer(Modifier.height(10.dp))

            // Arabic Tafsir (Hero - Larger font, rich leading)
            SelectionContainer {
                Text(
                    text = tafsir.arabicTafsir,
                    fontFamily = ThmanyahSerifText,
                    fontWeight = FontWeight.Normal,
                    fontSize = 19.5.sp,
                    lineHeight = 36.sp,
                    color = Color(0xFFF0F5EE),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 0.8.dp,
                color = Color(0xFF283424)
            )
            Spacer(Modifier.height(14.dp))

            // English Translation / Tafsir (Secondary - smaller font)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(AtharTextSecondary.copy(alpha = 0.7f))
                )
                Text(
                    text = "English Commentary & Meaning",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = AtharTextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            SelectionContainer {
                Text(
                    text = tafsir.englishTafsir,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFFB4C4B0),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // ─── ENGLISH IS HERO (Reversed) ───
            // English Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AtharPrimaryLight)
                )
                Text(
                    text = "English Commentary • ${tafsir.edition.englishName}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AtharPrimaryLight
                )
            }

            Spacer(Modifier.height(10.dp))

            // English Tafsir (Hero - Larger font)
            SelectionContainer {
                Text(
                    text = tafsir.englishTafsir,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.5.sp,
                    lineHeight = 28.sp,
                    color = Color(0xFFF0F5EE),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 0.8.dp,
                color = Color(0xFF283424)
            )
            Spacer(Modifier.height(14.dp))

            // Arabic Tafsir (Secondary)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(AtharTextSecondary.copy(alpha = 0.7f))
                )
                Text(
                    text = "التفسير بالعربية • ${tafsir.edition.arabicName}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = AtharTextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            SelectionContainer {
                Text(
                    text = tafsir.arabicTafsir,
                    fontFamily = ThmanyahSerifText,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 30.sp,
                    color = Color(0xFFB4C4B0),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
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

        // Ayah stepper counter indicator
        Text(
            text = "${formatDigits(currentAyah.toString(), numberStyle)} / ${formatDigits(maxAyahs.toString(), numberStyle)}",
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


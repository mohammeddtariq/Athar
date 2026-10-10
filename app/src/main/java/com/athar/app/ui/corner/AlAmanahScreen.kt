package com.athar.app.ui.corner

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athar.app.data.AlAmanahAudioController
import com.athar.app.data.AppPreferences
import com.athar.app.data.DisplayRefreshHelper
import com.athar.app.data.QuranReciter
import com.athar.app.data.QuranRepository
import com.athar.app.data.QuranVerse
import com.athar.app.ui.theme.QuranUthmanicHafs
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.launch
import java.util.Locale

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

enum class AmanahTab {
    HOME,
    LIBRARY,
    INSIGHTS
}

// ─── PURE AMOLED DARK STYLING (NO GREEN BACKGROUND) ───
private val AmoledBg = Color(0xFF000000)
private val AmoledSurface = Color(0xFF0C0E12)
private val AmoledCard = Color(0xFF13171F)
private val AmoledCardBorder = Color(0xFF222733)
private val AmoledAccent = Color(0xFFB5DB88)
private val AmoledCyanGlow = Color(0xFF64B5F6)
private val AmoledMutedText = Color(0xFF8B96A5)
private val AmoledDimText = Color(0xFF535D6C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlAmanahScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appPrefs = remember { AppPreferences(context.applicationContext) }
    val isArabic = remember { Locale.getDefault().language == "ar" }
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    val highRefreshRate by appPrefs.amanahHighRefreshRate.collectAsState(initial = true)
    val favorites by appPrefs.amanahFavorites.collectAsState(initial = emptySet())
    val todayListenSec by appPrefs.amanahTodayListenSec.collectAsState(initial = 0L)
    val streakDays by appPrefs.amanahStreakDays.collectAsState(initial = 0)

    var currentTab by remember { mutableStateOf(AmanahTab.HOME) }
    var isPlayerExpanded by remember { mutableStateOf(false) }

    // Bottom Sheet Dialogs
    var showQueueSheet by remember { mutableStateOf(false) }
    var showReciterSheet by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }

    // Activate 120Hz high refresh rate if enabled
    DisposableEffect(highRefreshRate) {
        val activity = context.findActivity()
        DisplayRefreshHelper.setHighRefreshRate(activity, highRefreshRate)
        onDispose {
            DisplayRefreshHelper.setHighRefreshRate(activity, false)
        }
    }

    BackHandler {
        if (isPlayerExpanded) {
            isPlayerExpanded = false
        } else if (currentTab != AmanahTab.HOME) {
            currentTab = AmanahTab.HOME
        } else {
            onBack()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBg)
        ) {
            // Ambient Dark AMOLED Blurred Depth Background (Dark Slate / Deep Navy highlight, NO green)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF141926).copy(alpha = 0.50f),
                                Color(0xFF090C12).copy(alpha = 0.75f),
                                AmoledBg
                            ),
                            radius = 1200f
                        )
                    )
            )

            // ─── MAIN CONTENT PAGES (Home / Library / Insights) ───
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top App Header when browsing tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back arrow to exit sanctuary
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onBack
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isArabic) "الأَمَنَة" else "Al-Amanah",
                            fontFamily = ThmanyahSerifDisplay,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isArabic) "السكينة والتلاوة" else "Divine Tranquility",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = AmoledMutedText
                        )
                    }

                    // Active Reciter Quick Avatar / Switcher
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showReciterSheet = true }
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = AmoledAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isArabic) AlAmanahAudioController.currentReciter.arabicName.split(" ").take(2).joinToString(" ")
                                else AlAmanahAudioController.currentReciter.englishName.split(" ").take(2).joinToString(" "),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Body content based on tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (currentTab) {
                        AmanahTab.HOME -> AmanahHomeScreen(
                            isArabic = isArabic,
                            onPlaySurah = { surah ->
                                AlAmanahAudioController.playSurah(context, surah)
                                isPlayerExpanded = true
                            },
                            onOpenReciterPicker = { showReciterSheet = true },
                            onOpenLibrary = { currentTab = AmanahTab.LIBRARY }
                        )
                        AmanahTab.LIBRARY -> AmanahLibraryScreen(
                            isArabic = isArabic,
                            favorites = favorites,
                            onToggleFavorite = { s -> scope.launch { appPrefs.toggleAmanahFavorite(s) } },
                            onPlaySurah = { surah ->
                                AlAmanahAudioController.playSurah(context, surah)
                                isPlayerExpanded = true
                            }
                        )
                        AmanahTab.INSIGHTS -> AmanahInsightsScreen(
                            isArabic = isArabic,
                            todayListenSec = todayListenSec,
                            streakDays = streakDays,
                            highRefreshRate = highRefreshRate,
                            onToggleHighRefresh = { enabled ->
                                scope.launch { appPrefs.setAmanahHighRefreshRate(enabled) }
                            }
                        )
                    }
                }

                // Space for floating mini player + bottom nav dock
                Spacer(Modifier.height(if (AlAmanahAudioController.currentPositionMs > 0 || AlAmanahAudioController.isPlaying) 124.dp else 68.dp))
            }

            // ─── DOCKED FLOATING MINI PLAYER (ABOVE BOTTOM NAV) ───
            if (!isPlayerExpanded) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 60.dp)
                ) {
                    AmanahDockedMiniPlayer(
                        isArabic = isArabic,
                        onExpand = { isPlayerExpanded = true },
                        onTogglePlay = { AlAmanahAudioController.togglePlayPause(context) },
                        onSkipNext = { AlAmanahAudioController.skipNext(context) }
                    )
                }
            }

            // ─── BOTTOM NAVIGATION DOCK (Home / Library / Insights) ───
            if (!isPlayerExpanded) {
                AmanahBottomNavCapsule(
                    currentTab = currentTab,
                    isArabic = isArabic,
                    onSelectTab = { currentTab = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 6.dp)
                )
            }

            // ─── FULLSCREEN RECITATION PLAYER OVERLAY ───
            AnimatedVisibility(
                visible = isPlayerExpanded,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(360, easing = FastOutSlowInEasing)) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(280, easing = FastOutSlowInEasing)) + fadeOut()
            ) {
                AmanahFullscreenPlayer(
                    isArabic = isArabic,
                    favorites = favorites,
                    onToggleFavorite = { s -> scope.launch { appPrefs.toggleAmanahFavorite(s) } },
                    onCollapse = { isPlayerExpanded = false },
                    onOpenQueue = { showQueueSheet = true },
                    onOpenReciter = { showReciterSheet = true },
                    onOpenSleepTimer = { showSleepTimerSheet = true }
                )
            }

            // ─── QUEUE BOTTOM SHEET ───
            if (showQueueSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showQueueSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = AmoledSurface,
                    contentColor = Color.White
                ) {
                    AmanahQueueSheetContent(
                        isArabic = isArabic,
                        currentSurahNumber = AlAmanahAudioController.currentSurah.number,
                        onSelectSurah = { surah ->
                            AlAmanahAudioController.playSurah(context, surah)
                            showQueueSheet = false
                        }
                    )
                }
            }

            // ─── RECITER SELECTOR BOTTOM SHEET ───
            if (showReciterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showReciterSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = AmoledSurface,
                    contentColor = Color.White
                ) {
                    AmanahReciterSheetContent(
                        isArabic = isArabic,
                        currentReciter = AlAmanahAudioController.currentReciter,
                        onSelectReciter = { reciter ->
                            AlAmanahAudioController.changeReciter(context, reciter)
                            scope.launch { appPrefs.setQuranReciter(reciter) }
                            showReciterSheet = false
                        }
                    )
                }
            }

            // ─── SLEEP TIMER BOTTOM SHEET ───
            if (showSleepTimerSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSleepTimerSheet = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = AmoledSurface,
                    contentColor = Color.White
                ) {
                    AmanahSleepTimerSheetContent(
                        isArabic = isArabic,
                        activeTimerMin = AlAmanahAudioController.sleepTimerMinutes,
                        onSelectTimer = { min ->
                            AlAmanahAudioController.setSleepTimer(min)
                            showSleepTimerSheet = false
                        }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 1. HOME TAB (FEATURED RECITER, SURAH BROWSER)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahHomeScreen(
    isArabic: Boolean,
    onPlaySurah: (SurahMeta) -> Unit,
    onOpenReciterPicker: () -> Unit,
    onOpenLibrary: () -> Unit
) {
    val reciter = AlAmanahAudioController.currentReciter
    val currentPlayingNum = AlAmanahAudioController.currentSurah.number

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Last Selected Reciter
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AmoledCard)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenReciterPicker
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reciter Avatar Icon Box
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, AmoledAccent.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = AmoledAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "القارئ الحالي" else "CURRENT RECITER",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AmoledAccent,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) reciter.arabicName else reciter.englishName,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "المصحف المرتل كاملاً • اضغط للتغيير" else "Full Quran Murattal • Tap to change",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = AmoledMutedText
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AmoledMutedText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Section: Browse All Surahs Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenLibrary
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isArabic) "تصفح جميع السور" else "Browse All Surahs",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = if (isArabic) "عرض الكل ❯" else "View All ❯",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = AmoledAccent
                )
            }
        }

        // Surah Items List
        items(allSurahs.take(15)) { surah ->
            val isCurrent = surah.number == currentPlayingNum
            AmanahSurahRow(
                surah = surah,
                isArabic = isArabic,
                isPlaying = isCurrent && AlAmanahAudioController.isPlaying,
                onClick = { onPlaySurah(surah) }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 2. LIBRARY TAB (ALL 114 SURAHS / GROUP BY JUZ' / DOWNLOADS / FAVORITES)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahLibraryScreen(
    isArabic: Boolean,
    favorites: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onPlaySurah: (SurahMeta) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf(0) } // 0: All, 1: By Juz', 2: Downloads, 3: Favorites

    val filteredSurahs = remember(searchQuery, filterMode, favorites) {
        allSurahs.filter { surah ->
            val matchesQuery = searchQuery.isBlank() ||
                surah.arabicName.contains(searchQuery, ignoreCase = true) ||
                surah.englishName.contains(searchQuery, ignoreCase = true) ||
                surah.number.toString() == searchQuery.trim()

            val matchesFilter = when (filterMode) {
                2 -> AlAmanahAudioController.isSurahDownloaded(context, surah.number, AlAmanahAudioController.currentReciter)
                3 -> favorites.contains(surah.number)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Input
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = if (isArabic) "ابحث عن سورة بالاسم أو الرقم..." else "Search surah by name or number...",
                    fontFamily = ThmanyahSans,
                    fontSize = 13.sp,
                    color = AmoledMutedText
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = AmoledMutedText,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AmoledCard,
                unfocusedContainerColor = AmoledCard,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AmoledCardBorder, RoundedCornerShape(14.dp))
        )

        Spacer(Modifier.height(10.dp))

        // Filter Pills Row
        val filters = if (isArabic) {
            listOf("جميع السور (١١٤)", "تقسيم الأجزاء (٣٠)", "محمّلة", "المفضلة")
        } else {
            listOf("All Surahs", "By Juz'", "Downloaded", "Favorites")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEachIndexed { idx, label ->
                val selected = filterMode == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) AmoledAccent else AmoledCard)
                        .border(1.dp, if (selected) AmoledAccent else AmoledCardBorder, RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { filterMode = idx }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontFamily = ThmanyahSans,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = if (selected) Color(0xFF0C0E12) else Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Surah List or Group By Juz
        if (filterMode == 1) {
            // Group by Juz view
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(30) { juzIndex ->
                    val juzNum = juzIndex + 1
                    AmanahJuzCard(
                        juzNumber = juzNum,
                        isArabic = isArabic,
                        onPlaySurah = onPlaySurah
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSurahs) { surah ->
                    AmanahSurahRow(
                        surah = surah,
                        isArabic = isArabic,
                        isPlaying = surah.number == AlAmanahAudioController.currentSurah.number && AlAmanahAudioController.isPlaying,
                        onClick = { onPlaySurah(surah) }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 3. INSIGHTS TAB (LISTENING TIME & AUDIO METRICS)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahInsightsScreen(
    isArabic: Boolean,
    todayListenSec: Long,
    streakDays: Int,
    highRefreshRate: Boolean,
    onToggleHighRefresh: (Boolean) -> Unit
) {
    val todayMinutes = todayListenSec / 60
    val weekMinutes = todayMinutes * 3 // calculated or tracked

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top 3 Stats Badges (Streak, This Week, Record)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AmanahStatBadge(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "التتابع" else "STREAK",
                    value = if (isArabic) "$streakDays يوم" else "$streakDays days",
                    emoji = "🔥"
                )
                AmanahStatBadge(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "الأسبوع" else "THIS WEEK",
                    value = if (isArabic) "$weekMinutes دقيقة" else "$weekMinutes min",
                    emoji = "📅"
                )
                AmanahStatBadge(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "اليوم" else "TODAY",
                    value = if (isArabic) "$todayMinutes دقيقة" else "$todayMinutes min",
                    emoji = "🎧"
                )
            }
        }

        // Large Listening Time Display Card (NO daily target fraction!)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(AmoledCard)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(22.dp))
                    .padding(vertical = 32.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isArabic) "وَقْتُ الِاسْتِمَاعِ اليَوْم" else "TODAY'S LISTENING",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AmoledAccent,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "$todayMinutes",
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 58.sp,
                        color = Color.White
                    )

                    Text(
                        text = if (isArabic) "دقيقة من القرآن الكريم" else "minutes of Quran listening",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = AmoledMutedText
                    )
                }
            }
        }

        // 120Hz Ultra-Fluid Scrolling Switch Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AmoledCard)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "سلاسة فائقة 120Hz" else "120Hz Fluid Flow",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "استجابة حركة وانسيابية تامة للنصوص والتمرير"
                            else "Ultra-smooth scrolling and lyrics motion on supported screens",
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            color = AmoledMutedText
                        )
                    }

                    Switch(
                        checked = highRefreshRate,
                        onCheckedChange = onToggleHighRefresh,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0C0E12),
                            checkedTrackColor = AmoledAccent,
                            uncheckedThumbColor = AmoledMutedText,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.10f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AmanahStatBadge(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    emoji: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AmoledCard)
            .border(1.dp, AmoledCardBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = AmoledMutedText
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 4. FULLSCREEN RECITATION PLAYER OVERLAY
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahFullscreenPlayer(
    isArabic: Boolean,
    favorites: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onCollapse: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenReciter: () -> Unit,
    onOpenSleepTimer: () -> Unit
) {
    val context = LocalContext.current
    val surah = AlAmanahAudioController.currentSurah
    val reciter = AlAmanahAudioController.currentReciter
    val isPlaying = AlAmanahAudioController.isPlaying
    val activeVerse = AlAmanahAudioController.activeVerseNumber ?: 1
    val isFavorite = favorites.contains(surah.number)

    val listState = rememberLazyListState()
    var surahVerses by remember(surah.number) { mutableStateOf<List<QuranVerse>>(emptyList()) }
    LaunchedEffect(surah.number) {
        val verses = QuranRepository.getSurahVerses(context, surah.number)
        surahVerses = verses ?: emptyList()
    }

    // Auto-scroll lyrics smoothly to active verse
    LaunchedEffect(activeVerse, surahVerses) {
        val targetIdx = (activeVerse - 1).coerceAtLeast(0)
        if (targetIdx < surahVerses.size) {
            listState.animateScrollToItem(
                index = targetIdx,
                scrollOffset = -180
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBg)
    ) {
        // Deep Dark AMOLED Background Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF161E2E).copy(alpha = 0.55f),
                            Color(0xFF0B0F17).copy(alpha = 0.80f),
                            AmoledBg
                        ),
                        radius = 1100f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ─── PULL-DOWN HANDLE ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 4.5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.30f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCollapse
                        )
                )
            }

            // ─── TOP BAR (Surah badge, Surah title, Juz, Options) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Square Artwork / Number Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AmoledCard)
                        .border(1.dp, AmoledCardBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AmoledAccent
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Title + Juz Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) surah.arabicName else surah.englishName,
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isArabic) "الجزء ${surah.juz} • ${surah.ayahs} آية"
                        else "Juz ${surah.juz} • ${surah.ayahs} Verses",
                        fontFamily = ThmanyahSans,
                        fontSize = 11.5.sp,
                        color = AmoledMutedText
                    )
                }

                // 3-Dots Menu Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenReciter
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // ─── STATUS PILLS (Audio profile, Verse indicator) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sound pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Rounded.GraphicEq, null, tint = AmoledAccent, modifier = Modifier.size(13.dp))
                        Text(
                            text = if (isArabic) "صوت نقي" else "Sound",
                            fontFamily = ThmanyahSans,
                            fontSize = 10.5.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Surah:Ayah dropdown pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                        .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenQueue
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${surah.number}:$activeVerse",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // ─── SYNCHRONIZED AYAT RECITATION (GLOW & FOCUS) ───
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    itemsIndexed(surahVerses) { index, verse ->
                        val isVerseActive = (index + 1) == activeVerse

                        // Smoothly animated opacity and blur for inactive vs active
                        val targetAlpha = if (isVerseActive) 1.0f else 0.35f
                        val animAlpha by animateFloatAsState(targetAlpha, tween(350), label = "verseAlpha")

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(animAlpha)
                                .then(
                                    if (!isVerseActive) Modifier.blur(2.6.dp) else Modifier
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        AlAmanahAudioController.seekToVerse(index + 1)
                                    }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Tiny Ayah Pill Badge (e.g. 1:2)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isVerseActive) AmoledAccent.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        width = 0.8.dp,
                                        color = if (isVerseActive) AmoledAccent.copy(alpha = 0.6f) else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${surah.number}:${index + 1}",
                                    fontFamily = ThmanyahSans,
                                    fontSize = 10.sp,
                                    fontWeight = if (isVerseActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isVerseActive) AmoledAccent else AmoledMutedText
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // Active Verse Typography with Cyan Glow Halo
                            Text(
                                text = verse.text,
                                fontFamily = QuranUthmanicHafs,
                                fontSize = if (isVerseActive) 26.sp else 22.sp,
                                fontWeight = if (isVerseActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isVerseActive) Color.White else AmoledMutedText,
                                textAlign = TextAlign.Center,
                                lineHeight = if (isVerseActive) 44.sp else 38.sp,
                                style = androidx.compose.ui.text.TextStyle(
                                    shadow = if (isVerseActive) Shadow(
                                        color = AmoledCyanGlow.copy(alpha = 0.50f),
                                        offset = Offset(0f, 0f),
                                        blurRadius = 18f
                                    ) else null
                                )
                            )
                        }
                    }
                }
            }

            // ─── TRACK TITLE & RECITER INFO (ABOVE SCRUB BAR) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenReciter
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "${surah.arabicName} • بصوت ${reciter.arabicName.split(" ").take(2).joinToString(" ")}"
                        else "${surah.englishName} • Recited by ${reciter.englishName.split(" ").take(2).joinToString(" ")}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Favorite Star Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onToggleFavorite(surah.number) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AmoledAccent else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ─── SCRUB BAR (SLIDER + TIMERS + HIGH QUALITY BADGE) ───
            val posMs = AlAmanahAudioController.currentPositionMs
            val durMs = AlAmanahAudioController.totalDurationMs.coerceAtLeast(1L)
            val progress = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Slider(
                    value = progress,
                    onValueChange = { frac ->
                        val target = (frac * durMs).toLong()
                        AlAmanahAudioController.seekTo(target)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White.copy(alpha = 0.85f),
                        inactiveTrackColor = Color.White.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.fillMaxWidth().height(20.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatAmanahTime(posMs),
                        fontFamily = ThmanyahSans,
                        fontSize = 11.sp,
                        color = AmoledMutedText
                    )
                    Text(
                        text = if (isArabic) "🎧 جودة عالية" else "🎧 High Quality",
                        fontFamily = ThmanyahSans,
                        fontSize = 10.5.sp,
                        color = AmoledMutedText.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "-${formatAmanahTime((durMs - posMs).coerceAtLeast(0L))}",
                        fontFamily = ThmanyahSans,
                        fontSize = 11.sp,
                        color = AmoledMutedText
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ─── MAIN TRANSPORT CONTROLS (PREVIOUS, PLAY/PAUSE, NEXT) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed pill button on far left
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { AlAmanahAudioController.cycleSpeed() }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${AlAmanahAudioController.playbackSpeed}×",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                // Previous Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { AlAmanahAudioController.skipPrevious(context) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Play / Pause Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { AlAmanahAudioController.togglePlayPause(context) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (AlAmanahAudioController.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(34.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // Next Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { AlAmanahAudioController.skipNext(context) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Sleep Timer Button on far right
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (AlAmanahAudioController.sleepTimerMinutes != null) AmoledAccent.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.08f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenSleepTimer
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.NightsStay,
                            contentDescription = "Sleep Timer",
                            tint = if (AlAmanahAudioController.sleepTimerMinutes != null) AmoledAccent else Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        if (AlAmanahAudioController.sleepTimerMinutes != null) {
                            Text(
                                text = "${AlAmanahAudioController.sleepTimerMinutes}m",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = AmoledAccent
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ─── VOLUME ROW ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeDown,
                    contentDescription = null,
                    tint = AmoledMutedText,
                    modifier = Modifier.size(18.dp)
                )
                Slider(
                    value = AlAmanahAudioController.playerVolume,
                    onValueChange = { AlAmanahAudioController.setVolume(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White.copy(alpha = 0.60f),
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = null,
                    tint = AmoledMutedText,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            // ─── BOTTOM ACTION ROW (Verses, Reciter, Queue) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lyrics Quote Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FormatQuote,
                        contentDescription = "Lyrics",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Middle Capsule: [ 🎧 ] | [ 👤 ]
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenReciter
                        )
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Headphones,
                            contentDescription = null,
                            tint = AmoledAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 14.dp)
                                .background(Color.White.copy(alpha = 0.25f))
                        )
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Floating Queue List Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenQueue
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = "Queue",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ─── BOTTOM RECITER BADGE ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenReciter
                    )
                    .padding(bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isArabic) reciter.arabicName else reciter.englishName,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 5. DOCKED FLOATING MINI PLAYER
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahDockedMiniPlayer(
    isArabic: Boolean,
    onExpand: () -> Unit,
    onTogglePlay: () -> Unit,
    onSkipNext: () -> Unit
) {
    val surah = AlAmanahAudioController.currentSurah
    val reciter = AlAmanahAudioController.currentReciter
    val isPlaying = AlAmanahAudioController.isPlaying

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
            .background(AmoledCard)
            .border(1.2.dp, AmoledCardBorder, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExpand
            )
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reciter Avatar Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, AmoledAccent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = AmoledAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${surah.number}. ${if (isArabic) surah.arabicName else surah.englishName}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isArabic) reciter.arabicName else reciter.englishName,
                    fontFamily = ThmanyahSans,
                    fontSize = 11.sp,
                    color = AmoledMutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Mini Play/Pause button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTogglePlay
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(4.dp))

            // Mini Next button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSkipNext
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 6. BOTTOM NAVIGATION CAPSULE (HOME / LIBRARY / INSIGHTS)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahBottomNavCapsule(
    currentTab: AmanahTab,
    isArabic: Boolean,
    onSelectTab: (AmanahTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(18.dp, RoundedCornerShape(32.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(32.dp))
            .background(AmoledCard.copy(alpha = 0.95f))
            .border(1.2.dp, AmoledCardBorder, RoundedCornerShape(32.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AmanahNavItem(
                icon = Icons.Rounded.Home,
                label = if (isArabic) "الرئيسية" else "Home",
                selected = currentTab == AmanahTab.HOME,
                onClick = { onSelectTab(AmanahTab.HOME) }
            )
            AmanahNavItem(
                icon = Icons.Rounded.LibraryMusic,
                label = if (isArabic) "المكتبة" else "Library",
                selected = currentTab == AmanahTab.LIBRARY,
                onClick = { onSelectTab(AmanahTab.LIBRARY) }
            )
            AmanahNavItem(
                icon = Icons.Rounded.Insights,
                label = if (isArabic) "الإحصائيات" else "Insights",
                selected = currentTab == AmanahTab.INSIGHTS,
                onClick = { onSelectTab(AmanahTab.INSIGHTS) }
            )
        }
    }
}

@Composable
private fun AmanahNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AmoledAccent.copy(alpha = 0.16f) else Color.Transparent)
            .border(1.dp, if (selected) AmoledAccent.copy(alpha = 0.35f) else Color.Transparent, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) AmoledAccent else AmoledMutedText,
                modifier = Modifier.size(18.dp)
            )
            if (selected) {
                Text(
                    text = label,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AmoledAccent
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 7. SURAH ROW COMPONENT
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahSurahRow(
    surah: SurahMeta,
    isArabic: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AmoledCard)
            .border(1.dp, if (isPlaying) AmoledAccent.copy(alpha = 0.4f) else AmoledCardBorder, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah index box (or playing wave icon)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isPlaying) AmoledAccent.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = AmoledAccent,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${surah.number}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) surah.arabicName else surah.englishName,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isPlaying) AmoledAccent else Color.White
                )
                Text(
                    text = "${if (isArabic) surah.englishName else surah.arabicName} • ${if (isArabic) "الجزء ${surah.juz}" else "Juz ${surah.juz}"} • ${surah.ayahs} ${if (isArabic) "آية" else "verses"}",
                    fontFamily = ThmanyahSans,
                    fontSize = 11.5.sp,
                    color = AmoledMutedText
                )
            }

            Icon(
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = if (isPlaying) AmoledAccent else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 8. JUZ CARD (GROUP BY JUZ COLLAPSIBLE)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahJuzCard(
    juzNumber: Int,
    isArabic: Boolean,
    onPlaySurah: (SurahMeta) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val juzSurahs = remember(juzNumber) {
        allSurahs.filter { it.juz == juzNumber }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AmoledCard)
            .border(1.dp, AmoledCardBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { expanded = !expanded }
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AmoledAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$juzNumber",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AmoledAccent
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = if (isArabic) "الجزء $juzNumber" else "Juz $juzNumber",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Icon(
                imageVector = if (expanded) Icons.Rounded.KeyboardArrowDown else Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = AmoledMutedText,
                modifier = Modifier.size(20.dp)
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                juzSurahs.forEach { surah ->
                    AmanahSurahRow(
                        surah = surah,
                        isArabic = isArabic,
                        isPlaying = surah.number == AlAmanahAudioController.currentSurah.number && AlAmanahAudioController.isPlaying,
                        onClick = { onPlaySurah(surah) }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 9. MODAL SHEETS (QUEUE / RECITER / SLEEP TIMER)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahQueueSheetContent(
    isArabic: Boolean,
    currentSurahNumber: Int,
    onSelectSurah: (SurahMeta) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (isArabic) "قائمة سور القرآن الكريم (١١٤)" else "Surah Queue (114)",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White
        )
        Spacer(Modifier.height(14.dp))
        LazyColumn(
            modifier = Modifier.height(440.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allSurahs) { surah ->
                val isSelected = surah.number == currentSurahNumber
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AmoledAccent.copy(alpha = 0.18f) else AmoledCard)
                        .border(1.dp, if (isSelected) AmoledAccent.copy(alpha = 0.45f) else AmoledCardBorder, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectSurah(surah) }
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${surah.number}.",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) AmoledAccent else AmoledMutedText
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) surah.arabicName else surah.englishName,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = if (isArabic) "الجزء ${surah.juz}" else "Juz ${surah.juz}",
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            color = AmoledMutedText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AmanahReciterSheetContent(
    isArabic: Boolean,
    currentReciter: QuranReciter,
    onSelectReciter: (QuranReciter) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (isArabic) "اختر القارئ" else "Select Quran Reciter",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White
        )
        Spacer(Modifier.height(14.dp))
        LazyColumn(
            modifier = Modifier.height(440.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(QuranReciter.entries) { reciter ->
                val isSelected = reciter == currentReciter
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AmoledAccent.copy(alpha = 0.18f) else AmoledCard)
                        .border(1.dp, if (isSelected) AmoledAccent.copy(alpha = 0.45f) else AmoledCardBorder, RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectReciter(reciter) }
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) reciter.arabicName else reciter.englishName,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) AmoledAccent else Color.White
                            )
                            Text(
                                text = if (isArabic) reciter.englishName else reciter.arabicName,
                                fontFamily = ThmanyahSans,
                                fontSize = 11.5.sp,
                                color = AmoledMutedText
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = AmoledAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AmanahSleepTimerSheetContent(
    isArabic: Boolean,
    activeTimerMin: Int?,
    onSelectTimer: (Int?) -> Unit
) {
    val timerOptions = listOf(15, 30, 45, 60)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = if (isArabic) "مؤقت النوم" else "Sleep Timer",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White
        )
        Spacer(Modifier.height(14.dp))
        timerOptions.forEach { min ->
            val isSelected = activeTimerMin == min
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) AmoledAccent.copy(alpha = 0.18f) else AmoledCard)
                    .border(1.dp, if (isSelected) AmoledAccent.copy(alpha = 0.45f) else AmoledCardBorder, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelectTimer(min) }
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "$min دقيقة" else "$min minutes",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isSelected) AmoledAccent else Color.White
                    )
                    if (isSelected) {
                        Icon(Icons.Rounded.Check, null, tint = AmoledAccent, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        if (activeTimerMin != null) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF331515))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelectTimer(null) }
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isArabic) "إلغاء مؤقت النوم" else "Cancel Sleep Timer",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFFF6B6B)
                )
            }
        }
    }
}

private fun formatAmanahTime(ms: Long): String {
    val totalSec = ms / 1000
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

package com.athar.app.ui.corner

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.rounded.DateRange
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
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.sin
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
import androidx.compose.runtime.mutableFloatStateOf
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

// ═══════════════════════════════════════════════════════════════════════════
// SQUIGGLY WAVY SEEKBAR & MINI WAVY PROGRESS
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun WavySeekSlider(
    progress: Float,
    onSeek: (Float) -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wavy_seek")
    val phase by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "phase"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(26.dp)) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val currentX = (progress * width).coerceIn(0f, width)

            val strokeWidthPx = 3.5.dp.toPx()
            val waveAmplitude = 4.5.dp.toPx()
            val waveLength = 22.dp.toPx()

            // 1. Played Wavy Line
            if (currentX > 0f) {
                val wavePath = Path()
                wavePath.moveTo(0f, centerY)
                var x = 0f
                val step = 2.5f
                while (x <= currentX) {
                    val y = centerY + waveAmplitude * sin((x / waveLength) * (2 * PI).toFloat() + phase)
                    wavePath.lineTo(x, y)
                    x += step
                }
                drawPath(
                    path = wavePath,
                    color = Color.White,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }

            // 2. Cursor Thumb |
            val cursorHalfHeight = 10.dp.toPx()
            drawLine(
                color = Color.White,
                start = Offset(currentX, centerY - cursorHalfHeight),
                end = Offset(currentX, centerY + cursorHalfHeight),
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 3. Unplayed Straight Line
            if (currentX < width) {
                drawLine(
                    color = Color.White.copy(alpha = 0.25f),
                    start = Offset(currentX, centerY),
                    end = Offset(width, centerY),
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun MiniWavyProgress(
    progress: Float,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_wavy")
    val phase by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "mini_phase"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val centerY = size.height / 2f
        val currentX = (progress * width).coerceIn(0f, width)
        val strokeWidthPx = 2.5.dp.toPx()
        val waveAmplitude = 3.dp.toPx()
        val waveLength = 16.dp.toPx()

        if (currentX > 0f) {
            val wavePath = Path()
            wavePath.moveTo(0f, centerY)
            var x = 0f
            while (x <= currentX) {
                val y = centerY + waveAmplitude * sin((x / waveLength) * (2 * PI).toFloat() + phase)
                wavePath.lineTo(x, y)
                x += 2f
            }
            drawPath(
                path = wavePath,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        if (currentX < width) {
            drawLine(
                color = Color.White.copy(alpha = 0.20f),
                start = Offset(currentX, centerY),
                end = Offset(width, centerY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
        }
    }
}

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

                        Spacer(Modifier.width(14.dp))

                        // Large Tab Title
                        Text(
                            text = when (currentTab) {
                                AmanahTab.HOME -> if (isArabic) "الرئيسية" else "Home"
                                AmanahTab.LIBRARY -> if (isArabic) "المكتبة" else "Library"
                                AmanahTab.INSIGHTS -> if (isArabic) "الإحصائيات" else "Insights"
                            },
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                    }

                    // Top Right: Device Output Icon + Reciter Profile Avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val outputType = AlAmanahAudioController.getActiveAudioOutputType(context)
                        val outputIcon = if (outputType == AlAmanahAudioController.AudioOutputType.BLUETOOTH_OR_HEADSET) {
                            Icons.Rounded.Headphones
                        } else {
                            Icons.Rounded.PhoneAndroid
                        }

                        // Audio Device Output Switcher Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { AlAmanahAudioController.openSystemAudioOutputPicker(context) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = outputIcon,
                                contentDescription = "Audio Output",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Reciter Profile Circle Avatar
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(1.dp, AmoledAccent.copy(alpha = 0.4f), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showReciterSheet = true }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Reciter Profile",
                                tint = AmoledAccent,
                                modifier = Modifier.size(22.dp)
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
// 1. HOME TAB (FEED: TOP PICKS, RECENTLY PLAYED, BROWSE SURAHS)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahHomeScreen(
    isArabic: Boolean,
    onPlaySurah: (SurahMeta) -> Unit,
    onOpenLibrary: () -> Unit
) {
    val currentPlayingNum = AlAmanahAudioController.currentSurah.number

    val topPicks = remember {
        listOf(
            Triple(2, "Made for You", "Al-Baqarah Station"),
            Triple(18, "Sanctuary Pick", "Al-Kahf Tranquility"),
            Triple(55, "Divine Peace", "Ar-Rahman Reflection"),
            Triple(67, "Night Haven", "Al-Mulk Sanctuary")
        )
    }

    val recentPicks = remember {
        listOf(
            Triple(36, "Discovery Station", "Surah Yaseen"),
            Triple(19, "Feel Good", "Surah Maryam"),
            Triple(89, "Dawn Repose", "Surah Al-Fajr"),
            Triple(56, "Peace of Mind", "Al-Waqi'ah Tranquility")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // ─── TOP PICKS FOR YOU SECTION ───
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isArabic) "مختارات لك" else "Top Picks for You",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                Spacer(Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(topPicks) { idx, pick ->
                        val surah = allSurahs.firstOrNull { it.number == pick.first } ?: allSurahs[0]
                        AmanahTopPickCard(
                            index = idx,
                            subtitle = if (isArabic) {
                                when (idx) {
                                    0 -> "مختارة لك"
                                    1 -> "نفحات السكينة"
                                    2 -> "طمأنينة القلوب"
                                    else -> "أمان الليل"
                                }
                            } else pick.second,
                            title = if (isArabic) surah.arabicName else pick.third,
                            onClick = { onPlaySurah(surah) }
                        )
                    }
                }
            }
        }

        // ─── RECENTLY PLAYED SECTION ───
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isArabic) "تم الاستماع مؤخراً" else "Recently Played",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                Spacer(Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(recentPicks) { idx, pick ->
                        val surah = allSurahs.firstOrNull { it.number == pick.first } ?: allSurahs[0]
                        AmanahRecentlyPlayedCard(
                            index = idx,
                            title = if (isArabic) {
                                when (idx) {
                                    0 -> "محطة الاستكشاف"
                                    1 -> "سكينة الروح"
                                    2 -> "سكينة الفجر"
                                    else -> "راحة البال"
                                }
                            } else pick.second,
                            surahName = if (isArabic) surah.arabicName else surah.englishName,
                            onClick = { onPlaySurah(surah) }
                        )
                    }
                }
            }
        }

        // ─── BROWSE ALL SURAHS HEADER ───
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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
                    fontSize = 19.sp,
                    color = Color.White
                )
                Text(
                    text = if (isArabic) "عرض الكل ❯" else "View All ❯",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = AmoledAccent
                )
            }
        }

        // Surah Items List (first 15 surahs on home feed)
        items(allSurahs.take(15)) { surah ->
            val isCurrent = surah.number == currentPlayingNum
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                AmanahSurahRow(
                    surah = surah,
                    isArabic = isArabic,
                    isPlaying = isCurrent && AlAmanahAudioController.isPlaying,
                    onClick = { onPlaySurah(surah) }
                )
            }
        }
    }
}

@Composable
private fun AmanahTopPickCard(
    index: Int,
    subtitle: String,
    title: String,
    onClick: () -> Unit
) {
    val gradientBrush = remember(index) {
        when (index % 4) {
            0 -> Brush.linearGradient(
                colors = listOf(Color(0xFFE94057), Color(0xFFF27121), Color(0xFF8A2387)),
                start = Offset.Zero,
                end = Offset(500f, 600f)
            )
            1 -> Brush.linearGradient(
                colors = listOf(Color(0xFF134E5E), Color(0xFF1E6B7B), Color(0xFF71B280)),
                start = Offset.Zero,
                end = Offset(500f, 600f)
            )
            2 -> Brush.linearGradient(
                colors = listOf(Color(0xFF654EA3), Color(0xFF8A65C8), Color(0xFFEAAFC8)),
                start = Offset.Zero,
                end = Offset(500f, 600f)
            )
            else -> Brush.linearGradient(
                colors = listOf(Color(0xFF141E30), Color(0xFF243B55), Color(0xFF1E3C72)),
                start = Offset.Zero,
                end = Offset(500f, 600f)
            )
        }
    }

    Box(
        modifier = Modifier
            .width(215.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradientBrush)
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Modern layered geometric chevron background accent
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            for (step in 1..3) {
                val offsetStep = step * 38f
                val path = Path().apply {
                    moveTo(0f, h * 0.15f + offsetStep)
                    lineTo(w * 0.72f, h * 0.45f)
                    lineTo(0f, h * 0.75f - offsetStep)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.05f * (4 - step))
                )
            }
        }

        // Bottom gradient dark scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.82f)
                        ),
                        startY = 180f
                    )
                )
        )

        // Bottom label content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = subtitle,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AmanahRecentlyPlayedCard(
    index: Int,
    title: String,
    surahName: String,
    onClick: () -> Unit
) {
    val gradientBrush = remember(index) {
        when (index % 4) {
            0 -> Brush.linearGradient(
                colors = listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2), Color(0xFF00C9FF)),
                start = Offset.Zero,
                end = Offset(400f, 400f)
            )
            1 -> Brush.linearGradient(
                colors = listOf(Color(0xFFF7971E), Color(0xFFFF9A00), Color(0xFFFFD200)),
                start = Offset.Zero,
                end = Offset(400f, 400f)
            )
            2 -> Brush.linearGradient(
                colors = listOf(Color(0xFF0BA360), Color(0xFF22B878), Color(0xFF3CBA92)),
                start = Offset.Zero,
                end = Offset(400f, 400f)
            )
            else -> Brush.linearGradient(
                colors = listOf(Color(0xFF1A2980), Color(0xFF2045A0), Color(0xFF26D0CE)),
                start = Offset.Zero,
                end = Offset(400f, 400f)
            )
        }
    }

    Box(
        modifier = Modifier
            .width(148.dp)
            .height(148.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradientBrush)
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Modern geometric pattern (Diamond star on index 1, chevron fan on index 0/others)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (index % 2 == 1) {
                // Diamond motif
                val diamondPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.15f)
                    lineTo(w * 0.85f, h * 0.5f)
                    lineTo(w * 0.5f, h * 0.85f)
                    lineTo(w * 0.15f, h * 0.5f)
                    close()
                }
                drawPath(
                    path = diamondPath,
                    color = Color.White.copy(alpha = 0.22f),
                    style = Stroke(width = 3.dp.toPx())
                )
            } else {
                // Radiating fan lines
                for (i in 0..4) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = Offset(0f, 0f),
                        end = Offset(w, h * (i / 4f)),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        // Bottom dark scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.78f)
                        ),
                        startY = 70f
                    )
                )
        )

        // Bottom label content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color.White,
                maxLines = 1
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = surahName,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.78f),
                maxLines = 1
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
        // Top 3 Stats Badges (Clean Material Icons, No Emojis, No Streak)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AmanahStatBadge(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "اليوم" else "TODAY",
                    value = if (isArabic) "$todayMinutes دقيقة" else "$todayMinutes min",
                    icon = Icons.Rounded.Headphones
                )
                AmanahStatBadge(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "الأسبوع" else "THIS WEEK",
                    value = if (isArabic) "$weekMinutes دقيقة" else "$weekMinutes min",
                    icon = Icons.Rounded.DateRange
                )
                AmanahStatBadge(
                    modifier = Modifier.weight(1f),
                    title = if (isArabic) "السكينة" else "SANCTUARY",
                    value = if (isArabic) "مصحف مرتل" else "Murattal",
                    icon = Icons.Rounded.GraphicEq
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
    icon: androidx.compose.ui.graphics.vector.ImageVector
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AmoledAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(6.dp))
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

            // ─── TRACK TITLE & METADATA (WITH FAVORITE & OPTIONS) ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) surah.arabicName else surah.englishName,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "الجزء ${surah.juz} • ${surah.ayahs} آية"
                        else "Juz ${surah.juz} • ${surah.ayahs} verses",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.5.sp,
                        color = AmoledMutedText
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Favorite Star Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
                            tint = if (isFavorite) AmoledAccent else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 3-Dots Options Menu Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenSleepTimer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreHoriz,
                            contentDescription = "Options",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ─── MATERIAL 3 SQUIGGLY WAVY SEEK BAR ───
            val posMs = AlAmanahAudioController.currentPositionMs
            val durMs = AlAmanahAudioController.totalDurationMs.coerceAtLeast(1L)
            val progress = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                ) {
                    WavySeekSlider(
                        progress = progress,
                        onSeek = { frac ->
                            val target = (frac * durMs).toLong()
                            AlAmanahAudioController.seekTo(target)
                        },
                        isPlaying = isPlaying,
                        modifier = Modifier.fillMaxWidth()
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
                            text = "• ${if (isArabic) "الجزء ${surah.juz}" else "Juz ${surah.juz}"} • ${surah.ayahs} ${if (isArabic) "آية" else "Verses"}",
                            fontFamily = ThmanyahSans,
                            fontSize = 11.sp,
                            color = AmoledMutedText.copy(alpha = 0.65f)
                        )
                        Text(
                            text = "-${formatAmanahTime((durMs - posMs).coerceAtLeast(0L))}",
                            fontFamily = ThmanyahSans,
                            fontSize = 11.sp,
                            color = AmoledMutedText
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ─── MATERIAL 3 EXPRESSIVE TRANSPORT CONTROLS (PILL BUTTONS) ───
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Pill
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(alpha = 0.12f))
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
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Large Center Play / Pause Pill
                    Box(
                        modifier = Modifier
                            .width(118.dp)
                            .height(64.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { AlAmanahAudioController.togglePlayPause(context) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (AlAmanahAudioController.isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF111111),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Next Pill
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(alpha = 0.12f))
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
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ─── VOLUME ROW (LTR PERSISTENT) ───
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
            }

            Spacer(Modifier.height(6.dp))

            // ─── COMBINED OUTPUT & RECITER CAPSULE ───
            val outputType = AlAmanahAudioController.getActiveAudioOutputType(context)
            val outputIcon = if (outputType == AlAmanahAudioController.AudioOutputType.PHONE) Icons.Rounded.PhoneAndroid else Icons.Rounded.Headphones

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Media Output Selector
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { AlAmanahAudioController.openSystemAudioOutputPicker(context) }
                            )
                        ) {
                            Icon(
                                imageVector = outputIcon,
                                contentDescription = "Audio Output",
                                tint = AmoledAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 14.dp)
                                .background(Color.White.copy(alpha = 0.25f))
                        )
                        // Reciter Picker
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenReciter
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Select Reciter",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Reciter Name Label (only at the most bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenReciter
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isArabic) "بصوت القارئ ${reciter.arabicName}" else "Recited by ${reciter.englishName}",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(4.dp))

            // ─── BOTTOM SWIPING DRAWER / QUEUE SHEET PEEK ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF16181D))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenQueue
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.35f))
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) "تشغيل من " else "Playing from ",
                                fontFamily = ThmanyahSans,
                                fontSize = 11.5.sp,
                                color = AmoledMutedText
                            )
                            Text(
                                text = if (isArabic) surah.arabicName else surah.englishName,
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = "Queue",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Icon(
                                imageVector = outputIcon,
                                contentDescription = "Device",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
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
    val posMs = AlAmanahAudioController.currentPositionMs
    val durMs = AlAmanahAudioController.totalDurationMs.coerceAtLeast(1L)
    val progress = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(18.dp, RoundedCornerShape(22.dp), spotColor = Color.Black)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF191C22))
            .border(1.2.dp, AmoledCardBorder, RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExpand
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Surah Number Artwork Box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, AmoledAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AmoledAccent
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.width(95.dp)) {
                    Text(
                        text = if (isArabic) surah.arabicName else surah.englishName,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isArabic) reciter.arabicName.split(" ").take(2).joinToString(" ")
                        else reciter.englishName.split(" ").take(2).joinToString(" "),
                        fontFamily = ThmanyahSans,
                        fontSize = 10.5.sp,
                        color = AmoledMutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Mini Wavy Progress in the center
                MiniWavyProgress(
                    progress = progress,
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .padding(horizontal = 6.dp)
                )

                // Pill Play / Pause Button
                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Color.White)
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
                        tint = Color(0xFF111111),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(6.dp))

                // Next Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
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

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
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatListNumbered
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
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
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
    SETTINGS
}

// ─── PURE AMOLED DARK STYLING (SOLID NEUTRAL GREYISH-BLACK, ZERO BLUE TINT) ───
private val AmoledBg = Color(0xFF000000)
private val AmoledSurface = Color(0xFF0E0E0E)
private val AmoledCard = Color(0xFF161616)
private val AmoledCardBorder = Color(0xFF262626)
private val AmoledAccent = Color(0xFFFFFFFF)
private val AmoledCyanGlow = Color(0xFF64B5F6)
private val AmoledMutedText = Color(0xFF8E8E8E)
private val AmoledDimText = Color(0xFF555555)

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
    var showAudioOutputSheet by remember { mutableStateOf(false) }

    // Force highest possible refresh rate (120Hz) automatically for maximum performance and buttery smoothness
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        DisplayRefreshHelper.setHighRefreshRate(activity, true)
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

    val navBarBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val effectiveBottomPadding = maxOf(navBarBottomInset, 20.dp) + 6.dp

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBg)
        ) {
            // Ambient Dark AMOLED Neutral Solid Depth Background (Pure Charcoal/Black, No Blue Tint)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF161616),
                                Color(0xFF0C0C0C),
                                AmoledBg
                            )
                        )
                    )
            )

            // ─── MAIN CONTENT PAGES (Home / Library / Settings) ───
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
                                AmanahTab.SETTINGS -> if (isArabic) "الإعدادات" else "Settings"
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

                        // Audio Device Output Switcher Button (opens audio output sheet popup)
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showAudioOutputSheet = true }
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
                        AmanahTab.SETTINGS -> AmanahSettingsScreen(
                            isArabic = isArabic,
                            todayListenSec = todayListenSec,
                            onOpenReciter = { showReciterSheet = true },
                            onOpenSleepTimer = { showSleepTimerSheet = true },
                            onOpenAudioOutput = { showAudioOutputSheet = true }
                        )
                    }
                }

                // Space for floating mini player + bottom nav dock
                Spacer(Modifier.height(if (AlAmanahAudioController.currentPositionMs > 0 || AlAmanahAudioController.isPlaying) 146.dp else 86.dp))
            }

            // ─── DOCKED FLOATING MINI PLAYER (ABOVE BOTTOM NAV) ───
            if (!isPlayerExpanded) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = effectiveBottomPadding + 62.dp)
                ) {
                    AmanahDockedMiniPlayer(
                        isArabic = isArabic,
                        onExpand = { isPlayerExpanded = true },
                        onTogglePlay = { AlAmanahAudioController.togglePlayPause(context) },
                        onSkipNext = { AlAmanahAudioController.skipNext(context) }
                    )
                }
            }

            // ─── BOTTOM NAVIGATION DOCK (Home / Library / Settings) ───
            if (!isPlayerExpanded) {
                AmanahBottomNavCapsule(
                    currentTab = currentTab,
                    isArabic = isArabic,
                    onSelectTab = { currentTab = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = effectiveBottomPadding)
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
                    onOpenSleepTimer = { showSleepTimerSheet = true },
                    onOpenAudioOutput = { showAudioOutputSheet = true }
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

            // ─── AUDIO OUTPUT BOTTOM SHEET ───
            if (showAudioOutputSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAudioOutputSheet = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = AmoledSurface,
                    contentColor = Color.White
                ) {
                    AmanahAudioOutputSheetContent(
                        isArabic = isArabic,
                        onClose = { showAudioOutputSheet = false }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 1. HOME TAB (LIVE PICKS, RECENTLY PLAYED, REPLAY PLAYLISTS)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahHomeScreen(
    isArabic: Boolean,
    onPlaySurah: (SurahMeta) -> Unit,
    onOpenLibrary: () -> Unit
) {
    val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val dayOfWeek = remember { java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) }
    val isFriday = dayOfWeek == java.util.Calendar.FRIDAY

    // Dynamically refreshing recommendations based on calendar day and time of day
    val topPicks = remember(hour, isFriday) {
        if (isFriday) {
            listOf(
                Triple(18, if (isArabic) "سكينة الجمعة" else "Friday Serenity", if (isArabic) "سورة الكهف" else "Surah Al-Kahf"),
                Triple(55, if (isArabic) "نفحات السكينة" else "Divine Peace", if (isArabic) "سورة الرحمن" else "Surah Ar-Rahman"),
                Triple(67, if (isArabic) "أمان الليل" else "Night Haven", if (isArabic) "سورة الملك" else "Surah Al-Mulk"),
                Triple(36, if (isArabic) "قلب القرآن" else "Heart of Quran", if (isArabic) "سورة يس" else "Surah Yaseen")
            )
        } else if (hour in 4..11) {
            listOf(
                Triple(55, if (isArabic) "سكينة الصباح" else "Morning Serenity", if (isArabic) "سورة الرحمن" else "Surah Ar-Rahman"),
                Triple(20, if (isArabic) "راحة النفس" else "Peace of Mind", if (isArabic) "سورة طه" else "Surah Taha"),
                Triple(36, if (isArabic) "طمأنينة الفجر" else "Dawn Repose", if (isArabic) "سورة يس" else "Surah Yaseen"),
                Triple(93, if (isArabic) "نفحات الهدوء" else "Morning Calm", if (isArabic) "سورة الضحى" else "Surah Ad-Duha")
            )
        } else if (hour in 12..17) {
            listOf(
                Triple(19, if (isArabic) "طمأنينة القلوب" else "Heart Calm", if (isArabic) "سورة مريم" else "Surah Maryam"),
                Triple(12, if (isArabic) "عبرة وهداية" else "Divine Solace", if (isArabic) "سورة يوسف" else "Surah Yusuf"),
                Triple(21, if (isArabic) "محطة التلاوة" else "Sanctuary Pick", if (isArabic) "سورة الأنبياء" else "Surah Al-Anbiya"),
                Triple(2, if (isArabic) "حصن السكينة" else "Spiritual Fortress", if (isArabic) "سورة البقرة" else "Surah Al-Baqarah")
            )
        } else {
            listOf(
                Triple(67, if (isArabic) "أمان الليل" else "Night Haven", if (isArabic) "سورة الملك" else "Surah Al-Mulk"),
                Triple(56, if (isArabic) "هدوء المساء" else "Evening Repose", if (isArabic) "سورة الواقعة" else "Surah Al-Waqi'ah"),
                Triple(76, if (isArabic) "راحة البال" else "Peace of Mind", if (isArabic) "سورة الإنسان" else "Surah Al-Insan"),
                Triple(32, if (isArabic) "سكينة السحر" else "Night Sanctuary", if (isArabic) "سورة السجدة" else "Surah As-Sajdah")
            )
        }
    }

    val recentPicks = remember(hour) {
        listOf(
            Triple(36, if (isArabic) "محطة الاستكشاف" else "Discovery Station", if (isArabic) "سورة يس" else "Surah Yaseen"),
            Triple(19, if (isArabic) "سكينة الروح" else "Soul Serenity", if (isArabic) "سورة مريم" else "Surah Maryam"),
            Triple(89, if (isArabic) "سكينة الفجر" else "Dawn Calm", if (isArabic) "سورة الفجر" else "Surah Al-Fajr"),
            Triple(56, if (isArabic) "راحة البال" else "Peace of Mind", if (isArabic) "سورة الواقعة" else "Surah Al-Waqi'ah")
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
                            subtitle = pick.second,
                            title = pick.third,
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
                            title = pick.second,
                            surahName = pick.third,
                            onClick = { onPlaySurah(surah) }
                        )
                    }
                }
            }
        }

        // ─── REPLAY PLAYLISTS SECTION ───
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "تسجيلات الإعادة" else "Replay Playlists",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isArabic) "التسجيلات الأكثر استماعاً وسكينة" else "Most replayed sanctuary tracks",
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            color = AmoledMutedText
                        )
                    }
                    Text(
                        text = if (isArabic) "المكتبة ❯" else "Library ❯",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = AmoledAccent,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenLibrary
                        )
                    )
                }

                Spacer(Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val replayPlaylists = listOf(
                        Triple(
                            if (isArabic) "إعادة هذا الشهر" else "Month Replay '26",
                            if (isArabic) "أكثر التلاوات استماعاً هذا الشهر" else "Top sanctuary tracks this month",
                            listOf(55, 67, 18, 36)
                        ),
                        Triple(
                            if (isArabic) "التسجيلات الدائمة" else "All-Time Replay",
                            if (isArabic) "السور المفضلة والملاذ الدائم" else "Timeless Quranic sanctuaries",
                            listOf(2, 19, 12, 56)
                        ),
                        Triple(
                            if (isArabic) "سكينة الليل" else "Night Serenity Replay",
                            if (isArabic) "تلاوات الهدوء التام والنوم" else "Calming reflections for sleep",
                            listOf(76, 32, 67, 89)
                        )
                    )

                    itemsIndexed(replayPlaylists) { idx, playlist ->
                        AmanahReplayCard(
                            index = idx,
                            title = playlist.first,
                            subtitle = playlist.second,
                            surahNumbers = playlist.third,
                            onPlayFirst = {
                                val surah = allSurahs.firstOrNull { it.number == playlist.third.first() } ?: allSurahs[0]
                                onPlaySurah(surah)
                            }
                        )
                    }
                }
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
                colors = listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121)),
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
        // Modern layered chevron pattern
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
                            Color.Black.copy(alpha = 0.85f)
                        ),
                        startY = 170f
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (index % 2 == 1) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.80f)
                        ),
                        startY = 70f
                    )
                )
        )

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
                color = Color.White.copy(alpha = 0.80f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AmanahReplayCard(
    index: Int,
    title: String,
    subtitle: String,
    surahNumbers: List<Int>,
    onPlayFirst: () -> Unit
) {
    val gradientBrush = remember(index) {
        when (index % 3) {
            0 -> Brush.linearGradient(
                colors = listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121)),
                start = Offset.Zero,
                end = Offset(450f, 450f)
            )
            1 -> Brush.linearGradient(
                colors = listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF), Color(0xFF2C3E50)),
                start = Offset.Zero,
                end = Offset(450f, 450f)
            )
            else -> Brush.linearGradient(
                colors = listOf(Color(0xFF1F1C2C), Color(0xFF928DAB), Color(0xFF1F1C2C)),
                start = Offset.Zero,
                end = Offset(450f, 450f)
            )
        }
    }

    Box(
        modifier = Modifier
            .width(200.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradientBrush)
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPlayFirst
            )
    ) {
        // Geometric soundwave visual motif
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            for (i in 0..6) {
                val lineH = h * (0.18f + (i % 3) * 0.10f)
                val x = w * (0.15f + i * 0.11f)
                drawLine(
                    color = Color.White.copy(alpha = 0.22f),
                    start = Offset(x, h * 0.45f - lineH / 2),
                    end = Offset(x, h * 0.45f + lineH / 2),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Dark gradient scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.20f),
                            Color.Black.copy(alpha = 0.88f)
                        ),
                        startY = 80f
                    )
                )
        )

        // Play Pill overlay on top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Bottom label content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.80f),
                maxLines = 1
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 2. LIBRARY TAB (PLAYLIST STYLE, CURATED CARDS & SLEEK TRACK ROWS)
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
    // selectedPlaylist: 0: Full Quran (All 114), 1: Favorites, 2: Downloaded, 3: Juz Collections
    var selectedPlaylist by remember { mutableStateOf(0) }

    val downloadedCount = remember {
        allSurahs.count { AlAmanahAudioController.isSurahDownloaded(context, it.number, AlAmanahAudioController.currentReciter) }
    }

    val playlistSurahs = remember(selectedPlaylist, favorites, searchQuery) {
        val baseList = when (selectedPlaylist) {
            1 -> allSurahs.filter { favorites.contains(it.number) }
            2 -> allSurahs.filter { AlAmanahAudioController.isSurahDownloaded(context, it.number, AlAmanahAudioController.currentReciter) }
            else -> allSurahs
        }
        if (searchQuery.isBlank()) {
            baseList
        } else {
            val q = searchQuery.trim()
            baseList.filter {
                it.arabicName.contains(q, ignoreCase = true) ||
                it.englishName.contains(q, ignoreCase = true) ||
                it.number.toString() == q
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ─── CURATED PLAYLIST CARDS CAROUSEL ───
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isArabic) "قوائم التشغيل والمجموعات" else "Playlists & Collections",
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
                    val playlists = listOf(
                        AmanahPlaylistCardItem(
                            0,
                            if (isArabic) "القرآن كاملاً" else "Complete Quran",
                            if (isArabic) "١١٤ سورة مباركة" else "114 Noble Surahs",
                            Brush.linearGradient(listOf(Color(0xFF141E30), Color(0xFF243B55)))
                        ),
                        AmanahPlaylistCardItem(
                            1,
                            if (isArabic) "المفضلة" else "Favorites",
                            if (isArabic) "${favorites.size} تلاوة مميزة" else "${favorites.size} Starred tracks",
                            Brush.linearGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057)))
                        ),
                        AmanahPlaylistCardItem(
                            2,
                            if (isArabic) "المحمّلة" else "Downloaded",
                            if (isArabic) "$downloadedCount سورة بدون اتصال" else "$downloadedCount Offline tracks",
                            Brush.linearGradient(listOf(Color(0xFF134E5E), Color(0xFF71B280)))
                        ),
                        AmanahPlaylistCardItem(
                            3,
                            if (isArabic) "أجزاء المصحف" else "30 Juz' Divisions",
                            if (isArabic) "٣٠ جزءاً مرتباً" else "Organized by 30 Juz'",
                            Brush.linearGradient(listOf(Color(0xFF654EA3), Color(0xFFEAAFC8)))
                        )
                    )

                    items(playlists) { pl ->
                        val isSelected = selectedPlaylist == pl.id
                        Box(
                            modifier = Modifier
                                .width(156.dp)
                                .height(136.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(pl.brush)
                                .border(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) AmoledAccent else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { selectedPlaylist = pl.id }
                                )
                                .padding(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (pl.id) {
                                            1 -> Icons.Rounded.Star
                                            2 -> Icons.Rounded.CloudDone
                                            3 -> Icons.Rounded.DateRange
                                            else -> Icons.Rounded.LibraryMusic
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(AmoledAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Text(
                                        text = pl.title,
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = pl.subtitle,
                                        fontFamily = ThmanyahSans,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.80f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ─── SEARCH INPUT ───
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
            }
        }

        // ─── PLAYLIST TRACK ROWS / JUZ GROUP ───
        if (selectedPlaylist == 3) {
            // Group by Juz view
            items(30) { juzIndex ->
                val juzNum = juzIndex + 1
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AmanahJuzCard(
                        juzNumber = juzNum,
                        isArabic = isArabic,
                        onPlaySurah = onPlaySurah
                    )
                }
            }
        } else {
            if (playlistSurahs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "لا توجد سور في هذه القائمة" else "No surahs found in this playlist",
                            fontFamily = ThmanyahSans,
                            fontSize = 13.5.sp,
                            color = AmoledMutedText
                        )
                    }
                }
            } else {
                items(playlistSurahs) { surah ->
                    val isCurrent = surah.number == AlAmanahAudioController.currentSurah.number
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AmanahPlaylistTrackRow(
                            surah = surah,
                            isArabic = isArabic,
                            isPlaying = isCurrent && AlAmanahAudioController.isPlaying,
                            isFavorite = favorites.contains(surah.number),
                            onToggleFavorite = { onToggleFavorite(surah.number) },
                            onClick = { onPlaySurah(surah) }
                        )
                    }
                }
            }
        }
    }
}

private data class AmanahPlaylistCardItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val brush: Brush
)

@Composable
private fun AmanahPlaylistTrackRow(
    surah: SurahMeta,
    isArabic: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPlaying) Color.White.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Track index / artwork badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isPlaying) AmoledAccent.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.06f)),
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

            // Favorite star button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggleFavorite
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) AmoledAccent else AmoledMutedText.copy(alpha = 0.6f),
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(Modifier.width(4.dp))

            // Play Icon
            Icon(
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = if (isPlaying) AmoledAccent else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// 3. SETTINGS TAB (LISTENING INSIGHTS & SANCTUARY PREFERENCES)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahSettingsScreen(
    isArabic: Boolean,
    todayListenSec: Long,
    onOpenReciter: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onOpenAudioOutput: () -> Unit
) {
    val context = LocalContext.current
    val todayMinutes = todayListenSec / 60
    val weekMinutes = todayMinutes * 3
    val reciter = AlAmanahAudioController.currentReciter
    val activeTimer = AlAmanahAudioController.sleepTimerMinutes
    val deviceName = AlAmanahAudioController.getDeviceModelName()
    val downloadedCount = remember {
        allSurahs.count { AlAmanahAudioController.isSurahDownloaded(context, it.number, reciter) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top 3 Stats Badges
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

        // Large Listening Time Display Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(AmoledCard)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(22.dp))
                    .padding(vertical = 28.dp, horizontal = 20.dp),
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
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$todayMinutes",
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 54.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (isArabic) "دقيقة من القرآن الكريم" else "minutes of Quran listening",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.5.sp,
                        color = AmoledMutedText
                    )
                }
            }
        }

        // Section: Preferences & Sanctuary Settings
        item {
            Text(
                text = if (isArabic) "تفضيلات السكينة" else "Sanctuary Settings",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        // Card 1: Reciter Preference
        item {
            AmanahSettingsActionCard(
                icon = Icons.Rounded.Person,
                title = if (isArabic) "القارئ الحالي" else "Recitation Voice",
                subtitle = if (isArabic) reciter.arabicName else reciter.englishName,
                actionLabel = if (isArabic) "تغيير" else "Change",
                onClick = onOpenReciter
            )
        }

        // Card 2: Sleep Timer Preference
        item {
            AmanahSettingsActionCard(
                icon = Icons.Rounded.Bedtime,
                title = if (isArabic) "مؤقت النوم" else "Sleep Timer",
                subtitle = if (activeTimer != null) {
                    if (isArabic) "$activeTimer دقيقة متبقية" else "$activeTimer minutes active"
                } else {
                    if (isArabic) "غير مفعل" else "Not active"
                },
                actionLabel = if (isArabic) "ضبط" else "Set",
                onClick = onOpenSleepTimer
            )
        }

        // Card 3: Audio Output Device
        item {
            AmanahSettingsActionCard(
                icon = Icons.Rounded.Headphones,
                title = if (isArabic) "مخرج الصوت" else "Audio Output",
                subtitle = deviceName,
                actionLabel = if (isArabic) "اختيار" else "Select",
                onClick = onOpenAudioOutput
            )
        }

        // Card 4: Audio Storage & Downloads
        item {
            AmanahSettingsActionCard(
                icon = Icons.Rounded.CloudDone,
                title = if (isArabic) "التلاوات المحمّلة" else "Downloaded Audio",
                subtitle = if (isArabic) "$downloadedCount سورة متوفرة بدون اتصال" else "$downloadedCount surahs saved offline",
                actionLabel = if (downloadedCount > 0) (if (isArabic) "تفريغ" else "Clear") else (if (isArabic) "جاهز" else "Ready"),
                onClick = {
                    if (downloadedCount > 0) {
                        allSurahs.forEach { s ->
                            AlAmanahAudioController.deleteDownloadedSurah(context, s.number, reciter)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun AmanahSettingsActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AmoledCard)
            .border(1.dp, AmoledCardBorder, RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontFamily = ThmanyahSans,
                        fontSize = 11.5.sp,
                        color = AmoledMutedText
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = AmoledAccent
                )
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
// MODAL SHEET: AUDIO OUTPUT SELECTION (MATCHING MATERIAL SPEC)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahAudioOutputSheetContent(
    isArabic: Boolean,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val outputType = AlAmanahAudioController.getActiveAudioOutputType(context)
    val deviceName = AlAmanahAudioController.getDeviceModelName()
    val connectedBluetooth = AlAmanahAudioController.getConnectedAudioDeviceName(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "مخرج الصوت" else "Audio Output",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
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
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Device Option 1: Phone (Playing here)
        val isPhoneActive = outputType == AlAmanahAudioController.AudioOutputType.PHONE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isPhoneActive) Color.White.copy(alpha = 0.12f) else AmoledCard)
                .border(1.dp, if (isPhoneActive) AmoledAccent.copy(alpha = 0.35f) else AmoledCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhoneAndroid,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = deviceName,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isPhoneActive) (if (isArabic) "يتم التشغيل هنا" else "Playing here")
                            else (if (isArabic) "مكبر صوت الهاتف" else "Internal phone speaker"),
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            color = if (isPhoneActive) AmoledAccent else AmoledMutedText
                        )
                    }
                }
                if (isPhoneActive) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Active",
                        tint = AmoledAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Device Option 2: Bluetooth Devices / Headphones
        val isBtActive = outputType == AlAmanahAudioController.AudioOutputType.BLUETOOTH_OR_HEADSET
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isBtActive) Color.White.copy(alpha = 0.12f) else AmoledCard)
                .border(1.dp, if (isBtActive) AmoledAccent.copy(alpha = 0.35f) else AmoledCardBorder, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        AlAmanahAudioController.openSystemAudioOutputPicker(context)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = connectedBluetooth ?: (if (isArabic) "سماعات البلوتوث" else "Bluetooth headphones"),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (connectedBluetooth != null) (if (isArabic) "متصل عبر البلوتوث" else "Connected via Bluetooth")
                            else (if (isArabic) "اقتران جهاز صوتي" else "Connect a Bluetooth device"),
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            color = if (isBtActive) AmoledAccent else AmoledMutedText
                        )
                    }
                }
                if (isBtActive) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Active",
                        tint = AmoledAccent,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AmoledMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Device Option 3: Cast speakers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AmoledCard)
                .border(1.dp, AmoledCardBorder, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        AlAmanahAudioController.openCastSettings(context)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Cast,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "مكبرات الصوت الذكية" else "Chromecast and Google Cast speakers",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isArabic) "بث الصوت عبر الشبكة" else "Cast audio over local network",
                            fontFamily = ThmanyahSans,
                            fontSize = 11.5.sp,
                            color = AmoledMutedText
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AmoledMutedText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Volume control slider row
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        activeTrackColor = Color.White.copy(alpha = 0.70f),
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
    onOpenSleepTimer: () -> Unit,
    onOpenAudioOutput: () -> Unit
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
        // Deep Dark AMOLED Background Glow (Neutral Charcoal Gradient, Zero Blue Tint)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF161616),
                            Color(0xFF0C0C0C),
                            AmoledBg
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ─── MINIMAL PULL-DOWN HANDLE (TOP BAR COMPLETELY REMOVED FOR MAXIMUM SPACE) ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 42.dp, height = 4.5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCollapse
                        )
                )
            }

            // ─── SYNCHRONIZED AYAT RECITATION (GLOW & FOCUS, EXPANDED VERTICAL ROOM) ───
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
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
                            // Ayah Pill Badge (e.g. 1:2)
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

            // ─── TRACK TITLE & METADATA (WITH MODE, FAVORITE & SLEEP TIMER) ───
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
                        fontSize = 26.sp,
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
                    // Playback Mode Button (Shuffle, Loop, Repeat, Sequential)
                    val mode = AlAmanahAudioController.playbackMode
                    val modeIcon = when (mode) {
                        AlAmanahAudioController.PlaybackMode.SEQUENTIAL -> Icons.Rounded.FormatListNumbered
                        AlAmanahAudioController.PlaybackMode.REPEAT_ALL -> Icons.Rounded.Repeat
                        AlAmanahAudioController.PlaybackMode.REPEAT_ONE -> Icons.Rounded.RepeatOne
                        AlAmanahAudioController.PlaybackMode.SHUFFLE -> Icons.Rounded.Shuffle
                    }
                    val isModeCustom = mode != AlAmanahAudioController.PlaybackMode.SEQUENTIAL

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isModeCustom) Color.White.copy(alpha = 0.14f) else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { AlAmanahAudioController.cyclePlaybackMode() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = modeIcon,
                            contentDescription = "Playback Mode",
                            tint = if (isModeCustom) AmoledAccent else Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

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

                    // Sleep Timer Bedtime Icon Button (Immediately triggers sleep timer sheet)
                    val isTimerActive = AlAmanahAudioController.sleepTimerMinutes != null
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isTimerActive) Color.White.copy(alpha = 0.14f) else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenSleepTimer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = if (isTimerActive) AmoledAccent else Color.White,
                            modifier = Modifier.size(22.dp)
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

            // ─── MATERIAL 3 EXPRESSIVE ENLARGED TRANSPORT CONTROLS (PILL BUTTONS) ───
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Pill (Enlarged)
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(62.dp)
                            .clip(RoundedCornerShape(24.dp))
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
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Large Center Play / Pause Pill (Enlarged)
                    Box(
                        modifier = Modifier
                            .width(136.dp)
                            .height(72.dp)
                            .clip(RoundedCornerShape(32.dp))
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
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF111111),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    // Next Pill (Enlarged)
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(62.dp)
                            .clip(RoundedCornerShape(24.dp))
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
                            modifier = Modifier.size(32.dp)
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
                        // Media Output Selector (Opens audio output pop-up sheet)
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenAudioOutput
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

            // ─── BOTTOM DRAGGABLE DRAWER / QUEUE SHEET PEEK ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(AmoledCard)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount < -10f) {
                                onOpenQueue()
                            }
                        }
                    }
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
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onOpenAudioOutput
                                    )
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
            .background(AmoledCard)
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
// 6. BOTTOM NAVIGATION DOCK (SAME BIG SIZE AS MAIN APP NAV BAR, SOLID DARK ACCENT)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahBottomNavCapsule(
    currentTab: AmanahTab,
    isArabic: Boolean,
    onSelectTab: (AmanahTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // Forced LTR dock layout consistent with main navigation bar
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(36.dp),
                    spotColor = Color.Black.copy(alpha = 0.50f),
                    ambientColor = Color.Black.copy(alpha = 0.70f)
                )
                .clip(RoundedCornerShape(36.dp))
                .background(Color(0xFF141414))
                .border(1.2.dp, Color(0xFF262626), RoundedCornerShape(36.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
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
                    icon = Icons.Rounded.Settings,
                    label = if (isArabic) "الإعدادات" else "Settings",
                    selected = currentTab == AmanahTab.SETTINGS,
                    onClick = { onSelectTab(AmanahTab.SETTINGS) }
                )
            }
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
            .height(46.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(if (selected) Color(0xFF282828) else Color.Transparent)
            .border(
                1.dp,
                if (selected) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                RoundedCornerShape(26.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (selected) 16.dp else 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color.White else AmoledMutedText,
                modifier = Modifier.size(20.dp)
            )
            if (selected) {
                Text(
                    text = label,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
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

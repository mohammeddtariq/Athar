package com.athar.app.ui.corner

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.athar.app.ui.theme.AtharNavGlow
import com.athar.app.ui.theme.AtharNavIconActive
import com.athar.app.ui.theme.AtharNavIconInactive
import com.athar.app.ui.theme.AtharNavPillBorder
import com.athar.app.ui.theme.AtharNavPillSelected
import com.athar.app.ui.theme.AtharNavbarBg
import com.athar.app.ui.theme.AtharNavbarBorder
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
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

private val TabMotionEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

@Composable
private fun IslamicPlaylistOrnament(
    styleIndex: Int,
    modifier: Modifier = Modifier,
    tint: Color = Color.White.copy(alpha = 0.28f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = minOf(w, h) * 0.40f

        when (styleIndex % 5) {
            0 -> {
                // Style 0: Rub el Hizb (8-Point Islamic Star)
                val halfSq = radius * 0.65f
                val path1 = Path().apply {
                    moveTo(cx - halfSq, cy - halfSq)
                    lineTo(cx + halfSq, cy - halfSq)
                    lineTo(cx + halfSq, cy + halfSq)
                    lineTo(cx - halfSq, cy + halfSq)
                    close()
                }
                drawPath(path1, tint, style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round))

                val halfSqRot = halfSq * 1.414f
                val path2 = Path().apply {
                    moveTo(cx, cy - halfSqRot)
                    lineTo(cx + halfSqRot, cy)
                    lineTo(cx, cy + halfSqRot)
                    lineTo(cx - halfSqRot, cy)
                    close()
                }
                drawPath(path2, tint, style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round))
                drawCircle(tint, radius = radius * 0.40f, style = Stroke(width = 1.6.dp.toPx()))
                drawCircle(tint, radius = 3.dp.toPx())
            }
            1 -> {
                // Style 1: Mihrab Arch (Pointed Moorish / Horseshoe Islamic Arch)
                val archWidth = radius * 1.4f
                val archHeight = radius * 1.7f
                val left = cx - archWidth / 2f
                val right = cx + archWidth / 2f
                val bottom = cy + archHeight / 2f
                val springY = cy - archHeight * 0.05f
                val apexY = cy - archHeight / 2f

                val archPath = Path().apply {
                    moveTo(left, bottom)
                    lineTo(left, springY)
                    cubicTo(left, springY - archHeight * 0.25f, cx - archWidth * 0.12f, apexY, cx, apexY)
                    cubicTo(cx + archWidth * 0.12f, apexY, right, springY - archHeight * 0.25f, right, springY)
                    lineTo(right, bottom)
                }
                drawPath(archPath, tint, style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round))

                val innerLeft = left + 7.dp.toPx()
                val innerRight = right - 7.dp.toPx()
                val innerApexY = apexY + 8.dp.toPx()
                val innerPath = Path().apply {
                    moveTo(innerLeft, bottom)
                    lineTo(innerLeft, springY)
                    cubicTo(innerLeft, springY - archHeight * 0.20f, cx - archWidth * 0.08f, innerApexY, cx, innerApexY)
                    cubicTo(cx + archWidth * 0.08f, innerApexY, innerRight, springY - archHeight * 0.20f, innerRight, springY)
                    lineTo(innerRight, bottom)
                }
                drawPath(innerPath, tint.copy(alpha = tint.alpha * 0.7f), style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round))
                drawLine(tint, Offset(cx, innerApexY), Offset(cx, innerApexY + 14.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                drawCircle(tint, radius = 2.5.dp.toPx(), center = Offset(cx, innerApexY + 16.5.dp.toPx()))
            }
            2 -> {
                // Style 2: Hilal & Najm (Islamic Crescent & Star)
                val cRadius = radius * 0.85f
                val cX = cx - radius * 0.15f
                val cY = cy

                val crescent = Path().apply {
                    addOval(androidx.compose.ui.geometry.Rect(cX - cRadius, cY - cRadius, cX + cRadius, cY + cRadius))
                }
                val cutRadius = cRadius * 0.82f
                val cutPath = Path().apply {
                    addOval(androidx.compose.ui.geometry.Rect(cX + cRadius * 0.38f - cutRadius, cY - cutRadius, cX + cRadius * 0.38f + cutRadius, cY + cutRadius))
                }
                val finalCrescent = Path().apply {
                    op(crescent, cutPath, androidx.compose.ui.graphics.PathOperation.Difference)
                }
                drawPath(finalCrescent, tint)

                val starCx = cx + radius * 0.45f
                val starCy = cy - radius * 0.12f
                val starR = radius * 0.30f
                val starPath = Path()
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) starR else starR * 0.42f
                    val angle = (i * 36 - 90) * (PI / 180.0)
                    val px = (starCx + r * kotlin.math.cos(angle)).toFloat()
                    val py = (starCy + r * kotlin.math.sin(angle)).toFloat()
                    if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
                }
                starPath.close()
                drawPath(starPath, tint)
            }
            3 -> {
                // Style 3: Shamsah Rosette (8-Fold Geometric Rosette)
                val petalCount = 8
                val outerR = radius * 1.05f
                val innerR = radius * 0.38f

                for (i in 0 until petalCount) {
                    val angle = (i * (360f / petalCount)) * (PI.toFloat() / 180f)
                    val px = cx + outerR * kotlin.math.cos(angle)
                    val py = cy + outerR * kotlin.math.sin(angle)
                    drawLine(
                        tint,
                        start = Offset(cx, cy),
                        end = Offset(px, py),
                        strokeWidth = 1.8.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        tint,
                        radius = 3.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
                drawCircle(tint, radius = innerR, style = Stroke(width = 1.8.dp.toPx()))
                drawCircle(tint, radius = radius * 0.75f, style = Stroke(width = 1.2.dp.toPx()))
                drawCircle(tint, radius = 3.dp.toPx())
            }
            else -> {
                // Style 4: Arabesque Geometric Interlaced Lattice
                val step = radius * 0.50f
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        val kx = cx + dx * step
                        val ky = cy + dy * step
                        val diamond = Path().apply {
                            moveTo(kx, ky - step * 0.48f)
                            lineTo(kx + step * 0.48f, ky)
                            lineTo(kx, ky + step * 0.48f)
                            lineTo(kx - step * 0.48f, ky)
                            close()
                        }
                        drawPath(diamond, tint, style = Stroke(width = 1.6.dp.toPx()))
                    }
                }
                drawCircle(tint, radius = radius * 1.10f, style = Stroke(width = 1.5.dp.toPx()))
            }
        }
    }
}

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

data class AmanahReplayPlaylist(
    val title: String,
    val subtitle: String,
    val surahNumbers: List<Int>,
    val styleIndex: Int
)

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
    var isAmanahNavVisible by remember { mutableStateOf(true) }
    var selectedRegularPlaylist by remember { mutableStateOf<AmanahReplayPlaylist?>(null) }
    var showMonthlyReplayView by remember { mutableStateOf(false) }

    val amanahNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -12f) {
                    isAmanahNavVisible = false
                } else if (available.y > 12f) {
                    isAmanahNavVisible = true
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(currentTab) {
        isAmanahNavVisible = true
    }

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
        if (showMonthlyReplayView) {
            showMonthlyReplayView = false
        } else if (selectedRegularPlaylist != null) {
            selectedRegularPlaylist = null
        } else if (isPlayerExpanded) {
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
                    .nestedScroll(amanahNestedScrollConnection)
            ) {
                // Top App Header when browsing tabs (Icons removed for clean, focused layout)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
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

                // Body content based on tab with fluid animated transitions
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300, easing = TabMotionEasing)) +
                         scaleIn(initialScale = 0.94f, animationSpec = tween(300, easing = TabMotionEasing)) +
                         slideInVertically(initialOffsetY = { (it * 0.04f).toInt() }, animationSpec = tween(300, easing = TabMotionEasing)))
                            .togetherWith(
                                fadeOut(animationSpec = tween(200, easing = TabMotionEasing)) +
                                scaleOut(targetScale = 0.97f, animationSpec = tween(200, easing = TabMotionEasing))
                            )
                    },
                    label = "AmanahTabTransition",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { tab ->
                    when (tab) {
                        AmanahTab.HOME -> AmanahHomeScreen(
                            isArabic = isArabic,
                            onPlaySurah = { surah ->
                                AlAmanahAudioController.playSurah(context, surah)
                                isPlayerExpanded = true
                            },
                            onOpenLibrary = { currentTab = AmanahTab.LIBRARY },
                            onOpenReplayPlaylist = { playlist -> selectedRegularPlaylist = playlist },
                            onOpenMonthlyReplay = { showMonthlyReplayView = true }
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
                            onOpenSleepTimer = { showSleepTimerSheet = true }
                        )
                    }
                }

                // Space for floating mini player + bottom nav dock
                Spacer(Modifier.height(if (AlAmanahAudioController.currentPositionMs > 0 || AlAmanahAudioController.isPlaying) 146.dp else 86.dp))
            }

            // ─── DOCKED FLOATING MINI PLAYER (ABOVE BOTTOM NAV) ───
            if (!isPlayerExpanded && !showMonthlyReplayView && selectedRegularPlaylist == null) {
                val miniPlayerBottomPadding by animateDpAsState(
                    targetValue = if (isAmanahNavVisible) effectiveBottomPadding + 70.dp else effectiveBottomPadding + 8.dp,
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f),
                    label = "miniPlayerBottomPad"
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = miniPlayerBottomPadding)
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
            AnimatedVisibility(
                visible = !isPlayerExpanded && !showMonthlyReplayView && selectedRegularPlaylist == null && isAmanahNavVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 350f)
                ) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                AmanahBottomNavCapsule(
                    currentTab = currentTab,
                    isArabic = isArabic,
                    onSelectTab = { currentTab = it },
                    modifier = Modifier.padding(bottom = effectiveBottomPadding)
                )
            }

            // ─── REGULAR REPLAY PLAYLIST DETAIL OVERLAY ───
            if (selectedRegularPlaylist != null) {
                AmanahPlaylistDetailSheet(
                    playlist = selectedRegularPlaylist!!,
                    isArabic = isArabic,
                    onClose = { selectedRegularPlaylist = null },
                    onPlaySurah = { surah ->
                        AlAmanahAudioController.playSurah(context, surah)
                        selectedRegularPlaylist = null
                        isPlayerExpanded = true
                    }
                )
            }

            // ─── MONTHLY REPLAY OVERLAY ───
            if (showMonthlyReplayView) {
                AmanahMonthlyReplayView(
                    isArabic = isArabic,
                    onClose = { showMonthlyReplayView = false },
                    onPlaySurah = { surah ->
                        AlAmanahAudioController.playSurah(context, surah)
                        isPlayerExpanded = true
                    }
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
                    onPlaySurah = { surah ->
                        AlAmanahAudioController.playSurah(context, surah)
                    },
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
    onOpenLibrary: () -> Unit,
    onOpenReplayPlaylist: (AmanahReplayPlaylist) -> Unit,
    onOpenMonthlyReplay: () -> Unit
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
                                if (idx == 0) {
                                    onOpenMonthlyReplay()
                                } else {
                                    onOpenReplayPlaylist(
                                        AmanahReplayPlaylist(
                                            title = playlist.first,
                                            subtitle = playlist.second,
                                            surahNumbers = playlist.third,
                                            styleIndex = idx
                                        )
                                    )
                                }
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
        // Procedural Islamic Geometric Ornament
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
        ) {
            IslamicPlaylistOrnament(
                styleIndex = index,
                modifier = Modifier.size(110.dp),
                tint = Color.White.copy(alpha = 0.32f)
            )
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
        // Procedural Islamic Geometric Ornament
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 18.dp)
        ) {
            IslamicPlaylistOrnament(
                styleIndex = index + 1,
                modifier = Modifier.size(76.dp),
                tint = Color.White.copy(alpha = 0.28f)
            )
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
        // Procedural Islamic Geometric Ornament
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 20.dp)
        ) {
            IslamicPlaylistOrnament(
                styleIndex = index + 2,
                modifier = Modifier.size(96.dp),
                tint = Color.White.copy(alpha = 0.30f)
            )
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
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .align(Alignment.BottomEnd)
                                    .padding(top = 10.dp)
                            ) {
                                IslamicPlaylistOrnament(
                                    styleIndex = pl.id + 1,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = Color.White.copy(alpha = 0.20f)
                                )
                            }

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
    onOpenSleepTimer: () -> Unit
) {
    val context = LocalContext.current
    val todayMinutes = todayListenSec / 60
    val weekMinutes = todayMinutes * 3
    val reciter = AlAmanahAudioController.currentReciter
    val activeTimer = AlAmanahAudioController.sleepTimerMinutes

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
                    title = if (isArabic) "الأَمَنَة" else "AL-AMANAH",
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

        // Section: Al-Amanah Preferences
        item {
            Text(
                text = if (isArabic) "تفضيلات الأَمَنَة" else "Al-Amanah Preferences",
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        AlAmanahAudioController.setPreferredAudioTarget(AlAmanahAudioController.AudioOutputType.PHONE)
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
                        AlAmanahAudioController.setPreferredAudioTarget(AlAmanahAudioController.AudioOutputType.BLUETOOTH_OR_HEADSET)
                        if (connectedBluetooth == null) {
                            AlAmanahAudioController.openSystemAudioOutputPicker(context)
                        }
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
// 3.5. PLAYLIST DETAIL & MONTHLY REPLAY OVERLAYS
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun AmanahPlaylistDetailSheet(
    playlist: AmanahReplayPlaylist,
    isArabic: Boolean,
    onClose: () -> Unit,
    onPlaySurah: (SurahMeta) -> Unit
) {
    val playlistSurahs = remember(playlist.surahNumbers) {
        playlist.surahNumbers.mapNotNull { num -> allSurahs.find { it.number == num } }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
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
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Playlist Cover Card & Header
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(190.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF222222),
                                            Color(0xFF141414)
                                        )
                                    )
                                )
                                .border(1.2.dp, AmoledCardBorder, RoundedCornerShape(26.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            IslamicPlaylistOrnament(
                                styleIndex = playlist.styleIndex,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(28.dp),
                                tint = Color.White.copy(alpha = 0.50f)
                            )
                        }

                        Spacer(Modifier.height(18.dp))

                        Text(
                            text = playlist.title,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${playlist.subtitle} • ${playlist.surahNumbers.size} ${if (isArabic) "سور" else "surahs"}",
                            fontFamily = ThmanyahSans,
                            fontSize = 13.sp,
                            color = AmoledMutedText,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(18.dp))

                        // Play All & Shuffle Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Play All
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            playlistSurahs.firstOrNull()?.let { onPlaySurah(it) }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = if (isArabic) "تشغيل الكل" else "Play All",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            // Shuffle
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            playlistSurahs.shuffled().firstOrNull()?.let { onPlaySurah(it) }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shuffle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isArabic) "خلط" else "Shuffle",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "السور في هذه القائمة" else "Surahs in Playlist",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                itemsIndexed(playlistSurahs) { _, surah ->
                    val isPlaying = surah.number == AlAmanahAudioController.currentSurah.number && AlAmanahAudioController.isPlaying
                    AmanahSurahRow(
                        surah = surah,
                        isArabic = isArabic,
                        isPlaying = isPlaying,
                        onClick = { onPlaySurah(surah) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AmanahMonthlyReplayView(
    isArabic: Boolean,
    onClose: () -> Unit,
    onPlaySurah: (SurahMeta) -> Unit
) {
    val months = if (isArabic) {
        listOf("مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر")
    } else {
        listOf("May", "Jun", "Jul", "Aug", "Sep", "Oct")
    }
    var selectedMonthIndex by remember { mutableStateOf(4) } // September by default

    val topSurahs = remember {
        listOf(
            allSurahs.first { it.number == 18 }, // Al-Kahf
            allSurahs.first { it.number == 67 }, // Al-Mulk
            allSurahs.first { it.number == 36 }, // Ya-Sin
            allSurahs.first { it.number == 55 }  // Ar-Rahman
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBg)
    ) {
        // Ambient Warm Golden/Amber Replay Glow Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE65100).copy(alpha = 0.28f),
                            Color(0xFFB71C1C).copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(Float.POSITIVE_INFINITY, 0f),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Nav Header: Back Arrow, "Replay", Year Pill ('26)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onClose
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = if (isArabic) "إعادة الاستماع" else "Replay",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color.White
                    )
                }

                // Year Pill '26
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "2026",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            // Horizontal Month Tabs (May, Jun, Jul, Aug, Sep, Oct)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(months) { idx, monthName ->
                    val isSelected = idx == selectedMonthIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.16f) else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { selectedMonthIndex = idx }
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = monthName,
                            fontFamily = ThmanyahSans,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else AmoledMutedText
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Hero Listening Stat Banner
                item {
                    val currentMonth = months[selectedMonthIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isArabic) {
                                "استمعت إلى القرآن لمدة 420 دقيقة في شهر $currentMonth."
                            } else {
                                "You listened for 420 minutes in $currentMonth."
                            },
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = Color.White,
                            lineHeight = 36.sp
                        )
                    }
                }

                // Section: Top Reciters
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "أكثر القراء استماعاً" else "Your Top Reciters",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            val reciters = listOf(
                                Triple(1, QuranReciter.ALAFASY, "240"),
                                Triple(2, QuranReciter.ABDUL_BASIT, "120"),
                                Triple(3, QuranReciter.MINSHAWI, "60")
                            )
                            items(reciters) { (rank, r, min) ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 170.dp, height = 210.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF242424),
                                                    Color(0xFF121212)
                                                )
                                            )
                                        )
                                        .border(1.dp, AmoledCardBorder, RoundedCornerShape(20.dp))
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = "$rank",
                                        fontFamily = ThmanyahSerifDisplay,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 38.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )

                                    Column(
                                        modifier = Modifier.align(Alignment.BottomStart)
                                    ) {
                                        Text(
                                            text = if (isArabic) r.arabicName else r.englishName,
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = Color.White,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = if (isArabic) "$min دقيقة" else "$min minutes",
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

                // Section: Top Surahs (1-4)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isArabic) "السور الأكثر استماعاً" else "Your Top Surahs",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        topSurahs.forEachIndexed { idx, surah ->
                            val rank = idx + 1
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AmoledCard)
                                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(16.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPlaySurah(surah) }
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$rank",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = AmoledMutedText,
                                        modifier = Modifier.width(28.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isArabic) surah.arabicName else surah.englishName,
                                            fontFamily = ThmanyahSans,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isArabic) "${36 - idx * 7} تلاوة • الجزء ${surah.juz}"
                                            else "${36 - idx * 7} recitations • Juz ${surah.juz}",
                                            fontFamily = ThmanyahSans,
                                            fontSize = 11.5.sp,
                                            color = AmoledMutedText
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        tint = AmoledAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section: Milestones
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (isArabic) "إنجازاتك" else "Your Milestones",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(AmoledCard)
                                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(18.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Icon(Icons.Rounded.GraphicEq, null, tint = AmoledAccent, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = if (isArabic) "ختمة السكينة" else "Sanctuary Milestone",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isArabic) "٧ ساعات استماع" else "7 hours total",
                                        fontFamily = ThmanyahSans,
                                        fontSize = 11.sp,
                                        color = AmoledMutedText
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(AmoledCard)
                                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(18.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Icon(Icons.Rounded.NightsStay, null, tint = AmoledAccent, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = if (isArabic) "هدوء الليل" else "Night Serenity",
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isArabic) "١٤ ليلة مباركة" else "14 serene nights",
                                        fontFamily = ThmanyahSans,
                                        fontSize = 11.sp,
                                        color = AmoledMutedText
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Button: Play Monthly Mix
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    topSurahs.firstOrNull()?.let { onPlaySurah(it) }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(22.dp))
                            Text(
                                text = if (isArabic) "تشغيل مزيج الشهر" else "Play Monthly Mix",
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(30.dp))
                }
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
    onPlaySurah: (SurahMeta) -> Unit,
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
    val density = LocalDensity.current

    val peekHeight = 64.dp
    val sheetMaxHeight = 490.dp
    val peekHeightPx = with(density) { peekHeight.toPx() }
    val sheetMaxHeightPx = with(density) { sheetMaxHeight.toPx() }
    val collapsedOffset = sheetMaxHeightPx - peekHeightPx
    val expandedOffset = 0f

    var currentOffsetPx by remember { mutableFloatStateOf(collapsedOffset) }
    val animatedOffsetPx by animateFloatAsState(
        targetValue = currentOffsetPx,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 380f),
        label = "queueSheetOffset"
    )

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

    // Persistent LTR layout direction so control positions are stable across all languages
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                // ─── MINIMAL PULL-DOWN HANDLE ───
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

                // ─── SYNCHRONIZED AYAT RECITATION ───
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        itemsIndexed(surahVerses) { index, verse ->
                            val isVerseActive = (index + 1) == activeVerse
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
                                // Ayah Pill Badge
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

                                // Verse Typography with Halo Glow
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

                // ─── TRACK TITLE & METADATA (FAVORITE STAR ALONE ON RIGHT) ───
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

                    // Favorite Star Button ALONE
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
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
                }

                Spacer(Modifier.height(6.dp))

                // ─── MATERIAL 3 SQUIGGLY WAVY SEEK BAR ───
                val posMs = AlAmanahAudioController.currentPositionMs
                val durMs = AlAmanahAudioController.totalDurationMs.coerceAtLeast(1L)
                val progress = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

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

                Spacer(Modifier.height(10.dp))

                // ─── MATERIAL 3 EXPRESSIVE ENLARGED TRANSPORT CONTROLS (VERTICALLY STRETCHED) ───
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Pill
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(28.dp))
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
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Large Center Play / Pause Pill
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(88.dp)
                            .clip(RoundedCornerShape(36.dp))
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
                                modifier = Modifier.size(34.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF111111),
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    // Next Pill
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(28.dp))
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
                            modifier = Modifier.size(34.dp)
                        )
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

                Spacer(Modifier.height(8.dp))

                // ─── BOTTOM CONTROLS ROW (SLEEP TIMER - OUTPUT CAPSULE - PLAYBACK MODE DROPDOWN) ───
                val outputType = AlAmanahAudioController.getActiveAudioOutputType(context)
                val outputIcon = if (outputType == AlAmanahAudioController.AudioOutputType.PHONE) Icons.Rounded.PhoneAndroid else Icons.Rounded.Headphones

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Sleep Timer Button (Bottom-Left)
                    val isTimerActive = AlAmanahAudioController.sleepTimerMinutes != null
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isTimerActive) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.08f))
                            .border(
                                1.dp,
                                if (isTimerActive) AmoledAccent.copy(alpha = 0.5f) else Color.Transparent,
                                CircleShape
                            )
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
                            tint = if (isTimerActive) AmoledAccent else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 2. Combined Audio Output & Reciter Capsule (Center)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
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
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(width = 1.dp, height = 15.dp)
                                    .background(Color.White.copy(alpha = 0.25f))
                            )
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
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    // 3. Playback Mode Dropdown Menu (Bottom-Right)
                    var showModeDropdown by remember { mutableStateOf(false) }
                    val mode = AlAmanahAudioController.playbackMode
                    val modeIcon = when (mode) {
                        AlAmanahAudioController.PlaybackMode.SEQUENTIAL -> Icons.Rounded.FormatListNumbered
                        AlAmanahAudioController.PlaybackMode.REPEAT_ALL -> Icons.Rounded.Repeat
                        AlAmanahAudioController.PlaybackMode.REPEAT_ONE -> Icons.Rounded.RepeatOne
                        AlAmanahAudioController.PlaybackMode.SHUFFLE -> Icons.Rounded.Shuffle
                    }
                    val isModeCustom = mode != AlAmanahAudioController.PlaybackMode.SEQUENTIAL

                    Box {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isModeCustom) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.08f))
                                .border(
                                    1.dp,
                                    if (isModeCustom) AmoledAccent.copy(alpha = 0.5f) else Color.Transparent,
                                    CircleShape
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showModeDropdown = true }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = modeIcon,
                                contentDescription = "Playback Mode",
                                tint = if (isModeCustom) AmoledAccent else Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showModeDropdown,
                            onDismissRequest = { showModeDropdown = false },
                            modifier = Modifier
                                .background(AmoledCard)
                                .border(1.dp, AmoledCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            val modes = listOf(
                                Triple(AlAmanahAudioController.PlaybackMode.SEQUENTIAL, Icons.Rounded.FormatListNumbered, if (isArabic) "تشغيل متتالي" else "Play in Order"),
                                Triple(AlAmanahAudioController.PlaybackMode.REPEAT_ALL, Icons.Rounded.Repeat, if (isArabic) "تكرار القائمة" else "Repeat All"),
                                Triple(AlAmanahAudioController.PlaybackMode.REPEAT_ONE, Icons.Rounded.RepeatOne, if (isArabic) "تكرار السورة" else "Repeat Current"),
                                Triple(AlAmanahAudioController.PlaybackMode.SHUFFLE, Icons.Rounded.Shuffle, if (isArabic) "تشغيل عشوائي" else "Shuffle")
                            )
                            modes.forEach { (m, icon, title) ->
                                val isSelected = mode == m
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) AmoledAccent else AmoledMutedText,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = title,
                                                fontFamily = ThmanyahSans,
                                                fontSize = 13.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) AmoledAccent else Color.White
                                            )
                                        }
                                    },
                                    trailingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = AmoledAccent,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null,
                                    onClick = {
                                        AlAmanahAudioController.updatePlaybackMode(m)
                                        showModeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Centered Reciter Name Label
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
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(68.dp))
            }

            // ─── BOTTOM INTERACTIVE TOUCH-DRAGGABLE QUEUE DRAWER ───
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(sheetMaxHeight)
                    .offset { IntOffset(0, animatedOffsetPx.roundToInt()) }
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                    .background(AmoledCard)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Peek & Handle Header (Tracks touch in real time and toggles on tap)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        currentOffsetPx = (currentOffsetPx + dragAmount).coerceIn(expandedOffset, collapsedOffset)
                                    },
                                    onDragEnd = {
                                        val midpoint = (collapsedOffset + expandedOffset) / 2f
                                        currentOffsetPx = if (currentOffsetPx < midpoint) expandedOffset else collapsedOffset
                                    }
                                )
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    currentOffsetPx = if (currentOffsetPx < (collapsedOffset / 2f)) collapsedOffset else expandedOffset
                                }
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 4.dp)
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentOffsetPx < (collapsedOffset / 2f)) Icons.Rounded.KeyboardArrowDown else Icons.AutoMirrored.Rounded.QueueMusic,
                                    contentDescription = "Queue",
                                    tint = Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Expanded Surah Queue List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(allSurahs) { queueSurah ->
                            val isCurrent = queueSurah.number == surah.number
                            AmanahSurahRow(
                                surah = queueSurah,
                                isArabic = isArabic,
                                isPlaying = isCurrent && isPlaying,
                                onClick = {
                                    onPlaySurah(queueSurah)
                                }
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
                    shape = RoundedCornerShape(38.dp),
                    spotColor = AtharNavGlow.copy(alpha = 0.20f),
                    ambientColor = Color.Black.copy(alpha = 0.50f)
                )
                .clip(RoundedCornerShape(38.dp))
                .background(AtharNavbarBg)
                .border(1.2.dp, AtharNavbarBorder, RoundedCornerShape(38.dp))
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
    val pillBackground by animateColorAsState(
        targetValue = if (selected) AtharNavPillSelected else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "pillBg"
    )
    val pillBorderColor by animateColorAsState(
        targetValue = if (selected) AtharNavPillBorder else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "pillBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) AtharNavIconActive else AtharNavIconInactive,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "iconTint"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) AtharNavIconActive else AtharNavIconInactive,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "labelColor"
    )

    Box(
        modifier = Modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(pillBackground)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = pillBorderColor,
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (selected) 18.dp else 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(24.dp)
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                spotColor = AtharNavGlow.copy(alpha = 0.85f),
                                ambientColor = AtharNavGlow.copy(alpha = 0.50f)
                            )
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                        expandHorizontally(
                            animationSpec = spring(dampingRatio = 0.82f, stiffness = 380f),
                            expandFrom = Alignment.Start
                        ),
                exit = fadeOut(animationSpec = tween(120, easing = FastOutSlowInEasing)) +
                        shrinkHorizontally(
                            animationSpec = spring(dampingRatio = 0.95f, stiffness = 450f),
                            shrinkTowards = Alignment.Start
                        )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = labelColor,
                        maxLines = 1,
                        softWrap = false
                    )
                }
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

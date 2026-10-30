package com.athar.app.ui.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.BatteryOptimizationHelper
import com.athar.app.data.CalcMethod
import com.athar.app.data.LocationHelper
import com.athar.app.data.rememberLocationEnabler
import com.athar.app.notifications.PrayerNotifications
import com.athar.app.ui.components.AtharAnimatedLogo
import com.athar.app.ui.components.AtharThemePicker
import com.athar.app.ui.components.HanafiAsrSetting
import com.athar.app.ui.components.TypewriterText
import com.athar.app.ui.theme.AtharAccentTone
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.AtharThemeId
import com.athar.app.ui.theme.AtharThemeState
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.data.PresetCity
import com.athar.app.data.presetCities
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * First-launch setup on a solid dark background:
 *  0. The wordmark as a splash, then the greeting, the bio and the language
 *  1. Theme
 *  2. Notification permission for prayer reminders
 *  3. Battery
 *  4. Location (GPS or city) + calculation method + school
 * "Skip setup" is always visible once a language is chosen.
 */
@Composable
fun OnboardingFlow(
    onFinished: (languageCode: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }

    var step by remember { mutableStateOf(0) }
    var stepForward by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf<String?>(null) }
    var theme by remember { mutableStateOf(AtharThemeId.default) }
    var accent by remember { mutableStateOf(AtharAccentTone.GOLD) }
    var notifGranted by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<PresetCity?>(null) }
    var gpsLabel by remember { mutableStateOf<String?>(null) }
    var gpsLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var method by remember { mutableStateOf(CalcMethod.MWL) }
    var school by remember { mutableStateOf("SHAFII") }
    var gpsLocating by remember { mutableStateOf(false) }

    // The opening beats: the mark holds the centre alternating between the two
    // scripts, the greeting is then written out word by word, and only after
    // that does setup appear with the mark docked in the top corner.
    var opening by remember { mutableStateOf(OpeningBeat.Mark) }
    var scrolled by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        delay(OPENING_MARK_MILLIS)
        opening = OpeningBeat.Salam
        delay(SALAM_MILLIS)
        opening = OpeningBeat.Ready
    }

    LaunchedEffect(step) {
        scrolled = 0f
    }

    // The top bar recedes as the step is read and comes back as the reader
    // returns to the top of it. The offset only drives a layer on the bar, so
    // scrolling never recomposes the header.
    val headerScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                scrolled = (scrolled - available.y).coerceIn(0f, 2_000f)
                return Offset.Zero
            }
        }
    }

    fun fetchFreshFix() {
        gpsLocating = true
        scope.launch {
            val fix = LocationHelper.freshFix(context.applicationContext)
            if (fix != null) {
                gpsLatLng = fix
                selectedCity = null
                val lang = language ?: "ar"
                val resolved = LocationHelper.resolveCityName(context, fix.first, fix.second, lang)
                gpsLabel = resolved ?: if (lang == "en") "My Location" else "موقعي"
            }
            gpsLocating = false
        }
    }
    val requestEnableLocation = rememberLocationEnabler(onEnabled = { fetchFreshFix() })

    fun finish(skipLocation: Boolean) {
        scope.launch {
            val lang = language ?: "ar"
            prefs.setLanguage(lang)
            prefs.setThemeId(theme)
            prefs.setAccentTone(accent)
            if (!skipLocation) {
                val gps = gpsLatLng
                if (gps != null) {
                    val resolved = gpsLabel ?: LocationHelper.resolveCityName(context, gps.first, gps.second, lang)
                    val cityText = resolved ?: if (lang == "en") "My Location" else "موقعي"
                    prefs.setLocation(gps.first, gps.second, cityText)
                } else if (selectedCity != null) {
                    prefs.setLocation(
                        selectedCity!!.lat,
                        selectedCity!!.lng,
                        selectedCity!!.display(lang)
                    )
                }
            }
            prefs.setCalcMethod(method.id)
            prefs.setSchool(school)
            if (notifGranted) {
                prefs.setNotificationsMaster(true)
                PrayerNotifications.ensureChannel(context)
                PrayerNotifications.scheduleNext(context)
            }
            prefs.completeOnboarding()
            onFinished(lang)
        }
    }

    /** Moves between steps, remembering the direction for the transition. */
    fun goTo(target: Int) {
        stepForward = target >= step
        step = target
    }

    fun advanceFromNotifications() {
        goTo(if (BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)) 4 else 3)
    }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            notifGranted = true
        }
        advanceFromNotifications()
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            // Permission just granted → one-tap dialog turns providers on, then a fresh fix.
            requestEnableLocation()
        }
    }

    val isReady = opening == OpeningBeat.Ready
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 62.dp
    // While the greeting is on screen the stage keeps showing it, so the hand-over
    // to the language screen is one fade instead of a flash of empty background.
    val stageBeat = if (opening == OpeningBeat.Ready) OpeningBeat.Salam else opening

    AuroraSetupBackdrop {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(headerScroll)
        ) {
            AnimatedVisibility(
                visible = isReady,
                enter = fadeIn(tween(420, easing = FastOutSlowInEasing)) +
                    slideInVertically(tween(560, easing = FastOutSlowInEasing)) { it / 30 },
                exit = fadeOut(tween(200, easing = FastOutSlowInEasing))
            ) {
                AnimatedContent(
                    targetState = step,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        val incoming = fadeIn(tween(300, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = 0.92f,
                                    stiffness = 380f
                                )
                            ) { width -> if (stepForward) width / 6 else -width / 6 } +
                            scaleIn(
                                initialScale = 0.972f,
                                animationSpec = spring(dampingRatio = 0.92f, stiffness = 380f)
                            )
                        val outgoing = fadeOut(tween(180, easing = FastOutSlowInEasing)) +
                            slideOutHorizontally(
                                spring(dampingRatio = 0.95f, stiffness = 420f)
                            ) { width -> if (stepForward) -width / 10 else width / 10 } +
                            scaleOut(targetScale = 0.988f, animationSpec = tween(180, easing = FastOutSlowInEasing))
                        incoming togetherWith outgoing
                    },
                    label = "setupStep"
                ) { currentStep ->
                    when (currentStep) {
                        0 -> GreetingStep(
                            language = language,
                            topInset = topInset,
                            onPickLanguage = { language = it },
                            onNext = { if (language != null) goTo(1) },
                            onSkip = { finish(skipLocation = true) }
                        )
                        1 -> ThemeStep(
                            language = language,
                            theme = theme,
                            accentTone = accent,
                            topInset = topInset,
                            onPick = { picked ->
                                theme = picked
                                // Repaint the setup flow in the chosen colour immediately.
                                AtharThemeState.apply(picked, accent)
                            },
                            onPickAccent = { tone ->
                                accent = tone
                                AtharThemeState.apply(theme, tone)
                            },
                            onBack = { goTo(0) },
                            onNext = { goTo(2) }
                        )
                        2 -> NotificationStep(
                            language = language,
                            onAllow = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (granted) {
                                        notifGranted = true
                                        advanceFromNotifications()
                                    } else {
                                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    notifGranted = true
                                    advanceFromNotifications()
                                }
                            },
                            onLater = { advanceFromNotifications() },
                            onBack = { goTo(1) }
                        )
                        3 -> BatteryStep(
                            language = language,
                            onAllow = {
                                BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context)
                                goTo(4)
                            },
                            onLater = { goTo(4) },
                            onBack = { goTo(2) }
                        )
                        else -> SetupStep(
                            selectedCity = selectedCity,
                            topInset = topInset,
                            onPickCity = {
                                selectedCity = it
                                gpsLatLng = null
                                gpsLabel = null
                            },
                            gpsActive = gpsLatLng != null,
                            gpsLocating = gpsLocating,
                            gpsLabel = gpsLabel,
                            language = language,
                            onUseGps = {
                                if (LocationHelper.hasPermission(context)) {
                                    // Pops the one-tap dialog if providers are off, then takes a fresh fix.
                                    requestEnableLocation()
                                } else {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            method = method,
                            onPickMethod = { method = it },
                            school = school,
                            onPickSchool = { school = it },
                            onBack = {
                                goTo(if (BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)) 2 else 3)
                            },
                            onStart = { finish(skipLocation = false) }
                        )
                    }
                }
            }

            SetupHeader(
                step = step,
                total = 5,
                shown = isReady,
                scrollOffset = { scrolled },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            AnimatedVisibility(
                visible = !isReady,
                enter = fadeIn(tween(500, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(420, easing = FastOutSlowInEasing))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = stageBeat == OpeningBeat.Mark,
                        enter = fadeIn(tween(760, easing = FastOutSlowInEasing)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(900, easing = FastOutSlowInEasing)
                            ),
                        exit = fadeOut(tween(320, easing = FastOutSlowInEasing)) +
                            scaleOut(
                                targetScale = 1.03f,
                                animationSpec = tween(320, easing = FastOutSlowInEasing)
                            )
                    ) {
                        OpeningMark()
                    }
                    AnimatedVisibility(
                        visible = stageBeat == OpeningBeat.Salam,
                        enter = fadeIn(tween(460, easing = FastOutSlowInEasing)),
                        exit = fadeOut(tween(260, easing = FastOutSlowInEasing))
                    ) {
                        SalamGreeting()
                    }
                }
            }
        }
    }
}

// ─── The opening beats ─────────────────────────────────────────

/** What the screen is showing before setup itself begins. */
private enum class OpeningBeat { Mark, Salam, Ready }

/** How long the mark holds the centre before the greeting is written out. */
private const val OPENING_MARK_MILLIS = 3_200L

private const val SALAM_ARABIC_STEP = 280L
private const val SALAM_ENGLISH_STEP = 170L
private const val SALAM_PAUSE = 420L
private const val SALAM_HOLD = 900L

private val SALAM_ARABIC_WORDS =
    listOf("السَّلَامُ", "عَلَيْكُمْ", "وَرَحْمَةُ", "اللَّهِ", "وَبَرَكَاتُهُ")

private val SALAM_ENGLISH_WORDS =
    listOf("As-salamu", "alaykum", "wa", "rahmatullahi", "wa", "barakatuh")

private val SALAM_MILLIS =
    SALAM_ARABIC_WORDS.size * SALAM_ARABIC_STEP + SALAM_PAUSE +
        SALAM_ENGLISH_WORDS.size * SALAM_ENGLISH_STEP + SALAM_HOLD

/** How far a step scrolls before the top bar has gone. */
private val SETUP_HEADER_FADE = 150.dp

/**
 * The mark alone in the middle of the screen, alternating between the two
 * scripts while a slow glow breathes behind it.
 */
@Composable
private fun OpeningMark() {
    val glow by rememberInfiniteTransition(label = "openingGlow").animateFloat(
        initialValue = 0.10f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            tween(4600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "openingGlowAlpha"
    )

    Box(
        modifier = Modifier
            .size(320.dp)
            .drawBehind {
                val radius = size.minDimension * 0.62f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AtharPrimary.copy(alpha = glow), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AtharAnimatedLogo(
            intervalMillis = 1_400L,
            startArabic = false,
            textScale = 1.75f
        )
    }
}

/**
 * The full greeting, written out a word at a time — Arabic first, then the
 * transliteration under it — and then gone for good.
 *
 * The words are spans of one string rather than a row of separate labels, so
 * the line lays out and wraps as prose while each word still arrives on its own.
 */
@Composable
private fun SalamGreeting() {
    var revealed by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in SALAM_ARABIC_WORDS.indices) {
            delay(SALAM_ARABIC_STEP)
            revealed = i + 1
        }
        delay(SALAM_PAUSE)
        for (i in SALAM_ENGLISH_WORDS.indices) {
            delay(SALAM_ENGLISH_STEP)
            revealed = SALAM_ARABIC_WORDS.size + i + 1
        }
    }

    val arabic = remember(revealed) {
        wordByWord(SALAM_ARABIC_WORDS, 0, revealed, AtharPrimaryLight)
    }
    val english = remember(revealed) {
        wordByWord(SALAM_ENGLISH_WORDS, SALAM_ARABIC_WORDS.size, revealed, AtharTextSecondary)
    }

    Column(
        modifier = Modifier.padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = arabic,
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            lineHeight = 38.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = english,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 13.5.sp,
            lineHeight = 21.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/** [words] as one string, with the first [revealed] of them (after [offset]) inked. */
private fun wordByWord(
    words: List<String>,
    offset: Int,
    revealed: Int,
    color: Color
) = buildAnnotatedString {
    words.forEachIndexed { index, word ->
        if (index > 0) append(" ")
        val shown = revealed > offset + index
        withStyle(SpanStyle(color = color.copy(alpha = if (shown) 1f else 0f))) {
            append(word)
        }
    }
}

/**
 * The bar that stays for the rest of setup: the mark on the left and the step
 * rail on the right in both reading directions, receding as the step is read.
 *
 * The scroll offset is read inside the layer block, so a scroll invalidates the
 * draw and never recomposes the bar.
 */
@Composable
private fun SetupHeader(
    step: Int,
    total: Int,
    shown: Boolean,
    scrollOffset: () -> Float,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = shown,
        modifier = modifier.fillMaxWidth(),
        enter = fadeIn(tween(440, easing = FastOutSlowInEasing)) +
            slideInVertically(tween(520, easing = FastOutSlowInEasing)) { -it / 2 },
        exit = fadeOut(tween(240, easing = FastOutSlowInEasing))
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 18.dp, end = 20.dp, top = 6.dp)
                    .graphicsLayer {
                        val fade = (1f - scrollOffset() / SETUP_HEADER_FADE.toPx()).coerceIn(0f, 1f)
                        alpha = fade
                        translationY = (1f - fade) * -10.dp.toPx()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AtharAnimatedLogo(modifier = Modifier.padding(start = 4.dp))
                SetupStepRail(current = step, total = total)
            }
        }
    }
}
@Composable
private fun GreetingStep(
    language: String?,
    topInset: Dp,
    onPickLanguage: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val isAr = language != "en"
    val bio = if (isAr) "رفيقك المسلم" else "Your Muslim companion app"
    val title = if (isAr) "اختر لغتك" else "Choose your language"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = topInset, bottom = 32.dp)
    ) {
        BismillahHeader(language = language)
        Spacer(Modifier.height(22.dp))
        Text(
            text = title,
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            color = AtharTextPrimary
        )
        Spacer(Modifier.height(8.dp))
        // The bio takes the mark's place once the mark has lifted away.
        TypewriterText(
            text = bio,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = AtharPrimaryLight
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LanguageCard(
                languageName = "العربية",
                languageSubtitle = "Arabic",
                isSelected = language == "ar",
                onClick = { onPickLanguage("ar") },
                modifier = Modifier.weight(1f)
            )
            LanguageCard(
                languageName = "English",
                languageSubtitle = "الإنجليزية",
                isSelected = language == "en",
                onClick = { onPickLanguage("en") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(24.dp))
        SetupPrimaryButton(
            text = if (isAr) "التالي" else "Next",
            onClick = onNext,
            enabled = language != null,
            modifier = Modifier.fillMaxWidth(),
            height = 52.dp,
            shape = RoundedCornerShape(26.dp)
        )
        if (language != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (language == "ar") "تخطَّ الإعداد وابدأ" else "Skip setup & start",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AtharTextSecondary
                )
            }
        }
    }
}

/**
 * The basmala, centred under the header: the Arabic line, the transliteration
 * beneath it, and — once a language other than Arabic has been chosen — the
 * meaning as well, so it is understood and not only read out.
 */
@Composable
private fun BismillahHeader(language: String?) {
    val showMeaning = language != null && language != "ar"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 30.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = "Bismillāhi r-raḥmāni r-raḥīm",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = AtharTextSecondary.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        if (showMeaning) {
            Spacer(Modifier.height(3.dp))
            Text(
                text = "In the name of Allah, the Most Gracious, the Most Merciful",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
private fun NotificationStep(
    language: String?,
    onAllow: () -> Unit,
    onLater: () -> Unit,
    onBack: () -> Unit
) {
    // The system locale is still the default during setup, so the chosen
    // language is applied explicitly here instead of via resources.
    val isAr = language != "en"
    val title = if (isAr) "تنبيهات الصلاة" else "Prayer reminders"
    val sub = if (isAr) "اسمح بالتنبيهات ليذكّرك أَثَر عند دخول كل صلاة."
        else "Allow notifications so Athar can remind you at each prayer time."
    val allow = if (isAr) "اسمح بالتنبيهات" else "Allow reminders"
    val later = if (isAr) "ليس الآن" else "Not now"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .padding(bottom = 32.dp, top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconHalo(
            icon = Icons.Rounded.NotificationsActive,
            modifier = Modifier.revealOnEnter(0)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.revealOnEnter(1)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = sub,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .revealOnEnter(1)
                .padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(32.dp))
        SetupPrimaryButton(
            text = allow,
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .revealOnEnter(2),
            height = 52.dp,
            shape = RoundedCornerShape(26.dp)
        )
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = onLater) {
            Text(
                text = later,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AtharTextSecondary
            )
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text(
                text = if (isAr) "رجوع" else "Back",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun BatteryStep(
    language: String?,
    onAllow: () -> Unit,
    onLater: () -> Unit,
    onBack: () -> Unit
) {
    val isAr = language != "en"
    val title = if (isAr) "العمل بدون قيود في الخلفية" else "Unrestricted Background"
    val sub = if (isAr) "لضمان دقة مواقيت الصلاة ووصول تنبيهات الأذان والتحديثات في وقتها دون أن يوقفها نظام توفير الطاقة، يُرجى السماح لـ «أَثَر» بالعمل بدون قيود."
        else "To ensure prayer calls and app update alerts arrive precisely on time without system sleep delays, please allow Athar to run unrestricted in battery settings."
    val allow = if (isAr) "السماح بدون قيود" else "Allow Unrestricted"
    val later = if (isAr) "ليس الآن" else "Not now"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .padding(bottom = 32.dp, top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconHalo(
            icon = Icons.Rounded.BatteryChargingFull,
            modifier = Modifier.revealOnEnter(0)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.revealOnEnter(1)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = sub,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .revealOnEnter(1)
                .padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(32.dp))
        SetupPrimaryButton(
            text = allow,
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .revealOnEnter(2),
            height = 52.dp,
            shape = RoundedCornerShape(26.dp)
        )
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = onLater) {
            Text(
                text = later,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AtharTextSecondary
            )
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text(
                text = if (isAr) "رجوع" else "Back",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Formats bilingual Arabic and English titles with bidirectional isolation
 * (RLI / LRI / LRM) preventing punctuation marks from flipping across RTL/LTR contexts.
 */
private fun bidiBilingual(ar: String, en: String): String =
    "\u2067$ar\u2069 • \u2066$en\u200E\u2069"

@Composable
private fun SetupStep(
    selectedCity: PresetCity?,
    topInset: Dp,
    onPickCity: (PresetCity) -> Unit,
    gpsActive: Boolean,
    gpsLocating: Boolean,
    gpsLabel: String? = null,
    language: String?,
    onUseGps: () -> Unit,
    method: CalcMethod,
    onPickMethod: (CalcMethod) -> Unit,
    school: String,
    onPickSchool: (String) -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = topInset + 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = bidiBilingual("أين أنت؟", "Where are you?"),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = bidiBilingual("نستخدم موقعك فقط لحساب مواقيت الصلاة بدقة.", "Used only for prayer accuracy."),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary
            )
            Spacer(Modifier.height(12.dp))
            GpsButton(
                active = gpsActive,
                locating = gpsLocating,
                capturedText = gpsLabel,
                onClick = onUseGps
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.setup_precise),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 11.5.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val columns = if (maxWidth < 380.dp) 2 else 3
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presetCities.chunked(columns).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { city ->
                                CityChip(
                                    city = city,
                                    language = language,
                                    selected = selectedCity == city,
                                    onClick = { onPickCity(city) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            Text(
                text = bidiBilingual("طريقة الحساب", "Calculation method"),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.height(8.dp))
        }

        items(CalcMethod.entries) { m ->
            MethodRow(
                title = methodTitle(m),
                selected = method == m,
                onClick = { onPickMethod(m) }
            )
        }

        item {
            Spacer(Modifier.height(6.dp))
            HanafiAsrSetting(
                isHanafi = school == "HANAFI",
                onToggle = { on -> onPickSchool(if (on) "HANAFI" else "SHAFII") },
                lang = language
            )
        }

        item {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text(
                        text = bidiBilingual("رجوع", "Back"),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AtharTextSecondary
                    )
                }
                SetupPrimaryButton(
                    text = bidiBilingual("ابدأ", "Begin"),
                    onClick = onStart,
                    modifier = Modifier.weight(2f),
                    height = 52.dp,
                    shape = RoundedCornerShape(26.dp)
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun methodTitle(m: CalcMethod): String = when (m) {
    CalcMethod.MWL -> bidiBilingual("رابطة العالم الإسلامي", "Muslim World League")
    CalcMethod.EGYPTIAN -> bidiBilingual("الهيئة المصرية", "Egyptian Authority")
    CalcMethod.KARACHI -> bidiBilingual("جامعة كراتشي", "Karachi")
    CalcMethod.UMM_AL_QURA -> bidiBilingual("أم القرى", "Umm al-Qura")
    CalcMethod.ISNA -> bidiBilingual("أمريكا الشمالية", "ISNA North America")
    CalcMethod.MOON_SIGHTING -> bidiBilingual("لجنة الرؤية", "Moonsighting Committee")
}

@Composable
private fun GpsButton(
    active: Boolean,
    locating: Boolean,
    capturedText: String? = null,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (active) AtharPrimary.copy(alpha = 0.16f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "gpsBg"
    )
    val border by animateColorAsState(
        targetValue = if (active) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "gpsBorder"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.2.dp, border, RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) AtharPrimary else AtharPrimary.copy(alpha = 0.14f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = if (active) AtharTextOnPrimary else AtharPrimaryLight,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(Modifier.padding(6.dp))
            Column {
                Text(
                    text = bidiBilingual("استخدم موقعي", "Use my location"),
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = AtharTextPrimary
                )
                if (locating) {
                    Text(
                        text = bidiBilingual("جارٍ التحديد", "Locating…"),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = AtharPrimaryLight
                    )
                } else if (active) {
                    Text(
                        text = if (!capturedText.isNullOrBlank()) "تم الالتقاط: $capturedText" else bidiBilingual("تم الالتقاط", "Captured"),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = AtharPrimaryLight
                    )
                }
            }
        }
    }
}

@Composable
private fun CityChip(
    city: PresetCity,
    language: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (selected) AtharPrimary.copy(alpha = 0.16f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "cityBg"
    )
    val border by animateColorAsState(
        targetValue = if (selected) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "cityBorder"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = city.display(language),
            fontFamily = ThmanyahSans,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            fontSize = 12.sp,
            color = if (selected) AtharPrimaryLight else AtharTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MethodRow(title: String, selected: Boolean, onClick: () -> Unit) {
    val border by animateColorAsState(
        targetValue = if (selected) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "mBorder"
    )
    val bg by animateColorAsState(
        targetValue = if (selected) AtharPrimary.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "mBg"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = ThmanyahSans,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                fontSize = 12.5.sp,
                color = if (selected) AtharPrimaryLight else AtharTextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(AtharPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = AtharTextOnPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageCard(
    languageName: String,
    languageSubtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AtharPrimary else AtharCardBorder,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "borderColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) AtharPrimary.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bgColor"
    )
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.2.dp, borderColor, RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(14.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(AtharPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = AtharTextOnPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
            Text(
                text = languageName,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = if (isSelected) AtharPrimaryLight else AtharTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = languageSubtitle,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Applies the chosen colours to the whole app before the user has even
 * finished setup, so picking a theme is something they can see immediately.
 */
@Composable
private fun ThemeStep(
    language: String?,
    theme: AtharThemeId,
    accentTone: AtharAccentTone,
    topInset: Dp,
    onPick: (AtharThemeId) -> Unit,
    onPickAccent: (AtharAccentTone) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val isAr = language != "en"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = topInset, bottom = 32.dp)
    ) {
        Text(
            text = if (isAr) "أختر طابع أثرك" else "Pick your theme",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            color = AtharTextPrimary,
            modifier = Modifier.revealOnEnter(0)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isAr) "لونٌ واحد يسري في كل شاشة، ويظلّ لك أن تغيّره متى شئت."
                else "One hue, carried through every screen. You can change it whenever you like.",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.5.sp,
            lineHeight = 19.sp,
            color = AtharTextSecondary,
            modifier = Modifier.revealOnEnter(1)
        )
        Spacer(Modifier.height(18.dp))
        AtharThemePicker(
            selected = theme,
            isArabic = isAr,
            onPick = onPick,
            modifier = Modifier.revealOnEnter(2),
            accentTone = accentTone,
            onPickAccent = onPickAccent
        )
        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.height(52.dp)
            ) {
                Text(
                    text = if (isAr) "رجوع" else "Back",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AtharTextSecondary
                )
            }
            SetupPrimaryButton(
                text = if (isAr) "التالي" else "Next",
                onClick = onNext,
                modifier = Modifier.weight(1f),
                height = 52.dp,
                shape = RoundedCornerShape(26.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

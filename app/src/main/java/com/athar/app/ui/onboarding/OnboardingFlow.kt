package com.athar.app.ui.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.athar.app.ui.components.HanafiAsrSetting
import com.athar.app.ui.components.IslamicPatternBackground
import com.athar.app.ui.components.TypewriterText
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.data.PresetCity
import com.athar.app.data.presetCities
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.launch

/**
 * First-launch setup on a solid dark background:
 *  0. Islamic greeting + streaming bio + language choice
 *  1. Notification permission for prayer reminders
 *  2. Location (GPS or city) + calculation method + school
 * "Skip setup" is always visible.
 */
@Composable
fun OnboardingFlow(
    onFinished: (languageCode: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }

    var step by remember { mutableStateOf(0) }
    var language by remember { mutableStateOf<String?>(null) }
    var notifGranted by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf<PresetCity?>(null) }
    var gpsLabel by remember { mutableStateOf<String?>(null) }
    var gpsLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var method by remember { mutableStateOf(CalcMethod.MWL) }
    var school by remember { mutableStateOf("SHAFII") }
    var gpsLocating by remember { mutableStateOf(false) }

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

    fun advanceFromNotifications() {
        if (BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)) {
            step = 3
        } else {
            step = 2
        }
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

    // Solid dark base with the Islamic lattice — the system theme can't wash it out.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
    ) {
        IslamicPatternBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.07f,
            animated = false
        )

        when (step) {
            0 -> GreetingStep(
                language = language,
                onPickLanguage = { language = it },
                onNext = { if (language != null) step = 1 }
            )
            1 -> NotificationStep(
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
                onBack = { step = 0 }
            )
            2 -> BatteryStep(
                language = language,
                onAllow = {
                    BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(context)
                    step = 3
                },
                onLater = { step = 3 },
                onBack = { step = 1 }
            )
            else -> SetupStep(
                selectedCity = selectedCity,
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
                    step = if (BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)) 1 else 2
                },
                onStart = { finish(skipLocation = false) }
            )
        }

        // Sticky top bar with skip button for location setup step
        if (step == 3) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AtharBackground,
                                AtharBackground.copy(alpha = 0.96f),
                                AtharBackground.copy(alpha = 0.82f),
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { finish(skipLocation = true) }) {
                        Text(
                            text = if (language == "ar") "تخطَّ الإعداد" else "Skip setup",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AtharTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GreetingStep(
    language: String?,
    onPickLanguage: (String) -> Unit,
    onNext: () -> Unit
) {
    val tagline = if (language == "ar") "رفيقك المسلم" else "\u2066Your Muslim companion app\u2069"

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
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(26.dp))
        Text(
            text = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللهِ وَبَرَكَاتُهُ",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            lineHeight = 36.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "As-salamu alaykum wa rahmatullahi wa barakatuh",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = AtharTextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "أَثَر",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 50.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "ATHAR",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = AtharTextSecondary,
            letterSpacing = 6.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        // Streaming bio — replays when the language changes.
        TypewriterText(
            text = tagline,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(30.dp))
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
        Spacer(Modifier.height(28.dp))
        val enabled = language != null
        val bg by animateColorAsState(
            targetValue = if (enabled) AtharPrimary else AtharCardBorder,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "nextBg"
        )
        Button(
            onClick = onNext,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = bg,
                contentColor = AtharTextOnPrimary,
                disabledContainerColor = AtharCardBorder,
                disabledContentColor = AtharTextSecondary.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = if (language == "ar") "التالي" else "Next",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
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
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(AtharPrimary.copy(alpha = 0.14f), CircleShape)
                .border(1.dp, AtharPrimary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.NotificationsActive,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
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
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AtharPrimary,
                contentColor = AtharTextOnPrimary
            )
        ) {
            Text(
                text = allow,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }
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
        Box(
            modifier = Modifier
                .size(88.dp)
                .background(AtharPrimary.copy(alpha = 0.14f), CircleShape)
                .border(1.dp, AtharPrimary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.BatteryChargingFull,
                contentDescription = null,
                tint = AtharPrimaryLight,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
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
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AtharPrimary,
                contentColor = AtharTextOnPrimary
            )
        ) {
            Text(
                text = allow,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }
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
        contentPadding = PaddingValues(top = 80.dp, bottom = 48.dp),
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
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AtharPrimary,
                        contentColor = AtharTextOnPrimary
                    )
                ) {
                    Text(
                        text = bidiBilingual("ابدأ", "Begin"),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
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

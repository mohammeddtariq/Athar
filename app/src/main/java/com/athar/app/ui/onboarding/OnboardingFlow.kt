package com.athar.app.ui.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.CalcMethod
import com.athar.app.notifications.PrayerNotifications
import com.athar.app.ui.components.SchoolSelector
import com.athar.app.ui.components.TypewriterText
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import kotlinx.coroutines.launch

data class PresetCity(val label: String, val lat: Double, val lng: Double)

val presetCities = listOf(
    PresetCity("Al Obour", 30.25, 31.47),
    PresetCity("Cairo", 30.0444, 31.2357),
    PresetCity("Abu Dhabi", 24.4539, 55.6713),
    PresetCity("Dubai", 25.2048, 55.2708),
    PresetCity("Makkah", 21.4225, 39.8262),
    PresetCity("Madinah", 24.5247, 39.5692),
    PresetCity("Istanbul", 41.0082, 28.9784),
    PresetCity("Jakarta", -6.2088, 106.8456),
    PresetCity("London", 51.5074, -0.1278),
    PresetCity("New York", 40.7128, -74.006)
)

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

    fun finish(skipLocation: Boolean) {
        scope.launch {
            val lang = language ?: "ar"
            prefs.setLanguage(lang)
            if (!skipLocation) {
                val gps = gpsLatLng
                if (gps != null) {
                    prefs.setLocation(gps.first, gps.second, gpsLabel ?: "My location")
                } else if (selectedCity != null) {
                    prefs.setLocation(selectedCity!!.lat, selectedCity!!.lng, selectedCity!!.label)
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

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            notifGranted = true
        }
        step = 2
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            readLastLocation(context) { lat, lng ->
                gpsLatLng = lat to lng
                selectedCity = null
                gpsLabel = context.getString(R.string.setup_use_gps)
            }
        }
    }

    // Solid dark base — never translucent, so the system theme can't wash it out.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtharBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
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

        when (step) {
            0 -> GreetingStep(
                language = language,
                onPickLanguage = { language = it },
                onNext = { if (language != null) step = 1 }
            )
            1 -> NotificationStep(
                onAllow = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val granted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (granted) {
                            notifGranted = true
                            step = 2
                        } else {
                            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        notifGranted = true
                        step = 2
                    }
                },
                onLater = { step = 2 },
                onBack = { step = 0 }
            )
            else -> SetupStep(
                selectedCity = selectedCity,
                onPickCity = {
                    selectedCity = it
                    gpsLatLng = null
                },
                gpsActive = gpsLatLng != null,
                onUseGps = {
                    val fine = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val coarse = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (fine || coarse) {
                        readLastLocation(context) { lat, lng ->
                            gpsLatLng = lat to lng
                            selectedCity = null
                            gpsLabel = context.getString(R.string.setup_use_gps)
                        }
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
                onBack = { step = 1 },
                onStart = { finish(skipLocation = false) }
            )
        }
    }
}

@Composable
private fun GreetingStep(
    language: String?,
    onPickLanguage: (String) -> Unit,
    onNext: () -> Unit
) {
    val tagline = if (language == "ar") "رفيقك المسلم." else "Your Muslim companion app."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .padding(bottom = 32.dp, top = 40.dp),
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
        Spacer(Modifier.height(22.dp))
        Text(
            text = "أَثَر",
            fontFamily = ThmanyahSerifDisplay,
            fontWeight = FontWeight.Black,
            fontSize = 52.sp,
            color = AtharPrimaryLight,
            textAlign = TextAlign.Center
        )
        Text(
            text = "ATHAR",
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Medium,
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
    onAllow: () -> Unit,
    onLater: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .padding(bottom = 32.dp, top = 40.dp),
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
            text = stringResource(R.string.onboarding_notif_title),
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = AtharTextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.onboarding_notif_sub),
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
                text = stringResource(R.string.onboarding_allow),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = onLater) {
            Text(
                text = stringResource(R.string.onboarding_later),
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AtharTextSecondary
            )
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text(
                text = "رجوع • Back",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SetupStep(
    selectedCity: PresetCity?,
    onPickCity: (PresetCity) -> Unit,
    gpsActive: Boolean,
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(Modifier.height(48.dp))
            Text(
                text = "أين أنت؟ • Where are you?",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "نستخدم موقعك فقط لحساب مواقيت الصلاة بدقة. • Used only for prayer accuracy.",
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = AtharTextSecondary
            )
            Spacer(Modifier.height(12.dp))
            GpsButton(active = gpsActive, onClick = onUseGps)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                presetCities.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { city ->
                            CityChip(
                                city = city,
                                selected = selectedCity == city,
                                onClick = { onPickCity(city) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "طريقة الحساب • Calculation method",
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
            SchoolSelector(schoolId = school, onPickSchool = onPickSchool)
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
                        text = "رجوع • Back",
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
                        text = "ابدأ • Begin",
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
    CalcMethod.MWL -> "Muslim World League • رابطة العالم الإسلامي"
    CalcMethod.EGYPTIAN -> "Egyptian Authority • الهيئة المصرية"
    CalcMethod.KARACHI -> "Karachi • جامعة كراتشي"
    CalcMethod.UMM_AL_QURA -> "Umm al-Qura • أم القرى"
    CalcMethod.ISNA -> "ISNA North America • أمريكا الشمالية"
    CalcMethod.MOON_SIGHTING -> "Moonsighting Committee • لجنة الرؤية"
}

@Composable
private fun GpsButton(active: Boolean, onClick: () -> Unit) {
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
                    text = "استخدم موقعي • Use my location",
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = AtharTextPrimary
                )
                if (active) {
                    Text(
                        text = "تم الالتقاط • Captured",
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
            text = city.label,
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

@SuppressLint("MissingPermission")
private fun readLastLocation(
    context: Context,
    onResult: (Double, Double) -> Unit
) {
    try {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            try {
                val loc = manager.getLastKnownLocation(provider)
                if (loc != null) {
                    onResult(loc.latitude, loc.longitude)
                    return
                }
            } catch (_: Exception) { /* try next provider */ }
        }
    } catch (_: Exception) { /* no location */ }
}

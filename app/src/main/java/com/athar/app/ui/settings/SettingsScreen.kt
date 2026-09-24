package com.athar.app.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.core.net.toUri
import com.athar.app.R
import com.athar.app.data.AppPreferences
import com.athar.app.data.CalcMethod
import com.athar.app.data.LocationHelper
import com.athar.app.data.NumberStylePreference
import com.athar.app.data.rememberLocationEnabler
import com.athar.app.notifications.PrayerNotifications
import com.athar.app.ui.components.HanafiAsrSetting
import com.athar.app.ui.components.PatternScaffold
import com.athar.app.data.presetCities
import com.athar.app.ui.theme.AtharBackground
import com.athar.app.ui.theme.AtharCardBorder
import com.athar.app.ui.theme.AtharCardSurface
import com.athar.app.ui.theme.AtharPrimary
import com.athar.app.ui.theme.AtharPrimaryLight
import com.athar.app.ui.theme.AtharPrimaryMuted
import com.athar.app.ui.theme.AtharTextOnPrimary
import com.athar.app.ui.theme.AtharTextPrimary
import com.athar.app.ui.theme.AtharTextSecondary
import com.athar.app.ui.theme.ThmanyahSans
import com.athar.app.ui.theme.ThmanyahSerifDisplay
import com.athar.app.BuildConfig
import com.athar.app.ui.components.AppUpdateDialog
import com.athar.app.updater.AppReleaseInfo
import com.athar.app.updater.AppUpdateManager
import com.athar.app.updater.UpdateCheckResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val ATHAR_GITHUB_URL = "https://github.com/mohammeddtariq/Athar"

@Composable
fun SettingsScreen(
    targetSection: String? = null,
    onTargetSectionConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { AppPreferences(context.applicationContext) }
    val listState = rememberLazyListState()
    var isNotifHighlighted by remember { mutableStateOf(false) }

    var isCheckingUpdate by remember { mutableStateOf(false) }
    var updateStatusMessage by remember { mutableStateOf<String?>(null) }
    var availableUpdate by remember { mutableStateOf<AppReleaseInfo?>(null) }

    LaunchedEffect(targetSection) {
        if (targetSection == "notifications") {
            listState.animateScrollToItem(6)
            isNotifHighlighted = true
            onTargetSectionConsumed()
            delay(2000)
            isNotifHighlighted = false
        }
    }

    val language by prefs.selectedLanguage.collectAsState(initial = "ar")
    val numberStyle by prefs.numberStyle.collectAsState(initial = NumberStylePreference.WESTERN)
    val city by prefs.cityLabel.collectAsState(initial = null)
    val methodId by prefs.calcMethodId.collectAsState(initial = "MWL")
    val schoolId by prefs.schoolId.collectAsState(initial = "SHAFII")
    val notifMaster by prefs.notificationsMaster.collectAsState(initial = false)

    var showMethods by remember { mutableStateOf(false) }
    var showCities by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                prefs.setNotificationsMaster(true)
                PrayerNotifications.ensureChannel(context)
                PrayerNotifications.scheduleNext(context)
            }
        }
    }
    var locLocating by remember { mutableStateOf(false) }

    fun fetchFreshFix() {
        locLocating = true
        scope.launch {
            val fix = LocationHelper.freshFix(context.applicationContext)
            if (fix != null) {
                val resolved = LocationHelper.resolveCityName(context, fix.first, fix.second, language)
                val cityText = resolved ?: if (language == "en") "My Location" else "موقعي"
                prefs.setLocation(fix.first, fix.second, cityText)
                PrayerNotifications.scheduleNext(context)
            }
            locLocating = false
        }
    }
    val requestEnableLocation = rememberLocationEnabler(onEnabled = { fetchFreshFix() })

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            requestEnableLocation()
        }
    }

    fun setMaster(enabled: Boolean) {
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        scope.launch {
            prefs.setNotificationsMaster(enabled)
            PrayerNotifications.ensureChannel(context)
            if (enabled) PrayerNotifications.scheduleNext(context)
            else PrayerNotifications.cancelAll(context)
        }
    }

    PatternScaffold(patternAlpha = 0.05f) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 48.dp, bottom = 150.dp)
            ) {
            item {
                Text(
                    stringResource(R.string.settings_title),
                    color = AtharTextPrimary,
                    fontFamily = ThmanyahSans,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 16.dp)
                )
            }

            // ── Language ──
            item {
                SectionCard(title = stringResource(R.string.settings_language)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LangChip(
                            label = "العربية",
                            selected = language == "ar",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    prefs.setLanguage("ar")
                                    (context as? Activity)?.recreate()
                                }
                            }
                        )
                        LangChip(
                            label = "English",
                            selected = language == "en",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    prefs.setLanguage("en")
                                    (context as? Activity)?.recreate()
                                }
                            }
                        )
                    }
                }
            }

            // ── Number Format (Western vs. Arabic-Indic) ──
            item {
                SectionCard(title = stringResource(R.string.settings_number_format)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LangChip(
                            label = stringResource(R.string.number_format_western),
                            selected = numberStyle == NumberStylePreference.WESTERN,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    prefs.setNumberStyle(NumberStylePreference.WESTERN)
                                }
                            }
                        )
                        LangChip(
                            label = stringResource(R.string.number_format_arabic),
                            selected = numberStyle == NumberStylePreference.ARABIC_INDIC,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    prefs.setNumberStyle(NumberStylePreference.ARABIC_INDIC)
                                }
                            }
                        )
                    }
                }
            }

            // ── Location ──
            item {
                SectionCard(title = stringResource(R.string.settings_location)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = AtharPrimaryLight,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        val displayCity = city?.let {
                            if (it == "موقعي" && language == "en") "My Location"
                            else if (it.equals("My location", ignoreCase = true) && language == "ar") "موقعي"
                            else it
                        } ?: stringResource(R.string.home_location_not_set)
                        Text(
                            displayCity,
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = AtharTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallButton(
                            label = if (locLocating) "…" else stringResource(R.string.setup_use_gps),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (LocationHelper.hasPermission(context)) {
                                    requestEnableLocation()
                                } else {
                                    locationLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        )
                        SmallButton(
                            label = if (showCities) "▲" else if (language == "ar") "المدن" else "Cities",
                            modifier = Modifier.weight(1f),
                            onClick = { showCities = !showCities }
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.setup_precise),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = AtharTextSecondary
                    )
                    if (showCities) {
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            presetCities.forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            1.dp,
                                            if (city == preset.display(language)) AtharPrimary else AtharCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .background(
                                            if (city == preset.display(language)) AtharPrimary.copy(alpha = 0.12f)
                                            else Color.Transparent
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                scope.launch {
                                                    prefs.setLocation(
                                                        preset.lat, preset.lng,
                                                        preset.display(language)
                                                    )
                                                    PrayerNotifications.scheduleNext(context)
                                                    showCities = false
                                                }
                                            }
                                        )
                                        .padding(horizontal = 14.dp, vertical = 9.dp)
                                ) {
                                    Text(
                                        preset.display(language),
                                        fontFamily = ThmanyahSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AtharTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Calculation method ──
            item {
                SectionCard(title = stringResource(R.string.settings_method)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { showMethods = !showMethods }
                            )
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                    ) {
                        Text(
                            methodDisplayName(CalcMethod.fromId(methodId)),
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AtharTextPrimary
                        )
                    }
                    if (showMethods) {
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CalcMethod.entries.forEach { m ->
                                val selected = m.id == methodId
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            1.dp,
                                            if (selected) AtharPrimary else AtharCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .background(
                                            if (selected) AtharPrimary.copy(alpha = 0.12f)
                                            else Color.Transparent
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                scope.launch {
                                                    prefs.setCalcMethod(m.id)
                                                    PrayerNotifications.scheduleNext(context)
                                                    showMethods = false
                                                }
                                            }
                                        )
                                        .padding(horizontal = 14.dp, vertical = 9.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            methodDisplayName(m),
                                            fontFamily = ThmanyahSans,
                                            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                                            fontSize = 12.5.sp,
                                            color = if (selected) AtharPrimaryLight else AtharTextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (selected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(AtharPrimary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Check, null,
                                                    tint = AtharTextOnPrimary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── School (Hanafi / Maliki / Shafii / Hanbali) + Hanafi Asr ──
            item {
                SectionCard(title = stringResource(R.string.settings_school)) {
                    HanafiAsrSetting(
                        isHanafi = schoolId == "HANAFI",
                        showTitle = false,
                        onToggle = { on ->
                            scope.launch {
                                prefs.setHanafiAsr(on)
                                PrayerNotifications.scheduleNext(context)
                            }
                        }
                    )
                }
            }

            // ── Notifications ──
            item(key = "notifications") {
                SectionCard(
                    title = stringResource(R.string.settings_notifications),
                    isHighlighted = isNotifHighlighted
                ) {
                    Text(
                        stringResource(R.string.settings_notifications_sub),
                        fontFamily = ThmanyahSans,
                        fontSize = 12.sp,
                        color = AtharTextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    NotifRow(
                        label = stringResource(R.string.settings_notifications),
                        checked = notifMaster,
                        onChange = ::setMaster
                    )
                    if (notifMaster) {
                        Spacer(Modifier.height(4.dp))
                        PrayerToggleList(prefs = prefs)
                    }
                }
            }

            // ── App Logo (Standalone above About section) ──
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "أَثَر",
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 40.sp,
                        color = AtharPrimaryLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "A T H A R",
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 6.sp,
                        color = AtharTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.onboarding_tagline_short),
                        fontFamily = ThmanyahSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = AtharPrimaryMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── About + GitHub ──
            item {
                SectionCard(title = stringResource(R.string.settings_about)) {
                    Text(
                        stringResource(R.string.settings_about_sub),
                        fontFamily = ThmanyahSans,
                        fontSize = 12.sp,
                        color = AtharTextSecondary
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, ATHAR_GITHUB_URL.toUri())
                                    context.startActivity(intent)
                                }
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                stringResource(R.string.settings_github),
                                fontFamily = ThmanyahSans,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                color = AtharPrimaryLight
                            )
                            Icon(
                                Icons.AutoMirrored.Rounded.OpenInNew, null,
                                tint = AtharTextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, AtharCardBorder, RoundedCornerShape(12.dp))
                            .clickable(
                                enabled = !isCheckingUpdate,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    isCheckingUpdate = true
                                    updateStatusMessage = null
                                    scope.launch {
                                        val result = AppUpdateManager.checkForUpdate(BuildConfig.VERSION_NAME)
                                        isCheckingUpdate = false
                                        when (result) {
                                            is UpdateCheckResult.UpdateAvailable -> {
                                                availableUpdate = result.releaseInfo
                                            }
                                            is UpdateCheckResult.UpToDate -> {
                                                updateStatusMessage = context.getString(R.string.update_already_latest)
                                            }
                                            is UpdateCheckResult.Error -> {
                                                updateStatusMessage = context.getString(R.string.update_status_error)
                                            }
                                        }
                                    }
                                }
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    stringResource(R.string.settings_check_updates),
                                    fontFamily = ThmanyahSans,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.5.sp,
                                    color = AtharPrimaryLight
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    if (isCheckingUpdate) stringResource(R.string.update_checking)
                                    else updateStatusMessage ?: stringResource(R.string.settings_check_updates_sub),
                                    fontFamily = ThmanyahSans,
                                    fontSize = 11.5.sp,
                                    color = if (updateStatusMessage != null) AtharPrimary else AtharTextSecondary
                                )
                            }
                            if (isCheckingUpdate) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = AtharPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Rounded.CloudDownload,
                                    contentDescription = null,
                                    tint = AtharPrimaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        formatAppVersion(BuildConfig.VERSION_NAME),
                        fontFamily = ThmanyahSans,
                        fontSize = 11.sp,
                        color = AtharTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Fixed closing quote (identical in every language) ──
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لا غالب إلا الله",
                        fontFamily = ThmanyahSerifDisplay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AtharPrimaryLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(3.dp))
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
                    ) {
                        Text(
                            text = "\"There is no victor except Allah\"",
                            fontFamily = ThmanyahSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.5.sp,
                            color = AtharTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Translucent gradient status bar overlay matching DuasScreen
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
                .height(18.dp)
        )
    }
}

    if (availableUpdate != null) {
        AppUpdateDialog(
            releaseInfo = availableUpdate!!,
            onDismiss = { availableUpdate = null }
        )
    }
}

@Composable
private fun PrayerToggleList(prefs: AppPreferences) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prayers = listOf(
        "fajr" to R.string.home_prayer_fajr,
        "sunrise" to R.string.home_prayer_sunrise,
        "dhuhr" to R.string.home_prayer_dhuhr,
        "asr" to R.string.home_prayer_asr,
        "maghrib" to R.string.home_prayer_maghrib,
        "isha" to R.string.home_prayer_isha
    )
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        prayers.forEach { (key, res) ->
            val enabled by prefs.prayerNotificationEnabled(key).collectAsState(initial = true)
            NotifRow(
                label = stringResource(res),
                checked = enabled,
                onChange = {
                    scope.launch {
                        prefs.setPrayerNotification(key, it)
                        PrayerNotifications.scheduleNext(context)
                    }
                }
            )
        }
    }
}

@Composable
private fun NotifRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = AtharTextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AtharTextOnPrimary,
                checkedTrackColor = AtharPrimary,
                uncheckedThumbColor = AtharTextSecondary,
                uncheckedTrackColor = AtharCardBorder
            )
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    isHighlighted: Boolean = false,
    content: @Composable () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isHighlighted) AtharPrimary else AtharCardBorder,
        animationSpec = tween(500),
        label = "cardBorder"
    )
    val glowColor by animateColorAsState(
        targetValue = if (isHighlighted) AtharPrimary.copy(alpha = 0.14f) else Color.Transparent,
        animationSpec = tween(500),
        label = "cardGlow"
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AtharCardSurface.copy(alpha = 0.85f))
            .background(glowColor)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                title,
                fontFamily = ThmanyahSans,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = AtharTextPrimary
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun LangChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (selected) AtharPrimary else AtharCardBorder,
                RoundedCornerShape(12.dp)
            )
            .background(if (selected) AtharPrimary.copy(alpha = 0.14f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontFamily = ThmanyahSans,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            fontSize = 13.sp,
            color = if (selected) AtharPrimaryLight else AtharTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SmallButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AtharPrimary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontFamily = ThmanyahSans,
            fontWeight = FontWeight.Black,
            fontSize = 12.5.sp,
            color = AtharTextOnPrimary,
            textAlign = TextAlign.Center
        )
    }
}

private fun methodDisplayName(m: CalcMethod): String = when (m) {
    CalcMethod.MWL -> "Muslim World League"
    CalcMethod.EGYPTIAN -> "Egyptian General Authority"
    CalcMethod.KARACHI -> "Karachi"
    CalcMethod.UMM_AL_QURA -> "Umm al-Qura"
    CalcMethod.ISNA -> "ISNA (North America)"
    CalcMethod.MOON_SIGHTING -> "Moonsighting Committee"
}

@Composable
private fun formatAppVersion(versionName: String): String {
    val betaMatch = remember(versionName) {
        Regex("""^v?(\d+\.\d+(?:\.\d+)?)-beta\.?(\d+)?$""", RegexOption.IGNORE_CASE).matchEntire(versionName)
    }
    if (betaMatch != null) {
        val base = betaMatch.groupValues[1]
        val num = betaMatch.groupValues.getOrNull(2)
        return if (!num.isNullOrEmpty()) {
            stringResource(R.string.settings_version_beta, base, num)
        } else {
            stringResource(R.string.settings_version_beta_simple, base)
        }
    }
    return stringResource(R.string.settings_version, versionName)
}

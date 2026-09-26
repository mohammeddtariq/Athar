package com.athar.app

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.athar.app.data.AppPreferences
import com.athar.app.data.BatteryOptimizationHelper
import com.athar.app.notifications.PrayerNotifications
import com.athar.app.ui.MainScreen
import com.athar.app.ui.components.AppUpdateDialog
import com.athar.app.ui.components.BatteryOptimizationDialog
import com.athar.app.ui.onboarding.OnboardingFlow
import com.athar.app.ui.theme.AtharTheme
import com.athar.app.updater.AppReleaseInfo
import com.athar.app.updater.AppUpdateManager
import com.athar.app.updater.UpdateCheckResult
import com.athar.app.updater.UpdateCheckWorker
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var appPreferences: AppPreferences
    private var pendingUpdatePrompt = false
    private val openWidgetSettingsState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Clean up previously downloaded APK update files and schedule background check
        AppUpdateManager.cleanupDownloadedApks(applicationContext)
        UpdateCheckWorker.schedule(applicationContext)

        appPreferences = AppPreferences(applicationContext)
        pendingUpdatePrompt = intent?.getBooleanExtra(AppUpdateManager.EXTRA_OPEN_UPDATER, false) == true
        if (intent?.getBooleanExtra("open_widget_settings", false) == true) {
            openWidgetSettingsState.value = true
        }

        setContent {
            val isOnboardingCompleted by appPreferences.isOnboardingCompleted
                .collectAsState(initial = null)
            val selectedLanguage by appPreferences.selectedLanguage
                .collectAsState(initial = "ar")

            var updateInfo by remember { mutableStateOf<AppReleaseInfo?>(null) }
            var showUpdateDialog by remember { mutableStateOf(false) }
            var showBatteryDialog by remember { mutableStateOf(false) }

            // Check for updates and battery optimization after onboarding is completed
            LaunchedEffect(isOnboardingCompleted) {
                if (isOnboardingCompleted == true) {
                    val result = AppUpdateManager.checkForUpdate(BuildConfig.VERSION_NAME)
                    if (result is UpdateCheckResult.UpdateAvailable) {
                        updateInfo = result.releaseInfo
                        showUpdateDialog = true
                        AppUpdateManager.showUpdateNotification(applicationContext, result.releaseInfo)
                    } else if (!BatteryOptimizationHelper.isIgnoringBatteryOptimizations(this@MainActivity)) {
                        showBatteryDialog = true
                    }
                }
            }

            // Handle launch from notification
            LaunchedEffect(pendingUpdatePrompt) {
                if (pendingUpdatePrompt) {
                    pendingUpdatePrompt = false
                    val result = AppUpdateManager.checkForUpdate(BuildConfig.VERSION_NAME)
                    if (result is UpdateCheckResult.UpdateAvailable) {
                        updateInfo = result.releaseInfo
                        showUpdateDialog = true
                    }
                }
            }

            applyLocale(selectedLanguage)

            val layoutDirection = if (selectedLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                AtharTheme {
                    when (isOnboardingCompleted) {
                        null -> {
                            // Loading state — show nothing (brief flash)
                        }
                        false -> {
                            OnboardingFlow(
                                onFinished = { langCode ->
                                    applyLocale(langCode)
                                    recreate()
                                }
                            )
                        }
                        true -> {
                            MainScreen(
                                openWidgetSettings = openWidgetSettingsState.value,
                                onWidgetSettingsHandled = { openWidgetSettingsState.value = false }
                            )
                        }
                    }

                    if (showUpdateDialog && updateInfo != null) {
                        AppUpdateDialog(
                            releaseInfo = updateInfo!!,
                            onDismiss = {
                                showUpdateDialog = false
                                AppUpdateManager.clearUpdateNotification(applicationContext)
                                if (!BatteryOptimizationHelper.isIgnoringBatteryOptimizations(this@MainActivity)) {
                                    showBatteryDialog = true
                                }
                            }
                        )
                    }

                    if (showBatteryDialog && !showUpdateDialog) {
                        BatteryOptimizationDialog(
                            onAllow = {
                                showBatteryDialog = false
                                BatteryOptimizationHelper.requestIgnoreBatteryOptimizations(this@MainActivity)
                            },
                            onDismiss = {
                                showBatteryDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(AppUpdateManager.EXTRA_OPEN_UPDATER, false)) {
            pendingUpdatePrompt = true
        }
        if (intent.getBooleanExtra("open_widget_settings", false)) {
            openWidgetSettingsState.value = true
        }
    }

    override fun onResume() {
        super.onResume()
        PrayerNotifications.scheduleNextAsync(this)
        com.athar.app.widget.AtharWidgetUpdater.updateAllWidgets(applicationContext)
    }

    private fun applyLocale(languageCode: String) {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

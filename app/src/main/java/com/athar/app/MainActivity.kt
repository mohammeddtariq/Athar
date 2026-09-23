package com.athar.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.athar.app.data.AppPreferences
import com.athar.app.ui.MainScreen
import com.athar.app.ui.onboarding.OnboardingFlow
import com.athar.app.ui.theme.AtharTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appPreferences = AppPreferences(applicationContext)

        setContent {
            val isOnboardingCompleted by appPreferences.isOnboardingCompleted
                .collectAsState(initial = null)
            val selectedLanguage by appPreferences.selectedLanguage
                .collectAsState(initial = "ar")

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
                            MainScreen()
                        }
                    }
                }
            }
        }
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

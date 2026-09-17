package com.athar.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    private fun applyLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

package com.athar.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.athar.app.data.LanguagePreferences
import com.athar.app.ui.MainScreen
import com.athar.app.ui.onboarding.LanguageSelectionScreen
import com.athar.app.ui.theme.AtharTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var languagePreferences: LanguagePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        languagePreferences = LanguagePreferences(applicationContext)

        setContent {
            val isOnboardingCompleted by languagePreferences.isOnboardingCompleted
                .collectAsState(initial = null)
            val selectedLanguage by languagePreferences.selectedLanguage
                .collectAsState(initial = "ar")
            val scope = rememberCoroutineScope()

            // Apply locale based on stored preference
            applyLocale(selectedLanguage)

            AtharTheme {
                when (isOnboardingCompleted) {
                    null -> {
                        // Loading state — show nothing (brief flash)
                    }
                    false -> {
                        LanguageSelectionScreen(
                            onLanguageSelected = { langCode ->
                                scope.launch {
                                    languagePreferences.setLanguageAndCompleteOnboarding(langCode)
                                    applyLocale(langCode)
                                    recreate()
                                }
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
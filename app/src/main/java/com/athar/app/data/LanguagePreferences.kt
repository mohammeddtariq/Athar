package com.athar.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * DataStore-backed language & onboarding preferences.
 * Delegates to the unified [AppPreferences] singleton DataStore.
 *
 * Stores:
 * - selectedLanguage ("ar" or "en")
 * - isOnboardingCompleted (true after first language selection)
 */
class LanguagePreferences(context: Context) {
    private val appPreferences = AppPreferences(context)

    val selectedLanguage: Flow<String> = appPreferences.selectedLanguage

    val isOnboardingCompleted: Flow<Boolean> = appPreferences.isOnboardingCompleted

    suspend fun setLanguage(languageCode: String) {
        appPreferences.setLanguage(languageCode)
    }

    suspend fun completeOnboarding() {
        appPreferences.completeOnboarding()
    }

    suspend fun setLanguageAndCompleteOnboarding(languageCode: String) {
        appPreferences.setLanguageAndCompleteOnboarding(languageCode)
    }
}


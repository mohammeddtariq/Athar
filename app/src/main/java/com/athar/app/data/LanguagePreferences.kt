package com.athar.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed language & onboarding preferences.
 *
 * Stores:
 * - selectedLanguage ("ar" or "en")
 * - isOnboardingCompleted (true after first language selection)
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "athar_preferences")

class LanguagePreferences(private val context: Context) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val selectedLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "ar" // Default to Arabic
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = languageCode
        }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    suspend fun setLanguageAndCompleteOnboarding(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = languageCode
            prefs[ONBOARDING_COMPLETED_KEY] = true
        }
    }
}

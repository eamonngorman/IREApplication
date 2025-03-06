package com.example.ireapplication.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ireapplication.data.models.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _settings = MutableStateFlow(loadSettings())
    val settings: Flow<AppSettings> = _settings.asStateFlow()

    fun updateSettings(settings: AppSettings) {
        android.util.Log.d("SettingsRepository", "Updating settings - language: ${settings.language}")
        prefs.edit().apply {
            putBoolean(KEY_EXHIBIT_NOTIFICATIONS, settings.exhibitNotificationsEnabled)
            putBoolean(KEY_EVENT_NOTIFICATIONS, settings.eventNotificationsEnabled)
            putBoolean(KEY_DARK_MODE, settings.darkModeEnabled)
            putFloat(KEY_FONT_SIZE, settings.fontSizeScale)
            putString(KEY_LANGUAGE, settings.language)
            commit()
        }
        _settings.value = settings
        
        // Double check the saved value
        val savedLanguage = prefs.getString(KEY_LANGUAGE, "en")
        android.util.Log.d("SettingsRepository", "Verified saved language: $savedLanguage")
        
        // Force reload settings
        _settings.value = loadSettings()
    }

    private fun loadSettings(): AppSettings {
        val language = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        android.util.Log.d("SettingsRepository", "Loading settings - language: $language")
        android.util.Log.d("SettingsRepository", "SharedPreferences file: ${prefs.all}")
        return AppSettings(
            exhibitNotificationsEnabled = prefs.getBoolean(KEY_EXHIBIT_NOTIFICATIONS, false),
            eventNotificationsEnabled = prefs.getBoolean(KEY_EVENT_NOTIFICATIONS, false),
            darkModeEnabled = prefs.getBoolean(KEY_DARK_MODE, false),
            fontSizeScale = prefs.getFloat(KEY_FONT_SIZE, 1.0f),
            language = language
        )
    }

    companion object {
        private const val PREFS_NAME = "ire_settings"
        private const val KEY_EXHIBIT_NOTIFICATIONS = "exhibit_notifications"
        private const val KEY_EVENT_NOTIFICATIONS = "event_notifications"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_LANGUAGE = "language"
    }
} 
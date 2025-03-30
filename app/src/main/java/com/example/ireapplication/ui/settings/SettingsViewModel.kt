package com.example.ireapplication.ui.settings

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.IREApplication
import com.example.ireapplication.data.models.AppSettings
import com.example.ireapplication.data.repository.SettingsRepository
import com.example.ireapplication.utils.TextScaleUtils
import com.example.ireapplication.data.SampleDataProvider
import com.example.ireapplication.ui.splash.SplashActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .map { settings ->
            // Ensure font scale is always quantized
            settings.copy(fontSizeScale = TextScaleUtils.quantizeScale(settings.fontSizeScale))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun toggleExhibitNotifications(enabled: Boolean) {
        val currentSettings = settings.value
        settingsRepository.updateSettings(
            currentSettings.copy(exhibitNotificationsEnabled = enabled)
        )
    }

    fun toggleEventNotifications(enabled: Boolean) {
        val currentSettings = settings.value
        settingsRepository.updateSettings(
            currentSettings.copy(eventNotificationsEnabled = enabled)
        )
    }

    fun toggleHighContrast(enabled: Boolean) {
        val currentSettings = settings.value
        settingsRepository.updateSettings(
            currentSettings.copy(highContrastEnabled = enabled)
        )
    }

    // This method only updates the selected font size in memory without applying it
    fun updateSelectedFontSize(scale: Float) {
        val quantizedScale = TextScaleUtils.quantizeScale(scale)
        // We don't update shared preferences or apply the change yet
        android.util.Log.d("SettingsViewModel", "Selected font size: $quantizedScale (not applied yet)")
    }

    // This method updates the font size in settings and applies it to the app
    fun updateFontSize(scale: Float) {
        val currentSettings = settings.value
        val quantizedScale = TextScaleUtils.quantizeScale(scale)
        
        // Update settings in repository
        settingsRepository.updateSettings(
            currentSettings.copy(fontSizeScale = quantizedScale)
        )
        
        // Update the global font scale via the companion object using the correct reference
        // This avoids potential unresolved reference errors
        com.example.ireapplication.IREApplication.updateFontScale(quantizedScale)
        
        android.util.Log.d("SettingsViewModel", "Font size updated and applied: $quantizedScale")
    }

    fun updateLanguage(language: String) {
        val currentSettings = settings.value
        android.util.Log.d("LanguageChange", "Starting language change to: $language")
        android.util.Log.d("LanguageChange", "Current language was: ${currentSettings.language}")
        
        try {
            // Clear all caches first
            android.util.Log.d("LanguageChange", "Clearing SampleDataProvider cache")
            SampleDataProvider.clearCache()
            
            // Update settings in SharedPreferences
            android.util.Log.d("LanguageChange", "Updating settings in SharedPreferences")
            settingsRepository.updateSettings(
                currentSettings.copy(language = language)
            )
            
            // Update application locale
            android.util.Log.d("LanguageChange", "Updating application locale")
            IREApplication.getInstance().updateLanguage(language)
            
            // Create restart intent with language
            android.util.Log.d("LanguageChange", "Creating restart intent")
            val intent = Intent(IREApplication.getInstance(), SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("LANGUAGE_CHANGED", true)
                putExtra("SELECTED_LANGUAGE", language)
            }
            
            // Start new activity and kill current process
            android.util.Log.d("LanguageChange", "Starting new activity and killing process")
            IREApplication.getInstance().startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
        } catch (e: Exception) {
            android.util.Log.e("LanguageChange", "Error during language change", e)
            throw e
        }
    }
} 
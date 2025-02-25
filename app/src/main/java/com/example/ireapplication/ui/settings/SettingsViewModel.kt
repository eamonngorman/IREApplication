package com.example.ireapplication.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.IREApplication
import com.example.ireapplication.data.models.AppSettings
import com.example.ireapplication.data.repository.SettingsRepository
import com.example.ireapplication.utils.TextScaleUtils
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

    fun toggleDarkMode(enabled: Boolean) {
        val currentSettings = settings.value
        settingsRepository.updateSettings(
            currentSettings.copy(darkModeEnabled = enabled)
        )
    }

    fun updateFontSize(scale: Float) {
        val currentSettings = settings.value
        val quantizedScale = TextScaleUtils.quantizeScale(scale)
        settingsRepository.updateSettings(
            currentSettings.copy(fontSizeScale = quantizedScale)
        )
        // Update the global font scale
        IREApplication.updateFontScale(quantizedScale)
    }
} 
package com.example.ireapplication

import android.app.Application
import android.content.res.Configuration
import com.example.ireapplication.utils.TextScaleUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IREApplication : Application() {
    companion object {
        private var fontScale: Float = 1.0f
        
        fun updateFontScale(scale: Float) {
            fontScale = TextScaleUtils.quantizeScale(scale)
        }
        
        fun getCurrentFontScale(): Float = fontScale
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize with the saved font scale
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = TextScaleUtils.quantizeScale(fontScale)
        val context = createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
} 
package com.example.ireapplication

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import com.example.ireapplication.data.repository.SettingsRepository
import com.example.ireapplication.ui.splash.SplashActivity
import com.example.ireapplication.utils.TextScaleUtils
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class IREApplication : Application() {
    companion object {
        private var fontScale: Float = 1.0f
        private var highContrast: Boolean = false
        private lateinit var instance: IREApplication
        
        fun updateFontScale(scale: Float) {
            fontScale = TextScaleUtils.quantizeScale(scale)
        }
        
        fun updateHighContrast(enabled: Boolean) {
            highContrast = enabled
        }
        
        fun getCurrentFontScale(): Float = fontScale
        
        fun isHighContrastEnabled(): Boolean = highContrast

        fun getInstance(): IREApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        
        // Load saved settings
        val prefs = getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
        
        // Load language
        val language = prefs.getString("language", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        // Load high contrast setting
        highContrast = prefs.getBoolean("high_contrast", false)
        android.util.Log.d("IREApp", "Loaded high contrast setting: $highContrast")
        
        // Load font scale
        fontScale = prefs.getFloat("font_size", 1.0f)
        
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        // Initialize with the saved font scale
        applySettings()
        
        android.util.Log.d("IREApp", "App started successfully")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val prefs = context.getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun applySettings() {
        val configuration = Configuration(resources.configuration)
        
        // Apply font scale
        configuration.fontScale = TextScaleUtils.quantizeScale(fontScale)
        
        // Apply to application context
        val context = createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        
        // Apply to scaledDensity to ensure consistent scaling
        resources.displayMetrics.scaledDensity = resources.displayMetrics.density * configuration.fontScale
        
        android.util.Log.d("IREApp", "Applied font scale: $fontScale, effective scale: ${configuration.fontScale}")
    }

    fun updateLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun restartApp() {
        val intent = Intent(this, SplashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        
        // Force kill the app process to ensure a complete restart
        Runtime.getRuntime().exit(0)
    }
} 
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
        private lateinit var instance: IREApplication
        
        fun updateFontScale(scale: Float) {
            fontScale = TextScaleUtils.quantizeScale(scale)
        }
        
        fun getCurrentFontScale(): Float = fontScale

        fun getInstance(): IREApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        
        // Load saved language
        val prefs = getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        // Initialize with the saved font scale
        applySettings()
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
        configuration.fontScale = TextScaleUtils.quantizeScale(fontScale)
        createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)
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
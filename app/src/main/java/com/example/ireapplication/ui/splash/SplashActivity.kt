package com.example.ireapplication.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ireapplication.MainActivity
import com.example.ireapplication.R
import com.example.ireapplication.data.SampleDataProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.*

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private val TAG = "SplashActivity"
    private val MINIMUM_SPLASH_DURATION = 1000L // 1 second minimum display time

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"
        val locale = Locale(language)
        val config = Configuration(newBase.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle language change if coming from settings
        if (intent?.getBooleanExtra("LANGUAGE_CHANGED", false) == true) {
            val language = intent?.getStringExtra("SELECTED_LANGUAGE") ?: "en"
            android.util.Log.d("SplashActivity", "Language change detected. New language: $language")
            updateLocale(language)
            
            // Save the language to preferences
            getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
                .edit()
                .putString("language", language)
                .commit()
        } else {
            android.util.Log.d("SplashActivity", "No language change detected")
            // Load saved language from preferences
            val prefs = getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
            val savedLanguage = prefs.getString("language", "en") ?: "en"
            android.util.Log.d("SplashActivity", "Loading saved language: $savedLanguage")
            updateLocale(savedLanguage)
        }
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Force configuration update
        val config = resources.configuration
        val locale = Locale(getSharedPreferences("ire_settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en")
        Locale.setDefault(locale)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            
            // Clear caches before loading new data
            SampleDataProvider.clearCache()
            
            // Prepare data and preload images concurrently
            val preparations = listOf(
                async { prepareData() },
                async { preloadImages() }
            )
            preparations.awaitAll()

            // Ensure minimum display time
            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime < MINIMUM_SPLASH_DURATION) {
                delay(MINIMUM_SPLASH_DURATION - elapsedTime)
            }

            startMainActivity()
        }
    }

    private fun updateLocale(language: String) {
        try {
            android.util.Log.d("SplashActivity", "Updating locale to: $language")
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            val context = createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
            
            // Verify the change
            val currentLocale = resources.configuration.locales[0].language
            android.util.Log.d("SplashActivity", "Current locale after update: $currentLocale")
            if (currentLocale != language) {
                android.util.Log.e("SplashActivity", "Locale update failed! Expected: $language, Got: $currentLocale")
            }
        } catch (e: Exception) {
            android.util.Log.e("SplashActivity", "Error updating locale", e)
            throw e
        }
    }

    private suspend fun prepareData() = withContext(Dispatchers.IO) {
        try {
            // Pre-fetch and cache all data
            SampleDataProvider.getFloors(this@SplashActivity)
            SampleDataProvider.getExhibits(this@SplashActivity)
            Log.d(TAG, "Data preparation complete")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prepare data", e)
        }
    }

    private suspend fun preloadImages() = withContext(Dispatchers.IO) {
        try {
            val imageResources = mutableListOf<Int>()
            
            // Add floor images
            SampleDataProvider.getFloors(this@SplashActivity).forEach { floor ->
                imageResources.add(floor.imageResourceId)
            }
            
            // Add exhibit images
            SampleDataProvider.getExhibits(this@SplashActivity).forEach { exhibit ->
                imageResources.add(exhibit.imageResourceId)
            }

            // Preload all images concurrently
            imageResources.distinct().map { resourceId ->
                async(Dispatchers.Main) {
                    try {
                        // Skip XML drawable preloading to avoid Glide errors
                        val resourceTypeName = resources.getResourceTypeName(resourceId)
                        if (resourceTypeName == "drawable") {
                            val resourceEntryName = resources.getResourceEntryName(resourceId)
                            // Skip XML drawables to avoid Glide issues
                            if (resourceEntryName.endsWith("_image") || 
                                resourceEntryName == "placeholder_image" || 
                                resourceEntryName == "error_image") {
                                Log.d(TAG, "Skipping XML drawable: $resourceEntryName")
                                return@async
                            }
                        }
                        
                        // Only preload bitmap images
                        Glide.with(this@SplashActivity)
                            .load(resourceId)
                            .skipMemoryCache(false)  // Use memory cache
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)  // Cache only final images
                            .preload()
                        Log.d(TAG, "Preloaded image: $resourceId")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to preload image: $resourceId", e)
                    }
                }
            }.awaitAll()

            viewModel.setPreloadingComplete(true)
            Log.d(TAG, "All images preloaded")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to preload images", e)
            viewModel.setPreloadingComplete(true)
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
} 
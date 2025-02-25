package com.example.ireapplication.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
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

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private val TAG = "SplashActivity"
    private val MINIMUM_SPLASH_DURATION = 1000L // 1 second minimum display time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            
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

    private suspend fun prepareData() = withContext(Dispatchers.IO) {
        try {
            // Pre-fetch and cache all data
            SampleDataProvider.getFloors()
            SampleDataProvider.getExhibits()
            Log.d(TAG, "Data preparation complete")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prepare data", e)
        }
    }

    private suspend fun preloadImages() = withContext(Dispatchers.IO) {
        try {
            val imageResources = mutableListOf<Int>()
            
            // Add floor images
            SampleDataProvider.getFloors().forEach { floor ->
                imageResources.add(floor.imageResourceId)
            }
            
            // Add exhibit images
            SampleDataProvider.getExhibits().forEach { exhibit ->
                imageResources.add(exhibit.imageResourceId)
            }

            // Preload all images concurrently
            imageResources.distinct().map { resourceId ->
                async(Dispatchers.Main) {
                    try {
                        Glide.with(this@SplashActivity)
                            .load(resourceId)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache both original & resized
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
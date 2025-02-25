package com.example.ireapplication.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.widget.TextView
import kotlin.math.roundToInt

object TextScaleUtils {
    const val MIN_SCALE = 1.0f
    const val MAX_SCALE = 3.0f
    const val STEP_SIZE = 0.2f

    fun scaleText(textView: TextView, scale: Float) {
        // Get the default text size in SP
        val defaultSize = textView.textSize / textView.context.resources.displayMetrics.scaledDensity
        // Set the new scaled size in SP
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultSize * scale)
    }

    fun applyFontScale(activity: Activity, scale: Float) {
        // Ensure the scale is properly quantized
        val quantizedScale = quantizeScale(scale)
        
        val configuration = Configuration(activity.resources.configuration)
        configuration.fontScale = quantizedScale
        
        // Create a new context with the updated configuration
        val context = activity.createConfigurationContext(configuration)
        
        // Update the activity's resources configuration
        activity.resources.updateConfiguration(configuration, activity.resources.displayMetrics)
        
        // Apply the new configuration
        activity.resources.displayMetrics.scaledDensity = activity.resources.displayMetrics.density * quantizedScale
    }

    fun quantizeScale(scale: Float): Float {
        // Handle edge cases first
        if (scale >= MAX_SCALE) return MAX_SCALE
        if (scale <= MIN_SCALE) return MIN_SCALE
        
        // Calculate steps and round to avoid floating point precision issues
        val steps = ((scale - MIN_SCALE) / STEP_SIZE).roundToInt()
        val result = MIN_SCALE + (steps * STEP_SIZE)
        
        // Final bounds check to ensure we never exceed limits
        return result.coerceIn(MIN_SCALE, MAX_SCALE)
    }
} 
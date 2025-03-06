package com.example.ireapplication.data.models

data class AppSettings(
    val exhibitNotificationsEnabled: Boolean = false,
    val eventNotificationsEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val fontSizeScale: Float = 1.0f,
    val language: String = "en" // Default to English
) 
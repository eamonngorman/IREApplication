package com.example.ireapplication.ui.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private val _isPreloadingComplete = MutableStateFlow(false)
    val isPreloadingComplete: StateFlow<Boolean> = _isPreloadingComplete

    fun setPreloadingComplete(complete: Boolean) {
        _isPreloadingComplete.value = complete
    }
} 
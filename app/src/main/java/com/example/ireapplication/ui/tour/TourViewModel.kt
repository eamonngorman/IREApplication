package com.example.ireapplication.ui.tour

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.data.repository.IRERepository
import com.example.ireapplication.data.SampleDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourViewModel @Inject constructor(
    private val repository: IRERepository,
    private val application: Application
) : ViewModel() {
    
    // Expose floors directly from repository
    val floors = repository.allFloorsWithExhibits

    init {
        // Load sample data on first launch
        viewModelScope.launch {
            val floors = SampleDataProvider.getFloors(application)
            val exhibits = SampleDataProvider.getExhibits(application)
            repository.populateDatabase(floors, exhibits)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val floors = SampleDataProvider.getFloors(application)
            val exhibits = SampleDataProvider.getExhibits(application)
            repository.populateDatabase(floors, exhibits)
        }
    }
} 
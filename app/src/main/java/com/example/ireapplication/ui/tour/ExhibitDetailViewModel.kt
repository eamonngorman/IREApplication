package com.example.ireapplication.ui.tour

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.R
import com.example.ireapplication.data.SampleDataProvider
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.repository.IRERepository
import com.example.ireapplication.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExhibitDetailViewModel @Inject constructor(
    private val repository: IRERepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _exhibit = MutableLiveData<Exhibit>()
    val exhibit: LiveData<Exhibit> = _exhibit

    // For debugging, handle these specific IDs specially
    private val specialExhibitIds = setOf(1, 28)

    fun loadExhibit(exhibitId: Int) {
        ErrorHandler.logDebug("ExhibitDetailViewModel: Loading exhibit ID $exhibitId")
        
        viewModelScope.launch {
            try {
                // Special handling for specific IDs for debugging
                if (specialExhibitIds.contains(exhibitId)) {
                    ErrorHandler.logDebug("ExhibitDetailViewModel: Special exhibit ID detected: $exhibitId")
                    
                    // For these special cases, we'll create a hardcoded exhibit
                    val exhibit = when (exhibitId) {
                        1 -> Exhibit(
                            id = 1,
                            name = "Tunnel Introduction Video",
                            shortDescription = "Welcome to The IRE",
                            fullDescription = "An immersive introduction to the world of rugby, setting the stage for your journey through this incredible sport.",
                            imageResourceId = R.drawable.exhibit_tunnel2,
                            floorId = 0
                        )
                        28 -> Exhibit(
                            id = 28,
                            name = "Hall of Fame Digital Archives",
                            shortDescription = "Historic archives",
                            fullDescription = "Digital collection preserving rugby's most legendary figures.",
                            imageResourceId = R.drawable.exhibit_archives,
                            floorId = 6
                        )
                        else -> null
                    }
                    
                    if (exhibit != null) {
                        _exhibit.value = exhibit
                        ErrorHandler.logDebug("ExhibitDetailViewModel: Created special hardcoded exhibit: ${exhibit.name}")
                        return@launch
                    }
                }
                
                // Normal lookup from repository
                ErrorHandler.logDebug("ExhibitDetailViewModel: Trying to get exhibit from repository")
                val exhibit = repository.getExhibit(exhibitId)
                if (exhibit != null) {
                    _exhibit.value = exhibit
                    ErrorHandler.logDebug("ExhibitDetailViewModel: Found exhibit in repository: ${exhibit.name}")
                } else {
                    ErrorHandler.logDebug("ExhibitDetailViewModel: Exhibit not found in repository")
                    
                    // As a fallback, try to get from sample data
                    val allExhibits = SampleDataProvider.getExhibits(context)
                    ErrorHandler.logDebug("ExhibitDetailViewModel: Sample exhibits count: ${allExhibits.size}")
                    
                    // Try to find the exhibit by ID (with zero-based indexing)
                    val index = exhibitId - 1
                    if (index >= 0 && index < allExhibits.size) {
                        val sampleExhibit = allExhibits[index]
                        _exhibit.value = sampleExhibit
                        ErrorHandler.logDebug("ExhibitDetailViewModel: Found exhibit in sample data: ${sampleExhibit.name}")
                    } else {
                        ErrorHandler.logDebug("ExhibitDetailViewModel: Exhibit not found anywhere, creating a fallback exhibit")
                        // Last resort - create a generic exhibit
                        val fallbackExhibit = Exhibit(
                            id = exhibitId,
                            name = "Exhibit #$exhibitId",
                            shortDescription = "Information not available",
                            fullDescription = "This exhibit information could not be found. Please try scanning the QR code again or contact staff for assistance.",
                            imageResourceId = R.drawable.placeholder,
                            floorId = 0
                        )
                        _exhibit.value = fallbackExhibit
                        ErrorHandler.logDebug("ExhibitDetailViewModel: Created fallback exhibit for ID: $exhibitId")
                    }
                }
            } catch (e: Exception) {
                ErrorHandler.logDebug("ExhibitDetailViewModel: Error loading exhibit: ${e.message}")
                e.printStackTrace()
                
                // Create a fallback exhibit on error
                val errorExhibit = Exhibit(
                    id = exhibitId,
                    name = "Error Loading Exhibit",
                    shortDescription = "An error occurred",
                    fullDescription = "There was an error loading this exhibit: ${e.message}. Please try again or contact staff for assistance.",
                    imageResourceId = R.drawable.placeholder,
                    floorId = 0
                )
                _exhibit.value = errorExhibit
                ErrorHandler.logDebug("ExhibitDetailViewModel: Created error exhibit due to exception")
            }
        }
    }
} 
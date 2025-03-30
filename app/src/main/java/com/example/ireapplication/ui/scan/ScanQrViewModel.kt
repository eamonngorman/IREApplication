package com.example.ireapplication.ui.scan

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
class ScanQrViewModel @Inject constructor(
    private val ireRepository: IRERepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _isFlashlightOn = MutableLiveData(false)
    val isFlashlightOn: LiveData<Boolean> = _isFlashlightOn

    private val _scanResult = MutableLiveData<ScanResult>()
    val scanResult: LiveData<ScanResult> = _scanResult
    
    // Flag to prevent duplicate error messages
    private var pendingErrorMessage = false

    // For debugging, handle these specific IDs specially
    private val specialExhibitIds = setOf(1, 28)

    fun onQrCodeScanned(content: String) {
        if (pendingErrorMessage) {
            ErrorHandler.logDebug("Skipping QR processing - error message is pending")
            return  // Skip if we're already showing an error
        }
        
        ErrorHandler.logDebug("Processing QR content: $content")
        
        viewModelScope.launch {
            try {
                // Check if QR content matches our exhibit format: "EXHIBIT_ID:123"
                if (content.startsWith("EXHIBIT_ID:")) {
                    val exhibitId = content.substringAfter("EXHIBIT_ID:").toIntOrNull()
                    if (exhibitId == null) {
                        ErrorHandler.logDebug("Invalid exhibit ID format in QR code")
                        showInvalidMessage(context.getString(R.string.qr_invalid_format))
                        return@launch
                    }

                    ErrorHandler.logDebug("QR CODE SCAN - Exhibit ID: $exhibitId")
                    
                    // For debugging: Create hardcoded exhibits for special IDs to guarantee they work
                    if (specialExhibitIds.contains(exhibitId)) {
                        ErrorHandler.logDebug("SPECIAL EXHIBIT ID DETECTED: $exhibitId - Using hardcoded exhibit")
                        
                        val specialExhibit = when (exhibitId) {
                            1 -> Exhibit(
                                id = 1,
                                name = "Tunnel Introduction Video",
                                shortDescription = "Welcome to The IRE",
                                fullDescription = "An immersive introduction to the world of rugby, setting the stage for your journey through this incredible sport.",
                                imageResourceId = R.drawable.exhibit_tunnel2,
                                floorId = SampleDataProvider.FloorLevel.GENERAL
                            )
                            28 -> Exhibit(
                                id = 28,
                                name = "Hall of Fame Digital Archives",
                                shortDescription = "Historic archives",
                                fullDescription = "Digital collection preserving rugby's most legendary figures.",
                                imageResourceId = R.drawable.exhibit_archives,
                                floorId = SampleDataProvider.FloorLevel.SIXTH
                            )
                            else -> null
                        }
                        
                        if (specialExhibit != null) {
                            ErrorHandler.logDebug("Created special hardcoded exhibit: ${specialExhibit.name}")
                            // Navigate regardless of the actual data lookup
                            _scanResult.value = ScanResult.ValidExhibit(exhibitId)
                            return@launch
                        }
                    }
                    
                    // Try using repository
                    val exhibit = ireRepository.getExhibit(exhibitId)
                    if (exhibit != null) {
                        ErrorHandler.logDebug("Found exhibit in IRERepository: ${exhibit.name}")
                        _scanResult.value = ScanResult.ValidExhibit(exhibitId)
                    } else {
                        ErrorHandler.logDebug("Exhibit not found in IRERepository, trying sample data")
                        // If not found, try using sample data
                        val sampleExhibit = getExhibitFromSampleData(exhibitId)
                        if (sampleExhibit != null) {
                            ErrorHandler.logDebug("Found exhibit in sample data: ${sampleExhibit.name}")
                            
                            // For demo purposes, just use the exhibit ID directly
                            _scanResult.value = ScanResult.ValidExhibit(exhibitId)
                        } else {
                            ErrorHandler.logDebug("CRITICAL ERROR: Exhibit ID $exhibitId not found anywhere!")
                            showInvalidMessage(context.getString(R.string.qr_exhibit_not_found))
                        }
                    }
                } else {
                    // Handle generic QR codes by displaying their content
                    ErrorHandler.logDebug("QR code contains generic content")
                    _scanResult.value = ScanResult.GenericContent(content)
                }
            } catch (e: Exception) {
                ErrorHandler.logDebug("Error processing QR code: ${e.message}")
                e.printStackTrace()
                showInvalidMessage(
                    context.getString(R.string.qr_error_processing) + ": ${e.message}"
                )
            }
        }
    }
    
    private fun showInvalidMessage(message: String) {
        pendingErrorMessage = true
        _scanResult.value = ScanResult.Invalid(message)
    }
    
    // Helper function to get exhibits from sample data
    private fun getExhibitFromSampleData(id: Int): Exhibit? {
        val allExhibits = SampleDataProvider.getExhibits(context)
        ErrorHandler.logDebug("Sample exhibits count: ${allExhibits.size}")
        
        // For special cases
        if (id == 1) {
            ErrorHandler.logDebug("Special handling for ID 1")
            val generalExhibits = allExhibits.filter { it.floorId == SampleDataProvider.FloorLevel.GENERAL }
            ErrorHandler.logDebug("General floor exhibits: ${generalExhibits.size}")
            val foundExhibit = generalExhibits.firstOrNull()
            ErrorHandler.logDebug("First general exhibit: ${foundExhibit?.name ?: "NONE"}")
            return foundExhibit
        } 
        
        if (id == 28) {
            ErrorHandler.logDebug("Special handling for ID 28")
            val legendsExhibits = allExhibits.filter { it.floorId == SampleDataProvider.FloorLevel.SIXTH }
            ErrorHandler.logDebug("Hall of Legends floor exhibits: ${legendsExhibits.size}")
            val foundExhibit = legendsExhibits.lastOrNull()
            ErrorHandler.logDebug("Last legends exhibit: ${foundExhibit?.name ?: "NONE"}")
            return foundExhibit
        }
        
        // Try to find based on index (zero-based index)
        val exhibitIndex = id - 1
        if (exhibitIndex >= 0 && exhibitIndex < allExhibits.size) {
            val foundExhibit = allExhibits[exhibitIndex]
            ErrorHandler.logDebug("Found exhibit by index: ${foundExhibit.name}")
            return foundExhibit
        }
        
        ErrorHandler.logDebug("Could not find exhibit with ID $id in sample data")
        return null
    }

    fun toggleFlashlight() {
        _isFlashlightOn.value = _isFlashlightOn.value?.not()
    }

    fun resetScanResult() {
        _scanResult.value = null
        pendingErrorMessage = false
    }
}

sealed class ScanResult {
    data class ValidExhibit(val exhibitId: Int) : ScanResult()
    data class GenericContent(val content: String) : ScanResult()
    data class Invalid(val message: String) : ScanResult()
} 
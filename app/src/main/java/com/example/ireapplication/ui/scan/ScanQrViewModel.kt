package com.example.ireapplication.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ireapplication.data.repository.ExhibitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanQrViewModel @Inject constructor(
    private val exhibitRepository: ExhibitRepository
) : ViewModel() {
    private val _isFlashlightOn = MutableLiveData(false)
    val isFlashlightOn: LiveData<Boolean> = _isFlashlightOn

    private val _scanResult = MutableLiveData<ScanResult>()
    val scanResult: LiveData<ScanResult> = _scanResult

    fun onQrCodeScanned(content: String) {
        viewModelScope.launch {
            try {
                // Expecting QR content in format: "EXHIBIT_ID:123"
                if (!content.startsWith("EXHIBIT_ID:")) {
                    _scanResult.value = ScanResult.Invalid("Invalid QR code format")
                    return@launch
                }

                val exhibitId = content.substringAfter("EXHIBIT_ID:").toIntOrNull()
                if (exhibitId == null) {
                    _scanResult.value = ScanResult.Invalid("Invalid exhibit ID format")
                    return@launch
                }

                val exhibit = exhibitRepository.getExhibit(exhibitId)
                if (exhibit != null) {
                    _scanResult.value = ScanResult.ValidExhibit(exhibitId)
                } else {
                    _scanResult.value = ScanResult.Invalid("Exhibit not found")
                }
            } catch (e: Exception) {
                _scanResult.value = ScanResult.Invalid("Error processing QR code")
            }
        }
    }

    fun toggleFlashlight() {
        _isFlashlightOn.value = _isFlashlightOn.value?.not()
    }

    fun resetScanResult() {
        _scanResult.value = null
    }
}

sealed class ScanResult {
    data class ValidExhibit(val exhibitId: Int) : ScanResult()
    data class Invalid(val message: String) : ScanResult()
} 
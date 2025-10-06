package com.example.ecolab.feature.quickaction

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class QuickActionUiState(
    val selectedWasteType: String? = null,
    val photoUri: String? = null, // Placeholder for photo URI
    val location: Pair<Double, Double>? = null // Placeholder for Lat/Lng
)

@HiltViewModel
class QuickActionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuickActionUiState())
    val uiState = _uiState.asStateFlow()

    fun onWasteTypeSelected(type: String) {
        _uiState.update { it.copy(selectedWasteType = type) }
    }

    fun onPhotoTaken(uri: String) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun onLocationCaptured(lat: Double, lng: Double) {
        _uiState.update { it.copy(location = lat to lng) }
    }

    fun submit() {
        // TODO: Implement submission logic (e.g., save to repository)
        println("Submitting new collection point: ${uiState.value}")
    }
}

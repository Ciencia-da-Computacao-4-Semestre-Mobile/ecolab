package com.example.ecolab.feature.quickaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.data.repository.CollectionPointRepository
import com.example.ecolab.data.repository.UserProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuickActionUiState(
    val selectedWasteType: String? = null,
    val photoUri: String? = null,
    val location: Pair<Double, Double>? = null
)

sealed interface QuickActionEvent {
    object SubmissionSuccess : QuickActionEvent
}

@HiltViewModel
class QuickActionViewModel @Inject constructor(
    private val collectionPointRepository: CollectionPointRepository,
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickActionUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = MutableSharedFlow<QuickActionEvent>()
    val eventChannel = _eventChannel.asSharedFlow()

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
        viewModelScope.launch {
            val currentState = uiState.value
            val wasteType = currentState.selectedWasteType
            val photoUri = currentState.photoUri
            val location = currentState.location

            if (wasteType != null && photoUri != null && location != null) {
                val newPoint = CollectionPoint(
                    name = "Novo Ponto de Coleta",
                    address = "Endereço a ser preenchido",
                    wasteType = wasteType,
                    photoUri = photoUri,
                    latitude = location.first,
                    longitude = location.second,
                    userSubmitted = true
                )
                collectionPointRepository.addPoint(newPoint)

                // Complete the relevant mission
                userProgressRepository.completeMission(wasteType)

                _eventChannel.emit(QuickActionEvent.SubmissionSuccess)
            } else {
                println("Error: Cannot submit with incomplete data.")
            }
        }
    }
}

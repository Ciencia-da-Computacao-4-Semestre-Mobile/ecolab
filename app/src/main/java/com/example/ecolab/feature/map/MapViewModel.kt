package com.example.ecolab.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.data.repository.CollectionPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MapUiState(
    val points: List<CollectionPoint> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: CollectionPointRepository
) : ViewModel() {

    val uiState: StateFlow<MapUiState> = repository.getAllPoints()
        .map { points -> MapUiState(points = points, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MapUiState(isLoading = true)
        )
}

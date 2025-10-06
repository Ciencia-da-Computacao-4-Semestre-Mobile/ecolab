package com.example.ecolab.presentation.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.ecolab.data.geojson.CollectionPoint
import com.example.ecolab.data.geojson.CollectionType
import com.example.ecolab.data.geojson.GeoJsonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapUiState(
    val centerLat: Double = -23.5505,
    val centerLng: Double = -46.6333,
    val markers: List<CollectionPoint> = emptyList(),
    val selected: Set<CollectionType> = CollectionType.values().toSet()
)

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        val points = GeoJsonRepository.loadCollectionPoints(context)
        _uiState.value = _uiState.value.copy(markers = points)
    }

    fun toggleType(type: CollectionType) {
        val sel = _uiState.value.selected.toMutableSet()
        if (sel.contains(type)) sel.remove(type) else sel.add(type)
        _uiState.value = _uiState.value.copy(selected = sel)
    }
}
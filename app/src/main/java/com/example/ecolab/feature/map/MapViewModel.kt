package com.example.ecolab.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val pointsRepository: PointsRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Todos")

    val uiState: StateFlow<MapUiState> = combine(
        pointsRepository.observePoints(),
        _selectedCategory
    ) { allPoints, selectedCategory ->
        val filteredPoints = if (selectedCategory == "Todos") {
            allPoints
        } else {
            allPoints.filter { it.category == selectedCategory }
        }
        MapUiState(collectionPoints = filteredPoints, selectedCategory = selectedCategory)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapUiState()
    )

    fun onFilterChange(category: String) {
        _selectedCategory.value = category
    }

    fun refresh() {
        viewModelScope.launch {
            pointsRepository.refresh()
        }
    }
}

data class MapUiState(
    val collectionPoints: List<CollectionPoint> = emptyList(),
    val selectedCategory: String = "Todos"
)

package com.example.ecolab.feature.map

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.data.repository.FirestorePointsRepository
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.android.gms.maps.model.LatLng
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
    private val pointsRepository: PointsRepository,
    private val geocoder: Geocoder
) : ViewModel() {

    init {
        viewModelScope.launch {
            (pointsRepository as? FirestorePointsRepository)?.migratePointsToFirestore()
        }
    }

    private val _selectedCategory = MutableStateFlow("Todos")
    private val _selectedPoint = MutableStateFlow<CollectionPoint?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _showFavorites = MutableStateFlow(false)
    private val _searchedLocation = MutableStateFlow<LatLng?>(null)

    private val _filteredPoints = combine(
        pointsRepository.observePoints(),
        _selectedCategory,
        _searchQuery,
        _showFavorites
    ) { allPoints, selectedCategory, searchQuery, showFavorites ->
        val filteredPointsByCategory = if (selectedCategory == "Todos") {
            allPoints
        } else {
            allPoints.filter { it.category == selectedCategory }
        }

        val filteredPointsBySearch = if (searchQuery.isBlank()) {
            filteredPointsByCategory
        } else {
            filteredPointsByCategory.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        if (showFavorites) {
            filteredPointsBySearch.filter { it.isFavorite }
        } else {
            filteredPointsBySearch
        }
    }

    val uiState: StateFlow<MapUiState> = combine(
        _filteredPoints,
        _selectedPoint,
        _searchQuery,
        _showFavorites,
        _searchedLocation
    ) { filteredPoints, selectedPoint, searchQuery, showFavorites, searchedLocation ->
        MapUiState(
            collectionPoints = filteredPoints,
            selectedCategory = _selectedCategory.value,
            selectedPoint = selectedPoint,
            searchQuery = searchQuery,
            showFavorites = showFavorites,
            searchedLocation = searchedLocation
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapUiState()
    )

    fun onFilterChange(category: String) {
        _selectedCategory.value = category
    }

    fun onMarkerClick(point: CollectionPoint) {
        _selectedPoint.value = point
    }

    fun onDismissBottomSheet() {
        _selectedPoint.value = null
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearch() {
        viewModelScope.launch {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(
                        _searchQuery.value,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: List<Address>) {
                                if (addresses.isNotEmpty()) {
                                    val address = addresses[0]
                                    _searchedLocation.value = LatLng(address.latitude, address.longitude)
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                // Handle error
                            }
                        })
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(_searchQuery.value, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        _searchedLocation.value = LatLng(address.latitude, address.longitude)
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun onToggleFavorites(isFavorite: Boolean) {
        _showFavorites.value = isFavorite
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _selectedPoint.value?.let { point ->
                val updatedPoint = point.copy(isFavorite = !point.isFavorite)
                _selectedPoint.value = updatedPoint
                pointsRepository.toggleFavorite(point.id)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            pointsRepository.refresh()
        }
    }
}

data class MapUiState(
    val collectionPoints: List<CollectionPoint> = emptyList(),
    val selectedCategory: String = "Todos",
    val selectedPoint: CollectionPoint? = null,
    val searchQuery: String = "",
    val showFavorites: Boolean = false,
    val searchedLocation: LatLng? = null
)

package com.example.ecolab.feature.map

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _selectedCategory = MutableStateFlow("Todos")
    private val _selectedPoint = MutableStateFlow<CollectionPoint?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _showFavorites = MutableStateFlow(false)
    private val _searchedLocation = MutableStateFlow<LatLng?>(null)
    private val _localFavoriteOverrides = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    private val _currentFilteredPoints = MutableStateFlow<List<CollectionPoint>>(emptyList())
    
    private val _filteredPoints = combine(
        pointsRepository.observePoints(),
        _selectedCategory,
        _searchQuery,
        _showFavorites,
        _localFavoriteOverrides
    ) { allPoints, selectedCategory, searchQuery, showFavorites, overrides ->
        Log.d("MapViewModel", "Starting filter process:")
        Log.d("MapViewModel", "- Total points: ${allPoints.size}")
        Log.d("MapViewModel", "- Selected category: $selectedCategory")
        Log.d("MapViewModel", "- Search query: '$searchQuery'")
        Log.d("MapViewModel", "- Show favorites: $showFavorites")
        Log.d("MapViewModel", "- Points with isFavorite=true: ${allPoints.count { it.isFavorite }}")
        
        val filteredPointsByCategory = if (selectedCategory == "Todos") {
            allPoints
        } else {
            allPoints.filter { it.category == selectedCategory }
        }
        Log.d("MapViewModel", "After category filter: ${filteredPointsByCategory.size} points")

        val filteredPointsBySearch = if (searchQuery.isBlank()) {
            filteredPointsByCategory
        } else {
            filteredPointsByCategory.filter { 
                val matches = it.name.contains(searchQuery, ignoreCase = true) ||
                              it.description.contains(searchQuery, ignoreCase = true)
                Log.d("MapViewModel", "Point ${it.name} matches search '$searchQuery': $matches")
                matches
            }
        }
        Log.d("MapViewModel", "After search filter: ${filteredPointsBySearch.size} points")

        val finalFilteredPoints = if (showFavorites) {
            Log.d("MapViewModel", "Applying favorites filter...")
            val favoritePoints = filteredPointsBySearch.filter { 
                val isFavorite = overrides[it.id] ?: it.isFavorite
                Log.d("MapViewModel", "Point ${it.name} (id: ${it.id}) - isFavorite: $isFavorite")
                isFavorite
            }
            Log.d("MapViewModel", "After favorites filter: ${favoritePoints.size} points")
            favoritePoints
        } else {
            filteredPointsBySearch
        }
        val merged = finalFilteredPoints.map { p ->
            val o = overrides[p.id]
            if (o != null && o != p.isFavorite) p.copy(isFavorite = o) else p
        }
        val toRemove = merged.filter { m -> overrides[m.id] == m.isFavorite }.map { it.id }
        if (toRemove.isNotEmpty()) {
            val newMap = overrides.toMutableMap()
            toRemove.forEach { newMap.remove(it) }
            _localFavoriteOverrides.value = newMap
        }
        _currentFilteredPoints.value = merged
        Log.d("MapViewModel", "Final result: ${merged.size} points after all filters")
        merged
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
        Log.d("MapViewModel", "onToggleFavorites called: $isFavorite")
        _showFavorites.value = isFavorite
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _selectedPoint.value?.let { point ->
                Log.d("MapViewModel", "toggleFavorite called for point: ${point.name} (id: ${point.id}), current favorite: ${point.isFavorite}")
                val updatedPoint = point.copy(isFavorite = !point.isFavorite)
                _selectedPoint.value = updatedPoint
                _localFavoriteOverrides.value = _localFavoriteOverrides.value.toMutableMap().apply { put(point.id, updatedPoint.isFavorite) }
                pointsRepository.toggleFavorite(point.id)
                
                // Atualizar também o ponto na lista de pontos filtrados
                // Isso garante que o estado seja consistente
                val currentPoints = _currentFilteredPoints.value
                val updatedPoints = currentPoints.map { 
                    if (it.id == point.id) updatedPoint else it 
                }
                _currentFilteredPoints.value = updatedPoints
                Log.d("MapViewModel", "Updated point favorite status to: ${updatedPoint.isFavorite}")
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
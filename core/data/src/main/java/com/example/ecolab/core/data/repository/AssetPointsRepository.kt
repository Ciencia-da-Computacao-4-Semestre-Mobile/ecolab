package com.example.ecolab.core.data.repository

import com.example.ecolab.core.data.prepopulation.GeoJsonParser
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetPointsRepository @Inject constructor(
    private val geoJsonParser: GeoJsonParser
) : PointsRepository {

    private val _points = MutableStateFlow<List<CollectionPoint>>(emptyList())

    init {
        _points.value = geoJsonParser.parse().mapIndexed { index, point ->
            point.copy(id = index.toLong()) // Assign a unique ID
        }
    }

    override fun observePoints(): Flow<List<CollectionPoint>> = _points.asStateFlow()

    override suspend fun toggleFavorite(id: Long) {
        _points.update { currentPoints ->
            currentPoints.map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }

    override suspend fun refresh() {
        // For now, this does nothing, but could be used to re-parse the assets
        // if they could change during the app's lifecycle.
    }
}

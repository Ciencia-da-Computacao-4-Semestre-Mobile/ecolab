package com.example.ecolab.data.repository

import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.domain.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of the PointsRepository for development and preview purposes.
 * Provides a hardcoded list of collection points in São Paulo.
 */
@Singleton
class MockPointsRepository @Inject constructor() : PointsRepository {

    private val _points = MutableStateFlow(listOf(
        CollectionPoint(
            id = 1,
            name = "Ecoponto Sé",
            description = "Ponto de descarte para múltiplos materiais.",
            latitude = -23.5505,
            longitude = -46.6333,
            category = "Multi"
        ),
        CollectionPoint(
            id = 2,
            name = "Coleta de Plástico Pinheiros",
            description = "Especializado em plásticos e PET.",
            latitude = -23.5674,
            longitude = -46.6974,
            category = "Plástico"
        ),
        CollectionPoint(
            id = 3,
            name = "Reciclagem de Papel na Paulista",
            description = "Ponto para descarte de papel e papelão.",
            latitude = -23.5613,
            longitude = -46.6565,
            category = "Papel"
        )
    ))

    override fun observePoints(): Flow<List<CollectionPoint>> = _points.asStateFlow()

    override suspend fun toggleFavorite(id: Long) {
        _points.update { currentPoints ->
            currentPoints.map {
                if (it.id == id) {
                    it.copy(isFavorite = !it.isFavorite)
                } else {
                    it
                }
            }
        }
    }

    override suspend fun refresh() {
        // No-op for the mock implementation, as requested.
    }
}

package com.example.ecolab.core.data.repository

import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockPointsRepository @Inject constructor() : PointsRepository {

    private val _points = MutableStateFlow(
        listOf(
            CollectionPoint(1, "Ecoponto Central", "Multi-materiais", -23.55, -46.63, "Ecoponto", null, null, false),
            CollectionPoint(2, "Cooperativa Verde", "Plástico e Papel", -23.56, -46.64, "Cooperativa", null, null, true),
            CollectionPoint(3, "Ponto de Descarte Rápido", "Vidro", -23.54, -46.65, "Ponto de Entrega", null, null, false)
        )
    )

    override fun observePoints(): Flow<List<CollectionPoint>> = _points.asStateFlow()

    override suspend fun toggleFavorite(id: Long) {
        _points.update { currentPoints ->
            currentPoints.map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }

    override suspend fun refresh() {
        // No-op for mock repository
    }
}

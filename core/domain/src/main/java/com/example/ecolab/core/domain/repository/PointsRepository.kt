package com.example.ecolab.core.domain.repository

import com.example.ecolab.core.domain.model.CollectionPoint
import kotlinx.coroutines.flow.Flow

interface PointsRepository {
    fun observePoints(): Flow<List<CollectionPoint>>
    suspend fun toggleFavorite(id: Long)
    suspend fun refresh()
}

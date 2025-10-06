package com.example.ecolab.domain

import com.example.ecolab.data.model.CollectionPoint
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the collection points data repository, as per the design prompt.
 * This defines the contract for data operations related to collection points.
 */
interface PointsRepository {
    /**
     * Observes the list of all collection points.
     */
    fun observePoints(): Flow<List<CollectionPoint>>

    /**
     * Toggles the favorite status of a specific point.
     */
    suspend fun toggleFavorite(id: Long)

    /**
     * Triggers a refresh of the points data (no-op for mock).
     */
    suspend fun refresh()
}

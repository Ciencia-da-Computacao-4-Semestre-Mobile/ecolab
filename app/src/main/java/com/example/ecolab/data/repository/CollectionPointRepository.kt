package com.example.ecolab.data.repository

import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.data.model.CollectionPointDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionPointRepository @Inject constructor(
    private val collectionPointDao: CollectionPointDao
) {

    suspend fun addPoint(point: CollectionPoint) {
        collectionPointDao.insert(point)
    }

    fun getAllPoints(): Flow<List<CollectionPoint>> = collectionPointDao.getAllPoints()
}

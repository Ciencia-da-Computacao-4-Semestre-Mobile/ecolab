package com.example.ecolab.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: CollectionPoint)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<CollectionPoint>)

    @Query("SELECT * FROM collection_points ORDER BY id DESC")
    fun getAllPoints(): Flow<List<CollectionPoint>>
}

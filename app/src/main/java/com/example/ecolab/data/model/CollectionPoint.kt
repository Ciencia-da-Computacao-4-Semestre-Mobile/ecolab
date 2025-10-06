package com.example.ecolab.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_points")
data class CollectionPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val openingHours: String? = null,
    val wasteType: String,
    val photoUri: String,
    val latitude: Double,
    val longitude: Double,
    val userSubmitted: Boolean = true
)

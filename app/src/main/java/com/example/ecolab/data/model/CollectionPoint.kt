package com.example.ecolab.data.model

/**
 * Data class representing a collection point, as defined in the project prompt.
 */
data class CollectionPoint(
    val id: Long,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    var isFavorite: Boolean = false
)

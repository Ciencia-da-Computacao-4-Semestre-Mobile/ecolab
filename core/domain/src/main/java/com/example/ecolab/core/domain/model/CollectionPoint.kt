package com.example.ecolab.core.domain.model

data class CollectionPoint(
    val id: Long,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val isFavorite: Boolean = false
)

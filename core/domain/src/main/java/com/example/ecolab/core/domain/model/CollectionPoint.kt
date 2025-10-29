package com.example.ecolab.core.domain.model

data class CollectionPoint(
    val id: Long = 0L,
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "",
    val openingHours: String? = null,
    val materials: String? = null,
    val isFavorite: Boolean = false
)
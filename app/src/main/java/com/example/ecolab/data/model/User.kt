package com.example.ecolab.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val favoritedPoints: List<String> = emptyList(),
    val unlockedAchievements: List<String> = emptyList()
)
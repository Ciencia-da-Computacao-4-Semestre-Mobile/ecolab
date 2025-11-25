package com.example.ecolab.data.model

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val totalPoints: Int = 0,
    val favoritedPoints: List<String> = emptyList(),
    val unlockedAchievements: List<String> = emptyList(),
    @get:PropertyName("equippedItems")
    val equippedItemsMap: Map<String, String> = emptyMap(),
    val purchasedItems: List<String> = emptyList()
)

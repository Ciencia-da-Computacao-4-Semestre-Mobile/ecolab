package com.example.ecolab.model

data class StoreItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val category: StoreCategory,
    val rarity: Rarity,
    val iconRes: String, // Nome do ícone/avatar
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false
)

enum class StoreCategory {
    AVATAR,
    BADGE,
    THEME,
    EFFECT
}

enum class Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY
}

enum class AvatarStyle {
    NATURE,
    TECH,
    ANIMAL,
    MYSTIC,
    ELEMENTAL
}
package com.example.ecolab.data.model

/**
 * Represents a single achievement or milestone the user can attain.
 *
 * @param id A unique identifier for the achievement.
 * @param title The name of the achievement, e.g., "Pioneiro do Plástico".
 * @param description A short explanation of how to unlock it.
 * @param isUnlocked Whether the user has completed this achievement.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false
)

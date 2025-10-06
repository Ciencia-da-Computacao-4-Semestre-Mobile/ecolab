package com.example.ecolab.data.model

/**
 * Represents a user within the ranking list.
 *
 * @param position The user's rank (e.g., 1, 2, 3).
 * @param name The user's display name.
 * @param score The user's total score or number of contributions.
 * @param isCurrentUser A flag to highlight the logged-in user in the list.
 */
data class RankedUser(
    val position: Int,
    val name: String,
    val score: Int,
    val isCurrentUser: Boolean = false
)

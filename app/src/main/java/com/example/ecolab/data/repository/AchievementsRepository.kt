package com.example.ecolab.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

data class AchievementsProgress(val progress: Float, val progressText: String, val completed: Boolean)

@Singleton
class AchievementsRepository @Inject constructor() {
    // Dummy implementation
    fun getAchievementsProgress(): Flow<AchievementsProgress> {
        val progress = 0.75f
        return flowOf(
            AchievementsProgress(
                progress = progress,
                progressText = "75%",
                completed = progress >= 1.0f
            )
        )
    }
}

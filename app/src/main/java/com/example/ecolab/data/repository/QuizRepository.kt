package com.example.ecolab.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

data class QuizProgress(val progress: Float, val progressText: String, val completed: Boolean)

@Singleton
class QuizRepository @Inject constructor() {
    // Dummy implementation
    fun getQuizProgress(): Flow<QuizProgress> {
        val progress = 0.4f
        return flowOf(
            QuizProgress(
                progress = progress,
                progressText = "2/5",
                completed = progress >= 1.0f
            )
        )
    }
}

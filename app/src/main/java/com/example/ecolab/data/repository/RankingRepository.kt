package com.example.ecolab.data.repository

import com.example.ecolab.data.model.RankedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for fetching ranking data.
 * This is a mock implementation for now.
 */
@Singleton
class RankingRepository @Inject constructor() {

    /**
     * Fetches a fake list of ranked users.
     * Simulates a network delay.
     */
    suspend fun getRanking(): List<RankedUser> = withContext(Dispatchers.IO) {
        delay(1000) // Simulate network latency
        return@withContext listOf(
            RankedUser(position = 1, name = "Maria S.", score = 1520),
            RankedUser(position = 2, name = "João P.", score = 1450),
            RankedUser(position = 3, name = "Você", score = 1390, isCurrentUser = true),
            RankedUser(position = 4, name = "Carlos A.", score = 1320),
            RankedUser(position = 5, name = "Ana L.", score = 1280),
            RankedUser(position = 6, name = "Pedro H.", score = 1100),
            RankedUser(position = 7, name = "Sofia R.", score = 980),
            RankedUser(position = 8, name = "Lucas M.", score = 950),
            RankedUser(position = 9, name = "Beatriz C.", score = 910),
            RankedUser(position = 10, name = "Gabriel F.", score = 880)
        )
    }
}

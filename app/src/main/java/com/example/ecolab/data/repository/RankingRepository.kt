package com.example.ecolab.data.repository

import com.example.ecolab.data.model.RankedUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for fetching ranking data.
 * This is a mock implementation for now.
 */
@Singleton
class RankingRepository @Inject constructor() {

    /**
     * A flow that emits a fake list of ranked users.
     * Simulates a network delay.
     */
    val ranking: Flow<List<RankedUser>> = flow {
        delay(1000) // Simulate network latency
        emit(listOf(
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
        ))
    }.flowOn(Dispatchers.IO)
}

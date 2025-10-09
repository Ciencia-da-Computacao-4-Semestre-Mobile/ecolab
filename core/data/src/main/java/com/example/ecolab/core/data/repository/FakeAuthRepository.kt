package com.example.ecolab.core.data.repository

import com.example.ecolab.core.domain.model.AuthUser
import com.example.ecolab.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {
    override fun getAuthState(): Flow<Boolean> {
        return flowOf(true)
    }

    override fun getCurrentUser(): AuthUser? {
        return AuthUser("123", "test@example.com")
    }

    override suspend fun signOut() {
        // No-op
    }
}

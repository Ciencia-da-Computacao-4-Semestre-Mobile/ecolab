package com.example.ecolab.core.data.repository

import com.example.ecolab.core.domain.model.AuthUser
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {
    override fun getAuthState(): Flow<Boolean> {
        return flowOf(true)
    }

    override fun getCurrentUser(): AuthUser? {
        return AuthUser("123", "test@example.com")
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<AuthUser> {
        return Result.Success(AuthUser("123", email))
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<AuthUser> {
        return Result.Success(AuthUser("123", email))
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        return Result.Success(AuthUser("123", "google_user@example.com"))
    }

    override suspend fun signOut() {
        // No-op
    }
}

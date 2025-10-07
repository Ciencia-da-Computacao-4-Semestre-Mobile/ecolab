package com.example.ecolab.core.domain.repository

import com.example.ecolab.core.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<Boolean>
    fun getCurrentUser(): AuthUser?
    suspend fun signOut()
}

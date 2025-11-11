package com.example.ecolab.core.domain.repository

import android.content.Intent
import com.example.ecolab.core.domain.model.AuthUser
import com.example.ecolab.core.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<Boolean>
    fun getCurrentUser(): AuthUser?
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<AuthUser>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<AuthUser>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut()
}
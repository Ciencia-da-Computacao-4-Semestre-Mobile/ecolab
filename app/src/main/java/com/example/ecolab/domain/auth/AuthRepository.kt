package com.example.ecolab.domain.auth

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
}
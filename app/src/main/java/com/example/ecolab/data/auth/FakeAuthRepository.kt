package com.example.ecolab.data.auth

import com.example.ecolab.domain.auth.AuthRepository

class FakeAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        // Simula sucesso quando há valores não vazios
        return email.isNotBlank() && password.length >= 3
    }
}
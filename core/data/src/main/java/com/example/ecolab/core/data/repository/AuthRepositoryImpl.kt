package com.example.ecolab.core.data.repository

import com.example.ecolab.core.domain.model.AuthUser
import com.example.ecolab.core.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override fun getCurrentUser(): AuthUser? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let { AuthUser(it.uid, it.email) }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}

package com.example.ecolab.core.data.repository

import android.util.Log
import com.example.ecolab.core.domain.model.AuthUser
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    override fun getCurrentUser(): AuthUser? {
        return firebaseAuth.currentUser?.toAuthUser()
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<AuthUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!!.toAuthUser()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<AuthUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            saveUserProfileToDatabase(user)
            Result.Success(user.toAuthUser())
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error creating user with email and password", e)
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user!!
            if (result.additionalUserInfo?.isNewUser == true) {
                saveUserProfileToDatabase(user)
            }
            Result.Success(user.toAuthUser())
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error signing in with Google", e)
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toAuthUser(): AuthUser {
        return AuthUser(
            uid = uid,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl?.toString()
        )
    }

    private suspend fun saveUserProfileToDatabase(user: FirebaseUser) {
        val userUID = user.uid

        val profileData = mapOf(
            "name" to (user.displayName ?: "Novo Usuário"),
            "email" to user.email,
            "created_at" to System.currentTimeMillis(),
            "is_active" to true,
            "favoritePoints" to emptyList<String>()
        )

        val userDocRef = firestore.collection("users").document(userUID)

        try {
            userDocRef.set(profileData).await()
            Log.d("AuthRepositoryImpl", "Sucesso: Perfil do usuário gravado no Firestore.")
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Erro ao gravar perfil no Firestore:", e)
        }
    }
}

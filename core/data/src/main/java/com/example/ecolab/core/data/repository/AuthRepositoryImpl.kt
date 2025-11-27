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
        Log.d("AuthRepositoryImpl", "Starting Google sign-in with idToken: ${idToken.take(10)}...")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d("AuthRepositoryImpl", "Google credential created successfully")
            
            val result = firebaseAuth.signInWithCredential(credential).await()
            Log.d("AuthRepositoryImpl", "Firebase sign-in with credential completed")
            
            val user = result.user!!
            Log.d("AuthRepositoryImpl", "User signed in: ${user.uid}, email: ${user.email}")
            
            if (result.additionalUserInfo?.isNewUser == true) {
                Log.d("AuthRepositoryImpl", "New user detected, saving profile to database")
                saveUserProfileToDatabase(user)
            } else {
                Log.d("AuthRepositoryImpl", "Existing user, skipping profile creation")
            }
            
            Result.Success(user.toAuthUser())
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error signing in with Google", e)
            Log.e("AuthRepositoryImpl", "Exception type: ${e.javaClass.simpleName}")
            Log.e("AuthRepositoryImpl", "Exception message: ${e.message}")
            
            // Verificar se é o erro 12500 específico
            if (e.message?.contains("12500") == true) {
                Log.e("AuthRepositoryImpl", "ERROR 12500 DETECTED: This typically indicates an OAuth consent screen or configuration issue")
            }
            
            Result.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.useAppLanguage()
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error sending password reset email", e)
            Result.Error(e.message ?: "Erro ao enviar email de recuperação")
        }
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

        val profileData = mutableMapOf(
            "name" to (user.displayName ?: "Novo Usuário"),
            "email" to user.email,
            "created_at" to System.currentTimeMillis(),
            "is_active" to true,
            "favoritePoints" to emptyList<String>()
        )

        user.photoUrl?.let {
            profileData["photoUrl"] = it.toString()
        }

        val userDocRef = firestore.collection("users").document(userUID)

        try {
            userDocRef.set(profileData).await()
            Log.d("AuthRepositoryImpl", "Sucesso: Perfil do usuário gravado no Firestore.")
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Erro ao gravar perfil no Firestore:", e)
        }
    }
}
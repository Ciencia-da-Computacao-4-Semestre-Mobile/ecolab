package com.example.ecolab.data.repository

import com.example.ecolab.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun getUser(userId: String): User? {
        val document = usersCollection.document(userId).get().await()
        return document.toObject(User::class.java)
    }

    suspend fun addFavoritePoint(userId: String, pointId: String) {
        val user = getUser(userId)
        user?.let {
            val updatedFavorites = it.favoritedPoints.toMutableList().apply { add(pointId) }
            usersCollection.document(userId).update("favoritedPoints", updatedFavorites).await()
        }
    }

    suspend fun removeFavoritePoint(userId: String, pointId: String) {
        val user = getUser(userId)
        user?.let {
            val updatedFavorites = it.favoritedPoints.toMutableList().apply { remove(pointId) }
            usersCollection.document(userId).update("favoritedPoints", updatedFavorites).await()
        }
    }

    suspend fun unlockAchievement(userId: String, achievementId: String) {
        val user = getUser(userId)
        user?.let {
            val updatedAchievements = it.unlockedAchievements.toMutableList().apply { add(achievementId) }
            usersCollection.document(userId).update("unlockedAchievements", updatedAchievements).await()
        }
    }
}
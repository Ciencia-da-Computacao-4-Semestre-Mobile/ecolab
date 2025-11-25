package com.example.ecolab.data.repository

import android.util.Log
import com.example.ecolab.data.model.User
import com.example.ecolab.model.StoreCategory
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User) {
        Log.d("UserRepository", "=== INICIANDO CRIAÇÃO DE USUÁRIO ===")
        if (user.id.isBlank()) {
            throw IllegalArgumentException("ID do usuário não pode estar vazio")
        }
        try {
            usersCollection.document(user.id).set(user).await()
            Log.d("UserRepository", "✅ USUÁRIO CRIADO COM SUCESSO: ${user.id}")
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ ERRO AO CRIAR USUÁRIO NO FIRESTORE", e)
            throw e
        }
    }

    fun getUserFlow(userId: String): Flow<User?> = callbackFlow {
        val listenerRegistration = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<User>()).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getUser(userId: String): User? {
        val document = usersCollection.document(userId).get().await()
        return document.toObject<User>()
    }

    suspend fun addFavoritePoint(userId: String, pointId: String) {
        usersCollection.document(userId).update("favoritedPoints", FieldValue.arrayUnion(pointId)).await()
    }

    suspend fun removeFavoritePoint(userId: String, pointId: String) {
        usersCollection.document(userId).update("favoritedPoints", FieldValue.arrayRemove(pointId)).await()
    }

    suspend fun unlockAchievement(userId: String, achievementId: String) {
        usersCollection.document(userId).update("unlockedAchievements", FieldValue.arrayUnion(achievementId)).await()
    }

    suspend fun updateEquippedItem(userId: String, category: StoreCategory, itemId: String) {
        val field = "equippedItemsMap.${category.name}"
        usersCollection.document(userId).update(field, itemId).await()
    }

    suspend fun addPurchasedItem(userId: String, itemId: String) {
        usersCollection.document(userId).update("purchasedItems", FieldValue.arrayUnion(itemId)).await()
    }
}

package com.example.ecolab.core.data.repository

import android.util.Log
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePointsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PointsRepository {

    override fun observePoints(): Flow<List<CollectionPoint>> = callbackFlow {
        val listener = firestore.collection("collection_points")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebasePointsRepo", "Listen failed.", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val points = snapshot.toObjects<CollectionPoint>()
                    trySend(points).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun toggleFavorite(id: Long) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userDocRef = firestore.collection("users").document(userId)

        try {
            val userDoc = userDocRef.get().await()
            val favorites = userDoc.get("favoritePoints") as? List<*>
            val isCurrentlyFavorite = favorites?.contains(id.toString()) ?: false

            if (isCurrentlyFavorite) {
                userDocRef.update("favoritePoints", FieldValue.arrayRemove(id.toString())).await()
            } else {
                userDocRef.update("favoritePoints", FieldValue.arrayUnion(id.toString())).await()
            }
        } catch (e: Exception) {
            Log.e("FirebasePointsRepo", "Error toggling favorite. Ensure user document exists.", e)
            // If the document doesn't exist, we might want to create it.
            // For simplicity, we'll just log the error for now.
        }
    }

    override suspend fun refresh() {
        // No-op as Firestore provides real-time updates.
    }
}

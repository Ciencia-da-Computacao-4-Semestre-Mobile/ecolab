package com.example.ecolab.core.data.repository

import android.util.Log
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePointsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PointsRepository {
    private val favoriteIds = kotlinx.coroutines.flow.MutableStateFlow<Set<String>>(emptySet())

    override fun observePoints(): Flow<List<CollectionPoint>> = callbackFlow {
        val userId = firebaseAuth.currentUser?.uid
        Log.d("FirebasePointsRepo", "observePoints called, userId: $userId")

        var lastPoints: List<CollectionPoint> = emptyList()

        val pointsListener = firestore.collection("collection_points")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebasePointsRepo", "Listen failed.", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val points = snapshot.toObjects(CollectionPoint::class.java)
                    lastPoints = points
                    Log.d("FirebasePointsRepo", "Loaded ${points.size} points from Firestore")

                    val favIds = favoriteIds.value
                    val updatedPoints = points.map { point ->
                        val isFavorite = favIds.contains(point.id.toString())
                        point.copy(isFavorite = isFavorite)
                    }
                    trySend(updatedPoints).isSuccess
                }
            }

        var userListener: com.google.firebase.firestore.ListenerRegistration? = null
        if (userId != null) {
            userListener = firestore.collection("users")
                .document(userId)
                .addSnapshotListener { document, error ->
                    if (error != null) {
                        Log.e("FirebasePointsRepo", "Failed to listen user favorites", error)
                        return@addSnapshotListener
                    }
                    val favoritePointIds = document?.get("favoritePoints") as? List<String> ?: emptyList()
                    favoriteIds.value = favoritePointIds.toSet()
                    if (lastPoints.isNotEmpty()) {
                        val updatedPoints = lastPoints.map { point ->
                            val isFavorite = favoriteIds.value.contains(point.id.toString())
                            point.copy(isFavorite = isFavorite)
                        }
                        trySend(updatedPoints).isSuccess
                    }
                }
        }

        awaitClose {
            pointsListener.remove()
            userListener?.remove()
        }
    }

    override suspend fun toggleFavorite(id: Long) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        Log.d("FirebasePointsRepo", "toggleFavorite called for point id: $id, userId: $userId")
        val userDocRef = firestore.collection("users").document(userId)

        try {
            val userDoc = userDocRef.get().await()
            val favorites = userDoc.get("favoritePoints") as? List<*>
            val isCurrentlyFavorite = favorites?.contains(id.toString()) ?: false
            val idStr = id.toString()

            // Otimista: atualiza cache local imediatamente
            favoriteIds.value = if (isCurrentlyFavorite) favoriteIds.value - idStr else favoriteIds.value + idStr

            if (isCurrentlyFavorite) {
                userDocRef.update("favoritePoints", FieldValue.arrayRemove(idStr)).await()
            } else {
                userDocRef.update("favoritePoints", FieldValue.arrayUnion(idStr)).await()
            }
        } catch (e: Exception) {
            Log.e("FirebasePointsRepo", "Error toggling favorite. Ensure user document exists.", e)
            try {
                val newFavorites = listOf(id.toString())
                userDocRef.set(hashMapOf("favoritePoints" to newFavorites)).await()
                favoriteIds.value = favoriteIds.value + id.toString()
            } catch (createError: Exception) {
                Log.e("FirebasePointsRepo", "Error creating user document", createError)
            }
        }
    }

    override suspend fun refresh() {
        // No-op as Firestore provides real-time updates.
    }
}
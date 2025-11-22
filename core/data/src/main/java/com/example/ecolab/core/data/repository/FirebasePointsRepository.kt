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

    override fun observePoints(): Flow<List<CollectionPoint>> = callbackFlow {
        val userId = firebaseAuth.currentUser?.uid
        Log.d("FirebasePointsRepo", "observePoints called, userId: $userId")
        
        val listener = firestore.collection("collection_points")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebasePointsRepo", "Listen failed.", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val points = snapshot.toObjects(CollectionPoint::class.java)
                    Log.d("FirebasePointsRepo", "Loaded ${points.size} points from Firestore")
                    
                    // Verificar se há usuário logado e carregar favoritos
                    if (userId != null) {
                        // Carregar favoritos do usuário
                        firestore.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val favoritePointIds = document.get("favoritePoints") as? List<String> ?: emptyList()
                                Log.d("FirebasePointsRepo", "User has ${favoritePointIds.size} favorite points: $favoritePointIds")
                                
                                // Atualizar o campo isFavorite de cada ponto
                                val updatedPoints = points.map { point ->
                                    val isFavorite = favoritePointIds.contains(point.id.toString())
                                    Log.d("FirebasePointsRepo", "Point ${point.name} (id: ${point.id}) - isFavorite: $isFavorite")
                                    point.copy(isFavorite = isFavorite)
                                }
                                
                                val favoriteCount = updatedPoints.count { it.isFavorite }
                                Log.d("FirebasePointsRepo", "Updated ${updatedPoints.size} points, $favoriteCount marked as favorites")
                                trySend(updatedPoints).isSuccess
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirebasePointsRepo", "Failed to load user favorites", e)
                                // Se falhar ao carregar favoritos, enviar pontos sem modificação
                                trySend(points).isSuccess
                            }
                    } else {
                        Log.d("FirebasePointsRepo", "No user logged in, sending points without favorites")
                        trySend(points).isSuccess
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun toggleFavorite(id: Long) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        Log.d("FirebasePointsRepo", "toggleFavorite called for point id: $id, userId: $userId")
        val userDocRef = firestore.collection("users").document(userId)

        try {
            val userDoc = userDocRef.get().await()
            val favorites = userDoc.get("favoritePoints") as? List<*>
            val isCurrentlyFavorite = favorites?.contains(id.toString()) ?: false
            Log.d("FirebasePointsRepo", "Point $id is currently favorite: $isCurrentlyFavorite")

            if (isCurrentlyFavorite) {
                Log.d("FirebasePointsRepo", "Removing point $id from favorites")
                userDocRef.update("favoritePoints", FieldValue.arrayRemove(id.toString())).await()
                Log.d("FirebasePointsRepo", "Point $id removed from favorites successfully")
            } else {
                Log.d("FirebasePointsRepo", "Adding point $id to favorites")
                userDocRef.update("favoritePoints", FieldValue.arrayUnion(id.toString())).await()
                Log.d("FirebasePointsRepo", "Point $id added to favorites successfully")
            }
        } catch (e: Exception) {
            Log.e("FirebasePointsRepo", "Error toggling favorite. Ensure user document exists.", e)
            // Se o documento não existir, criar um novo com o favorito
            try {
                Log.d("FirebasePointsRepo", "Creating user document with favorite point $id")
                val newFavorites = listOf(id.toString())
                userDocRef.set(hashMapOf("favoritePoints" to newFavorites)).await()
                Log.d("FirebasePointsRepo", "User document created with favorite point $id")
            } catch (createError: Exception) {
                Log.e("FirebasePointsRepo", "Error creating user document", createError)
            }
        }
    }

    override suspend fun refresh() {
        // No-op as Firestore provides real-time updates.
    }
}
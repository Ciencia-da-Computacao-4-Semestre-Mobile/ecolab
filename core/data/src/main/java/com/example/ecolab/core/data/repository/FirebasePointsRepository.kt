package com.example.ecolab.core.data.repository

import android.util.Log
import com.example.ecolab.core.data.prepopulation.GeoJsonParser
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePointsRepository @Inject constructor(
    private val geoJsonParser: GeoJsonParser,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : PointsRepository {

    private val allPoints: Flow<List<CollectionPoint>> by lazy {
        flowOf(geoJsonParser.parse().mapIndexed { index, point ->
            point.copy(id = index.toLong())
        })
    }

    override fun observePoints(): Flow<List<CollectionPoint>> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            return allPoints
        }

        val favoritesFlow: Flow<Set<Long>> = callbackFlow {
            val favoritesRef = database.getReference("users").child(userId).child("favorites")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favoriteIds = snapshot.children.mapNotNull { it.key?.toLong() }.toSet()
                    trySend(favoriteIds)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("FirebasePointsRepo", "Listen for favorites failed.", error.toException())
                    close(error.toException())
                }
            }
            favoritesRef.addValueEventListener(listener)
            awaitClose { favoritesRef.removeEventListener(listener) }
        }

        return allPoints.combine(favoritesFlow) { points, favoriteIds ->
            points.map { point ->
                point.copy(isFavorite = favoriteIds.contains(point.id))
            }
        }
    }

    override suspend fun toggleFavorite(id: Long) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = database.getReference("users").child(userId).child("favorites").child(id.toString())

        try {
            val snapshot = favoriteRef.get().await()
            if (snapshot.exists()) {
                favoriteRef.removeValue().await()
            } else {
                favoriteRef.setValue(true).await()
            }
        } catch (e: Exception) {
            Log.e("FirebasePointsRepo", "Failed to toggle favorite", e)
            // O app não vai mais crashar, mas o erro será impresso no Logcat.
        }
    }

    override suspend fun refresh() {
        // No-op for now. Could be implemented to re-fetch points from a remote source.
    }
}

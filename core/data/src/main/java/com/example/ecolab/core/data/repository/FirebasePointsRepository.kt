package com.example.ecolab.core.data.repository

import android.util.Log
import com.example.ecolab.core.data.prepopulation.GeoJsonParser
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePointsRepository @Inject constructor(
    private val geoJsonParser: GeoJsonParser,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PointsRepository {

    private val allPoints: Flow<List<CollectionPoint>> by lazy {
        flowOf(geoJsonParser.parse().mapIndexed { index, point ->
            point.copy(id = index.toLong())
        })
    }

    override fun observePoints(): Flow<List<CollectionPoint>> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("FirestorePointsRepo", "Usuário não autenticado, retornando pontos sem favoritos.")
            return allPoints
        }

        val favoritesFlow: Flow<Set<Long>> = callbackFlow {
            val userDocRef = firestore.collection("users").document(userId)

            val listenerRegistration = userDocRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestorePointsRepo", "Falha ao escutar favoritos.", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val favoriteIds = (snapshot.get("favoritePoints") as? List<*>)
                        ?.mapNotNull { it.toString().toLongOrNull() }
                        ?.toSet() ?: emptySet()
                    trySend(favoriteIds)
                } else {
                    trySend(emptySet())
                }
            }

            awaitClose { listenerRegistration.remove() }
        }

        return allPoints.combine(favoritesFlow) { points, favoriteIds ->
            points.map { point ->
                point.copy(isFavorite = favoriteIds.contains(point.id))
            }
        }
    }

    override suspend fun toggleFavorite(id: Long) {
        val userId = auth.currentUser?.uid ?: run {
            Log.w("FirestorePointsRepo", "Tentativa de favoritar sem usuário logado.")
            return
        }

        val userDocRef = firestore.collection("users").document(userId)

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)

                // Verifica se o documento do usuário existe
                if (!snapshot.exists()) {
                    // Cria o documento com o primeiro favorito
                    val newUserProfile = mapOf(
                        "favoritePoints" to listOf(id)
                    )
                    transaction.set(userDocRef, newUserProfile)
                    Log.d("FirestorePointsRepo", "Documento criado e ponto $id adicionado aos favoritos.")
                    return@runTransaction null
                }

                // Documento já existe, procede normalmente
                val favorites = (snapshot.get("favoritePoints") as? List<*>)
                    ?.mapNotNull { it.toString().toLongOrNull() } ?: emptyList()

                if (id in favorites) {
                    // Remove dos favoritos
                    transaction.update(
                        userDocRef,
                        "favoritePoints",
                        FieldValue.arrayRemove(id)
                    )
                    Log.d("FirestorePointsRepo", "Ponto $id removido dos favoritos.")
                } else {
                    // Adiciona aos favoritos
                    transaction.update(
                        userDocRef,
                        "favoritePoints",
                        FieldValue.arrayUnion(id)
                    )
                    Log.d("FirestorePointsRepo", "Ponto $id adicionado aos favoritos.")
                }
                null
            }.await()

        } catch (e: Exception) {
            Log.e("FirestorePointsRepo", "Erro ao alternar favorito para ponto $id", e)
        }
    }

    override suspend fun refresh() {
        Log.d("FirestorePointsRepo", "Refresh chamado (no-op por enquanto).")
    }

    // ATENÇÃO: ISTO É UM CÓDIGO DE MIGRAÇÃO TEMPORÁRIO.
    // DEVE SER REMOVIDO APÓS O USO.
    suspend fun migratePointsToFirestore() {
        val collectionRef = firestore.collection("collectionPoints")

        // Verifica se a coleção já tem dados para não duplicar
        val existingDocs = collectionRef.limit(1).get().await()
        if (!existingDocs.isEmpty) {
            Log.d("MIGRATION", "A coleção 'collectionPoints' já contém dados. Migração cancelada.")
            return
        }

        Log.d("MIGRATION", "Iniciando a migração dos pontos do GeoJSON para o Firestore...")

        // 1. Lê todos os pontos do seu arquivo local
        val pointsFromGeoJson = this.geoJsonParser.parse()

        // 2. Itera sobre cada ponto e o envia para o Firestore
        pointsFromGeoJson.forEach { point ->
            try {
                // Cria um novo documento com um ID gerado automaticamente
                collectionRef.add(point).await()
                Log.d("MIGRATION", "Ponto '${point.name}' migrado com sucesso.")
            } catch (e: Exception) {
                Log.e("MIGRATION", "Falha ao migrar o ponto '${point.name}'", e)
            }
        }

        Log.d("MIGRATION", "Migração concluída!")
    }
}

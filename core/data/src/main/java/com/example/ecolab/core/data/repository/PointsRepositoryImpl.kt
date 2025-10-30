package com.example.ecolab.core.data.repository

import android.content.Context
import android.util.Log
import com.example.ecolab.core.domain.model.CollectionPoint
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val json: Json,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : PointsRepository {

    private val _points = MutableStateFlow<List<CollectionPoint>>(emptyList())
    private val favoritePointIds = MutableStateFlow<Set<String>>(emptySet())


    init {
        loadPointsFromAssets()
        loadFavoritePoints()
    }

    private fun loadFavoritePoints() {
        firebaseAuth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("PointsRepositoryImpl", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val favoriteIds = snapshot.get("favoritePoints") as? List<String>
                        favoritePointIds.value = favoriteIds?.toSet() ?: emptySet()
                    } else {
                        Log.d("PointsRepositoryImpl", "Current data: null")
                    }
                }
        }
    }


    private fun loadPointsFromAssets() {
        val assets = context.assets
        val collectionPointsDir = "collection_points"
        val files = assets.list(collectionPointsDir)

        val allPoints = files?.flatMap { fileName ->
            try {
                val jsonString = assets.open("$collectionPointsDir/$fileName").bufferedReader().use { it.readText() }
                val featureCollection = json.decodeFromString<FeatureCollection>(jsonString)
                val category = mapFileNameToCategory(fileName)
                featureCollection.features.mapNotNull { it.toCollectionPoint(category, fileName) }
            } catch (e: Exception) {
                Log.e("PointsRepositoryImpl", "Error parsing $fileName: ${e.message}")
                emptyList()
            }
        } ?: emptyList()

        _points.value = allPoints
        Log.d("PointsRepositoryImpl", "Loaded ${allPoints.size} points from assets.")
    }

    private fun mapFileNameToCategory(fileName: String): String {
        return when {
            fileName.contains("ponto_entrega_voluntaria") -> "Ponto de Entrega"
            fileName.contains("ecoponto") -> "Ecoponto"
            fileName.contains("patio_compostagem") -> "Pátio de Compostagem"
            fileName.contains("central_triagem_cooperativa") -> "Cooperativa"
            else -> "Desconhecido"
        }
    }


    override fun observePoints(): Flow<List<CollectionPoint>> {
        return combine(_points, favoritePointIds) { points, favoriteIds ->
            points.map { point ->
                point.copy(isFavorite = favoriteIds.contains(point.id.toString()))
            }
        }
    }


    override suspend fun toggleFavorite(id: Long) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userDocRef = firestore.collection("users").document(userId)

        val isCurrentlyFavorite = favoritePointIds.value.contains(id.toString())

        _points.update { currentPoints ->
            currentPoints.map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
        }

        try {
            if (isCurrentlyFavorite) {
                userDocRef.update("favoritePoints", FieldValue.arrayRemove(id.toString())).await()
            } else {
                userDocRef.update("favoritePoints", FieldValue.arrayUnion(id.toString())).await()
            }
        } catch (e: Exception) {
            // Revert the local state if the firestore update fails
            _points.update { currentPoints ->
                currentPoints.map {
                    if (it.id == id) it.copy(isFavorite = isCurrentlyFavorite) else it
                }
            }
            Log.e("PointsRepositoryImpl", "Error toggling favorite status in Firestore", e)
        }
    }


    override suspend fun refresh() {
        // No-op for now, could be used to re-load from assets if they can change
    }
}

@Serializable
private data class FeatureCollection(
    val features: List<Feature>
)

@Serializable
private data class Feature(
    val id: String,
    val geometry: Geometry,
    val properties: Properties
) {
    fun toCollectionPoint(category: String, fileName: String): CollectionPoint? {
        if (geometry.coordinates.size < 2) {
            Log.w("PointsRepositoryImpl", "Skipping point in $fileName, not enough coordinates: $id")
            return null
        }

        return try {
            val crsFactory = CRSFactory()
            val sourceCRS = crsFactory.createFromName("EPSG:31983")
            val targetCRS = crsFactory.createFromName("EPSG:4326") // WGS84
            val ctFactory = CoordinateTransformFactory()
            val transform = ctFactory.createTransform(sourceCRS, targetCRS)

            val sourceCoord = ProjCoordinate(geometry.coordinates[0], geometry.coordinates[1])
            val targetCoord = ProjCoordinate()
            transform.transform(sourceCoord, targetCoord)

            val name = properties.nm_local
                ?: properties.nm_patio_compostagem
                ?: properties.nm_cooperativa
                ?: properties.nm_ecoponto
                ?: "Nome não disponível"

            CollectionPoint(
                id = id.substringAfter('.').toLongOrNull() ?: 0,
                name = name,
                description = "Endereço: ${properties.nm_endereco ?: "Não informado"}\nContêineres: ${properties.qt_container ?: "Não informado"}",
                latitude = targetCoord.y,
                longitude = targetCoord.x,
                category = category,
                isFavorite = false
            )
        } catch (e: Exception) {
            Log.e("PointsRepositoryImpl", "Error transforming coordinates for point $id in $fileName: ${e.message}")
            null
        }
    }
}

@Serializable
private data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
private data class Properties(
    val nm_local: String? = null,
    val nm_endereco: String? = null,
    val qt_container: Int? = null,
    val nm_subprefeitura: String? = null,
    val nm_patio_compostagem: String? = null,
    val nm_cooperativa: String? = null,
    val nm_ecoponto: String? = null
)

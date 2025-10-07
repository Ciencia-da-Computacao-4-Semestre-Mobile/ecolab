package com.example.ecolab.core.data.prepopulation

import android.content.Context
import com.example.ecolab.core.domain.model.CollectionPoint
import kotlinx.serialization.json.Json
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

class GeoJsonParser(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val crsFactory = CRSFactory()
    private val ctFactory = CoordinateTransformFactory()
    private val utmCrs = crsFactory.createFromParameters("UTM_23S", "+proj=utm +zone=23 +south +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs")
    private val wgs84Crs = crsFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs")
    private val transform = ctFactory.createTransform(utmCrs, wgs84Crs)

    fun parse(): List<CollectionPoint> {
        val assetManager = context.assets
        val fileNames = assetManager.list("collection_points") ?: return emptyList()

        return fileNames.flatMap { fileName ->
            val content = assetManager.open("collection_points/$fileName").bufferedReader().use { it.readText() }
            val featureCollection = json.decodeFromString<FeatureCollection>(content)

            featureCollection.features.mapNotNull { feature ->
                val properties = feature.properties
                val name = properties.name ?: properties.compostYardName ?: properties.voluntaryPointName ?: properties.coopName
                val description = properties.address ?: properties.compostYardAddress ?: properties.voluntaryPointAddress ?: properties.coopAddress
                val category = getCategoryFromFileName(fileName) // Correctly pass the file name

                if (name != null && description != null && feature.geometry.coordinates.size == 2) {
                    val projCoord = ProjCoordinate(feature.geometry.coordinates[0], feature.geometry.coordinates[1])
                    val resultCoord = ProjCoordinate()
                    transform.transform(projCoord, resultCoord)

                    CollectionPoint(
                        id = 0, // We can assign a proper ID later if needed
                        name = name,
                        description = description,
                        latitude = resultCoord.y,
                        longitude = resultCoord.x,
                        category = category
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun getCategoryFromFileName(fileName: String): String {
        return when {
            fileName.contains("ecoponto") -> "Ecoponto"
            fileName.contains("patio_compostagem") -> "Pátio de Compostagem"
            fileName.contains("ponto_entrega_voluntaria") -> "Ponto de Entrega"
            fileName.contains("central_triagem_cooperativa") -> "Cooperativa"
            else -> "Geral"
        }
    }
}

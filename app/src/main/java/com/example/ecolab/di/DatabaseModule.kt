package com.example.ecolab.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ecolab.data.local.AppDatabase
import com.example.ecolab.data.local.prepopulation.GeoJsonFeatureCollection
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.data.model.CollectionPointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.locationtech.proj4j.*
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        daoProvider: Provider<CollectionPointDao> // Use Provider for lazy injection
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ecolab-db"
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(context, daoProvider.get())
                }
            }
        })
        .fallbackToDestructiveMigration() // Deletes the DB on schema changes
        .build()
    }

    @Provides
    fun provideCollectionPointDao(appDatabase: AppDatabase): CollectionPointDao {
        return appDatabase.collectionPointDao()
    }

    private suspend fun prepopulateDatabase(context: Context, dao: CollectionPointDao) {
        val json = Json { isLenient = true; ignoreUnknownKeys = true }
        val files = listOf(
            "collection_points/geoportal_ecoponto.geojson",
            "collection_points/geoportal_central_triagem_cooperativa.geojson",
            "collection_points/geoportal_patio_compostagem.geojson",
            "collection_points/geoportal_ponto_entrega_voluntaria.geojson"
        )

        val crsFactory = CRSFactory()
        val utmCrs = crsFactory.createFromParameters("UTM23S", "+proj=utm +zone=23 +south +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs")
        val wgs84Crs = crsFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs")
        val transform = CoordinateTransformFactory().createTransform(utmCrs, wgs84Crs)

        val allPoints = mutableListOf<CollectionPoint>()

        files.forEach { fileName ->
            try {
                context.assets.open(fileName).bufferedReader().use { reader ->
                    val text = reader.readText()
                    val collection = json.decodeFromString<GeoJsonFeatureCollection>(text)

                    val points = collection.features.mapNotNull { feature ->
                        val props = feature.properties
                        val name = props.ecopontoName ?: props.triagemName ?: props.compostagemName ?: props.pevName
                        val address = props.address
                        val openingHours = props.openingHours ?: props.openingHoursAlt
                        
                        val wasteType = when {
                            props.wasteTypes != null -> props.wasteTypes
                            fileName.contains("cooperativa") -> "Cooperativa"
                            fileName.contains("compostagem") -> "Orgânico"
                            fileName.contains("voluntaria") -> "Ponto de Entrega Voluntária (PEV)"
                            else -> "Não especificado"
                        }
                        
                        val coords = feature.geometry.coordinates

                        if (name != null && address != null && coords.size == 2) {
                            val srcCoord = ProjCoordinate(coords[0], coords[1])
                            val dstCoord = ProjCoordinate()
                            transform.transform(srcCoord, dstCoord)

                            CollectionPoint(
                                name = name,
                                address = address,
                                openingHours = openingHours,
                                wasteType = wasteType,
                                photoUri = "", // No photo for prepopulated data
                                latitude = dstCoord.y,
                                longitude = dstCoord.x,
                                userSubmitted = false
                            )
                        } else {
                            null
                        }
                    }
                    allPoints.addAll(points)
                }
            } catch (e: Exception) {
                println("Error prepopulating from $fileName: ${e.message}")
            }
        }
        if (allPoints.isNotEmpty()) {
            dao.insertAll(allPoints)
        }
    }
}

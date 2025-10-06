package com.example.ecolab.data.local.prepopulation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Defines the structure of the entire GeoJSON file
@Serializable
data class GeoJsonFeatureCollection(
    val features: List<GeoJsonFeature>
)

// Represents a single point/feature in the collection
@Serializable
data class GeoJsonFeature(
    val properties: GeoJsonProperties,
    val geometry: GeoJsonGeometry
)

// Holds the textual properties of a feature from ANY of the GeoJSON files
@Serializable
data class GeoJsonProperties(
    // --- Common Fields ---
    @SerialName("nm_endereco")
    val address: String? = null,

    // --- Name Fields (one of these should exist) ---
    @SerialName("nm_ecoponto")
    val ecopontoName: String? = null,
    @SerialName("nm_cooperativa")
    val triagemName: String? = null,
    @SerialName("nm_patio_compostagem")
    val compostagemName: String? = null,
    @SerialName("nm_local")
    val pevName: String? = null, // Ponto de Entrega Voluntária

    // --- Detail Fields (optional) ---
    @SerialName("tx_atendimento")
    val openingHours: String? = null,
    @SerialName("tx_horario_atendimento")
    val openingHoursAlt: String? = null, // Alternative field for compostagem
    @SerialName("tx_recebimento_comum")
    val wasteTypes: String? = null
)

// Holds the geometry information, specifically the coordinates
@Serializable
data class GeoJsonGeometry(
    @SerialName("coordinates")
    val coordinates: List<Double>
)

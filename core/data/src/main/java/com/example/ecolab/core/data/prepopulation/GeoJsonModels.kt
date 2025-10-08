package com.example.ecolab.core.data.prepopulation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureCollection(
    val features: List<Feature>
)

@Serializable
data class Feature(
    val geometry: Geometry,
    val properties: Properties
)

@Serializable
data class Geometry(
    val coordinates: List<Double>
)

@Serializable
data class Properties(
    // Name fields from different files
    @SerialName("nm_ecoponto") val ecopontoName: String? = null,
    @SerialName("nm_patio_compostagem") val compostYardName: String? = null,
    @SerialName("nm_local") val voluntaryPointName: String? = null,
    @SerialName("nm_cooperativa") val coopName: String? = null,

    // Address field (consistent across all files)
    @SerialName("nm_endereco") val address: String? = null,

    // Opening hours fields
    @SerialName("tx_atendimento") val ecopontoOpeningHours: String? = null,
    @SerialName("tx_horario_atendimento") val compostYardOpeningHours: String? = null,

    // Materials fields
    @SerialName("tx_recebimento_comum") val ecopontoMaterials: String? = null,
    @SerialName("materiais") val voluntaryPointMaterials: String? = null,
    @SerialName("tx_especialidade") val coopSpecialty: String? = null
)

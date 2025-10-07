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
    @SerialName("nm_ecoponto") val name: String? = null,
    @SerialName("nm_endereco") val address: String? = null,
    @SerialName("tx_atendimento") val openingHours: String? = null,

    // Properties for patio_compostagem
    @SerialName("Nome") val compostYardName: String? = null,
    @SerialName("Endereco") val compostYardAddress: String? = null,
    @SerialName("Obs") val compostYardObs: String? = null,

    // Properties for ponto_entrega_voluntaria
    @SerialName("nome") val voluntaryPointName: String? = null,
    @SerialName("endereco") val voluntaryPointAddress: String? = null,
    @SerialName("materiais") val materials: String? = null,

    // Properties for central_triagem_cooperativa
    @SerialName("Nome_da_Cooperativa") val coopName: String? = null,
    @SerialName("Endereco_Completo") val coopAddress: String? = null,
    @SerialName("telefone") val coopPhone: String? = null
)

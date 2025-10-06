package com.example.ecolab.data.geojson

import com.google.android.gms.maps.model.LatLng

enum class CollectionType(val displayName: String) {
    ECOPONTO("Ecoponto"),
    PEV("Ponto de Entrega Voluntária"),
    COOPERATIVA("Cooperativa"),
    COMPOSTAGEM("Pátio de Compostagem")
}

data class CollectionPoint(
    val position: LatLng,
    val type: CollectionType,
    val name: String? = null
)
package com.example.ecolab.data.geojson

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

object GeoJsonRepository {
    fun loadCollectionPoints(context: Context): List<CollectionPoint> {
        val result = mutableListOf<CollectionPoint>()
        val dir = "collection_points"
        val files = context.assets.list(dir) ?: emptyArray()
        files.filter { it.endsWith(".geojson") }.forEach { name ->
            val json = context.assets.open("$dir/$name").bufferedReader().use { it.readText() }
            val inferred = inferTypeFromFile(name)
            result += parseFeatureCollection(json, inferred)
        }
        return result
    }

    private fun parseFeatureCollection(json: String, defaultType: CollectionType?): List<CollectionPoint> {
        val list = mutableListOf<CollectionPoint>()
        try {
            val root = JSONObject(json)
            val features = root.optJSONArray("features") ?: return list
            for (i in 0 until features.length()) {
                val feat = features.optJSONObject(i) ?: continue
                val geometry = feat.optJSONObject("geometry") ?: continue
                val props = feat.optJSONObject("properties")
                val name = props?.optString("name")
                    ?: props?.optString("Nome")
                    ?: props?.optString("nome")
                val typeProp = props?.optString("type")
                    ?: props?.optString("tipo")
                    ?: props?.optString("Tipo")
                val pointType = parseType(typeProp) ?: defaultType ?: CollectionType.ECOPONTO

                when (geometry.optString("type")) {
                    "Point" -> {
                        val coords = geometry.optJSONArray("coordinates") ?: continue
                        val lng = coords.optDouble(0)
                        val lat = coords.optDouble(1)
                        list.add(CollectionPoint(LatLng(lat, lng), pointType, name))
                    }
                    "MultiPoint" -> {
                        val coords = geometry.optJSONArray("coordinates") ?: continue
                        for (j in 0 until coords.length()) {
                            val arr = coords.optJSONArray(j) ?: continue
                            val lng = arr.optDouble(0)
                            val lat = arr.optDouble(1)
                            list.add(CollectionPoint(LatLng(lat, lng), pointType, name))
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Ignora arquivos malformados sem quebrar a UI
        }
        return list
    }

    private fun inferTypeFromFile(name: String): CollectionType? {
        val n = name.lowercase()
        return when {
            "ecoponto" in n -> CollectionType.ECOPONTO
            "ponto_entrega_voluntaria" in n || "pev" in n -> CollectionType.PEV
            "cooperativa" in n || "triagem" in n -> CollectionType.COOPERATIVA
            "compostagem" in n || "patio" in n || "pátio" in n -> CollectionType.COMPOSTAGEM
            else -> null
        }
    }

    private fun parseType(value: String?): CollectionType? {
        val v = value?.lowercase() ?: return null
        return when {
            v.contains("ecoponto") -> CollectionType.ECOPONTO
            v.contains("ponto de entrega") || v.contains("pev") -> CollectionType.PEV
            v.contains("cooperativa") || v.contains("triagem") -> CollectionType.COOPERATIVA
            v.contains("compostagem") || v.contains("pátio") || v.contains("patio") -> CollectionType.COMPOSTAGEM
            else -> null
        }
    }
}
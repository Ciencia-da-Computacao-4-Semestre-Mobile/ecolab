package com.example.ecolab.data.geojson

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

object GeoJsonLoader {
    fun loadAllPoints(context: Context): List<LatLng> {
        val result = mutableListOf<LatLng>()
        val dir = "collection_points"
        val files = context.assets.list(dir) ?: emptyArray()
        files.filter { it.endsWith(".geojson") }.forEach { name ->
            val json = context.assets.open("$dir/$name").bufferedReader().use { it.readText() }
            result += parseFeatureCollection(json)
        }
        return result
    }

    private fun parseFeatureCollection(json: String): List<LatLng> {
        val list = mutableListOf<LatLng>()
        try {
            val root = JSONObject(json)
            val features = root.optJSONArray("features") ?: return list
            for (i in 0 until features.length()) {
                val feat = features.optJSONObject(i) ?: continue
                val geometry = feat.optJSONObject("geometry") ?: continue
                when (geometry.optString("type")) {
                    "Point" -> {
                        val coords = geometry.optJSONArray("coordinates") ?: continue
                        val lng = coords.optDouble(0)
                        val lat = coords.optDouble(1)
                        list.add(LatLng(lat, lng))
                    }
                    "MultiPoint" -> {
                        val coords = geometry.optJSONArray("coordinates") ?: continue
                        for (j in 0 until coords.length()) {
                            val arr = coords.optJSONArray(j) ?: continue
                            val lng = arr.optDouble(0)
                            val lat = arr.optDouble(1)
                            list.add(LatLng(lat, lng))
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Ignora arquivos malformados sem quebrar a UI
        }
        return list
    }
}
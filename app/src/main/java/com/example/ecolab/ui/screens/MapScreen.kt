package com.example.ecolab.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.feature.map.MapViewModel
import com.example.ecolab.ui.components.PointDetailsSheet
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToQuickAction: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPoint by remember { mutableStateOf<CollectionPoint?>(null) }
    val context = LocalContext.current

    val saoPaulo = LatLng(-23.5505, -46.6333)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(saoPaulo, 11f)
    }

    fun navigateToPoint(point: CollectionPoint) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${point.latitude},${point.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToQuickAction) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar novo ponto de coleta")
            }
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(padding),
            cameraPositionState = cameraPositionState,
            onMapClick = { selectedPoint = null }
        ) {
            uiState.points.forEach { point ->
                Marker(
                    state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                    title = point.name,
                    snippet = point.wasteType,
                    onClick = {
                        selectedPoint = point
                        true // Consume the click
                    }
                )
            }
        }
    }

    selectedPoint?.let { point ->
        PointDetailsSheet(
            point = point,
            onDismiss = { selectedPoint = null },
            onNavigate = { navigateToPoint(point) }
        )
    }
}

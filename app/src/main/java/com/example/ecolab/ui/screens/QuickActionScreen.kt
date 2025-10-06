package com.example.ecolab.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.feature.quickaction.QuickActionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuickActionScreen(
    viewModel: QuickActionViewModel = hiltViewModel(),
    onClose: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val wasteTypes = listOf("Plástico", "Vidro", "Papel", "Metal", "Orgânico")
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-15.7801, -47.9292), 12f) // Default to Brazil's capital
    }

    LaunchedEffect(locationPermission) {
        locationPermission.launchPermissionRequest()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Ponto de Coleta") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { /* TODO: viewModel.submit() */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                 enabled = uiState.selectedWasteType != null
            ) {
                Text("CONCLUIR", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Waste Type Selection
            Text("Qual o tipo de material?", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(wasteTypes) {
                    FilterChip(
                        selected = it == uiState.selectedWasteType,
                        onClick = { viewModel.onWasteTypeSelected(it) },
                        label = { Text(it) }
                    )
                }
            }

            // Photo Button
            OutlinedButton(
                onClick = { /* TODO: Launch camera */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Adicionar Foto")
            }

            // Map
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)) {
                 if (locationPermission.status.isGranted) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { viewModel.onLocationCaptured(it.latitude, it.longitude) }
                    ) {
                        uiState.location?.let {
                            Marker(state = MarkerState(position = LatLng(it.first, it.second)))
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("É necessário permissão de localização para usar o mapa.")
                    }
                }
            }
        }
    }
}

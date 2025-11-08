package com.example.ecolab.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import com.example.ecolab.ui.components.AwesomePointDetailsModal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.feature.map.MapViewModel
import com.example.ecolab.ui.theme.Palette
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val saoPaulo = LatLng(-23.55, -46.63)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(saoPaulo, 10f)
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val filterCategories = listOf(
        "Todos",
        "Ecoponto",
        "Cooperativa",
        "Ponto de Entrega",
        "Pátio de Compostagem",
        "Favoritos"
    )

    LaunchedEffect(uiState.searchedLocation) {
        uiState.searchedLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(Unit) {
        locationPermissions.launchMultiplePermissionRequest()
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissions.allPermissionsGranted,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            uiSettings = MapUiSettings(
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        ) {
            uiState.collectionPoints.forEach { point ->
                Marker(
                    state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                    title = point.name,
                    snippet = point.description,
                    icon = BitmapDescriptorFactory.defaultMarker(getMarkerHue(point.category)),
                    onClick = {
                        viewModel.onMarkerClick(point)
                        true
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Palette.textMuted, RoundedCornerShape(24.dp)),
                placeholder = { Text("Pesquisar...", color = Palette.textMuted) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onSearch() }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = Palette.textMuted
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Palette.text,
                    unfocusedTextColor = Palette.text
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 16.dp)
            ) {
                filterCategories.forEach { category ->
                    val categoryColor = getCategoryColor(category)
                    val isSelected = if (category == "Favoritos") {
                        uiState.showFavorites
                    } else {
                        uiState.selectedCategory == category
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (category == "Favoritos") {
                                viewModel.onToggleFavorites(!uiState.showFavorites)
                            } else {
                                viewModel.onFilterChange(category)
                            }
                        },
                        label = { Text(category) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            selectedContainerColor = categoryColor,
                            labelColor = Palette.textMuted,
                            selectedLabelColor = Color.White
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) categoryColor else Palette.textMuted
                        )
                    )
                }
            }
        }

        if (uiState.selectedPoint != null) {
            val point = uiState.selectedPoint!!
            val categoryColor = getCategoryColor(point.category)

            AwesomePointDetailsModal(
                point = point,
                onDismiss = { viewModel.onDismissBottomSheet() },
                onFavorite = { viewModel.toggleFavorite() },
                onRouteClick = {
                    val gmmIntentUri = Uri.parse(
                        "geo:${point.latitude},${point.longitude}?q=${point.name}"
                    )
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                categoryColor = categoryColor
            )
        }

        MapControls(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            scope = scope,
            locationPermissions = locationPermissions,
            cameraPositionState = cameraPositionState,
            fusedLocationClient = fusedLocationClient
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MapControls(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    locationPermissions: MultiplePermissionsState,
    cameraPositionState: CameraPositionState,
    fusedLocationClient: FusedLocationProviderClient
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = {
                scope.launch {
                    if (locationPermissions.allPermissionsGranted) {
                        try {
                            @SuppressLint("MissingPermission")
                            val location = fusedLocationClient.lastLocation.await()
                            location?.let {
                                val userLatLng = LatLng(it.latitude, it.longitude)
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f),
                                    durationMs = 1000
                                )
                            }
                        } catch (e: Exception) {
                            // Handle exception
                        }
                    } else {
                        locationPermissions.launchMultiplePermissionRequest()
                    }
                }
            },
            containerColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Minha Localização"
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(
                            CameraUpdateFactory.zoomTo(currentZoom + 1f)
                        )
                    }
                },
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In")
            }

            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(
                            CameraUpdateFactory.zoomTo(currentZoom - 1f)
                        )
                    }
                },
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out")
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Ecoponto" -> Color(0xFF0F9D58) // Verde
        "Cooperativa" -> Color(0xFFD50F25) // Vermelho
        "Ponto de Entrega" -> Color(0xFF3369E8) // Azul
        "Pátio de Compostagem" -> Color(0xFFF4B400) // Amarelo
        else -> Color.Gray
    }
}

private fun getMarkerHue(category: String): Float {
    return when (category) {
        "Ecoponto" -> BitmapDescriptorFactory.HUE_GREEN
        "Cooperativa" -> BitmapDescriptorFactory.HUE_RED
        "Ponto de Entrega" -> BitmapDescriptorFactory.HUE_AZURE
        "Pátio de Compostagem" -> BitmapDescriptorFactory.HUE_ORANGE
        else -> BitmapDescriptorFactory.HUE_VIOLET
    }
}

private fun getMockMaterials(category: String): List<String> {
    return when (category) {
        "Ecoponto" -> listOf(
            "Entulho",
            "Pneus",
            "Móveis Velhos",
            "Óleo de Cozinha",
            "Eletrônicos"
        )
        "Cooperativa" -> listOf(
            "Papel",
            "Plástico",
            "Metal",
            "Vidro",
            "Recicláveis em geral"
        )
        "Ponto de Entrega" -> listOf(
            "Pilhas",
            "Baterias",
            "Lâmpadas",
            "Eletrônicos de Pequeno Porte"
        )
        "Pátio de Compostagem" -> listOf(
            "Restos de Alimentos",
            "Podas de Jardim"
        )
        else -> emptyList()
    }
}
package com.example.ecolab.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.feature.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
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

    val filterCategories = listOf("Todos", "Ecoponto", "Cooperativa", "Ponto de Entrega", "Pátio de Compostagem", "Favoritos")

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
                val categoryColor = getCategoryColor(category = point.category)
                Marker(
                    state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                    title = point.name,
                    snippet = point.description,
                    icon = getMarkerIconBitmap(context, categoryColor),
                    onClick = { viewModel.onMarkerClick(point); true }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar...") },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 16.dp)
            ) {
                filterCategories.forEach { category ->
                    FilterChip(
                        selected = if (category == "Favoritos") uiState.showFavorites else uiState.selectedCategory == category,
                        onClick = {
                            if (category == "Favoritos") {
                                viewModel.onToggleFavorites(!uiState.showFavorites)
                            } else {
                                viewModel.onFilterChange(category)
                            }
                        },
                        label = { Text(category) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        if (uiState.selectedPoint != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onDismissBottomSheet() },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(uiState.selectedPoint!!.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (uiState.selectedPoint!!.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favoritar"
                            )
                        }
                    }
                    Text(uiState.selectedPoint!!.category, style = MaterialTheme.typography.labelMedium)
                    Text(uiState.selectedPoint!!.description, style = MaterialTheme.typography.bodyLarge)
                    uiState.selectedPoint!!.openingHours?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Horário de funcionamento",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    uiState.selectedPoint!!.materials?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Recycling,
                                contentDescription = "Materiais aceitos",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(onClick = {
                        val point = uiState.selectedPoint!!
                        val gmmIntentUri = Uri.parse("geo:${point.latitude},${point.longitude}?q=${point.name}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }) {
                        Text("Traçar rota")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(CameraUpdateFactory.zoomTo(currentZoom + 1f))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In")
            }
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(CameraUpdateFactory.zoomTo(currentZoom - 1f))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out")
            }
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
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Minha Localização")
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Ecoponto" -> Color(0x993369E8) // Azul fosco
        "Cooperativa" -> Color(0x99D50F25) // Vermelho fosco
        "Ponto de Entrega" -> Color(0x990F9D58) // Verde fosco
        "Pátio de Compostagem" -> Color(0x99F4B400) // Amarelo fosco
        else -> Color.Gray
    }
}

private fun getMarkerIconBitmap(context: Context, color: Color): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_location)!!
    drawable.colorFilter = PorterDuffColorFilter(color.toArgb(), PorterDuff.Mode.SRC_IN)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

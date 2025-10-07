package com.example.ecolab.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    val filterCategories = listOf("Todos", "Ecoponto", "Cooperativa", "Ponto de Entrega", "Pátio de Compostagem")

    LaunchedEffect(Unit) {
        locationPermissions.launchMultiplePermissionRequest()
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissions.allPermissionsGranted),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false) // We use a custom FAB
        ) {
            uiState.collectionPoints.forEach { point ->
                Marker(
                    state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                    title = point.name,
                    snippet = point.description,
                    icon = getMarkerIconBitmap(context, point.category)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                filterCategories.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onFilterChange(category) },
                        label = { Text(category) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
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
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Minha Localização")
        }
    }
}

@Composable
private fun getMarkerIconBitmap(context: Context, category: String): BitmapDescriptor {
    val drawableId = when (category) {
        "Cooperativa" -> R.drawable.ic_recycling
        "Ecoponto" -> R.drawable.ic_add_location
        else -> R.drawable.ic_location
    }
    val drawable = ContextCompat.getDrawable(context, drawableId)!!
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


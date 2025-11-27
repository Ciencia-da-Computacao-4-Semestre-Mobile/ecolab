package com.example.ecolab.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.setValue
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

    val fusedLocationClient = androidx.compose.runtime.remember(context) { LocationServices.getFusedLocationProviderClient(context) }

    val filterCategories = listOf(
        "Todos",
        "Ecoponto",
        "Cooperativa",
        "Ponto de Entrega",
        "Pátio de Compostagem"
    )

    LaunchedEffect(uiState.searchedLocation) {
        uiState.searchedLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 250
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
            val items = androidx.compose.runtime.remember(uiState.collectionPoints) {
                uiState.collectionPoints.map { p ->
                    PointItem(
                        id = p.id,
                        itemPosition = LatLng(p.latitude, p.longitude),
                        itemTitle = p.name,
                        itemSnippet = p.description,
                        category = p.category,
                        point = p
                    )
                }
            }
            var clusterManager by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.google.maps.android.clustering.ClusterManager<PointItem>?>(null) }
            MapEffect(items) { map ->
                if (clusterManager == null) {
                    val cm = com.google.maps.android.clustering.ClusterManager<PointItem>(context, map)
                    cm.renderer = PointRenderer(context, map, cm)
                    cm.setOnClusterItemClickListener { item ->
                        viewModel.onMarkerClick(item.point)
                        true
                    }
                    cm.setOnClusterClickListener { cluster ->
                        val b = com.google.android.gms.maps.model.LatLngBounds.Builder()
                        cluster.items.forEach { b.include(it.position) }
                        val bounds = b.build()
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngBounds(bounds, 64),
                                durationMs = 250
                            )
                        }
                        true
                    }
                    clusterManager = cm
                }
                // Filtra por bounds visíveis para reduzir carga de renderização
                val visibleBounds = map.projection.visibleRegion.latLngBounds
                val visibleItems = items.filter { visibleBounds.contains(it.position) }
                clusterManager?.clearItems()
                clusterManager?.addItems(visibleItems)
                clusterManager?.cluster()
            }
            androidx.compose.runtime.LaunchedEffect(cameraPositionState.isMoving) {
                if (!cameraPositionState.isMoving) {
                    clusterManager?.onCameraIdle()
                }
            }

            // Garante re-cluster imediato ao alternar favoritos
            androidx.compose.runtime.LaunchedEffect(uiState.showFavorites) {
                clusterManager?.clearItems()
                // Usa itens já derivados de uiState.collectionPoints
                clusterManager?.addItems(items)
                clusterManager?.cluster()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Palette.surface)
            ) {
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pesquisar...", color = Palette.textMuted) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onSearch() }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Pesquisar",
                                tint = Palette.primary
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Palette.text,
                        unfocusedTextColor = Palette.text
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 16.dp)
            ) {
                filterCategories.forEach { category ->
                    val isSelected = uiState.selectedCategory == category

                    val containerColor by animateColorAsState(
                        targetValue = if (isSelected) Palette.primary else Palette.surface,
                        animationSpec = tween(durationMillis = 300), label = ""
                    )
                    val labelColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Palette.text,
                        animationSpec = tween(durationMillis = 300), label = ""
                    )

                    Card(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = BorderStroke(1.dp, if(isSelected) Palette.primary else Palette.textMuted)
                    ) {
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onFilterChange(category) },
                            label = { Text(text = category, color = labelColor) },
                            modifier = Modifier,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.Transparent,
                                selectedContainerColor = Color.Transparent,
                            ),
                            border = null
                        )
                    }
                }
            }
        }

        if (uiState.selectedPoint != null) {
            val point = uiState.selectedPoint!!

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
                categoryColor = getCategoryColorForModal(point.category)
            )
        }

        MapControls(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            scope = scope,
            locationPermissions = locationPermissions,
            cameraPositionState = cameraPositionState,
            fusedLocationClient = fusedLocationClient,
            isFavoritesEnabled = uiState.showFavorites,
            onToggleFavorites = { viewModel.onToggleFavorites(!uiState.showFavorites) }
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
    fusedLocationClient: FusedLocationProviderClient,
    isFavoritesEnabled: Boolean,
    onToggleFavorites: () -> Unit
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
                                    durationMs = 250
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
            containerColor = Palette.primary,
            contentColor = Color.White
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
                containerColor = Palette.surface,
                contentColor = Palette.primary
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
                containerColor = Palette.surface,
                contentColor = Palette.primary
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            FloatingActionButton(
                onClick = onToggleFavorites,
                containerColor = if (isFavoritesEnabled) Palette.primary else Palette.surface,
                contentColor = if (isFavoritesEnabled) Color.White else Palette.primary
            ) {
                Icon(
                    imageVector = if (isFavoritesEnabled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favoritos"
                )
            }
        }
    }
}

@Composable
private fun getCategoryColorForModal(category: String): Color {
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

private data class PointItem(
    val id: Long,
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String?,
    val category: String,
    val point: com.example.ecolab.core.domain.model.CollectionPoint
) : com.google.maps.android.clustering.ClusterItem {
    override fun getPosition(): LatLng = itemPosition
    override fun getTitle(): String = itemTitle
    override fun getSnippet(): String? = itemSnippet
    override fun getZIndex(): Float = 0f
}

private class PointRenderer(
    context: android.content.Context,
    map: com.google.android.gms.maps.GoogleMap,
    clusterManager: com.google.maps.android.clustering.ClusterManager<PointItem>
) : com.google.maps.android.clustering.view.DefaultClusterRenderer<PointItem>(context, map, clusterManager) {
    override fun onBeforeClusterItemRendered(item: PointItem, markerOptions: com.google.android.gms.maps.model.MarkerOptions) {
        val hue = getMarkerHue(item.category)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hue))
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}
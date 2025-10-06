package com.example.ecolab.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ecolab.feature.quickaction.QuickActionEvent
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
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuickActionScreen(
    viewModel: QuickActionViewModel = hiltViewModel(),
    onClose: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- Event Handling ---
    LaunchedEffect(Unit) {
        viewModel.eventChannel.collectLatest {
            when(it) {
                is QuickActionEvent.SubmissionSuccess -> onClose()
            }
        }
    }

    // --- Camera & Permissions Setup ---
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let { viewModel.onPhotoTaken(it.toString()) }
            }
        }
    )

    fun createImageUri(context: Context): Uri {
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
    // --- End Camera Setup ---

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
                onClick = { viewModel.submit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                 enabled = uiState.selectedWasteType != null && uiState.photoUri != null
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

            // --- Photo Section ---
            if (uiState.photoUri == null) {
                OutlinedButton(
                    onClick = {
                        if (cameraPermission.status.isGranted) {
                            val uri = createImageUri(context)
                            tempImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermission.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Adicionar Foto")
                    }
                }
            } else {
                Box(modifier = Modifier.height(200.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(model = Uri.parse(uiState.photoUri)),
                        contentDescription = "Foto do Ponto de Coleta",
                        modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Card(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
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

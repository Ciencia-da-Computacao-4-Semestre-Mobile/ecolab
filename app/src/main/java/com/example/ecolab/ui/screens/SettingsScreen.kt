package com.example.ecolab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecolab.ui.components.AnimatedParticles
import com.example.ecolab.ui.theme.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showAbout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Configurações",
                        color = Palette.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Palette.text
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Palette.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Palette.background)
                .padding(paddingValues)
        ) {
            AnimatedParticles(
                particleCount = 15,
                colors = listOf(
                    Palette.primary.copy(alpha = 0.3f),
                    Palette.secondary.copy(alpha = 0.3f),
                    Palette.success.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                

                

                SettingsSection(
                    title = "Sobre",
                    items = listOf(
                        SettingsItem(
                            title = "Sobre o App",
                            description = "Versão ${uiState.appVersion}",
                            icon = Icons.Default.Info,
                            onClick = { showAbout = true }
                        ),
                        SettingsItem(
                            title = "Avaliar App",
                            description = "Avalie-nos na Play Store",
                            icon = Icons.Default.Star,
                            onClick = {
                                val pkg = context.packageName
                                val market = android.net.Uri.parse("market://details?id=$pkg")
                                val web = android.net.Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, market)
                                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                runCatching { context.startActivity(intent) }.getOrElse {
                                    context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, web).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                                }
                            }
                        ),
                        SettingsItem(
                            title = "Compartilhar App",
                            description = "Compartilhe com amigos",
                            icon = Icons.Default.Share,
                            onClick = {
                                val pkg = context.packageName
                                val url = "https://play.google.com/store/apps/details?id=$pkg"
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
                                intent.type = "text/plain"
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Conheça o EcoLab: $url")
                                context.startActivity(android.content.Intent.createChooser(intent, "Compartilhar EcoLab").addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                        )
                    )
                )

                

                if (showAbout) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showAbout = false },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = { showAbout = false }) {
                                androidx.compose.material3.Text("OK")
                            }
                        },
                        title = { androidx.compose.material3.Text("EcoLab") },
                        text = { androidx.compose.material3.Text("Versão ${uiState.appVersion}\nAplicativo para educação ambiental e reciclagem.") }
                    )
                }
            }
        }
    }
}

data class SettingsItem(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
    val hasSwitch: Boolean = false,
    val switchState: Boolean = false,
    val onSwitchChange: ((Boolean) -> Unit)? = null
)

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Palette.surface.copy(alpha = 0.8f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = Palette.text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        items.forEach { item ->
            SettingsItemRow(item)
        }
    }
}

@Composable
fun SettingsItemRow(item: SettingsItem) {
    Surface(
        onClick = item.onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Palette.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    color = Palette.text,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = item.description,
                    color = Palette.textMuted,
                    fontSize = 12.sp
                )
            }

            if (item.hasSwitch) {
                Switch(
                    checked = item.switchState,
                    onCheckedChange = item.onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Palette.primary,
                        checkedTrackColor = Palette.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = Palette.textMuted,
                        uncheckedTrackColor = Palette.textMuted.copy(alpha = 0.3f)
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir",
                    tint = Palette.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
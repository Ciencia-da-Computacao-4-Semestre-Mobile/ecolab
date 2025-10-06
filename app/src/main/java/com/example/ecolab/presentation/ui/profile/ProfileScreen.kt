package com.example.ecolab.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.presentation.theme.EcoGreen
import com.example.ecolab.presentation.components.EcoCard
import com.example.ecolab.presentation.components.EcoSection
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        // Cabeçalho verde com avatar e nome
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(state.name, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge)
                    Text("usuario@ecolab.com", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Meu Progresso (grid 2x2)
        EcoSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            accentColor = EcoGreen,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("Meu Progresso", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row {
                StatCard(title = "Pontos Totais", value = state.points.toString(), icon = Icons.Filled.Star, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                StatCard(title = "Quizzes Feitos", value = "0", icon = Icons.Filled.Psychology, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row {
                StatCard(title = "Conquistas", value = "0", icon = Icons.Filled.EmojiEvents, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                StatCard(title = "Artigos Lidos", value = "0", icon = Icons.Filled.Article, modifier = Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Conquistas (lista)
        EcoSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            accentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("Conquistas (0/6)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Column {
                AchievementItem(
                    title = "Primeiro Passo",
                    subtitle = "Leu seu primeiro artigo",
                    icon = Icons.Filled.EmojiEvents
                )
                Spacer(Modifier.height(8.dp))
                AchievementItem(
                    title = "Leitor Dedicado",
                    subtitle = "Leu 10 artigos",
                    icon = Icons.Filled.Article
                )
                Spacer(Modifier.height(8.dp))
                AchievementItem(
                    title = "Quiz Master",
                    subtitle = "Completou 5 quizzes",
                    icon = Icons.Filled.Psychology
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Interesses Ambientais (chips simples)
        EcoSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            accentColor = EcoGreen,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("Interesses Ambientais", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row {
                Chip("Reciclagem")
                Spacer(Modifier.width(8.dp))
                Chip("Energia")
                Spacer(Modifier.width(8.dp))
                Chip("Água")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Menu
        EcoSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            accentColor = EcoGreen,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("Menu", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            MenuItem(Icons.Filled.History, "Histórico de Ações")
            Spacer(Modifier.height(8.dp))
            MenuItem(Icons.Filled.Settings, "Configurações")
        }

        Spacer(Modifier.height(16.dp))

        // Sair do Aplicativo
        Row(Modifier.padding(horizontal = 16.dp)) {
            Button(onClick = { /* TODO logout */ }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sair do Aplicativo")
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    EcoCard(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Text(value, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    val shape: Shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)), shape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    EcoCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        containerColor = MaterialTheme.colorScheme.surface
    ) { 
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = EcoGreen)
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AchievementItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    EcoCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = EcoGreen)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
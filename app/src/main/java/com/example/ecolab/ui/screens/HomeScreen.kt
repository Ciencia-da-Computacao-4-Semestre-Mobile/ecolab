package com.example.ecolab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.feature.home.HomeViewModel
import com.example.ecolab.ui.components.PointCard
import com.example.ecolab.ui.theme.EcoLabTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenRanking: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onNavigateToQuickAction: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ecolab_logo),
                        contentDescription = "EcoLab Logo",
                        modifier = Modifier.height(32.dp)
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                MissionOfTheDayCard(
                    isCompleted = uiState.isPlasticMissionCompleted,
                    onComplete = onNavigateToQuickAction
                )
            }

            item {
                ShortcutsRow(
                    onOpenRanking = onOpenRanking,
                    onOpenAchievements = onOpenAchievements
                )
            }

            item {
                Text(
                    "Pontos próximos",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(uiState.points) { point ->
                PointCard(
                    point = point,
                    onClick = { /* TODO: Navigate to point details */ }
                )
            }

            item { Spacer(Modifier.height(16.dp)) } // Bottom padding
        }
    }
}

@Composable
private fun MissionOfTheDayCard(
    isCompleted: Boolean,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            if (isCompleted) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Missão completa", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Text("Primeiro descarte de plástico concluído!", style = MaterialTheme.typography.titleLarge)
                }
            } else {
                Text("Missão do Dia: Descarte Plástico", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    Button(onClick = onComplete) {
                        Text("Concluir agora")
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { /*TODO*/ }) {
                        Text("Pular")
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutsRow(
    onOpenRanking: () -> Unit,
    onOpenAchievements: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onOpenRanking,
            label = { Text("Ranking") },
            leadingIcon = {
                Icon(
                    Icons.Default.Leaderboard,
                    contentDescription = "Abrir Ranking"
                )
            }
        )
        AssistChip(
            onClick = onOpenAchievements,
            label = { Text("Conquistas") },
            leadingIcon = {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Abrir Conquistas"
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    EcoLabTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            MissionOfTheDayCard(isCompleted = false, onComplete = {})
            ShortcutsRow({}, {})
            Text("Pontos próximos", style = MaterialTheme.typography.titleMedium)
            PointCard(
                point = CollectionPoint(
                    id = 1,
                    name = "Ecoponto Preview",
                    address = "Rua Ficticia, 123",
                    openingHours = "Seg-Sex: 8h-18h",
                    wasteType = "Multi",
                    photoUri = "",
                    latitude = -23.55,
                    longitude = -46.63,
                    userSubmitted = true
                ),
                onClick = {}
            )
        }
    }
}

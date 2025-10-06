package com.example.ecolab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.feature.home.HomeViewModel
import com.example.ecolab.ui.components.PointCard
import com.example.ecolab.ui.theme.EcoLabTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenRanking: () -> Unit = {},
    onOpenAchievements: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }

        item {
            MissionOfTheDayCard()
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

@Composable
private fun MissionOfTheDayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Missão do Dia", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row {
                Button(onClick = { /*TODO*/ }) {
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
            MissionOfTheDayCard()
            ShortcutsRow({}, {})
             Text("Pontos próximos", style = MaterialTheme.typography.titleMedium)
            PointCard(
                point = CollectionPoint(1, "Ecoponto Preview", "Multi", "", -23.55, -46.63, true),
                onClick = {}
            )
        }
    }
}

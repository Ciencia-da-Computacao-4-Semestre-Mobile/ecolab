package com.example.ecolab.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.feature.home.HomeViewModel
import com.example.ecolab.ui.components.PointCard
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRankingClick: () -> Unit,
    onAchievementsClick: () -> Unit
) {
    val points by viewModel.points.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { MissionCard() }
        item { Shortcuts(onRankingClick = onRankingClick, onAchievementsClick = onAchievementsClick) }
        item {
            Text(
                text = "Pontos próximos",
                style = MaterialTheme.typography.titleMedium,
                color = Palette.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(points) { point ->
            PointCard(
                point = point,
                onClick = { /* TODO: Navigate to point details */ },
                onFavorite = { viewModel.toggleFavorite(point.id) }
            )
        }
    }
}

@Composable
private fun MissionCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Missão do Dia",
                style = MaterialTheme.typography.titleLarge,
                color = Palette.text
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier.fillMaxWidth(),
                color = Palette.primary,
                trackColor = Palette.divider
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO */ }) {
                    Text("Pular", color = Palette.textMuted)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.primary),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("Concluir agora")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Shortcuts(onRankingClick: () -> Unit, onAchievementsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AssistChip(
            onClick = onRankingClick,
            label = { Text("Ranking") },
            leadingIcon = { Icon(Icons.Default.Leaderboard, "Ranking", modifier = Modifier.size(24.dp)) },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(44.dp),
            border = BorderStroke(1.dp, Palette.divider)
        )
        AssistChip(
            onClick = onAchievementsClick,
            label = { Text("Conquistas") },
            leadingIcon = { Icon(Icons.Default.EmojiEvents, "Conquistas", modifier = Modifier.size(24.dp)) },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(44.dp),
            border = BorderStroke(1.dp, Palette.divider)
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    EcoLabTheme {
        HomeScreen(onRankingClick = {}, onAchievementsClick = {})
    }
}

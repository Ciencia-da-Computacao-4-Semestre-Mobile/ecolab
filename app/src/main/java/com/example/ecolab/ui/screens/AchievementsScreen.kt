package com.example.ecolab.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.data.model.Achievement
import com.example.ecolab.feature.achievements.AchievementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val achievements by viewModel.achievements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conquistas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    val cardColor = if (achievement.isUnlocked) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val icon = if (achievement.isUnlocked) {
        Icons.Default.CheckCircle
    } else {
        Icons.Default.Lock
    }

    val iconColor = if (achievement.isUnlocked) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = if (achievement.isUnlocked) "Desbloqueado" else "Bloqueado",
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = iconColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

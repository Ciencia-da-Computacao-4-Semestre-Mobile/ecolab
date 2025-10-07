package com.example.ecolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecolab.ui.theme.Palette

@Composable
fun AchievementsScreen() {
    val unlockedCount = 8
    val totalCount = 24

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Conquistas ($unlockedCount/$totalCount)",
            style = MaterialTheme.typography.headlineSmall,
            color = Palette.text
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { unlockedCount.toFloat() / totalCount.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = Palette.primary,
            trackColor = Palette.divider
        )
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(totalCount) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(containerColor = if (it < unlockedCount) Palette.divider else Palette.surface)
                ) {
                    Box(
                        modifier = Modifier.aspectRatio(1f).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Badge ${it + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (it < unlockedCount) Palette.primary else Palette.textMuted
                        )
                    }
                }
            }
        }
    }
}

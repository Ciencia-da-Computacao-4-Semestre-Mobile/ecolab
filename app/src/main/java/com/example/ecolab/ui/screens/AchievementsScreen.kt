package com.example.ecolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    val unlockedBadges = (0 until unlockedCount).toList()
    val lockedBadges = (unlockedCount until totalCount).toList()

    val unlockedRows = unlockedBadges.chunked(3)
    val lockedRows = lockedBadges.chunked(3)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
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
            Spacer(Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Conquistadas",
                style = MaterialTheme.typography.titleMedium,
                color = Palette.text
            )
            Spacer(Modifier.height(16.dp))
        }

        items(unlockedRows) { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { badgeIndex ->
                    Box(modifier = Modifier.weight(1f)) {
                        BadgeCard(badgeIndex = badgeIndex, isUnlocked = true)
                    }
                }
                // Add empty boxes to keep the grid aligned if the last row is not full
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Bloqueadas",
                style = MaterialTheme.typography.titleMedium,
                color = Palette.text
            )
            Spacer(Modifier.height(16.dp))
        }

        items(lockedRows) { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { badgeIndex ->
                    Box(modifier = Modifier.weight(1f)) {
                        BadgeCard(badgeIndex = badgeIndex, isUnlocked = false)
                    }
                }
                // Add empty boxes to keep the grid aligned if the last row is not full
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun BadgeCard(badgeIndex: Int, isUnlocked: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = if (isUnlocked) Palette.divider else Palette.surface)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Badge ${badgeIndex + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isUnlocked) Palette.primary else Palette.textMuted
            )
        }
    }
}

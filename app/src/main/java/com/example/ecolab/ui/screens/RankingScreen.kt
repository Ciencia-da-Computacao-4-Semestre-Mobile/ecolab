package com.example.ecolab.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecolab.ui.theme.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen() {
    var selectedSegment by remember { mutableStateOf("Semanal") }
    val users = listOf( // Mock data
        "Maria S." to 1520,
        "João P." to 1450,
        "Você" to 1390,
        "Carlos A." to 1320,
        "Ana L." to 1280,
        "Pedro H." to 1100,
        "Sofia R." to 980,
        "Lucas M." to 950,
        "Beatriz C." to 910,
        "Gabriel F." to 880
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            val isSemanalSelected = selectedSegment == "Semanal"
            FilterChip(
                selected = isSemanalSelected,
                onClick = { selectedSegment = "Semanal" },
                label = { Text("Semanal") },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Palette.divider,
                    selectedLabelColor = Palette.primary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSemanalSelected,
                    borderColor = Palette.divider,
                    selectedBorderColor = Palette.divider
                )
            )
            val isMensalSelected = selectedSegment == "Mensal"
            FilterChip(
                selected = isMensalSelected,
                onClick = { selectedSegment = "Mensal" },
                label = { Text("Mensal") },
                shape = RoundedCornerShape(20.dp),
                 colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Palette.divider,
                    selectedLabelColor = Palette.primary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isMensalSelected,
                    borderColor = Palette.divider,
                    selectedBorderColor = Palette.divider
                )
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items(users.size) { index ->
                val user = users[index]
                val isCurrentUser = user.first == "Você"
                ListItem(
                    headlineContent = { Text(user.first, color = if(isCurrentUser) Palette.primary else Palette.text) },
                    leadingContent = { Text("#${index + 1}", style = MaterialTheme.typography.titleMedium, color = Palette.textMuted) },
                    trailingContent = { Text("${user.second} pts", color = Palette.textMuted) },
                    modifier = if (isCurrentUser) Modifier.border(1.dp, Palette.accent, RoundedCornerShape(2.dp)) else Modifier,
                    colors = ListItemDefaults.colors(containerColor = Palette.background)
                )
                HorizontalDivider(color = Palette.divider)
            }
        }
    }
}

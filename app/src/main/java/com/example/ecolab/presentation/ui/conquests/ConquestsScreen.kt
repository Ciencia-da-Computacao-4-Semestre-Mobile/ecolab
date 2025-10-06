package com.example.ecolab.presentation.ui.conquests

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.presentation.theme.EcoGreen
import com.example.ecolab.presentation.components.EcoTopBar
import com.example.ecolab.presentation.components.EcoCard
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConquestsScreen(viewModel: ConquestsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        EcoTopBar(title = "Conquistas")
        LazyVerticalGrid(
            modifier = Modifier.padding(16.dp),
            columns = GridCells.Fixed(2),
        ) {
            items(state.items) { item ->
                EcoCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column {
                        Text(item.title, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        val status = if (item.achieved) "Concluída" else "Em progresso"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (item.achieved) Icons.Filled.CheckCircle else Icons.Filled.HourglassBottom,
                                contentDescription = null,
                                tint = if (item.achieved) EcoGreen else MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(status, color = if (item.achieved) EcoGreen else MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}
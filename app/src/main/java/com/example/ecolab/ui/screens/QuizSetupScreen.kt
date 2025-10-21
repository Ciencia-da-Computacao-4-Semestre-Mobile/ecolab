package com.example.ecolab.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ecolab.R
import com.example.ecolab.ui.theme.Palette

data class SelectionItem(
    val name: String,
    val description: String? = null,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(onStartQuiz: (theme: String, gameMode: GameMode) -> Unit, onBack: () -> Unit) {
    var selectedGameMode by remember { mutableStateOf<SelectionItem?>(null) }
    var selectedTheme by remember { mutableStateOf<SelectionItem?>(null) }

    val gameModes = listOf(
        SelectionItem("Normal", "Responda no seu tempo.", Icons.Default.PlayArrow, Color(0xFF1E88E5)),
        SelectionItem("Speed Run", "Corra contra o relógio!", Icons.Default.Schedule, Color(0xFFE53935))
    )

    val themes = listOf(
        SelectionItem("Água", icon = Icons.Filled.WaterDrop, color = Color(0xFF2196F3)),
        SelectionItem("Energia", icon = Icons.Filled.Bolt, color = Color(0xFFFFC107)),
        SelectionItem("Fauna e Flora", icon = Icons.Filled.Forest, color = Color(0xFFF57C00)), // Laranja
        SelectionItem("Poluição", icon = Icons.Filled.Public, color = Color(0xFF795548)),
        SelectionItem("Reciclagem", icon = Icons.Filled.Recycling, color = Color(0xFF2E7D32)),
        SelectionItem("Sustentabilidade", icon = Icons.Filled.Eco, color = Color(0xFF7B1FA2)) // Roxo
    ).sortedBy { it.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Quiz", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Modo de Jogo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                gameModes.forEach { item ->
                    GameModeCard(
                        item = item,
                        isSelected = selectedGameMode == item,
                        onClick = { selectedGameMode = item },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Escolha um Tema",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(themes) {
                    ThemeCard(
                        item = it,
                        isSelected = selectedTheme == it,
                        onClick = { selectedTheme = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val theme = selectedTheme?.name ?: "Default"
                    val gameMode = if (selectedGameMode?.name == "Speed Run") GameMode.SPEEDRUN else GameMode.NORMAL
                    onStartQuiz(theme, gameMode)
                },
                enabled = selectedGameMode != null && selectedTheme != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedTheme?.color ?: Palette.primary,
                    disabledContainerColor = Palette.surface,
                    contentColor = Color.White,
                    disabledContentColor = Palette.textMuted
                )
            ) {
                Text("Começar o Quiz", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GameModeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = item.color
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) contentColor.copy(alpha = 0.2f) else contentColor.copy(alpha = 0.1f),
            contentColor = contentColor
        ),
        border = if (isSelected) BorderStroke(2.dp, contentColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = item.icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = contentColor)
            Column {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
                Text(item.description.orEmpty(), style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun ThemeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = item.color
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = contentColor.copy(alpha = if (isSelected) 0.25f else 0.1f),
            contentColor = contentColor
        ),
        border = if (isSelected) BorderStroke(2.dp, contentColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = item.icon, contentDescription = item.name, modifier = Modifier.size(40.dp), tint = contentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

package com.example.ecolab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.ui.components.NewGameModeCard
import com.example.ecolab.ui.components.NewThemeCard
import com.example.ecolab.ui.theme.Palette
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    onStartQuiz: (theme: String, gameMode: GameMode) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White, // Branco no topo
            Palette.background // Cinza claro do app
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prepare seu Desafio", color = Palette.text, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Palette.text)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Palette.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Escolha o Modo de Jogo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Palette.text,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.gameModes.forEach { gameMode ->
                    NewGameModeCard(
                        item = gameMode,
                        isSelected = uiState.selectedGameMode == gameMode,
                        onClick = { viewModel.selectGameMode(gameMode) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Navegue pelos Temas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Palette.text,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.themes) { theme ->
                    NewThemeCard(
                        item = theme,
                        isSelected = uiState.selectedTheme == theme,
                        onClick = { viewModel.selectTheme(theme) }
                    )
                }
            }

            Button(
                onClick = {
                    val theme = uiState.selectedTheme?.name ?: "Default"
                    val gameMode = if (uiState.selectedGameMode?.name == "Speed Run") GameMode.SPEEDRUN else GameMode.NORMAL
                    onStartQuiz(theme, gameMode)
                },
                enabled = uiState.selectedGameMode != null && uiState.selectedTheme != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = uiState.selectedTheme?.color?.copy(alpha = 0.9f) ?: Palette.primary,
                    disabledContainerColor = Palette.textMuted.copy(alpha = 0.3f),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
            ) {
                Text("INICIAR", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizSetupScreenPreview() {
    MaterialTheme {
        QuizSetupScreen(
            onStartQuiz = { _, _ -> },
            onBack = {}
        )
    }
}
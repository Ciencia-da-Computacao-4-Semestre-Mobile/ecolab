package com.example.ecolab.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.ecolab.ui.theme.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class QuizSetupState(
    val gameModes: List<SelectionItem> = emptyList(),
    val themes: List<SelectionItem> = emptyList(),
    val selectedGameMode: SelectionItem? = null,
    val selectedTheme: SelectionItem? = null
)

@HiltViewModel
class QuizSetupViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuizSetupState())
    val uiState: StateFlow<QuizSetupState> = _uiState.asStateFlow()

    init {
        loadQuizOptions()
    }

    private fun loadQuizOptions() {
        val gameModes = listOf(
            SelectionItem("Normal", "Responda no seu tempo.", Icons.Default.PlayArrow, Color(0xFF1E88E5)),
            SelectionItem("Speed Run", "Corra contra o relogio!", Icons.Default.Schedule, Color(0xFFE53935))
        )

        val themes = listOf(
            SelectionItem("Água", "Teste seus conhecimentos sobre a crise hídrica, conservação e poluição da água.", Icons.Filled.WaterDrop, Color(0xFF2196F3)),
            SelectionItem("Energia", "Aprenda sobre fontes de energia renováveis e não renováveis, e como economizar energia.", Icons.Filled.Bolt, Color(0xFFFFC107)),
            SelectionItem("Fauna e Flora", "Descubra a importância da biodiversidade, os biomas brasileiros e os animais em extinção.", Icons.Filled.Forest, Color(0xFFF57C00)),
            SelectionItem("Poluição", "Entenda os tipos de poluição, suas causas e consequências para o meio ambiente e a saúde.", Icons.Filled.Public, Color(0xFF795548)),
            SelectionItem("Reciclagem", "Saiba como separar o lixo, os tipos de materiais recicláveis e a importância da coleta seletiva.", Icons.Filled.Recycling, Color(0xFF2E7D32)),
            SelectionItem("Sustentabilidade", "Explore os conceitos de desenvolvimento sustentável, consumo consciente e pegada ecológica.", Icons.Filled.Eco, Color(0xFF7B1FA2))
        ).sortedBy { it.name }

        _uiState.value = QuizSetupState(gameModes = gameModes, themes = themes)
    }

    fun selectGameMode(gameMode: SelectionItem) {
        _uiState.update { it.copy(selectedGameMode = gameMode) }
    }

    fun selectTheme(theme: SelectionItem) {
        _uiState.update { it.copy(selectedTheme = theme) }
    }
}
package com.example.ecolab.feature.achievements

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
)

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList()
)

@HiltViewModel
class AchievementsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        _uiState.value = AchievementsUiState(
            achievements = listOf(
                Achievement(1, "Primeira Coleta", "Realize sua primeira coleta de recicláveis.", true),
                Achievement(2, "Rei do Plástico", "Recicle 50kg de plástico.", true),
                Achievement(3, "Mestre do Vidro", "Recicle 100kg de vidro.", true),
                Achievement(4, "Campeão do Papel", "Recicle 200kg de papel.", false),
                Achievement(5, "Amigo do Bairro", "Visite 5 ecopontos diferentes.", true),
                Achievement(6, "Explorador Urbano", "Visite 10 ecopontos diferentes.", false),
                Achievement(7, "Guardião do Metal", "Recicle 20kg de metal.", true),
                Achievement(8, "Missão Cumprida", "Complete 10 missões do dia.", false)
            )
        )
    }
}

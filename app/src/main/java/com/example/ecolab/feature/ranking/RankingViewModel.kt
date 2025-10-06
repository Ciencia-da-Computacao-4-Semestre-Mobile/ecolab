package com.example.ecolab.feature.ranking

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class RankingUiState(
    val users: List<RankedUser> = emptyList(),
    val selectedTabIndex: Int = 0 // 0 for Weekly, 1 for General
)

data class RankedUser(
    val id: Int,
    val name: String,
    val points: Int,
    val position: Int
)

@HiltViewModel
class RankingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState

    init {
        loadWeeklyRanking()
    }

    fun onTabSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
        if (index == 0) {
            loadWeeklyRanking()
        } else {
            loadGeneralRanking()
        }
    }

    private fun loadWeeklyRanking() {
        _uiState.value = _uiState.value.copy(
            users = listOf(
                RankedUser(1, "Maria Silva", 1250, 1),
                RankedUser(2, "João Santos", 1100, 2),
                RankedUser(3, "Ana Pereira", 980, 3),
                RankedUser(4, "Carlos Souza", 850, 4),
                 RankedUser(5, "Você", 750, 5) // Assuming current user
            )
        )
    }

    private fun loadGeneralRanking() {
         _uiState.value = _uiState.value.copy(
            users = listOf(
                RankedUser(1, "Maria Silva", 25500, 1),
                RankedUser(3, "Ana Pereira", 24000, 2),
                RankedUser(2, "João Santos", 23100, 3),
                 RankedUser(5, "Você", 22000, 4), // Assuming current user
                RankedUser(4, "Carlos Souza", 21500, 5)
            ).sortedByDescending { it.points }.mapIndexed { index, user -> user.copy(position = index + 1) }
        )
    }
}

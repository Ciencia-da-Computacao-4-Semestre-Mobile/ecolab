package com.example.ecolab.feature.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.model.RankedUser
import com.example.ecolab.data.repository.RankingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankingUiState(
    val users: List<RankedUser> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val repository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRanking()
    }

    private fun loadRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val users = repository.getRanking()
            _uiState.update { it.copy(users = users, isLoading = false) }
        }
    }
}

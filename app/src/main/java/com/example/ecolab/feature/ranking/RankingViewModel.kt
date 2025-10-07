package com.example.ecolab.feature.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.model.RankedUser
import com.example.ecolab.data.repository.RankingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RankingUiState(
    val users: List<RankedUser> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RankingViewModel @Inject constructor(
    repository: RankingRepository
) : ViewModel() {

    val uiState: StateFlow<RankingUiState> = repository.ranking
        .map { users -> RankingUiState(users = users, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RankingUiState(isLoading = true)
        )
}

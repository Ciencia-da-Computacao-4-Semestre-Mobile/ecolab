package com.example.ecolab.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.data.repository.CollectionPointRepository
import com.example.ecolab.data.repository.UserProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Data class for the UI state
data class HomeUiState(
    val points: List<CollectionPoint> = emptyList(),
    val isPlasticMissionCompleted: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    collectionPointRepository: CollectionPointRepository,
    userProgressRepository: UserProgressRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =  combine(
        collectionPointRepository.getAllPoints(),
        userProgressRepository.achievements // Use the new 'achievements' flow
    ) { points, achievements ->
        // Find the plastic mission in the list and check if it's unlocked.
        val plasticMission = achievements.find { it.id == "achievement_plastic_pioneer" }
        val isMissionCompleted = plasticMission?.isUnlocked ?: false

        HomeUiState(
            points = points,
            isPlasticMissionCompleted = isMissionCompleted,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(isLoading = true)
    )
}

package com.example.ecolab.feature.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.model.Achievement
import com.example.ecolab.data.repository.UserProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    userProgressRepository: UserProgressRepository
) : ViewModel() {

    val achievements: StateFlow<List<Achievement>> = userProgressRepository.achievements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}

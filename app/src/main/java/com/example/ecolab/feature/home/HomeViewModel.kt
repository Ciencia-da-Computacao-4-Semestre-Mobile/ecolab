package com.example.ecolab.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.repository.AchievementsRepository
import com.example.ecolab.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val achievementsRepository: AchievementsRepository
) : ViewModel() {

    private val _quizProgress = quizRepository.getQuizProgress()
    private val _achievementsProgress = achievementsRepository.getAchievementsProgress()

    val uiState: StateFlow<HomeUiState> = combine(
        _quizProgress,
        _achievementsProgress
    ) { quizProgress, achievementsProgress ->
        HomeUiState(
            quizProgress = quizProgress.progress,
            quizProgressText = quizProgress.progressText,
            quizCompleted = quizProgress.completed,
            achievementsProgress = achievementsProgress.progress,
            achievementsProgressText = achievementsProgress.progressText,
            achievementsCompleted = achievementsProgress.completed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}

data class HomeUiState(
    val quizProgress: Float = 0f,
    val quizProgressText: String = "",
    val quizCompleted: Boolean = false,
    val achievementsProgress: Float = 0f,
    val achievementsProgressText: String = "",
    val achievementsCompleted: Boolean = false
)

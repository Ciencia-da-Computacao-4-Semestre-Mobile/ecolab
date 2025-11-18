package com.example.ecolab.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.repository.AchievementsRepository
import com.example.ecolab.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _totalPoints = MutableStateFlow(0)

    init {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .addSnapshotListener { snapshot, _ ->
                    val p = snapshot?.getLong("totalPoints")?.toInt() ?: 0
                    _totalPoints.value = p
                }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _quizProgress,
        _achievementsProgress,
        _totalPoints
    ) { quizProgress, achievementsProgress, points ->
        HomeUiState(
            quizProgress = quizProgress.progress,
            quizProgressText = quizProgress.progressText,
            quizCompleted = quizProgress.completed,
            achievementsProgress = achievementsProgress.progress,
            achievementsProgressText = achievementsProgress.progressText,
            achievementsCompleted = achievementsProgress.completed,
            totalPoints = points
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
    val achievementsCompleted: Boolean = false,
    val totalPoints: Int = 1250,
    val levelProgress: Float = 0.75f,
    val levelText: String = "Nível 3 - 75% para o próximo nível"
)
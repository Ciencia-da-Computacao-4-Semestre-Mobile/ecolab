package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.data.repository.UserProgressRepository
import com.example.ecolab.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val totalPoints: Int = 0,
    val articlesRead: Int = 0,
    val quizzesDone: Int = 0,
    val achievementsUnlocked: Int = 0,
    val level: Int = 1,
    val levelProgress: Float = 0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val userData = userRepository.getUser(user.uid)

                _state.update {
                    it.copy(
                        displayName = user.displayName ?: userData?.name ?: "",
                        email = user.email ?: userData?.email ?: "",
                        photoUrl = user.photoUrl?.toString(),
                        totalPoints = calculateTotalPoints(userData),
                        articlesRead = calculateArticlesRead(userData),
                        quizzesDone = calculateQuizzesDone(userData),
                        achievementsUnlocked = calculateAchievementsUnlocked(userData),
                        level = calculateLevel(userData),
                        levelProgress = calculateLevelProgress(userData),
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateTotalPoints(userData: com.example.ecolab.data.model.User?): Int {
        // Lógica para calcular pontos totais baseado em quizzes, conquistas, etc.
        return userData?.favoritedPoints?.size?.times(10) ?: 0
    }

    private fun calculateArticlesRead(userData: com.example.ecolab.data.model.User?): Int {
        // Lógica para calcular artigos lidos
        return 0 // Implementar quando houver sistema de artigos
    }

    private fun calculateQuizzesDone(userData: com.example.ecolab.data.model.User?): Int {
        // Lógica para calcular quizzes feitos
        return 0 // Implementar quando houver sistema de quizzes
    }

    private fun calculateAchievementsUnlocked(userData: com.example.ecolab.data.model.User?): Int {
        // Lógica para calcular conquistas desbloqueadas
        return userData?.unlockedAchievements?.size ?: 0
    }

    private fun calculateLevel(userData: com.example.ecolab.data.model.User?): Int {
        // Lógica para calcular nível baseado em pontos
        val points = calculateTotalPoints(userData)
        return (points / 100) + 1
    }

    private fun calculateLevelProgress(userData: com.example.ecolab.data.model.User?): Float {
        // Lógica para calcular progresso para próximo nível
        val points = calculateTotalPoints(userData)
        return (points % 100) / 100f
    }
}
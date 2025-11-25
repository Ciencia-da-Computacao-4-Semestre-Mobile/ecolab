package com.example.ecolab.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.data.repository.AchievementsRepository
import com.example.ecolab.data.repository.QuizRepository
import com.example.ecolab.data.repository.UserRepository
import com.example.ecolab.model.Rarity
import com.example.ecolab.model.StoreCategory
import com.example.ecolab.model.StoreItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val achievementsRepository: AchievementsRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _quizProgress = quizRepository.getQuizProgress()
    private val _achievementsProgress = achievementsRepository.getAchievementsProgress()

    private val _user = authRepository.getCurrentUser()?.uid?.let {
        userRepository.getUserFlow(it)
    } ?: MutableStateFlow(null)

    val uiState: StateFlow<HomeUiState> = combine(
        _quizProgress,
        _achievementsProgress,
        _user
    ) { quizProgress, achievementsProgress, user ->
        val storeItems = getHardcodedItems()
        val equippedAvatarId = user?.equippedItemsMap?.get(StoreCategory.AVATAR.name)
        val equippedAvatar = storeItems.find { it.id == equippedAvatarId }

        HomeUiState(
            quizProgress = quizProgress.progress,
            quizProgressText = quizProgress.progressText,
            quizCompleted = quizProgress.completed,
            achievementsProgress = achievementsProgress.progress,
            achievementsProgressText = achievementsProgress.progressText,
            achievementsCompleted = achievementsProgress.completed,
            totalPoints = user?.totalPoints ?: 0,
            levelProgress = user?.let { (it.totalPoints % 100) / 100f } ?: 0f,
            levelText = user?.let { "Nível ${(it.totalPoints / 100) + 1} - ${it.totalPoints % 100}% para o próximo nível" } ?: "Nível 1 - 0% para o próximo nível",
            avatar = equippedAvatar?.drawableRes ?: user?.photoUrl,
            userName = user?.name
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    private fun getHardcodedItems(): List<StoreItem> {
        return listOf(
            StoreItem("avatar_nature_1", "Guardião da Floresta", "Avatar com tema de floresta tropical", 150, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.COMMON, "🌳", drawableRes = com.example.ecolab.R.drawable.avatar_02),
            StoreItem("avatar_nature_2", "Espírito do Verde", "Avatar com aura natural", 180, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.COMMON, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_03),
            StoreItem("avatar_nature_3", "Filho da Terra", "Avatar com conexão profunda com a natureza", 120, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.COMMON, "🌱", drawableRes = com.example.ecolab.R.drawable.avatar_04),
            StoreItem("avatar_nature_4", "Aprendiz Verde", "Avatar iniciante na jornada ecológica", 80, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.COMMON, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_05),
            StoreItem("avatar_nature_5", "Herói da Mata", "Avatar protetor das florestas", 200, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.COMMON, "🌲", drawableRes = com.example.ecolab.R.drawable.avatar_06),
            StoreItem("avatar_nature_6", "Avatar Ancião", "Avatar com sabedoria da natureza", 350, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.RARE, "🍃", drawableRes = com.example.ecolab.R.drawable.avatar_07),
            StoreItem("avatar_nature_7", "Druida Moderno", "Avatar com poderes naturais antigos", 400, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.RARE, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_08),
            StoreItem("avatar_nature_8", "Guardião das Águas", "Avatar protetor dos oceanos", 300, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.RARE, "🌊", drawableRes = com.example.ecolab.R.drawable.avatar_09),
            StoreItem("avatar_nature_9", "Senhor dos Ventos", "Avatar com domínio dos ventos limpos", 380, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.RARE, "💨", drawableRes = com.example.ecolab.R.drawable.avatar_10),
            StoreItem("avatar_nature_10", "Curandeiro da Terra", "Avatar com poder de curar a natureza", 320, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.RARE, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_11),
            StoreItem("avatar_nature_11", "Avatar Elemental", "Avatar mestre dos quatro elementos", 650, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.EPIC, "🔥🌊🌪️🌍", drawableRes = com.example.ecolab.R.drawable.avatar_12),
            StoreItem("avatar_nature_12", "Guardião Sagrado", "Avatar protetor de todos os ecossistemas", 700, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.EPIC, "🌟", drawableRes = com.example.ecolab.R.drawable.avatar_13),
            StoreItem("avatar_nature_13", "Espírito da Floresta", "Avatar uma com a floresta", 550, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.EPIC, "🌳✨", drawableRes = com.example.ecolab.R.drawable.avatar_14),
            StoreItem("avatar_nature_14", "Avatar da Harmonia", "Avatar que equilibra natureza e tecnologia", 600, com.example.ecolab.model.StoreCategory.AVATAR, Rarity.EPIC, "🌿⚡", drawableRes = com.example.ecolab.R.drawable.avatar_15),
        )
    }
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
    val levelText: String = "Nível 3 - 75% para o próximo nível",
    val avatar: Any? = null,
    val userName: String? = null
)

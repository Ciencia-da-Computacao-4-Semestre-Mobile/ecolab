package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.data.model.User
import com.example.ecolab.data.repository.AchievementsRepository
import com.example.ecolab.data.repository.UserProgressRepository
import com.example.ecolab.data.repository.UserRepository
import com.example.ecolab.model.Rarity
import com.example.ecolab.model.StoreCategory
import com.example.ecolab.model.StoreItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: Any? = null,
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
    private val userProgressRepository: UserProgressRepository,
    private val achievementsRepository: AchievementsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            val authUser = authRepository.getCurrentUser()
            if (authUser != null) {
                userRepository.getUserFlow(authUser.uid).collectLatest { userData ->
                    val storeItems = getHardcodedItems()
                    val equippedAvatarId = userData?.equippedItemsMap?.get(StoreCategory.AVATAR.name)
                    val equippedAvatar = storeItems.find { it.id == equippedAvatarId }
                    val unlockedCount = userData?.unlockedAchievements?.size ?: 0


                    _state.update {
                        it.copy(
                            displayName = authUser.displayName ?: userData?.name ?: "",
                            email = authUser.email ?: userData?.email ?: "",
                            photoUrl = equippedAvatar?.drawableRes ?: authUser.photoUrl,
                            totalPoints = userData?.totalPoints ?: 0,
                            articlesRead = calculateArticlesRead(userData),
                            quizzesDone = calculateQuizzesDone(userData),
                            achievementsUnlocked = unlockedCount,
                            level = calculateLevel(userData),
                            levelProgress = calculateLevelProgress(userData),
                            isLoading = false
                        )
                    }
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateArticlesRead(userData: User?): Int {
        return 0
    }

    private fun calculateQuizzesDone(userData: User?): Int {
        return 0
    }

    private fun calculateLevel(userData: User?): Int {
        val points = userData?.totalPoints ?: 0
        return (points / 100) + 1
    }

    private fun calculateLevelProgress(userData: User?): Float {
        val points = userData?.totalPoints ?: 0
        return (points % 100) / 100f
    }

    private fun getHardcodedItems(): List<StoreItem> {
        return listOf(
            StoreItem("avatar_nature_1", "Guardião da Floresta", "Avatar com tema de floresta tropical", 150, StoreCategory.AVATAR, Rarity.COMMON, "🌳", drawableRes = com.example.ecolab.R.drawable.avatar_02),
            StoreItem("avatar_nature_2", "Espírito do Verde", "Avatar com aura natural", 180, StoreCategory.AVATAR, Rarity.COMMON, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_03),
            StoreItem("avatar_nature_3", "Filho da Terra", "Avatar com conexão profunda com a natureza", 120, StoreCategory.AVATAR, Rarity.COMMON, "🌱", drawableRes = com.example.ecolab.R.drawable.avatar_04),
            StoreItem("avatar_nature_4", "Aprendiz Verde", "Avatar iniciante na jornada ecológica", 80, StoreCategory.AVATAR, Rarity.COMMON, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_05),
            StoreItem("avatar_nature_5", "Herói da Mata", "Avatar protetor das florestas", 200, StoreCategory.AVATAR, Rarity.COMMON, "🌲", drawableRes = com.example.ecolab.R.drawable.avatar_06),
            StoreItem("avatar_nature_6", "Avatar Ancião", "Avatar com sabedoria da natureza", 350, StoreCategory.AVATAR, Rarity.RARE, "🍃", drawableRes = com.example.ecolab.R.drawable.avatar_07),
            StoreItem("avatar_nature_7", "Druida Moderno", "Avatar com poderes naturais antigos", 400, StoreCategory.AVATAR, Rarity.RARE, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_08),
            StoreItem("avatar_nature_8", "Guardião das Águas", "Avatar protetor dos oceanos", 300, StoreCategory.AVATAR, Rarity.RARE, "🌊", drawableRes = com.example.ecolab.R.drawable.avatar_09),
            StoreItem("avatar_nature_9", "Senhor dos Ventos", "Avatar com domínio dos ventos limpos", 380, StoreCategory.AVATAR, Rarity.RARE, "💨", drawableRes = com.example.ecolab.R.drawable.avatar_10),
            StoreItem("avatar_nature_10", "Curandeiro da Terra", "Avatar com poder de curar a natureza", 320, StoreCategory.AVATAR, Rarity.RARE, "🌿", drawableRes = com.example.ecolab.R.drawable.avatar_11),
            StoreItem("avatar_nature_11", "Avatar Elemental", "Avatar mestre dos quatro elementos", 650, StoreCategory.AVATAR, Rarity.EPIC, "🔥🌊🌪️🌍", drawableRes = com.example.ecolab.R.drawable.avatar_12),
            StoreItem("avatar_nature_12", "Guardião Sagrado", "Avatar protetor de todos os ecossistemas", 700, StoreCategory.AVATAR, Rarity.EPIC, "🌟", drawableRes = com.example.ecolab.R.drawable.avatar_13),
            StoreItem("avatar_nature_13", "Espírito da Floresta", "Avatar uma com a floresta", 550, StoreCategory.AVATAR, Rarity.EPIC, "🌳✨", drawableRes = com.example.ecolab.R.drawable.avatar_14),
            StoreItem("avatar_nature_14", "Avatar da Harmonia", "Avatar que equilibra natureza e tecnologia", 600, StoreCategory.AVATAR, Rarity.EPIC, "🌿⚡", drawableRes = com.example.ecolab.R.drawable.avatar_15),
        )
    }
}
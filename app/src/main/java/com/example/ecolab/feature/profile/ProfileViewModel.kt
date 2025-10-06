package com.example.ecolab.feature.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Data model for the user profile, as per the prompt
data class UserProfile(
    val name: String,
    val email: String,
    val points: Int
)

data class ProfileUiState(
    val userProfile: UserProfile? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMockUserProfile()
    }

    private fun loadMockUserProfile() {
        _uiState.value = ProfileUiState(
            userProfile = UserProfile(
                name = "Nicolas Freitas", // Placeholder name
                email = "nicolas.freitas@example.com", // Placeholder email
                points = 1250
            )
        )
    }
}

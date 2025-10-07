package com.example.ecolab.feature.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(val name: String, val email: String, val points: Int)

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(
        UserProfile(name = "Visitante", email = "visitante@ecolab.app", points = 0)
    )
    val userProfile = _userProfile.asStateFlow()
}

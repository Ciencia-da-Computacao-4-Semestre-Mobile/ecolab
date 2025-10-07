package com.example.ecolab.ui.navigation

import androidx.lifecycle.ViewModel
import com.example.ecolab.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState = authRepository.getAuthState()
}

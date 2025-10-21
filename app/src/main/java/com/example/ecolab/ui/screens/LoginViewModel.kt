package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val signInError: String? = null,
    val isSignInSuccessful: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.signInWithEmailAndPassword(
                email = state.value.email,
                password = state.value.password
            )
            _state.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isSignInSuccessful = true) }
                }
                is Result.Error -> {
                    _state.update { it.copy(signInError = result.message) }
                }
                else -> {}
            }
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.createUserWithEmailAndPassword(
                email = state.value.email,
                password = state.value.password
            )
            _state.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isSignInSuccessful = true) }
                }
                is Result.Error -> {
                    _state.update { it.copy(signInError = result.message) }
                }
                else -> {}
            }
        }
    }

    fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.signInWithGoogle(idToken)
            _state.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isSignInSuccessful = true) }
                }
                is Result.Error -> {
                    _state.update { it.copy(signInError = result.message) }
                }
                else -> {}
            }
        }
    }
}

package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun onSendPasswordReset(email: String) {
        if (email.isBlank()) {
            _state.value = _state.value.copy(error = "Por favor, insira seu email")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = _state.value.copy(error = "Por favor, insira um email válido")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.sendPasswordResetEmail(email)
            when (result) {
                is com.example.ecolab.core.util.Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                }
                is com.example.ecolab.core.util.Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                else -> {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)

data class PasswordRequirements(
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasNumber: Boolean = false,
    val hasSpecialCharacter: Boolean = false,
    val isLengthCorrect: Boolean = false,
) {
    fun allMet(): Boolean =
        hasUppercase && hasLowercase && hasNumber && hasSpecialCharacter && isLengthCorrect
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    sealed class RegisterEvent {
        object RegistrationSuccess : RegisterEvent()
        data class RegistrationFailed(val message: String) : RegisterEvent()
    }

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _passwordRequirements = MutableStateFlow(PasswordRequirements())
    val passwordRequirements = _passwordRequirements.asStateFlow()

    private val _eventChannel = Channel<RegisterEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
        validatePassword(password)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            _eventChannel.send(RegisterEvent.RegistrationSuccess)
                        }
                    } else {
                        viewModelScope.launch {
                            _eventChannel.send(RegisterEvent.RegistrationFailed(task.exception?.message ?: "Erro desconhecido no login com Google"))
                        }
                    }
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                auth.createUserWithEmailAndPassword(
                    _state.value.email,
                    _state.value.password
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            _eventChannel.send(RegisterEvent.RegistrationSuccess)
                        }
                    } else {
                        viewModelScope.launch {
                            val exception = task.exception
                            val message = when (exception) {
                                is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso."
                                else -> exception?.message ?: "Ocorreu um erro desconhecido."
                            }
                            _eventChannel.send(RegisterEvent.RegistrationFailed(message))
                        }
                    }
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _eventChannel.send(RegisterEvent.RegistrationFailed(e.message ?: "Ocorreu um erro inesperado."))
            }
        }
    }

    private fun validatePassword(password: String) {
        _passwordRequirements.update {
            it.copy(
                hasUppercase = password.any { char -> char.isUpperCase() },
                hasLowercase = password.any { char -> char.isLowerCase() },
                hasNumber = password.any { char -> char.isDigit() },
                hasSpecialCharacter = password.any { char -> !char.isLetterOrDigit() },
                isLengthCorrect = password.length >= 8
            )
        }
    }
}

package com.example.ecolab.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolab.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
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
        Log.d("RegisterViewModel", "onGoogleSignInResult called with idToken: ${idToken.take(10)}...")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                Log.d("RegisterViewModel", "Firebase credential created successfully")
                
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        Log.d("RegisterViewModel", "signInWithCredential completed: success=${task.isSuccessful}")
                        if (task.isSuccessful) {
                            // Criar documento do usuário no Firestore
                            val userId = auth.currentUser?.uid
                            val userEmail = auth.currentUser?.email
                            val userName = auth.currentUser?.displayName ?: "Usuário Google"
                            
                            Log.d("RegisterViewModel", "User authenticated - UID: $userId, Email: $userEmail, Name: $userName")
                            
                            if (userId != null && userEmail != null) {
                                viewModelScope.launch {
                                    try {
                                        userRepository.createUser(
                                            com.example.ecolab.data.model.User(
                                                id = userId,
                                                name = userName,
                                                email = userEmail,
                                                favoritedPoints = emptyList(),
                                                unlockedAchievements = emptyList()
                                            )
                                        )
                                        Log.d("RegisterViewModel", "User document created successfully")
                                        _eventChannel.send(RegisterEvent.RegistrationSuccess)
                                    } catch (e: Exception) {
                                        Log.e("RegisterViewModel", "Error creating user document", e)
                                        _eventChannel.send(RegisterEvent.RegistrationFailed("Erro ao salvar dados do usuário: ${e.message}"))
                                    }
                                }
                            } else {
                                Log.w("RegisterViewModel", "User authenticated but UID or email is null")
                                viewModelScope.launch {
                                    _eventChannel.send(RegisterEvent.RegistrationSuccess)
                                }
                            }
                        } else {
                            val exception = task.exception
                            Log.e("RegisterViewModel", "signInWithCredential failed", exception)
                            Log.e("RegisterViewModel", "Error details: ${exception?.javaClass?.simpleName} - ${exception?.message}")
                            
                            viewModelScope.launch {
                                val message = when (exception) {
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        Log.e("RegisterViewModel", "Invalid credentials error")
                                        "Credenciais inválidas do Google. Tente novamente."
                                    }
                                    is FirebaseAuthUserCollisionException -> {
                                        Log.e("RegisterViewModel", "User collision error")
                                        "Este e-mail já está associado a outra conta."
                                    }
                                    else -> {
                                        Log.e("RegisterViewModel", "Unknown error type: ${exception?.javaClass}")
                                        exception?.message ?: "Erro desconhecido no login com Google"
                                    }
                                }
                                _eventChannel.send(RegisterEvent.RegistrationFailed(message))
                            }
                        }
                        _state.update { it.copy(isLoading = false) }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("RegisterViewModel", "signInWithCredential failure listener", exception)
                        _state.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Unexpected error in onGoogleSignInResult", e)
                _state.update { it.copy(isLoading = false) }
                viewModelScope.launch {
                    _eventChannel.send(RegisterEvent.RegistrationFailed("Erro inesperado: ${e.message}"))
                }
            }
        }
    }

    fun onRegisterClick() {
        Log.d("RegisterViewModel", "Iniciando processo de cadastro")
        
        // Validação extra dos dados antes de tentar criar usuário
        val name = _state.value.name.trim()
        val email = _state.value.email.trim()
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword
        
        Log.d("RegisterViewModel", "Dados do cadastro - Nome: '$name', Email: '$email', Password length: ${password.length}, ConfirmPassword length: ${confirmPassword.length}")
        
        // Verificar se os campos estão válidos
        if (name.isEmpty()) {
            Log.e("RegisterViewModel", "Nome está vazio")
            viewModelScope.launch {
                _eventChannel.send(RegisterEvent.RegistrationFailed("Por favor, insira seu nome"))
            }
            return
        }
        
        if (email.isEmpty()) {
            Log.e("RegisterViewModel", "Email está vazio")
            viewModelScope.launch {
                _eventChannel.send(RegisterEvent.RegistrationFailed("Por favor, insira seu email"))
            }
            return
        }
        
        if (password.isEmpty()) {
            Log.e("RegisterViewModel", "Senha está vazia")
            viewModelScope.launch {
                _eventChannel.send(RegisterEvent.RegistrationFailed("Por favor, insira sua senha"))
            }
            return
        }
        
        if (password != confirmPassword) {
            Log.e("RegisterViewModel", "Senhas não coincidem")
            viewModelScope.launch {
                _eventChannel.send(RegisterEvent.RegistrationFailed("As senhas não coincidem"))
            }
            return
        }
        
        if (!passwordRequirements.value.allMet()) {
            Log.e("RegisterViewModel", "Senha não atende requisitos")
            viewModelScope.launch {
                _eventChannel.send(RegisterEvent.RegistrationFailed("A senha não atende todos os requisitos"))
            }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                Log.d("RegisterViewModel", "Criando usuário com email: $email")
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        Log.d("RegisterViewModel", "Tarefa de criação completada: ${task.isSuccessful}")
                        if (task.isSuccessful) {
                            // Criar documento do usuário no Firestore
                            val userId = auth.currentUser?.uid
                            Log.d("RegisterViewModel", "UID do usuário: $userId")
                            
                            if (userId != null) {
                                viewModelScope.launch {
                                    try {
                                        Log.d("RegisterViewModel", "Criando documento no Firestore")
                                        
                                        // Criar objeto User com validação extra
                                        val user = com.example.ecolab.data.model.User(
                                            id = userId,
                                            name = name,
                                            email = email,
                                            favoritedPoints = emptyList(),
                                            unlockedAchievements = emptyList()
                                        )
                                        
                                        Log.d("RegisterViewModel", "Objeto User criado: ID=$userId, Nome='$name', Email='$email'")
                                        
                                        userRepository.createUser(user)
                                        
                                        Log.d("RegisterViewModel", "Documento criado com sucesso")
                                        _eventChannel.send(RegisterEvent.RegistrationSuccess)
                                    } catch (e: Exception) {
                                        Log.e("RegisterViewModel", "Erro ao criar documento no Firestore", e)
                                        Log.e("RegisterViewModel", "Detalhes do erro: ${e.javaClass.simpleName} - ${e.message}")
                                        _eventChannel.send(RegisterEvent.RegistrationFailed("Erro ao salvar dados do usuário: ${e.message}"))
                                    }
                                }
                            } else {
                                Log.e("RegisterViewModel", "UID é nulo após criação bem-sucedida")
                                viewModelScope.launch {
                                    _eventChannel.send(RegisterEvent.RegistrationFailed("Erro ao obter ID do usuário"))
                                }
                            }
                        } else {
                            val exception = task.exception
                            Log.e("RegisterViewModel", "Erro ao criar usuário no Firebase Auth", exception)
                            viewModelScope.launch {
                                val message = when (exception) {
                                    is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso."
                                    else -> exception?.message ?: "Ocorreu um erro desconhecido."
                                }
                                _eventChannel.send(RegisterEvent.RegistrationFailed(message))
                            }
                        }
                        _state.update { it.copy(isLoading = false) }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("RegisterViewModel", "Falha na criação do usuário", exception)
                    }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Erro inesperado no processo de cadastro", e)
                Log.e("RegisterViewModel", "Tipo da exceção: ${e.javaClass.simpleName}")
                Log.e("RegisterViewModel", "Mensagem: ${e.message}")
                _state.update { it.copy(isLoading = false) }
                viewModelScope.launch {
                    _eventChannel.send(RegisterEvent.RegistrationFailed(e.message ?: "Ocorreu um erro inesperado."))
                }
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
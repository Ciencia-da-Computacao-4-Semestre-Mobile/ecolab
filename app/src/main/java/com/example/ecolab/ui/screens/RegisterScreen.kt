package com.example.ecolab.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.ui.theme.Palette
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by registerViewModel.state.collectAsState()
    val passwordRequirements by registerViewModel.passwordRequirements.collectAsState()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let {
                    registerViewModel.onGoogleSignInResult(it)
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Falha ao entrar com Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        registerViewModel.eventFlow.collect { event ->
            when (event) {
                is RegisterViewModel.RegisterEvent.RegistrationSuccess -> {
                    Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    onRegistrationSuccess()
                }
                is RegisterViewModel.RegisterEvent.RegistrationFailed -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val isFormValid = state.name.isNotBlank() &&
            state.email.isNotBlank() &&
            state.password.isNotBlank() &&
            state.confirmPassword.isNotBlank() &&
            state.password == state.confirmPassword &&
            passwordRequirements.allMet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Conta") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Palette.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { registerViewModel.onNameChange(it) },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    enabled = !state.isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { registerViewModel.onEmailChange(it) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !state.isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { registerViewModel.onPasswordChange(it) },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !state.isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { registerViewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirmar Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !state.isLoading
                )
                Spacer(modifier = Modifier.height(24.dp))

                PasswordRequirementList(requirements = passwordRequirements)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { registerViewModel.onRegisterClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.primary)
                ) {
                    Text("Cadastrar", color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f))
                    Text("ou", modifier = Modifier.padding(horizontal = 8.dp), color = Palette.textMuted)
                    Divider(modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Palette.surface),
                    border = BorderStroke(1.dp, Palette.textMuted)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Logo do Google",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cadastrar com o Google", color = Palette.text)
                    }
                }
            }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun PasswordRequirementList(requirements: PasswordRequirements) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PasswordRequirement(
            text = "Pelo menos 8 caracteres",
            isMet = requirements.isLengthCorrect
        )
        PasswordRequirement(
            text = "Uma letra maiúscula",
            isMet = requirements.hasUppercase
        )
        PasswordRequirement(
            text = "Uma letra minúscula",
            isMet = requirements.hasLowercase
        )
        PasswordRequirement(
            text = "Um número",
            isMet = requirements.hasNumber
        )
        PasswordRequirement(
            text = "Um caractere especial (@, #, $, etc.)",
            isMet = requirements.hasSpecialCharacter
        )
    }
}

@Composable
private fun PasswordRequirement(text: String, isMet: Boolean) {
    val color = if (isMet) Color(0xFF008000) else Color.Gray
    val icon = if (isMet) Icons.Default.Check else Icons.Default.Close

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Text(text, color = color, fontSize = 14.sp)
    }
}

package com.example.ecolab.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.ui.components.AnimatedParticles
import com.example.ecolab.ui.theme.Palette
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay

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

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    val backgroundColor = Color(0xFFDFE6DE)

    val buttonScale by animateFloatAsState(
        targetValue = if (state.isLoading) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "register_button_scale"
    )

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            Log.d("RegisterScreen", "Google Sign-In result received")
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("RegisterScreen", "Google account: ${account?.email}, idToken present: ${account?.idToken != null}")
                account?.idToken?.let { token ->
                    Log.d("RegisterScreen", "Calling onGoogleSignInResult with token")
                    registerViewModel.onGoogleSignInResult(token)
                } ?: run {
                    Log.e("RegisterScreen", "Google account or idToken is null")
                    Toast.makeText(context, "Erro ao obter token do Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("RegisterScreen", "Google Sign-In ApiException: statusCode=${e.statusCode}, message=${e.message}", e)
                when (e.statusCode) {
                    12500 -> Toast.makeText(context, "Erro 12500: Problema de configuração do Google Sign-In. Verifique o OAuth Consent Screen e as credenciais.", Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(context, "Falha ao entrar com Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterScreen", "Google Sign-In unexpected error", e)
                Toast.makeText(context, "Erro inesperado no Google Sign-In", Toast.LENGTH_SHORT).show()
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
            passwordRequirements.isLengthCorrect &&
            passwordRequirements.hasUppercase &&
            passwordRequirements.hasLowercase &&
            passwordRequirements.hasNumber &&
            passwordRequirements.hasSpecialCharacter

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f) + slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                onClick = onBackClick,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Palette.primary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(0.4f),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_ecolab_logo),
                                contentDescription = "Logo do EcoLab",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                            ) {
                                AnimatedParticles()
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Criar Conta",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Palette.text
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Junte-se à nossa comunidade sustentável",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Palette.textMuted
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + scaleIn(initialScale = 0.9f, animationSpec = tween(600, delayMillis = 200))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = state.name,
                                onValueChange = { registerViewModel.onNameChange(it) },
                                label = { Text("Nome completo") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Palette.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = state.email,
                                onValueChange = { registerViewModel.onEmailChange(it) },
                                label = { Text("Email") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Palette.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = state.password,
                                onValueChange = { registerViewModel.onPasswordChange(it) },
                                label = { Text("Senha") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Palette.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(Modifier.height(16.dp))

                            AnimatedVisibility(
                                visible = state.password.isNotEmpty(),
                                enter = expandVertically() + fadeIn(),
                                exit = fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Palette.primary.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "Requisitos da senha:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = Palette.textMuted
                                        )
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    PasswordRequirementItem("Mínimo 8 caracteres", passwordRequirements.isLengthCorrect)
                                    PasswordRequirementItem("Pelo menos uma letra maiúscula", passwordRequirements.hasUppercase)
                                    PasswordRequirementItem("Pelo menos uma letra minúscula", passwordRequirements.hasLowercase)
                                    PasswordRequirementItem("Pelo menos um número", passwordRequirements.hasNumber)
                                    PasswordRequirementItem("Pelo menos um caractere especial", passwordRequirements.hasSpecialCharacter)
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = state.confirmPassword,
                                onValueChange = { registerViewModel.onConfirmPasswordChange(it) },
                                label = { Text("Confirmar senha") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Palette.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                isError = state.confirmPassword.isNotBlank() && state.password != state.confirmPassword,
                                shape = RoundedCornerShape(16.dp)
                            )

                            AnimatedVisibility(
                                visible = state.confirmPassword.isNotBlank() && state.password != state.confirmPassword,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                Text(
                                    text = "As senhas não coincidem",
                                    color = Palette.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(32.dp))

                            Button(
                                onClick = { registerViewModel.onRegisterClick() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .scale(buttonScale),
                                enabled = isFormValid && !state.isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Palette.primary,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color(0xFFCCCCCC),
                                    disabledContentColor = Color(0xFF888888)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 8.dp,
                                    pressedElevation = 12.dp,
                                    disabledElevation = 0.dp
                                )
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        "Criar Conta",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Divider(
                                    modifier = Modifier.weight(1f),
                                    color = Palette.divider
                                )
                                Text(
                                    "ou",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Palette.textMuted
                                    )
                                )
                                Divider(
                                    modifier = Modifier.weight(1f),
                                    color = Palette.divider
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            OutlinedButton(
                                onClick = {
                                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(context.getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build()
                                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                    val signInIntent = googleSignInClient.signInIntent
                                    googleSignInLauncher.launch(signInIntent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Palette.text
                                ),
                                border = BorderStroke(1.dp, Palette.divider),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_google_logo),
                                        contentDescription = "Google",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Unspecified
                                    )
                                    Text(
                                        "Continuar com Google",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(32.dp))

                            Text(
                                "Ao criar uma conta, você concorda com nossos",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Palette.textMuted
                                ),
                                textAlign = TextAlign.Center
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(
                                    onClick = { /* TODO: Abrir termos de uso */ },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "Termos de Uso",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Palette.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                Text(
                                    "e",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Palette.textMuted
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                TextButton(
                                    onClick = { /* TODO: Abrir política de privacidade */ },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "Política de Privacidade",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Palette.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
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
private fun PasswordRequirementItem(text: String, isMet: Boolean) {
    val color = if (isMet) Palette.primary else Palette.textMuted
    val icon = if (isMet) Icons.Default.Check else Icons.Default.Close

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}
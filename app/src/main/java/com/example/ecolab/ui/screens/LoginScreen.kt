package com.example.ecolab.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
fun LoginScreen(
    onLogin: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
        label = "button_scale"
    )

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            Log.d("LoginScreen", "Google Sign-In result received")
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let {
                    viewModel.onGoogleSignInResult(it)
                } ?: Log.e("LoginScreen", "Google account or idToken is null")
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Google Sign-In ApiException: statusCode=${e.statusCode}", e)
            } catch (e: Exception) {
                Log.e("LoginScreen", "Google Sign-In unexpected error", e)
            }
        }
    )

    LaunchedEffect(state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            onLogin()
        }
    }

    LaunchedEffect(state.signInError) {
        state.signInError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Palette.primary,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + scaleIn(initialScale = 0.8f) + slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(800, easing = FastOutSlowInEasing)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.fillMaxWidth(0.6f), // Aumentado para 60% da largura
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
                            Spacer(Modifier.height(16.dp)) // Espaço menor entre logo e texto
                            Text(
                                "Bem-vindo ao EcoLab!",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Palette.text
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Sua jornada sustentável começa aqui",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Palette.textMuted
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp)) // Espaçamento ajustado

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + scaleIn(initialScale = 0.9f, animationSpec = tween(600, delayMillis = 200))
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                OutlinedTextField(
                                    value = state.email,
                                    onValueChange = viewModel::onEmailChange,
                                    label = { Text("E-mail") },
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
                                    onValueChange = viewModel::onPasswordChange,
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
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    visualTransformation = PasswordVisualTransformation(),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                Spacer(Modifier.height(8.dp))

                                TextButton(
                                    onClick = onForgotPasswordClick,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(
                                        "Esqueceu sua senha?",
                                        color = Palette.primary,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                Button(
                                    onClick = viewModel::onSignInClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .scale(buttonScale),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Palette.primary,
                                        contentColor = Color.White
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 8.dp
                                    )
                                ) {
                                    Text(
                                        "Entrar",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Divider(
                                        modifier = Modifier.weight(1f),
                                        color = Palette.outline.copy(alpha = 0.3f),
                                        thickness = 1.dp
                                    )
                                    Text(
                                        "ou",
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Palette.textMuted,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Divider(
                                        modifier = Modifier.weight(1f),
                                        color = Palette.outline.copy(alpha = 0.3f),
                                        thickness = 1.dp
                                    )
                                }

                                Spacer(Modifier.height(16.dp))

                                Surface(
                                    onClick = {
                                        val clientIdRes = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                                        val clientId = runCatching { if (clientIdRes != 0) context.getString(clientIdRes) else "" }.getOrDefault("")
                                        if (clientId.isBlank()) {
                                            Log.e("LoginScreen", "default_web_client_id ausente ou inválido")
                                            scope.launch { snackbarHostState.showSnackbar("Configuração do Google Sign-In ausente. Verifique google-services.json") }
                                            return@Surface
                                        }
                                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(clientId)
                                            .requestEmail()
                                            .build()
                                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White,
                                    shadowElevation = 4.dp,
                                    border = BorderStroke(1.dp, Palette.outline.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_google_logo),
                                            contentDescription = "Logo do Google",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Continuar com Google",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Palette.textDark,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }

                                Spacer(Modifier.height(24.dp))

                                OutlinedButton(
                                    onClick = onRegisterClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Palette.primary
                                    ),
                                    border = BorderStroke(2.dp, Palette.primary)
                                ) {
                                    Text(
                                        "Criar nova conta",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
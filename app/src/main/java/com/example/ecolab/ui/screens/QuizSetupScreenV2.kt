package com.example.ecolab.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.ui.screens.GameMode
import com.example.ecolab.ui.theme.*
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreenV2(
    onStartQuiz: (theme: String, gameMode: GameMode) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Animações de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Gradientes dinâmicos com base no tema selecionado
    val backgroundGradient = remember(uiState.selectedTheme?.color) {
        Brush.verticalGradient(
            colors = listOf(
                uiState.selectedTheme?.color?.copy(alpha = 0.1f) ?: Color.White,
                Palette.background,
                Palette.background.copy(alpha = 0.95f)
            )
        )
    }

    // Animação de brilho suave
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shine"
    )

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
            ) {
                TopAppBar(
                    title = { 
                        Text(
                            "Prepare seu Desafio",
                            color = Palette.text,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    },
                    navigationIcon = {
                        Surface(
                            onClick = onBack,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.size(48.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Palette.text,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        containerColor = Palette.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(paddingValues)
        ) {
            // Partículas animadas de fundo
            AnimatedParticles()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Seção Modo de Jogo com animação
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600))
                ) {
                    GameModeSection(uiState, viewModel)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Seção Temas com animação
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600, delayMillis = 200))
                ) {
                    ThemeSection(uiState, viewModel)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botão Start com animação épica
                AnimatedVisibility(
                    visible = isVisible && uiState.selectedGameMode != null && uiState.selectedTheme != null,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    StartQuizButton(
                        theme = uiState.selectedTheme,
                        onClick = {
                            val theme = uiState.selectedTheme?.name ?: "Default"
                            val gameMode = if (uiState.selectedGameMode?.name == "Speed Run") GameMode.SPEEDRUN else GameMode.NORMAL
                            onStartQuiz(theme, gameMode)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun GameModeSection(
    uiState: QuizSetupState,
    viewModel: QuizSetupViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "🎮 Escolha o Modo de Jogo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        uiState.gameModes.forEachIndexed { index, gameMode ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it * 2 },
                    animationSpec = tween(500, delayMillis = index * 100)
                )
            ) {
                GameModeCard(
                    item = gameMode,
                    isSelected = uiState.selectedGameMode == gameMode,
                    onClick = { viewModel.selectGameMode(gameMode) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeSection(
    uiState: QuizSetupState,
    viewModel: QuizSetupViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "🌿 Navegue pelos Temas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.themes.forEachIndexed { index, theme ->
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn() + fadeIn(
                        animationSpec = tween(400, delayMillis = index * 50)
                    )
                ) {
                    ThemeCard(
                        item = theme,
                        isSelected = uiState.selectedTheme == theme,
                        onClick = { viewModel.selectTheme(theme) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameModeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 8.dp,
        animationSpec = tween(300),
        label = "elevation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) item.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
            contentColor = item.color
        ),
        border = if (isSelected) BorderStroke(3.dp, item.color) else BorderStroke(1.dp, item.color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ícone com animação de pulsação
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(item.color.copy(alpha = 0.2f), CircleShape)
                    .border(2.dp, item.color.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = item.color
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = item.color
                )
                Text(
                    text = item.description.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Indicador de seleção animado
            AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Surface(
                    shape = CircleShape,
                    color = item.color,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selecionado",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "rotation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .scale(scale)
            .rotate(rotation)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) item.color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
            contentColor = item.color
        ),
        border = if (isSelected) BorderStroke(2.dp, item.color) else BorderStroke(1.dp, item.color.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 6.dp,
            pressedElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ícone com efeito de brilho
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(item.color.copy(alpha = 0.3f), CircleShape)
                    .border(2.dp, item.color.copy(alpha = 0.6f), CircleShape)
                    .blur(if (isSelected) 2.dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = item.color
                )
            }
            
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = item.color,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            // Indicador de seleção
            AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Surface(
                    shape = CircleShape,
                    color = item.color,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selecionado",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StartQuizButton(
    theme: SelectionItem?,
    onClick: () -> Unit
) {
    val buttonColor = theme?.color ?: Palette.primary
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .height(64.dp),
        shape = RoundedCornerShape(32.dp),
        color = buttonColor,
        shadowElevation = 16.dp,
        border = BorderStroke(3.dp, buttonColor.copy(alpha = borderAlpha))
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Efeito de brilho
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    "INICIAR DESAFIO",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Icon(
                    Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    // Animação de partículas flutuantes
    val particle1Y by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle1"
    )
    
    val particle2Y by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 1050f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle2"
    )
    
    val particle3Y by infiniteTransition.animateFloat(
        initialValue = -80f,
        targetValue = 1020f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle3"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Partícula 1
        Box(
            modifier = Modifier
                .offset(x = 50.dp, y = particle1Y.dp)
                .size(8.dp)
                .background(Palette.primary.copy(alpha = 0.3f), CircleShape)
                .blur(2.dp)
        )
        
        // Partícula 2
        Box(
            modifier = Modifier
                .offset(x = 250.dp, y = particle2Y.dp)
                .size(6.dp)
                .background(Palette.secondary.copy(alpha = 0.4f), CircleShape)
                .blur(1.5.dp)
        )
        
        // Partícula 3
        Box(
            modifier = Modifier
                .offset(x = 150.dp, y = particle3Y.dp)
                .size(10.dp)
                .background(Palette.accent.copy(alpha = 0.2f), CircleShape)
                .blur(3.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun QuizSetupScreenV2Preview() {
    EcoLabTheme {
        QuizSetupScreenV2(
            onStartQuiz = { _, _ -> },
            onBack = {}
        )
    }
}
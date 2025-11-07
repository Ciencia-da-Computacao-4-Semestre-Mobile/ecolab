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
    
    // AnimaÃ§Ãµes de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Gradientes dinÃ¢micos com base no tema selecionado
    val backgroundGradient = remember(uiState.selectedTheme?.color) {
        Brush.verticalGradient(
            colors = listOf(
                uiState.selectedTheme?.color?.copy(alpha = 0.1f) ?: Color.White,
                Palette.background,
                Palette.background.copy(alpha = 0.95f)
            )
        )
    }

    // Animação de brilho suave (simplificada)
    val shineAlpha by animateFloatAsState(
        targetValue = 0.5f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
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
                                fontSize = 24.sp,
                                style = MaterialTheme.typography.titleLarge
                            )
                    },
                    navigationIcon = {
                        Surface(
                            onClick = onBack,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.size(40.dp),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Palette.text,
                                    modifier = Modifier.size(20.dp)
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
            // PartÃ­culas animadas de fundo
            AnimatedParticles()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Seção Modo de Jogo com animação
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600))
                ) {
                    GameModeSection(uiState, viewModel)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Seção Temas com animação
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600, delayMillis = 200))
                ) {
                    ThemeSection(uiState, viewModel)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Start com animação
                AnimatedVisibility(
                    visible = isVisible && uiState.selectedGameMode != null && uiState.selectedTheme != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f)
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

                Spacer(modifier = Modifier.height(24.dp))
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
            text = " Escolha o Modo de Jogo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 12.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
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
            text = " Navegue pelos Temas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Palette.text,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 3
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
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) item.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
            contentColor = item.color
        ),
        border = if (isSelected) BorderStroke(2.dp, item.color) else BorderStroke(1.dp, item.color.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 10.dp else 5.dp,
            pressedElevation = 14.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Ícone simples
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(item.color.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, item.color.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = item.color
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = item.color
                )
                Text(
                    text = item.description.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            // Indicador de seleção simples
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = item.color,
                    modifier = Modifier.size(22.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeCard(
    item: SelectionItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .scale(scale),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) item.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
            contentColor = item.color
        ),
        border = if (isSelected) BorderStroke(2.dp, item.color) else BorderStroke(1.dp, item.color.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ícone simples
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(item.color.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, item.color.copy(alpha = 0.3f), CircleShape),
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

            // Indicador de seleção simples
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = item.color,
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selecionado",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
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

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(30.dp),
        color = buttonColor,
        shadowElevation = 8.dp,
        border = BorderStroke(2.dp, buttonColor.copy(alpha = 0.9f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    "INICIAR QUIZ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun AnimatedParticles() {
    // Partículas estáticas (sem animação infinita para evitar crash)
    Box(modifier = Modifier.fillMaxSize()) {
        // Partícula 1
        Box(
            modifier = Modifier
                .offset(x = 50.dp, y = 200.dp)
                .size(8.dp)
                .background(Palette.primary.copy(alpha = 0.3f), CircleShape)
                .blur(2.dp)
        )

        // Partícula 2
        Box(
            modifier = Modifier
                .offset(x = 250.dp, y = 400.dp)
                .size(6.dp)
                .background(Palette.secondary.copy(alpha = 0.4f), CircleShape)
                .blur(1.5.dp)
        )

        // Partícula 3
        Box(
            modifier = Modifier
                .offset(x = 150.dp, y = 600.dp)
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
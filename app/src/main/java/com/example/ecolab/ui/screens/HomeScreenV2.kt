package com.example.ecolab.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.feature.home.HomeUiState
import com.example.ecolab.feature.home.HomeViewModel
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette

@Composable
fun HomeScreenV2(
    onQuizClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fundo com gradiente (estilo quiz)
            val backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                    Palette.primary.copy(alpha = 0.1f),
                    Palette.background,
                    Palette.background
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundBrush)
            ) {
                // Cabeçalho animado
                AnimatedHeader()
                
                // Cards principais
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnimatedFeatureCards(uiState, onQuizClick, onAchievementsClick)
                }
            }
        }
    }
}

@Composable
private fun AnimatedHeader() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "header_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            )
    ) {
        // Gradient de fundo do cabeçalho
        val headerBrush = Brush.verticalGradient(
            colors = listOf(
                Palette.primary,
                Palette.primaryHover
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBrush)
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            // Avatar animado
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White, CircleShape)
                    .border(3.dp, Palette.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Olá, Eco Hero! 🌱",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Pronto para salvar o planeta hoje?",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AnimatedFeatureCards(
    uiState: HomeUiState,
    onQuizClick: () -> Unit,
    onAchievementsClick: () -> Unit
) {
    // Card do Quiz
    AnimatedFeatureCard(
        title = "Quiz Ambiental",
        description = "Teste seus conhecimentos sobre sustentabilidade",
        icon = Icons.Default.Quiz,
        color = Palette.quizIcon,
        progress = uiState.quizProgress,
        progressText = uiState.quizProgressText,
        completed = uiState.quizCompleted,
        onClick = onQuizClick
    )

    // Card de Conquistas
    AnimatedFeatureCard(
        title = "Conquistas",
        description = "Veja suas medalhas e progresso",
        icon = Icons.Default.EmojiEvents,
        color = Palette.achievementsIcon,
        progress = uiState.achievementsProgress,
        progressText = uiState.achievementsProgressText,
        completed = uiState.achievementsCompleted,
        onClick = onAchievementsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    progress: Float,
    progressText: String,
    completed: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animações suaves
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_elevation"
    )

    // Efeito de brilho no hover
    val hoverGlow by animateFloatAsState(
        targetValue = if (isPressed) 0.1f else 0f,
        animationSpec = tween(300),
        label = "hover_glow"
    )

    Box {
        // Efeito de brilho
        if (hoverGlow > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(1.05f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = hoverGlow),
                                color.copy(alpha = hoverGlow * 0.5f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }

        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = CardDefaults.cardColors(
                containerColor = Palette.surface
            ),
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ícone animado
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.1f))
                        .border(2.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Palette.text
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Palette.textMuted,
                        fontWeight = FontWeight.Medium
                    )

                    if (!completed) {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = color,
                            trackColor = color.copy(alpha = 0.2f)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = progressText,
                            fontSize = 12.sp,
                            color = Palette.textMuted,
                            modifier = Modifier.align(Alignment.End)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completo",
                                tint = Palette.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            Text(
                                text = "Completo!",
                                color = Palette.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Seta animada
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Avançar",
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenV2Preview() {
    EcoLabTheme {
        HomeScreenV2(
            onQuizClick = {},
            onAchievementsClick = {}
        )
    }
}
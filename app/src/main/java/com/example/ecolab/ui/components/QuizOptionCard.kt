package com.example.ecolab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizOptionCard(
    modifier: Modifier = Modifier,
    optionText: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit,
    themeColor: Color = Palette.primary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animações suaves - removido tilt, mantido apenas zoom
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.97f
            isHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    // Efeito de brilho suave no hover
    val hoverGlow by animateFloatAsState(
        targetValue = if (isHovered && !isAnswered) 0.2f else 0f,
        animationSpec = tween(300),
        label = "hover_glow"
    )

    val elevation by animateDpAsState(
        targetValue = when {
            isPressed -> 2.dp
            isHovered -> 12.dp
            isSelected -> 8.dp
            else -> 4.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "elevation"
    )

    val targetBorderColor = when {
        isAnswered && isCorrect -> Palette.success
        isSelected && isAnswered && !isCorrect -> Palette.error
        isSelected -> themeColor
        else -> Palette.textMuted.copy(alpha = 0.2f)
    }

    val targetBackgroundColor = when {
        isAnswered && isCorrect -> Palette.success.copy(alpha = 0.15f)
        isSelected && isAnswered && !isCorrect -> Palette.error.copy(alpha = 0.15f)
        else -> Palette.surface
    }

    val borderColor by animateColorAsState(targetValue = targetBorderColor, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = targetBackgroundColor, animationSpec = tween(300))

    // Efeito de brilho quando selecionado
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected && !isAnswered) 0.3f else 0f,
        animationSpec = tween(500),
        label = "glow"
    )

    Box {
        // Efeito de brilho no hover
        if (hoverGlow > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(1.05f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                themeColor.copy(alpha = hoverGlow * 0.3f),
                                themeColor.copy(alpha = hoverGlow * 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
        
        // Efeito de brilho de seleção
        if (glowAlpha > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(1.1f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                themeColor.copy(alpha = glowAlpha),
                                Color.Transparent
                            ),
                            radius = 100f
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }

        Card(
            onClick = { if (!isAnswered) onClick() },
            modifier = modifier
                .fillMaxWidth()
                .scale(scale)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            interactionSource = interactionSource,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo de seleção (Forma)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(2.dp, if(isSelected) themeColor else Palette.textMuted.copy(alpha = 0.5f), CircleShape)
                        .background(if(isSelected) themeColor.copy(alpha = 0.1f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if(isSelected) {
                       Icon(Icons.Default.Check, contentDescription = "Selecionado", tint = themeColor, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = optionText,
                    modifier = Modifier.weight(1f),
                    color = Palette.text
                )

                if (isAnswered) {
                    Spacer(modifier = Modifier.width(16.dp))
                    if (isCorrect) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Resposta correta",
                            tint = Palette.success
                        )
                    } else if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Resposta incorreta",
                            tint = Palette.error
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Correct Answer", showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
private fun QuizOptionCardCorrectPreview() {
    EcoLabTheme {
        QuizOptionCard(
            optionText = "A fotossíntese é o processo pelo qual as plantas convertem luz em energia.",
            isSelected = true,
            isCorrect = true,
            isAnswered = true,
            onClick = {},
            themeColor = Palette.primary
        )
    }
}

@Preview(name = "Wrong Answer", showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
private fun QuizOptionCardWrongPreview() {
    EcoLabTheme {
        QuizOptionCard(
            optionText = "A fotossíntese é o processo pelo qual as plantas respiram à noite.",
            isSelected = true,
            isCorrect = false,
            isAnswered = true,
            onClick = {},
            themeColor = Palette.primary
        )
    }
}
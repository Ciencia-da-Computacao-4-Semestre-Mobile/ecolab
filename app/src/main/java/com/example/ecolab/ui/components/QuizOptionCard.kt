package com.example.ecolab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val targetBorderColor = when {
        isSelected && isAnswered && isCorrect -> themeColor
        isSelected && isAnswered && !isCorrect -> Palette.error
        isSelected -> themeColor
        else -> Palette.textMuted.copy(alpha = 0.2f)
    }

    val targetBackgroundColor = when {
        isSelected && isAnswered && isCorrect -> themeColor.copy(alpha = 0.1f)
        isSelected && isAnswered && !isCorrect -> Palette.error.copy(alpha = 0.1f)
        else -> Color.White
    }

    val borderColor by animateColorAsState(targetValue = targetBorderColor, animationSpec = tween(300))
    val backgroundColor by animateColorAsState(targetValue = targetBackgroundColor, animationSpec = tween(300))

    Card(
        onClick = { if (!isAnswered) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
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

            if (isAnswered && isSelected) {
                Spacer(modifier = Modifier.width(16.dp))
                val icon = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close
                val tint = if (isCorrect) themeColor else Palette.error
                Icon(imageVector = icon, contentDescription = "Status da resposta", tint = tint)
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

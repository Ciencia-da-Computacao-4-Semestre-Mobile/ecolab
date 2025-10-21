package com.example.ecolab.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecolab.ui.components.QuizOptionCard
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette
import kotlinx.coroutines.delay

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

val quizQuestions = listOf(
    QuizQuestion(
        question = "Qual destes materiais NÃO é reciclável?",
        options = listOf("Garrafa PET", "Pilha", "Papelão", "Vidro"),
        correctAnswer = "Pilha"
    ),
    QuizQuestion(
        question = "Qual a cor do cesto de lixo para PLÁSTICO?",
        options = listOf("Amarelo", "Azul", "Verde", "Vermelho"),
        correctAnswer = "Vermelho"
    ),
    QuizQuestion(
        question = "O que significa a sigla ESG?",
        options = listOf("Environmental, Social and Governance", "Economic, Social and Global", "Environmental, Security and Global", "Efficient, Sustainable and Green"),
        correctAnswer = "Environmental, Social and Governance"
    )
)

private const val TIME_PER_QUESTION = 15

enum class GameMode { NORMAL, SPEEDRUN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onClose: () -> Unit = {},
    theme: String = "Default",
    gameMode: GameMode = GameMode.NORMAL
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(TIME_PER_QUESTION) }

    val progress by animateFloatAsState(
        targetValue = (currentQuestionIndex.toFloat() + 1) / quizQuestions.size,
        label = "Quiz Progress"
    )

    if (gameMode == GameMode.SPEEDRUN) {
        LaunchedEffect(key1 = currentQuestionIndex) {
            timeLeft = TIME_PER_QUESTION
            while (timeLeft > 0 && !isAnswered) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft == 0 && !isAnswered) {
                isAnswered = true
            }
        }
    }

    val themeColor = when (theme) {
        "Reciclagem" -> Color(0xFF2E7D32)
        "Energia" -> Color(0xFFFFC107)
        "Água" -> Color(0xFF2196F3)
        "Fauna e Flora" -> Color(0xFFF57C00) // Laranja
        "Poluição" -> Color(0xFF795548)
        "Sustentabilidade" -> Color(0xFF7B1FA2) // Roxo
        "ESG" -> Palette.achievementsIcon
        else -> Palette.primary
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            themeColor.copy(alpha = 0.2f),
            Palette.background,
            Palette.background
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(theme, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar Quiz")
                    }
                },
                actions = {
                    if (gameMode == GameMode.SPEEDRUN) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = if(timeLeft < 6 && timeLeft % 2 == 0) Palette.error else themeColor)
                            Text(text = "0:${timeLeft.toString().padStart(2, '0')}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
            if (currentQuestionIndex < quizQuestions.size) {
                val question = quizQuestions[currentQuestionIndex]

                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.medium),
                        color = themeColor,
                        trackColor = themeColor.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Questão ${currentQuestionIndex + 1} de ${quizQuestions.size}", style = MaterialTheme.typography.bodyMedium, color = Palette.textMuted)
                        Text(
                            text = "MODO: ${gameMode.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Palette.textMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.5f))

                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    question.options.forEach { option ->
                        QuizOptionCard(
                            optionText = option,
                            isSelected = (selectedOption == option),
                            isCorrect = (option == question.correctAnswer),
                            isAnswered = isAnswered,
                            onClick = { if (!isAnswered) selectedOption = option },
                            themeColor = themeColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (isAnswered) {
                                isAnswered = false
                                selectedOption = null
                                currentQuestionIndex++
                            } else {
                                if (selectedOption == question.correctAnswer) {
                                    score += 10
                                }
                                isAnswered = true
                            }
                        },
                        enabled = selectedOption != null || (gameMode == GameMode.SPEEDRUN && timeLeft == 0),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                    ) {
                        Text(if (isAnswered) "Próxima" else "Responder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Quiz finalizado!", style = MaterialTheme.typography.headlineMedium)
                    Text(text = "Sua pontuação: $score", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

data class QuizPreviewParams(val theme: String, val gameMode: GameMode)
class QuizPreviewProvider : CollectionPreviewParameterProvider<QuizPreviewParams>(
    listOf(
        QuizPreviewParams("Reciclagem", GameMode.SPEEDRUN),
        QuizPreviewParams("Fauna e Flora", GameMode.NORMAL) // Preview com a nova cor
    )
)

@Preview(showBackground = true, name = "Quiz Screen")
@Composable
private fun QuizScreenPreview(
    @PreviewParameter(QuizPreviewProvider::class) params: QuizPreviewParams
) {
    EcoLabTheme {
        QuizScreen(onClose = {}, theme = params.theme, gameMode = params.gameMode)
    }
}

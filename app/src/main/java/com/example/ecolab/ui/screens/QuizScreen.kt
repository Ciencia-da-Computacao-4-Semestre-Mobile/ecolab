package com.example.ecolab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

val quizQuestions = listOf(
    QuizQuestion(
        question = "Qual destes materiais é reciclável?",
        options = listOf("Garrafa PET", "Pilha", "Bituca de cigarro"),
        correctAnswer = "Garrafa PET"
    ),
    QuizQuestion(
        question = "Qual a cor do cesto de lixo para papel?",
        options = listOf("Amarelo", "Azul", "Verde"),
        correctAnswer = "Azul"
    ),
    QuizQuestion(
        question = "O que significa a sigla ESG?",
        options = listOf("Environmental, Social and Governance", "Economic, Social and Global", "Environmental, Security and Global"),
        correctAnswer = "Environmental, Social and Governance"
    )
)

@Composable
fun QuizScreen() {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableStateOf(0) }

    if (currentQuestionIndex < quizQuestions.size) {
        val question = quizQuestions[currentQuestionIndex]

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = question.question, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            question.options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    RadioButton(
                        selected = (selectedOption == option),
                        onClick = { selectedOption = option }
                    )
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedOption == question.correctAnswer) {
                        score += 10
                    }
                    selectedOption = null
                    currentQuestionIndex++
                },
                enabled = selectedOption != null
            ) {
                Text("Próxima")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Quiz finalizado!", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Sua pontuação: $score", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

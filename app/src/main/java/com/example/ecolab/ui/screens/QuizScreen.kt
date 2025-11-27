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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.json.Json
import com.example.ecolab.ui.screens.AiQuizData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp

data class ResultData(
    val message: String,
    val color: Color,
    val icon: ImageVector
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val theme: String
)

val quizQuestions = listOf(
    // Água
    QuizQuestion("Qual porcentagem da água do planeta é doce?", listOf("A) 3%", "B) 10%", "C) 25%", "D) 50%"), "A) 3%", "Água"),
    QuizQuestion("O que é água potável?", listOf("A) Água do mar", "B) Água própria para consumo", "C) Água poluída", "D) Água congelada"), "B) Água própria para consumo", "Água"),

    // Energia
    QuizQuestion("Qual das seguintes é uma fonte de energia renovável?", listOf("A) Carvão", "B) Petróleo", "C) Gás Natural", "D) Energia Solar"), "D) Energia Solar", "Energia"),
    QuizQuestion("O que são combustíveis fósseis?", listOf("A) Fontes de energia limpa", "B) Restos de plantas e animais decompostos", "C) Energia do vento", "D) Energia da água"), "B) Restos de plantas e animais decompostos", "Energia"),

    // Fauna e Flora
    QuizQuestion("Qual o maior bioma do Brasil?", listOf("A) Mata Atlântica", "B) Pampa", "C) Cerrado", "D) Amazônia"), "D) Amazônia", "Fauna e Flora"),
    QuizQuestion("Qual animal é um símbolo da fauna brasileira em risco de extinção?", listOf("A) Galinha", "B) Mico-leão-dourado", "C) Cachorro", "D) Gato"), "B) Mico-leão-dourado", "Fauna e Flora"),

    // Poluição
    QuizQuestion("O que causa a chuva ácida?", listOf("A) Excesso de CO2", "B) Poluentes do ar de fábricas e carros", "C) Derramamento de óleo", "D) Lixo nos rios"), "B) Poluentes do ar de fábricas e carros", "Poluição"),
    QuizQuestion("Qual o principal gás do efeito estufa?", listOf("A) Oxigênio", "B) Nitrogênio", "C) Dióxido de Carbono (CO2)", "D) Hélio"), "C) Dióxido de Carbono (CO2)", "Poluição"),

    // Reciclagem
    QuizQuestion("Qual a cor da lixeira para descarte de metal?", listOf("A) Azul", "B) Verde", "C) Vermelho", "D) Amarelo"), "D) Amarelo", "Reciclagem"),
    QuizQuestion("O que é compostagem?", listOf("A) Processo de queima de lixo", "B) Transformação de lixo orgânico em adubo", "C) Separação de plásticos", "D) Reciclagem de vidro"), "B) Transformação de lixo orgânico em adubo", "Reciclagem"),

    // Sustentabilidade
    QuizQuestion("O que são os 3 Rs da sustentabilidade?", listOf("A) Rir, Rezar, Reclamar", "B) Reduzir, Reutilizar, Reciclar", "C) Remediar, Remover, Replantar", "D) Rápido, Rasteiro, Real"), "B) Reduzir, Reutilizar, Reciclar", "Sustentabilidade"),
    QuizQuestion("O que é pegada de carbono?", listOf("A) Uma marca de sapato ecológico", "B) A quantidade de carbono emitida por uma pessoa ou empresa", "C) Um tipo de fóssil", "D) Um novo app de celular"), "B) A quantidade de carbono emitida por uma pessoa ou empresa", "Sustentabilidade")
)

private const val TIME_PER_QUESTION = 15

enum class GameMode { NORMAL, SPEEDRUN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onClose: () -> Unit = {},
    theme: String = "Default",
    gameMode: GameMode = GameMode.NORMAL,
    initialQuizJson: String? = null
) {
    val ctx = LocalContext.current
    val aiQuestions = remember(theme) {
        val prefs = ctx.getSharedPreferences("quiz_cache", android.content.Context.MODE_PRIVATE)
        val jsonStr = initialQuizJson ?: prefs.getString("latest_ai_quiz", null)
        if (!jsonStr.isNullOrBlank()) {
            try {
                val data = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true; allowTrailingComma = true }
                    .decodeFromString<AiQuizData>(jsonStr)
                data.questions.mapNotNull { q ->
                    val opts = q.options.map { it.trim() }.filter { it.isNotBlank() }
                    val idx = q.correctIndex.coerceIn(0, if (opts.isNotEmpty()) opts.size - 1 else 0)
                    if (opts.size >= 2) QuizQuestion(q.question.trim(), opts, opts[idx], theme) else null
                }
            } catch (_: Throwable) { emptyList() }
        } else emptyList()
    }
    val questionsForTheme = remember(theme, aiQuestions) {
        if (aiQuestions.isNotEmpty()) aiQuestions else when (theme) {
            "Aleatório" -> quizQuestions.shuffled().take(10)
            "Default" -> quizQuestions.shuffled().take(10)
            else -> quizQuestions.filter { it.theme == theme }.shuffled()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            val prefs = ctx.getSharedPreferences("quiz_cache", android.content.Context.MODE_PRIVATE)
            prefs.edit().remove("latest_ai_quiz").apply()
        }
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(TIME_PER_QUESTION) }

    val progress by animateFloatAsState(
        targetValue = (currentQuestionIndex.toFloat() + 1) / questionsForTheme.size,
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
        "Fauna e Flora" -> Color(0xFFF57C00)
        "Poluição" -> Color(0xFF795548)
        "Sustentabilidade" -> Color(0xFF7B1FA2)
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
            // ✅ SÓ MOSTRA TopAppBar durante o quiz (não na tela de resultado)
            if (currentQuestionIndex < questionsForTheme.size) {
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
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = "Timer",
                                    tint = if (timeLeft < 6 && timeLeft % 2 == 0) Palette.error else themeColor
                                )
                                Text(
                                    text = "0:${timeLeft.toString().padStart(2, '0')}",
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
            if (questionsForTheme.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nenhuma pergunta encontrada para o tema \"$theme\".",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onClose) {
                        Text("Voltar")
                    }
                }
            } else if (currentQuestionIndex < questionsForTheme.size) {
                val question = questionsForTheme[currentQuestionIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.medium),
                        color = themeColor,
                        trackColor = themeColor.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Questão ${currentQuestionIndex + 1} de ${questionsForTheme.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Palette.textMuted
                        )
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

                    val optionsShuffled = remember(currentQuestionIndex) { question.options.shuffled() }
                    optionsShuffled.forEach { option ->
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                    ) {
                        Text(
                            if (isAnswered) "Próxima" else "Responder",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Limpa cache ao finalizar o quiz
                val prefs = ctx.getSharedPreferences("quiz_cache", android.content.Context.MODE_PRIVATE)
                androidx.compose.runtime.LaunchedEffect(theme, questionsForTheme.size) {
                    val key = theme.trim().lowercase() + ":" + questionsForTheme.size
                    prefs.edit().remove("latest_ai_quiz").remove(key).commit()
                }
                QuizResultScreen(
                    score = score,
                    totalQuestions = questionsForTheme.size,
                    theme = theme,
                    onPlayAgain = {
                        currentQuestionIndex = 0
                        selectedOption = null
                        isAnswered = false
                        score = 0
                        timeLeft = TIME_PER_QUESTION
                    },
                    onBackToHome = onClose,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    totalQuestions: Int,
    theme: String,
    onPlayAgain: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val boost = context.getSharedPreferences("ecolab_prefs", android.content.Context.MODE_PRIVATE)
        .getInt("equipped_seal_effect_quiz_boost_percent", 0)
    val boostedScore = score + (score * boost) / 100
    val percentage = (boostedScore.toFloat() / (totalQuestions * 10)) * 100
    val animatedScore by animateIntAsState(
        targetValue = boostedScore,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "Score Animation"
    )

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "Percentage Animation"
    )

    val resultData = when {
        percentage >= 90 -> ResultData(
            "Parabéns! Você é um Expert em $theme! 🌟",
            Palette.primary,
            Icons.Default.Star
        )
        percentage >= 70 -> ResultData(
            "Muito bem! Você dominou o tema! 🎯",
            Palette.primary,
            Icons.Default.CheckCircle
        )
        percentage >= 50 -> ResultData(
            "Bom trabalho! Continue estudando! 📚",
            Palette.secondary,
            Icons.Default.School
        )
        percentage >= 30 -> ResultData(
            "Você está no caminho certo! 🌱",
            Palette.accent,
            Icons.Default.TrendingUp
        )
        else -> ResultData(
            "Não desista! Tente novamente! 💪",
            Palette.error,
            Icons.Default.Refresh
        )
    }

    LaunchedEffect(Unit) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val fs = FirebaseFirestore.getInstance()
                val doc = fs.collection("users").document(uid).get().await()
                val current = doc.getLong("totalPoints")?.toInt() ?: 0
                val newTotal = current + boostedScore
                fs.collection("users").document(uid).update("totalPoints", newTotal)
            }
        } catch (_: Exception) { }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated Icon
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "Icon Scale"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .background(
                        resultData.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(60.dp)
                    )
                    .border(
                        2.dp,
                        resultData.color.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = resultData.icon,
                    contentDescription = null,
                    tint = resultData.color,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Message
            val messageAlpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(1000, delayMillis = 500),
                label = "Message Alpha"
            )

            Text(
                text = resultData.message,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Palette.text,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(messageAlpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Score Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Palette.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = animatedScore.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = resultData.color
                        )
                        Text(
                            text = "/${totalQuestions * 10}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Palette.textMuted,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = animatedPercentage / 100,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = resultData.color,
                        trackColor = resultData.color.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${String.format("%.1f", animatedPercentage)}% de acerto",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Palette.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToHome,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Palette.text
                    )
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Home Quiz")
                }

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Palette.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again")
                }
            }
        }
    }
}

// Preview Providers
data class QuizPreviewParams(val theme: String, val gameMode: GameMode)

class QuizPreviewProvider : CollectionPreviewParameterProvider<QuizPreviewParams>(
    listOf(
        QuizPreviewParams("Reciclagem", GameMode.SPEEDRUN),
        QuizPreviewParams("Fauna e Flora", GameMode.NORMAL)
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

@Preview(showBackground = true, name = "Quiz Result - Expert")
@Composable
private fun QuizResultExpertPreview() {
    EcoLabTheme {
        QuizResultScreen(
            score = 90,
            totalQuestions = 10,
            theme = "Reciclagem",
            onPlayAgain = {},
            onBackToHome = {}
        )
    }
}

@Preview(showBackground = true, name = "Quiz Result - Good")
@Composable
private fun QuizResultGoodPreview() {
    EcoLabTheme {
        QuizResultScreen(
            score = 70,
            totalQuestions = 10,
            theme = "Energia",
            onPlayAgain = {},
            onBackToHome = {}
        )
    }
}

@Preview(showBackground = true, name = "Quiz Result - Needs Practice")
@Composable
private fun QuizResultNeedsPracticePreview() {
    EcoLabTheme {
        QuizResultScreen(
            score = 30,
            totalQuestions = 10,
            theme = "Água",
            onPlayAgain = {},
            onBackToHome = {}
        )
    }
}

@Composable
fun QuizAIScreen(quizViewModel: QuizViewModel = viewModel()) {
    var topic by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("10") }
    val state by quizViewModel.uiState.collectAsState()
    val isLoading = state is QuizUiState.Loading
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { quizViewModel.attachCache(ctx) }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            TextField(value = topic, onValueChange = { if (!isLoading) topic = it }, enabled = !isLoading, placeholder = { Text("Tema do quiz") })
            Spacer(Modifier.height(8.dp))
            TextField(value = count, onValueChange = { if (!isLoading) count = it }, enabled = !isLoading, placeholder = { Text("Quantidade") })
            Spacer(Modifier.height(8.dp))
            val canGenerate = !isLoading && topic.isNotBlank() && count.toIntOrNull() != null
            Button(enabled = canGenerate, onClick = { count.toIntOrNull()?.let { quizViewModel.generateQuiz(topic, it) } }) { Text("Gerar Quiz") }
            Spacer(Modifier.height(16.dp))
            when (val s = state) {
                is QuizUiState.Loading -> {
                    val progress = s.progress.coerceIn(0, 100)
                    Text("Preparando quiz… $progress%")
                    LinearProgressIndicator(progress = progress / 100f, modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.medium))
                }
                is QuizUiState.Error -> {
                    Text("Erro: ${s.error}")
                }
                is QuizUiState.Success -> {
                    if (s.quiz.questions.isEmpty()) {
                        Text("Nenhuma questão gerada.")
                    } else {
                        LazyColumn {
                            items(s.quiz.questions) { q ->
                                Text(q.question)
                                q.options.forEachIndexed { i, opt -> Text("${i + 1}. $opt") }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
                else -> {}
            }
        }
        if (isLoading) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f))) {
                androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
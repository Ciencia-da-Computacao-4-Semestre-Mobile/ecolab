package com.example.ecolab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun HomeScreen(
    onQuizClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Palette.background)
        ) {
            Header()
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DailyMissionCard()
                Shortcuts(uiState, onQuizClick = onQuizClick, onAchievementsClick = onAchievementsClick)
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Palette.primary)
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White, CircleShape)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Olá, Usuário!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Pronto para mais um dia sustentável!", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun DailyMissionCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Missão", tint = Palette.primary, modifier = Modifier.size(40.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Missão Diária", fontWeight = FontWeight.Bold, color = Palette.text)
                Text("Responda a 5 perguntas hoje e ganhe 50 pontos extras.", color = Palette.textMuted)
            }
        }
    }
}

@Composable
private fun Shortcuts(uiState: HomeUiState, onQuizClick: () -> Unit, onAchievementsClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ShortcutCard(
            title = "Quiz",
            icon = Icons.Default.Quiz,
            color = Palette.quizIcon,
            progress = uiState.quizProgress,
            progressText = uiState.quizProgressText,
            completed = uiState.quizCompleted,
            onClick = onQuizClick
        )
        ShortcutCard(
            title = "Conquistas",
            icon = Icons.Default.EmojiEvents,
            color = Palette.achievementsIcon,
            progress = uiState.achievementsProgress,
            progressText = uiState.achievementsProgressText,
            completed = uiState.achievementsCompleted,
            onClick = onAchievementsClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShortcutCard(
    title: String,
    icon: ImageVector,
    color: Color,
    progress: Float,
    progressText: String,
    completed: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Palette.text)
                Spacer(modifier = Modifier.height(8.dp))
                if (completed) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle, contentDescription = "Completo", tint = Palette.primary, modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Completo", color = Palette.primary, fontWeight = FontWeight.Bold)
                    }
                } else {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Palette.primary,
                        trackColor = Palette.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(progressText, fontSize = 12.sp, color = Palette.textMuted, modifier = Modifier.align(Alignment.End))
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Palette.textMuted)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    EcoLabTheme {
        HomeScreen(onQuizClick = {}, onAchievementsClick = {})
    }
}

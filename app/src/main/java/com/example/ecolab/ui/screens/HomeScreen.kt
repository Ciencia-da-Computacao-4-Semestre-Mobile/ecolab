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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ecolab.R
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette

@Composable
fun HomeScreen(
    onQuizClick: () -> Unit,
    onAchievementsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.background)
            .padding(16.dp)
    ) {
        Header()
        Spacer(modifier = Modifier.height(24.dp))
        DailyMissionCard()
        Spacer(modifier = Modifier.height(24.dp))
        Shortcuts(onQuizClick = onQuizClick, onAchievementsClick = onAchievementsClick)
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Olá, Usuário!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Palette.text)
            Text("Bem-vindo de volta!", fontSize = 16.sp, color = Palette.textMuted)
        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Quiz, contentDescription = "Missão", tint = Palette.primary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text("Missão Diária", fontWeight = FontWeight.Bold, color = Palette.text)
                Text("Responda a 5 perguntas hoje e ganhe 50 pontos extras.", color = Palette.textMuted)
            }
        }
    }
}

@Composable
private fun Shortcuts(onQuizClick: () -> Unit, onAchievementsClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ShortcutCard(
            title = "Quiz",
            icon = Icons.Default.Quiz,
            color = Palette.quizIcon,
            onClick = onQuizClick
        )
        ShortcutCard(
            title = "Conquistas",
            icon = Icons.Default.EmojiEvents,
            color = Palette.achievementsIcon,
            onClick = onAchievementsClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShortcutCard(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Palette.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Palette.text)
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

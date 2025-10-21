package com.example.ecolab.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditProfileClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.background)
    ) {
        ProfileHeader(
            displayName = state.displayName,
            email = state.email,
            photoUrl = state.photoUrl,
            onEditProfileClick = onEditProfileClick
        )
        ProgressSection()
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSignOutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Red
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Sair do Aplicativo", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileHeader(displayName: String, email: String, photoUrl: String?, onEditProfileClick: () -> Unit) {
    val greenGradient = Brush.verticalGradient(
        colors = listOf(Palette.primary.copy(alpha = 0.8f), Palette.primary)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(greenGradient, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initial = displayName.firstOrNull()?.uppercase() ?: email.firstOrNull()?.uppercase() ?: "U"
                Text(text = initial, fontSize = 48.sp, color = Palette.primary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(displayName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(email, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text("Iniciante", color = Color.White)
            }
            Button(
                onClick = onEditProfileClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Palette.text)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Editar", color = Palette.text)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProgressSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Meu Progresso", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Palette.text)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProgressCard(
                title = "Artigos Lidos",
                value = "0",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                color = Palette.surface,
                modifier = Modifier.weight(1f)
            )
            ProgressCard(
                title = "Quizzes Feitos",
                value = "0",
                icon = Icons.Default.Psychology,
                color = Palette.quizIcon.copy(alpha = 0.2f),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProgressCard(
                title = "Pontos Totais",
                value = "0",
                icon = Icons.Default.Star,
                color = Palette.achievementsIcon.copy(alpha = 0.2f),
                modifier = Modifier.weight(1f)
            )
            ProgressCard(
                title = "Conquistas",
                value = "0",
                icon = Icons.Default.EmojiEvents,
                color = Palette.achievementsIcon.copy(alpha = 0.4f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProgressCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = Palette.text.copy(alpha = 0.8f), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Palette.text)
            Text(title, fontSize = 14.sp, color = Palette.textMuted, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    EcoLabTheme {
        ProfileScreen(onEditProfileClick = {}, onSignOutClick = {})
    }
}

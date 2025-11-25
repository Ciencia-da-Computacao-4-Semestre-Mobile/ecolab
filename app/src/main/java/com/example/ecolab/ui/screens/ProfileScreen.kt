package com.example.ecolab.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import android.graphics.BitmapFactory
import com.example.ecolab.ui.components.UserAvatar
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditProfileClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onAchievementsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.background)
    ) {
        

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                displayName = state.displayName,
                email = state.email,
                photoUrl = state.photoUrl,
                onEditProfileClick = onEditProfileClick
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Palette.primary)
                }
            } else {
                StatsSection(
                    totalPoints = state.totalPoints,
                    articlesRead = state.articlesRead,
                    quizzesDone = state.quizzesDone,
                    achievementsUnlocked = state.achievementsUnlocked
                )

                Spacer(modifier = Modifier.height(24.dp))

                QuickActionsSection(
                    onSettingsClick = onSettingsClick,
                    onHelpClick = onHelpClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                AccountSection(
                    onSignOutClick = onSignOutClick
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    email: String,
    photoUrl: String?,
    onEditProfileClick: () -> Unit
) {
    val greenGradient = Brush.verticalGradient(
        colors = listOf(Palette.primary.copy(alpha = 0.9f), Palette.primary)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(greenGradient, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Container for Avatar + Seal
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            UserAvatar(
                modifier = Modifier.fillMaxSize(),
                displayName = displayName,
                email = email,
                photoUrl = photoUrl
            )

            // Seal logic, moved to the outer Box
            val prefsLocal = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("ecolab_prefs", android.content.Context.MODE_PRIVATE)
            var sealRes by remember { mutableStateOf(prefsLocal.getInt("equipped_seal_res_id", 0)) }
            var sealEmoji by remember { mutableStateOf(prefsLocal.getString("equipped_seal_emoji", "")) }
            DisposableEffect(prefsLocal) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
                    if (key == "equipped_seal_res_id") {
                        sealRes = sp.getInt("equipped_seal_res_id", 0)
                    }
                    if (key == "equipped_seal_emoji") {
                        sealEmoji = sp.getString("equipped_seal_emoji", "")
                    }
                }
                prefsLocal.registerOnSharedPreferenceChangeListener(listener)
                onDispose { prefsLocal.unregisterOnSharedPreferenceChangeListener(listener) }
            }
            val sealValid = sealRes != 0 && runCatching { androidx.compose.ui.platform.LocalContext.current.resources.getResourceName(sealRes) }.isSuccess
            val bmpSeal = if (sealValid && sealRes != 0) runCatching { BitmapFactory.decodeResource(androidx.compose.ui.platform.LocalContext.current.resources, sealRes) }.getOrNull() else null

            val sealModifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-5).dp, y = 5.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))

            if (bmpSeal != null) {
                Box(
                    modifier = sealModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bmpSeal.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (!sealEmoji.isNullOrEmpty()) {
                Box(
                    modifier = sealModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sealEmoji ?: "", fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User info with animations
        Text(
            text = displayName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.animateContentSize()
        )
        Text(
            text = email,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Edit profile button
        OutlinedButton(
            onClick = onEditProfileClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White,
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                )
            )
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatsSection(
    totalPoints: Int,
    articlesRead: Int,
    quizzesDone: Int,
    achievementsUnlocked: Int
) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            "Estatísticas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Palette.text
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Main stat - EcoPoints
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Palette.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$totalPoints",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Palette.primary
                    )
                    Text(
                        text = "EcoPoints",
                        fontSize = 16.sp,
                        color = Palette.textMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Palette.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EnergySavingsLeaf,
                        contentDescription = "EcoPoints",
                        tint = Palette.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Palette.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Palette.text
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Palette.textMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            "Ações Rápidas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Palette.text
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                title = "Configurações",
                description = "Personalize sua experiência",
                icon = Icons.Default.Settings,
                color = Palette.primary,
                onClick = onSettingsClick
            )
            QuickActionButton(
                title = "Ajuda",
                description = "Dúvidas e suporte",
                icon = Icons.Default.HelpOutline,
                color = Palette.tertiary,
                onClick = onHelpClick
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Palette.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Palette.text
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Palette.textMuted,
                    lineHeight = 18.sp
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Abrir",
                tint = Palette.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AccountSection(
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            "Conta",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Palette.text
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onSignOutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red
            ),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Red.copy(alpha = 0.3f), Color.Red.copy(alpha = 0.1f))
                )
            )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Sair",
                tint = Color.Red,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sair da Conta",
                color = Color.Red,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    EcoLabTheme {
        ProfileScreen(
            onEditProfileClick = {},
            onSignOutClick = {},
            onAchievementsClick = {},
            onSettingsClick = {},
            onHelpClick = {}
        )
    }
}
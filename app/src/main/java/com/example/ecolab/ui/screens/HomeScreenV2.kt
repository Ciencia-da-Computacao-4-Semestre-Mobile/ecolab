package com.example.ecolab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import android.graphics.BitmapFactory
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecolab.R
import com.example.ecolab.feature.home.HomeUiState
import com.example.ecolab.feature.home.HomeViewModel
import com.example.ecolab.ui.theme.EcoLabTheme
import com.example.ecolab.ui.theme.Palette
import kotlin.math.PI
import kotlin.math.sin
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.ecolab.ui.components.AnimatedParticles
import com.example.ecolab.ui.components.UserAvatar
import coil.compose.AsyncImage

@Composable
fun HomeScreenV2(
    onQuizClick: () -> Unit,
    onStoreClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Palette.background)
                .padding(paddingValues)
        ) {
            AnimatedParticles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedHeader(uiState)

                Column(
                    modifier = Modifier
                        .fillMaxWidth() // MUDADO: De fillMaxSize para fillMaxWidth
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var cardsVisible by remember { mutableStateOf(false) }
                    var statsVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        cardsVisible = true
                        kotlinx.coroutines.delay(300)
                        statsVisible = true
                    }

                    AnimatedVisibility(
                        visible = statsVisible,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) +
                                slideInVertically(animationSpec = tween(800, delayMillis = 200)) { it / 3 }
                    ) {
                        AnimatedStatsSection(uiState)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(
                        visible = cardsVisible,
                        enter = fadeIn(animationSpec = tween(800, delayMillis = 500)) +
                                slideInVertically(animationSpec = tween(800, delayMillis = 500)) { it / 3 }
                    ) {
                        AnimatedFeatureCards(uiState, onQuizClick, onStoreClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedHeader(uiState: HomeUiState) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect("sync_equipped_assets") {
        runCatching {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).collection("store").document("state").get()
                    .addOnSuccessListener { stateDoc ->
                        if (stateDoc.exists()) {
                            val equippedAvatarResId = stateDoc.getLong("equippedAvatarResId")?.toInt()
                            val equippedSealEmoji = stateDoc.getString("equippedSealEmoji")
                            val prefs = context.getSharedPreferences("ecolab_prefs", android.content.Context.MODE_PRIVATE)
                            equippedAvatarResId?.let { resId ->
                                if (resId != 0 && runCatching { context.resources.getResourceName(resId) }.isSuccess) {
                                    prefs.edit().putInt("equipped_avatar_res_id", resId).apply()
                                }
                            }
                            if (!equippedSealEmoji.isNullOrEmpty()) {
                                prefs.edit().putString("equipped_seal_emoji", equippedSealEmoji).apply()
                                prefs.edit().remove("equipped_seal_res_id").apply()
                            }
                        }
                    }
            }
        }
    }

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

    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "header_glow"
    )

    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_rotation"
    )

    val particle1Y by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle1_y"
    )

    val particle2Y by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle2_y"
    )

    val particle3Y by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle3_y"
    )

    val particle4Y by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle4_y"
    )

    val particle1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle1_scale"
    )

    val particle2Scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle2_scale"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val waveAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            )
    ) {
        val headerHeight = maxWidth * 0.28f
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .align(Alignment.TopCenter)
        ) {
            for (i in 0..3) {
                val waveY = 54f + sin(waveAnimation + i.toFloat() * 0.5f) * 25f
                drawCircle(
                    color = Palette.primary.copy(alpha = 0.02f),
                    radius = 80f + i.toFloat() * 20f,
                    center = Offset(size.width / 2, waveY)
                )
            }

            drawCircle(
                color = Palette.secondary.copy(alpha = 0.4f * pulseGlow),
                radius = 8f * particle1Scale,
                center = Offset(size.width * 0.2f, particle1Y + 54f)
            )

            drawCircle(
                color = Palette.tertiary.copy(alpha = 0.5f * pulseGlow),
                radius = 10f * particle2Scale,
                center = Offset(size.width * 0.8f, particle2Y + 54f)
            )

            if (headerGlow > 0) {
                drawCircle(
                    color = Palette.primary.copy(alpha = headerGlow * 0.08f),
                    radius = 120f,
                    center = Offset(size.width / 2, 54f)
                )
            }
        }

        val headerBrush = Brush.verticalGradient(
            colors = listOf(
                Palette.primary,
                Palette.primaryHover
            )
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(animationSpec = tween(800)) { -it / 3 }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBrush)
                    .padding(top = 24.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val avatarScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "avatar_scale"
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(avatarScale),
                    contentAlignment = Alignment.Center
                ) {
                    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    UserAvatar(
                        modifier = Modifier.fillMaxSize(),
                        displayName = user?.displayName,
                        email = user?.email,
                        photoUrl = run {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val prefs = context.getSharedPreferences("ecolab_prefs", android.content.Context.MODE_PRIVATE)
                            val resId = prefs.getInt("equipped_avatar_res_id", 0)
                            val isValid = resId != 0 && runCatching { context.resources.getResourceName(resId) }.isSuccess
                            if (isValid) resId else uiState.avatar ?: user?.photoUrl?.toString()
                        }
                    )

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val prefs = remember { context.getSharedPreferences("ecolab_prefs", android.content.Context.MODE_PRIVATE) }
                    var sealRes by remember { mutableStateOf(prefs.getInt("equipped_seal_res_id", 0)) }
                    var sealEmoji by remember { mutableStateOf(prefs.getString("equipped_seal_emoji", "")) }

                    androidx.compose.runtime.DisposableEffect(prefs) {
                        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
                            if (key == "equipped_seal_res_id") {
                                sealRes = sp.getInt("equipped_seal_res_id", 0)
                            }
                            if (key == "equipped_seal_emoji") {
                                sealEmoji = sp.getString("equipped_seal_emoji", "")
                            }
                        }
                        prefs.registerOnSharedPreferenceChangeListener(listener)
                        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
                    }

                    val sealValid = sealRes != 0 && runCatching { context.resources.getResourceName(sealRes) }.isSuccess
                    val bmpSeal = if (sealValid && sealRes != 0) runCatching { BitmapFactory.decodeResource(context.resources, sealRes) }.getOrNull() else null

                    val sealModifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-5).dp, y = 5.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)

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
                            androidx.compose.material3.Text(text = sealEmoji ?: "", fontSize = 20.sp)
                        }
                    } else {
                        if (sealRes != 0 && !sealValid) {
                            prefs.edit().remove("equipped_seal_res_id").apply()
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    val textAlpha by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(1000, delayMillis = 300),
                        label = "text_alpha"
                    )

                    Text(
                        text = "Olá, Eco Hero!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = textAlpha)
                    )

                    Text(
                        text = "Vamos salvar o planeta?",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = textAlpha * 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedFeatureCards(
    uiState: HomeUiState,
    onQuizClick: () -> Unit,
    onStoreClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 100)) { it / 2 }
        ) {
            AnimatedFeatureCard(
                title = "Quiz Ambiental",
                description = "Teste seus conhecimentos sobre sustentabilidade",
                icon = Icons.Default.Quiz,
                color = Palette.quizIcon,
                onClick = onQuizClick,
                progress = uiState.quizProgress,
                progressText = uiState.quizProgressText,
                completed = uiState.quizCompleted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 300)) { it / 2 }
        ) {
            AnimatedFeatureCard(
                title = "Loja",
                description = "Troque seus EcoPoints por recompensas",
                icon = Icons.Outlined.ShoppingBag,
                color = Palette.achievementsIcon,
                onClick = onStoreClick
            )
        }
    }
}

@Composable
private fun AnimatedStatsSection(uiState: HomeUiState) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stats_animations")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val backgroundGlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "background_glow"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600, delayMillis = 500)) +
                slideInVertically(animationSpec = tween(600, delayMillis = 500)) { it / 2 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Palette.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícone de estrela menor
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(Palette.tertiary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EnergySavingsLeaf,
                        contentDescription = "Seus EcoPoints",
                        tint = Palette.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Título com fonte reduzida
                Text(
                    text = "${uiState.totalPoints} EcoPoints",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Palette.text,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Descrição mais curta e menor
                Text(
                    text = "Continue reciclando!",
                    fontSize = 14.sp,
                    color = Palette.textMuted,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    progress: Float? = null,
    progressText: String? = null,
    completed: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

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

    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")

    val iconPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse"
    )

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
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(iconPulse)
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

                if (completed) {
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

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Avançar",
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenV2Preview() {
    EcoLabTheme {
        HomeScreenV2(
            onQuizClick = { },
            onStoreClick = { }
        )
    }
}
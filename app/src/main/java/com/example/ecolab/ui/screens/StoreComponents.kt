package com.example.ecolab.ui.screens

    import androidx.compose.animation.*
    import androidx.compose.animation.core.*
    import androidx.compose.foundation.*
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.draw.drawWithContent
    import androidx.compose.ui.draw.scale
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.geometry.Offset
    import androidx.compose.ui.graphics.*
    import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.ecolab.model.*
    import com.example.ecolab.ui.theme.Palette

    @Composable
    fun StoreItemCard(
        item: StoreItem,
        userPoints: Int,
        onPurchase: () -> Unit,
        onEquip: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        // Animações para itens lendários
        val infiniteTransition = rememberInfiniteTransition(label = "Legendary Animation")

        // Animação de brilho
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Glow Alpha"
        )

        // Animação de partículas
        val particleRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "Particle Rotation"
        )

        // Animação de clique
        var isPressed by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "Click Scale"
        )

        Card(
            modifier = modifier
                .scale(scale)
                .clickable { isPressed = !isPressed }
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Palette.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Fundo com gradiente baseado na raridade
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            brush = when (item.rarity) {
                                Rarity.COMMON -> Brush.verticalGradient(
                                    colors = listOf(
                                        Palette.primary.copy(alpha = 0.1f),
                                        Palette.primary.copy(alpha = 0.05f)
                                    )
                                )
                                Rarity.UNCOMMON -> Brush.verticalGradient(
                                    colors = listOf(
                                        Palette.secondary.copy(alpha = 0.12f),
                                        Palette.secondary.copy(alpha = 0.06f)
                                    )
                                )
                                Rarity.RARE -> Brush.verticalGradient(
                                    colors = listOf(
                                        Palette.secondary.copy(alpha = 0.15f),
                                        Palette.secondary.copy(alpha = 0.08f)
                                    )
                                )
                                Rarity.EPIC -> Brush.verticalGradient(
                                    colors = listOf(
                                        Palette.tertiary.copy(alpha = 0.2f),
                                        Palette.tertiary.copy(alpha = 0.1f)
                                    )
                                )
                                Rarity.LEGENDARY -> Brush.verticalGradient(
                                    colors = listOf(
                                        Palette.accent.copy(alpha = 0.25f),
                                        Palette.accent.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        )
                ) {
                    // Efeitos especiais para itens lendários
                    if (item.rarity == Rarity.LEGENDARY) {
                        // Brilho animado
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawContent()
                                    withTransform({
                                        rotate(particleRotation)
                                    }) {
                                        drawCircle(
                                            color = Palette.accent.copy(alpha = glowAlpha * 0.3f),
                                            radius = 50f,
                                            center = Offset(size.width * 0.8f, size.height * 0.2f)
                                        )
                                    }
                                }
                        )

                        // Partículas
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(scaleX = 1.2f, scaleY = 1.2f)
                        ) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val radius = 40f

                            for (i in 0..6) {
                                val angle = (particleRotation + i * 51.4f) * (Math.PI / 180f).toFloat()
                                val x = centerX + radius * kotlin.math.cos(angle)
                                val y = centerY + radius * kotlin.math.sin(angle)

                                drawCircle(
                                    color = Palette.accent.copy(alpha = 0.6f),
                                    radius = 3f,
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }

                    if (item.drawableRes != null) {
                        Image(
                            painter = painterResource(id = item.drawableRes),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(72.dp)
                        )
                    } else {
                        Text(
                            text = item.iconRes,
                            fontSize = 48.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .graphicsLayer(scaleX = 1.2f, scaleY = 1.2f),
                            color = when (item.rarity) {
                                Rarity.COMMON -> Palette.text
                                Rarity.UNCOMMON -> Palette.secondary
                                Rarity.RARE -> Palette.secondary
                                Rarity.EPIC -> Palette.tertiary
                                Rarity.LEGENDARY -> Palette.accent
                            }
                        )
                    }
                }

                

                // Conteúdo principal
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 120.dp)
                        .padding(16.dp)
                ) {
                    // Nome do item
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Palette.text,
                        maxLines = 1
                    )

                    // Descrição curta
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Palette.textMuted,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status e ações
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Preço ou status
                        StoreStatusChip(item = item, userPoints = userPoints)

                        // Botão de ação
                        when {
                            item.isEquipped -> {
                                Button(
                                    onClick = { },
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = Palette.success.copy(alpha = 0.3f),
                                        disabledContentColor = Palette.success
                                    ),
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Equipado",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            item.isPurchased -> {
                                Button(
                                    onClick = onEquip,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Palette.primary,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Equipar",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            else -> {
                                Button(
                                    onClick = onPurchase,
                                    enabled = userPoints >= item.price,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (userPoints >= item.price) {
                                            Palette.accent
                                        } else {
                                            Palette.textMuted.copy(alpha = 0.3f)
                                        },
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Comprar",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StoreRarityBadge(
        rarity: Rarity,
        modifier: Modifier = Modifier
    ) {
        val backgroundColor = when (rarity) {
            Rarity.COMMON -> Palette.primary.copy(alpha = 0.8f)
            Rarity.UNCOMMON -> Palette.secondary.copy(alpha = 0.7f)
            Rarity.RARE -> Palette.secondary.copy(alpha = 0.8f)
            Rarity.EPIC -> Palette.tertiary.copy(alpha = 0.8f)
            Rarity.LEGENDARY -> Palette.accent.copy(alpha = 0.8f)
        }

        val textColor = Color.White

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor
        ) {
            Text(
                text = when (rarity) {
                    Rarity.COMMON -> "COMUM"
                    Rarity.UNCOMMON -> "INCOMUM"
                    Rarity.RARE -> "RARO"
                    Rarity.EPIC -> "ÉPICO"
                    Rarity.LEGENDARY -> "LENDÁRIO"
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }

    @Composable
    fun StoreStatusChip(
        item: StoreItem,
        userPoints: Int,
        modifier: Modifier = Modifier
    ) {
        // Escolhe a cor base para o chip com base na raridade e status
        val baseColor = when (item.rarity) {
            Rarity.COMMON -> Palette.primary
            Rarity.UNCOMMON -> Palette.secondary
            Rarity.RARE -> Palette.secondary
            Rarity.EPIC -> Palette.tertiary
            Rarity.LEGENDARY -> Palette.accent
        }

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = when {
                item.isEquipped -> Palette.success.copy(alpha = 0.1f)
                item.isPurchased -> baseColor.copy(alpha = 0.1f)
                userPoints >= item.price -> baseColor.copy(alpha = 0.1f)
                else -> Palette.error.copy(alpha = 0.1f)
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = when {
                        item.isEquipped -> Icons.Default.CheckCircle
                        item.isPurchased -> Icons.Default.Inventory2
                        else -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = when {
                        item.isEquipped -> Palette.success
                        item.isPurchased -> baseColor
                        userPoints >= item.price -> baseColor
                        else -> Palette.error
                    },
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = when {
                        item.isEquipped -> "Equipado"
                        item.isPurchased -> "Adquirido"
                        else -> "${item.price} pts"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        item.isEquipped -> Palette.success
                        item.isPurchased -> baseColor
                        userPoints >= item.price -> baseColor
                        else -> Palette.error
                    }
                )
            }
        }
    }
package com.example.ecolab.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Particle(
    val id: Int,
    var x: Float,
    var y: Float,
    var size: Float,
    var alpha: Float,
    var speed: Float,
    var direction: Float,
    var color: Color
)

@Composable
fun AnimatedParticles(
    modifier: Modifier = Modifier.fillMaxSize(),
    particleCount: Int = 15,
    colors: List<Color> = listOf(
        Color(0xFF4CAF50).copy(alpha = 0.3f),
        Color(0xFF81C784).copy(alpha = 0.3f),
        Color(0xFFA5D6A7).copy(alpha = 0.3f),
        Color(0xFF66BB6A).copy(alpha = 0.3f)
    )
) {
    var particles by remember { mutableStateOf(listOf<Particle>()) }
    var time by remember { mutableStateOf(0f) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particle_animation")
    val animationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    // Inicializar partículas
    LaunchedEffect(Unit) {
        particles = List(particleCount) { index ->
            Particle(
                id = index,
                x = Random.nextFloat() * 1000f,
                y = Random.nextFloat() * 1000f,
                size = Random.nextFloat() * 8.dp.value + 4.dp.value,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                direction = Random.nextFloat() * 360f,
                color = colors.random()
            )
        }
    }

    // Atualizar partículas
    LaunchedEffect(animationValue) {
        particles = particles.map { particle ->
            val newX = (particle.x + kotlin.math.cos(Math.toRadians(particle.direction.toDouble())).toFloat() * particle.speed * 2f) % 1000f
            val newY = (particle.y + kotlin.math.sin(Math.toRadians(particle.direction.toDouble())).toFloat() * particle.speed * 2f) % 1000f
            
            particle.copy(
                x = if (newX < 0) 1000f + newX else newX,
                y = if (newY < 0) 1000f + newY else newY,
                alpha = (kotlin.math.sin(animationValue * 2f * Math.PI + particle.id).toFloat() * 0.3f + 0.2f).coerceIn(0.1f, 0.5f)
            )
        }
    }

    Canvas(
        modifier = modifier
    ) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(particle.x, particle.y)
            )
        }
        
        // Adicionar alguns círculos maiores para variedade
        if (particles.isNotEmpty()) {
            for (i in 0..2) {
                val particle = particles.random()
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha * 0.3f),
                    radius = particle.size * 3f,
                    center = Offset(particle.x + 50f, particle.y + 50f)
                )
            }
        }
    }
}

// Versão simplificada para telas com menos recursos
@Composable
fun SimpleAnimatedParticles(
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var offset by remember { mutableStateOf(0f) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "simple_particle_animation")
    val animationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "simple_particle_rotation"
    )

    Canvas(
        modifier = modifier
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 3
        
        // Criar padrão de círculos concêntricos animados
        for (i in 0..3) {
            val angle = Math.toRadians((animationValue + i * 90).toDouble())
            val x = centerX + radius * kotlin.math.cos(angle).toFloat()
            val y = centerY + radius * kotlin.math.sin(angle).toFloat()
            
            drawCircle(
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                radius = 20f + i * 10f,
                center = Offset(x, y)
            )
        }
    }
}
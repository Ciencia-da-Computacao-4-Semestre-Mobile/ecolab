package com.example.ecolab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ecolab.ui.components.AnimatedParticles
import com.example.ecolab.ui.theme.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ajuda",
                        color = Palette.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Palette.text
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Palette.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Palette.background)
                .padding(paddingValues)
        ) {
            AnimatedParticles(
                particleCount = 10,
                colors = listOf(
                    Palette.primary.copy(alpha = 0.3f),
                    Palette.secondary.copy(alpha = 0.3f),
                    Palette.success.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HelpSection(
                    title = "Perguntas Frequentes",
                    items = listOf(
                        HelpItem(
                            question = "Como ganho EcoPoints?",
                            answer = "Você ganha pontos respondendo quizzes. Cada quiz concluído soma EcoPoints."
                        ),
                        HelpItem(
                            question = "Como editar meu nome?",
                            answer = "Abra Perfil e toque em 'Editar Nome'. Salve para sincronizar."
                        ),
                        HelpItem(
                            question = "Como equipar Avatares e Selos?",
                            answer = "Vá em Loja, compre o item e toque em 'Equipar'. O inventário agora mostra Avatares e Selos separados."
                        ),
                        HelpItem(
                            question = "Onde vejo meus EcoPoints?",
                            answer = "No topo da Loja e na seção 'Estatísticas' do Perfil."
                        )
                    )
                )

                HelpSection(
                    title = "Suporte",
                    items = listOf(
                        HelpItem(
                            question = "Reportar Problema",
                            answer = "Se você encontrar algum problema, entre em contato conosco pelo email ecolabmanager7@gmail.com"
                        ),
                        HelpItem(
                            question = "Sugerir Melhorias",
                            answer = "Adoramos receber sugestões! Envie suas ideias para ecolabmanager7@gmail.com"
                        ),
                        HelpItem(
                            question = "Versão do App",
                            answer = "Versão 1.0.0 - Atualizado em Dezembro 2024"
                        )
                    )
                )

                
            }
        }
    }
}

data class HelpItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpSection(
    title: String,
    items: List<HelpItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Palette.surface.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = Palette.text,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            items.forEach { item ->
                HelpItemRow(item = item)
            }
        }
    }
}

@Composable
fun HelpItemRow(item: HelpItem) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Palette.primary.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    color = Palette.text,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Recolher" else "Expandir",
                    tint = Palette.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.answer,
                    color = Palette.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
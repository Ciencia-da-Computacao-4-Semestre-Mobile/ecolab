package com.example.ecolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ecolab.ui.theme.Palette

@Composable
fun LoginScreen(onLogin: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Bem-vindo ao Eco Lab",
            style = MaterialTheme.typography.headlineSmall,
            color = Palette.text
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { onLogin?.invoke() },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Palette.primary)
        ) {
            Text("Continuar")
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Integre o Firebase/Auth para login com Google aqui.",
            style = MaterialTheme.typography.bodySmall,
            color = Palette.textMuted,
            textAlign = TextAlign.Center
        )
    }
}

package com.example.ecolab.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecolab.ui.theme.Palette

@Composable
fun QuickActionSheet(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Nova ação",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            color = Palette.text
        )
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Palette.primary),
            border = BorderStroke(1.dp, Palette.divider)
        ) {
            Text("Registrar evidência")
        }
        OutlinedButton(
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Palette.primary),
            border = BorderStroke(1.dp, Palette.divider)
        ) {
            Text("Criar missão")
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onDone) {
            Text("Fechar", color = Palette.textMuted)
        }
    }
}

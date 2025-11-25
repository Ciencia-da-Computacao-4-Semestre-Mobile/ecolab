package com.example.ecolab.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ecolab.ui.theme.Palette

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    displayName: String?,
    email: String?,
    photoUrl: String?
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto de Perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("ecolab_prefs", android.content.Context.MODE_PRIVATE) }
            var equippedRes by remember { mutableStateOf(prefs.getInt("equipped_avatar_res_id", 0)) }
            DisposableEffect(prefs) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
                    if (key == "equipped_avatar_res_id") {
                        equippedRes = sp.getInt("equipped_avatar_res_id", 0)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
            }
            val isValidRes = equippedRes != 0 && runCatching { context.resources.getResourceName(equippedRes) }.isSuccess
            val bmpAvatar = if (isValidRes) runCatching { BitmapFactory.decodeResource(context.resources, equippedRes) }.getOrNull() else null
            if (bmpAvatar != null) {
                Image(
                    bitmap = bmpAvatar.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                if (equippedRes != 0) {
                    prefs.edit().remove("equipped_avatar_res_id").apply()
                }
                val initial = displayName?.firstOrNull()?.uppercase() ?: email?.firstOrNull()?.uppercase() ?: "U"
                Text(
                    text = initial,
                    fontSize = 56.sp,
                    color = Palette.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
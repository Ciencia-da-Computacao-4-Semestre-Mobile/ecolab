package com.example.ecolab.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.example.ecolab.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

private val markerIconCache = mutableMapOf<Color, BitmapDescriptor>()

@Composable
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Ecoponto" -> Color(0xFF0F9D58) // Verde
        "Cooperativa" -> Color(0xFFD50F25) // Vermelho
        "Ponto de Entrega" -> Color(0xFF3369E8) // Azul
        "Pátio de Compostagem" -> Color(0xFFF4B400) // Amarelo
        else -> Color.Gray
    }
}

fun getMarkerIconBitmap(context: Context, color: Color): BitmapDescriptor {
    return markerIconCache.getOrPut(color) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_location)!!
        
        val scale = 0.8f
        val width = (drawable.intrinsicWidth * scale).toInt()
        val height = (drawable.intrinsicHeight * scale).toInt()

        drawable.colorFilter = PorterDuffColorFilter(color.toArgb(), PorterDuff.Mode.SRC_IN)
        drawable.setBounds(0, 0, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

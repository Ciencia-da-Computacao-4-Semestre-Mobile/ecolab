package com.example.ecolab

import android.app.Application
import android.util.Log
import kotlin.system.exitProcess

class EcoLabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configurar crash handler global
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("EcoLabCrash", "🚨 CRASH DETECTADO! 🚨", exception)
            Log.e("EcoLabCrash", "Thread: ${thread.name}")
            Log.e("EcoLabCrash", "Exception: ${exception.javaClass.simpleName}")
            Log.e("EcoLabCrash", "Message: ${exception.message}")
            Log.e("EcoLabCrash", "StackTrace:")
            exception.stackTrace.forEach { element ->
                Log.e("EcoLabCrash", "  at $element")
            }

            if (exception.message?.contains("register", ignoreCase = true) == true ||
                exception.message?.contains("RegisterViewModel", ignoreCase = true) == true ||
                exception.message?.contains("UserRepository", ignoreCase = true) == true ||
                exception.stackTrace.any { it.className.contains("Register", ignoreCase = true) }) {
                Log.e("EcoLabCrash", "💥 CRASH NO PROCESSO DE CADASTRO DETECTADO! 💥")
            }

            try {
                Thread.sleep(500)
            } catch (_: InterruptedException) {}

            originalHandler?.uncaughtException(thread, exception)
        }
        
        Log.d("EcoLabApplication", "Aplicação iniciada - Crash handler configurado")
    }
}
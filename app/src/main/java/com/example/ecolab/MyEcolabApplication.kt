package com.example.ecolab

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class MyEcolabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configurar crash handler global para capturar erros
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("EcoLabCrash", "🚨 CRASH DETECTADO! 🚨", exception)
            Log.e("EcoLabCrash", "Thread: ${thread.name}")
            Log.e("EcoLabCrash", "Exception: ${exception.javaClass.simpleName}")
            Log.e("EcoLabCrash", "Message: ${exception.message}")

            if (exception.stackTrace.any { element ->
                element.className.contains("RegisterViewModel", ignoreCase = true) ||
                element.className.contains("UserRepository", ignoreCase = true) ||
                element.className.contains("RegisterScreen", ignoreCase = true) ||
                element.methodName.contains("onRegisterClick", ignoreCase = true) ||
                element.methodName.contains("createUser", ignoreCase = true)
            }) {
                Log.e("EcoLabCrash", "💥 CRASH NO PROCESSO DE CADASTRO! 💥")
            }

            try {
                Thread.sleep(500)
            } catch (_: InterruptedException) {}

            originalHandler?.uncaughtException(thread, exception)
        }
        
        Log.d("MyEcolabApplication", "Aplicação iniciada - Crash handler configurado")
    }
}
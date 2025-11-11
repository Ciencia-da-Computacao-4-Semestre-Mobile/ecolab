package com.example.ecolab

import android.app.Application
import android.util.Log
import kotlin.system.exitProcess

class EcoLabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configurar crash handler global
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("EcoLabCrash", "🚨 CRASH DETECTADO! 🚨", exception)
            Log.e("EcoLabCrash", "Thread: ${thread.name}")
            Log.e("EcoLabCrash", "Exception: ${exception.javaClass.simpleName}")
            Log.e("EcoLabCrash", "Message: ${exception.message}")
            Log.e("EcoLabCrash", "StackTrace:")
            exception.stackTrace.forEach { element ->
                Log.e("EcoLabCrash", "  at $element")
            }
            
            // Log específico para crashes no cadastro
            if (exception.message?.contains("register", ignoreCase = true) == true ||
                exception.message?.contains("RegisterViewModel", ignoreCase = true) == true ||
                exception.message?.contains("UserRepository", ignoreCase = true) == true ||
                exception.stackTrace.any { it.className.contains("Register", ignoreCase = true) }) {
                Log.e("EcoLabCrash", "💥 CRASH NO PROCESSO DE CADASTRO DETECTADO! 💥")
                Log.e("EcoLabCrash", "Possíveis causas:")
                Log.e("EcoLabCrash", "1. Erro ao criar usuário no Firebase Auth")
                Log.e("EcoLabCrash", "2. Erro ao criar documento no Firestore")
                Log.e("EcoLabCrash", "3. Problema com os dados do usuário")
                Log.e("EcoLabCrash", "4. Falta de permissões no Firebase")
                Log.e("EcoLabCrash", "5. Problema de conexão com internet")
            }
            
            // Aguardar um pouco para garantir que os logs sejam escritos
            Thread.sleep(1000)
            
            // Finalizar o app de forma controlada
            exitProcess(1)
        }
        
        Log.d("EcoLabApplication", "Aplicação iniciada - Crash handler configurado")
    }
}
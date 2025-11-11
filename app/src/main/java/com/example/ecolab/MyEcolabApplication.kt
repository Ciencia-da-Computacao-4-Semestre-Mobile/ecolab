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
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("EcoLabCrash", "🚨 CRASH DETECTADO! 🚨", exception)
            Log.e("EcoLabCrash", "Thread: ${thread.name}")
            Log.e("EcoLabCrash", "Exception: ${exception.javaClass.simpleName}")
            Log.e("EcoLabCrash", "Message: ${exception.message}")
            
            // Log específico para crashes no cadastro
            if (exception.stackTrace.any { element ->
                element.className.contains("RegisterViewModel", ignoreCase = true) ||
                element.className.contains("UserRepository", ignoreCase = true) ||
                element.className.contains("RegisterScreen", ignoreCase = true) ||
                element.methodName.contains("onRegisterClick", ignoreCase = true) ||
                element.methodName.contains("createUser", ignoreCase = true)
            }) {
                Log.e("EcoLabCrash", "💥 CRASH NO PROCESSO DE CADASTRO! 💥")
                Log.e("EcoLabCrash", "Possíveis causas:")
                Log.e("EcoLabCrash", "1. Firebase Auth não configurado corretamente")
                Log.e("EcoLabCrash", "2. Firestore não tem permissões de escrita")
                Log.e("EcoLabCrash", "3. Dados do usuário inválidos (null, empty, etc)")
                Log.e("EcoLabCrash", "4. Problema de conexão com internet")
                Log.e("EcoLabCrash", "5. Email já cadastrado ou formato inválido")
                Log.e("EcoLabCrash", "6. Senha não atende requisitos mínimos")
            }
            
            // Aguardar para garantir que os logs sejam escritos
            Thread.sleep(1000)
            
            // Finalizar o app
            exitProcess(1)
        }
        
        Log.d("MyEcolabApplication", "Aplicação iniciada - Crash handler configurado")
    }
}
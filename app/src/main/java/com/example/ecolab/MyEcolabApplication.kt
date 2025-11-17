package com.example.ecolab

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

@HiltAndroidApp
class MyEcolabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("MyEcolabApplication", "FirebaseApp inicializado")
            }
        } catch (e: Exception) {
            Log.e("MyEcolabApplication", "Falha ao inicializar FirebaseApp", e)
        }

        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            Log.d("MyEcolabApplication", "Firestore configurado com persistence")
        } catch (e: Exception) {
            Log.e("MyEcolabApplication", "Falha ao configurar Firestore", e)
        }

        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously()
                    .addOnSuccessListener { Log.d("MyEcolabApplication", "Login anônimo realizado") }
                    .addOnFailureListener { ex -> Log.e("MyEcolabApplication", "Falha no login anônimo", ex) }
            }
        } catch (e: Exception) {
            Log.e("MyEcolabApplication", "Erro ao verificar autenticação", e)
        }

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
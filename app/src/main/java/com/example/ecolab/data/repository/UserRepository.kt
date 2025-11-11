package com.example.ecolab.data.repository

import android.util.Log
import com.example.ecolab.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User) {
        Log.d("UserRepository", "=== INICIANDO CRIAÇÃO DE USUÁRIO ===")
        Log.d("UserRepository", "Dados recebidos:")
        Log.d("UserRepository", "  ID: ${user.id}")
        Log.d("UserRepository", "  Nome: '${user.name}'")
        Log.d("UserRepository", "  Email: '${user.email}'")
        Log.d("UserRepository", "  FavoritedPoints: ${user.favoritedPoints.size} items")
        Log.d("UserRepository", "  Achievements: ${user.unlockedAchievements.size} items")
        
        // Validação dos dados antes de tentar salvar
        if (user.id.isBlank()) {
            Log.e("UserRepository", "ERRO: ID do usuário está vazio ou nulo")
            throw IllegalArgumentException("ID do usuário não pode estar vazio")
        }
        
        if (user.name.isBlank()) {
            Log.e("UserRepository", "ERRO: Nome do usuário está vazio")
            throw IllegalArgumentException("Nome do usuário não pode estar vazio")
        }
        
        if (user.email.isBlank()) {
            Log.e("UserRepository", "ERRO: Email do usuário está vazio")
            throw IllegalArgumentException("Email do usuário não pode estar vazio")
        }
        
        // Verificar se o email tem formato válido
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(user.email)) {
            Log.e("UserRepository", "ERRO: Email inválido: '${user.email}'")
            throw IllegalArgumentException("Formato de email inválido")
        }
        
        try {
            Log.d("UserRepository", "Tentando salvar documento no Firestore...")
            usersCollection.document(user.id).set(user).await()
            Log.d("UserRepository", "✅ USUÁRIO CRIADO COM SUCESSO: ${user.id}")
            
            // Verificar se foi realmente criado
            try {
                val createdUser = getUser(user.id)
                if (createdUser != null) {
                    Log.d("UserRepository", "✅ Verificação: Usuário encontrado no Firestore")
                } else {
                    Log.w("UserRepository", "⚠️  Aviso: Usuário não encontrado após criação")
                }
            } catch (verificationError: Exception) {
                Log.w("UserRepository", "⚠️  Erro ao verificar usuário criado", verificationError)
            }
            
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ ERRO AO CRIAR USUÁRIO NO FIRESTORE", e)
            Log.e("UserRepository", "Tipo da exceção: ${e.javaClass.simpleName}")
            Log.e("UserRepository", "Mensagem: ${e.message}")
            
            // Logs específicos para diferentes tipos de erros
            when (e) {
                is IllegalArgumentException -> {
                    Log.e("UserRepository", "Erro de validação dos dados")
                }
                is com.google.firebase.firestore.FirebaseFirestoreException -> {
                    Log.e("UserRepository", "Erro do Firestore - Código: ${e.code}")
                    Log.e("UserRepository", "Verifique:")
                    Log.e("UserRepository", "1. Se o Firestore está habilitado no Firebase Console")
                    Log.e("UserRepository", "2. Se as regras de segurança permitem escrita")
                    Log.e("UserRepository", "3. Se o dispositivo tem conexão com internet")
                    Log.e("UserRepository", "4. Se o google-services.json está atualizado")
                }
                else -> {
                    Log.e("UserRepository", "Erro desconhecido - pode ser problema de rede ou permissões")
                }
            }
            
            throw e
        }
    }

    suspend fun getUser(userId: String): User? {
        val document = usersCollection.document(userId).get().await()
        return document.toObject(User::class.java)
    }

    suspend fun addFavoritePoint(userId: String, pointId: String) {
        val user = getUser(userId)
        user?.let {
            val updatedFavorites = it.favoritedPoints.toMutableList().apply { add(pointId) }
            usersCollection.document(userId).update("favoritedPoints", updatedFavorites).await()
        }
    }

    suspend fun removeFavoritePoint(userId: String, pointId: String) {
        val user = getUser(userId)
        user?.let {
            val updatedFavorites = it.favoritedPoints.toMutableList().apply { remove(pointId) }
            usersCollection.document(userId).update("favoritedPoints", updatedFavorites).await()
        }
    }

    suspend fun unlockAchievement(userId: String, achievementId: String) {
        val user = getUser(userId)
        user?.let {
            val updatedAchievements = it.unlockedAchievements.toMutableList().apply { add(achievementId) }
            usersCollection.document(userId).update("unlockedAchievements", updatedAchievements).await()
        }
    }
}
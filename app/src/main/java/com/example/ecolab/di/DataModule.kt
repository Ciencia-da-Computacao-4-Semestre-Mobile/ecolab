package com.example.ecolab.di

import com.example.ecolab.core.data.repository.AuthRepositoryImpl
import com.example.ecolab.core.data.repository.FirebasePointsRepository
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.domain.repository.PointsRepository
import com.example.ecolab.data.repository.AchievementsRepository
import com.example.ecolab.data.repository.QuizRepository
import com.example.ecolab.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun providePointsRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): PointsRepository = FirebasePointsRepository(firestore, firebaseAuth)

    @Provides
    @Singleton
    fun provideAchievementsRepository(): AchievementsRepository = AchievementsRepository()

    @Provides
    @Singleton
    fun provideQuizRepository(): QuizRepository = QuizRepository()

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository = UserRepository(firestore)
}
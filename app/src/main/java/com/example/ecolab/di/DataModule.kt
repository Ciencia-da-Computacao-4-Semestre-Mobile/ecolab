package com.example.ecolab.di

import com.example.ecolab.core.data.repository.AuthRepositoryImpl
import com.example.ecolab.core.data.repository.FirebasePointsRepository
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.domain.repository.PointsRepository
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
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

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
}

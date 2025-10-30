package com.example.ecolab.di

import android.content.Context
import com.example.ecolab.core.data.repository.AuthRepositoryImpl
import com.example.ecolab.core.data.repository.PointsRepositoryImpl
import com.example.ecolab.core.domain.repository.AuthRepository
import com.example.ecolab.core.domain.repository.PointsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
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
        @ApplicationContext context: Context,
        json: Json,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): PointsRepository = PointsRepositoryImpl(context, json, firestore, firebaseAuth)

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }
}

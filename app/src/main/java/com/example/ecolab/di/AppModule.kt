package com.example.ecolab.di

import com.example.ecolab.data.auth.FakeAuthRepository
import com.example.ecolab.domain.auth.AuthRepository
import com.example.ecolab.domain.auth.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = FakeAuthRepository()

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase = LoginUseCase(repository)
}
package com.example.ecolab.di;

/**
 * Hilt module to provide repository implementations.
 */
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'\u00a8\u0006\u0007"}, d2 = {"Lcom/example/ecolab/di/RepositoryModule;", "", "()V", "bindPointsRepository", "Lcom/example/ecolab/domain/PointsRepository;", "repository", "Lcom/example/ecolab/data/repository/MockPointsRepository;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    /**
     * Binds the MockPointsRepository implementation to the PointsRepository interface.
     * This tells Hilt to use MockPointsRepository whenever PointsRepository is requested.
     */
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.ecolab.domain.PointsRepository bindPointsRepository(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.repository.MockPointsRepository repository);
}
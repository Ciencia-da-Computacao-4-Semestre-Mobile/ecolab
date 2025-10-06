package com.example.ecolab.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0082@\u00a2\u0006\u0002\u0010\tJ \u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\u0005\u001a\u00020\u00062\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\b0\rH\u0007J\u0010\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u000bH\u0007\u00a8\u0006\u0010"}, d2 = {"Lcom/example/ecolab/di/DatabaseModule;", "", "()V", "prepopulateDatabase", "", "context", "Landroid/content/Context;", "dao", "Lcom/example/ecolab/data/model/CollectionPointDao;", "(Landroid/content/Context;Lcom/example/ecolab/data/model/CollectionPointDao;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "provideAppDatabase", "Lcom/example/ecolab/data/local/AppDatabase;", "daoProvider", "Ljavax/inject/Provider;", "provideCollectionPointDao", "appDatabase", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class DatabaseModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.ecolab.di.DatabaseModule INSTANCE = null;
    
    private DatabaseModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.example.ecolab.data.local.AppDatabase provideAppDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    javax.inject.Provider<com.example.ecolab.data.model.CollectionPointDao> daoProvider) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.example.ecolab.data.model.CollectionPointDao provideCollectionPointDao(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.local.AppDatabase appDatabase) {
        return null;
    }
    
    private final java.lang.Object prepopulateDatabase(android.content.Context context, com.example.ecolab.data.model.CollectionPointDao dao, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}
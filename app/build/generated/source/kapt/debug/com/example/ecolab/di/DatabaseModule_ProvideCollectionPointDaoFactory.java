package com.example.ecolab.di;

import com.example.ecolab.data.local.AppDatabase;
import com.example.ecolab.data.model.CollectionPointDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideCollectionPointDaoFactory implements Factory<CollectionPointDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public DatabaseModule_ProvideCollectionPointDaoFactory(
      Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public CollectionPointDao get() {
    return provideCollectionPointDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideCollectionPointDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideCollectionPointDaoFactory(appDatabaseProvider);
  }

  public static CollectionPointDao provideCollectionPointDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCollectionPointDao(appDatabase));
  }
}

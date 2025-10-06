package com.example.ecolab.data.repository;

import com.example.ecolab.data.model.CollectionPointDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class CollectionPointRepository_Factory implements Factory<CollectionPointRepository> {
  private final Provider<CollectionPointDao> collectionPointDaoProvider;

  public CollectionPointRepository_Factory(
      Provider<CollectionPointDao> collectionPointDaoProvider) {
    this.collectionPointDaoProvider = collectionPointDaoProvider;
  }

  @Override
  public CollectionPointRepository get() {
    return newInstance(collectionPointDaoProvider.get());
  }

  public static CollectionPointRepository_Factory create(
      Provider<CollectionPointDao> collectionPointDaoProvider) {
    return new CollectionPointRepository_Factory(collectionPointDaoProvider);
  }

  public static CollectionPointRepository newInstance(CollectionPointDao collectionPointDao) {
    return new CollectionPointRepository(collectionPointDao);
  }
}

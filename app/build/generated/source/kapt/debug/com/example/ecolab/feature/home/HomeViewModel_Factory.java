package com.example.ecolab.feature.home;

import com.example.ecolab.data.repository.CollectionPointRepository;
import com.example.ecolab.data.repository.UserProgressRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<CollectionPointRepository> collectionPointRepositoryProvider;

  private final Provider<UserProgressRepository> userProgressRepositoryProvider;

  public HomeViewModel_Factory(
      Provider<CollectionPointRepository> collectionPointRepositoryProvider,
      Provider<UserProgressRepository> userProgressRepositoryProvider) {
    this.collectionPointRepositoryProvider = collectionPointRepositoryProvider;
    this.userProgressRepositoryProvider = userProgressRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(collectionPointRepositoryProvider.get(), userProgressRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<CollectionPointRepository> collectionPointRepositoryProvider,
      Provider<UserProgressRepository> userProgressRepositoryProvider) {
    return new HomeViewModel_Factory(collectionPointRepositoryProvider, userProgressRepositoryProvider);
  }

  public static HomeViewModel newInstance(CollectionPointRepository collectionPointRepository,
      UserProgressRepository userProgressRepository) {
    return new HomeViewModel(collectionPointRepository, userProgressRepository);
  }
}

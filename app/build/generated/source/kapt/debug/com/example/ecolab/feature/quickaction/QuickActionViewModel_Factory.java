package com.example.ecolab.feature.quickaction;

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
public final class QuickActionViewModel_Factory implements Factory<QuickActionViewModel> {
  private final Provider<CollectionPointRepository> collectionPointRepositoryProvider;

  private final Provider<UserProgressRepository> userProgressRepositoryProvider;

  public QuickActionViewModel_Factory(
      Provider<CollectionPointRepository> collectionPointRepositoryProvider,
      Provider<UserProgressRepository> userProgressRepositoryProvider) {
    this.collectionPointRepositoryProvider = collectionPointRepositoryProvider;
    this.userProgressRepositoryProvider = userProgressRepositoryProvider;
  }

  @Override
  public QuickActionViewModel get() {
    return newInstance(collectionPointRepositoryProvider.get(), userProgressRepositoryProvider.get());
  }

  public static QuickActionViewModel_Factory create(
      Provider<CollectionPointRepository> collectionPointRepositoryProvider,
      Provider<UserProgressRepository> userProgressRepositoryProvider) {
    return new QuickActionViewModel_Factory(collectionPointRepositoryProvider, userProgressRepositoryProvider);
  }

  public static QuickActionViewModel newInstance(
      CollectionPointRepository collectionPointRepository,
      UserProgressRepository userProgressRepository) {
    return new QuickActionViewModel(collectionPointRepository, userProgressRepository);
  }
}

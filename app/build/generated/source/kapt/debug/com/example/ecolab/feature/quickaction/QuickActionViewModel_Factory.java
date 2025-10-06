package com.example.ecolab.feature.quickaction;

import com.example.ecolab.data.repository.CollectionPointRepository;
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
  private final Provider<CollectionPointRepository> repositoryProvider;

  public QuickActionViewModel_Factory(Provider<CollectionPointRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public QuickActionViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static QuickActionViewModel_Factory create(
      Provider<CollectionPointRepository> repositoryProvider) {
    return new QuickActionViewModel_Factory(repositoryProvider);
  }

  public static QuickActionViewModel newInstance(CollectionPointRepository repository) {
    return new QuickActionViewModel(repository);
  }
}

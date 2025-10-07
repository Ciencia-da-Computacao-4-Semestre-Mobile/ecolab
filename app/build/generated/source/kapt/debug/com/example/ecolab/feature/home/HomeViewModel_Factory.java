package com.example.ecolab.feature.home;

import com.example.ecolab.core.domain.repository.PointsRepository;
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
  private final Provider<PointsRepository> repositoryProvider;

  public HomeViewModel_Factory(Provider<PointsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<PointsRepository> repositoryProvider) {
    return new HomeViewModel_Factory(repositoryProvider);
  }

  public static HomeViewModel newInstance(PointsRepository repository) {
    return new HomeViewModel(repository);
  }
}

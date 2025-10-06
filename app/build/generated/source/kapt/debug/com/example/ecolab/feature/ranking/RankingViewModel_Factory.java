package com.example.ecolab.feature.ranking;

import com.example.ecolab.data.repository.RankingRepository;
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
public final class RankingViewModel_Factory implements Factory<RankingViewModel> {
  private final Provider<RankingRepository> repositoryProvider;

  public RankingViewModel_Factory(Provider<RankingRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RankingViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static RankingViewModel_Factory create(Provider<RankingRepository> repositoryProvider) {
    return new RankingViewModel_Factory(repositoryProvider);
  }

  public static RankingViewModel newInstance(RankingRepository repository) {
    return new RankingViewModel(repository);
  }
}

package com.example.ecolab.di;

import com.example.ecolab.domain.auth.AuthRepository;
import com.example.ecolab.domain.auth.LoginUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideLoginUseCaseFactory implements Factory<LoginUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public AppModule_ProvideLoginUseCaseFactory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public LoginUseCase get() {
    return provideLoginUseCase(repositoryProvider.get());
  }

  public static AppModule_ProvideLoginUseCaseFactory create(
      Provider<AuthRepository> repositoryProvider) {
    return new AppModule_ProvideLoginUseCaseFactory(repositoryProvider);
  }

  public static LoginUseCase provideLoginUseCase(AuthRepository repository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLoginUseCase(repository));
  }
}

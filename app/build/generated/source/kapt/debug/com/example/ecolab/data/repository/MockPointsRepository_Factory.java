package com.example.ecolab.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class MockPointsRepository_Factory implements Factory<MockPointsRepository> {
  @Override
  public MockPointsRepository get() {
    return newInstance();
  }

  public static MockPointsRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MockPointsRepository newInstance() {
    return new MockPointsRepository();
  }

  private static final class InstanceHolder {
    private static final MockPointsRepository_Factory INSTANCE = new MockPointsRepository_Factory();
  }
}

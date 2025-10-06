package com.example.ecolab.feature.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/example/ecolab/feature/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "collectionPointRepository", "Lcom/example/ecolab/data/repository/CollectionPointRepository;", "userProgressRepository", "Lcom/example/ecolab/data/repository/UserProgressRepository;", "(Lcom/example/ecolab/data/repository/CollectionPointRepository;Lcom/example/ecolab/data/repository/UserProgressRepository;)V", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/example/ecolab/feature/home/HomeUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.home.HomeUiState> uiState = null;
    
    @javax.inject.Inject()
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.repository.CollectionPointRepository collectionPointRepository, @org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.repository.UserProgressRepository userProgressRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.home.HomeUiState> getUiState() {
        return null;
    }
}
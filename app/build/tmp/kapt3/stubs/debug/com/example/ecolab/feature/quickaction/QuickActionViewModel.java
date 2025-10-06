package com.example.ecolab.feature.quickaction;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018J\u000e\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u001cJ\u0006\u0010\u001f\u001a\u00020\u0016R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\f0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/example/ecolab/feature/quickaction/QuickActionViewModel;", "Landroidx/lifecycle/ViewModel;", "collectionPointRepository", "Lcom/example/ecolab/data/repository/CollectionPointRepository;", "userProgressRepository", "Lcom/example/ecolab/data/repository/UserProgressRepository;", "(Lcom/example/ecolab/data/repository/CollectionPointRepository;Lcom/example/ecolab/data/repository/UserProgressRepository;)V", "_eventChannel", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/example/ecolab/feature/quickaction/QuickActionEvent;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/ecolab/feature/quickaction/QuickActionUiState;", "eventChannel", "Lkotlinx/coroutines/flow/SharedFlow;", "getEventChannel", "()Lkotlinx/coroutines/flow/SharedFlow;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "onLocationCaptured", "", "lat", "", "lng", "onPhotoTaken", "uri", "", "onWasteTypeSelected", "type", "submit", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class QuickActionViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.data.repository.CollectionPointRepository collectionPointRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.data.repository.UserProgressRepository userProgressRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.ecolab.feature.quickaction.QuickActionUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.quickaction.QuickActionUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.example.ecolab.feature.quickaction.QuickActionEvent> _eventChannel = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<com.example.ecolab.feature.quickaction.QuickActionEvent> eventChannel = null;
    
    @javax.inject.Inject()
    public QuickActionViewModel(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.repository.CollectionPointRepository collectionPointRepository, @org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.repository.UserProgressRepository userProgressRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.quickaction.QuickActionUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<com.example.ecolab.feature.quickaction.QuickActionEvent> getEventChannel() {
        return null;
    }
    
    public final void onWasteTypeSelected(@org.jetbrains.annotations.NotNull()
    java.lang.String type) {
    }
    
    public final void onPhotoTaken(@org.jetbrains.annotations.NotNull()
    java.lang.String uri) {
    }
    
    public final void onLocationCaptured(double lat, double lng) {
    }
    
    public final void submit() {
    }
}
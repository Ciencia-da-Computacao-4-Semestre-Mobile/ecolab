package com.example.ecolab.feature.quickaction;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016J\u000e\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001aJ\u0006\u0010\u001d\u001a\u00020\u0014R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001e"}, d2 = {"Lcom/example/ecolab/feature/quickaction/QuickActionViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/ecolab/data/repository/CollectionPointRepository;", "(Lcom/example/ecolab/data/repository/CollectionPointRepository;)V", "_eventChannel", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/example/ecolab/feature/quickaction/QuickActionEvent;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/ecolab/feature/quickaction/QuickActionUiState;", "eventChannel", "Lkotlinx/coroutines/flow/SharedFlow;", "getEventChannel", "()Lkotlinx/coroutines/flow/SharedFlow;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "onLocationCaptured", "", "lat", "", "lng", "onPhotoTaken", "uri", "", "onWasteTypeSelected", "type", "submit", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class QuickActionViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.data.repository.CollectionPointRepository repository = null;
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
    com.example.ecolab.data.repository.CollectionPointRepository repository) {
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
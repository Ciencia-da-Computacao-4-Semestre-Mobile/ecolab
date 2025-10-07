package com.example.ecolab.feature.map;

import androidx.lifecycle.ViewModel;
import com.example.ecolab.core.domain.model.CollectionPoint;
import com.example.ecolab.core.domain.repository.PointsRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0007J\u0006\u0010\u0010\u001a\u00020\u000eR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0011"}, d2 = {"Lcom/example/ecolab/feature/map/MapViewModel;", "Landroidx/lifecycle/ViewModel;", "pointsRepository", "Lcom/example/ecolab/core/domain/repository/PointsRepository;", "(Lcom/example/ecolab/core/domain/repository/PointsRepository;)V", "_selectedCategory", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/example/ecolab/feature/map/MapUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "onFilterChange", "", "category", "refresh", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class MapViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.core.domain.repository.PointsRepository pointsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.map.MapUiState> uiState = null;
    
    @javax.inject.Inject()
    public MapViewModel(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.core.domain.repository.PointsRepository pointsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.map.MapUiState> getUiState() {
        return null;
    }
    
    public final void onFilterChange(@org.jetbrains.annotations.NotNull()
    java.lang.String category) {
    }
    
    public final void refresh() {
    }
}
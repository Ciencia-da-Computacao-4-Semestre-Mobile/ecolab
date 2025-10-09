package com.example.ecolab.feature.map;

import android.location.Geocoder;
import androidx.lifecycle.ViewModel;
import com.example.ecolab.core.domain.model.CollectionPoint;
import com.example.ecolab.core.domain.repository.PointsRepository;
import com.google.android.gms.maps.model.LatLng;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\f\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u001c\u001a\u00020\rJ\u000e\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\nJ\u0006\u0010\u001f\u001a\u00020\u001aJ\u000e\u0010 \u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\rJ\u000e\u0010\"\u001a\u00020\u001a2\u0006\u0010#\u001a\u00020\u0013J\u0006\u0010$\u001a\u00020\u001aJ\u0006\u0010%\u001a\u00020\u001aR\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006&"}, d2 = {"Lcom/example/ecolab/feature/map/MapViewModel;", "Landroidx/lifecycle/ViewModel;", "pointsRepository", "Lcom/example/ecolab/core/domain/repository/PointsRepository;", "geocoder", "Landroid/location/Geocoder;", "(Lcom/example/ecolab/core/domain/repository/PointsRepository;Landroid/location/Geocoder;)V", "_filteredPoints", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/ecolab/core/domain/model/CollectionPoint;", "_searchQuery", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_searchedLocation", "Lcom/google/android/gms/maps/model/LatLng;", "_selectedCategory", "_selectedPoint", "_showFavorites", "", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/example/ecolab/feature/map/MapUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "onDismissBottomSheet", "", "onFilterChange", "category", "onMarkerClick", "point", "onSearch", "onSearchQueryChange", "query", "onToggleFavorites", "isFavorite", "refresh", "toggleFavorite", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class MapViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.core.domain.repository.PointsRepository pointsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.location.Geocoder geocoder = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.ecolab.core.domain.model.CollectionPoint> _selectedPoint = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _showFavorites = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.google.android.gms.maps.model.LatLng> _searchedLocation = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.example.ecolab.core.domain.model.CollectionPoint>> _filteredPoints = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.map.MapUiState> uiState = null;
    
    @javax.inject.Inject()
    public MapViewModel(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.core.domain.repository.PointsRepository pointsRepository, @org.jetbrains.annotations.NotNull()
    android.location.Geocoder geocoder) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.ecolab.feature.map.MapUiState> getUiState() {
        return null;
    }
    
    public final void onFilterChange(@org.jetbrains.annotations.NotNull()
    java.lang.String category) {
    }
    
    public final void onMarkerClick(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.core.domain.model.CollectionPoint point) {
    }
    
    public final void onDismissBottomSheet() {
    }
    
    public final void onSearchQueryChange(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void onSearch() {
    }
    
    public final void onToggleFavorites(boolean isFavorite) {
    }
    
    public final void toggleFavorite() {
    }
    
    public final void refresh() {
    }
}
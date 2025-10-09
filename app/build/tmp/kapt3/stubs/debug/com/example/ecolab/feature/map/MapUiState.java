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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0004\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\u0002\u0010\rJ\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\nH\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\fH\u00c6\u0003JO\u0010\u001f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00c6\u0001J\u0013\u0010 \u001a\u00020\n2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0006H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006%"}, d2 = {"Lcom/example/ecolab/feature/map/MapUiState;", "", "collectionPoints", "", "Lcom/example/ecolab/core/domain/model/CollectionPoint;", "selectedCategory", "", "selectedPoint", "searchQuery", "showFavorites", "", "searchedLocation", "Lcom/google/android/gms/maps/model/LatLng;", "(Ljava/util/List;Ljava/lang/String;Lcom/example/ecolab/core/domain/model/CollectionPoint;Ljava/lang/String;ZLcom/google/android/gms/maps/model/LatLng;)V", "getCollectionPoints", "()Ljava/util/List;", "getSearchQuery", "()Ljava/lang/String;", "getSearchedLocation", "()Lcom/google/android/gms/maps/model/LatLng;", "getSelectedCategory", "getSelectedPoint", "()Lcom/example/ecolab/core/domain/model/CollectionPoint;", "getShowFavorites", "()Z", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class MapUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.ecolab.core.domain.model.CollectionPoint> collectionPoints = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String selectedCategory = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.ecolab.core.domain.model.CollectionPoint selectedPoint = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String searchQuery = null;
    private final boolean showFavorites = false;
    @org.jetbrains.annotations.Nullable()
    private final com.google.android.gms.maps.model.LatLng searchedLocation = null;
    
    public MapUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.ecolab.core.domain.model.CollectionPoint> collectionPoints, @org.jetbrains.annotations.NotNull()
    java.lang.String selectedCategory, @org.jetbrains.annotations.Nullable()
    com.example.ecolab.core.domain.model.CollectionPoint selectedPoint, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, boolean showFavorites, @org.jetbrains.annotations.Nullable()
    com.google.android.gms.maps.model.LatLng searchedLocation) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.ecolab.core.domain.model.CollectionPoint> getCollectionPoints() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSelectedCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.ecolab.core.domain.model.CollectionPoint getSelectedPoint() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSearchQuery() {
        return null;
    }
    
    public final boolean getShowFavorites() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.google.android.gms.maps.model.LatLng getSearchedLocation() {
        return null;
    }
    
    public MapUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.ecolab.core.domain.model.CollectionPoint> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.ecolab.core.domain.model.CollectionPoint component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.google.android.gms.maps.model.LatLng component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.ecolab.feature.map.MapUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.ecolab.core.domain.model.CollectionPoint> collectionPoints, @org.jetbrains.annotations.NotNull()
    java.lang.String selectedCategory, @org.jetbrains.annotations.Nullable()
    com.example.ecolab.core.domain.model.CollectionPoint selectedPoint, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, boolean showFavorites, @org.jetbrains.annotations.Nullable()
    com.google.android.gms.maps.model.LatLng searchedLocation) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}
package com.example.ecolab.feature.home;

import androidx.lifecycle.ViewModel;
import com.example.ecolab.core.domain.model.CollectionPoint;
import com.example.ecolab.core.domain.repository.PointsRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/example/ecolab/feature/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/ecolab/core/domain/repository/PointsRepository;", "(Lcom/example/ecolab/core/domain/repository/PointsRepository;)V", "points", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/example/ecolab/core/domain/model/CollectionPoint;", "getPoints", "()Lkotlinx/coroutines/flow/StateFlow;", "refresh", "", "toggleFavorite", "id", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.core.domain.repository.PointsRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.ecolab.core.domain.model.CollectionPoint>> points = null;
    
    @javax.inject.Inject()
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.core.domain.repository.PointsRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.ecolab.core.domain.model.CollectionPoint>> getPoints() {
        return null;
    }
    
    public final void toggleFavorite(long id) {
    }
    
    public final void refresh() {
    }
}
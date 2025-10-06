package com.example.ecolab.domain;

/**
 * Interface for the collection points data repository, as per the design prompt.
 * This defines the contract for data operations related to collection points.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u000e\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\f\u00a8\u0006\r"}, d2 = {"Lcom/example/ecolab/domain/PointsRepository;", "", "observePoints", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/ecolab/data/model/CollectionPoint;", "refresh", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleFavorite", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface PointsRepository {
    
    /**
     * Observes the list of all collection points.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.ecolab.data.model.CollectionPoint>> observePoints();
    
    /**
     * Toggles the favorite status of a specific point.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object toggleFavorite(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Triggers a refresh of the points data (no-op for mock).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refresh(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}
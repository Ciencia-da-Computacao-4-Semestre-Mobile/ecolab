package com.example.ecolab.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0012\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\f0\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/example/ecolab/data/repository/CollectionPointRepository;", "", "collectionPointDao", "Lcom/example/ecolab/data/model/CollectionPointDao;", "(Lcom/example/ecolab/data/model/CollectionPointDao;)V", "addPoint", "", "point", "Lcom/example/ecolab/data/model/CollectionPoint;", "(Lcom/example/ecolab/data/model/CollectionPoint;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllPoints", "Lkotlinx/coroutines/flow/Flow;", "", "app_debug"})
public final class CollectionPointRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.ecolab.data.model.CollectionPointDao collectionPointDao = null;
    
    @javax.inject.Inject()
    public CollectionPointRepository(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.model.CollectionPointDao collectionPointDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addPoint(@org.jetbrains.annotations.NotNull()
    com.example.ecolab.data.model.CollectionPoint point, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.example.ecolab.data.model.CollectionPoint>> getAllPoints() {
        return null;
    }
}
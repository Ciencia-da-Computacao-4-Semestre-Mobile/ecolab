package com.example.ecolab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecolab.data.model.CollectionPoint
import com.example.ecolab.data.model.CollectionPointDao

@Database(entities = [CollectionPoint::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectionPointDao(): CollectionPointDao
}

package com.example.ecolab.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.ecolab.data.model.CollectionPointDao;
import com.example.ecolab.data.model.CollectionPointDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CollectionPointDao _collectionPointDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `collection_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `address` TEXT NOT NULL, `openingHours` TEXT, `wasteType` TEXT NOT NULL, `photoUri` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `userSubmitted` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9791bfaf05426b94f7ce663b619d0bfc')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `collection_points`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCollectionPoints = new HashMap<String, TableInfo.Column>(9);
        _columnsCollectionPoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("openingHours", new TableInfo.Column("openingHours", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("wasteType", new TableInfo.Column("wasteType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("photoUri", new TableInfo.Column("photoUri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionPoints.put("userSubmitted", new TableInfo.Column("userSubmitted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCollectionPoints = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCollectionPoints = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCollectionPoints = new TableInfo("collection_points", _columnsCollectionPoints, _foreignKeysCollectionPoints, _indicesCollectionPoints);
        final TableInfo _existingCollectionPoints = TableInfo.read(db, "collection_points");
        if (!_infoCollectionPoints.equals(_existingCollectionPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "collection_points(com.example.ecolab.data.model.CollectionPoint).\n"
                  + " Expected:\n" + _infoCollectionPoints + "\n"
                  + " Found:\n" + _existingCollectionPoints);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9791bfaf05426b94f7ce663b619d0bfc", "db4b0095465b1c08d82fba0dac3ed6d5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "collection_points");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `collection_points`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CollectionPointDao.class, CollectionPointDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CollectionPointDao collectionPointDao() {
    if (_collectionPointDao != null) {
      return _collectionPointDao;
    } else {
      synchronized(this) {
        if(_collectionPointDao == null) {
          _collectionPointDao = new CollectionPointDao_Impl(this);
        }
        return _collectionPointDao;
      }
    }
  }
}

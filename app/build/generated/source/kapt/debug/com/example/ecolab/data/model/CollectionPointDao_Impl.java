package com.example.ecolab.data.model;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CollectionPointDao_Impl implements CollectionPointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CollectionPoint> __insertionAdapterOfCollectionPoint;

  public CollectionPointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCollectionPoint = new EntityInsertionAdapter<CollectionPoint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `collection_points` (`id`,`name`,`address`,`openingHours`,`wasteType`,`photoUri`,`latitude`,`longitude`,`userSubmitted`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CollectionPoint entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAddress());
        }
        if (entity.getOpeningHours() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getOpeningHours());
        }
        if (entity.getWasteType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getWasteType());
        }
        if (entity.getPhotoUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPhotoUri());
        }
        statement.bindDouble(7, entity.getLatitude());
        statement.bindDouble(8, entity.getLongitude());
        final int _tmp = entity.getUserSubmitted() ? 1 : 0;
        statement.bindLong(9, _tmp);
      }
    };
  }

  @Override
  public Object insert(final CollectionPoint point, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCollectionPoint.insert(point);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<CollectionPoint> points,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCollectionPoint.insert(points);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CollectionPoint>> getAllPoints() {
    final String _sql = "SELECT * FROM collection_points ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"collection_points"}, new Callable<List<CollectionPoint>>() {
      @Override
      @NonNull
      public List<CollectionPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfOpeningHours = CursorUtil.getColumnIndexOrThrow(_cursor, "openingHours");
          final int _cursorIndexOfWasteType = CursorUtil.getColumnIndexOrThrow(_cursor, "wasteType");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfUserSubmitted = CursorUtil.getColumnIndexOrThrow(_cursor, "userSubmitted");
          final List<CollectionPoint> _result = new ArrayList<CollectionPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CollectionPoint _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final String _tmpOpeningHours;
            if (_cursor.isNull(_cursorIndexOfOpeningHours)) {
              _tmpOpeningHours = null;
            } else {
              _tmpOpeningHours = _cursor.getString(_cursorIndexOfOpeningHours);
            }
            final String _tmpWasteType;
            if (_cursor.isNull(_cursorIndexOfWasteType)) {
              _tmpWasteType = null;
            } else {
              _tmpWasteType = _cursor.getString(_cursorIndexOfWasteType);
            }
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final boolean _tmpUserSubmitted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUserSubmitted);
            _tmpUserSubmitted = _tmp != 0;
            _item = new CollectionPoint(_tmpId,_tmpName,_tmpAddress,_tmpOpeningHours,_tmpWasteType,_tmpPhotoUri,_tmpLatitude,_tmpLongitude,_tmpUserSubmitted);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

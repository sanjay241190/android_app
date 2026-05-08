package com.riffstealer.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
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
public final class RiffDao_Impl implements RiffDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MelodyEntity> __insertionAdapterOfMelodyEntity;

  private final EntityInsertionAdapter<VariationEntity> __insertionAdapterOfVariationEntity;

  private final EntityDeletionOrUpdateAdapter<MelodyEntity> __deletionAdapterOfMelodyEntity;

  private final EntityDeletionOrUpdateAdapter<VariationEntity> __deletionAdapterOfVariationEntity;

  private final EntityDeletionOrUpdateAdapter<VariationEntity> __updateAdapterOfVariationEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetFavorite;

  public RiffDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMelodyEntity = new EntityInsertionAdapter<MelodyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `melodies` (`id`,`name`,`abcNotation`,`bpm`,`noteSequence`,`createdAt`,`durationMs`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MelodyEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getAbcNotation());
        statement.bindLong(4, entity.getBpm());
        statement.bindString(5, entity.getNoteSequence());
        statement.bindLong(6, entity.getCreatedAt());
        statement.bindLong(7, entity.getDurationMs());
      }
    };
    this.__insertionAdapterOfVariationEntity = new EntityInsertionAdapter<VariationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `variations` (`id`,`melodyId`,`genre`,`mood`,`tempo`,`abcNotation`,`description`,`isFavorite`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VariationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMelodyId());
        statement.bindString(3, entity.getGenre());
        statement.bindString(4, entity.getMood());
        statement.bindLong(5, entity.getTempo());
        statement.bindString(6, entity.getAbcNotation());
        statement.bindString(7, entity.getDescription());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfMelodyEntity = new EntityDeletionOrUpdateAdapter<MelodyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `melodies` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MelodyEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfVariationEntity = new EntityDeletionOrUpdateAdapter<VariationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `variations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VariationEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVariationEntity = new EntityDeletionOrUpdateAdapter<VariationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `variations` SET `id` = ?,`melodyId` = ?,`genre` = ?,`mood` = ?,`tempo` = ?,`abcNotation` = ?,`description` = ?,`isFavorite` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VariationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMelodyId());
        statement.bindString(3, entity.getGenre());
        statement.bindString(4, entity.getMood());
        statement.bindLong(5, entity.getTempo());
        statement.bindString(6, entity.getAbcNotation());
        statement.bindString(7, entity.getDescription());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfSetFavorite = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE variations SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMelody(final MelodyEntity melody,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMelodyEntity.insertAndReturnId(melody);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertVariation(final VariationEntity variation,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVariationEntity.insertAndReturnId(variation);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertVariations(final List<VariationEntity> variations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVariationEntity.insert(variations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMelody(final MelodyEntity melody,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMelodyEntity.handle(melody);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteVariation(final VariationEntity variation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVariationEntity.handle(variation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateVariation(final VariationEntity variation,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVariationEntity.handle(variation);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setFavorite(final long id, final boolean isFavorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetFavorite.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetFavorite.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MelodyEntity>> getAllMelodies() {
    final String _sql = "SELECT * FROM melodies ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"melodies"}, new Callable<List<MelodyEntity>>() {
      @Override
      @NonNull
      public List<MelodyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAbcNotation = CursorUtil.getColumnIndexOrThrow(_cursor, "abcNotation");
          final int _cursorIndexOfBpm = CursorUtil.getColumnIndexOrThrow(_cursor, "bpm");
          final int _cursorIndexOfNoteSequence = CursorUtil.getColumnIndexOrThrow(_cursor, "noteSequence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final List<MelodyEntity> _result = new ArrayList<MelodyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MelodyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpAbcNotation;
            _tmpAbcNotation = _cursor.getString(_cursorIndexOfAbcNotation);
            final int _tmpBpm;
            _tmpBpm = _cursor.getInt(_cursorIndexOfBpm);
            final String _tmpNoteSequence;
            _tmpNoteSequence = _cursor.getString(_cursorIndexOfNoteSequence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            _item = new MelodyEntity(_tmpId,_tmpName,_tmpAbcNotation,_tmpBpm,_tmpNoteSequence,_tmpCreatedAt,_tmpDurationMs);
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

  @Override
  public Object getMelodyById(final long id, final Continuation<? super MelodyEntity> $completion) {
    final String _sql = "SELECT * FROM melodies WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MelodyEntity>() {
      @Override
      @Nullable
      public MelodyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAbcNotation = CursorUtil.getColumnIndexOrThrow(_cursor, "abcNotation");
          final int _cursorIndexOfBpm = CursorUtil.getColumnIndexOrThrow(_cursor, "bpm");
          final int _cursorIndexOfNoteSequence = CursorUtil.getColumnIndexOrThrow(_cursor, "noteSequence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final MelodyEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpAbcNotation;
            _tmpAbcNotation = _cursor.getString(_cursorIndexOfAbcNotation);
            final int _tmpBpm;
            _tmpBpm = _cursor.getInt(_cursorIndexOfBpm);
            final String _tmpNoteSequence;
            _tmpNoteSequence = _cursor.getString(_cursorIndexOfNoteSequence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            _result = new MelodyEntity(_tmpId,_tmpName,_tmpAbcNotation,_tmpBpm,_tmpNoteSequence,_tmpCreatedAt,_tmpDurationMs);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VariationEntity>> getVariationsForMelody(final long melodyId) {
    final String _sql = "SELECT * FROM variations WHERE melodyId = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, melodyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"variations"}, new Callable<List<VariationEntity>>() {
      @Override
      @NonNull
      public List<VariationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMelodyId = CursorUtil.getColumnIndexOrThrow(_cursor, "melodyId");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfTempo = CursorUtil.getColumnIndexOrThrow(_cursor, "tempo");
          final int _cursorIndexOfAbcNotation = CursorUtil.getColumnIndexOrThrow(_cursor, "abcNotation");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<VariationEntity> _result = new ArrayList<VariationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VariationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMelodyId;
            _tmpMelodyId = _cursor.getLong(_cursorIndexOfMelodyId);
            final String _tmpGenre;
            _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpTempo;
            _tmpTempo = _cursor.getInt(_cursorIndexOfTempo);
            final String _tmpAbcNotation;
            _tmpAbcNotation = _cursor.getString(_cursorIndexOfAbcNotation);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new VariationEntity(_tmpId,_tmpMelodyId,_tmpGenre,_tmpMood,_tmpTempo,_tmpAbcNotation,_tmpDescription,_tmpIsFavorite,_tmpCreatedAt);
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

  @Override
  public Object getVariationById(final long id,
      final Continuation<? super VariationEntity> $completion) {
    final String _sql = "SELECT * FROM variations WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VariationEntity>() {
      @Override
      @Nullable
      public VariationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMelodyId = CursorUtil.getColumnIndexOrThrow(_cursor, "melodyId");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfTempo = CursorUtil.getColumnIndexOrThrow(_cursor, "tempo");
          final int _cursorIndexOfAbcNotation = CursorUtil.getColumnIndexOrThrow(_cursor, "abcNotation");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final VariationEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMelodyId;
            _tmpMelodyId = _cursor.getLong(_cursorIndexOfMelodyId);
            final String _tmpGenre;
            _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpTempo;
            _tmpTempo = _cursor.getInt(_cursorIndexOfTempo);
            final String _tmpAbcNotation;
            _tmpAbcNotation = _cursor.getString(_cursorIndexOfAbcNotation);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new VariationEntity(_tmpId,_tmpMelodyId,_tmpGenre,_tmpMood,_tmpTempo,_tmpAbcNotation,_tmpDescription,_tmpIsFavorite,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VariationEntity>> getFavoriteVariations() {
    final String _sql = "SELECT * FROM variations WHERE isFavorite = 1 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"variations"}, new Callable<List<VariationEntity>>() {
      @Override
      @NonNull
      public List<VariationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMelodyId = CursorUtil.getColumnIndexOrThrow(_cursor, "melodyId");
          final int _cursorIndexOfGenre = CursorUtil.getColumnIndexOrThrow(_cursor, "genre");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfTempo = CursorUtil.getColumnIndexOrThrow(_cursor, "tempo");
          final int _cursorIndexOfAbcNotation = CursorUtil.getColumnIndexOrThrow(_cursor, "abcNotation");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<VariationEntity> _result = new ArrayList<VariationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VariationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMelodyId;
            _tmpMelodyId = _cursor.getLong(_cursorIndexOfMelodyId);
            final String _tmpGenre;
            _tmpGenre = _cursor.getString(_cursorIndexOfGenre);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpTempo;
            _tmpTempo = _cursor.getInt(_cursorIndexOfTempo);
            final String _tmpAbcNotation;
            _tmpAbcNotation = _cursor.getString(_cursorIndexOfAbcNotation);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new VariationEntity(_tmpId,_tmpMelodyId,_tmpGenre,_tmpMood,_tmpTempo,_tmpAbcNotation,_tmpDescription,_tmpIsFavorite,_tmpCreatedAt);
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

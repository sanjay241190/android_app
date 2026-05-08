package com.riffstealer.app.data;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RiffDatabase_Impl extends RiffDatabase {
  private volatile RiffDao _riffDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `melodies` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `abcNotation` TEXT NOT NULL, `bpm` INTEGER NOT NULL, `noteSequence` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `durationMs` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `variations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `melodyId` INTEGER NOT NULL, `genre` TEXT NOT NULL, `mood` TEXT NOT NULL, `tempo` INTEGER NOT NULL, `abcNotation` TEXT NOT NULL, `description` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`melodyId`) REFERENCES `melodies`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_variations_melodyId` ON `variations` (`melodyId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '322db2376b4ccbdf6f7516bd95eedbd3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `melodies`");
        db.execSQL("DROP TABLE IF EXISTS `variations`");
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
        db.execSQL("PRAGMA foreign_keys = ON");
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
        final HashMap<String, TableInfo.Column> _columnsMelodies = new HashMap<String, TableInfo.Column>(7);
        _columnsMelodies.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMelodies.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMelodies.put("abcNotation", new TableInfo.Column("abcNotation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMelodies.put("bpm", new TableInfo.Column("bpm", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMelodies.put("noteSequence", new TableInfo.Column("noteSequence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMelodies.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMelodies.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMelodies = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMelodies = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMelodies = new TableInfo("melodies", _columnsMelodies, _foreignKeysMelodies, _indicesMelodies);
        final TableInfo _existingMelodies = TableInfo.read(db, "melodies");
        if (!_infoMelodies.equals(_existingMelodies)) {
          return new RoomOpenHelper.ValidationResult(false, "melodies(com.riffstealer.app.data.MelodyEntity).\n"
                  + " Expected:\n" + _infoMelodies + "\n"
                  + " Found:\n" + _existingMelodies);
        }
        final HashMap<String, TableInfo.Column> _columnsVariations = new HashMap<String, TableInfo.Column>(9);
        _columnsVariations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("melodyId", new TableInfo.Column("melodyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("genre", new TableInfo.Column("genre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("mood", new TableInfo.Column("mood", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("tempo", new TableInfo.Column("tempo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("abcNotation", new TableInfo.Column("abcNotation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVariations.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVariations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVariations.add(new TableInfo.ForeignKey("melodies", "CASCADE", "NO ACTION", Arrays.asList("melodyId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesVariations = new HashSet<TableInfo.Index>(1);
        _indicesVariations.add(new TableInfo.Index("index_variations_melodyId", false, Arrays.asList("melodyId"), Arrays.asList("ASC")));
        final TableInfo _infoVariations = new TableInfo("variations", _columnsVariations, _foreignKeysVariations, _indicesVariations);
        final TableInfo _existingVariations = TableInfo.read(db, "variations");
        if (!_infoVariations.equals(_existingVariations)) {
          return new RoomOpenHelper.ValidationResult(false, "variations(com.riffstealer.app.data.VariationEntity).\n"
                  + " Expected:\n" + _infoVariations + "\n"
                  + " Found:\n" + _existingVariations);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "322db2376b4ccbdf6f7516bd95eedbd3", "c7fe3f2d3823e9fee9f512768d4ef9ec");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "melodies","variations");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `melodies`");
      _db.execSQL("DELETE FROM `variations`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
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
    _typeConvertersMap.put(RiffDao.class, RiffDao_Impl.getRequiredConverters());
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
  public RiffDao riffDao() {
    if (_riffDao != null) {
      return _riffDao;
    } else {
      synchronized(this) {
        if(_riffDao == null) {
          _riffDao = new RiffDao_Impl(this);
        }
        return _riffDao;
      }
    }
  }
}

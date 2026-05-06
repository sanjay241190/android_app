package com.spotphoto.saver.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PhotoSpot::class], version = 2, exportSchema = false)
abstract class PhotoSpotDatabase : RoomDatabase() {

    abstract fun photoSpotDao(): PhotoSpotDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoSpotDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE photo_spots ADD COLUMN category TEXT NOT NULL DEFAULT 'general'")
            }
        }

        fun getDatabase(context: Context): PhotoSpotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoSpotDatabase::class.java,
                    "photo_spots_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

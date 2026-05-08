package com.riffstealer.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MelodyEntity::class, VariationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RiffDatabase : RoomDatabase() {
    abstract fun riffDao(): RiffDao

    companion object {
        @Volatile
        private var INSTANCE: RiffDatabase? = null

        fun getDatabase(context: Context): RiffDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RiffDatabase::class.java,
                    "riffstealer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example.zcode.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase - Room database for Zcode application
 *
 * Centralizes all database operations and provides singleton access to the database
 */
@Database(
    entities = [UserPreferences::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get or create database instance
         *
         * Uses double-checked locking pattern for thread-safe singleton
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zcode_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}


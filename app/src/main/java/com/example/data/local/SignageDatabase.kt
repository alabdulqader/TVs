package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ScreenEntity::class,
        AnnouncementEntity::class,
        MeetingEntity::class,
        TaskEntity::class,
        KpiEntity::class,
        WeatherCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SignageDatabase : RoomDatabase() {
    abstract fun signageDao(): SignageDao

    companion object {
        @Volatile
        private var INSTANCE: SignageDatabase? = null

        fun getInstance(context: Context): SignageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SignageDatabase::class.java,
                    "smart_display_lite.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

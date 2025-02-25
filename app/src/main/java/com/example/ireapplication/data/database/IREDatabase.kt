package com.example.ireapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ireapplication.data.models.Floor
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.dao.FloorDao
import com.example.ireapplication.data.dao.ExhibitDao

@Database(
    entities = [Floor::class, Exhibit::class],
    version = 2,
    exportSchema = false
)
abstract class IREDatabase : RoomDatabase() {
    abstract fun floorDao(): FloorDao
    abstract fun exhibitDao(): ExhibitDao

    companion object {
        @Volatile
        private var INSTANCE: IREDatabase? = null

        fun getDatabase(context: Context): IREDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IREDatabase::class.java,
                    "ire_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 
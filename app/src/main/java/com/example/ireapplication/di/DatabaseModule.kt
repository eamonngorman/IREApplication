package com.example.ireapplication.di

import android.content.Context
import com.example.ireapplication.data.database.IREDatabase
import com.example.ireapplication.data.dao.FloorDao
import com.example.ireapplication.data.dao.ExhibitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): IREDatabase {
        return IREDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideFloorDao(database: IREDatabase): FloorDao {
        return database.floorDao()
    }

    @Singleton
    @Provides
    fun provideExhibitDao(database: IREDatabase): ExhibitDao {
        return database.exhibitDao()
    }
} 
package com.example.ireapplication.data.dao

import androidx.room.*
import com.example.ireapplication.data.models.Exhibit
import kotlinx.coroutines.flow.Flow

@Dao
interface ExhibitDao {
    @Query("SELECT * FROM exhibits WHERE floorId = :floorId")
    fun getExhibitsForFloor(floorId: Int): Flow<List<Exhibit>>

    @Query("SELECT * FROM exhibits WHERE id = :exhibitId")
    fun getExhibit(exhibitId: Int): Flow<Exhibit>

    @Query("SELECT * FROM exhibits WHERE name LIKE '%' || :query || '%' OR shortDescription LIKE '%' || :query || '%' OR fullDescription LIKE '%' || :query || '%'")
    fun searchExhibits(query: String): Flow<List<Exhibit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExhibit(exhibit: Exhibit): Long

    @Update
    suspend fun updateExhibit(exhibit: Exhibit)

    @Delete
    suspend fun deleteExhibit(exhibit: Exhibit)

    @Query("DELETE FROM exhibits")
    suspend fun deleteAllExhibits()
} 
package com.example.ireapplication.data.dao

import androidx.room.*
import com.example.ireapplication.data.models.Floor
import com.example.ireapplication.data.models.FloorWithExhibits
import kotlinx.coroutines.flow.Flow

@Dao
interface FloorDao {
    @Query("SELECT * FROM floors ORDER BY level")
    fun getAllFloors(): Flow<List<Floor>>

    @Query("SELECT * FROM floors WHERE id = :floorId")
    fun getFloorById(floorId: Int): Flow<Floor>

    @Transaction
    @Query("SELECT * FROM floors ORDER BY level")
    fun getFloorsWithExhibits(): Flow<List<FloorWithExhibits>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFloor(floor: Floor): Long

    @Update
    suspend fun updateFloor(floor: Floor)

    @Delete
    suspend fun deleteFloor(floor: Floor)

    @Query("DELETE FROM floors")
    suspend fun deleteAllFloors()
} 
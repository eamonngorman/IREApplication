package com.example.ireapplication.data.repository

import com.example.ireapplication.data.dao.FloorDao
import com.example.ireapplication.data.dao.ExhibitDao
import com.example.ireapplication.data.models.Floor
import com.example.ireapplication.data.models.Exhibit
import com.example.ireapplication.data.models.FloorWithExhibits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IRERepository @Inject constructor(
    private val floorDao: FloorDao,
    private val exhibitDao: ExhibitDao
) {
    // Floor-related operations
    val allFloorsWithExhibits: Flow<List<FloorWithExhibits>> = floorDao.getFloorsWithExhibits()

    suspend fun getFloorById(floorId: Int): Floor? {
        return floorDao.getFloorById(floorId).firstOrNull()
    }

    suspend fun insertFloor(floor: Floor): Long {
        return floorDao.insertFloor(floor)
    }

    // Exhibit-related operations
    suspend fun getExhibit(exhibitId: Int): Exhibit? {
        return exhibitDao.getExhibit(exhibitId).firstOrNull()
    }

    fun searchExhibits(query: String): Flow<List<Exhibit>> {
        return exhibitDao.searchExhibits(query)
    }

    fun getExhibitsForFloor(floorId: Int): Flow<List<Exhibit>> {
        return exhibitDao.getExhibitsForFloor(floorId)
    }

    suspend fun insertExhibit(exhibit: Exhibit): Long {
        return exhibitDao.insertExhibit(exhibit)
    }

    // Database population
    suspend fun populateDatabase(floors: List<Floor>, exhibits: List<Exhibit>) {
        // First, clear existing data
        exhibitDao.deleteAllExhibits()
        floorDao.deleteAllFloors()

        // Insert floors and get their IDs
        val floorIdMap = mutableMapOf<Int, Int>() // Map of level to actual floor ID
        floors.forEach { floor ->
            val id = insertFloor(floor).toInt()
            floorIdMap[floor.level] = id
        }

        // Insert exhibits with correct floor IDs
        exhibits.forEach { exhibit ->
            val actualFloorId = floorIdMap[exhibit.floorId] ?: return@forEach
            val updatedExhibit = exhibit.copy(floorId = actualFloorId)
            insertExhibit(updatedExhibit)
        }
    }
} 
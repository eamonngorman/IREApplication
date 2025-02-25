package com.example.ireapplication.data.repository

import com.example.ireapplication.data.dao.ExhibitDao
import com.example.ireapplication.data.models.Exhibit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExhibitRepository @Inject constructor(
    private val exhibitDao: ExhibitDao
) {
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

    suspend fun updateExhibit(exhibit: Exhibit) {
        exhibitDao.updateExhibit(exhibit)
    }

    suspend fun deleteExhibit(exhibit: Exhibit) {
        exhibitDao.deleteExhibit(exhibit)
    }
} 
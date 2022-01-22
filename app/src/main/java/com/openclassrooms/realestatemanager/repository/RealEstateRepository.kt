package com.openclassrooms.realestatemanager.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.flow.Flow

class RealEstateRepository(private val realEstateDao: RealEstateDao) {

    val getAllRealEstate: Flow<List<RealEstate>> = realEstateDao.getAllRealEstate()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(realEstate: RealEstate) {
        realEstateDao.insert(realEstate)
    }
}
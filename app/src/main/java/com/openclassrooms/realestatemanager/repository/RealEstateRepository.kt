package com.openclassrooms.realestatemanager.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.flow.Flow

class RealEstateRepository(private val realEstateDao: RealEstateDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(realEstate: RealEstate) {
        realEstateDao.insert(realEstate)
    }

    suspend fun update(realEstate: RealEstate) {
        realEstateDao.updateRealEstate(realEstate)
    }

    val getAllRealEstate: Flow<List<RealEstate>> = realEstateDao.getAllRealEstate()
    fun getRealEstateByAddress(realEstateAddress: String) : Flow<RealEstate> {
        return realEstateDao.getRealEstateByAddress(realEstateAddress)
    }

    //TODO add get real estate by id
}
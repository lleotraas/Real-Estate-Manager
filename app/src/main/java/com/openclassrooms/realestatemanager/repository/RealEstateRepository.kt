package com.openclassrooms.realestatemanager.repository

import androidx.annotation.WorkerThread
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.flow.Flow

class RealEstateRepository(private val realEstateDao: RealEstateDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(realEstate: RealEstate): Long {
        return realEstateDao.insert(realEstate)
    }

    suspend fun update(realEstate: RealEstate) {
        realEstateDao.updateRealEstate(realEstate)
    }

    val getAllRealEstate: Flow<List<RealEstate>> = realEstateDao.getAllRealEstate()
    fun getRealEstateByAddress(realEstateAddress: String) : Flow<RealEstate> {
        return realEstateDao.getRealEstateByAddress(realEstateAddress)
    }
    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate> {
        return realEstateDao.searchRealEstateWithParameters(query)
    }

    //TODO add get real estate by id

}
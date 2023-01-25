package com.openclassrooms.realestatemanager.features_real_estate.data.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RealEstateRepositoryImpl @Inject constructor (
    private val realEstateDao: RealEstateDao
    ) : RealEstateRepository {

    override suspend fun insert(realEstate: RealEstate): Long {
        return realEstateDao.insert(realEstate)
    }

    override suspend fun update(realEstate: RealEstate) {
        realEstateDao.updateRealEstate(realEstate)
    }

    override fun getRealEstateByAddress(realEstateAddress: String) : Flow<RealEstate> {
        return realEstateDao.getRealEstateByAddress(realEstateAddress)
    }

    override fun getRealEstateById(id: Long): Flow<RealEstate> {
        return realEstateDao.getRealEstateById(id)
    }

    override suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery) = realEstateDao.searchRealEstateWithParameters(query)

}
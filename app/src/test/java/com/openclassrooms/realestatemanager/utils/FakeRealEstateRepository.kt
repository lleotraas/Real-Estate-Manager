package com.openclassrooms.realestatemanager.utils

import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.REAL_ESTATE_1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRealEstateRepository : RealEstateRepository {

    private val realEstates = mutableListOf<RealEstate>()

    override suspend fun insert(realEstate: RealEstate): Long {
        realEstates.add(realEstate)
        return realEstate.id
    }

    override suspend fun update(realEstate: RealEstate) {
        for (i in realEstates.indices) {
            if (realEstates[i].id == realEstate.id) {
                realEstates[i] = realEstate
            }
        }
    }

    override fun getRealEstateByAddress(realEstateAddress: String): Flow<RealEstate> {
        var searchedRealEstate = REAL_ESTATE_1
        realEstates.forEach { realEstate ->
            if (realEstate.address == realEstateAddress) {
                searchedRealEstate = realEstate
            }
        }
        return flow { emit(searchedRealEstate) }
    }

    override fun getRealEstateById(id: Long): Flow<RealEstate> {
        var searchedRealEstate = REAL_ESTATE_1
        realEstates.forEach { realEstate ->
            if (realEstate.id == id) {
                searchedRealEstate = realEstate
            }
        }
        return flow { emit(searchedRealEstate) }
    }

    override suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate> {
        return realEstates
    }
}
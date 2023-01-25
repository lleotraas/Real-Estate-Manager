package com.openclassrooms.realestatemanager.features_real_estate.domain.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Adresse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RealEstateRepository {

    suspend fun insert(realEstate: RealEstate): Long

    suspend fun update(realEstate: RealEstate)

    fun getRealEstateByAddress(realEstateAddress: String) : Flow<RealEstate>

    fun getRealEstateById(id: Long): Flow<RealEstate>

    suspend fun searchRealEstateWithParameters(query: SupportSQLiteQuery): List<RealEstate>

}
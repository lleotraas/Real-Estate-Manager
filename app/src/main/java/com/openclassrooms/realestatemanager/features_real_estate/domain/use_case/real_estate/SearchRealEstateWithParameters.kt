package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate

import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository

class SearchRealEstateWithParameters(
    private val repository: RealEstateRepository
) {

    suspend operator fun invoke(query: SimpleSQLiteQuery): List<RealEstate> = repository.searchRealEstateWithParameters(query)

}
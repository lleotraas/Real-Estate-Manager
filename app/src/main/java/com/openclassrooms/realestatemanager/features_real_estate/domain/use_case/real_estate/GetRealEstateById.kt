package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow

class GetRealEstateById(
    private val repository: RealEstateRepository
) {

    operator fun invoke(id: Long): Flow<RealEstate> = repository.getRealEstateById(id)

}
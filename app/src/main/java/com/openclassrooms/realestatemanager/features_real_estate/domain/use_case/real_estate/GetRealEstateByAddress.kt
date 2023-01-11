package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GetRealEstateByAddress(
    private val repository: RealEstateRepository
) {

    operator fun invoke(address: String): Flow<RealEstate> = repository.getRealEstateByAddress(address)

}
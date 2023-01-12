package com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository

class AddRealEstate(
    private val repository: RealEstateRepository
) {

    suspend operator fun invoke(realEstate: RealEstate): Long = repository.insert(realEstate)

}
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstateRepository

class UpdateRealEstate(
    private val repository: RealEstateRepository
) {

    suspend operator fun invoke(realEstate: RealEstate) {
        repository.update(realEstate)
    }

}
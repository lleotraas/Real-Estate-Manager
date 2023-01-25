package com.openclassrooms.realestatemanager.features_real_estate.presentation

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto

data class RealEstateState(
    val realEstate: RealEstate? = null,
    val realEstates: List<RealEstate> = emptyList(),
    val realEstatePhotoState: List<RealEstatePhoto> = emptyList()
)
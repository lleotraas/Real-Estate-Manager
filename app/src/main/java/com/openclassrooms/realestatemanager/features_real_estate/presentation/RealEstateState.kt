package com.openclassrooms.realestatemanager.features_real_estate.presentation

import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto

data class RealEstateState(
    val realEstate: RealEstate? = null,
    val realEstates: List<RealEstate> = emptyList(),
    val realEstatePhotoState: List<RealEstatePhoto> = emptyList(),
    val query: SimpleSQLiteQuery = SimpleSQLiteQuery("SELECT * FROM real_estate")
)
package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate

data class RealEstateUseCases(
    val addRealEstate: AddRealEstate,
    val getAllRelEstate: GetAllRelEstate,
    val getRealEstateByAddress: GetRealEstateByAddress,
    val getRealEstateById: GetRealEstateById,
    val searchRealEstateWithParameters: SearchRealEstateWithParameters,
    val updateRealEstate: UpdateRealEstate,
)
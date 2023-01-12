package com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case

import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.autocomplete.GetAutocompleteApi
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate.AddRealEstate
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate_photo.DeleteRealEstatePhoto
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate_photo.InsertPhoto
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate_photo.UpdateRealEstatePhoto

class AddRealEstateUseCases(
    val getAutocompleteApi: GetAutocompleteApi,
    val addRealEstate: AddRealEstate,
    val deleteRealEstate: DeleteRealEstatePhoto,
    val insertPhoto: InsertPhoto,
    val updateRealEstatePhoto: UpdateRealEstatePhoto
)
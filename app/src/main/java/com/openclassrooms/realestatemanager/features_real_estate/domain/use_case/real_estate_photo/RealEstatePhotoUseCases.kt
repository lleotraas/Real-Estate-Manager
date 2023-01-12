package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo

import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate_photo.DeleteRealEstatePhoto
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate_photo.InsertPhoto
import com.openclassrooms.realestatemanager.features_add_real_estate.domain.use_case.real_estate_photo.UpdateRealEstatePhoto

class RealEstatePhotoUseCases(
    val getAllRealEstatePhoto: GetAllRealEstatePhoto,
    val insertPhoto: InsertPhoto,
    val deleteRealEstatePhoto: DeleteRealEstatePhoto,
    val updateRealEstatePhoto: UpdateRealEstatePhoto
)
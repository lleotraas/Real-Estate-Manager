package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstatePhotoRepository

class UpdateRealEstatePhoto(
    private val repository: RealEstatePhotoRepository
) {

    suspend operator fun invoke(realEstatePhoto: RealEstatePhoto) = repository.updateRealEstatePhoto(realEstatePhoto)

}
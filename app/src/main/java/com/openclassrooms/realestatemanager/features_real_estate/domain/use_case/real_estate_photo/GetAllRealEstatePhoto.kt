package com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate_photo

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstatePhotoRepository
import kotlinx.coroutines.flow.Flow

class GetAllRealEstatePhoto(
    private val repository: RealEstatePhotoRepository
) {

    operator fun invoke(id: Long): Flow<List<RealEstatePhoto>> = repository.getRealEstatePhotos(id)

}
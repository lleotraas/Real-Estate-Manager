package com.openclassrooms.realestatemanager.features_real_estate.domain.repository

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import kotlinx.coroutines.flow.Flow

interface RealEstatePhotoRepository {

    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long

    fun getRealEstatePhotos(id: Long): Flow<List<RealEstatePhoto>>

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto)

    suspend fun deleteRealEstatePhoto(photoId: Long)

}
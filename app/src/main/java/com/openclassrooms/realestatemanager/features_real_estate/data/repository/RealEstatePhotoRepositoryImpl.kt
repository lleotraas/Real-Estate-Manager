package com.openclassrooms.realestatemanager.features_real_estate.data.repository

import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.features_real_estate.domain.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealEstatePhotoRepositoryImpl @Inject constructor(
    private val realEstatePhotoDao: RealEstatePhotoDao
    ): RealEstatePhotoRepository {

    override suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long {
        return realEstatePhotoDao.insertPhoto(realEstatePhoto)
    }

    override fun getRealEstatePhotos(id: Long): Flow<List<RealEstatePhoto>>{
       return realEstatePhotoDao.getRealEstatePhotos(id)
    }

    override suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoDao.updateRealEstatePhotos(realEstatePhoto)
    }

    override suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoDao.deleteRealEstatePhoto(photoId)
    }
}
package com.openclassrooms.realestatemanager.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import kotlinx.coroutines.flow.Flow

class RealEstatePhotoRepository(private val realEstatePhotoDao: RealEstatePhotoDao) {

    @WorkerThread
    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long {
        return realEstatePhotoDao.insertPhoto(realEstatePhoto)
    }

    fun getRealEstatePhotos(id: Long): Flow<List<RealEstatePhoto>>{
       return realEstatePhotoDao.getRealEstatePhotos(id)
    }

    suspend fun updateRealEstatePhoto(realEstatePhoto: RealEstatePhoto) {
        realEstatePhotoDao.updateRealEstatePhotos(realEstatePhoto)
    }

    suspend fun deleteRealEstatePhoto(photoId: Long) {
        realEstatePhotoDao.deleteRealEstatePhoto(photoId)
    }
}
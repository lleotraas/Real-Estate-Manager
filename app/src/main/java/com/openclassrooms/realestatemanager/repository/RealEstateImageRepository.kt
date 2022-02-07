package com.openclassrooms.realestatemanager.repository

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.dao.RealEstateImageDao
import com.openclassrooms.realestatemanager.model.RealEstateImage
import kotlinx.coroutines.flow.Flow

class RealEstateImageRepository(private val realEstateRealEstateImageDao: RealEstateImageDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(realEstateImage: RealEstateImage) {
        realEstateRealEstateImageDao.insert(realEstateImage)
    }

    fun getRealEstateAndImage(id: Long): Flow<RealEstateImage>{
       return realEstateRealEstateImageDao.getRealEstateAndImage(id)
    }
}
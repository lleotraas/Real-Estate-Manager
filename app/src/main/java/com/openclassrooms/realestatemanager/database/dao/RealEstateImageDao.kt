package com.openclassrooms.realestatemanager.database.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.model.RealEstateImage
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstateImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(realEstateImage: RealEstateImage)

    @Query("SELECT * FROM real_estate_image WHERE real_estate_id = :id")
    fun getRealEstateAndImage(id: Long): Flow<RealEstateImage>

//    @Query("DELETE FROM real_estate_image")
//    suspend fun deleteImage(id: Long)
}
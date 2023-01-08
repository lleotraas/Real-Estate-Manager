package com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface RealEstatePhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(realEstatePhoto: RealEstatePhoto): Long

    @Query("SELECT * FROM real_estate_image WHERE real_estate_id = :id")
    fun getRealEstatePhotos(id: Long): Flow<List<RealEstatePhoto>>

    @Update
    suspend fun updateRealEstatePhotos(realEstatePhoto: RealEstatePhoto)

    @Query("DELETE FROM real_estate_image WHERE id = :id")
    suspend fun deleteRealEstatePhoto(id: Long)

    @Query("SELECT * FROM real_estate_image WHERE id = :id")
    fun getRealEstatePhotoWithCursor(id: Long): Cursor
}
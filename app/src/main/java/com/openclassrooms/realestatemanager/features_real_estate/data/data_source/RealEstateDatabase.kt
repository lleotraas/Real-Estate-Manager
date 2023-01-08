package com.openclassrooms.realestatemanager.features_real_estate.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.Converters
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto

@Database(
    entities = [RealEstate::class, RealEstatePhoto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase(){

    abstract fun realEstateDao(): RealEstateDao
    abstract fun realEstatePhotoDao(): RealEstatePhotoDao

}
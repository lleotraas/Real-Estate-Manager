package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.database.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto

@Database(
    entities = [RealEstate::class, RealEstatePhoto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase(){

    abstract fun realEstateDao(): RealEstateDao
    abstract fun realEstatePhotoDao(): RealEstatePhotoDao


    companion object {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null

        fun getDatabase(context: Context): RealEstateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java,
                    "real_estate_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.openclassrooms.realestatemanager.database

import android.app.Application
import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.database.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
        private val isRunningTest = RealEstateApplication.isRunningTest
        private val databaseName = if (isRunningTest) "real_estate_database_test" else "real_estate_database"

        fun getDatabase(context: Context): RealEstateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java,
                    databaseName
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
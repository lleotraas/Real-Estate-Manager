package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.database.dao.RealEstateImageDao
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstateImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [RealEstate::class, RealEstateImage::class], version = 1, exportSchema = false)
abstract class RealEstateDatabase : RoomDatabase(){

    abstract fun realEstateDao(): RealEstateDao
    abstract fun realEstateImageDao(): RealEstateImageDao

    private class RealEstateCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
//                    var realEstateDao = database.realEstateDao()
                    //TODO check if need this method
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RealEstateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateDatabase::class.java,
                    "real_estate_database"
                )
                    .addCallback(RealEstateCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
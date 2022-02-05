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
            INSTANCE?.let { database ->
                scope.launch {
                    var realEstateDao = database.realEstateDao()
                    //TODO check if need this method
                    realEstateDao.deleteAll()

                    realEstateDao.insert(RealEstate(3, "manoir", "300000", "163", "15", "3", "10", "dfb;kj:mlk!:jk,nfgdvtyjkuodfs", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220205_204410546.png", "rue du mont ventoux, agde", "43.6344799", "4.0395957", "rien", "herault", "15/06/2021", "", "", ""))
                    realEstateDao.insert(RealEstate(3, "villa", "112000", "105", "5", "1", "3", "dfb;kj:mlk!:jk,nfgdvtyjkuodfs", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220205_204410546.png", "rue du mont ventoux, agde", "43.9344799", "4.3395957", "rien", "herault", "02/03/2021", "", "", ""))
                    realEstateDao.insert(RealEstate(3, "appartement", "180000", "65", "4", "1", "2", "dfb;kj:mlk!:jk,nfgdvtyjkuodfs", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220205_204410546.png", "rue du mont ventoux, agde", "43.0344799", "4.9395957", "rien", "herault", "21/09/2021", "", "", ""))
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
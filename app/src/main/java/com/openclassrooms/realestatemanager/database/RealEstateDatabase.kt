package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [RealEstate::class], version = 1, exportSchema = false)
abstract class RealEstateDatabase : RoomDatabase(){

    abstract fun realEstateDao(): RealEstateDao

    private class RealEstateCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var realEstateDao = database.realEstateDao()

                    realEstateDao.deleteAll()

                    var realEstate1 = RealEstate(0, "maison", 250000, "rue du soleil levant")
                    realEstateDao.insert(realEstate1)
                    var realEstate2 = RealEstate(0, "chateau", 1550000, "rue du linceul vert")
                    realEstateDao.insert(realEstate2)
                    var realEstate3 = RealEstate(0, "appartement", 125000, "impasse du trou")
                    realEstateDao.insert(realEstate3)
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
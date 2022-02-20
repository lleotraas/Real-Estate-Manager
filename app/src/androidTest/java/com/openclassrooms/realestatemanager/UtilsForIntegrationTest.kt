package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.utils.Utils

class UtilsForIntegrationTest {
    companion object {
        val REAL_ESTATE_1 = RealEstate(1, "House", 123456, 90, 5, 1, 3, "this the description for test 1", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_084259257.png", 3, "9 Bd Chevalier de Clerville, Sète, France", "43.4065331", "3.684261699999999", "Hospital, School, Trades", "Hérault", Utils.getTodayDate(), Utils.getTodayDate(), "marcel")
        val REAL_ESTATE_2 = RealEstate(2, "Triplex", 14253678, 30, 10, 3, 6, "this the description for test 2", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_124522544.png", 6, "18 Rue Vendémiaire, Perpignan, France", "42.6944867", "2.8756294", "Sports hall, Pharmacy", "Pyrenées-Orientales", Utils.getTodayDate(), Utils.getTodayDate(), "marc")
        val REAL_ESTATE_3 = RealEstate(3, "Duplex", 6000000, 60, 15, 2, 12, "this the description for test 3", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_125020600.png", 9, "2 Rue Dupleix, Narbonne, France", "43.18245870000001", "3.0000799", "", "Aude", Utils.getTodayDate(), Utils.getTodayDate(), "michel")

        val REAL_ESTATE_PHOTO_1 =RealEstatePhoto(1, 1, "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_084259257.png", "Bathroom")
        val REAL_ESTATE_PHOTO_2 =RealEstatePhoto(2, 1, "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_084332989.png", "Bedroom")
        val REAL_ESTATE_PHOTO_3 =RealEstatePhoto(3, 1, "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_090642144.png", "Facade")
        val REAL_ESTATE_PHOTO_4 =RealEstatePhoto(4, 2, "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_124522544.png", "")
        val REAL_ESTATE_PHOTO_5 =RealEstatePhoto(5, 2, "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_124534786.png", "Lounge")
        val REAL_ESTATE_PHOTO_6 =RealEstatePhoto(6, 3, "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_124557723.png", "Kitchen")

        const val INPUT = "input"
        const val LANGUAGE = "language"
        const val TYPES = "types"
        const val KEY = "key"
        const val PLACE_ID = "place_id"

        const val PLACE_ADDRESS = "Rue du Parc Royal"

        fun getRealEstate(): ArrayList<RealEstate> {
            val listOfRealEstate = ArrayList<RealEstate>()
            listOfRealEstate.add(REAL_ESTATE_1)
            listOfRealEstate.add(REAL_ESTATE_2)
            listOfRealEstate.add(REAL_ESTATE_3)
            return listOfRealEstate
        }

        fun createDatabase(context: Context): RealEstateDatabase {
            return Room.inMemoryDatabaseBuilder(context, RealEstateDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }
    }
}
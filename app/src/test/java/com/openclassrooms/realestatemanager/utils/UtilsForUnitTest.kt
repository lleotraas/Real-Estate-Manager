package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate

class UtilsForUnitTest {
    companion object {
        const val NUMBER_OF_ROOMS = 10
        const val MIN_PRICE = 150000
        const val MAX_PRICE = 250000
        const val CREATION_DATE_IN_MILLIS = 1643532129546
        const val SELL_DATE_IN_MILLIS = 1644828129546
        const val MIN_SURFACE = 60
        const val MAX_SURFACE = 75
        const val NUMBER_OF_BATHROOMS = 1
        const val NUMBER_OF_BEDROOMS = 6
        const val NUMBER_OF_PHOTOS = 8
        const val PROPERTY = "House"
        const val CITY_NAME = "Sète"
        const val STATE_NAME = "Hérault"
        val LIST_OF_POI = ArrayList<String>()

        const val IMAGE_URI = "/storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_084259257.png"

        const val QUERY_STRING_FULL = " SELECT * FROM real_estate WHERE rooms >= ? AND price >= ? AND price <= ? AND creationDate >= ? AND sellDate >= ? AND surface >= ? AND surface <= ? AND bathrooms >= ? AND bedrooms >= ? AND pictureListSize >= ? AND property LIKE ? AND address LIKE ? AND pointOfInterest LIKE ? AND pointOfInterest LIKE ? AND state LIKE ?"
        const val QUERY_STRING_EMPTY = " SELECT * FROM real_estate"
        const val QUERY_STRING_MIN_PRICE = " SELECT * FROM real_estate WHERE price >= ?"
        const val QUERY_STRING_MAX_PRICE = " SELECT * FROM real_estate WHERE price <= ?"
        const val QUERY_STRING_CREATION_DATE_IN_MILLIS = " SELECT * FROM real_estate WHERE creationDate >= ?"
        const val QUERY_STRING_SELL_DATE_IN_MILLIS = " SELECT * FROM real_estate WHERE sellDate >= ?"
        const val QUERY_STRING_MIN_SURFACE = " SELECT * FROM real_estate WHERE surface >= ?"
        const val QUERY_STRING_MAX_SURFACE = " SELECT * FROM real_estate WHERE surface <= ?"
        const val QUERY_STRING_NUMBER_OF_BATHROOMS = " SELECT * FROM real_estate WHERE bathrooms >= ?"
        const val QUERY_STRING_NUMBER_OF_BEDROOMS = " SELECT * FROM real_estate WHERE bedrooms >= ?"
        const val QUERY_STRING_NUMBER_OF_PHOTOS = " SELECT * FROM real_estate WHERE pictureListSize >= ?"
        const val QUERY_STRING_PROPERTY_NAME = " SELECT * FROM real_estate WHERE property LIKE ?"
        const val QUERY_STRING_CITY_NAME = " SELECT * FROM real_estate WHERE address LIKE ?"
        const val QUERY_STRING_STATE_NAME = " SELECT * FROM real_estate WHERE pointOfInterest LIKE ? AND pointOfInterest LIKE ?"
        const val QUERY_STRING_LIST_OF_POI = " SELECT * FROM real_estate WHERE state LIKE ?"

        fun getListOfPoi(): ArrayList<String> {
            val listOfPoi = ArrayList<String>()
            listOfPoi.add("Hospital")
            listOfPoi.add("School")
            return listOfPoi
        }

        val REAL_ESTATE_1 = RealEstate(1, "House", 123456, 90, 5, 1, 3, "this the description for test 1", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_084259257.png", 3, "9 Bd Chevalier de Clerville, Sète, France", "43.4065331", "3.684261699999999", "Hospital, School, Trades", "Hérault", UtilsKt.parseDate(
            UtilsKt.getTodayDate()
        ), UtilsKt.parseDate(UtilsKt.getTodayDate()), "marcel")
        val REAL_ESTATE_2 = RealEstate(2, "Triplex", 14253678, 30, 10, 3, 6, "this the description for test 2", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_124522544.png", 6, "18 Rue Vendémiaire, Perpignan, France", "42.6944867", "2.8756294", "Sports hall, Pharmacy", "Pyrenées-Orientales", UtilsKt.parseDate(
            UtilsKt.getTodayDate()
        ), UtilsKt.parseDate(UtilsKt.getTodayDate()), "marc")
        val REAL_ESTATE_3 = RealEstate(3, "Duplex", 6000000, 60, 15, 2, 12, "this the description for test 3", "file:///storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_125020600.png", 9, "2 Rue Dupleix, Narbonne, France", "43.18245870000001", "3.0000799", "", "Aude", UtilsKt.parseDate(
            UtilsKt.getTodayDate()
        ), UtilsKt.parseDate(UtilsKt.getTodayDate()), "michel")
    }
}
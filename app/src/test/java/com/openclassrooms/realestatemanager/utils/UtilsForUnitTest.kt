package com.openclassrooms.realestatemanager.utils

class UtilsForUnitTest {
    companion object {
        val NUMBER_OF_ROOMS = 10
        val MIN_PRICE = 150000
        val MAX_PRICE = 250000
        val CREATION_DATE_IN_MILLIS = 1643532129546
        val SELL_DATE_IN_MILLIS = 1644828129546
        val MIN_SURFACE = 60
        val MAX_SURFACE = 75
        val NUMBER_OF_BATHROOMS = 1
        val NUMBER_OF_BEDROOMS = 6
        val NUMBER_OF_PHOTOS = 8
        val PROPERTY = "House"
        val CITY_NAME = "Sète"
        val STATE_NAME = "Hérault"
        val LIST_OF_POI = ArrayList<String>()

        val IMAGE_URI = "/storage/emulated/0/Android/data/com.openclassrooms.realestatemanager/files/DCIM/IMG_20220214_084259257.png"

        val QUERY_STRING_FULL = " SELECT * FROM real_estate WHERE rooms >= ? AND price >= ? AND price <= ? AND creationDate >= ? AND sellDate >= ? AND surface >= ? AND surface <= ? AND bathrooms >= ? AND bedrooms >= ? AND pictureListSize >= ? AND property LIKE ? AND address LIKE ? AND pointOfInterest LIKE ? AND pointOfInterest LIKE ? AND state LIKE ?"
        val QUERY_STRING_EMPTY = " SELECT * FROM real_estate"
        val QUERY_STRING_MIN_PRICE = " SELECT * FROM real_estate WHERE price >= ?"
        val QUERY_STRING_MAX_PRICE = " SELECT * FROM real_estate WHERE price <= ?"
        val QUERY_STRING_CREATION_DATE_IN_MILLIS = " SELECT * FROM real_estate WHERE creationDate >= ?"
        val QUERY_STRING_SELL_DATE_IN_MILLIS = " SELECT * FROM real_estate WHERE sellDate >= ?"
        val QUERY_STRING_MIN_SURFACE = " SELECT * FROM real_estate WHERE surface >= ?"
        val QUERY_STRING_MAX_SURFACE = " SELECT * FROM real_estate WHERE surface <= ?"
        val QUERY_STRING_NUMBER_OF_BATHROOMS = " SELECT * FROM real_estate WHERE bathrooms >= ?"
        val QUERY_STRING_NUMBER_OF_BEDROOMS = " SELECT * FROM real_estate WHERE bedrooms >= ?"
        val QUERY_STRING_NUMBER_OF_PHOTOS = " SELECT * FROM real_estate WHERE pictureListSize >= ?"
        val QUERY_STRING_PROPERTY_NAME = " SELECT * FROM real_estate WHERE property LIKE ?"
        val QUERY_STRING_CITY_NAME = " SELECT * FROM real_estate WHERE address LIKE ?"
        val QUERY_STRING_STATE_NAME = " SELECT * FROM real_estate WHERE pointOfInterest LIKE ? AND pointOfInterest LIKE ?"
        val QUERY_STRING_LIST_OF_POI = " SELECT * FROM real_estate WHERE state LIKE ?"

        fun getListOfPoi(): ArrayList<String> {
            val listOfPoi = ArrayList<String>()
            listOfPoi.add("Hospital")
            listOfPoi.add("School")
            return listOfPoi
        }
    }
}
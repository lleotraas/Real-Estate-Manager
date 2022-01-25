package com.openclassrooms.realestatemanager.utils

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromListOfImage(listOfUri: List<Uri>): String {
        val json = Gson().toJson(listOfUri)
        println(json)
        return json
    }

//    @TypeConverter
//    fun fromJson(json: String): ArrayList<Uri> {
//        val listOfUri: ArrayList<Uri> = Gson().fromJson(json, TypeToken<List<Uri>>() {}.type)
//        return listOfUri
//    }
}
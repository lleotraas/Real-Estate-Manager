package com.openclassrooms.realestatemanager.features_real_estate.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


open class Converters {

    // STRING LIST TO GSON
    @TypeConverter
    open fun fromString(value: String?): ArrayList<String?>? {
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    // GSON TO STRING LIST
    @TypeConverter
    open fun fromArrayList(list: ArrayList<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    // DATE TO LONG
    @TypeConverter
    open fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    // LONG TO DATE
    @TypeConverter
    open fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
}
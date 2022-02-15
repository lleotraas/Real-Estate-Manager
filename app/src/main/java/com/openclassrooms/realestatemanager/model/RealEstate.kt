package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "real_estate")
class RealEstate(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    var property: String,
    val price: Int,
    val surface: Int,
    val rooms: Int,
    val bathrooms: Int,
    val bedrooms: Int,
    val description: String,
    var picture: String,
    var pictureListSize: Int,
    val address: String,
    val latitude: String,
    val longitude: String,
    val pointOfInterest: ArrayList<String>,
    val state: String,
    val creationDate: Date,
    var sellDate: Date,
    var sellerName: String
    )

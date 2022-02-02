package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate")
class RealEstate(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    val property: String,
    val price: String,
//    val surface: Int,
//    val rooms: Int,
//    val description: String,
    val picture: String,
    val staticMap: ByteArray,
    val address: String,
//    val PointOfInterest: List<String>,
    val state: String,
//    val creationDate: String,
//    val sellDate: String,
//    val sellerName: String
    )
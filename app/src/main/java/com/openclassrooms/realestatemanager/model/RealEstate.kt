package com.openclassrooms.realestatemanager.model

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate")
class RealEstate(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    val property: String,
    val price: Int,
//    val surface: Int,
//    val rooms: Int,
//    val description: String,
//    val picture: List<Drawable>,
    val address: String
//    val PointOfInterest: List<String>,
//    val state: String,
//    val creationDate: String,
//    val sellDate: String,
//    val sellerName: String
    )
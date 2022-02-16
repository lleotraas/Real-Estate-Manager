package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate_image")
class RealEstatePhoto(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "real_estate_id")
    val realEstateId: Long,
    var photo: String,
    var category: String
)
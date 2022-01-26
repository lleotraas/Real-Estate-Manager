package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "real_estate_image")
class RealEstateImage(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "real_estate_id")
    val realEstateId: Long,
    val imageUri: String
) {
}
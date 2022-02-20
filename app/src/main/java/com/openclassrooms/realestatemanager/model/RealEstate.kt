package com.openclassrooms.realestatemanager.model

import android.content.ContentValues
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.util.Util
import com.openclassrooms.realestatemanager.utils.Utils
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
    val pointOfInterest: String,
    val state: String,
    val creationDate: Date,
    var sellDate: Date,
    var sellerName: String
    ) {

    companion object {
        fun fromContentValues(values: ContentValues): RealEstate {
            return RealEstate(
                id = if (values.containsKey("id")) values.getAsLong("id") else 0,
                property = if (values.containsKey("property")) values.getAsString("property") else "",
                price = if (values.containsKey("price")) values.getAsInteger("price") else 0,
                surface = if (values.containsKey("surface")) values.getAsInteger("surface") else 0,
                rooms = if (values.containsKey("rooms")) values.getAsInteger("rooms") else 0,
                bathrooms = if (values.containsKey("bathrooms")) values.getAsInteger("bathrooms") else 0,
                bedrooms = if (values.containsKey("bedrooms")) values.getAsInteger("bedrooms") else 0,
                description = if (values.containsKey("description")) values.getAsString("description") else "",
                picture = if (values.containsKey("picture")) values.getAsString("picture") else "",
                pictureListSize = if (values.containsKey("pictureListSize")) values.getAsInteger("pictureListSize") else 0,
                address = if (values.containsKey("address")) values.getAsString("address") else "",
                latitude = if (values.containsKey("latitude")) values.getAsString("latitude") else "",
                longitude = if (values.containsKey("longitude")) values.getAsString("longitude") else "",
                pointOfInterest = if (values.containsKey("pointOfInterest")) values.getAsString("pointOfInterest") else "",
                state = if (values.containsKey("state")) values.getAsString("state") else "",
//                creationDate = if (values.containsKey("creationDate")) values.getAsString("creationDate") else "",
//                sellDate = if (values.containsKey("sellDate")) values.getAsString("sellDate") else "",
                creationDate = Utils.getTodayDate(),
                sellDate = Utils.getTodayDate(),
                sellerName = if (values.containsKey("sellerName")) values.getAsString("sellerName") else "",
            )
        }
    }
}

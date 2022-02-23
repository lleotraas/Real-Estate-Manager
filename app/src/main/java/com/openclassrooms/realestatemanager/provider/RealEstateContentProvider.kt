package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.model.RealEstatePhoto

class RealEstateContentProvider : ContentProvider() {


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        if (context != null && selection == PROPERTY) {
            val realEstatePhotoId = ContentUris.parseId(uri)
            val cursor = RealEstateDatabase.getDatabase(context!!).realEstateDao().getRealEstateWithCursor(realEstatePhotoId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        if (context != null && selection == PHOTOS) {
            val realEstateId = ContentUris.parseId(uri)
            val cursorPhoto = RealEstateDatabase.getDatabase(context!!).realEstatePhotoDao()
                .getRealEstatePhotoWithCursor(realEstateId)
            cursorPhoto.setNotificationUri(context!!.contentResolver, uri)
            return cursorPhoto
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return ""
    }

    override fun insert(uri: Uri, contentValue: ContentValues?): Uri {
        return "".toUri()
    }

    override fun delete(uri: Uri, s: String?, strings: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, contentValue: ContentValues?, s: String?, strings: Array<out String>?): Int {
        return 0
    }

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager"
        val TABLE_NAME_1 = RealEstate::class.simpleName
        val TABLE_NAME_2 = RealEstatePhoto::class.simpleName
        val URI_REAL_ESTATE = Uri.parse("content://$AUTHORITY/$TABLE_NAME_1")
        val URI_REAL_ESTATE_PHOTO = Uri.parse("content://${AUTHORITY}/${TABLE_NAME_2}")
        const val PROPERTY = "property"
        const val PHOTOS = "photos"
    }
}
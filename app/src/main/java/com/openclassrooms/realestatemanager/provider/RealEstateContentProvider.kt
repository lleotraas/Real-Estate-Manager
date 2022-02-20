package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.model.RealEstate

class RealEstateContentProvider : ContentProvider() {


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        if (context != null) {
            val realEstateId = ContentUris.parseId(uri)
            val cursor = RealEstateDatabase.getDatabase(context!!).realEstateDao().getRealEstateWithCursor(realEstateId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.realEstate/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, contentValue: ContentValues?): Uri {

//        if (context != null && contentValue != null) {
//            val id = RealEstateDatabase.getDatabase(context!!).realEstateDao().insert(RealEstate.fromContentValues(contentValue))
//            if (id > 0) {
//                context!!.contentResolver.notifyChange(uri, null)
//                return ContentUris.withAppendedId(uri, id)
//            }
//        }
//        throw java.lang.IllegalArgumentException("Failed to insert row into $uri")
        return "".toUri()
    }

    override fun delete(uri: Uri, s: String?, strings: Array<out String>?): Int {
//        if (context != null) {
//            val count = RealEstateDatabase.getDatabase(context!!).realEstateDao().deleteRealEstateById(ContentUris.parseId(uri))
//            context!!.contentResolver.notifyChange(uri, null)
//            return count
//        }
//        throw java.lang.IllegalArgumentException("Failed to delete row into $uri")
        return 0
    }

    override fun update(uri: Uri, contentValue: ContentValues?, s: String?, strings: Array<out String>?): Int {
//        if (context != null && contentValue != null) {
//            val count = RealEstateDatabase.getDatabase(context!!).realEstateDao().updateRealEstate(RealEstate.fromContentValues(contentValue))
//            context!!.contentResolver.notifyChange(uri, null)
//            return count
//        }
//        throw java.lang.IllegalArgumentException("Failed to update row into $uri")
        return 0
    }

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        val TABLE_NAME = RealEstate::class.simpleName
//            "com.openclassrooms.realestatemanager.model.RealEstate"
        val URI_REAL_ESTATE = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

    }
}
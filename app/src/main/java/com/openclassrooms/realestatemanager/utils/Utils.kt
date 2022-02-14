package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.net.toUri
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.model.RealEstatePhoto
import java.io.File
import kotlin.math.abs

class UtilsKt {

    companion object {

        fun isConnectedToInternet(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false

                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION") val networkInfo =
                    connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }

        fun getDifference(periodicProgress: Int?): Int {
            return when (periodicProgress) {
                // DAYS
                in 1..6 -> periodicProgress!!
                //WEEKS
                in 7..9 -> (periodicProgress!!.minus(6)).times(7)
                //MONTHS
                in 10..20 -> (periodicProgress!!.minus(9)).times(30)
                //YEARS
                in 21..23 -> (periodicProgress!!.minus(20)).times(365)
                else -> 0
                //TODO need to search for today too
            }
        }

        fun convertDateInDays(date: Long, multiplier: Long): Long {
            val daysInMillis = (multiplier * 86400000)
            return abs(date - daysInMillis)
        }

        fun createCustomQuery(
            currentDay: Long,
            numberOfRooms: Int,
            minPrice: Int,
            maxPrice: Int,
            creationDateInMillis: Long,
            sellDateInMillis: Long,
            minSurface: Int,
            maxSurface: Int,
            numberOfBathrooms: Int,
            numberOfBedrooms: Int,
            numberOfPhotos: Int,
            cityName: String,
            poiList: ArrayList<String>,
            stateName: String
        ): SimpleSQLiteQuery {

            var queryString = ""
            val args = ArrayList<Any>()
            var containsCondition = false
            val table = "SELECT * FROM real_estate"



            queryString = "$queryString $table"

            if (numberOfRooms != 0) {
                queryString = "$queryString WHERE"
                queryString = "$queryString rooms >= ?"
                args.add(numberOfRooms)
                containsCondition = true
            }

            if (minPrice != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString price >= ?"
                args.add(minPrice)
            }

            if (maxPrice != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString price <= ?"
                args.add(maxPrice)
            }

            if (creationDateInMillis != currentDay) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString creationDate >= ?"
                args.add(creationDateInMillis)
            }

            if (sellDateInMillis != currentDay) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString sellDate >= ?"
                args.add(sellDateInMillis)
            }

            if (minSurface != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString surface >= ?"
                args.add(minSurface)
            }

            if (maxSurface != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString surface <= ?"
                args.add(maxSurface)
            }

            if (numberOfBathrooms != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString bathrooms >= ?"
                args.add(numberOfBathrooms)
            }

            if (numberOfBedrooms != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString bedrooms >= ?"
                args.add(numberOfBedrooms)
            }

            if (numberOfPhotos != 0) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString pictureListSize >= ?"
                args.add(numberOfPhotos)
            }

            if (cityName.isNotEmpty()) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString address LIKE ?"
                args.add("%$cityName%")
            }

            if (poiList.isNotEmpty()) {
                for (poi in poiList) {
                    if (containsCondition) {
                        queryString = "$queryString AND"
                    } else {
                        queryString = "$queryString WHERE"
                        containsCondition = true
                    }
                    queryString = "$queryString pointOfInterest LIKE (?)"
                    args.add("%$poi%")
                }
            }

            if (stateName.isNotEmpty()) {
                queryString = if (containsCondition) {
                    "$queryString AND"
                } else {
                    "$queryString WHERE"
                }
                queryString = "$queryString state LIKE ?"
                args.add(stateName)
            }

            return SimpleSQLiteQuery(queryString, args.toArray())
        }
        fun getPicture(context: Context, realEstatePhoto: RealEstatePhoto): String? {
            val uriPathHelper = UriPathHelper()
            return uriPathHelper.getPath(context, realEstatePhoto.photo.toUri())
        }

        fun loadPhotoFromAppDirectory(photo: String?): Bitmap {
            var bitmap: Bitmap? = null
            val imageFile = File(photo!!)
            if (imageFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            }

            return bitmap!!
        }
    }
}
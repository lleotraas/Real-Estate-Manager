@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.openclassrooms.realestatemanager.features_real_estate.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.sqlite.db.SimpleSQLiteQuery
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class UtilsKt {

    companion object {

        /**
         * @return Today date formatted as "dd/mm/yyy"
         */
        @SuppressLint("SimpleDateFormat")
        fun getTodayDate(): String = SimpleDateFormat("dd/MM/yyyy").format(Date())


        /**
         * Call to verify if there is internet or wifi connection active or not.
         * @param context Fragment context.
         * @return True if a connection is found.
         */
        @SuppressLint("MissingPermission")
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

        /**
         * Call to convert an amount (Dollars) to Euros.
         * @param dollars Amount in Dollars.
         * @return Amount in Euros.
         */
        fun convertDollarToEuro(dollars: Int): Int {
            return (dollars * 0.88).roundToInt()
        }

        /**
         * Call to convert an amount (Euros) to Dollars.
         * @param euros Amount in Euros.
         * @return Amount in Dollars.
         */
        fun convertEuroToDollar(euros: Int): Int {
                    return (euros / 0.88).roundToInt()
                }

        /**
         * Call to put separation "." on a number in order to have a better readability.
         * @param price Price amount.
         * @return Formatted price.
         */
        fun formatPrice(price: Int): String {
            val numberFormat = NumberFormat.getInstance(Locale.ITALIAN)
            return numberFormat.format(price)
        }

        /**
         * Call to get monthly payment for a loan.
         * @param contribution Amount of the contribution, can be equal to 0., in Dollars or Euros.
         * @param rate Amount of a loan rate in percent.
         * @param duration Loan duration in years.
         * @param price Property price in Dollars or Euros.
         * @return Monthly payment.
         */
        fun loanCalculator(
            contribution: Int,
            rate: Double,
            duration: Int,
            price: Int
        ): Double {
            val finalPrice = price - contribution
            val finalRate = rate / 100
            val pow: Double = 1.0 / 12
            val monthlyRate = (1 + finalRate).pow(pow) - 1
            val monthlyDuration = duration * 12
            val up = finalPrice * monthlyRate * (1 + monthlyRate).pow(monthlyDuration)
            val down = (1 + monthlyRate).pow(monthlyDuration) - 1
            return up / down

        }

        /**
         * Call to have the number of days that have been selected in Fragment_Filter.
         * @param periodicProgress Seekbar progress.
         * @return Equivalent of days in days, weeks, months or years.
         */
        fun getDifference(periodicProgress: Int?): Int {
            return when (periodicProgress) {
                // DAYS
                in 1..7 -> periodicProgress!!
                //WEEKS
                in 8..10 -> (periodicProgress!!.minus(7)).times(7)
                //MONTHS
                in 11..21 -> (periodicProgress!!.minus(10)).times(30)
                //YEARS
                in 22..24 -> (periodicProgress!!.minus(21)).times(365)
                else -> 0
            }
        }

        /**
         * Call to get the difference between today date in millis with the difference in millis.
         * @param currentDay Today date.
         * @param difference Target date obtained with getDifference().
         * @return Date in millis for search in database.
         */
        fun convertDateInDays(currentDay: Long, difference: Long): Long {
            val daysInMillis = (difference * 86400000)
            return abs(currentDay - daysInMillis)
        }

        /**
         * Call to do a custom research with none, one or multiple parameters.
         * @param currentDay Today date.
         * @param numberOfRooms Property number of rooms.
         * @param minPrice Property minimum price.
         * @param maxPrice Property maximum price.
         * @param creationDateInMillis Property creation date in millis.
         * @param sellDateInMillis Property sell date in millis.
         * @param minSurface Property minimum surface.
         * @param maxSurface Property maximum surface.
         * @param numberOfBathrooms Property number of bathrooms.
         * @param numberOfBedrooms Property number of bedrooms.
         * @param numberOfPhotos Property number of photos.
         * @param cityName Property city name location.
         * @param poiList Property point of interest.
         * @param stateName Property state location.
         * @return Query with parameters informed.
         */
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
            property: String,
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

            if (property.isNotEmpty()) {
                if (containsCondition) {
                    queryString = "$queryString AND"
                } else {
                    queryString = "$queryString WHERE"
                    containsCondition = true
                }
                queryString = "$queryString property LIKE ?"
                args.add("%$property%")
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
                    queryString = "$queryString pointOfInterest LIKE ?"
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

        /**
         * Call to parse a date.
         * @param date Date to parse.
         * @return Parsed date.
         */
        @SuppressLint("SimpleDateFormat")
        fun parseDate(date: String): Date {
            val format = SimpleDateFormat("dd/MM/yyy")
            return format.parse(date)
        }

        fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    afterTextChanged.invoke(editableText.toString())
                }
            })
        }

        fun createCustomBundle(key: String, value: Any, bundle: Bundle): Bundle {
            if (key == ID) {
                bundle.putLong(key, value as Long)
            } else {
                bundle.putString(key, value as String)
            }
            return bundle
        }

        fun addDataToPlaceHolder(name: String, data: String) {
            PlaceholderContent.ITEM_MAP[name] = PlaceholderContent.PlaceholderItem(name, data,"")
        }

        fun getPlaceHolderContent(placeholderItem: MutableMap<String, PlaceholderContent.PlaceholderItem>, key: String): String? {
            val args: MutableMap<String, PlaceholderContent.PlaceholderItem> = PlaceholderContent.ITEM_MAP
            if (args.containsKey(key)) {
                return args[key].toString()
            }
            return null
        }

        const val ID = "id"
        const val ADDRESS = "address"
        const val RESULT_CODE = "RESULT_CODE"
    }
}
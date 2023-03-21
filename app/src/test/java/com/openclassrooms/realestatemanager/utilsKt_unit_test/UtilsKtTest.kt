package com.openclassrooms.realestatemanager.utilsKt_unit_test

import android.os.Bundle
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.CITY_NAME
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.CREATION_DATE_IN_MILLIS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.LIST_OF_POI
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.getListOfPoi
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.MAX_PRICE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.MAX_SURFACE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.MIN_PRICE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.MIN_SURFACE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.NUMBER_OF_BATHROOMS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.NUMBER_OF_BEDROOMS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.NUMBER_OF_PHOTOS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.NUMBER_OF_ROOMS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.PROPERTY
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_CITY_NAME
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_CREATION_DATE_IN_MILLIS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_EMPTY
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_FULL
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_LIST_OF_POI
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_MAX_PRICE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_MAX_SURFACE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_MIN_PRICE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_MIN_SURFACE
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_NUMBER_OF_BATHROOMS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_NUMBER_OF_BEDROOMS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_NUMBER_OF_PHOTOS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_PROPERTY_NAME
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_SELL_DATE_IN_MILLIS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.QUERY_STRING_STATE_NAME
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.SELL_DATE_IN_MILLIS
import com.openclassrooms.realestatemanager.utils.UtilsForUnitTest.Companion.STATE_NAME
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.ID
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.createCustomBundle
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.getTodayDate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@RunWith(JUnit4::class)
class UtilsKtTest {

    @Test
    fun getCurrentDay_withSuccess() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentDayExpected = dateFormat.format(Date())
        val currentDayToTest = getTodayDate()
        assertEquals(currentDayExpected, currentDayToTest)
    }

    @Test
    fun convertDollarToEuro() {
        val dollar = 250000
        val resultExpected = 220000
        val resultActual = UtilsKt.convertDollarToEuro(dollar)
        assertEquals(resultExpected, resultActual)
    }

    @Test
    fun convertEuroToDollar() {
        val euro = 250000
        val resultExpected = 284091
        val resultActual = UtilsKt.convertEuroToDollar(euro)
        assertEquals(resultExpected, resultActual)
    }

    @Test
    fun formatPrice() {
        val priceActual = UtilsKt.formatPrice(250000)
        val priceExpected = "250.000"
        assertEquals(priceExpected, priceActual)
    }

    @Test
    fun loanCalculator() {
        val contribution = 15000
        val rate = 2.34
        val duration = 20
        val price = 165000
        val monthlyPaymentExpected = 781.48
        val monthlyPaymentActual = UtilsKt.loanCalculator(contribution, rate, duration, price)
        assertEquals(monthlyPaymentExpected, monthlyPaymentActual, 0.10)
    }

    @Test
    fun getDifferenceShouldReturnDaysPeriod() {
        // 5 days
        var numberOfDaysExpected = 5
        var numberOfDaysActual = UtilsKt.getDifference(5)
        assertEquals(numberOfDaysExpected, numberOfDaysActual)

        // 2 weeks in days
        numberOfDaysExpected = 14
        numberOfDaysActual = UtilsKt.getDifference(9)
        assertEquals(numberOfDaysExpected, numberOfDaysActual)

        // 3 months in days
        numberOfDaysExpected = 180
        numberOfDaysActual = UtilsKt.getDifference(16)
        assertEquals(numberOfDaysExpected, numberOfDaysActual)

        // 2 years in days
        numberOfDaysExpected = 730
        numberOfDaysActual = UtilsKt.getDifference(23)
        assertEquals(numberOfDaysExpected, numberOfDaysActual)
    }

    @Test
    fun convertDateInDays() {
        // 30/01/2022
        val dayInMillisExpected = 1643532129546
        // 14/02/2022
        val dayInMillisToTest = 1644828129546
        val numberOfDayToSubtract = 15
        val dayInMillisActual =
            UtilsKt.convertDateInDays(dayInMillisToTest, numberOfDayToSubtract.toLong())
        assertEquals(dayInMillisExpected, dayInMillisActual)
    }

    @Test
    fun createCustomQuery() {
        val currentDay = UtilsKt.parseDate(getTodayDate()).time
        var query = UtilsKt.createCustomQuery(
            UtilsKt.parseDate(getTodayDate()).time,
            NUMBER_OF_ROOMS,
            MIN_PRICE,
            MAX_PRICE,
            CREATION_DATE_IN_MILLIS,
            SELL_DATE_IN_MILLIS,
            MIN_SURFACE,
            MAX_SURFACE,
            NUMBER_OF_BATHROOMS,
            NUMBER_OF_BEDROOMS,
            NUMBER_OF_PHOTOS,
            PROPERTY,
            CITY_NAME,
            getListOfPoi(),
            STATE_NAME
        )
        var queryString = query.sql
        assertEquals(QUERY_STRING_FULL, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_EMPTY, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            MIN_PRICE,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_MIN_PRICE, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            MAX_PRICE,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_MAX_PRICE, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            CREATION_DATE_IN_MILLIS,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_CREATION_DATE_IN_MILLIS, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            SELL_DATE_IN_MILLIS,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_SELL_DATE_IN_MILLIS, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            MIN_SURFACE,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_MIN_SURFACE, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            MAX_SURFACE,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_MAX_SURFACE, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            NUMBER_OF_BATHROOMS,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_NUMBER_OF_BATHROOMS, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            NUMBER_OF_BEDROOMS,
            0,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_NUMBER_OF_BEDROOMS, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            NUMBER_OF_PHOTOS,
            "",
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_NUMBER_OF_PHOTOS, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            PROPERTY,
            "",
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_PROPERTY_NAME, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            CITY_NAME,
            LIST_OF_POI,
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_CITY_NAME, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            getListOfPoi(),
            ""
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_STATE_NAME, queryString)

        query = UtilsKt.createCustomQuery(
            currentDay,
            0,
            0,
            0,
            currentDay,
            currentDay,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            LIST_OF_POI,
            STATE_NAME
        )
        queryString = query.sql
        assertEquals(QUERY_STRING_LIST_OF_POI, queryString)
    }

    @Test
    fun parseDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today = getTodayDate()
        val dateExpected = dateFormat.parse(today)
        val dateActual = UtilsKt.parseDate(today)
        assertEquals(dateExpected, dateActual)
    }

    @Test
    fun `create a custom bundle with Long value`() {
        val bundleExpected = createMockedBundle()
        val bundleToTest = createMockedBundle()
        bundleExpected.putLong(ID, 12L)
        createCustomBundle(ID, 12L, bundleToTest)
        assert(bundleExpected.getLong(ID) == bundleToTest.getLong(ID))
    }

    private fun createMockedBundle(): Bundle {
        val fakeBundle = HashMap<String, Long>()
        val bundle = mock(Bundle::class.java)
        doAnswer { invocation ->
            val arguments = invocation.arguments
            val key: String = arguments[0] as String
            val value: Long = arguments[1] as Long
            fakeBundle.put(key, value)
        }.`when`(bundle).putLong(anyString(), anyLong())
        `when`(bundle.get(anyString())).thenAnswer { invocation ->
            val arguments = invocation.arguments
            val key = arguments[0]
            fakeBundle[key]
        }
        return bundle
    }
}
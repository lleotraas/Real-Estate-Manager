package com.openclassrooms.realestatemanager.retrofit_test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.INPUT
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.KEY
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.LANGUAGE
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.PLACE_ADDRESS
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.PLACE_ID
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.TYPES
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class RetrofitTest {

    @Test
     fun getAutocompleteResponse() {
        runBlocking {
            val api = RetrofitInstance.autocompleteApi
            val response: Response<AutocompletePredictions> = api.getPlacesAutocomplete(INPUT, LANGUAGE, TYPES, KEY)
            assertTrue(response.isSuccessful)
            assertEquals(5, response.body()!!.predictions.size)
        }
    }

    @Test
    fun getPlaceDetailsResponse() {
        runBlocking {
            val api = RetrofitInstance.placeDetailsApi
            val response: Response<PlaceDetails> = api.getPlaceDetails(PLACE_ID, KEY)
            assertTrue(response.isSuccessful)
            assertEquals(PLACE_ADDRESS, response.body()!!.result.name)
        }
    }

}
package com.openclassrooms.realestatemanager.retrofit

import com.openclassrooms.realestatemanager.model.details.PlaceDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceDetailsApi {

    @GET("details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): Response<PlaceDetails>
}
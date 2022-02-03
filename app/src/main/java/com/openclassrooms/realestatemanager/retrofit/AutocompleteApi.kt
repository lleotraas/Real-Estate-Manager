package com.openclassrooms.realestatemanager.retrofit

import com.openclassrooms.realestatemanager.model.autocomplete.AutocompletePredictions
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface AutocompleteApi {

    @GET("autocomplete/json")
    suspend fun getPlacesAutocomplete(
        @Query("input") input: String,
        @Query("language") language: String,
        @Query("types") types: String,
        @Query("key") apiKey: String,
    ): Response<AutocompletePredictions>
}
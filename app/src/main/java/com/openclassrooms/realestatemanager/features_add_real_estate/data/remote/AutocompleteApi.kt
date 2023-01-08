package com.openclassrooms.realestatemanager.features_add_real_estate.data.remote

import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Adresse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AutocompleteApi {

    @GET("search")
    suspend fun getPlacesAutocomplete(
        @Query("q") input: String
    ): Response<Adresse>
}
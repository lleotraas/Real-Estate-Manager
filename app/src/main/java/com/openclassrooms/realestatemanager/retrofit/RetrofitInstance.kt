package com.openclassrooms.realestatemanager.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val autocompleteApi: AutocompleteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AutocompleteApi:: class.java)
    }

    val placeDetailsApi: PlaceDetailsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaceDetailsApi:: class.java)
    }
}
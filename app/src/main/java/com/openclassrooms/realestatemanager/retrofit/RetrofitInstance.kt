package com.openclassrooms.realestatemanager.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: MarkerApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/staticmap")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MarkerApi::class.java)
    }
}
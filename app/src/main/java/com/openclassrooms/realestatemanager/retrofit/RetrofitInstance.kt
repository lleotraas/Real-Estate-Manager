package com.openclassrooms.realestatemanager.retrofit

import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val isRunningTest = RealEstateApplication.isRunningTest
    private val placeBaseUrl = if (isRunningTest) "https://3a103716-bd5c-477a-90f6-fc16bd4e1efd.mock.pstmn.io\n" else "https://maps.googleapis.com/maps/api/place/"
    private val autocompleteBaseUrl = if (isRunningTest) "https://3a103716-bd5c-477a-90f6-fc16bd4e1efd.mock.pstmn.io/" else "https://maps.googleapis.com/maps/api/place/"

    val autocompleteApi: AutocompleteApi by lazy {
        Retrofit.Builder()
            .baseUrl(autocompleteBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AutocompleteApi:: class.java)
    }

    val placeDetailsApi: PlaceDetailsApi by lazy {
        Retrofit.Builder()
            .baseUrl(placeBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlaceDetailsApi:: class.java)
    }
}
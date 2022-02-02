package com.openclassrooms.realestatemanager.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface MarkerApi {

    @GET
    fun getImageData(
        @Url
        url: String,
        @Query("center") center: String,
        @Query("zoom") zoom: String,
        @Query("size") size: String,
        @Query("format") format: String,
        @Query("scale") scale: String,
        @Query("markers") marker: String,
        @Query("key") apiKey: String
    ): Call<ResponseBody>
}
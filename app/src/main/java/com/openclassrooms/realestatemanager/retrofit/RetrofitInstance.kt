package com.openclassrooms.realestatemanager.retrofit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

object RetrofitInstance {

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/staticmap/").build()
    }
        private val api: MarkerApi by lazy { provideRetrofit().create(MarkerApi::class.java) }

        fun getBitmapFrom(url: String, center: String, zoom: String, size: String, format: String, scale: String, markers: String, apiKey: String, onComplete: (Bitmap?) -> Unit) {
            api.getImageData(url, center, zoom, size, format, scale, markers, apiKey).enqueue(object : retrofit2.Callback<ResponseBody> {

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onComplete(null)
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (!response.isSuccessful || response.body() == null || response.errorBody() != null) {
                        onComplete(null)
                        return
                    }
                    val bytes = response.body()!!.bytes()
                    onComplete(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                }
            })
        }

}
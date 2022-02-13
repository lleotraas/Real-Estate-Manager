package com.openclassrooms.realestatemanager

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.retrofit.RetrofitInstance
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RetrofitTest {

    private val api = RetrofitInstance.placeDetailsApi

    @Test
     fun getQuelqueChose() {
        runBlocking {
            val actual = api.getPlaceDetails("id", BuildConfig.GMP_KEY)
        }
    }

}
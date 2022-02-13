package com.openclassrooms.realestatemanager

import android.app.Application
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.coroutineContext

@RunWith(AndroidJUnit4::class)
class UtilsWifiTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun isInternetAvailableShouldReturnTrue() {
        assertTrue(Utils.isInternetAvailable(context))
    }
}
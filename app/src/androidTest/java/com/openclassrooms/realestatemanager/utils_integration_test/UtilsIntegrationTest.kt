package com.openclassrooms.realestatemanager.utils_integration_test

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.utils.Utils.isInternetAvailable
import com.openclassrooms.realestatemanager.utils.UtilsKt.Companion.isConnectedToInternet
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsIntegrationTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun isInternetAvailableShouldReturnTrue() {
        assertTrue(isInternetAvailable(context))
    }

    @Test
    fun isConnectedToInternetShouldReturnTrue() {
        assertTrue(isConnectedToInternet(context))
    }
}
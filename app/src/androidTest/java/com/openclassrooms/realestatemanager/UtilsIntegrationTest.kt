package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.Utils.Companion.REAL_ESTATE_PHOTO_1
import com.openclassrooms.realestatemanager.utils.Utils.isInternetAvailable
import com.openclassrooms.realestatemanager.utils.UtilsKt
import com.openclassrooms.realestatemanager.utils.UtilsKt.Companion.getPictureFromRealEstatePhoto
import com.openclassrooms.realestatemanager.utils.UtilsKt.Companion.isConnectedToInternet
import org.junit.Assert.assertNotEquals
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
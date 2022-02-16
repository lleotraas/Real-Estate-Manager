package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.Utils.Companion.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.ui.activity.RealEstateViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RealEstateViewModelTest {

    private lateinit var viewModel: RealEstateViewModel
    private lateinit var realEstateDao: RealEstateDao
    private lateinit var database: RealEstateDatabase
    private val app = RealEstateApplication()


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = RealEstateDatabase.createDatabaseForTest(context)
        realEstateDao = database.realEstateDao()
//       viewModel: RealEstateViewModel by viewModels {
//            RealEstateViewModelFactory(
//                (requireActivity().application as RealEstateApplication).realEstateRepository,
//                (requireActivity().application as RealEstateApplication).realEstateImageRepository,
//                (requireActivity().application as RealEstateApplication).filterRepository)
//        }
        viewModel = RealEstateViewModel(app.realEstateRepository, app.realEstateImageRepository, app.filterRepository)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealEstate() = runBlocking {
        viewModel!!.insert(REAL_ESTATE_1)
        val realEstates = viewModel!!.getAllRealEstate
        assertTrue(realEstates.value!!.size == 1)
    }
}
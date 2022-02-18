package com.openclassrooms.realestatemanager.viewmodel_test

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.database.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.ui.activity.RealEstateViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RealEstateViewModelTest {

    private lateinit var realEstateDao: RealEstateDao
    private lateinit var realEstatePhotoDao: RealEstatePhotoDao
    private lateinit var database: RealEstateDatabase
    private lateinit var realEstateRepository: RealEstateRepository
    private lateinit var realEstatePhotoRepository: RealEstatePhotoRepository
    private lateinit var filterRepository: FilterRepository
    private lateinit var viewModel: RealEstateViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RealEstateDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        realEstateDao = database.realEstateDao()
        realEstatePhotoDao = database.realEstatePhotoDao()
        realEstateRepository = RealEstateRepository(realEstateDao)
        realEstatePhotoRepository = RealEstatePhotoRepository(realEstatePhotoDao)
        filterRepository = FilterRepository()
        viewModel = RealEstateViewModel(realEstateRepository, realEstatePhotoRepository, filterRepository)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealEstate() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)
        val allRealEstate = viewModel.getAllRealEstate
        Truth.assertThat(allRealEstate.value!![0].address).contains(REAL_ESTATE_1.address)
    }

}
package com.openclassrooms.realestatemanager.viewmodel_test

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_2
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_1
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_2
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_3
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_4
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_5
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_6
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.RealEstateDatabase
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.getOrAwaitValue
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstatePhotoRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstateRepositoryImpl
import com.openclassrooms.realestatemanager.features_add_real_estate.presentation.AddViewModel
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AddViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var realEstateDao: RealEstateDao
    private lateinit var realEstatePhotoDao: RealEstatePhotoDao
    private lateinit var database: RealEstateDatabase
    private lateinit var realEstateRepository: RealEstateRepositoryImpl
    private lateinit var realEstatePhotoRepository: RealEstatePhotoRepositoryImpl
    private lateinit var viewModel: AddViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RealEstateDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        realEstateDao = database.realEstateDao()
        realEstatePhotoDao = database.realEstatePhotoDao()
        realEstateRepository = RealEstateRepositoryImpl(realEstateDao)
        realEstatePhotoRepository = RealEstatePhotoRepositoryImpl(realEstatePhotoDao)
        viewModel = AddViewModel(realEstateRepository, realEstatePhotoRepository)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealEstateById() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)
        val realEstate = viewModel.getRealEstateById(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(realEstate.id , REAL_ESTATE_1.id)
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateByAddress() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)
        viewModel.insert(REAL_ESTATE_2)
        val realEstate1 = viewModel.getRealEstateByAddress(REAL_ESTATE_1.address).getOrAwaitValue()
        val realEstate2 = viewModel.getRealEstateByAddress(REAL_ESTATE_2.address).getOrAwaitValue()
        assertEquals(realEstate1.address, REAL_ESTATE_1.address)
        assertEquals(realEstate2.address, REAL_ESTATE_2.address)
    }

    @Test
    @Throws(Exception::class)
    fun updateRealEstate() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)

        var realEstate = viewModel.getRealEstateById(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(realEstate.property, REAL_ESTATE_1.property)

        val property = "Studio"
        realEstate.property = property
        viewModel.update(realEstate)

        realEstate = viewModel.getRealEstateById(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(realEstate.property, property)
    }

    @Test
    @Throws(Exception::class)
    fun insertPhotoAndGetPhoto() = runBlocking {
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_1)
        val allRealEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(REAL_ESTATE_PHOTO_1.photo, allRealEstatePhoto[0].photo)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRealEstatePhoto() = runBlocking {
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_1)
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_2)
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_3)
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_4)
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_5)
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_6)
        val realEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_PHOTO_1.realEstateId).getOrAwaitValue()
        assertTrue(realEstatePhoto.size == 3)
        assertEquals(realEstatePhoto[0].photo, REAL_ESTATE_PHOTO_1.photo)
        assertEquals(realEstatePhoto[1].photo, REAL_ESTATE_PHOTO_2.photo)
        assertEquals(realEstatePhoto[2].photo, REAL_ESTATE_PHOTO_3.photo)
    }

    @Test
    @Throws(Exception::class)
    fun updateRealEstatePhoto() = runBlocking {
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_1)
        var allRealEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(REAL_ESTATE_PHOTO_1.photo, allRealEstatePhoto[0].photo)

        allRealEstatePhoto[0].photo = REAL_ESTATE_PHOTO_2.photo
        viewModel.updateRealEstatePhoto(allRealEstatePhoto[0])
        allRealEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(REAL_ESTATE_PHOTO_2.photo, allRealEstatePhoto[0].photo)
    }

    @Test
    @Throws(Exception::class)
    fun deleteRealEstatePhoto() = runBlocking {
        viewModel.insertPhoto(REAL_ESTATE_PHOTO_1)
        var allRealEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_1.id).getOrAwaitValue()
        assertEquals(REAL_ESTATE_PHOTO_1.photo, allRealEstatePhoto[0].photo)

        viewModel.deleteRealEstatePhoto(allRealEstatePhoto[0].id)
        allRealEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_1.id).getOrAwaitValue()
        assertTrue(allRealEstatePhoto.isEmpty())
    }
}
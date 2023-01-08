package com.openclassrooms.realestatemanager.viewmodel_test

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_2
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_3
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_1
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_2
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_3
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_4
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_5
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_6
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.getRealEstate
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.RealEstateDatabase
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstatePhotoDao
import com.openclassrooms.realestatemanager.getOrAwaitValue
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.FilterRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstatePhotoRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.data.repository.RealEstateRepositoryImpl
import com.openclassrooms.realestatemanager.features_real_estate.presentation.RealEstateViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RealEstateViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var realEstateDao: RealEstateDao
    private lateinit var realEstatePhotoDao: RealEstatePhotoDao
    private lateinit var database: RealEstateDatabase
    private lateinit var realEstateRepository: RealEstateRepositoryImpl
    private lateinit var realEstatePhotoRepository: RealEstatePhotoRepositoryImpl
    private lateinit var filterRepository: FilterRepositoryImpl
    private lateinit var viewModel: RealEstateViewModel

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
        filterRepository = FilterRepositoryImpl()
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
        val allRealEstate = viewModel.getAllRealEstate.getOrAwaitValue()
        assertEquals(allRealEstate[0].address ,REAL_ESTATE_1.address)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRealEstate() = runBlocking {
        viewModel.insert(REAL_ESTATE_2)
        viewModel.insert(REAL_ESTATE_3)
        val allRealEstate = viewModel.getAllRealEstate.getOrAwaitValue()
        assertEquals(allRealEstate[0].address, REAL_ESTATE_2.address)
        assertEquals(allRealEstate[1].address, REAL_ESTATE_3.address)
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateById() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)
        viewModel.insert(REAL_ESTATE_2)
        val realEstate1 = viewModel.getRealEstateById(REAL_ESTATE_1.id).getOrAwaitValue()
        val realEstate2 = viewModel.getRealEstateById(REAL_ESTATE_2.id).getOrAwaitValue()
        assertEquals(realEstate1.id, REAL_ESTATE_1.id)
        assertEquals(realEstate2.id, REAL_ESTATE_2.id)
    }

    @Test
    @Throws(Exception::class)
    fun searchRealEstateWithParameters() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)
        viewModel.insert(REAL_ESTATE_2)
        val queryString =  "SELECT * FROM real_estate WHERE property LIKE (?) AND price >= ? AND surface <= ?"
        val args = ArrayList<Any>()
        args.add("Triplex")
        args.add(124000)
        args.add(30)
        val simpleQuery = SimpleSQLiteQuery(queryString, args.toArray())
        val result = viewModel.searchRealEstateWithParameters(simpleQuery)
        assertEquals(1, result.size)
        assertEquals(REAL_ESTATE_2.id, result[0].id)
    }

    @Test
    @Throws(Exception::class)
    fun updateRealEstate() = runBlocking {
        viewModel.insert(REAL_ESTATE_1)

        var allRealEstate = viewModel.getAllRealEstate.getOrAwaitValue()
        assertEquals(allRealEstate[0].property, REAL_ESTATE_1.property)

        val property = "Studio"
        allRealEstate[0].property = property
        viewModel.updateRealEstate(allRealEstate[0])

        allRealEstate = viewModel.getAllRealEstate.getOrAwaitValue()
        assertEquals(allRealEstate[0].property, property)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRealEstatePhoto() = runBlocking {
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_1)
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_2)
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_3)
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_4)
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_5)
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_6)
        val realEstatePhoto = viewModel.getAllRealEstatePhoto(REAL_ESTATE_PHOTO_1.realEstateId).getOrAwaitValue()
        assertTrue(realEstatePhoto.size == 3)
        assertEquals(realEstatePhoto[0].photo, REAL_ESTATE_PHOTO_1.photo)
        assertEquals(realEstatePhoto[1].photo, REAL_ESTATE_PHOTO_2.photo)
        assertEquals(realEstatePhoto[2].photo, REAL_ESTATE_PHOTO_3.photo)
    }

    @Test
    @Throws(Exception::class)
    fun setFilteredListAndGetAllFilteredRealEstate() = runBlocking {
        val listOfFilteredRealEstateExpected = getRealEstate()
        viewModel.setFilteredList(listOfFilteredRealEstateExpected)
        val listOfFilteredRealEstateActual = viewModel.getFilteredRealEstate().getOrAwaitValue()
        assertEquals(listOfFilteredRealEstateExpected, listOfFilteredRealEstateActual)
    }

    @Test
    @Throws(Exception::class)
    fun isFilteredListIsEmptyShouldReturnTrueOrFalse() = runBlocking {
        viewModel.setFilteredListEmpty()
        var isFilteredListIsEmpty = viewModel.isFilteredListIsEmpty().getOrAwaitValue()
        assertFalse(isFilteredListIsEmpty)

        viewModel.setFilteredListNotEmpty()
        isFilteredListIsEmpty = viewModel.isFilteredListIsEmpty().getOrAwaitValue()
        assertTrue(isFilteredListIsEmpty)
    }
}
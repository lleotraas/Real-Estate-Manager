package com.openclassrooms.realestatemanager.dao_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.REAL_ESTATE_2
import com.openclassrooms.realestatemanager.REAL_ESTATE_3
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.RealEstateDatabase
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named


@HiltAndroidTest
class RealEstateDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: RealEstateDatabase
    private lateinit var realEstateDao: RealEstateDao

    @Before
    fun setUp() {
        hiltRule.inject()
        realEstateDao = database.realEstateDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealEstate() = runBlocking {
        realEstateDao.insert(REAL_ESTATE_1)
        val allRealEstate = realEstateDao.searchRealEstateWithParameters(SimpleSQLiteQuery("SELECT * FROM real_estate")).first()
        assertThat(allRealEstate.address).contains(REAL_ESTATE_1.address)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRealEstate() = runBlocking {
        realEstateDao.insert(REAL_ESTATE_2)
        realEstateDao.insert(REAL_ESTATE_3)
        val allRealEstate = realEstateDao.searchRealEstateWithParameters(SimpleSQLiteQuery("SELECT * FROM real_estate"))
        assertEquals(allRealEstate[0].address, REAL_ESTATE_2.address)
        assertEquals(allRealEstate[1].address, REAL_ESTATE_3.address)
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateByAddress() = runBlocking {
        realEstateDao.insert(REAL_ESTATE_1)
        realEstateDao.insert(REAL_ESTATE_2)
        val realEstate1 = realEstateDao.getRealEstateByAddress(REAL_ESTATE_1.address).first()
        val realEstate2 = realEstateDao.getRealEstateByAddress(REAL_ESTATE_2.address).first()
        assertEquals(realEstate1.address, REAL_ESTATE_1.address)
        assertEquals(realEstate2.address, REAL_ESTATE_2.address)
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateById() = runBlocking {
        realEstateDao.insert(REAL_ESTATE_1)
        realEstateDao.insert(REAL_ESTATE_2)
        val realEstate1 = realEstateDao.getRealEstateById(REAL_ESTATE_1.id).first()
        val realEstate2 = realEstateDao.getRealEstateById(REAL_ESTATE_2.id).first()
        assertEquals(realEstate1.id, REAL_ESTATE_1.id)
        assertEquals(realEstate2.id, REAL_ESTATE_2.id)
    }

    @Test
    @Throws(Exception::class)
    fun searchRealEstateWithParameters() = runBlocking {
        realEstateDao.insert(REAL_ESTATE_1)
        realEstateDao.insert(REAL_ESTATE_2)
        val queryString =  "SELECT * FROM real_estate WHERE property LIKE (?) AND price >= ? AND surface <= ?"
        val args = ArrayList<Any>()
        args.add("Triplex")
        args.add(124000)
        args.add(30)
        val simpleQuery = SimpleSQLiteQuery(queryString, args.toArray())
        val result = realEstateDao.searchRealEstateWithParameters(simpleQuery)
        assertEquals(1, result.size)
        assertEquals(REAL_ESTATE_2.id, result[0].id)
    }

    @Test
    @Throws(Exception::class)
    fun updateRealEstate() = runBlocking {
        realEstateDao.insert(REAL_ESTATE_1)

        var allRealEstate = realEstateDao.searchRealEstateWithParameters(SimpleSQLiteQuery("SELECT * FROM real_estate"))
        assertEquals(allRealEstate[0].property, REAL_ESTATE_1.property)

        val property = "Studio"
        allRealEstate[0].property = property
        realEstateDao.updateRealEstate(allRealEstate[0])

        allRealEstate = realEstateDao.searchRealEstateWithParameters(SimpleSQLiteQuery("SELECT * FROM real_estate"))
        assertEquals(allRealEstate[0].property, property)
    }
}
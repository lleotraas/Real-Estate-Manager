package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.model.RealEstate
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class RealEstateDaoTest {

    private lateinit var realEstateDao: RealEstateDao
    private lateinit var database: RealEstateDatabase


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RealEstateDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        realEstateDao = database.realEstateDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetWord() = runBlocking {
        val pictureArray = ArrayList<String>()
        val realEstate = RealEstate(
            0,
            "House",
            123456,
            45,
            5,
            1,
            3,
            "this the description test",
            pictureArray,
            pictureArray.size,
            "address",
            "43.758614",
            "3.458762",
            pictureArray,
            "state",
            Utils.getTodayDate(),
            Utils.getTodayDate(),
            "marcel"
        )
        realEstateDao.insert(realEstate)
        val allRealEstate = realEstateDao.getAllRealEstate().first()
        assertThat(allRealEstate[0].address).contains(realEstate.address)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRealEstate() = runBlocking {
        val pictureArray = ArrayList<String>()
        val realEstate1 = RealEstate(
            0,
            "Studio",
            123456,
            45,
            5,
            1,
            3,
            "this the description test",
            pictureArray,
            pictureArray.size,
            "address to test 1",
            "43.758614",
            "3.458762",
            pictureArray,
            "state",
            Utils.getTodayDate(),
            Utils.getTodayDate(),
            "marc")
        realEstateDao.insert(realEstate1)
        val realEstate2 = RealEstate(
            0,
            "Duplex",
            123456,
            45,
            5,
            1,
            3,
            "this the description test",
            pictureArray,
            pictureArray.size,
            "address to test 2",
            "43.758614",
            "3.458762",
            pictureArray,
            "state",
            Utils.getTodayDate(),
            Utils.getTodayDate(),
            "michel"
        )
        realEstateDao.insert(realEstate2)
        val allRealEstate = realEstateDao.getAllRealEstate().first()
        assertEquals(allRealEstate[0].address, realEstate1.address)
        assertEquals(allRealEstate[1].address, realEstate2.address)
    }

    @Test
    @Throws(Exception::class)
    fun updateRealEstate() = runBlocking {
        val pictureArray = ArrayList<String>()
        val realEstate = RealEstate(
            0,
            "Duplex",
            123456,
            45,
            5,
            1,
            3,
            "this the description test",
            pictureArray,
            pictureArray.size,
            "address",
            "43.758614",
            "3.458762",
            pictureArray,
            "state",
            Utils.getTodayDate(),
            Utils.getTodayDate(),
            "michel"
        )
        realEstateDao.insert(realEstate)

        var allRealEstate = realEstateDao.getAllRealEstate().first()
        assertEquals(allRealEstate[0].property, realEstate.property)

        val property = "Studio"
        allRealEstate[0].property = property
        realEstateDao.updateRealEstate(allRealEstate[0])

        allRealEstate = realEstateDao.getAllRealEstate().first()
        assertEquals(allRealEstate[0].property, property)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllRealEstate() = runBlocking {
        val pictureArray = ArrayList<String>()
        val realEstate = RealEstate(
            0,
            "Duplex",
            123456,
            45,
            5,
            1,
            3,
            "this the description test",
            pictureArray,
            pictureArray.size,
            "address",
            "43.758614",
            "3.458762",
            pictureArray,
            "state",
            Utils.getTodayDate(),
            Utils.getTodayDate(),
            "michel"
        )
        realEstateDao.insert(realEstate)
        val realEstate2 = RealEstate(
            0,
            "Duplex",
            123456,
            45,
            5,
            1,
            3,
            "this the description test",
            pictureArray,
            pictureArray.size,
            "address",
            "43.758614",
            "3.458762",
            pictureArray,
            "state",
            Utils.getTodayDate(),
            Utils.getTodayDate(),
            "michel"
        )
        realEstateDao.insert(realEstate2)
        realEstateDao.deleteAll()
        val allRealEstate = realEstateDao.getAllRealEstate().first()
        assertTrue(allRealEstate.isEmpty())
    }
}
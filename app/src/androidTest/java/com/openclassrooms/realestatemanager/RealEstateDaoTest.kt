package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.model.RealEstate

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class RealEstateDaoTest {

    private lateinit var realEstateDao: RealEstateDao
    private lateinit var database: RealEstateDatabase


    @Before
    fun createDatabase() {
        val context: Context = ApplicationProvider.getApplicationContext()
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
        val realEstate = RealEstate(0, "property", 123456, "address")
        realEstateDao.insert(realEstate)
        val allRealEstate = realEstateDao.getAllRealEstate().first()
        assertEquals(allRealEstate[0].property, realEstate.property)
    }

    @Test
    @Throws(Exception::class)
    fun getAllRealEstate() = runBlocking {
        val realEstate1 = RealEstate(0, "property", 123456, "address")
        realEstateDao.insert(realEstate1)
        val realEstate2 = RealEstate(0, "property2", 123456, "address2")
        realEstateDao.insert(realEstate2)
        val allRealEstate = realEstateDao.getAllRealEstate().first()
        assertEquals(allRealEstate[0].property, realEstate2.property)
        assertEquals(allRealEstate[1].property, realEstate2.property)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllRealEstate() = runBlocking {
        val realEstate = RealEstate(0, "property", 123456, "address")
        realEstateDao.insert(realEstate)
        val realEstate2 = RealEstate(0, "property2", 123456, "address2")
        realEstateDao.insert(realEstate2)
        realEstateDao.deleteAll()
        val allRealEstate = realEstateDao.getAllRealEstate().first()
        assertTrue(allRealEstate.isEmpty())

    }
}
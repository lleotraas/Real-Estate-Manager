package com.openclassrooms.realestatemanager.dao_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.*
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.RealEstateDatabase
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstatePhotoDao
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
class RealEstatePhotoDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: RealEstateDatabase
    private lateinit var realEstatePhotoDao: RealEstatePhotoDao


    @Before
    fun setUp() {
        hiltRule.inject()
        realEstatePhotoDao = database.realEstatePhotoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPhoto() = runBlocking {
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_1)
        val realEstatePhoto = realEstatePhotoDao.getRealEstatePhotos(REAL_ESTATE_PHOTO_1.realEstateId).first()
        assertEquals(realEstatePhoto[0].photo, REAL_ESTATE_PHOTO_1.photo)
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
        val realEstatePhoto = realEstatePhotoDao.getRealEstatePhotos(REAL_ESTATE_PHOTO_1.realEstateId).first()
        assertTrue(realEstatePhoto.size == 3)
        assertEquals(realEstatePhoto[0].photo, REAL_ESTATE_PHOTO_1.photo)
        assertEquals(realEstatePhoto[1].photo, REAL_ESTATE_PHOTO_2.photo)
        assertEquals(realEstatePhoto[2].photo, REAL_ESTATE_PHOTO_3.photo)
    }

    @Test
    @Throws(Exception::class)
    fun updateRealEstatePhoto() = runBlocking {
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_1)
        var realEstatePhoto = realEstatePhotoDao.getRealEstatePhotos(REAL_ESTATE_PHOTO_1.realEstateId).first()
        assertEquals(realEstatePhoto[0].photo, REAL_ESTATE_PHOTO_1.photo)
        val expectedPhoto = REAL_ESTATE_PHOTO_3.photo
        val expectedCategory = REAL_ESTATE_PHOTO_3.category
        realEstatePhoto[0].photo = expectedPhoto
        realEstatePhoto[0].category = expectedCategory
        realEstatePhotoDao.updateRealEstatePhotos(realEstatePhoto[0])
        realEstatePhoto = realEstatePhotoDao.getRealEstatePhotos(REAL_ESTATE_PHOTO_1.realEstateId).first()
        assertEquals(expectedPhoto, realEstatePhoto[0].photo)
        assertEquals(expectedCategory, realEstatePhoto[0].category)
    }

    @Test
    @Throws(Exception::class)
    fun deleteRealEstatePhoto() = runBlocking {
        realEstatePhotoDao.insertPhoto(REAL_ESTATE_PHOTO_1)
        var realEstatePhoto = realEstatePhotoDao.getRealEstatePhotos(REAL_ESTATE_PHOTO_1.realEstateId).first()
        assertEquals(realEstatePhoto[0].photo, REAL_ESTATE_PHOTO_1.photo)
        realEstatePhotoDao.deleteRealEstatePhoto(REAL_ESTATE_PHOTO_1.id)
        realEstatePhoto = realEstatePhotoDao.getRealEstatePhotos(REAL_ESTATE_PHOTO_1.realEstateId).first()
        assertTrue(realEstatePhoto.isEmpty())
    }
}
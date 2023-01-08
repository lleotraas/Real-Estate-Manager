package com.openclassrooms.realestatemanager.content_provider_test

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_PHOTO_1
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.RealEstateDatabase
import com.openclassrooms.realestatemanager.features_real_estate.data.data_source.dao.RealEstateDao
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider.Companion.PHOTOS
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider.Companion.PROPERTY
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider.Companion.URI_REAL_ESTATE
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider.Companion.URI_REAL_ESTATE_PHOTO
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RealEstateContentProviderTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var realEstateDao: RealEstateDao
    private lateinit var database: RealEstateDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RealEstateDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        realEstateDao = database.realEstateDao()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
        runBlocking {
            realEstateDao.insert(REAL_ESTATE_1)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstateValue() {
        val cursor = contentResolver.query(ContentUris.withAppendedId(URI_REAL_ESTATE, REAL_ESTATE_1.id), null, PROPERTY, null, null)
        assertNotNull(cursor)
        assertEquals(1, cursor!!.count)
        assertTrue(cursor.moveToFirst())
        assertEquals("15 Quai Claude Bernard, Lyon, France", cursor.getString(cursor.getColumnIndexOrThrow("address")))
        cursor.close()
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstatePhotoValue() {
        val cursor = contentResolver.query(ContentUris.withAppendedId(URI_REAL_ESTATE_PHOTO, REAL_ESTATE_PHOTO_1.id), null, PHOTOS, null, null)
        assertNotNull(cursor)
        assertEquals(1, cursor!!.count)
        assertTrue(cursor.moveToFirst())
        val photo = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
        assertEquals("content://com.android.externalstorage.documents/document/primary%3ADCIM%2Fduplex4.webp", photo)
        cursor.close()
    }
}
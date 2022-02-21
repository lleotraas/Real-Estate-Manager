package com.openclassrooms.realestatemanager.content_provider_test

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest
import com.openclassrooms.realestatemanager.UtilsForIntegrationTest.Companion.REAL_ESTATE_1
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.database.dao.RealEstateDao
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider.Companion.TABLE_NAME_1
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
        val cursor = contentResolver.query(ContentUris.withAppendedId(URI_REAL_ESTATE, REAL_ESTATE_1.id), null, TABLE_NAME_1, null, null)
        assertNotNull(cursor)
        assertEquals(cursor!!.count, 1)
        assertTrue(cursor.moveToFirst())
        assertEquals("Georges Quay, Dublin, Irlande", cursor.getString(cursor.getColumnIndexOrThrow("address")))
        cursor.close()
    }

    @Test
    @Throws(Exception::class)
    fun getRealEstatePhotoValue() {
        val cursor = contentResolver.query(ContentUris.withAppendedId(URI_REAL_ESTATE_PHOTO, UtilsForIntegrationTest.REAL_ESTATE_PHOTO_1.id), null,
            RealEstateContentProvider.TABLE_NAME_2, null, null)
        assertNotNull(cursor)
        assertEquals(cursor!!.count, 1)
        assertTrue(cursor.moveToFirst())
        val photo = cursor.getString(cursor.getColumnIndexOrThrow("photo"))
        assertEquals("content://com.android.externalstorage.documents/document/primary%3ADCIM%2Findex.jpg", photo)
        cursor.close()
    }
}
package com.openclassrooms.realestatemanager.dependency

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.openclassrooms.realestatemanager.database.RealEstateDatabase
import com.openclassrooms.realestatemanager.repository.FilterRepository
import com.openclassrooms.realestatemanager.repository.RealEstatePhotoRepository
import com.openclassrooms.realestatemanager.repository.RealEstateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateApplication : Application() {
    private val database by lazy { RealEstateDatabase.getDatabase(this) }
    val realEstateRepository by lazy { RealEstateRepository(database.realEstateDao()) }
    val realEstateImageRepository by lazy {RealEstatePhotoRepository(database.realEstatePhotoDao())}
    val filterRepository by lazy { FilterRepository() }
    private val CHANNEL_NAME = "real_estate_created"
    private val CHANNEL_ID = "channel_id"

    override fun onCreate() {
        super.onCreate()
        isRunningTest = try {
            Class.forName("com.openclassrooms.realestatemanager.RetrofitTest")
            true
        } catch (exception: ClassNotFoundException) {
            false
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        var isRunningTest: Boolean = false
    }
}
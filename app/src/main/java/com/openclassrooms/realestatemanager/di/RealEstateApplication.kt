package com.openclassrooms.realestatemanager.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RealEstateApplication : Application() {

    private val CHANNEL_NAME = "real_estate_created"
    private val CHANNEL_ID = "channel_id"

    override fun onCreate() {
        super.onCreate()
        isRunningTest = try {
            Class.forName("com.openclassrooms.realestatemanager.retrofit_test.RetrofitTest")
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
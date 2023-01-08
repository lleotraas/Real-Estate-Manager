package com.openclassrooms.realestatemanager.features_real_estate.data.utils

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.openclassrooms.realestatemanager.R

class NotificationHelper(context: Context) {

    private val mContext = context
    private val CHANNEL_ID = "channel_id"


    fun createNotification(): Notification {
        return NotificationCompat.Builder(mContext, CHANNEL_ID)
           .setContentTitle(mContext.resources.getString(R.string.fragment_list_property_created))
           .setSmallIcon(R.drawable.ic_sharp_house)
           .setPriority(NotificationCompat.PRIORITY_HIGH)
           .build()
    }

    fun getNotificationManager(): NotificationManagerCompat {
        return NotificationManagerCompat.from(mContext)
    }

}
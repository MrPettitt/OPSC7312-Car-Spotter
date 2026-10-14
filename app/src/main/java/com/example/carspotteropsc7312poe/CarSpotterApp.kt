package com.example.carspotteropsc7312poe

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.carspotteropsc7312poe.notifications.NotificationScheduler

class CarSpotterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (NotificationScheduler.isEnabled(this)) NotificationScheduler.schedule(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notify"
    }
}

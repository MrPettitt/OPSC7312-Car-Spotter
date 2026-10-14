package com.example.carspotteropsc7312poe.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.carspotteropsc7312poe.CarSpotterApp
import com.example.carspotteropsc7312poe.R
import com.example.carspotteropsc7312poe.data.SampleCars
import com.example.carspotteropsc7312poe.ui.welcome.WelcomeActivity

/** Posts a "Car of the Day" notification. Runs daily via [NotificationScheduler]. */
class DailyCarWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val allowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!allowed) return Result.success()

        val car = SampleCars.all.random()
        val openApp = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, WelcomeActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val text = "${car.name}: ${car.description}"

        val notification = NotificationCompat.Builder(applicationContext, CarSpotterApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.car_of_the_day))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        return Result.success()
    }

    private companion object {
        const val NOTIFICATION_ID = 1001
    }
}

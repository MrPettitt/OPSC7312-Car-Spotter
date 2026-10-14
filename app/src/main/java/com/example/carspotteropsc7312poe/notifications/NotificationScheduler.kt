package com.example.carspotteropsc7312poe.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/** Schedules the daily "Car of the Day" notification with WorkManager. No server involved. */
object NotificationScheduler {

    private const val WORK_NAME = "daily_car_of_the_day"
    private const val PREFS = "notification_prefs"
    private const val KEY_ENABLED = "daily_enabled"
    const val NOTIFY_HOUR = 9

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (enabled) schedule(context) else cancel(context)
    }

    /** Safe to call on every app start: an existing schedule is kept. */
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<DailyCarWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(millisUntilNext(NOTIFY_HOUR, Calendar.getInstance()), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /** Milliseconds from [now] until the next occurrence of [hour]:00 (today if still ahead, else tomorrow). */
    fun millisUntilNext(hour: Int, now: Calendar): Long {
        val next = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return next.timeInMillis - now.timeInMillis
    }
}

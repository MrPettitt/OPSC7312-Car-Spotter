package com.example.carspotteropsc7312poe.notifications

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationSchedulerTest {

    private fun at(hour: Int, minute: Int) = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, 14, hour, minute, 0)
        set(Calendar.MILLISECOND, 0)
    }

    @Test
    fun beforeNotifyTime_waitsUntilLaterToday() {
        val delay = NotificationScheduler.millisUntilNext(9, at(7, 30))
        assertEquals(TimeUnit.MINUTES.toMillis(90), delay)
    }

    @Test
    fun afterNotifyTime_waitsUntilTomorrow() {
        val delay = NotificationScheduler.millisUntilNext(9, at(10, 0))
        assertEquals(TimeUnit.HOURS.toMillis(23), delay)
    }

    @Test
    fun exactlyAtNotifyTime_waitsAFullDay() {
        val delay = NotificationScheduler.millisUntilNext(9, at(9, 0))
        assertEquals(TimeUnit.HOURS.toMillis(24), delay)
    }
}

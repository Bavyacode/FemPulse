package com.saveetha.fempulse.utils

import com.saveetha.fempulse.*
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleDailyPeriodCheck(context: Context) {
    val prefs = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
    val hour = prefs.getInt("period_hour", 10)
    val minute = prefs.getInt("period_minute", 0)

    val now = Calendar.getInstance()
    val firstRun = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (firstRun.before(now)) firstRun.add(Calendar.DAY_OF_MONTH, 1)
    val initialDelay = firstRun.timeInMillis - now.timeInMillis

    val workRequest = PeriodicWorkRequestBuilder<PeriodCheckWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "PeriodCheckWorker",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

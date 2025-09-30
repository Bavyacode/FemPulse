package com.saveetha.fempulse

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.MenstrualPhaseResponse
import com.saveetha.fempulse.response.UserIdRequest
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.*

class PeriodCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sharedPref = applicationContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) return Result.failure()

        try {
            val response = RetrofitClient.instance.getManualPhases(UserIdRequest(userId)).awaitResponse()
            if (response.isSuccessful) {
                val nextPeriodStr = response.body()?.nextPeriod?.date ?: return Result.failure()
                val sdf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
                val nextPeriod = Calendar.getInstance().apply { time = sdf.parse(nextPeriodStr)!! }

                val prefs = applicationContext.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
                val hour = prefs.getInt("period_hour", 10)
                val minute = prefs.getInt("period_minute", 0)
                val daysBefore = prefs.getInt("period_days_before", 3)

                val today = Calendar.getInstance()
                nextPeriod.set(Calendar.HOUR_OF_DAY, hour)
                nextPeriod.set(Calendar.MINUTE, minute)
                nextPeriod.set(Calendar.SECOND, 0)
                nextPeriod.set(Calendar.MILLISECOND, 0)



                val diffInMillis = nextPeriod.timeInMillis - today.timeInMillis
                val diff = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()

                if (diff in 0..daysBefore) sendNotification(diff)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }

        return Result.success()
    }

    private fun sendNotification(daysRemaining: Int) {
        val channelId = "period_reminder_channel"
        val channelName = "Period Reminder"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.enableLights(true)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, HomeeditperiodActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = when {
            (daysRemaining > 0) -> "Your period is coming in $daysRemaining day${if (daysRemaining > 1) "s" else ""}.Take your essentials dear🤍"
            (daysRemaining == 0) -> "Your period is expected Soon. Log your period for future accuracy!.Take Care🩷"
            else -> "Log your data"
        }
        // Save to history
        val title = "Period Reminder"
        val prefs = applicationContext.getSharedPreferences("NotificationHistory", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("notifications", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val timestamp = System.currentTimeMillis()
        set.add("$timestamp|$title|$message")
        prefs.edit().putStringSet("notifications", set).apply()
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

package com.saveetha.fempulse

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "drink_water_channel"
        val channelName = "Drink Water Reminder"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val title = intent.getStringExtra("title") ?: "Drink Water Reminder"
        val message = intent.getStringExtra("message") ?: "Stay hydrated! Time for a glass of water."

        // Save to history
        val prefs = context.getSharedPreferences("NotificationHistory", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("notifications", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val timestamp = System.currentTimeMillis()
        set.add("$timestamp|$title|$message")
        prefs.edit().putStringSet("notifications", set).apply()

        val activityIntent = Intent(context, RemindersActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            timestamp.toInt(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        notificationManager.notify(timestamp.toInt(), builder.build())
    }
}

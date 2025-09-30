package com.saveetha.fempulse

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat

class PeriodReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val channelId = "period_reminder_channel"
        val channelName = "Period Reminder"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
            notificationManager.createNotificationChannel(channel)
        }

        val activityIntent = Intent(context, RemindersActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            intent.getIntExtra("alarm_id", 0), // unique ID from PeriodReminder
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(intent.getStringExtra("title") ?: "Period Reminder")
            .setContentText(intent.getStringExtra("message") ?: "Your period reminder")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

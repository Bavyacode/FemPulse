package com.saveetha.fempulse

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.*
import androidx.cardview.widget.CardView
import java.util.*

class DrinkWater(private val context: Context) {

    private lateinit var switchDrinkWater: Switch
    private lateinit var cardSettings: CardView
    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private lateinit var btnSave: Button
    private lateinit var spinnerInterval: Spinner

    private var startHour = -1
    private var startMinute = -1
    private var endHour = -1
    private var endMinute = -1
    private var intervalMinutes = 60

    fun init() {
        if (context !is RemindersActivity) return

        switchDrinkWater = context.findViewById(R.id.switch_drink_water)
        cardSettings = context.findViewById(R.id.cardDrinkWaterSettings)
        btnStart = context.findViewById(R.id.btnSetStartTime)
        btnEnd = context.findViewById(R.id.btnSetEndTime)
        btnSave = context.findViewById(R.id.btnSaveTimes)
        spinnerInterval = context.findViewById(R.id.spinner_interval)

        val sharedPref = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

        // Restore switch states
        switchDrinkWater.isChecked = sharedPref.getBoolean("drink_water", false)

        cardSettings.visibility = if (switchDrinkWater.isChecked) CardView.VISIBLE else CardView.GONE

        // Restore times
        startHour = sharedPref.getInt("drinkwater_start_hour", -1)
        startMinute = sharedPref.getInt("drinkwater_start_minute", -1)
        endHour = sharedPref.getInt("drinkwater_end_hour", -1)
        endMinute = sharedPref.getInt("drinkwater_end_minute", -1)

        if (startHour >= 0 && startMinute >= 0) btnStart.text = "Start: %02d:%02d".format(startHour, startMinute)
        if (endHour >= 0 && endMinute >= 0) btnEnd.text = "End: %02d:%02d".format(endHour, endMinute)

        val intervals = listOf("Every 30 min", "Every 1 hour", "Every 1.5 hours", "Every 2 hours")
        spinnerInterval.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, intervals).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerInterval.setSelection(sharedPref.getInt("interval_position", 1))

        // Switch listeners
        switchDrinkWater.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("drink_water", isChecked).apply()
            cardSettings.visibility = if (isChecked) CardView.VISIBLE else CardView.GONE
            if (!isChecked) cancelAllReminders() else if (isTimeSet()) scheduleReminders()
        }



        btnStart.setOnClickListener { pickTime(true) }
        btnEnd.setOnClickListener { pickTime(false) }

        btnSave.setOnClickListener {
            if (!isTimeSet()) {
                Toast.makeText(context, "Select start and end time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            intervalMinutes = when (spinnerInterval.selectedItemPosition) {
                0 -> 30
                1 -> 60
                2 -> 90
                3 -> 120
                else -> 60
            }

            scheduleReminders()

            // Save prefs
            sharedPref.edit()
                .putInt("drinkwater_start_hour", startHour)
                .putInt("drinkwater_start_minute", startMinute)
                .putInt("drinkwater_end_hour", endHour)
                .putInt("drinkwater_end_minute", endMinute)
                .putInt("interval_position", spinnerInterval.selectedItemPosition)
                .apply()

            Toast.makeText(context, "Drink Water reminders set!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isTimeSet() = startHour >= 0 && startMinute >= 0 && endHour >= 0 && endMinute >= 0

    private fun pickTime(isStart: Boolean) {
        val cal = Calendar.getInstance()
        android.app.TimePickerDialog(context, { _, hour, minute ->
            if (isStart) {
                startHour = hour
                startMinute = minute
                btnStart.text = "Start: %02d:%02d".format(hour, minute)
            } else {
                endHour = hour
                endMinute = minute
                btnEnd.text = "End: %02d:%02d".format(hour, minute)
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    private fun scheduleReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelAllReminders()

        val startCal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, startHour); set(Calendar.MINUTE, startMinute); set(Calendar.SECOND, 0) }
        val endCal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, endHour); set(Calendar.MINUTE, endMinute); set(Calendar.SECOND, 0) }

        if (endCal.before(startCal)) endCal.add(Calendar.DAY_OF_MONTH, 1)

        var requestCode = 3000
        val now = Calendar.getInstance()

        while (startCal.before(endCal)) {
            if (startCal.after(now)) {
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("title", "Drink Water 💧")
                    putExtra("message", "Stay hydrated! Have a glass of water now.")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startCal.timeInMillis, pendingIntent)
            }
            startCal.add(Calendar.MINUTE, intervalMinutes)
            requestCode++
        }

        context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
            .edit().putInt("drinkwater_alarm_count", requestCode - 3000).apply()
    }

    private fun cancelAllReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sharedPref = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val count = sharedPref.getInt("drinkwater_alarm_count", 0)
        for (i in 0 until count) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                3000 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        sharedPref.edit().putInt("drinkwater_alarm_count", 0).apply()
    }
}

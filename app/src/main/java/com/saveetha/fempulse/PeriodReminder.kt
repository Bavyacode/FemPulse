package com.saveetha.fempulse

import android.app.TimePickerDialog
import android.content.Context
import android.widget.*
import androidx.cardview.widget.CardView
import com.saveetha.fempulse.response.MenstrualPhaseResponse
import com.saveetha.fempulse.response.UserIdRequest
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.utils.scheduleDailyPeriodCheck
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PeriodReminder(private val activity: RemindersActivity) {

    private lateinit var switchRemindBefore: Switch
    private lateinit var cardPeriodReminder: CardView
    private lateinit var spinnerDaysBefore: Spinner
    private lateinit var txtNotificationTime: TextView
    private lateinit var btnSave: Button
    private var selectedHour = 10
    private var selectedMinute = 0

    fun init() {
        switchRemindBefore = activity.findViewById(R.id.switch_remind_before_period)
        cardPeriodReminder = activity.findViewById(R.id.cardPeriodReminder)
        spinnerDaysBefore = activity.findViewById(R.id.spinner_days_before)
        txtNotificationTime = activity.findViewById(R.id.txt_period_notification_time)
        btnSave = activity.findViewById(R.id.btnSavePeriodReminder)

        // Spinner values 1–7
        val daysList = (1..7).toList()
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, daysList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDaysBefore.adapter = adapter

        val sharedPref = activity.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

        // Restore toggle state
        val isRemindOn = sharedPref.getBoolean("remind_before_period", false)
        switchRemindBefore.isChecked = isRemindOn
        cardPeriodReminder.visibility = if (isRemindOn) LinearLayout.VISIBLE else LinearLayout.GONE

        switchRemindBefore.setOnCheckedChangeListener { _, isChecked ->
            cardPeriodReminder.visibility = if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE
            sharedPref.edit().putBoolean("remind_before_period", isChecked).apply()
        }

        // Time picker
        txtNotificationTime.setOnClickListener {
            TimePickerDialog(
                activity,
                { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val hourDisplay = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                    txtNotificationTime.text =
                        String.format("%02d:%02d %s", hourDisplay, minute, amPm)
                },
                selectedHour,
                selectedMinute,
                false
            ).show()
        }

        // Restore saved settings
        val savedDays = sharedPref.getInt("period_days_before", 3)
        val savedHour = sharedPref.getInt("period_hour", 10)
        val savedMinute = sharedPref.getInt("period_minute", 0)
        selectedHour = savedHour
        selectedMinute = savedMinute
        spinnerDaysBefore.setSelection((spinnerDaysBefore.adapter as ArrayAdapter<Int>).getPosition(savedDays))
        val amPm = if (savedHour < 12) "AM" else "PM"
        val hourDisplay = if (savedHour % 12 == 0) 12 else savedHour % 12
        txtNotificationTime.text = String.format("%02d:%02d %s", hourDisplay, savedMinute, amPm)

        // Save button
        btnSave.setOnClickListener {
            if (!switchRemindBefore.isChecked) return@setOnClickListener
            val daysBefore = spinnerDaysBefore.selectedItem as Int

            sharedPref.edit()
                .putInt("period_days_before", daysBefore)
                .putInt("period_hour", selectedHour)
                .putInt("period_minute", selectedMinute)
                .apply()

            // Start WorkManager to check daily
            scheduleDailyPeriodCheck(activity)

            Toast.makeText(activity, "Period reminders set!", Toast.LENGTH_SHORT).show()
        }
    }
}

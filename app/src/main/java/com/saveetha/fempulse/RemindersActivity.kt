package com.saveetha.fempulse

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class RemindersActivity : AppCompatActivity() {

    private lateinit var drinkWater: DrinkWater
    private lateinit var periodReminder: PeriodReminder
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }
        // Fetch userId
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) return

        // Initialize DrinkWater
        drinkWater = DrinkWater(this)
        drinkWater.init()


        // Initialize Period Reminder
        periodReminder = PeriodReminder(this )
        periodReminder.init()

    }
}

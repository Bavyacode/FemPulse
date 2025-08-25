package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val editperiodlayyout = findViewById<LinearLayout>(R.id.edit_period_layout)
        editperiodlayyout.setOnClickListener {
            val intent = Intent(this, EditPeriodActivity::class.java)
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        val notificationhistory = findViewById<LinearLayout>(R.id.notification_history_layout)
        editperiodlayyout.setOnClickListener {
            val intent = Intent(this, NotificationhistoryActivity::class.java)
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}
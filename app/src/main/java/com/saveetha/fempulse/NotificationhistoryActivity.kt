package com.saveetha.fempulse

import NotificationAdapter
import NotificationItem
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationhistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationhistory)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val historyPref = getSharedPreferences("NotificationHistory", Context.MODE_PRIVATE)
        val set = historyPref.getStringSet("notifications", setOf()) ?: setOf()
        val notifications = set.map {
            val parts = it.split("|")
            NotificationItem(parts[0].toLong(), parts[1], parts[2])
        }.sortedByDescending { it.timestamp } // latest first
            .take(20)
        recyclerView.adapter = NotificationAdapter(notifications)
    }
}

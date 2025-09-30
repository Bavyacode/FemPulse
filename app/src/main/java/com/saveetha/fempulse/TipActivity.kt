package com.saveetha.fempulse

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WellnessTipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip)

        val category = intent.getStringExtra("category") ?: "Unknown"
        val tips = intent.getStringArrayListExtra("tips") ?: arrayListOf()
        Log.d("WELLNESS", "Received category=$category, tips=${tips.size}")

        // Set title
        val titleView = findViewById<TextView>(R.id.tvCategoryTitle)
        titleView.text = category

        // RecyclerView setup
        val recyclerView = findViewById<RecyclerView>(R.id.rvTips)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = WellnessTipAdapter(tips)
    }
}

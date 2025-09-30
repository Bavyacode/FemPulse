package com.saveetha.fempulse

import android.content.ContentValues.TAG
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.adapters.HealthTipAdapter
import com.saveetha.fempulse.retrofit.*
import com.saveetha.fempulse.response.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddlogsActivity : BaseActivity() {

    private lateinit var recyclerViewTips: RecyclerView
    override fun getCurrentNavId(): Int = R.id.nav_add
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addlogs)

        recyclerViewTips = findViewById(R.id.recyclerViewHealthTips)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        Log.d(TAG, "user_id from prefs = $userId")
        if (userId == -1) {
            Toast.makeText(this, "User ID not found in preferences", Toast.LENGTH_SHORT).show()
            return
        }

        findViewById<LinearLayout>(R.id.physical).setOnClickListener {
            startActivity(
                Intent(this, SymptomsActivity::class.java).putExtra(
                    "category",
                    "physical"
                )
            )
        }
        findViewById<LinearLayout>(R.id.mood).setOnClickListener {
            startActivity(Intent(this, SymptomsActivity::class.java).putExtra("category", "mood"))
        }
        findViewById<LinearLayout>(R.id.behaviour).setOnClickListener {
            startActivity(
                Intent(this, SymptomsActivity::class.java).putExtra(
                    "category",
                    "behavioral"
                )
            )
        }

        val btnTips = findViewById<Button>(R.id.btnThird)
        btnTips.setOnClickListener {
            val sharedPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = sharedPrefs.getInt("user_id", -1)
            startActivity(Intent(this, HealthTipsActivity::class.java).putExtra("user_id", userId))
        }
        val log : LinearLayout = findViewById(R.id.periodlog)
        log.setOnClickListener {
            startActivity(Intent(this,HomeeditperiodActivity::class.java))
        }

    }


}

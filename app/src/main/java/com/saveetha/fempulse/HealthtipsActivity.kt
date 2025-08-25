package com.saveetha.fempulse

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.saveetha.fempulse.adapters.HealthTipAdapter
import com.saveetha.fempulse.response.HealthTip
import com.saveetha.fempulse.response.HealthTipsResponse
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthTipsActivity : AppCompatActivity() {

    private lateinit var recyclerViewTips: RecyclerView
    private lateinit var adapter: HealthTipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthtipsactivity)

        recyclerViewTips = findViewById(R.id.recyclerViewHealthTips)
        recyclerViewTips.layoutManager = LinearLayoutManager(this)

        adapter = HealthTipAdapter()  // Start empty
        recyclerViewTips.adapter = adapter

        // Get user ID from SharedPreferences
        val sharedPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)
        Log.d("HealthTipsActivity", "UserID = $userId")
        if (userId != -1) {
            fetchHealthTips(userId)
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchHealthTips(userId: Int) {
        val request = mapOf("user_id" to userId)
        Log.d("HealthTipRequest", Gson().toJson(request))

        RetrofitClient.instance.getHealthTips(request)
            .enqueue(object : Callback<HealthTipsResponse> {
                override fun onResponse(
                    call: Call<HealthTipsResponse>,
                    response: Response<HealthTipsResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val tipsList: List<HealthTip> = response.body()!!.tips
                        adapter.setTips(tipsList)  // update adapter
                    } else {
                        Toast.makeText(
                            this@HealthTipsActivity,
                            "No tips available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<HealthTipsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@HealthTipsActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

package com.saveetha.fempulse

import FullHistoryAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.adapters.*
import com.saveetha.fempulse.response.FullHistoryListItem
import com.saveetha.fempulse.response.FullHistoryResponse
import com.saveetha.fempulse.response.HistoryItem
import com.saveetha.fempulse.response.UserIdRequest
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCycleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var tvAveragePeriodDays: TextView
    private lateinit var tvAverageCycleDays: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycycle)

        recyclerView = findViewById(R.id.recyclerViewFullHistory)
        backButton = findViewById(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton.setOnClickListener {
            finish() // Go back to previous activity
        }
        val addperiod : Button = findViewById(R.id.addperiod)
        addperiod.setOnClickListener {
            startActivity(Intent(this,HomeeditperiodActivity::class.java))
        }
        tvAveragePeriodDays = findViewById(R.id.tvAveragePeriodDays)
        tvAverageCycleDays = findViewById(R.id.tvAverageCycleDays)

        // Example: Fetch values from SharedPreferences or API
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val averagePeriod = sharedPref.getInt("average_period_length",5) // default 5
        val averageCycle = sharedPref.getInt("average_cycle_length", 28) // default 28

        tvAveragePeriodDays.text = "$averagePeriod days"
        tvAverageCycleDays.text = "$averageCycle days"
        fetchFullHistory()
    }

    private fun fetchFullHistory() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) return

        val api = RetrofitClient.instance
        val request = UserIdRequest(userId = userId)

        api.getFullHistory(request).enqueue(object : Callback<FullHistoryResponse> {
            override fun onResponse(
                call: Call<FullHistoryResponse>,
                response: Response<FullHistoryResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    // The API response has history grouped by year (Map<String, List<HistoryItem>>)
                    val historyMap: Map<String, List<HistoryItem>> = response.body()?.history ?: mapOf()
                    val list = mutableListOf<FullHistoryListItem>()

                    // Flatten the year-wise map into a single list with headers
                    for ((year, historyItems) in historyMap) {
                        list.add(FullHistoryListItem.YearHeader(year))
                        historyItems.forEach { historyItem ->
                            list.add(FullHistoryListItem.History(historyItem))
                        }
                    }

                    val adapter = FullHistoryAdapter(list)
                    recyclerView.adapter = adapter

                } else {
                    Toast.makeText(this@MyCycleActivity, "No history found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FullHistoryResponse>, t: Throwable) {
                Toast.makeText(
                    this@MyCycleActivity,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

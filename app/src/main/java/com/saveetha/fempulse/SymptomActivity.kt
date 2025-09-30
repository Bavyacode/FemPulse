package com.saveetha.fempulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.adapters.SymptomAdapter
import com.saveetha.fempulse.response.*
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SymptomsActivity : AppCompatActivity() {
    private lateinit var adapter: SymptomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var btnSave: Button
    private var userId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)

        recyclerView = findViewById(R.id.recyclerViewSymptoms)
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
        btnSave = findViewById(R.id.btnSaveSymptoms) // ⚠️ make sure you have a Save button in layout
        // ✅ Retrieve user_id from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_SHORT).show()
            finish() // exit activity if no user_id
            return
        }
        val category = intent.getStringExtra("category") ?: return
        tvCategoryTitle.text = category.replaceFirstChar { it.uppercase() }

        fetchSymptoms(category)

        btnSave.setOnClickListener {
            saveSelectedSymptoms()
        }
        val back : ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // close activity, go back
        }
    }

    private fun fetchSymptoms(category: String) {
        val api = RetrofitClient.instance
        val body = mapOf("category" to category)

        api.getSymptoms(body).enqueue(object : Callback<SymptomResponse> {
            override fun onResponse(call: Call<SymptomResponse>, response: Response<SymptomResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val symptoms = response.body()!!.symptoms
                    setupRecycler(symptoms)
                } else {
                    Toast.makeText(this@SymptomsActivity, "No symptoms found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SymptomResponse>, t: Throwable) {
                Toast.makeText(this@SymptomsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecycler(symptoms: List<Symptom>) {
        adapter = SymptomAdapter(symptoms) { selectedList ->
            val selectedNames = selectedList.joinToString { it.name }

        }
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
    }

    private fun saveSelectedSymptoms() {
        val selectedSymptoms = adapter.getSelectedSymptoms()
        if (selectedSymptoms.isEmpty()) {
            Toast.makeText(this, "Please select at least one symptom", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIds = selectedSymptoms.map { it.id } // ✅ extract only IDs

        val request = LogSymptomsRequest(
            user_id = userId,  // TODO: replace with real logged-in user_id
            symptom_id = selectedIds
        )

        RetrofitClient.instance.logSymptoms(request)
            .enqueue(object : Callback<LogResponse> {
                override fun onResponse(call: Call<LogResponse>, response: Response<LogResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@SymptomsActivity, "Saved successfully!", Toast.LENGTH_SHORT).show()
                        Log.d("API_SUCCESS", response.body().toString())
                    } else {
                        Toast.makeText(this@SymptomsActivity, "Save failed!", Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", response.errorBody()?.string() ?: "Unknown error")
                    }
                }

                override fun onFailure(call: Call<LogResponse>, t: Throwable) {
                    Toast.makeText(this@SymptomsActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}

package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.UserIdRequest
import com.saveetha.fempulse.response.WellnessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WellnessActivity : AppCompatActivity() {

    private var wellnessResponse: WellnessResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wellness)

        // ✅ Get user_id from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "User ID not found in preferences", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Call API with userId
        RetrofitClient.instance.getWellnessTips(UserIdRequest(userId))
            .enqueue(object : Callback<WellnessResponse> {
                override fun onResponse(
                    call: Call<WellnessResponse>,
                    response: Response<WellnessResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        wellnessResponse = response.body()
                        setupCardClicks()
                    } else {
                        Toast.makeText(this@WellnessActivity, "Failed to load tips", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<WellnessResponse>, t: Throwable) {

                    t.printStackTrace()
                }
            })

        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }
    }

    private fun setupCardClicks() {
        findViewById<LinearLayout>(R.id.card_period_comfort).setOnClickListener {
            openTips("Period & Comfort")
        }

        findViewById<LinearLayout>(R.id.card_energy_focus).setOnClickListener {
            openTips("Energy & Focus")
        }

        findViewById<LinearLayout>(R.id.card_mood_emotions).setOnClickListener {
            openTips("Mood & Emotions")
        }

        findViewById<LinearLayout>(R.id.card_nutrition_cravings).setOnClickListener {
            openTips("Nutrition & Cravings")
        }

        findViewById<LinearLayout>(R.id.card_explore_tips).setOnClickListener {
            openTips("Explore All Tips")
        }
    }

    private fun openTips(category: String) {
        val tipsList = wellnessResponse?.tips?.get(category) ?: listOf()
        Log.d("WELLNESS", "Opening category: $category, tips found: ${tipsList.size}")

        if (tipsList.isEmpty()) {
            Toast.makeText(this, "No tips available for $category", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, WellnessTipsActivity::class.java)
        intent.putExtra("category", category)
        intent.putStringArrayListExtra("tips", ArrayList(tipsList))
        startActivity(intent)
    }

}

package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.retrofit.*
import com.saveetha.fempulse.response.*
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val feedbackInput: EditText = findViewById(R.id.etFeedback)
        val submitButton: Button = findViewById(R.id.btnSendFeedback)

        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val savedEmail = prefs.getString("email", null)
        val btnBack: ImageView = findViewById(R.id.Back)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }
        submitButton.setOnClickListener {
            val feedback = feedbackInput.text.toString().trim()

            if (feedback.isEmpty() || savedEmail.isNullOrEmpty()) {
                // If no feedback or no email saved, just navigate back
                startActivity(Intent(this@FeedbackActivity, HelpsupportActivity::class.java))
                finish()
                return@setOnClickListener
            }

            val request = FeedbackRequest(feedback, savedEmail)
            RetrofitClient.instance.sendFeedback(request).enqueue(object : Callback<FeedbackResponse> {
                override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                    // Navigate back immediately regardless of success
                    val intent = Intent(this@FeedbackActivity, HelpsupportActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                    // Navigate back even if request failed
                    val intent = Intent(this@FeedbackActivity, HelpsupportActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
        }
    }
}

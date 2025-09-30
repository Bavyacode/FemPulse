package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.CycleIntervalRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog

class CycleintervalActivity : AppCompatActivity() {
    private lateinit var textinterval: TextView
    private lateinit var btnplus: Button
    private lateinit var btnminus: Button
    private lateinit var Continuebtn: Button
    private lateinit var notSureText: TextView
    private var interval_days: Int = 28  // Default duration
    private var userId: Int = -1   // Get from intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycleinterval)

        // Get views
        textinterval = findViewById(R.id.text_days)
        btnplus = findViewById(R.id.btn_plus)
        btnminus = findViewById(R.id.btn_minus)
        Continuebtn = findViewById(R.id.btn_continue)
        notSureText = findViewById(R.id.amnotsure)

        // Get user ID from intent
        userId = intent.getIntExtra("user_id", -1)

        // Show default duration
        updateIntervalText()

        btnplus.setOnClickListener {
            if (interval_days < 45) {
                interval_days++
                updateIntervalText()
            }
        }

        btnminus.setOnClickListener {
            if (interval_days > 10) {
                interval_days--
                updateIntervalText()
            }
        }

        notSureText.setOnClickListener {
            interval_days = 28  // fallback
            updateIntervalText()
            showDefaultIntervalDialog()

        }

        Continuebtn.setOnClickListener {
            showCycleIntervalDialog(interval_days) // <-- Call warning dialog first
        }
    }

    private fun updateIntervalText() {
        textinterval.text = "$interval_days"
    }

    private fun showDefaultIntervalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val gotItButton = dialogView.findViewById<Button>(R.id.got_it)
        gotItButton.setOnClickListener {
            interval_days = 28
            updateIntervalText()
            dialog.dismiss()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("interval_days", interval_days)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    // 🔸 Show warning dialog for unusual duration
    private fun showCycleIntervalDialog(interval: Int) {
        val message = when {
            interval< 21 -> "Your cycle interval is shorter than normal. Please consider consulting a doctor."
            interval > 35 -> "Your cycle interval is longer than normal. Please consider consulting a doctor."
            else -> {
                sendIntervalToBackend() // Normal range, just proceed
                return
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Cycle Interval Warning")
            .setMessage(message)
            .setPositiveButton("Proceed") { _, _ ->
                sendIntervalToBackend() // Proceed after acknowledging
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.my_primary))
    }

    private fun sendIntervalToBackend() {
        val requesting = CycleIntervalRequest(
            user_id = userId,
            interval_days = interval_days
        )

        RetrofitClient.instance.saveCycleInterval(requesting).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CycleintervalActivity, "Saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CycleintervalActivity, HomeActivity::class.java)
                    intent.putExtra("user_id", userId)
                    intent.putExtra("interval_days", interval_days)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CycleintervalActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CycleintervalActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

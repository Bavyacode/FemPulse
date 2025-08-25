package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.CycleDurationRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog

class CycledurationActivity : AppCompatActivity() {
    private lateinit var textDuration: TextView
    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var btnContinue: Button
    private lateinit var notSureText: TextView
    private var duration: Int = 5  // Default duration
    private var userId: Int = -1   // Get from intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycleduration)

        // Get views
        textDuration = findViewById(R.id.text_duration)
        btnIncrease = findViewById(R.id.btn_increase)
        btnDecrease = findViewById(R.id.btn_decrease)
        btnContinue = findViewById(R.id.btn_continue)
        notSureText = findViewById(R.id.notsure)

        // Get user ID from intent
        userId = intent.getIntExtra("user_id", -1)

        // Show default duration
        updateDurationText()

        btnIncrease.setOnClickListener {
            if (duration < 15) {
                duration++
                updateDurationText()
            }
        }

        btnDecrease.setOnClickListener {
            if (duration > 1) {
                duration--
                updateDurationText()
            }
        }

        notSureText.setOnClickListener {
            duration = 5  // fallback
            updateDurationText()
            showDefaultDurationDialog()
            Toast.makeText(this, "Defaulting to 5 days", Toast.LENGTH_SHORT).show()
        }

        btnContinue.setOnClickListener {
            showCycleDurationDialog(duration) // <-- Call warning dialog first
        }
    }

    private fun updateDurationText() {
        textDuration.text = "$duration"
    }

    private fun showDefaultDurationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_duration, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val gotItButton = dialogView.findViewById<Button>(R.id.btn_got_it)
        gotItButton.setOnClickListener {
            duration = 5
            updateDurationText()
            dialog.dismiss()
            val intent = Intent(this, CycleintervalActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("cycle_duration", duration)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    // 🔸 Show warning dialog for unusual duration
    private fun showCycleDurationDialog(duration: Int) {
        val message = when {
            duration < 3 -> "Your cycle duration is shorter than normal. Please consult a doctor."
            duration > 7 -> "Your cycle duration is longer than normal. Please consult a doctor."
            else -> {
                sendDurationToBackend() // Normal range, just proceed
                return
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Cycle Duration Warning")
            .setMessage(message)
            .setPositiveButton("Proceed") { _, _ ->
                sendDurationToBackend() // Proceed after acknowledging
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.my_primary))
    }

    private fun sendDurationToBackend() {
        val request = CycleDurationRequest(
            user_id = userId,
            duration = duration
        )

        RetrofitClient.instance.saveCycleDuration(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CycledurationActivity, "Saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CycledurationActivity, CycleintervalActivity::class.java)
                    intent.putExtra("user_id", userId)
                    intent.putExtra("cycle_duration", duration)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CycledurationActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CycledurationActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

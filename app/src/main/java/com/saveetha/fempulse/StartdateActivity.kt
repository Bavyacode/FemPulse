package com.saveetha.fempulse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.CycledataRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StartdateActivity : AppCompatActivity() {
    private lateinit var selectDateText: TextView
    private lateinit var calendarIcon: ImageView
    private lateinit var continueButton: Button
    private var selectedDate: String? = null
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startdate)

        userId = intent.getIntExtra("user_id", -1)
        selectDateText = findViewById(R.id.select_date_text)
        calendarIcon = findViewById(R.id.calendar_icon)
        continueButton = findViewById(R.id.btn_continue)

        calendarIcon.setOnClickListener {
            showDatePicker()
        }

        continueButton.setOnClickListener {
            if (selectedDate.isNullOrBlank() || selectedDate == "0000-00-00") {
                showCustomOverlay()
                return@setOnClickListener
            }

            val selectedLocalDate = LocalDate.parse(selectedDate)
            if (selectedLocalDate.isAfter(LocalDate.now())) {
                showInvalidFutureDateDialog()
            } else {
                sendStartDateToBackend()
            }

        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )
                selectedDate = formattedDate
                selectDateText.text = formattedDate
                continueButton.isEnabled = true
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showCustomOverlay() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_start_date_required, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val btnSelectDate = dialogView.findViewById<Button>(R.id.btn_select_date)
        btnSelectDate.setOnClickListener {
            alertDialog.dismiss()
            showDatePicker()
        }

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    // 🚫 Show dialog if future date is selected
    private fun showInvalidFutureDateDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Invalid Start Date")
            .setMessage("Start date cannot be in the future. Please select a valid date.")
            .setPositiveButton("Change Date") { d, _ ->
                d.dismiss()
                showDatePicker()
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.my_primary))
    }

    private fun sendStartDateToBackend() {
        if (selectedDate.isNullOrBlank() || selectedDate == "0000-00-00") {
            Toast.makeText(this, "Start date is not valid", Toast.LENGTH_SHORT).show()
            return
        }

        val requestData = CycledataRequest(
            user_id = userId,
            start_date = selectedDate!!
        )

        Log.d("API_DEBUG", "Sending start_date: $selectedDate for user_id: $userId")

        RetrofitClient.instance.saveCycleData(requestData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Date saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@StartdateActivity, CycledurationActivity::class.java)
                    intent.putExtra("user_id", userId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

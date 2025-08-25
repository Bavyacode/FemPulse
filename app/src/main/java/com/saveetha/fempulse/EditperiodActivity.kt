package com.saveetha.fempulse

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.databinding.ActivityEditperiodBinding
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.EditPEriodRequest
import com.saveetha.fempulse.response.EditPeriodResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditPeriodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditperiodBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)
        selectedStartDate = sharedPref.getString("stored_start_date_${userId}", null)

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityEditperiodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If we already have a start date saved, show it
        selectedStartDate?.let {
            binding.startDateEdit.setText(it)
        }

        binding.startDateEdit.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                binding.startDateEdit.setText(date)
                saveStartDate(date) // Save in SharedPreferences
                sendStartDateToBackend(date)
            }
        }

        binding.endDateEdit.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                binding.endDateEdit.setText(date)
            }
        }

        binding.saveBtn.setOnClickListener {
            if (selectedEndDate.isNullOrEmpty()) {
                Toast.makeText(this, "Please select end date", Toast.LENGTH_SHORT).show()
            } else {
                sendEndDateToBackend()
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                onDateSelected(dateFormat.format(cal.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun saveStartDate(date: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPref.edit().putString("stored_start_date_${userId}", date).apply()

    }

    private fun sendStartDateToBackend(date: String) {
        val request = EditPEriodRequest(
            user_id = userId,
            start_date = date,
            end_date = null // Only start date
        )

        RetrofitClient.instance.updateCycleData(request)
            .enqueue(object : Callback<EditPeriodResponse> {
                override fun onResponse(call: Call<EditPeriodResponse>, response: Response<EditPeriodResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditPeriodActivity, "Start date saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EditPeriodActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EditPeriodResponse>, t: Throwable) {
                    Toast.makeText(this@EditPeriodActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun sendEndDateToBackend() {
        if (selectedStartDate.isNullOrEmpty()) {
            Toast.makeText(this, "Start date missing!", Toast.LENGTH_SHORT).show()
            return
        }

        val request = EditPEriodRequest(
            user_id = userId,
            start_date = selectedStartDate!!,
            end_date = selectedEndDate!!
        )

        RetrofitClient.instance.updateCycleData(request)
            .enqueue(object : Callback<EditPeriodResponse> {
                override fun onResponse(call: Call<EditPeriodResponse>, response: Response<EditPeriodResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditPeriodActivity, "End date saved", Toast.LENGTH_SHORT).show()

                        // ✅ Clear stored start date so it's not shown next time
                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        sharedPref.edit().remove("stored_start_date_${userId}").apply()


                        finish()
                    } else {
                        Toast.makeText(this@EditPeriodActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EditPeriodResponse>, t: Throwable) {
                    Toast.makeText(this@EditPeriodActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}

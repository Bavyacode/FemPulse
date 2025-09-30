package com.saveetha.fempulse

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.CommonResponse
import com.saveetha.fempulse.response.IntervalRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditIntervalActivity : AppCompatActivity() {

    private lateinit var numberPicker: NumberPicker
    private lateinit var switchUseAverage: Switch
    private lateinit var btnSave: TextView
    private lateinit var tvSelectedDays: TextView

    private var selectedDays = 28
    private var useAverage = false
    private var userId: Int = -1 // will fetch from SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_period_interval)

        // 🔹 Fetch userId from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔹 Bind views
        numberPicker = findViewById(R.id.numberPickerCycle) // reuse same id
        switchUseAverage = findViewById(R.id.switch_use_average)
        btnSave = findViewById(R.id.btnSave)
        tvSelectedDays = findViewById(R.id.tv_selected_days)

        // 🔹 Configure NumberPicker (interval usually 21–40 days)
        numberPicker.minValue = 21
        numberPicker.maxValue = 40
        numberPicker.value = selectedDays
        numberPicker.wrapSelectorWheel = false

        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }
        // Show initial value
        tvSelectedDays.text = "$selectedDays Days"

        numberPicker.setOnValueChangedListener { _, _, newVal ->
            selectedDays = newVal
            tvSelectedDays.text = "$newVal Days"
        }

        // 🔹 Handle switch toggle
        switchUseAverage.setOnCheckedChangeListener { _, isChecked ->
            useAverage = isChecked
            if (isChecked) {

            }
        }

        // 🔹 Save button
        btnSave.setOnClickListener {
            saveCycleInterval()
        }
    }

    private fun saveCycleInterval() {
        val intervalDays = numberPicker.value
        val request = IntervalRequest(userId, intervalDays)

        val api = RetrofitClient.instance
        api.updateCycleInterval(request).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@EditIntervalActivity, "Saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditIntervalActivity, "Failed to save", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Toast.makeText(this@EditIntervalActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

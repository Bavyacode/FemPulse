package com.saveetha.fempulse

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.retrofit.RetrofitClient
import com.saveetha.fempulse.response.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditLengthActivity : AppCompatActivity() {

    private lateinit var numberPicker: NumberPicker
    private lateinit var switchUseAverage: Switch
    private lateinit var btnSave: TextView
    private lateinit var tvSelectedDays: TextView

    private var selectedDays = 7
    private var useAverage = false
    private val userId = 245 // TODO: get from SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_length)

        // 🔹 Bind views
        numberPicker = findViewById(R.id.numberPickerPeriod)
        switchUseAverage = findViewById(R.id.switch_use_average)
        btnSave = findViewById(R.id.btnSave)
        tvSelectedDays = findViewById(R.id.tv_selected_days)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }

        // 🔹 Configure NumberPicker
        numberPicker.minValue = 3
        numberPicker.maxValue = 10
        numberPicker.value = selectedDays
        numberPicker.wrapSelectorWheel = false

        // Show initial value in top-right corner
        tvSelectedDays.text = "$selectedDays Days"

        numberPicker.setOnValueChangedListener { _, _, newVal ->
            selectedDays = newVal
            tvSelectedDays.text = "$newVal Days"
        }

        // 🔹 Handle switch toggle
        switchUseAverage.setOnCheckedChangeListener { _, isChecked ->
            useAverage = isChecked
            if (isChecked) {
                // If average is ON → (Example: assign default from cycle_data later)

            }
        }

        // 🔹 Save button
        btnSave.setOnClickListener {
            savePeriodLength()
        }
    }

    private fun savePeriodLength() {
        val periodLength = numberPicker.value
        val request = PeriodLengthRequest(userId, periodLength)

        val api = RetrofitClient.instance
        api.updatePeriodLength(request).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@EditLengthActivity, "Saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditLengthActivity, "Failed to save", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                Toast.makeText(this@EditLengthActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

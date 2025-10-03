package com.saveetha.fempulse

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.saveetha.fempulse.response.ProfileResponse
import com.saveetha.fempulse.response.ProfileUpdateRequest
import com.saveetha.fempulse.response.UserIdRequest
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditprofileActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etAge: EditText
    private lateinit var tvEmail: TextView
    private lateinit var etCycleLength: EditText
    private lateinit var etCycleInterval: EditText
    private lateinit var btnSave: Button
    private lateinit var editSymbol: ImageView

    private var userId: Int = -1
    private var isEditMode = false  // Track if editing is enabled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        // Load saved user_id
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        // Initialize views
        etUsername = findViewById(R.id.editTextUsername)
        etAge = findViewById(R.id.editTextAge)
        tvEmail = findViewById(R.id.tv_email)
        etCycleLength = findViewById(R.id.editTextCycleLength)
        etCycleInterval = findViewById(R.id.editTextCycleInterval)
        btnSave = findViewById(R.id.btn_continue)
        editSymbol = findViewById(R.id.edit_symbol)

        // Disable fields initially (View Mode)
        setEditable(false)

        // Edit button click → toggle edit mode
        editSymbol.setOnClickListener {
            isEditMode = !isEditMode
            setEditable(isEditMode)
        }

        val btnBack: ImageView = findViewById(R.id.btnback)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }

        // Save changes button
        btnSave.setOnClickListener {
            if (userId != -1) {
                updateProfile()
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch profile on load
        loadProfile()
    }

    private fun setEditable(enable: Boolean) {
        etUsername.isEnabled = enable
        etAge.isEnabled = enable
        etCycleLength.isEnabled = enable
        etCycleInterval.isEnabled = enable
        btnSave.isVisible = enable
    }

    private fun loadProfile() {
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getProfile(UserIdRequest(userId))
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val profile = response.body()?.profile
                        profile?.let {
                            etUsername.setText(it.username)
                            etAge.setText(it.age?.toString() ?: "")
                            tvEmail.text = it.email
                            etCycleLength.setText(it.cycle_length?.toString() ?: "")
                            etCycleInterval.setText(it.cycle_duration?.toString() ?: "")
                        }
                    } else {
                        Toast.makeText(this@EditprofileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(this@EditprofileActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateProfile() {
        val request = ProfileUpdateRequest(
            user_id = userId,
            username = etUsername.text.toString().trim(),
            age = etAge.text.toString().toIntOrNull(),
            email = tvEmail.text.toString().trim(),
            cycle_length = etCycleLength.text.toString().toIntOrNull(),
            cycle_duration = etCycleInterval.text.toString().toIntOrNull()
        )
        RetrofitClient.instance.updateProfile(request).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@EditprofileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()

                    // Update UI with new profile data
                    response.body()?.profile?.let { profile ->
                        etUsername.setText(profile.username)
                        etAge.setText(profile.age.toString())
                        tvEmail.text = profile.email
                        etCycleLength.setText(profile.cycle_length?.toString() ?: "")
                        etCycleInterval.setText(profile.cycle_duration?.toString() ?: "")

                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("username", profile.username)
                            .apply()
                    }

                    // Back to view mode
                    isEditMode = false
                    setEditable(false)

                } else {
                    Toast.makeText(this@EditprofileActivity, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(this@EditprofileActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

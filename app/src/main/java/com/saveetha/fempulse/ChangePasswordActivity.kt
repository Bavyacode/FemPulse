package com.saveetha.fempulse
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.response.*
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var changePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password) // XML file

        newPassword = findViewById(R.id.newPassword)
        confirmPassword = findViewById(R.id.confirmPassword)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        // 🔹 Get saved user email from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        // 👇 Get email passed from OTP verification screen
        val email = intent.getStringExtra("email") ?: ""

        changePasswordButton.setOnClickListener {
            val newPass = newPassword.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Call API only if passwords match
            changePassword(email, newPass)
        }
    }

    private fun changePassword(email: String, newPass: String) {
        val request = ChangePasswordRequest(email, newPass)
        println("Sending request: $request")
        RetrofitClient.instance.changePassword(request)
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result?.status == "success") {
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                "Password changed successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            // ✅ Navigate to HomeActivity
                            val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                result?.message ?: "Update failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Request failed: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}

package com.saveetha.fempulse
import com.saveetha.fempulse.retrofit.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.saveetha.fempulse.response.*
import android.widget.EditText
import retrofit2.Call
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotpasswordActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var emailEditText: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var btnback:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)
        val email_error = findViewById<TextView>(R.id.email_error)
        emailEditText = findViewById(R.id.edit_email)
        sendOtpButton = findViewById(R.id.btn_code)
        btnback = findViewById(R.id.btn_back)

        apiService = RetrofitClient.instance // Ensure RetrofitClient is set up correctly

        sendOtpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                email_error.text = "Enter the email"
                email_error.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val request = ForgotPasswordRequest(email)

            apiService.sendOtp(request).enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        val otp = response.body()?.otp ?: ""
                        val intent = Intent(this@ForgotpasswordActivity, VerifyOtpActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("otp", otp)
                        startActivity(intent)
                    } else {
                        email_error.text = "Failed to send OTP"
                        email_error.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    Toast.makeText(this@ForgotpasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        btnback.setOnClickListener {
            val intent = Intent(this@ForgotpasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.saveetha.fempulse.response.SignupRequest
import com.saveetha.fempulse.response.SignupResponse
import com.saveetha.fempulse.response.EmailVerifyRequest
import com.saveetha.fempulse.response.EmailVerifyResponse
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var password: EditText
    private lateinit var eyeIcon: ImageView
    private var isPasswordVisible = false

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otpLayout: LinearLayout
    private lateinit var btnConfirmOtp: Button
    private lateinit var btnVerifyEmail: Button

    private var sentOtp: String = ""
    private var emailVerified = false
    private var verifiedEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val fullName = findViewById<EditText>(R.id.fullName)
        val email = findViewById<EditText>(R.id.email)
        password = findViewById(R.id.password)
        val age = findViewById<EditText>(R.id.age)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val loginLink = findViewById<TextView>(R.id.loginLink)
        eyeIcon = findViewById(R.id.eye_icon)
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail)

        // OTP UI
        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otpLayout = findViewById(R.id.otpLayout)
        btnConfirmOtp = findViewById(R.id.btnConfirmOtp)

        // Hide OTP UI initially
        otpLayout.visibility = View.GONE
        btnConfirmOtp.visibility = View.GONE

        // 👁 Password toggle
        eyeIcon.setOnClickListener {
            if (isPasswordVisible) {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                eyeIcon.setImageResource(R.drawable.eyeclose)
                isPasswordVisible = false
            } else {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                eyeIcon.setImageResource(R.drawable.eye)
                isPasswordVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    password.transformationMethod = PasswordTransformationMethod.getInstance()
                    eyeIcon.setImageResource(R.drawable.eyeclose)
                    isPasswordVisible = false
                }, 1000)
            }
            password.setSelection(password.text.length)
        }

        // OTP Auto-move
        moveFocus(otp1, otp2)
        moveFocus(otp2, otp3)
        moveFocus(otp3, otp4)
        moveBackspace(otp2, otp1)
        moveBackspace(otp3, otp2)
        moveBackspace(otp4, otp3)

        // Verify Email → Send OTP
        findViewById<Button>(R.id.btnVerifyEmail).setOnClickListener {
            val emailText = email.text.toString().trim()
            if (emailText.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            verifiedEmail = emailText
            val request = EmailVerifyRequest(emailText)
            RetrofitClient.instance.verifyEmail(request).enqueue(object : Callback<EmailVerifyResponse> {
                override fun onResponse(
                    call: Call<EmailVerifyResponse>,
                    response: Response<EmailVerifyResponse>
                ) {
                    if (response.isSuccessful) {
                        sentOtp = response.body()?.otp ?: ""
                        otpLayout.visibility = View.VISIBLE
                        btnConfirmOtp.visibility = View.VISIBLE
                        Toast.makeText(this@SignupActivity, "OTP sent to $emailText", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignupActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<EmailVerifyResponse>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Confirm OTP
        btnConfirmOtp.setOnClickListener {
            val enteredOtp = otp1.text.toString() + otp2.text.toString() +
                    otp3.text.toString() + otp4.text.toString()

            if (enteredOtp == sentOtp) {
                Toast.makeText(this, "Email Verified!", Toast.LENGTH_SHORT).show()
                emailVerified = true
                // Disable email field
                email.isEnabled = false

                // Hide OTP boxes
                otpLayout.visibility = View.GONE
                btnConfirmOtp.visibility = View.GONE

                // Change VERIFY button to VERIFIED (green)
                btnVerifyEmail.text = "VERIFIED"
                btnVerifyEmail.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))

                // Disable further clicks
                btnVerifyEmail.isEnabled = false
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }


        // Signup
        signupButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val ageText = age.text.toString().trim()

            if (name.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || ageText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!emailVerified) {
                Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = SignupRequest(
                username = name,
                email = emailText,
                password = passwordText,
                age = ageText.toInt()
            )

            RetrofitClient.instance.signup(request).enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    if (response.isSuccessful) {
                        val signupResponse = response.body()
                        if (signupResponse?.success == true) {
                            val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            prefs.edit().putBoolean("is_new_user", true).apply()

                            prefs.edit()
                                .putInt("user_id", signupResponse.user_id ?: -1)
                                .putString("username", name)
                                .apply()

                            val intent = Intent(this@SignupActivity, WelcomePageActivity::class.java)
                            intent.putExtra("username", name)
                            intent.putExtra("user_id", signupResponse.user_id)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignupActivity,
                                "Signup Failed: ${signupResponse?.message ?: "Unknown error"}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@SignupActivity, "Server error: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun moveFocus(current: EditText, next: EditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) next.requestFocus()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun moveBackspace(current: EditText, previous: EditText) {
        current.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (current.text.isEmpty()) previous.requestFocus()
            }
            false
        }
    }

    private fun disableOtpInputs() {
        otp1.isEnabled = false
        otp2.isEnabled = false
        otp3.isEnabled = false
        otp4.isEnabled = false
        btnConfirmOtp.isEnabled = false
    }
}

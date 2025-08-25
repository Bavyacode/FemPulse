package com.saveetha.fempulse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.response.SignupRequest
import com.saveetha.fempulse.response.SignupResponse
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var password: EditText
    private lateinit var eyeIcon: ImageView
    private var isPasswordVisible = false
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

        // 👁 Show password temporarily when eye icon is clicked


        eyeIcon.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                eyeIcon.setImageResource(R.drawable.eyeclose) // eye closed icon
                isPasswordVisible = false
            } else {
                // Show password
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                eyeIcon.setImageResource(R.drawable.eye) // eye open icon
                isPasswordVisible = true

                // Optional: Auto-hide after 3 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    password.transformationMethod = PasswordTransformationMethod.getInstance()
                    eyeIcon.setImageResource(R.drawable.eyeclose)
                    isPasswordVisible = false
                }, 1000)
            }

            // Keep cursor at the end
            password.setSelection(password.text.length)
        }


        signupButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val ageText = age.text.toString().trim()

            // Validate inputs
            if (name.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || ageText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
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
                            prefs.edit()
                                .putInt("user_id", signupResponse.user_id ?: -1) // ✅ store as Int
                                .putString("username", name)
                                .apply()

                            val intent = Intent(this@SignupActivity, WelcomePageActivity::class.java)
                            intent.putExtra("username", name)
                            intent.putExtra("user_id", signupResponse.user_id) // add this if you want to pass ID
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
                        Toast.makeText(
                            this@SignupActivity,
                            "Server error: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Toast.makeText(
                        this@SignupActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}

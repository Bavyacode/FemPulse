package com.saveetha.fempulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.response.LoginRequest
import com.saveetha.fempulse.response.LoginResponse
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val iconeye = findViewById<ImageView>(R.id.iconeye)
        val emailInput = findViewById<EditText>(R.id.Email)
        val passwordInput = findViewById<EditText>(R.id.Password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotpassword)
        val createAccount = findViewById<TextView>(R.id.createanaccount)
        val passwordError = findViewById<TextView>(R.id.passwordError)

        // Toggle password visibility
        iconeye.setOnClickListener {
            if (isPasswordVisible) {
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                iconeye.setImageResource(R.drawable.eyeclose)
                isPasswordVisible = false
            } else {
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                iconeye.setImageResource(R.drawable.eye)
                isPasswordVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                    iconeye.setImageResource(R.drawable.eyeclose)
                    isPasswordVisible = false
                }, 1000)
            }
            passwordInput.setSelection(passwordInput.text.length)
        }

        // Login button
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            passwordError.visibility = View.GONE

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Log.d("LoginActivity", "Raw login response: $loginResponse")

                        if (loginResponse?.status == "success") {
                            Log.d("LoginActivity", "user_id from API = ${loginResponse.user_id}")

                            // ✅ Save user_id
                            val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            sharedPref.edit()
                                .putInt("user_id", loginResponse.user_id)
                                .putString("email", loginResponse.email)
                                .putString("username", loginResponse.username)
                                .apply()
                            Log.d("LoginDebug", "Saving user_id = ${loginResponse.user_id}")
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                            // ✅ Go to home
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            passwordError.visibility = View.VISIBLE
                            passwordError.text = loginResponse?.message ?: "Invalid credentials"
                        }
                    } else {
                        passwordError.visibility = View.VISIBLE
                        passwordError.text = "Server error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // Forgot password
        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotpasswordActivity::class.java))
        }

        // Create account
        createAccount.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}

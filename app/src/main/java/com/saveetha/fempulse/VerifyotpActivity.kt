package com.saveetha.fempulse
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.saveetha.fempulse.response.*
import com.saveetha.fempulse.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvResend: TextView


    private lateinit var apiService: ApiService
    private var countDownTimer: CountDownTimer? = null
    private val resendWaitTime = 30000L // 30 seconds

    private var correctOtp: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifyotp)

        // Get intent values
        correctOtp = intent.getStringExtra("otp")
        email = intent.getStringExtra("email")
        val otpError = findViewById<TextView>(R.id.otpError)

        // Initialize views
        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        btnVerify = findViewById(R.id.btnverify)
        tvResend = findViewById(R.id.resend)

        // API service
        apiService = RetrofitClient.instance

        // Setup OTP box auto-focus
        moveToNextEditText(otp1, otp2)
        moveToNextEditText(otp2, otp3)
        moveToNextEditText(otp3, otp4)

        // Verify button click
        btnVerify.setOnClickListener {
            val enteredOtp = otp1.text.toString() + otp2.text.toString() +
                    otp3.text.toString() + otp4.text.toString()

            if (enteredOtp.length < 4) {
                otpError.text = "Please enter all 4 digits"
                otpError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (enteredOtp == correctOtp) {
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                otpError.text = "The code is incorrect"
                otpError.visibility = View.VISIBLE
            }
        }

        // Resend OTP click
        tvResend.setOnClickListener {
            if (!email.isNullOrEmpty()) {
                resendOtp(email!!)
                startResendCountdown()
            } else {
                otpError.text = "Entered Email Not Found"
                otpError.visibility = View.VISIBLE
            }
        }

        // Start initial countdown
        startResendCountdown()
    }

    // Auto move to next field
    private fun moveToNextEditText(current: EditText, next: EditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) next.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Resend OTP API
    private fun resendOtp(email: String) {
        val request = ForgotPasswordRequest(email)
        apiService.sendOtp(request).enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (response.isSuccessful) {
                    correctOtp = response.body()?.otp
                    Toast.makeText(this@VerifyOtpActivity, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VerifyOtpActivity, "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                Toast.makeText(this@VerifyOtpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Start resend countdown
    private fun startResendCountdown() {
        tvResend.isEnabled = false
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(resendWaitTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                tvResend.text = "Resend in ${secondsRemaining}s"
                tvResend.setTextColor(resources.getColor(android.R.color.darker_gray))
            }

            override fun onFinish() {
                tvResend.text = "Didn’t receive the code? Resend"
                tvResend.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                tvResend.isEnabled = true
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}

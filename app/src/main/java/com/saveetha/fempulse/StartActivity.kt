package com.saveetha.fempulse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
   
    private val splashDuration: Long = 2000 // 2 seconds

    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView
    private val dotHandler = Handler(Looper.getMainLooper())
    private var dotIndex = 0

    private val dotRunnable = object : Runnable {
        override fun run() {
            val dots = listOf(dot1, dot2, dot3)
            dots.forEachIndexed { index, textView ->
                textView.visibility = if (index == dotIndex % 3) View.VISIBLE else View.INVISIBLE
            }
            dotIndex++
            dotHandler.postDelayed(this, 400) // every 400ms
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        dotHandler.post(dotRunnable)

        Handler(Looper.getMainLooper()).postDelayed({
            dotHandler.removeCallbacks(dotRunnable)

            val intent=Intent(this, LoginsignupActivity::class.java)


            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()

        }, splashDuration)
    }
}
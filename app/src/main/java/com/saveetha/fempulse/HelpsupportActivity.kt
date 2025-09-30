package com.saveetha.fempulse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HelpsupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helpsupport)  // your layout XML file

        val emailButton: Button = findViewById(R.id.btnEmailUs) // make sure ID matches your XML

        emailButton.setOnClickListener {
            sendSupportEmail()
        }
        val feedback: Button = findViewById(R.id.submit_feedback)
        feedback.setOnClickListener{
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
        val chatbot : Button = findViewById(R.id.chatbot)
        chatbot.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java))
        }
        val back : ImageView = findViewById(R.id.backbutton)
        back.setOnClickListener {
                finish() // close activity, go back

        }

    }

    private fun sendSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // important: use "mailto:" only, not full address here
            putExtra(Intent.EXTRA_EMAIL, arrayOf("fempulse.health@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
        }

        try {
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }




}
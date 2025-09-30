package com.saveetha.fempulse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val editperiodlayyout = findViewById<LinearLayout>(R.id.edit_period_layout)
        editperiodlayyout.setOnClickListener {
            val intent = Intent(this, HomeeditperiodActivity::class.java)
            startActivity(intent)
        }

        val editprofile = findViewById<LinearLayout>(R.id.edit_profile)
        editprofile.setOnClickListener {
            val intent = Intent(this, EditprofileActivity::class.java)
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        val notificationhistory = findViewById<LinearLayout>(R.id.notification_history_layout)
        notificationhistory.setOnClickListener {
            val intent = Intent(this, NotificationhistoryActivity::class.java)

            startActivity(intent)
        }
        val editlengthlayout = findViewById<LinearLayout>(R.id.edit_length_layout)
        editlengthlayout.setOnClickListener {
            val intent = Intent(this, EditLengthActivity::class.java)
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        val editintervallayout = findViewById<LinearLayout>(R.id.edit_interval)
        editintervallayout.setOnClickListener {
        val intent = Intent(this, EditIntervalActivity::class.java)
        startActivity(intent)
    }
        val pcosSymptomsLayout = findViewById<LinearLayout>(R.id.pcos_symptoms_layout)
        pcosSymptomsLayout.setOnClickListener {
            val url = "https://www.nhs.uk/conditions/polycystic-ovary-syndrome-pcos/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val pcosguidelines = findViewById<LinearLayout>(R.id.Pcos_guidelines)
        pcosguidelines.setOnClickListener {
            val url = "https://www.who.int/news-room/fact-sheets/detail/polycystic-ovary-syndrome"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val myth = findViewById<LinearLayout>(R.id.myths)
        myth.setOnClickListener {
            val url = "https://www.metropolisindia.com/blog/health-wellness/busting-period-myths-science-about-periods"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val hygienetips = findViewById<LinearLayout>(R.id.hygiene)
        hygienetips.setOnClickListener {
            val url = "https://www.cdc.gov/hygiene/about/menstrual-hygiene.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val addsymptoms = findViewById<Button>(R.id.addsymp)
        addsymptoms.setOnClickListener {
            val intent = Intent(this, AddlogsActivity::class.java)
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }


        val helpsupport = findViewById<Button>(R.id.help_support)
        helpsupport.setOnClickListener {
            val intent = Intent(this,HelpsupportActivity ::class.java)
            startActivity(intent)
        }
    }
}
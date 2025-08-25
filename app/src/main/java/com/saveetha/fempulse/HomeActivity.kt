package com.saveetha.fempulse
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.content.Intent
import androidx.drawerlayout.widget.DrawerLayout
import com.saveetha.fempulse.response.MenstrualPhaseResponse
import com.saveetha.fempulse.response.UserIdRequest
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.GravityCompat


class HomeActivity : BaseActivity() {
    override fun getCurrentNavId(): Int = R.id.nav_home
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var iconMenu: ImageView
    private lateinit var currentPhaseText: TextView
    private lateinit var currentPhaseDaysRemaining: TextView
    private lateinit var currentPhaseDateRange: TextView
    private lateinit var nextPeriodDate: TextView
    private lateinit var nextPhaseText: TextView
    private lateinit var nextPhaseDate: TextView
    private lateinit var phaseMenstrualDate: TextView
    private lateinit var phaseFollicularDate: TextView
    private lateinit var phaseOvulationDate: TextView
    private lateinit var phaseLutealDate: TextView
    private lateinit var snapshotLastPeriodValue: TextView
    private lateinit var snapshotCycleLengthValue: TextView
    private lateinit var snapshotPeriodLengthValue: TextView
    private lateinit var snapshotPreviousPhaseValue: TextView
    private lateinit var tvUserGreeting: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home) // injected inside BaseActivity's container
        bindViews()
        setupDrawerMenu()

        fetchMenstrualPhasesFromPrefs()

    }

    private fun bindViews() {
        tvUserGreeting = findViewById(R.id.tv_user_greeting)
        drawerLayout = findViewById(R.id.drawer_layout)
        iconMenu = findViewById(R.id.iconMenu)
        currentPhaseText = findViewById(R.id.currentPhaseText)
        currentPhaseDaysRemaining = findViewById(R.id.currentPhaseDaysRemaining)
        currentPhaseDateRange = findViewById(R.id.currentPhaseDateRange)
        nextPeriodDate = findViewById(R.id.nextPeriodDate)
        nextPhaseText = findViewById(R.id.nextPhaseText)
        nextPhaseDate = findViewById(R.id.nextPhaseDate)
        phaseMenstrualDate = findViewById(R.id.phaseMenstrualDate)
        phaseFollicularDate = findViewById(R.id.phaseFollicularDate)
        phaseOvulationDate = findViewById(R.id.phaseOvulationDate)
        phaseLutealDate = findViewById(R.id.phaseLutealDate)
        snapshotLastPeriodValue = findViewById(R.id.snapshotLastPeriodValue)
        snapshotCycleLengthValue = findViewById(R.id.snapshotCycleLengthValue)
        snapshotPeriodLengthValue = findViewById(R.id.snapshotPeriodLengthValue)
        snapshotPreviousPhaseValue = findViewById(R.id.snapshotPreviousPhaseValue)
    }
    private fun setupDrawerMenu() {
        iconMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "User") ?: "User"

        // Find the TextView inside the hamburger menu
        val tvUserGreeting = findViewById<TextView>(R.id.tv_user_greeting)
        tvUserGreeting.text = "Hi, $username!"

        val settingsLayout = findViewById<LinearLayout>(R.id.settingslayout)
        val helpLayout = findViewById<LinearLayout>(R.id.helpsupportlayout)
        val logoutLayout = findViewById<LinearLayout>(R.id.logoutlayout)

        settingsLayout.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        helpLayout.setOnClickListener {
            startActivity(Intent(this, HelpsupportActivity::class.java))
        }

        logoutLayout.setOnClickListener {
            val intent = Intent(this, LoginsignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    private fun fetchMenstrualPhasesFromPrefs() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        val username = sharedPref.getString("username", "User") ?: "User"

        // Show username in UI
        findViewById<TextView>(R.id.tvUsername).text = "Hi, $username 👋"

        Log.d("HomeActivity", "user_id from prefs = $userId")

        if (userId == -1) {
            Toast.makeText(this, "User ID not found in preferences", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with API call
        fetchAndDisplayPhases(userId)
    }


    private fun fetchAndDisplayPhases(userId: Int) {
        val api = RetrofitClient.instance
        val request = UserIdRequest(userId = userId)

        // 1️⃣ Fetch manual phases first and display immediately
        api.getManualPhases(request).enqueue(object : Callback<MenstrualPhaseResponse> {
            override fun onResponse(call: Call<MenstrualPhaseResponse>, response: Response<MenstrualPhaseResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val manualData = response.body()!!
                    updateUI(manualData) // Show manual data immediately
                }

                // 2️⃣ After manual data is displayed, call API in background
                api.getApiPhases(request).enqueue(object : Callback<MenstrualPhaseResponse> {
                    override fun onResponse(call: Call<MenstrualPhaseResponse>, response: Response<MenstrualPhaseResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            val apiData = response.body()!!
                            updateUI(apiData) // Update UI with API response
                        } else {
                            Log.d("HomeActivity", "API returned empty or failed, keeping manual data")
                        }
                    }

                    override fun onFailure(call: Call<MenstrualPhaseResponse>, t: Throwable) {
                        Log.d("HomeActivity", "API call failed: ${t.message}, keeping manual data")
                    }
                })
            }

            override fun onFailure(call: Call<MenstrualPhaseResponse>, t: Throwable) {
                Log.d("HomeActivity", "Manual phase fetch failed: ${t.message}")
                // Optionally, still try API
                api.getApiPhases(request).enqueue(object : Callback<MenstrualPhaseResponse> {
                    override fun onResponse(call: Call<MenstrualPhaseResponse>, response: Response<MenstrualPhaseResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            val apiData = response.body()!!
                            updateUI(apiData)
                        }
                    }

                    override fun onFailure(call: Call<MenstrualPhaseResponse>, t: Throwable) {
                        Log.d("HomeActivity", "API also failed: ${t.message}")
                    }
                })
            }
        })
    }


    private fun updateUI(data: MenstrualPhaseResponse) {
        data.currentPhase?.let {
            currentPhaseText.text = "${it.emoji.orEmpty()} ${it.name.orEmpty()}"
            currentPhaseDaysRemaining.text = it.daysRemaining.orEmpty()
            currentPhaseDateRange.text = "${it.start.orEmpty()} - ${it.end.orEmpty()}"
        }

        nextPeriodDate.text = data.nextPeriod?.date.orEmpty()

        data.nextPhase?.let {
            nextPhaseText.text = "${it.emoji.orEmpty()} ${it.name.orEmpty()}"
            nextPhaseDate.text = "${it.start.orEmpty()} - ${it.end.orEmpty()}"
        }

        data.cyclePhaseOverview?.forEach { phase ->
            val text = "${phase.emoji.orEmpty()} \n ${phase.range.orEmpty()}"
            when (phase.name?.lowercase()) {
                "menstrual" -> phaseMenstrualDate.text = text
                "follicular" -> phaseFollicularDate.text = text
                "ovulatory" -> phaseOvulationDate.text = text
                "luteal" -> phaseLutealDate.text = text
            }
        }

        snapshotLastPeriodValue.text = data.snapshot?.lastPeriod.orEmpty()
        snapshotCycleLengthValue.text = data.snapshot?.cycleLength.orEmpty()
        snapshotPeriodLengthValue.text = data.snapshot?.periodLength.orEmpty()
        snapshotPreviousPhaseValue.text = data.snapshot?.previousPhase.orEmpty()
    }

}

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.adapter.RecentCycleAdapter
import com.saveetha.fempulse.response.*
import com.saveetha.fempulse.response.RecentHistoryResponse

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
    private lateinit var recyclerViewRecentHistory: RecyclerView
    private lateinit var recentHistoryAdapter: RecentCycleAdapter
    private lateinit var ratingBar: RatingBar
    private val prefsName = "RateUsPrefs"
    private val ratingKey = "user_rating"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val wellnessText = findViewById<TextView>(R.id.wellnessText)
        wellnessText.setOnClickListener{
            startActivity(Intent(this, WellnessActivity::class.java))
        }
        bindViews()
        setupDrawerMenu()
        fetchMenstrualPhasesFromPrefs()
    }
    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        if (userId != -1) {
            // Refetch latest cycle data to update UI after returning from edit
            fetchAndDisplayPhases(userId)
            fetchRecentCycleHistory(userId)
        }
    }
    private fun safeStringToInt(value: String?, default: Int): Int {
        if (value.isNullOrBlank()) return default
        // Remove non-digit characters
        val cleaned = value.filter { it.isDigit() }
        return cleaned.toIntOrNull() ?: default
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
        recyclerViewRecentHistory = findViewById(R.id.recyclerViewRecentHistory)
        recyclerViewRecentHistory.layoutManager = LinearLayoutManager(this)
    }



    private fun setupDrawerMenu() {
        iconMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "User") ?: "User"



        val tvUserGreeting = findViewById<TextView>(R.id.tv_user_greeting)
        tvUserGreeting.text = "Hi, $username!"

        val settingsLayout = findViewById<LinearLayout>(R.id.settingslayout)
        val helpLayout = findViewById<LinearLayout>(R.id.helpsupportlayout)
        val logoutLayout = findViewById<LinearLayout>(R.id.logoutlayout)
        val wellness = findViewById<LinearLayout>(R.id.wellnesslayout)
        val stats = findViewById<LinearLayout>(R.id.statslayout)
        val reminder = findViewById<ImageView>(R.id.iconBell)
        val editperiod = findViewById<Button>(R.id.home_edit_period)
        val tvMoreHistory = findViewById<TextView>(R.id.tvMoreHistory)

        tvMoreHistory.setOnClickListener {
            val intent = Intent(this@HomeActivity, MyCycleActivity::class.java)
            startActivity(intent)
        }
        editperiod.setOnClickListener {
            startActivity(Intent(this , HomeeditperiodActivity::class.java))
        }
        settingsLayout.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        ratingBar = findViewById(R.id.ratingBar)

        // Load saved rating
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val savedRating = prefs.getFloat(ratingKey, 0f)
        ratingBar.rating = savedRating

        // Handle rating changes
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                prefs.edit().putFloat(ratingKey, rating).apply()
                Toast.makeText(this, "You rated $rating stars", Toast.LENGTH_SHORT).show()
            }
        }

        reminder.setOnClickListener{
            startActivity(Intent(this,RemindersActivity::class.java))
        }
        stats.setOnClickListener{
            startActivity(Intent(this,InsightsActivity::class.java))
        }
        wellness.setOnClickListener {
            startActivity(Intent(this,WellnessActivity::class.java))
        }

        helpLayout.setOnClickListener {
            startActivity(Intent(this, HelpsupportActivity::class.java))
        }

        logoutLayout.setOnClickListener {
            val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            prefs.edit().clear().apply() // clears user_id and any other stored data

            val intent = Intent(this, LoginsignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun fetchMenstrualPhasesFromPrefs() {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        val username = sharedPref.getString("username", "User") ?: "User"

        findViewById<TextView>(R.id.tvUsername).text = "Hi, $username 👋"

        Log.d("HomeActivity", "user_id from prefs = $userId")

        if (userId == -1) {
            Toast.makeText(this, "User ID not found in preferences", Toast.LENGTH_SHORT).show()
            return
        }

        fetchRecentCycleHistory(userId)

    }

    private fun fetchRecentCycleHistory(userId: Int) {
        val body = mapOf("user_id" to userId)

        RetrofitClient.instance.getRecentHistory(UserIdRequest(userId))
            .enqueue(object : Callback<RecentHistoryResponse> {
                override fun onResponse(
                    call: Call<RecentHistoryResponse>,
                    response: Response<RecentHistoryResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val historyList = response.body()?.history ?: emptyList()

                        recyclerViewRecentHistory.adapter = RecentCycleAdapter(historyList)
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Failed to load history",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RecentHistoryResponse>, t: Throwable) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Network error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


        private fun fetchAndDisplayPhases(userId: Int) {
        val api = RetrofitClient.instance
        val request = UserIdRequest(userId = userId)

        // ✅ Only fetch manual phases
        api.getManualPhases(request).enqueue(object : Callback<MenstrualPhaseResponse> {
            override fun onResponse(
                call: Call<MenstrualPhaseResponse>,
                response: Response<MenstrualPhaseResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    updateUI(response.body()!!)
                } else {
                    Toast.makeText(this@HomeActivity, "No phase data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MenstrualPhaseResponse>, t: Throwable) {
                Log.d("HomeActivity", "Manual phase fetch failed: ${t.message}")
                Toast.makeText(this@HomeActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
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
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPref.edit()
            .putInt(
                "average_cycle_length",
                safeStringToInt(data.snapshot?.cycleLength, 28)
            )
            .putInt(
                "average_period_length",
                safeStringToInt(data.snapshot?.periodLength, 5)
            )
            .apply()

    }

}

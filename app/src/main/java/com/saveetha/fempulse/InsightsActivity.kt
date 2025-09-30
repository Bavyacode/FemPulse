package com.saveetha.fempulse

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.saveetha.fempulse.BaseActivity
import com.saveetha.fempulse.R
import com.saveetha.fempulse.retrofit.RetrofitClient
import kotlinx.coroutines.*
import com.saveetha.fempulse.response.*
import retrofit2.HttpException

class InsightsActivity : BaseActivity() {
    override fun getCurrentNavId(): Int = R.id.nav_stats
    private lateinit var stateNewUser: View
    private lateinit var stateInsufficient: View
    private lateinit var stateInsights: View
    private lateinit var btnRefresh: Button

    private lateinit var lineCycle: LineChart
    private lateinit var lineDuration: LineChart
    private lateinit var barTop: BarChart
    private lateinit var pieCat: PieChart
    private lateinit var tvFacts: TextView
    private lateinit var tvCyclePlaceholder: TextView
    private lateinit var tvDurationPlaceholder: TextView
    private lateinit var tvTopPlaceholder: TextView
    private lateinit var tvCatPlaceholder: TextView


    private val scope = MainScope()

    private var userId: Int = -1
    private val TAG = "InsightsActivity"

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        // ✅ Get user_id from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)
        Log.d(TAG, "user_id from prefs = $userId")

        if (userId == -1) {
            Toast.makeText(this, "User ID not found in preferences", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        stateNewUser = findViewById(R.id.stateNewUser)
        stateInsufficient = findViewById(R.id.stateInsufficient)
        stateInsights = findViewById(R.id.stateInsights)
        btnRefresh = findViewById(R.id.btnRefresh)

        lineCycle = findViewById(R.id.lineCycleLengths)
        lineDuration = findViewById(R.id.lineDurations)
        barTop = findViewById(R.id.barTopSymptoms)
        pieCat = findViewById(R.id.pieCategory)
        tvFacts = findViewById(R.id.tvFacts)
        tvCyclePlaceholder = findViewById(R.id.tvCyclePlaceholder)
        tvDurationPlaceholder = findViewById(R.id.tvDurationPlaceholder)
        tvTopPlaceholder = findViewById(R.id.tvTopPlaceholder)
        tvCatPlaceholder = findViewById(R.id.tvCatPlaceholder)


        findViewById<Button>(R.id.btnStartLogging)?.setOnClickListener {
            // Navigate to your logging screen
            Toast.makeText(this, "Go to logging screen", Toast.LENGTH_SHORT).show()
        }
        Log.d("Insights", "Fetching phases for insights...")
        btnRefresh.setOnClickListener { loadData() }

        loadData()
    }
    private fun showNewUser() {
        stateNewUser.visibility = View.VISIBLE
        stateInsufficient.visibility = View.GONE
        stateInsights.visibility = View.GONE
    }

    private fun showInsufficient(msg: String) {
        stateNewUser.visibility = View.GONE
        stateInsufficient.visibility = View.VISIBLE
        stateInsights.visibility = View.GONE
        findViewById<TextView>(R.id.tvInsufficientMsg).text = msg
    }

    private fun showInsights() {
        stateNewUser.visibility = View.GONE
        stateInsufficient.visibility = View.GONE
        stateInsights.visibility = View.VISIBLE
    }

    private fun loadData() {
        scope.launch {
            try {
                val body = mapOf("user_id" to userId)
                val insights = RetrofitClient.instance.getInsights(body)
                val top = RetrofitClient.instance.getTopSymptoms(body)
                val cat = RetrofitClient.instance.getSymptomsByCategory(body)

                val cycles = insights.cycle_length_trends
                val durations = insights.period_duration_trends

                val hasCycleData = cycles.size >= 2
                val hasDurationData = durations.size >= 2
                val hasTop = top.top_symptoms.isNotEmpty()
                val hasCat = cat.symptoms_by_category.isNotEmpty()

                // 🔴 If nothing has data at all → show insufficient/new user state
                if (!hasCycleData && !hasDurationData && !hasTop && !hasCat) {
                    if (cycles.size <= 1 && durations.size <= 1) {
                        showNewUser()
                    } else {
                        showInsufficient("Not enough data yet. Keep logging!")
                    }
                    return@launch
                }

                // ✅ Otherwise, show insights section and fill graphs selectively
                showInsights()

                // ---- Cycle Lengths ----
                if (hasCycleData) {
                    renderLine(lineCycle, cycles, "Cycle Lengths")
                    lineCycle.visibility = View.VISIBLE
                    tvCyclePlaceholder.visibility = View.GONE
                } else {
                    lineCycle.visibility = View.GONE
                    tvCyclePlaceholder.visibility = View.VISIBLE
                }

                // ---- Period Durations ----
                if (hasDurationData) {
                    renderLine(lineDuration, durations, "Period Durations")
                    lineDuration.visibility = View.VISIBLE
                    tvDurationPlaceholder.visibility = View.GONE
                } else {
                    lineDuration.visibility = View.GONE
                    tvDurationPlaceholder.visibility = View.VISIBLE
                }

                // ---- Top Symptoms ----
                if (hasTop) {
                    renderBar(barTop, top.top_symptoms)
                    barTop.visibility = View.VISIBLE
                    tvTopPlaceholder.visibility = View.GONE
                } else {
                    barTop.visibility = View.GONE
                    tvTopPlaceholder.visibility = View.VISIBLE
                }

                // ---- Symptoms by Category ----
                if (hasCat) {
                    renderPie(pieCat, cat.symptoms_by_category)
                    pieCat.visibility = View.VISIBLE
                    tvCatPlaceholder.visibility = View.GONE
                } else {
                    pieCat.visibility = View.GONE
                    tvCatPlaceholder.visibility = View.VISIBLE
                }

                // ---- Facts ----


            } catch (e: HttpException) {
                showInsufficient("Server error: ${e.code()}")
            } catch (e: Exception) {
                showInsufficient("Something went wrong: ${e.message}")
            }
        }
    }


    private fun renderLine(chart: LineChart, values: List<Int>, label: String) {

        val entries = values.mapIndexed { idx, v -> Entry(idx.toFloat(), v.toFloat()) }
        val set = LineDataSet(entries, label).apply {
            setDrawCircles(true)

            circleRadius = 3f
            lineWidth = 2f
            valueTextSize = 10f
        }
        chart.data = LineData(set)
        chart.description = Description().apply { text = "" }
        chart.axisRight.isEnabled = false
        chart.xAxis.granularity = 1f
        chart.invalidate()
    }

    private fun renderBar(chart: BarChart, items: List<TopSymptomItem>) {
        val entries = items.mapIndexed { idx, it -> BarEntry(idx.toFloat(), it.frequency.toFloat()) }
        val set = BarDataSet(entries, "Top Symptoms")
        set.valueTextColor = getColor(R.color.my_primary)

        // ✅ Use app primary color
        val primaryColor = getColor(R.color.my_secondary) // make sure colorPrimary is defined in colors.xml
        set.color = primaryColor
        set.valueTextSize = 12f

        chart.data = BarData(set).apply { barWidth = 0.5f }

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(items.map { it.name })
        chart.xAxis.granularity = 1f
        chart.xAxis.labelCount = items.size
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawAxisLine(false)
        chart.axisRight.setDrawAxisLine(false)
        chart.xAxis.setDrawAxisLine(false)
        chart.axisRight.isEnabled = false
        chart.description = Description().apply { text = "" }
        chart.invalidate()
    }

    private fun renderPie(chart: PieChart, items: List<CategoryItem>) {
        // Define categories and their colors
        val categoryColors = mapOf(
            "physical" to getColor(R.color.physicalColor),
            "mood" to getColor(R.color.moodColor),
            "behavioral" to getColor(R.color.behavioralColor),
            "behaviour" to getColor(R.color.behavioralColor) // support British spelling
        )

        val categories = listOf("Physical", "Mood", "Behavioral") // order in pie chart
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        for (cat in categories) {
            // Find item in list matching category (ignore case, support behaviour)
            val item = items.find {
                it.category.equals(cat, ignoreCase = true) || it.category.equals("Behaviour", ignoreCase = true)
            }
            // Use 0.01f if no data so slice appears
            val value = item?.total?.toFloat()?.takeIf { it > 0 } ?: 0.01f
            entries.add(PieEntry(value, cat))

            // Assign color based on category
            val color = categoryColors[cat.lowercase()] ?: Color.GRAY
            colors.add(color)
        }

        val set = PieDataSet(entries, "Symptoms").apply {
            this.colors = colors
            valueTextColor = getColor(R.color.my_primary)
            valueTextSize = 12f
        }

        chart.data = PieData(set)
        chart.description = Description().apply { text = "" }
        chart.setUsePercentValues(true)
        chart.setDrawEntryLabels(false)

        // Legend styling
        chart.legend.apply {
            isEnabled = true
            textColor = Color.BLACK
            textSize = 14f
            formSize = 14f
        }

        chart.invalidate()
    }

}

package com.saveetha.fempulse

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import java.time.DayOfWeek
import java.time.LocalDate
import com.saveetha.fempulse.response.*
import com.saveetha.fempulse.retrofit.*
import java.time.YearMonth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.TextStyle
import java.util.*

class HomeeditperiodActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var btnSave: Button

    // 🔹 Store selections month-wise
    private val monthSelections = mutableMapOf<YearMonth, MutableSet<LocalDate>>()
    private val periodLength = 5 // Example, can fetch from settings or DB

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeeditperiod)

        // ✅ Get user_id from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        calendarView = findViewById(R.id.calendarView)
        btnSave = findViewById(R.id.btnSave)

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)

        calendarView.setup(startMonth, endMonth, DayOfWeek.SUNDAY)
        calendarView.scrollToMonth(currentMonth)

        // Day binder
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: android.view.View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    val ym = data.date.yearMonth
                    val selectedDates = monthSelections[ym] ?: mutableSetOf()
                    val today = LocalDate.now()
                    val maxSelectableDate = today.plusDays(periodLength.toLong() - 1)

                    // ✅ Allow all past dates, today, and limited future dates
                    val isSelectable = !data.date.isAfter(maxSelectableDate)

                    // Reset style
                    container.textView.background = null
                    container.textView.setTextColor(getColor(R.color.black))

                    // Apply styles
                    when {
                        selectedDates.contains(data.date) -> {
                            container.textView.setBackgroundResource(R.drawable.bg_selected_day)
                            container.textView.setTextColor(getColor(R.color.white))
                        }
                        !isSelectable -> {
                            container.textView.setBackgroundResource(R.drawable.bg_disabled_day)
                            container.textView.setTextColor(getColor(R.color.dark_grey))
                        }
                        data.date == today -> {
                            container.textView.setBackgroundResource(R.drawable.bg_today)
                            container.textView.setTextColor(getColor(R.color.black))
                        }
                    }

                    if (isSelectable) {
                        container.textView.setOnClickListener {
                            if (selectedDates.isEmpty()) {
                                // First tap → auto-select full period across months
                                for (i in 0 until periodLength) {
                                    val d = data.date.plusDays(i.toLong())
                                    if (!d.isAfter(maxSelectableDate)) {
                                        val monthKey = d.yearMonth
                                        val set = monthSelections.getOrPut(monthKey) { mutableSetOf() }
                                        set.add(d)
                                        monthSelections[monthKey] = set
                                    }
                                }
                            } else {
                                // Toggle single date
                                val monthKey = data.date.yearMonth
                                if (selectedDates.contains(data.date)) {
                                    selectedDates.remove(data.date)
                                } else if (!data.date.isAfter(maxSelectableDate)) {
                                    selectedDates.add(data.date)
                                }
                                monthSelections[monthKey] = selectedDates
                            }

                            // Refresh all affected months
                            val affectedMonths = (0 until periodLength)
                                .map { data.date.plusDays(it.toLong()).yearMonth }
                                .toSet()
                            affectedMonths.forEach { calendarView.notifyMonthChanged(it) }
                        }
                    } else {
                        container.textView.setOnClickListener(null)
                    }
                } else {
                    container.textView.isEnabled = false
                }
            }
        }

        // Month header binder
        calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: android.view.View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    val title =
                        "${data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${data.yearMonth.year}"
                    container.textView.text = title
                }
            }

        // ✅ Load previously saved cycles from API
        RetrofitClient.instance.getCycles(GetCyclesRequest(user_id = userId))
            .enqueue(object : Callback<GetCyclesResponse> {
                override fun onResponse(call: Call<GetCyclesResponse>, response: Response<GetCyclesResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val savedDates = response.body()!!.dates.map { LocalDate.parse(it) }
                        savedDates.forEach { date ->
                            val monthKey = date.yearMonth
                            val set = monthSelections.getOrPut(monthKey) { mutableSetOf() }
                            set.add(date)
                            monthSelections[monthKey] = set
                        }
                        calendarView.notifyCalendarChanged()
                    }
                }

                override fun onFailure(call: Call<GetCyclesResponse>, t: Throwable) {
                    Toast.makeText(this@HomeeditperiodActivity, "Failed to load saved cycles", Toast.LENGTH_SHORT).show()
                }
            })
        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // close activity, go back
        }
        btnSave.setOnClickListener {
            val allDates = monthSelections.values.flatten().sorted().distinct()

            if (allDates.isEmpty()) {
                Toast.makeText(this, "Please select dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cycles = mutableListOf<Pair<LocalDate, LocalDate>>()
            var rangeStart = allDates.first()
            var prev = allDates.first()

            for (i in 1 until allDates.size) {
                val current = allDates[i]
                if (prev.plusDays(1) != current) {
                    cycles.add(Pair(rangeStart, prev))
                    rangeStart = current
                }
                prev = current
            }
            cycles.add(Pair(rangeStart, prev)) // last cycle

            // Save cycles to backend
            for (cycle in cycles) {
                val cycleDates = mutableListOf<String>()
                var d = cycle.first
                while (!d.isAfter(cycle.second)) {
                    cycleDates.add(d.toString())
                    d = d.plusDays(1)
                }

                val request = SaveCycleRequest(
                    user_id = userId,
                    dates = cycleDates
                )

                RetrofitClient.instance.saveCycle(request).enqueue(object : Callback<CycleResponse> {
                    override fun onResponse(call: Call<CycleResponse>, response: Response<CycleResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@HomeeditperiodActivity,
                                "Saved cycle: ${cycle.first} → ${cycle.second}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CycleResponse>, t: Throwable) {
                        Toast.makeText(this@HomeeditperiodActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    class DayViewContainer(view: android.view.View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
    }

    class MonthViewContainer(view: android.view.View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.monthHeaderText)
    }
}

package com.saveetha.fempulse

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.saveetha.fempulse.decorators.EmojiDayDecorator
import com.saveetha.fempulse.decorators.*
import com.saveetha.fempulse.response.CyclePhaseOverview
import com.saveetha.fempulse.response.MenstrualPhaseResponse
import com.saveetha.fempulse.response.UserIdRequest
import com.saveetha.fempulse.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarActivity : BaseActivity() {
    override fun getCurrentNavId(): Int = R.id.nav_calendar
    private lateinit var calendarView: MaterialCalendarView
    private val TAG = "CalendarActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendarView = findViewById(R.id.calendarView)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        Log.d(TAG, "user_id from prefs = $userId")
        if (userId == -1) {
            Toast.makeText(this, "User ID not found in preferences", Toast.LENGTH_SHORT).show()
            return
        }

        fetchPhases(userId)
    }

    private fun fetchPhases(userId: Int) {
        val api = RetrofitClient.instance
        api.getManualPhases(UserIdRequest(userId))
            .enqueue(object : Callback<MenstrualPhaseResponse> {
                override fun onResponse(
                    call: Call<MenstrualPhaseResponse>,
                    response: Response<MenstrualPhaseResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e(TAG, "API error: ${response.code()}")
                        return
                    }
                    val body = response.body() ?: return
                    val overview = body.cyclePhaseOverview ?: emptyList()
                    decorateFromOverview(overview)

                }

                override fun onFailure(call: Call<MenstrualPhaseResponse>, t: Throwable) {
                    Log.e(TAG, "API failure: ${t.localizedMessage}", t)
                }
            })
    }

    private fun decorateFromOverview(phases: List<CyclePhaseOverview>) {
        // Clear previous decorators if any (when revisiting)
        calendarView.removeDecorators()

        for (phase in phases) {
            val days = parseRangeToDays(phase.range ?: "") // list of CalendarDay

            if (days.isEmpty()) continue

            // Background drawable by phase name (colors can be tuned in drawables)
            val bgRes = when (phase.name?.lowercase(Locale.ENGLISH)) {
                "menstrual"  -> R.drawable.bg_menstrual
                "follicular" -> R.drawable.bg_follicular
                "ovulatory"  -> R.drawable.bg_ovulation
                "luteal"     -> R.drawable.bg_luteal
                else         -> R.drawable.bg_default
            }
            calendarView.addDecorator(PhaseDecorator(this, days, bgRes))

            // Emoji overlay – take from API if present, otherwise map by name
            val emoji = when {
                !phase.emoji.isNullOrBlank() -> phase.emoji!!
                phase.name.equals("menstrual", true)  -> "🩸"
                phase.name.equals("follicular", true) -> "🌱"
                phase.name.equals("ovulatory", true)  -> "💧"
                phase.name.equals("luteal", true)     -> "🌙"
                else -> ""
            }
            if (emoji.isNotEmpty()) {
                calendarView.addDecorator(EmojiDayDecorator(days, emoji))
            }
        }
    }

    /**
     * Parses "Aug 7 – Aug 12" (note: en dash) or "Aug 7 - Aug 12" into a list of CalendarDay,
     * assigning the current year and handling month wrap (e.g., Dec -> Jan).
     */
    private fun parseRangeToDays(range: String): List<CalendarDay> {
        if (range.isBlank()) return emptyList()

        // Split by EN DASH (– U+2013) first; fall back to hyphen
        val parts = when {
            range.contains("–") -> range.split("–")
            range.contains("-") -> range.split("-")
            else -> return emptyList()
        }
        if (parts.size != 2) return emptyList()

        val startStr = parts[0].trim()
        val endStr   = parts[1].trim()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val fmtNoYear   = SimpleDateFormat("MMM d", Locale.ENGLISH)
        val fmtWithYear = SimpleDateFormat("MMM d yyyy", Locale.ENGLISH)

        try {
            // Parse months/days first (no year)
            val startNoYear: Date = fmtNoYear.parse(startStr) ?: return emptyList()
            val endNoYear: Date   = fmtNoYear.parse(endStr)   ?: return emptyList()

            // Put into calendars so we can set year & iterate
            val calStart = Calendar.getInstance()
            val calEnd   = Calendar.getInstance()
            calStart.time = startNoYear
            calEnd.time   = endNoYear

            // Decide years: if end month is before start month, we rolled over to next year
            val startMonth = calStart.get(Calendar.MONTH)
            val endMonth   = calEnd.get(Calendar.MONTH)

            calStart.set(Calendar.YEAR, currentYear)
            calEnd.set(Calendar.YEAR, if (endMonth < startMonth) currentYear + 1 else currentYear)

            // Normalize time
            setToStartOfDay(calStart)
            setToStartOfDay(calEnd)

            // Now iterate inclusive
            val out = mutableListOf<CalendarDay>()
            val walker = calStart.clone() as Calendar
            while (!walker.after(calEnd)) {
                out.add(CalendarDay.from(walker))
                walker.add(Calendar.DAY_OF_MONTH, 1)
            }
            return out
        } catch (e: ParseException) {
            Log.e(TAG, "Failed to parse range: '$range' -> ${e.message}")
        }
        return emptyList()
    }

    private fun setToStartOfDay(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
}

package com.saveetha.fempulse.decorators

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class PhaseDecorator(
    context: Context,
    private val dates: List<CalendarDay>,
    private val bgRes: Int
) : DayViewDecorator {

    private val drawable: Drawable? = ContextCompat.getDrawable(context, bgRes)

    override fun shouldDecorate(day: CalendarDay): Boolean = dates.contains(day)

    override fun decorate(view: DayViewFacade) {
        drawable?.let { view.setBackgroundDrawable(it) }
    }
}

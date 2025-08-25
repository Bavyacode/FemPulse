package com.saveetha.fempulse.decorators

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EmojiDayDecorator(
    private val dates: List<CalendarDay>,
    private val emoji: String
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean = dates.contains(day)

    override fun decorate(view: DayViewFacade) {
        view.addSpan(EmojiSpan(emoji))
    }

    private class EmojiSpan(private val emoji: String) : LineBackgroundSpan {
        override fun drawBackground(
            c: Canvas,
            p: Paint,
            left: Int,
            right: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            charSequence: CharSequence,
            start: Int,
            end: Int,
            lnum: Int
        ) {
            val oldColor = p.color
            p.textAlign = Paint.Align.CENTER
            p.textSize = p.textSize * 0.9f

            // Draw emoji slightly below the day number
            c.drawText(
                emoji,
                (left + right) / 2f,
                bottom + 20f, // adjust offset
                p
            )
            p.color = oldColor
        }
    }
}

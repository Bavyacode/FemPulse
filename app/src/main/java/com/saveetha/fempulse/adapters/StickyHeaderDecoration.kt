package com.saveetha.fempulse.adapters

import FullHistoryAdapter
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderDecoration(private val adapter: FullHistoryAdapter) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = 0xFFE0E0E0.toInt() // background
        textSize = 48f
        isAntiAlias = true
        isFakeBoldText = true
    }

    private val textPaint = Paint().apply {
        color = 0xFF000000.toInt()
        textSize = 48f
        isAntiAlias = true
        isFakeBoldText = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val firstVisiblePos = (parent.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)
            ?.findFirstVisibleItemPosition() ?: return

        val year = adapter.getYearForPosition(firstVisiblePos) ?: return
        val child = parent.findViewHolderForAdapterPosition(firstVisiblePos)?.itemView ?: return

        val top = 0f
        val height = 80f

        c.drawRect(0f, top, parent.width.toFloat(), height, paint)
        c.drawText(year, 32f, height - 20f, textPaint)
    }
}

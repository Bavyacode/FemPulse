package com.saveetha.fempulse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.R
import com.saveetha.fempulse.response.CycleHistoryItem

class RecentCycleAdapter(
    private val historyList: List<CycleHistoryItem>
) : RecyclerView.Adapter<RecentCycleAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        holder.tvDuration.text = item.duration.toString()
        holder.tvDateRange.text = "${item.start_date} - ${item.end_date}"
        holder.tvIntervalDays.text = "Interval: ${item.interval_days} days"
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvDateRange: TextView = itemView.findViewById(R.id.tvDateRange)
        val tvIntervalDays: TextView = itemView.findViewById(R.id.tvIntervalDays)
        val iconClock: ImageView = itemView.findViewById(R.id.iconClock) // Optional if needed
    }
}

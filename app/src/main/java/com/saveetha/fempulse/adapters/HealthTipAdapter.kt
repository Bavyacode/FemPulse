package com.saveetha.fempulse.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.R
import com.saveetha.fempulse.response.*

class HealthTipAdapter : RecyclerView.Adapter<HealthTipAdapter.ViewHolder>() {

    private var tipsList = listOf<HealthTip>()

    fun setTips(list: List<HealthTip>) {
        tipsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_tip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tip = tipsList[position]

        // Show emoji before the text
        holder.tvTip.text = "${tip.emoji} ${tip.tip.replace(tip.emoji, "").trim()}"

        holder.itemView.setBackgroundResource(R.drawable.bg_tip)
    }


    override fun getItemCount(): Int = tipsList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvTip: TextView = view.findViewById(R.id.tvTip)
    }
}

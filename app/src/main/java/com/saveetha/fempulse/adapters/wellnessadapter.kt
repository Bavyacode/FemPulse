package com.saveetha.fempulse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WellnessTipAdapter(
    private val tips: List<String>
) : RecyclerView.Adapter<WellnessTipAdapter.TipViewHolder>() {

    inner class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipText: TextView = itemView.findViewById(R.id.tvTip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = tips[position]

        // ✅ Add emoji before the tip text
        holder.tipText.text = "💗 $tip"

        // ✅ Set background for each card
        holder.itemView.setBackgroundResource(R.drawable.bg_tip)
    }


    override fun getItemCount(): Int = tips.size
}

package com.saveetha.fempulse.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.emoji2.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.R
import com.saveetha.fempulse.response.Symptom


class SymptomAdapter(
    private val symptomsList: List<Symptom>,
    private val onSelectionChanged: (List<Symptom>) -> Unit
) : RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder>() {

    private val selectedSymptoms = mutableSetOf<Symptom>()

    inner class SymptomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emoji: EmojiTextView = itemView.findViewById(R.id.emoji)
        val tvSymptomName: TextView = itemView.findViewById(R.id.tvSymptomName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_symptom, parent, false)
        return SymptomViewHolder(view)
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        val symptom = symptomsList[position]

        holder.emoji.text = symptom.emoji
        holder.tvSymptomName.text = symptom.name

        // Highlight if selected
        holder.itemView.isSelected = selectedSymptoms.contains(symptom)
        holder.itemView.setBackgroundResource(
            if (selectedSymptoms.contains(symptom)) R.drawable.bg_symptom_selected
            else R.drawable.bg_symptom_unselected
        )

        // Toggle selection
        holder.itemView.setOnClickListener {
            if (selectedSymptoms.contains(symptom)) {
                selectedSymptoms.remove(symptom)
            } else {
                selectedSymptoms.add(symptom)
            }
            notifyItemChanged(position)
            onSelectionChanged(selectedSymptoms.toList())
        }
    }

    override fun getItemCount(): Int = symptomsList.size

    fun getSelectedSymptoms(): List<Symptom> = selectedSymptoms.toList()
}

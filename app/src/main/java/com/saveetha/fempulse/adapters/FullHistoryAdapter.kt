import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.R
import com.saveetha.fempulse.response.FullHistoryListItem
import com.saveetha.fempulse.response.HistoryItem

class FullHistoryAdapter(val items: List<FullHistoryListItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_YEAR = 0
        private const val VIEW_TYPE_HISTORY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is FullHistoryListItem.YearHeader -> VIEW_TYPE_YEAR
            is FullHistoryListItem.History -> VIEW_TYPE_HISTORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_YEAR) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_year_header, parent, false)
            YearViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history, parent, false)
            HistoryViewHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is FullHistoryListItem.YearHeader -> (holder as YearViewHolder).bind(item)
            is FullHistoryListItem.History -> (holder as HistoryViewHolder).bind(item.item)
        }
    }

    fun getYearForPosition(position: Int): String? {
        for (i in position downTo 0) {
            val item = items[i]
            if (item is FullHistoryListItem.YearHeader) return item.year
        }
        return null
    }

    inner class YearViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvYear = view.findViewById<TextView>(R.id.tvYearHeader)
        fun bind(item: FullHistoryListItem.YearHeader) {
            tvYear.text = item.year
        }
    }

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        private val tvDateRange: TextView = view.findViewById(R.id.tvDateRange)
        private val tvIntervalDays: TextView = view.findViewById(R.id.tvIntervalDays)

        fun bind(item: HistoryItem) {
            tvDuration.text = item.duration.toString()
            tvDateRange.text = "${item.start_date} - ${item.end_date}"
            tvIntervalDays.text = "Interval: ${item.interval_days} days"
        }
    }
}

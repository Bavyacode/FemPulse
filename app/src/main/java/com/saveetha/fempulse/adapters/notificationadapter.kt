import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationAdapter(private val notifications: List<NotificationItem>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = notifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notifications[position]
        holder.tvTitle.text = item.title
        holder.tvMessage.text = item.message
        holder.tvTime.text = SimpleDateFormat("hh:mm a, dd/MM/yyyy", Locale.getDefault())
            .format(Date(item.timestamp))
    }
}

package com.saveetha.fempulse.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.R
import com.saveetha.fempulse.response.ChatMessage

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val onSampleClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_USER = 1
    private val TYPE_BOT = 2
    private val TYPE_SAMPLE = 3
    private val TYPE_TYPING = 4 // typing dots

    override fun getItemViewType(position: Int): Int {
        val msg = messages[position]
        return when {
            msg.isLoading -> TYPE_TYPING
            msg.isSample -> TYPE_SAMPLE
            msg.isUser -> TYPE_USER
            else -> TYPE_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> {
                val view = inflater.inflate(R.layout.item_message_user, parent, false)
                UserViewHolder(view)
            }
            TYPE_BOT -> {
                val view = inflater.inflate(R.layout.item_message_bot, parent, false)
                BotViewHolder(view)
            }
            TYPE_SAMPLE -> {
                val view = inflater.inflate(R.layout.item_message_sample, parent, false)
                SampleViewHolder(view, onSampleClick)
            }
            else -> { // TYPE_TYPING
                val view = inflater.inflate(R.layout.item_typing, parent, false)
                TypingViewHolder(view)
            }
        }
    }
    private fun cleanText(text: String): String {
        return text
            .replace("**", "")   // remove bold markers
            // remove italics/bullet markers
            .replace("#", "")    // remove headings
            .trim()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        when (holder) {
            is UserViewHolder -> holder.messageText.text = msg.text
            is BotViewHolder -> holder.bind(cleanText(msg.text))
            // ✅ use bind()
            is SampleViewHolder -> holder.bind(msg)
            is TypingViewHolder -> holder.startAnimation()
        }
    }


    override fun getItemCount(): Int = messages.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
    }
    class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)

        fun bind(text: String) {
            // Remove all ** from the response
            val cleaned = text.replace("**", "")
            messageText.text = cleaned
        }
    }


    class SampleViewHolder(view: View, private val onSampleClick: ((String) -> Unit)?) :
        RecyclerView.ViewHolder(view) {
        private val sampleText: TextView = view.findViewById(R.id.sampleMessageText)

        fun bind(message: ChatMessage) {
            sampleText.text = message.text
            sampleText.setOnClickListener {
                onSampleClick?.invoke(message.text)
            }
        }
    }

    class TypingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dot1: View = view.findViewById(R.id.dot1)
        private val dot2: View = view.findViewById(R.id.dot2)
        private val dot3: View = view.findViewById(R.id.dot3)

        fun startAnimation() {
            val dots = listOf(dot1, dot2, dot3)

            dots.forEachIndexed { index, dot ->
                dot.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay((index * 300).toLong())
                    .withEndAction {
                        dot.alpha = 0f // reset after showing
                    }
                    .start()
            }
        }
    }
}

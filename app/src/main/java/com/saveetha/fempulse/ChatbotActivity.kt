package com.saveetha.fempulse

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.fempulse.adapter.ChatAdapter
import com.saveetha.fempulse.retrofit.*
import com.saveetha.fempulse.response.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

class ChatActivity : AppCompatActivity() {
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private var loadingIndex: Int? = null // To track loading message index

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val userInput = findViewById<EditText>(R.id.userInput)
        val sendBtn = findViewById<ImageButton>(R.id.sendBtn)

        chatAdapter = ChatAdapter(messages) { clickedText ->
            handleUserMessage(clickedText)
        }

        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Welcome & sample Qs
        addMessage("🤖 Welcome! Tap a question below or type your own.", false)
        val samples = listOf(
            "How can I track my periods?",
            "What are common PMS symptoms?",
            "How can I reduce cramps?"
        )
        for (q in samples) {
            addMessage(q, false, isSample = true)
        }

        sendBtn.setOnClickListener {
            val message = userInput.text.toString().trim()
            if (message.isNotEmpty()) {
                handleUserMessage(message)
                userInput.text.clear()
            }
        }
        val back: ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            finish() // close activity, go back
        }
    }

    private fun handleUserMessage(message: String) {
        addMessage(message, true)
        showLoadingDots()
        sendMessageToServer(message)
    }

    private fun addMessage(text: String, isUser: Boolean, isSample: Boolean = false) {
        messages.add(ChatMessage(text, isUser, isSample))
        chatAdapter.notifyItemInserted(messages.size - 1)
    }

    private fun showLoadingDots() {
        val loadingMessage = ChatMessage("...", false, isSample = false, isLoading = true)
        messages.add(loadingMessage)
        loadingIndex = messages.size - 1
        chatAdapter.notifyItemInserted(loadingIndex!!)

        // Animate "..." every 500ms
        val handler = Handler(Looper.getMainLooper())
        var dotCount = 1
        val runnable = object : Runnable {
            override fun run() {
                if (loadingIndex != null && loadingIndex!! < messages.size) {
                    messages[loadingIndex!!].text = ".".repeat(dotCount)
                    chatAdapter.notifyItemChanged(loadingIndex!!)
                    dotCount = (dotCount % 3) + 1
                    handler.postDelayed(this, 500)
                }
            }
        }
        handler.post(runnable)
    }

    private fun removeLoadingDots() {
        loadingIndex?.let {
            messages.removeAt(it)
            chatAdapter.notifyItemRemoved(it)
            loadingIndex = null
        }
    }
    private fun cleanText(text: String): String {
        return text
            .replace("**", "")   // remove bold markers
               // remove italics/bullet markers
            .replace("#", "")    // remove headings
            .trim()
    }


    private fun sendMessageToServer(message: String) {
        val request = ChatRequest(message)
        RetrofitClient.instance.sendMessage(request)
            .enqueue(object : Callback<ChatResponse> {
                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                    removeLoadingDots()
                    if (response.isSuccessful && response.body() != null) {
                        val combined = response.body()!!.messages.joinToString("\n\n")
                        addMessage(cleanText(combined), false)
                    } else {
                        addMessage("Bot: Error in response.", false)
                    }

                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    removeLoadingDots()
                    addMessage("Bot: Failed to connect - ${t.message}", false)
                }
            })
    }
}

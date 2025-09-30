package com.saveetha.fempulse.response

// request
data class EmailVerifyRequest(val email: String)

// response (assuming backend sends { "success": true, "otp": "1234" })
data class EmailVerifyResponse(
    val success: Boolean,
    val message : String,
    val otp: String
)
// Data class for request body
data class FeedbackRequest(
    val feedback: String,
    val email: String
)

// Data class for response
data class FeedbackResponse(
    val status: String,
    val message: String
)

// Request
data class ChatRequest(
    val message: String
)

// Response
data class ChatResponse(
    val status: String,
    val messages: List<String>
)
data class ChatMessage(
    var text: String,  // ⬅ make it mutable
    val isUser: Boolean,
    val isSample: Boolean = false,
    val isLoading: Boolean = false
)



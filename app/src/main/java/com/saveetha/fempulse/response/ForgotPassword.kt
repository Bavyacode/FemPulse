package com.saveetha.fempulse.response
data class ForgotPasswordRequest(
    val email: String
)
data class ForgotPasswordResponse(
    val status: String,
    val message: String,
    val otp: String
)
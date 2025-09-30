package com.saveetha.fempulse.response
data class ForgotPasswordRequest(
    val email: String
)
data class ForgotPasswordResponse(
    val status: String,
    val message: String,
    val otp: String
)

data class ChangePasswordRequest(
    val email: String,
    val new_password: String
)

data class ChangePasswordResponse(
    val status: String,
    val message: String
)

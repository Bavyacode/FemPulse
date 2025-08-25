package com.saveetha.fempulse.response
data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val age: Int
)
data class SignupResponse(
    val success : Boolean,
    val message: String,
    val user_id: Int
)

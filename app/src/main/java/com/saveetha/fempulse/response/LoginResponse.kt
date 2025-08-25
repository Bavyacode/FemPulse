package com.saveetha.fempulse.response
data class LoginRequest(
    val email: String,
    val password: String
)
data class LoginResponse(
    val status: String,
    val message: String,
    val user_id : Int,
    val email : String,
    val username:String
)

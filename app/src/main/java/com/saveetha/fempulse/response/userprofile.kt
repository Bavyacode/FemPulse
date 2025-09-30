package com.saveetha.fempulse.response

data class ProfileUpdateRequest(
    val user_id: Int,
    val username: String? = null,
    val email: String? = null,
    val age: Int? = null,
    val password: String? = null,
    val cycle_duration: Int? = null,
    val cycle_length: Int? = null
)

data class ProfileResponse(
    val status: String,
    val message: String,
    val profile: UserProfile?
)

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    val age: Int,
    val cycle_duration: Int?,
    val cycle_length: Int?
)


package com.saveetha.fempulse.response

data class CycledataRequest(
    val user_id: Int,
    val start_date: String
)

data class CycleDurationRequest(
    val user_id: Int,
    val duration: Int
)
data class CycleIntervalRequest(
    val user_id : Int,
    val interval_days:Int
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)
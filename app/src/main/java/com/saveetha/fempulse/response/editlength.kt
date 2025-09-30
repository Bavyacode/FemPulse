package com.saveetha.fempulse.response

data class CommonResponse(
    val status: String,
    val message: String
)
data class PeriodLengthRequest(
    val user_id: Int,
    val duration: Int
)
data class IntervalRequest(
    val user_id: Int,
    val interval_days: Int
)
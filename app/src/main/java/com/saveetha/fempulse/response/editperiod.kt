package com.saveetha.fempulse.response

data class EditPEriodRequest(
    val user_id: Int,
    val start_date: String,
    val end_date: String? = null
)

data class EditPeriodResponse(
    val success: Boolean,
    val message: String,
)

data class CycleData(
    val user_id: Int,
    val start_date: String,
    val end_date: String,
    val interval_days: Int?,
    val duration: Int
)

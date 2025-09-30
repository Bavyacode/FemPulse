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

data class Cycle_Data(
    val user_id: Int,
    val start_date: String,
    val end_date: String,
    val interval_days: Int?,
    val duration: Int
)
data class SaveCycleRequest(
    val user_id: Int,
    val dates: List<String>,
    val handle_skip: String? = null
)

data class CycleResponse(
    val status: Any,
    val message: String,
    val logged_cycle: LoggedCycle? = null,
    val auto_cycle: LoggedCycle? = null,
    val cycle_data: CycleData? = null,
    val expected_month: String? = null
)

data class LoggedCycle(
    val start_date: String,
    val end_date: String?,
    val duration: Int?,
    val interval_days: Int?
)

data class CycleData(
    val avg_duration: Int?,
    val avg_interval: Int?,
    val current_start: String?
)

data class GetCyclesRequest(
    val user_id: Int
)

data class GetCyclesResponse(
    val status: Boolean,
    val dates: List<String>
)
package com.saveetha.fempulse.response

import com.google.gson.annotations.SerializedName

// Request model
data class UserIdRequest(
    @SerializedName("user_id") val userId: Int
)



// Full Response model
data class MenstrualPhaseResponse(
    val status: Boolean,
    @SerializedName("current_phase") val currentPhase: CurrentPhase?,
    @SerializedName("next_period") val nextPeriod: NextPeriod?,
    @SerializedName("next_phase") val nextPhase: NextPhase?,
    @SerializedName("cycle_phase_overview") val cyclePhaseOverview: List<CyclePhaseOverview>?, // ✅ List not object
    val snapshot: Snapshot?
)


// Current Phase with days_remaining
data class CurrentPhase(
    val name: String?,
    val emoji: String?,
    val start: String?,
    val end: String?,
    @SerializedName("days_remaining") val daysRemaining: String? // ✅ String not Int
)


// Next Period
data class NextPeriod(
    val date: String?
)

// Next Phase
data class NextPhase(
    val name: String?,
    val emoji: String?,
    val start: String?,
    val end: String?
)

// Cycle Phase Overview
// ✅ New version matching API response
data class CyclePhaseOverview(
    val emoji: String?,
    val name: String?,
    val range: String?
)


data class Snapshot(
    @SerializedName("last_period") val lastPeriod: String?,
    @SerializedName("cycle_length") val cycleLength: String?,   // String not Int
    @SerializedName("period_length") val periodLength: String?, // String not Int
    @SerializedName("previous_phase") val previousPhase: String?
)

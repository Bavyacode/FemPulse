package com.saveetha.fempulse.response
data class SymptomResponse(
    val status: String,
    val symptoms: List<Symptom>
)
data class WellnessTip(
    val tip: String,
    val emoji: String = "🤍" // fallback emoji for wellness
)

// Top-level response
data class WellnessResponse(
    val status: String,
    val age: String,
    val is_student: Boolean,
    val symptoms: List<String>,
    val phase: String,
    val tips: Map<String, List<String>>
)




data class Symptom(
    val id: Int,
    val name: String,
    val emoji: String
)

data class HealthTipsResponse(
    val status: String,
    val message: String,
    val tips: List<HealthTip>
)

data class HealthTip(
    val emoji: String,
    val tip: String
)




data class UserRequest(
    val user_id: Int
)




data class LogResponse(
    val status: String,
    val message: String,
    val data: LogData?
)

data class LogData(
    val user_id: Int,
    val symptom_id: List<Int>
)

data class LogSymptomsRequest(
    val user_id: Int,
    val symptom_id: List<Int>
)
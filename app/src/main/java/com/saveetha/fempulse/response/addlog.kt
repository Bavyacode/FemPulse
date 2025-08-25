package com.saveetha.fempulse.response
data class SymptomResponse(
    val status: String,
    val symptoms: List<Symptom>
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
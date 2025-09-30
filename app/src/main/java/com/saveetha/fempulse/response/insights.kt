package com.saveetha.fempulse.response

// models/InsightsResponse.kt
data class InsightsResponse(
    // period duration (bleeding days)
    val period_duration_trends: List<Int> = emptyList(),
    val average_duration: Double? = null,
    val duration_variation: Int? = null,

    // cycle lengths (interval_days)
    val cycle_length_trends: List<Int> = emptyList(),
    val average_cycle_length: Double? = null,
    val cycle_variation: Int? = null,

    val period_regularity: String? = null
)

// models/TopSymptomsResponse.kt
data class TopSymptomsResponse(
    val top_symptoms: List<TopSymptomItem> = emptyList()
)
data class TopSymptomItem(
    val name: String,
    val frequency: Int
)

// models/SymptomsByCategoryResponse.kt
data class SymptomsByCategoryResponse(
    val symptoms_by_category: List<CategoryItem> = emptyList()
)
data class CategoryItem(
    val category: String,
    val total: Int
)

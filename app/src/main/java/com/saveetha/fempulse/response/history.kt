package com.saveetha.fempulse.response

sealed class FullHistoryListItem {
    data class YearHeader(val year: String) : FullHistoryListItem()
    data class History(val item: HistoryItem) : FullHistoryListItem()
}

data class RecentHistoryResponse(
    val status: Boolean,
    val message: String,
    val history: List<CycleHistoryItem>
)

data class FullHistoryResponse(
    val status: Boolean,
    val message: String,
    val history: Map<String, List<HistoryItem>>  // <-- Map for year-wise grouping
)

data class HistoryItem(
    val id: Int,
    val start_date: String,
    val end_date: String,
    val duration: Int,
    val interval_days: Int
)

data class CycleHistoryItem(
    val id: Int,
    val start_date: String,
    val end_date: String,
    val duration: Int,
    val interval_days: Int
)


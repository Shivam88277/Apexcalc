package com.example.data.repository

import com.example.data.local.HistoryDao
import com.example.data.model.CalculationHistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    val allHistory: Flow<List<CalculationHistoryEntity>> = dao.getAllHistory()
    val bookmarkedHistory: Flow<List<CalculationHistoryEntity>> = dao.getBookmarkedHistory()

    fun searchHistory(query: String): Flow<List<CalculationHistoryEntity>> {
        return dao.searchHistory(query)
    }

    suspend fun addCalculation(expression: String, result: String, angleUnit: String) {
        dao.insert(
            CalculationHistoryEntity(
                expression = expression,
                result = result,
                angleUnit = angleUnit
            )
        )
    }

    suspend fun toggleBookmark(id: Long, currentBookmarked: Boolean) {
        dao.setBookmark(id, !currentBookmarked)
    }

    suspend fun updateNote(id: Long, note: String?) {
        dao.updateNote(id, note)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}

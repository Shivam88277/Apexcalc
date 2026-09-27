package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val angleUnit: String = "DEG", // "DEG" or "RAD"
    val timestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false,
    val note: String? = null
)

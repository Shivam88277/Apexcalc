package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CalculationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedHistory(): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE expression LIKE '%' || :query || '%' OR result LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<CalculationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CalculationHistoryEntity): Long

    @Update
    suspend fun update(entity: CalculationHistoryEntity)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()

    @Query("UPDATE calculation_history SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE calculation_history SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String?)
}

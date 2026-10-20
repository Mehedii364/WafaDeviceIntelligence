package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BenchmarkHistoryEntity
import com.example.data.local.entity.ChargingSessionEntity
import com.example.data.local.entity.DiagnosticHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargingSessionDao {
  @Query("SELECT * FROM charging_sessions ORDER BY id DESC")
  fun getAllSessions(): Flow<List<ChargingSessionEntity>>

  @Query("SELECT * FROM charging_sessions WHERE isCompleted = 0 ORDER BY id DESC LIMIT 1")
  suspend fun getActiveSession(): ChargingSessionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: ChargingSessionEntity): Long

  @Update
  suspend fun updateSession(session: ChargingSessionEntity)

  @Query("DELETE FROM charging_sessions")
  suspend fun deleteAll()
}

@Dao
interface DiagnosticHistoryDao {
  @Query("SELECT * FROM diagnostic_history ORDER BY sessionTimestamp DESC")
  fun getAllHistory(): Flow<List<DiagnosticHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(history: DiagnosticHistoryEntity): Long

  @Query("DELETE FROM diagnostic_history")
  suspend fun deleteAll()
}

@Dao
interface BenchmarkHistoryDao {
  @Query("SELECT * FROM benchmark_history ORDER BY timestamp DESC")
  fun getAllBenchmarks(): Flow<List<BenchmarkHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBenchmark(benchmark: BenchmarkHistoryEntity): Long

  @Query("DELETE FROM benchmark_history")
  suspend fun deleteAll()
}

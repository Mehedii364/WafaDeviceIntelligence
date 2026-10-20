package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnostic_history")
data class DiagnosticHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val sessionTimestamp: Long,
  val overallStatus: String,
  val totalTests: Int,
  val passedCount: Int,
  val warningCount: Int,
  val failedCount: Int,
  val limitedCount: Int,
  val unsupportedCount: Int,
  val summaryDetailsJson: String
)

@Entity(tableName = "benchmark_history")
data class BenchmarkHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val testType: String,
  val score: Long,
  val durationMs: Long,
  val initialThermalStatus: String,
  val finalThermalStatus: String,
  val initialBatteryTempC: Float?,
  val finalBatteryTempC: Float?,
  val timestamp: Long = System.currentTimeMillis()
)

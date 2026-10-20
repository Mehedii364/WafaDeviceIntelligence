package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_sessions")
data class ChargingSessionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val startTime: Long,
  val endTime: Long? = null,
  val initialPercentage: Int,
  val currentPercentage: Int,
  val finalPercentage: Int? = null,
  val durationMs: Long = 0,
  val minTemperatureC: Float? = null,
  val maxTemperatureC: Float? = null,
  val minVoltageV: Float? = null,
  val maxVoltageV: Float? = null,
  val pluggedSource: String,
  val isCompleted: Boolean = false,
  val completionReason: String = "In Progress"
)

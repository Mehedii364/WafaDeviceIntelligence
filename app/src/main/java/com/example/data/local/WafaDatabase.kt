package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BenchmarkHistoryDao
import com.example.data.local.dao.ChargingSessionDao
import com.example.data.local.dao.DiagnosticHistoryDao
import com.example.data.local.entity.BenchmarkHistoryEntity
import com.example.data.local.entity.ChargingSessionEntity
import com.example.data.local.entity.DiagnosticHistoryEntity

@Database(
  entities = [
    ChargingSessionEntity::class,
    DiagnosticHistoryEntity::class,
    BenchmarkHistoryEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class WafaDatabase : RoomDatabase() {
  abstract fun chargingSessionDao(): ChargingSessionDao
  abstract fun diagnosticHistoryDao(): DiagnosticHistoryDao
  abstract fun benchmarkHistoryDao(): BenchmarkHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: WafaDatabase? = null

    fun getDatabase(context: Context): WafaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WafaDatabase::class.java,
          "wafa_device_intelligence.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}

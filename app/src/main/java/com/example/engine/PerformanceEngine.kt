package com.example.engine

import android.content.Context
import com.example.data.local.WafaDatabase
import com.example.data.local.entity.BenchmarkHistoryEntity
import com.example.data.model.BenchmarkResult
import com.example.data.model.BenchmarkType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerformanceEngine(
  private val context: Context,
  private val thermalEngine: ThermalEngine,
  private val batteryEngine: BatteryEngine
) {
  private val database = WafaDatabase.getDatabase(context)

  suspend fun runCpuBenchmark(): BenchmarkResult = withContext(Dispatchers.Default) {
    val initialThermal = thermalEngine.getCurrentStatus()
    val initialTemp = batteryEngine.batteryInfo.value.temperatureC

    val startTime = System.currentTimeMillis()
    var primesFound = 0L

    // Prime Sieve of Eratosthenes across 12 iterations
    val limit = 120_000
    for (iter in 0 until 10) {
      val isPrime = BooleanArray(limit + 1) { true }
      isPrime[0] = false
      isPrime[1] = false
      var p = 2
      while (p * p <= limit) {
        if (isPrime[p]) {
          var i = p * p
          while (i <= limit) {
            isPrime[i] = false
            i += p
          }
        }
        p++
      }
      var count = 0
      for (b in isPrime) {
        if (b) count++
      }
      primesFound += count
    }

    val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(1L)
    val finalThermal = thermalEngine.getCurrentStatus()
    val finalTemp = batteryEngine.batteryInfo.value.temperatureC

    // Score is operations normalized over time
    val score = ((primesFound * 1000L) / duration)

    val result = BenchmarkResult(
      benchmarkType = BenchmarkType.CPU_PRIME_SIEVE,
      score = score,
      durationMs = duration,
      initialThermalStatus = initialThermal,
      finalThermalStatus = finalThermal,
      initialBatteryTempC = initialTemp,
      finalBatteryTempC = finalTemp
    )

    // Save to database
    database.benchmarkHistoryDao().insertBenchmark(
      BenchmarkHistoryEntity(
        testType = result.benchmarkType.title,
        score = result.score,
        durationMs = result.durationMs,
        initialThermalStatus = result.initialThermalStatus,
        finalThermalStatus = result.finalThermalStatus,
        initialBatteryTempC = result.initialBatteryTempC,
        finalBatteryTempC = result.finalBatteryTempC,
        timestamp = result.timestamp
      )
    )

    result
  }

  suspend fun runMemoryBenchmark(): BenchmarkResult = withContext(Dispatchers.Default) {
    val initialThermal = thermalEngine.getCurrentStatus()
    val initialTemp = batteryEngine.batteryInfo.value.temperatureC

    val startTime = System.currentTimeMillis()

    // Allocate and manipulate 2.5 million Float buffer elements over 8 passes
    val bufferSize = 2_500_000
    var accumulator = 0.0f
    for (pass in 0 until 6) {
      val array = FloatArray(bufferSize) { i -> (i % 256).toFloat() }
      for (i in 0 until bufferSize step 4) {
        array[i] = array[i] * 1.05f + 0.5f
        accumulator += array[i]
      }
    }

    val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(1L)
    val finalThermal = thermalEngine.getCurrentStatus()
    val finalTemp = batteryEngine.batteryInfo.value.temperatureC

    val megaBytesProcessed = (bufferSize * 4L * 6L) / (1024L * 1024L) // in MB
    val score = ((megaBytesProcessed * 1000L) / duration) * 10L // MB/s normalized

    val result = BenchmarkResult(
      benchmarkType = BenchmarkType.MEMORY_THROUGHPUT,
      score = score,
      durationMs = duration,
      initialThermalStatus = initialThermal,
      finalThermalStatus = finalThermal,
      initialBatteryTempC = initialTemp,
      finalBatteryTempC = finalTemp
    )

    // Save to database
    database.benchmarkHistoryDao().insertBenchmark(
      BenchmarkHistoryEntity(
        testType = result.benchmarkType.title,
        score = result.score,
        durationMs = result.durationMs,
        initialThermalStatus = result.initialThermalStatus,
        finalThermalStatus = result.finalThermalStatus,
        initialBatteryTempC = result.initialBatteryTempC,
        finalBatteryTempC = result.finalBatteryTempC,
        timestamp = result.timestamp
      )
    )

    result
  }
}

package com.example.data.model

data class SensorItem(
  val type: Int,
  val typeName: String,
  val name: String,
  val vendor: String,
  val version: Int,
  val powerMa: Float,
  val resolution: Float,
  val maximumRange: Float,
  val minDelayUs: Int,
  val isWakeUp: Boolean,
  val isAvailable: Boolean = true,
  val values: FloatArray = FloatArray(0),
  val unit: String = ""
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as SensorItem
    return type == other.type && name == other.name
  }

  override fun hashCode(): Int {
    var result = type
    result = 31 * result + name.hashCode()
    return result
  }
}

enum class BenchmarkType(val title: String) {
  CPU_PRIME_SIEVE("CPU Arithmetic Prime Sieve"),
  CPU_FIBONACCI("CPU Recursive Tree & Math"),
  MEMORY_THROUGHPUT("Memory Buffer Bandwidth")
}

data class BenchmarkResult(
  val id: Long = 0,
  val benchmarkType: BenchmarkType,
  val score: Long,
  val durationMs: Long,
  val initialThermalStatus: String,
  val finalThermalStatus: String,
  val initialBatteryTempC: Float?,
  val finalBatteryTempC: Float?,
  val timestamp: Long = System.currentTimeMillis()
)

data class NetworkDiagnostics(
  val isConnected: Boolean = false,
  val transport: String = "None",
  val isValidated: Boolean = false,
  val isMetered: Boolean = false,
  val isVpn: Boolean = false,
  val dnsLookupMs: Long? = null,
  val httpLatencyMs: Long? = null,
  val jitterMs: Long? = null,
  val localIpv4: String? = null,
  val localIpv6: String? = null,
  val testEndpoint: String = "dns.google"
)

data class StorageDiagnostics(
  val internalTotalBytes: Long = 0L,
  val internalAvailableBytes: Long = 0L,
  val internalUsedBytes: Long = 0L,
  val appCacheBytes: Long = 0L,
  val appFilesBytes: Long = 0L,
  val volumesCount: Int = 1
)

data class HardwareDiagnostics(
  val manufacturer: String,
  val brand: String,
  val model: String,
  val board: String,
  val hardware: String,
  val androidRelease: String,
  val sdkInt: Int,
  val securityPatch: String,
  val supportedAbis: List<String>,
  val cpuCores: Int,
  val totalRamBytes: Long,
  val availableRamBytes: Long,
  val isLowRamDevice: Boolean
)

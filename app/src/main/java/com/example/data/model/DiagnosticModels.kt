package com.example.data.model

enum class DiagnosticStatus(val label: String) {
  AVAILABLE("Available"),
  LIMITED("Limited"),
  UNSUPPORTED("Unsupported"),
  PERMISSION_REQUIRED("Permission Required"),
  NOT_TESTED("Not Tested"),
  RUNNING("Running..."),
  PASSED("Passed"),
  WARNING("Warning"),
  FAILED("Failed"),
  INTERRUPTED("Interrupted")
}

data class DiagnosticResult(
  val id: String,
  val name: String,
  val category: String,
  val status: DiagnosticStatus,
  val measurementSource: String,
  val timestamp: Long = System.currentTimeMillis(),
  val summary: String,
  val details: Map<String, String> = emptyMap(),
  val unit: String? = null
)

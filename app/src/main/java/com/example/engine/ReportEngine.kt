package com.example.engine

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.BatteryInfo
import com.example.data.model.DiagnosticResult
import com.example.data.model.HardwareDiagnostics
import com.example.data.model.NetworkDiagnostics
import com.example.data.model.StorageDiagnostics
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportEngine(private val context: Context) {

  fun generateJsonReport(
    hardware: HardwareDiagnostics,
    battery: BatteryInfo,
    storage: StorageDiagnostics,
    network: NetworkDiagnostics,
    diagnostics: List<DiagnosticResult>
  ): String {
    val dateStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
    val sb = StringBuilder()
    sb.append("{\n")
    sb.append("  \"application\": \"Wafa Device Intelligence\",\n")
    sb.append("  \"integratedModule\": \"Wafa ChargeLab\",\n")
    sb.append("  \"developer\": \"Mehedi364 (Md. Mehedi Hasan)\",\n")
    sb.append("  \"brand\": \"Wafa Zone\",\n")
    sb.append("  \"timestamp\": \"$dateStr\",\n")
    sb.append("  \"device\": {\n")
    sb.append("    \"model\": \"${hardware.model}\",\n")
    sb.append("    \"manufacturer\": \"${hardware.manufacturer}\",\n")
    sb.append("    \"brand\": \"${hardware.brand}\",\n")
    sb.append("    \"androidRelease\": \"${hardware.androidRelease}\",\n")
    sb.append("    \"sdkInt\": ${hardware.sdkInt},\n")
    sb.append("    \"securityPatch\": \"${hardware.securityPatch}\",\n")
    sb.append("    \"cpuCores\": ${hardware.cpuCores},\n")
    sb.append("    \"totalRamBytes\": ${hardware.totalRamBytes},\n")
    sb.append("    \"availableRamBytes\": ${hardware.availableRamBytes}\n")
    sb.append("  },\n")
    sb.append("  \"battery\": {\n")
    sb.append("    \"percentage\": ${battery.percentage},\n")
    sb.append("    \"state\": \"${battery.state.name}\",\n")
    sb.append("    \"plugged\": \"${battery.plugged.name}\",\n")
    sb.append("    \"temperatureC\": ${battery.temperatureC ?: "null"},\n")
    sb.append("    \"voltageV\": ${battery.voltageV ?: "null"},\n")
    sb.append("    \"health\": \"${battery.healthString}\"\n")
    sb.append("  },\n")
    sb.append("  \"storage\": {\n")
    sb.append("    \"internalTotalBytes\": ${storage.internalTotalBytes},\n")
    sb.append("    \"internalAvailableBytes\": ${storage.internalAvailableBytes},\n")
    sb.append("    \"internalUsedBytes\": ${storage.internalUsedBytes}\n")
    sb.append("  },\n")
    sb.append("  \"network\": {\n")
    sb.append("    \"transport\": \"${network.transport}\",\n")
    sb.append("    \"isConnected\": ${network.isConnected},\n")
    sb.append("    \"isValidated\": ${network.isValidated},\n")
    sb.append("    \"dnsLookupMs\": ${network.dnsLookupMs ?: "null"},\n")
    sb.append("    \"httpLatencyMs\": ${network.httpLatencyMs ?: "null"}\n")
    sb.append("  },\n")
    sb.append("  \"diagnosticResults\": [\n")
    diagnostics.forEachIndexed { index, diag ->
      sb.append("    {\n")
      sb.append("      \"id\": \"${diag.id}\",\n")
      sb.append("      \"name\": \"${diag.name}\",\n")
      sb.append("      \"category\": \"${diag.category}\",\n")
      sb.append("      \"status\": \"${diag.status.name}\",\n")
      sb.append("      \"summary\": \"${diag.summary.replace("\"", "\\\"")}\",\n")
      sb.append("      \"measurementSource\": \"${diag.measurementSource}\"\n")
      sb.append("    }${if (index < diagnostics.size - 1) "," else ""}\n")
    }
    sb.append("  ]\n")
    sb.append("}\n")
    return sb.toString()
  }

  fun generateCsvReport(diagnostics: List<DiagnosticResult>): String {
    val sb = StringBuilder()
    sb.append("ID,Category,Name,Status,MeasurementSource,Summary\n")
    for (d in diagnostics) {
      val cleanSummary = d.summary.replace("\"", "\"\"")
      sb.append("\"${d.id}\",\"${d.category}\",\"${d.name}\",\"${d.status.name}\",\"${d.measurementSource}\",\"$cleanSummary\"\n")
    }
    return sb.toString()
  }

  fun generateTextSummary(
    hardware: HardwareDiagnostics,
    battery: BatteryInfo,
    diagnostics: List<DiagnosticResult>
  ): String {
    val sb = StringBuilder()
    sb.append("WAFA DEVICE INTELLIGENCE - DIAGNOSTIC REPORT\n")
    sb.append("Developer: Mehedi364 (Md. Mehedi Hasan) | Wafa Zone\n")
    sb.append("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n")
    sb.append("===================================================\n\n")
    sb.append("DEVICE: ${hardware.manufacturer} ${hardware.model} (${hardware.brand})\n")
    sb.append("OS: Android ${hardware.androidRelease} (API ${hardware.sdkInt}) | Security: ${hardware.securityPatch}\n")
    sb.append("CPU: ${hardware.cpuCores} Cores | ABIs: ${hardware.supportedAbis.joinToString()}\n")
    sb.append("RAM: %.2f GB Total / %.2f GB Available\n".format(
      hardware.totalRamBytes / 1_073_741_824.0,
      hardware.availableRamBytes / 1_073_741_824.0
    ))
    sb.append("BATTERY: ${battery.percentage}% (${battery.state.label}, ${battery.plugged.label})\n")
    sb.append("TEMP: ${battery.temperatureC?.let { "%.1f°C".format(it) } ?: "N/A"} | VOLTAGE: ${battery.voltageV?.let { "%.2f V".format(it) } ?: "N/A"}\n\n")
    sb.append("TEST RESULTS SUMMARY (${diagnostics.size} Checks):\n")
    for (d in diagnostics) {
      sb.append("[${d.status.name}] ${d.name} (${d.category}): ${d.summary}\n")
    }
    return sb.toString()
  }

  fun shareReport(content: String, filename: String, mimeType: String) {
    try {
      val reportsDir = File(context.cacheDir, "reports")
      if (!reportsDir.exists()) reportsDir.mkdirs()
      val file = File(reportsDir, filename)
      file.writeText(content)

      val uri = FileProvider.getUriForFile(
        context,
        "site.wafazone.deviceintelligence.fileprovider",
        file
      )

      val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Wafa Device Intelligence Report")
        putExtra(Intent.EXTRA_TEXT, "Diagnostic Report generated by Wafa Device Intelligence (Mehedi364 / Wafa Zone)")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(intent, "Share Diagnostic Report").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
    } catch (_: Exception) {}
  }
}

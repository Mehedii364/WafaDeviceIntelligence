package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BatteryChargingState
import com.example.data.model.BatteryInfo
import com.example.data.model.DiagnosticResult
import com.example.data.model.DiagnosticStatus
import com.example.data.model.HardwareDiagnostics
import com.example.data.model.NetworkDiagnostics
import com.example.data.model.PluggedSource
import com.example.data.model.StorageDiagnostics
import com.example.engine.ReportEngine
import com.example.ui.theme.WafaThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameResource() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Wafa Device Intelligence", appName)
  }

  @Test
  fun testThemeModesAvailability() {
    val themes = WafaThemeMode.values()
    assertEquals(4, themes.size)
    assertTrue(themes.contains(WafaThemeMode.OBSIDIAN_AURORA))
    assertTrue(themes.contains(WafaThemeMode.GRAPHITE_PRO))
    assertTrue(themes.contains(WafaThemeMode.LIGHT_TITANIUM))
    assertTrue(themes.contains(WafaThemeMode.MIDNIGHT_VIOLET))
  }

  @Test
  fun testReportEngineJsonGeneration() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val reportEngine = ReportEngine(context)

    val hw = HardwareDiagnostics(
      manufacturer = "Google",
      brand = "Pixel",
      model = "Pixel 8 Pro",
      board = "husky",
      hardware = "tensor",
      androidRelease = "14",
      sdkInt = 34,
      securityPatch = "2026-10-01",
      supportedAbis = listOf("arm64-v8a"),
      cpuCores = 8,
      totalRamBytes = 12_000_000_000L,
      availableRamBytes = 6_000_000_000L,
      isLowRamDevice = false
    )

    val bat = BatteryInfo(
      percentage = 85,
      state = BatteryChargingState.CHARGING,
      plugged = PluggedSource.AC,
      temperatureC = 29.5f,
      voltageV = 4.15f
    )

    val st = StorageDiagnostics(
      internalTotalBytes = 128_000_000_000L,
      internalAvailableBytes = 64_000_000_000L,
      internalUsedBytes = 64_000_000_000L
    )

    val net = NetworkDiagnostics(
      isConnected = true,
      transport = "Wi-Fi",
      isValidated = true
    )

    val diags = listOf(
      DiagnosticResult(
        id = "test_1",
        name = "CPU Core Verification",
        category = "Processor",
        status = DiagnosticStatus.PASSED,
        measurementSource = "Runtime.availableProcessors",
        summary = "8 CPU Cores operational"
      )
    )

    val json = reportEngine.generateJsonReport(hw, bat, st, net, diags)
    assertNotNull(json)
    assertTrue(json.contains("Wafa Device Intelligence"))
    assertTrue(json.contains("Mehedi364"))
    assertTrue(json.contains("Pixel 8 Pro"))
    assertTrue(json.contains("CPU Core Verification"))
  }

  @Test
  fun testReportEngineCsvGeneration() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val reportEngine = ReportEngine(context)

    val diags = listOf(
      DiagnosticResult(
        id = "test_sensor",
        name = "Accelerometer",
        category = "Sensors",
        status = DiagnosticStatus.AVAILABLE,
        measurementSource = "SensorManager",
        summary = "Motion sensor verified"
      )
    )

    val csv = reportEngine.generateCsvReport(diags)
    assertNotNull(csv)
    assertTrue(csv.contains("ID,Category,Name,Status,MeasurementSource,Summary"))
    assertTrue(csv.contains("test_sensor"))
    assertTrue(csv.contains("Accelerometer"))
  }
}

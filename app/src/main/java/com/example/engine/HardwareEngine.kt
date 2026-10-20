package com.example.engine

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.data.model.DiagnosticResult
import com.example.data.model.DiagnosticStatus
import com.example.data.model.HardwareDiagnostics
import com.example.data.model.StorageDiagnostics
import java.io.File

class HardwareEngine(private val context: Context) {

  fun getHardwareDiagnostics(): HardwareDiagnostics {
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)

    return HardwareDiagnostics(
      manufacturer = Build.MANUFACTURER,
      brand = Build.BRAND,
      model = Build.MODEL,
      board = Build.BOARD,
      hardware = Build.HARDWARE,
      androidRelease = Build.VERSION.RELEASE,
      sdkInt = Build.VERSION.SDK_INT,
      securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "N/A",
      supportedAbis = Build.SUPPORTED_ABIS.toList(),
      cpuCores = Runtime.getRuntime().availableProcessors(),
      totalRamBytes = memInfo.totalMem,
      availableRamBytes = memInfo.availMem,
      isLowRamDevice = memInfo.lowMemory
    )
  }

  fun getStorageDiagnostics(): StorageDiagnostics {
    val statFs = StatFs(Environment.getDataDirectory().path)
    val blockSize = statFs.blockSizeLong
    val totalBlocks = statFs.blockCountLong
    val availableBlocks = statFs.availableBlocksLong

    val totalBytes = totalBlocks * blockSize
    val availableBytes = availableBlocks * blockSize
    val usedBytes = totalBytes - availableBytes

    val appCache = context.cacheDir.calculateDirectorySize() + (context.externalCacheDir?.calculateDirectorySize() ?: 0L)
    val appFiles = context.filesDir.calculateDirectorySize()

    return StorageDiagnostics(
      internalTotalBytes = totalBytes,
      internalAvailableBytes = availableBytes,
      internalUsedBytes = usedBytes,
      appCacheBytes = appCache,
      appFilesBytes = appFiles,
      volumesCount = 1
    )
  }

  private fun File.calculateDirectorySize(): Long {
    var length = 0L
    try {
      val files = this.listFiles()
      if (files != null) {
        for (file in files) {
          length += if (file.isFile) file.length() else file.calculateDirectorySize()
        }
      }
    } catch (_: Exception) {}
    return length
  }

  fun getDisplayDiagnostics(): Map<String, String> {
    return try {
      val metrics = context.resources.displayMetrics
      val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
      val refreshRate = try {
        @Suppress("DEPRECATION")
        wm?.defaultDisplay?.refreshRate ?: 60.0f
      } catch (_: Exception) {
        60.0f
      }

      val modesStr = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && wm != null) {
          @Suppress("DEPRECATION")
          wm.defaultDisplay?.supportedModes?.joinToString {
            "${it.physicalWidth}x${it.physicalHeight}@${it.refreshRate.toInt()}Hz"
          } ?: "Standard Mode"
        } else "Standard Mode"
      } catch (_: Exception) {
        "Standard Mode"
      }

      mapOf(
        "Resolution" to "${metrics.widthPixels} x ${metrics.heightPixels} px",
        "Density DPI" to "${metrics.densityDpi} dpi (scale factor: ${metrics.density})",
        "Refresh Rate" to "${"%.1f".format(refreshRate)} Hz",
        "Supported Modes" to modesStr
      )
    } catch (_: Exception) {
      mapOf(
        "Resolution" to "Available on Device Screen",
        "Density DPI" to "Standard Density",
        "Refresh Rate" to "60.0 Hz",
        "Supported Modes" to "Standard Mode"
      )
    }
  }

  fun getCameraCharacteristicsSummary(): List<DiagnosticResult> {
    val list = mutableListOf<DiagnosticResult>()
    try {
      val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
      if (cameraManager == null) {
        list.add(
          DiagnosticResult(
            id = "camera_service",
            name = "Camera Hardware",
            category = "Camera",
            status = DiagnosticStatus.UNSUPPORTED,
            measurementSource = "CameraManager",
            summary = "Camera service is not available on this device"
          )
        )
        return list
      }

      val cameraIds = cameraManager.cameraIdList
      if (cameraIds.isEmpty()) {
        list.add(
          DiagnosticResult(
            id = "camera_sensors",
            name = "Camera Sensors",
            category = "Camera",
            status = DiagnosticStatus.UNSUPPORTED,
            measurementSource = "CameraManager",
            summary = "No accessible camera sensors detected"
          )
        )
        return list
      }

      for (id in cameraIds) {
        val chars = cameraManager.getCameraCharacteristics(id)
        val facing = chars.get(CameraCharacteristics.LENS_FACING)
        val facingStr = when (facing) {
          CameraCharacteristics.LENS_FACING_BACK -> "Rear Main Camera"
          CameraCharacteristics.LENS_FACING_FRONT -> "Front Selfie Camera"
          CameraCharacteristics.LENS_FACING_EXTERNAL -> "External Camera"
          else -> "Auxiliary Camera $id"
        }
        val flashAvailable = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
        val hwLevel = chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        val hwLevelStr = when (hwLevel) {
          CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "Legacy"
          CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "Limited"
          CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "Full"
          CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "Level 3 (Advanced)"
          else -> "Basic"
        }

        list.add(
          DiagnosticResult(
            id = "camera_$id",
            name = "$facingStr (ID: $id)",
            category = "Camera",
            status = DiagnosticStatus.AVAILABLE,
            measurementSource = "CameraCharacteristics",
            summary = "Hardware Level: $hwLevelStr | Flash: ${if (flashAvailable) "Supported" else "None"}",
            details = mapOf(
              "Facing" to facingStr,
              "Hardware Support Level" to hwLevelStr,
              "Flash Unit Available" to if (flashAvailable) "Yes" else "No",
              "Camera ID" to id
            )
          )
        )
      }
    } catch (e: Exception) {
      list.add(
        DiagnosticResult(
          id = "camera_error",
          name = "Camera Inspection",
          category = "Camera",
          status = DiagnosticStatus.WARNING,
          measurementSource = "CameraManager",
          summary = "Camera access limited: ${e.message}"
        )
      )
    }
    return list
  }

  fun getSystemFeaturesMatrix(): List<DiagnosticResult> {
    val pm = context.packageManager
    val features = listOf(
      Triple("NFC", PackageManager.FEATURE_NFC, "Near Field Communication transceiver"),
      Triple("Bluetooth LE", PackageManager.FEATURE_BLUETOOTH_LE, "Bluetooth Low Energy peripheral and central"),
      Triple("Biometrics / Fingerprint", PackageManager.FEATURE_FINGERPRINT, "Secure biometric hardware sensor"),
      Triple("Location / GPS", PackageManager.FEATURE_LOCATION_GPS, "Hardware Global Positioning System receiver"),
      Triple("Camera Flash", PackageManager.FEATURE_CAMERA_FLASH, "Rear optical LED flash hardware"),
      Triple("Accelerometer", PackageManager.FEATURE_SENSOR_ACCELEROMETER, "3-Axis motion & gravity sensor"),
      Triple("Gyroscope", PackageManager.FEATURE_SENSOR_GYROSCOPE, "3-Axis angular velocity gyroscope"),
      Triple("Compass / Magnetometer", PackageManager.FEATURE_SENSOR_COMPASS, "Magnetic field direction sensor"),
      Triple("Barometer", PackageManager.FEATURE_SENSOR_BAROMETER, "Atmospheric air pressure sensor"),
      Triple("Step Counter", PackageManager.FEATURE_SENSOR_STEP_COUNTER, "Hardware pedometer step counter")
    )

    return features.map { (name, feature, desc) ->
      val hasFeature = pm.hasSystemFeature(feature)
      DiagnosticResult(
        id = "feat_$feature",
        name = name,
        category = "Hardware Features",
        status = if (hasFeature) DiagnosticStatus.AVAILABLE else DiagnosticStatus.UNSUPPORTED,
        measurementSource = "PackageManager.hasSystemFeature",
        summary = if (hasFeature) "Supported by hardware ($desc)" else "Unsupported / Hardware not present",
        details = mapOf("Feature Key" to feature, "Description" to desc)
      )
    }
  }
}

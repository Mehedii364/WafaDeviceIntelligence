package com.example.engine

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin
import kotlin.math.sqrt

class HardwareTestLabEngine(private val context: Context) {

  // ==================== 1. Speaker Playback Test (Real Sine Wave Tone) ====================
  suspend fun playTestTone(frequencyHz: Int = 440, durationMs: Int = 1500): Boolean = withContext(Dispatchers.Default) {
    var audioTrack: AudioTrack? = null
    try {
      val sampleRate = 44100
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
      val samples = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val angle = 2.0 * Math.PI * i * frequencyHz / sampleRate
        samples[i] = (sin(angle) * Short.MAX_VALUE * 0.75).toInt().toShort()
      }

      val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      ).coerceAtLeast(numSamples * 2)

      audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(samples, 0, numSamples)
      audioTrack.play()
      kotlinx.coroutines.delay(durationMs.toLong())
      audioTrack.stop()
      audioTrack.release()
      true
    } catch (_: Exception) {
      try {
        audioTrack?.release()
      } catch (_: Exception) {}
      false
    }
  }

  // ==================== 2. Microphone Audio Level Meter ====================
  @SuppressLint("MissingPermission")
  suspend fun measureMicrophoneLevel(durationMs: Int = 2000, onLevelUpdate: (Float) -> Unit): Boolean = withContext(Dispatchers.IO) {
    var audioRecord: AudioRecord? = null
    try {
      val sampleRate = 44100
      val channelConfig = AudioFormat.CHANNEL_IN_MONO
      val audioFormat = AudioFormat.ENCODING_PCM_16BIT
      val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

      if (minBufferSize <= 0) return@withContext false

      audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        channelConfig,
        audioFormat,
        minBufferSize
      )

      if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
        audioRecord.release()
        return@withContext false
      }

      audioRecord.startRecording()
      val buffer = ShortArray(minBufferSize)
      val startTime = System.currentTimeMillis()

      while (System.currentTimeMillis() - startTime < durationMs) {
        val read = audioRecord.read(buffer, 0, buffer.size)
        if (read > 0) {
          var sum = 0.0
          for (i in 0 until read) {
            sum += buffer[i] * buffer[i]
          }
          val rms = sqrt(sum / read)
          // Normalize to [0..100] range
          val level = ((rms / Short.MAX_VALUE) * 100.0f).coerceIn(0.0, 100.0).toFloat()
          onLevelUpdate(level)
        }
        kotlinx.coroutines.delay(80)
      }

      audioRecord.stop()
      audioRecord.release()
      true
    } catch (_: Exception) {
      try {
        audioRecord?.release()
      } catch (_: Exception) {}
      false
    }
  }

  // ==================== 3. Vibration Test ====================
  fun testVibration(pattern: String = "Double Pulse"): Boolean {
    return try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        val vibrator = vibratorManager?.defaultVibrator
        if (vibrator?.hasVibrator() == true) {
          val timings = longArrayOf(0, 150, 100, 250)
          val amplitudes = intArrayOf(0, 200, 0, 255)
          val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
          vibrator.vibrate(effect)
          true
        } else false
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() == true) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
          } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(350)
          }
          true
        } else false
      }
    } catch (_: Exception) {
      false
    }
  }

  // ==================== 4. Flashlight Test ====================
  fun toggleFlashlight(enabled: Boolean): Boolean {
    return try {
      val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
      val cameraIds = cameraManager?.cameraIdList ?: return false
      for (id in cameraIds) {
        val chars = cameraManager.getCameraCharacteristics(id)
        val hasFlash = chars.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
        if (hasFlash) {
          cameraManager.setTorchMode(id, enabled)
          return true
        }
      }
      false
    } catch (_: Exception) {
      false
    }
  }
}

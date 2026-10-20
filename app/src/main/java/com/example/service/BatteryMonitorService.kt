package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.WafaApplication

class BatteryMonitorService : Service() {
  companion object {
    private const val CHANNEL_ID = "wafa_battery_monitor_channel"
    private const val NOTIFICATION_ID = 1001

    fun startService(context: Context) {
      try {
        val intent = Intent(context, BatteryMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          context.startForegroundService(intent)
        } else {
          context.startService(intent)
        }
      } catch (_: Exception) {}
    }

    fun stopService(context: Context) {
      try {
        val intent = Intent(context, BatteryMonitorService::class.java)
        context.stopService(intent)
      } catch (_: Exception) {}
    }
  }

  override fun onCreate() {
    super.onCreate()
    try {
      createNotificationChannel()
      val notification = buildNotification("Wafa ChargeLab active: Monitoring battery and thermal state")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        startForeground(
          NOTIFICATION_ID,
          notification,
          android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )
      } else {
        startForeground(NOTIFICATION_ID, notification)
      }
    } catch (_: Exception) {}
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Wafa ChargeLab Continuous Monitor",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Notifies when background battery charging session monitoring is active"
      }
      val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }

  private fun buildNotification(text: String): Notification {
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      Intent(this, MainActivity::class.java),
      PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Wafa ChargeLab Monitor")
      .setContentText(text)
      .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .build()
  }
}

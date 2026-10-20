package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatteryChargingState
import com.example.data.model.BatteryInfo
import com.example.ui.theme.LocalWafaColors

@Composable
fun WafaBatteryGauge(
  batteryInfo: BatteryInfo,
  modifier: Modifier = Modifier
) {
  val animatedPercentage by animateFloatAsState(
    targetValue = batteryInfo.percentage.toFloat(),
    animationSpec = tween(durationMillis = 800),
    label = "BatteryPctAnim"
  )

  val wafaColors = LocalWafaColors.current

  val arcColor = when {
    batteryInfo.state == BatteryChargingState.CHARGING -> wafaColors.accentBlue
    batteryInfo.percentage <= 20 -> wafaColors.statusError
    batteryInfo.percentage <= 40 -> wafaColors.statusWarning
    else -> wafaColors.statusSuccess
  }

  val trackColor = wafaColors.elevatedSurface

  Box(
    modifier = modifier.size(220.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokeWidth = 14.dp.toPx()
      val arcSize = size.width - strokeWidth
      val topLeftOffset = strokeWidth / 2f

      // Background track (260 degree arc from 140 to 400 deg)
      drawArc(
        color = trackColor,
        startAngle = 140f,
        sweepAngle = 260f,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(topLeftOffset, topLeftOffset),
        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Active fill arc
      val sweep = (animatedPercentage / 100f) * 260f
      drawArc(
        brush = Brush.sweepGradient(
          0.0f to arcColor.copy(alpha = 0.8f),
          0.8f to arcColor,
          1.0f to arcColor
        ),
        startAngle = 140f,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(topLeftOffset, topLeftOffset),
        size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "${batteryInfo.percentage}%",
        style = MaterialTheme.typography.displayLarge.copy(
          fontSize = 44.sp,
          fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = batteryInfo.state.label.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        ),
        color = arcColor
      )
      if (batteryInfo.plugged.name != "NONE") {
        Text(
          text = batteryInfo.plugged.label,
          style = MaterialTheme.typography.labelSmall,
          color = LocalWafaColors.current.accentGold
        )
      }
    }
  }
}

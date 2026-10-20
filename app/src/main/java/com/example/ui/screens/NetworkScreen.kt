package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkDiagnostics
import com.example.engine.NetworkEngine
import com.example.ui.components.WafaIcons
import com.example.ui.components.WafaMetricCard
import com.example.ui.components.WafaPrimaryButton
import com.example.ui.components.WafaSectionHeader
import com.example.ui.theme.LocalWafaColors
import kotlinx.coroutines.launch

@Composable
fun NetworkScreen(
  network: NetworkDiagnostics,
  engine: NetworkEngine,
  modifier: Modifier = Modifier
) {
  val colors = LocalWafaColors.current
  val scope = rememberCoroutineScope()

  var dnsMs by remember { mutableStateOf<Long?>(null) }
  var isRunningDns by remember { mutableStateOf(false) }

  var httpLatencyMs by remember { mutableStateOf<Long?>(null) }
  var httpJitterMs by remember { mutableStateOf<Long?>(null) }
  var isRunningHttp by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("network_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text(
          text = "NETWORK QUALITY LAB",
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          ),
          color = colors.accentGold
        )
        Text(
          text = "Real connectivity diagnostics, DNS lookup speed, and HTTP latency testing",
          style = MaterialTheme.typography.bodyMedium,
          color = colors.textMuted
        )
      }
    }

    // 1. Connection Status Overview
    item {
      WafaSectionHeader(title = "Connection State & Transport")

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        WafaMetricCard(
          title = "Active Transport",
          value = network.transport,
          subvalue = if (network.isConnected) "Status: Online" else "Status: Offline",
          icon = WafaIcons.Wifi,
          accentColor = colors.accentBlue,
          modifier = Modifier.weight(1f)
        )

        WafaMetricCard(
          title = "Validation",
          value = if (network.isValidated) "Validated" else "Unconfirmed",
          subvalue = if (network.isMetered) "Metered Connection" else "Unmetered",
          icon = WafaIcons.Network,
          accentColor = if (network.isValidated) colors.statusSuccess else colors.statusWarning,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 2. IP Addresses Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(colors.cardBorder, Color.Transparent)
          )
        )
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Interface IP Addresses",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Local IPv4", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
            Text(text = network.localIpv4 ?: "Not Assigned", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Local IPv6", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
            Text(text = network.localIpv6 ?: "Not Assigned", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "VPN Active", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
            Text(text = if (network.isVpn) "Yes (Encrypted Tunnel)" else "No", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
          }
        }
      }
    }

    // 3. DNS Lookup Latency Benchmark Card
    item {
      WafaSectionHeader(title = "DNS & HTTP Quality Tests")

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(colors.cardBorder, Color.Transparent)
          )
        )
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Real DNS Resolution Test",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Resolves target 'dns.google' using system resolver and calculates actual lookup time in ms.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = colors.textMuted
          )

          if (dnsMs != null) {
            Text(
              text = "Measured DNS Lookup: ${dnsMs} ms",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = colors.accentGold
            )
          }

          WafaPrimaryButton(
            text = if (isRunningDns) "Resolving DNS..." else "Test DNS Latency",
            icon = WafaIcons.Network,
            enabled = !isRunningDns,
            onClick = {
              scope.launch {
                isRunningDns = true
                dnsMs = engine.runDnsLatencyTest("dns.google")
                isRunningDns = false
              }
            }
          )
        }
      }
    }

    // 4. HTTP Round-Trip Ping & Jitter Benchmark Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.verticalGradient(
            listOf(colors.cardBorder, Color.Transparent)
          )
        )
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "HTTP Ping Latency & Jitter Test",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Executes 3 consecutive lightweight HTTPS requests to measure round-trip time and packet jitter.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
            color = colors.textMuted
          )

          if (httpLatencyMs != null && httpJitterMs != null) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(text = "Average Latency", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                Text(text = "${httpLatencyMs} ms", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.accentBlue)
              }
              Column {
                Text(text = "Jitter (Variance)", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
                Text(text = "±${httpJitterMs} ms", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.accentGold)
              }
            }
          }

          WafaPrimaryButton(
            text = if (isRunningHttp) "Pinging Endpoint..." else "Run HTTP Latency Test",
            icon = WafaIcons.Performance,
            enabled = !isRunningHttp,
            onClick = {
              scope.launch {
                isRunningHttp = true
                val (avg, jitter) = engine.runHttpLatencyAndJitter()
                httpLatencyMs = avg
                httpJitterMs = jitter
                isRunningHttp = false
              }
            }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

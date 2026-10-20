package com.example.engine

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.model.NetworkDiagnostics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class NetworkEngine(private val context: Context) {
  private val client = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .build()

  fun getNetworkState(): NetworkDiagnostics {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val activeNetwork = cm?.activeNetwork
    val caps = activeNetwork?.let { cm.getNetworkCapabilities(it) }

    val isConnected = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val isValidated = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
    val isMetered = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == false

    val transport = when {
      caps == null -> "Disconnected"
      caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
      caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular Mobile Data"
      caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
      caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth Tethering"
      else -> "Active Connection"
    }

    val isVpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
    val (ipv4, ipv6) = getLocalIpAddresses()

    return NetworkDiagnostics(
      isConnected = isConnected,
      transport = transport,
      isValidated = isValidated,
      isMetered = isMetered,
      isVpn = isVpn,
      localIpv4 = ipv4,
      localIpv6 = ipv6
    )
  }

  suspend fun runDnsLatencyTest(targetHost: String = "dns.google"): Long? = withContext(Dispatchers.IO) {
    try {
      val start = System.nanoTime()
      val addresses = InetAddress.getAllByName(targetHost)
      val elapsedMs = (System.nanoTime() - start) / 1_000_000
      if (addresses.isNotEmpty()) elapsedMs else null
    } catch (_: Exception) {
      null
    }
  }

  suspend fun runHttpLatencyAndJitter(testUrl: String = "https://www.google.com/generate_204"): Pair<Long?, Long?> = withContext(Dispatchers.IO) {
    val latencies = mutableListOf<Long>()
    for (i in 0 until 3) {
      try {
        val request = Request.Builder().url(testUrl).build()
        val start = System.nanoTime()
        val response = client.newCall(request).execute()
        val elapsedMs = (System.nanoTime() - start) / 1_000_000
        response.close()
        latencies.add(elapsedMs)
      } catch (_: Exception) {}
    }

    if (latencies.isEmpty()) {
      return@withContext Pair(null, null)
    }

    val avg = latencies.average().toLong()
    val minL = latencies.minOrNull() ?: avg
    val maxL = latencies.maxOrNull() ?: avg
    val jitter = abs(maxL - minL)

    Pair(avg, jitter)
  }

  private fun getLocalIpAddresses(): Pair<String?, String?> {
    var ipv4: String? = null
    var ipv6: String? = null
    try {
      val interfaces = NetworkInterface.getNetworkInterfaces()
      while (interfaces.hasMoreElements()) {
        val nif = interfaces.nextElement()
        if (nif.isLoopback || !nif.isUp) continue
        val addrs = nif.inetAddresses
        while (addrs.hasMoreElements()) {
          val addr = addrs.nextElement()
          if (!addr.isLoopbackAddress) {
            if (addr is Inet4Address && ipv4 == null) {
              ipv4 = addr.hostAddress
            } else if (addr is Inet6Address && ipv6 == null) {
              val host = addr.hostAddress ?: ""
              // Strip scope id if present
              ipv6 = host.substringBefore('%')
            }
          }
        }
      }
    } catch (_: Exception) {}
    return Pair(ipv4, ipv6)
  }
}

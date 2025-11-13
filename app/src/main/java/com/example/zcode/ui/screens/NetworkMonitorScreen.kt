package com.example.zcode.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zcode.network.IPAddressHandler
import kotlinx.coroutines.delay
import java.net.NetworkInterface

/**
 * NetworkMonitorScreen - Real-time network monitoring
 */
@Composable
fun NetworkMonitorScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    var ipv4Address by remember { mutableStateOf("") }
    var ipv6Address by remember { mutableStateOf("") }
    var networkType by remember { mutableStateOf("Unknown") }
    var isConnected by remember { mutableStateOf(false) }
    var linkSpeed by remember { mutableStateOf("N/A") }
    var interfaces by remember { mutableStateOf<List<NetworkInterfaceInfo>>(emptyList()) }

    // Update network info every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            try {
                // Get IP addresses directly from network interfaces
                ipv4Address = getIPv4()
                ipv6Address = getIPv6()

                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                isConnected = network != null && capabilities != null

                networkType = when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile Data"
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
                    else -> "Unknown"
                }

                linkSpeed = capabilities?.linkDownstreamBandwidthKbps?.let {
                    "${it / 1024} Mbps"
                } ?: "N/A"

                interfaces = getNetworkInterfaces()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(2000)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Text(
                text = "Network Monitor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // Connection Status
        item {
            NetworkStatusCard(
                isConnected = isConnected,
                networkType = networkType,
                linkSpeed = linkSpeed
            )
        }

        // IP Addresses
        item {
            InfoCard(title = "IP Addresses") {
                InfoRow("IPv4", ipv4Address.ifEmpty { "Not available" })
                InfoRow("IPv6", ipv6Address.ifEmpty { "Not available" })
            }
        }

        // Network Interfaces
        item {
            Text(
                text = "Network Interfaces",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(interfaces.size) { index ->
            val iface = interfaces[index]
            NetworkInterfaceCard(iface)
        }
    }
}

@Composable
fun NetworkStatusCard(
    isConnected: Boolean,
    networkType: String,
    linkSpeed: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isConnected) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = networkType,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Speed: $linkSpeed",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.SignalWifiOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isConnected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun NetworkInterfaceCard(iface: NetworkInterfaceInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = iface.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = when {
                        iface.name.contains("wlan", ignoreCase = true) -> Icons.Default.Wifi
                        iface.name.contains("eth", ignoreCase = true) -> Icons.Default.Cable
                        iface.name.contains("rmnet", ignoreCase = true) -> Icons.Default.SignalCellularAlt
                        else -> Icons.Default.NetworkCheck
                    },
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (iface.addresses.isNotEmpty()) {
                Text(
                    text = "Addresses:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                iface.addresses.forEach { address ->
                    Text(
                        text = "  • $address",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (iface.isUp) "UP" else "DOWN",
                    color = if (iface.isUp)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "MTU: ${iface.mtu}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

data class NetworkInterfaceInfo(
    val name: String,
    val addresses: List<String>,
    val isUp: Boolean,
    val mtu: Int
)

fun getNetworkInterfaces(): List<NetworkInterfaceInfo> {
    return try {
        NetworkInterface.getNetworkInterfaces().toList().map { netInterface ->
            NetworkInterfaceInfo(
                name = netInterface.name,
                addresses = netInterface.inetAddresses.toList()
                    .map { it.hostAddress ?: "" }
                    .filter { it.isNotEmpty() },
                isUp = netInterface.isUp,
                mtu = netInterface.mtu
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun getIPv4(): String {
    return try {
        NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { ni ->
            ni.inetAddresses?.toList()?.forEach { addr ->
                if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                    return addr.hostAddress ?: ""
                }
            }
        }
        ""
    } catch (e: Exception) {
        ""
    }
}

fun getIPv6(): String {
    return try {
        NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { ni ->
            ni.inetAddresses?.toList()?.forEach { addr ->
                if (!addr.isLoopbackAddress && addr is java.net.Inet6Address) {
                    return addr.hostAddress?.split("%")?.get(0) ?: ""
                }
            }
        }
        ""
    } catch (e: Exception) {
        ""
    }
}

// InfoRow and InfoCard removed - using ones from SystemInfoScreenWorking


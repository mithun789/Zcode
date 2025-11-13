package com.example.zcode.ui.screens

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.io.File

/**
 * SystemInfoScreen - Real-time system monitoring (like htop/Task Manager)
 */
@Composable
fun SystemInfoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activityManager = remember {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    var systemInfo by remember { mutableStateOf<SystemInfoData?>(null) }
    var runningApps by remember { mutableStateOf<List<ProcessInfo>>(emptyList()) }

    // Update system info every second
    LaunchedEffect(Unit) {
        while (true) {
            systemInfo = collectSystemInfo(context, activityManager)
            runningApps = collectRunningProcesses(activityManager)
            delay(1000)
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
                text = "System Monitor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // Device Info
        item {
            InfoCard(title = "Device Information") {
                InfoRow("Manufacturer", Build.MANUFACTURER.uppercase())
                InfoRow("Model", Build.MODEL)
                InfoRow("Android", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                InfoRow("Build", Build.DISPLAY)
                InfoRow("Kernel", systemInfo?.kernelVersion ?: "Loading...")
            }
        }

        // CPU Info
        item {
            InfoCard(title = "CPU") {
                InfoRow("Cores", "${Runtime.getRuntime().availableProcessors()}")
                InfoRow("Architecture", Build.SUPPORTED_ABIS.first())
                InfoRow("Hardware", Build.HARDWARE)
                InfoRow("CPU Usage", "${systemInfo?.cpuUsage ?: 0}%")
            }
        }

        // Memory Info
        item {
            systemInfo?.let { info ->
                InfoCard(title = "Memory") {
                    InfoRow("Total RAM", formatBytes(info.totalMemory))
                    InfoRow("Available", formatBytes(info.availableMemory))
                    InfoRow("Used", formatBytes(info.totalMemory - info.availableMemory))
                    InfoRow("Usage", "${((info.totalMemory - info.availableMemory) * 100 / info.totalMemory)}%")

                    LinearProgressIndicator(
                        progress = (info.totalMemory - info.availableMemory).toFloat() / info.totalMemory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }

        // Storage Info
        item {
            systemInfo?.let { info ->
                InfoCard(title = "Storage") {
                    InfoRow("Total", formatBytes(info.totalStorage))
                    InfoRow("Free", formatBytes(info.freeStorage))
                    InfoRow("Used", formatBytes(info.totalStorage - info.freeStorage))

                    LinearProgressIndicator(
                        progress = (info.totalStorage - info.freeStorage).toFloat() / info.totalStorage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }

        // Running Processes
        item {
            Text(
                text = "Running Processes (${runningApps.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(runningApps.take(20)) { process ->
            ProcessCard(process)
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProcessCard(process: ProcessInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = process.processName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "PID: ${process.pid}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                text = formatBytes(process.memory),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

data class SystemInfoData(
    val totalMemory: Long,
    val availableMemory: Long,
    val totalStorage: Long,
    val freeStorage: Long,
    val cpuUsage: Int,
    val kernelVersion: String
)

data class ProcessInfo(
    val processName: String,
    val pid: Int,
    val memory: Long
)

fun collectSystemInfo(context: Context, activityManager: ActivityManager): SystemInfoData {
    val memInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memInfo)

    val statFile = File("/data")

    return SystemInfoData(
        totalMemory = memInfo.totalMem,
        availableMemory = memInfo.availMem,
        totalStorage = statFile.totalSpace,
        freeStorage = statFile.freeSpace,
        cpuUsage = getCpuUsage(),
        kernelVersion = getKernelVersion()
    )
}

fun collectRunningProcesses(activityManager: ActivityManager): List<ProcessInfo> {
    return try {
        activityManager.runningAppProcesses?.map { process ->
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)

            ProcessInfo(
                processName = process.processName,
                pid = process.pid,
                memory = memInfo.availMem / activityManager.runningAppProcesses.size
            )
        } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

fun getCpuUsage(): Int {
    return try {
        val reader = File("/proc/stat").bufferedReader()
        val line = reader.readLine()
        reader.close()

        val tokens = line.split("\\s+".toRegex())
        if (tokens.size >= 5) {
            val idle = tokens[4].toLong()
            val total = tokens.drop(1).take(4).sumOf { it.toLong() }
            ((total - idle) * 100 / total).toInt()
        } else 0
    } catch (e: Exception) {
        0
    }
}

fun getKernelVersion(): String {
    return try {
        // Use system property instead of /proc/version to avoid SELinux denial
        System.getProperty("os.version") ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)} KB"
        bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024))} MB"
        else -> "${"%.2f".format(bytes / (1024.0 * 1024 * 1024))} GB"
    }
}


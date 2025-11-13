package com.example.zcode.fastfetch

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * FastfetchIntegration - System information collector similar to fastfetch/neofetch
 *
 * Provides functionality to:
 * - Collect system information
 * - Format output similar to fastfetch
 * - Generate system info with custom themes
 * - Display device specifications
 */
class FastfetchIntegration(private val context: Context) {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    /**
     * Get complete system information
     *
     * @return SystemInfo object with all details
     */
    suspend fun getSystemInfo(): SystemInfo = withContext(Dispatchers.Default) {
        try {
            SystemInfo(
                osName = getOSName(),
                osVersion = getOSVersion(),
                kernelVersion = getKernelVersion(),
                deviceModel = getDeviceModel(),
                deviceManufacturer = getDeviceManufacturer(),
                cpuInfo = getCPUInfo(),
                ramInfo = getRamInfo(),
                storageInfo = getStorageInfo(),
                displayInfo = getDisplayInfo(),
                batteryInfo = getBatteryInfo(),
                buildTimestamp = Build.TIME
            )
        } catch (e: Exception) {
            throw FastfetchException("Failed to get system info: ${e.message}", e)
        }
    }

    /**
     * Get OS name
     */
    private fun getOSName(): String = "Android"

    /**
     * Get OS version
     */
    private fun getOSVersion(): String = Build.VERSION.RELEASE

    /**
     * Get kernel version
     */
    private fun getKernelVersion(): String {
        return try {
            val process = Runtime.getRuntime().exec("cat /proc/version")
            val reader = process.inputStream.bufferedReader()
            reader.readLine() ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * Get device model name
     */
    private fun getDeviceModel(): String = Build.MODEL

    /**
     * Get device manufacturer
     */
    private fun getDeviceManufacturer(): String = Build.MANUFACTURER

    /**
     * Get CPU information
     */
    private fun getCPUInfo(): CPUInfo {
        return try {
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val abiList = Build.SUPPORTED_ABIS.joinToString(", ")

            CPUInfo(
                cores = cpuCount,
                abiList = abiList,
                hardware = Build.HARDWARE
            )
        } catch (e: Exception) {
            CPUInfo(cores = 0, abiList = "Unknown", hardware = "Unknown")
        }
    }

    /**
     * Get RAM information
     */
    private fun getRamInfo(): RAMInfo {
        return try {
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory() / (1024 * 1024)
            val freeMemory = runtime.freeMemory() / (1024 * 1024)
            val maxMemory = runtime.maxMemory() / (1024 * 1024)

            RAMInfo(
                totalMB = totalMemory,
                freeMB = freeMemory,
                usedMB = totalMemory - freeMemory,
                maxMB = maxMemory
            )
        } catch (e: Exception) {
            RAMInfo(0, 0, 0, 0)
        }
    }

    /**
     * Get storage information
     */
    private fun getStorageInfo(): StorageInfo {
        return try {
            val path = context.filesDir
            val state = android.os.StatFs(path.absolutePath)

            val totalSpace = state.totalBytes / (1024 * 1024 * 1024)
            val freeSpace = state.freeBytes / (1024 * 1024 * 1024)
            val usedSpace = totalSpace - freeSpace

            StorageInfo(
                totalGB = totalSpace,
                usedGB = usedSpace,
                freeGB = freeSpace
            )
        } catch (e: Exception) {
            StorageInfo(0, 0, 0)
        }
    }

    /**
     * Get display information
     */
    private fun getDisplayInfo(): DisplayInfo {
        return try {
            val displayMetrics = context.resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            val density = displayMetrics.density

            DisplayInfo(
                resolution = "${width}x${height}",
                density = String.format(java.util.Locale.US, "%.2f", density)
            )
        } catch (e: Exception) {
            DisplayInfo("Unknown", "Unknown")
        }
    }

    /**
     * Get battery information
     */
    private fun getBatteryInfo(): BatteryInfo {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE)
                as? android.os.BatteryManager

            val battery = IntArray(2)
            val ifilter = android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)

            val level = batteryStatus?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status = batteryStatus?.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) ?: -1

            val batteryPercent = (level / scale.toFloat() * 100).toInt()
            val isCharging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING

            BatteryInfo(
                level = batteryPercent,
                isCharging = isCharging
            )
        } catch (e: Exception) {
            BatteryInfo(0, false)
        }
    }

    /**
     * Format system info as string similar to neofetch
     *
     * @param systemInfo System information
     * @return Formatted string
     */
    fun formatAsNeofetch(systemInfo: SystemInfo): String {
        return """
            |${systemInfo.deviceManufacturer} ${systemInfo.deviceModel}
            |─────────────────────────────────────
            |OS:        ${systemInfo.osName} ${systemInfo.osVersion}
            |Kernel:    ${systemInfo.kernelVersion}
            |CPU:       ${systemInfo.cpuInfo.cores} cores (${systemInfo.cpuInfo.abiList})
            |RAM:       ${systemInfo.ramInfo.usedMB}MB / ${systemInfo.ramInfo.totalMB}MB
            |Storage:   ${systemInfo.storageInfo.usedGB}GB / ${systemInfo.storageInfo.totalGB}GB
            |Display:   ${systemInfo.displayInfo.resolution} (${systemInfo.displayInfo.density}x)
            |Battery:   ${systemInfo.batteryInfo.level}% ${if (systemInfo.batteryInfo.isCharging) "⚡" else ""}
        """.trimMargin()
    }
}

/**
 * SystemInfo - Complete system information
 */
data class SystemInfo(
    val osName: String,
    val osVersion: String,
    val kernelVersion: String,
    val deviceModel: String,
    val deviceManufacturer: String,
    val cpuInfo: CPUInfo,
    val ramInfo: RAMInfo,
    val storageInfo: StorageInfo,
    val displayInfo: DisplayInfo,
    val batteryInfo: BatteryInfo,
    val buildTimestamp: Long
)

/**
 * CPUInfo - CPU information
 */
data class CPUInfo(
    val cores: Int,
    val abiList: String,
    val hardware: String
)

/**
 * RAMInfo - RAM information
 */
data class RAMInfo(
    val totalMB: Long,
    val freeMB: Long,
    val usedMB: Long,
    val maxMB: Long
)

/**
 * StorageInfo - Storage information
 */
data class StorageInfo(
    val totalGB: Long,
    val usedGB: Long,
    val freeGB: Long
)

/**
 * DisplayInfo - Display information
 */
data class DisplayInfo(
    val resolution: String,
    val density: String
)

/**
 * BatteryInfo - Battery information
 */
data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean
)

/**
 * FastfetchException - Custom exception for fastfetch operations
 */
class FastfetchException(message: String, cause: Throwable? = null) : Exception(message, cause)


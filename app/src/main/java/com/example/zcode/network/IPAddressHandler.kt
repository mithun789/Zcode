package com.example.zcode.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface

/**
 * IPAddressHandler - Retrieves and manages IP addresses
 *
 * Provides functionality to:
 * - Get IPv4 and IPv6 addresses
 * - Get device hostname
 * - Retrieve network information
 * - Query all network interfaces
 */
class IPAddressHandler(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        as? ConnectivityManager

    /**
     * Get IPv4 address
     *
     * @return IPv4 address or null if not available
     */
    suspend fun getIPv4Address(): String? = withContext(Dispatchers.Default) {
        try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { ni ->
                ni.inetAddresses?.toList()?.forEach { addr ->
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return@withContext addr.hostAddress
                    }
                }
            }
            null
        } catch (e: Exception) {
            throw IPAddressException("Failed to get IPv4 address: ${e.message}", e)
        }
    }

    /**
     * Get IPv6 address
     *
     * @return IPv6 address or null if not available
     */
    suspend fun getIPv6Address(): String? = withContext(Dispatchers.Default) {
        try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { ni ->
                ni.inetAddresses?.toList()?.forEach { addr ->
                    if (!addr.isLoopbackAddress && addr is Inet6Address) {
                        return@withContext addr.hostAddress?.split("%")?.get(0)
                    }
                }
            }
            null
        } catch (e: Exception) {
            throw IPAddressException("Failed to get IPv6 address: ${e.message}", e)
        }
    }

    /**
     * Get device hostname
     *
     * @return Device hostname
     */
    suspend fun getHostname(): String? = withContext(Dispatchers.Default) {
        try {
            InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get all network addresses with their associated network names
     *
     * @return Map of network name to list of addresses
     */
    suspend fun getAllNetworkAddresses(): Map<String, List<String>> = withContext(Dispatchers.Default) {
        try {
            val addressMap = mutableMapOf<String, List<String>>()

            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { ni ->
                if (ni.isUp) {
                    val addresses = ni.inetAddresses?.toList()
                        ?.filter { !it.isLoopbackAddress }
                        ?.mapNotNull { it.hostAddress }
                        ?: emptyList()

                    if (addresses.isNotEmpty()) {
                        addressMap[ni.name] = addresses
                    }
                }
            }

            addressMap
        } catch (e: Exception) {
            throw IPAddressException("Failed to get network addresses: ${e.message}", e)
        }
    }

    /**
     * Check if network is connected
     *
     * @return true if connected to any network
     */
    fun isNetworkConnected(): Boolean {
        return try {
            val network = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(network)

            capabilities?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check current network type
     *
     * @return Network type (WiFi, Cellular, Ethernet, None)
     */
    fun getNetworkType(): NetworkType {
        return try {
            val network = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(network)

            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkType.WIFI
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkType.CELLULAR
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkType.ETHERNET
                else -> NetworkType.NONE
            }
        } catch (e: Exception) {
            NetworkType.NONE
        }
    }

    /**
     * Get detailed network info
     *
     * @return NetworkInfo object with detailed information
     */
    suspend fun getNetworkInfo(): NetworkInfo = withContext(Dispatchers.Default) {
        try {
            NetworkInfo(
                isConnected = isNetworkConnected(),
                networkType = getNetworkType(),
                ipv4 = getIPv4Address(),
                ipv6 = getIPv6Address(),
                hostname = getHostname(),
                interfaceCount = NetworkInterface.getNetworkInterfaces()?.toList()?.size ?: 0
            )
        } catch (e: Exception) {
            throw IPAddressException("Failed to get network info: ${e.message}", e)
        }
    }
}

/**
 * NetworkType - Enum for network types
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    NONE
}

/**
 * NetworkInfo - Data class with network information
 */
data class NetworkInfo(
    val isConnected: Boolean,
    val networkType: NetworkType,
    val ipv4: String?,
    val ipv6: String?,
    val hostname: String?,
    val interfaceCount: Int
)

/**
 * IPAddressException - Custom exception for IP address operations
 */
class IPAddressException(message: String, cause: Throwable? = null) : Exception(message, cause)


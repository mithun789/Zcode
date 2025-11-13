package com.example.zcode.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * NATBridgeMode - Enum for NAT bridge modes
 */
enum class NATBridgeMode {
    IPv4,
    IPv6,
    DUAL_STACK
}

/**
 * NATBridgeManager - Manages NAT bridge configuration
 *
 * Handles:
 * - NAT mode switching (IPv4, IPv6, Dual Stack)
 * - Port forwarding rules
 * - Network translation settings
 * - Bridge status monitoring
 */
class NATBridgeManager {

    private var currentMode: NATBridgeMode = NATBridgeMode.IPv4
    private val portForwardingRules = mutableMapOf<Int, PortForwardingRule>()

    /**
     * Get current NAT mode
     */
    fun getCurrentMode(): NATBridgeMode = currentMode

    /**
     * Set NAT mode
     *
     * @param mode The NAT mode to switch to
     */
    suspend fun setNATMode(mode: NATBridgeMode) = withContext(Dispatchers.Default) {
        try {
            currentMode = mode
        } catch (e: Exception) {
            throw NATBridgeException("Failed to set NAT mode: ${e.message}", e)
        }
    }

    /**
     * Get all active port forwarding rules
     *
     * @return List of port forwarding rules
     */
    fun getPortForwardingRules(): List<PortForwardingRule> {
        return portForwardingRules.values.toList()
    }

    /**
     * Add port forwarding rule
     *
     * @param localPort Local port number
     * @param remoteHost Remote host address
     * @param remotePort Remote port number
     * @throws NATBridgeException if rule cannot be added
     */
    suspend fun addPortForwardingRule(
        localPort: Int,
        remoteHost: String,
        remotePort: Int
    ) = withContext(Dispatchers.Default) {
        try {
            validatePortNumber(localPort)
            validatePortNumber(remotePort)

            val rule = PortForwardingRule(
                localPort = localPort,
                remoteHost = remoteHost,
                remotePort = remotePort,
                isActive = true
            )

            portForwardingRules[localPort] = rule
        } catch (e: Exception) {
            throw NATBridgeException("Failed to add port forwarding rule: ${e.message}", e)
        }
    }

    /**
     * Remove port forwarding rule
     *
     * @param localPort Local port number of the rule to remove
     */
    suspend fun removePortForwardingRule(localPort: Int) = withContext(Dispatchers.Default) {
        try {
            portForwardingRules.remove(localPort)
        } catch (e: Exception) {
            throw NATBridgeException("Failed to remove port forwarding rule: ${e.message}", e)
        }
    }

    /**
     * Enable port forwarding rule
     *
     * @param localPort Local port number
     */
    suspend fun enablePortForwarding(localPort: Int) = withContext(Dispatchers.Default) {
        try {
            portForwardingRules[localPort]?.let {
                portForwardingRules[localPort] = it.copy(isActive = true)
            }
        } catch (e: Exception) {
            throw NATBridgeException("Failed to enable port forwarding: ${e.message}", e)
        }
    }

    /**
     * Disable port forwarding rule
     *
     * @param localPort Local port number
     */
    suspend fun disablePortForwarding(localPort: Int) = withContext(Dispatchers.Default) {
        try {
            portForwardingRules[localPort]?.let {
                portForwardingRules[localPort] = it.copy(isActive = false)
            }
        } catch (e: Exception) {
            throw NATBridgeException("Failed to disable port forwarding: ${e.message}", e)
        }
    }

    /**
     * Get NAT bridge status
     *
     * @return Status information
     */
    suspend fun getStatus(): NATBridgeStatus = withContext(Dispatchers.Default) {
        try {
            NATBridgeStatus(
                currentMode = currentMode,
                isActive = true,
                activePortForwardingCount = portForwardingRules.values.count { it.isActive },
                totalPortForwardingRules = portForwardingRules.size
            )
        } catch (e: Exception) {
            throw NATBridgeException("Failed to get NAT bridge status: ${e.message}", e)
        }
    }

    /**
     * Clear all port forwarding rules
     */
    suspend fun clearAllRules() = withContext(Dispatchers.Default) {
        try {
            portForwardingRules.clear()
        } catch (e: Exception) {
            throw NATBridgeException("Failed to clear port forwarding rules: ${e.message}", e)
        }
    }

    /**
     * Validate port number
     *
     * @param port Port number to validate
     * @throws IllegalArgumentException if port is invalid
     */
    private fun validatePortNumber(port: Int) {
        if (port < 0 || port > 65535) {
            throw IllegalArgumentException("Invalid port number: $port (must be 0-65535)")
        }
    }
}

/**
 * PortForwardingRule - Represents a port forwarding rule
 */
data class PortForwardingRule(
    val localPort: Int,
    val remoteHost: String,
    val remotePort: Int,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * NATBridgeStatus - Status information for NAT bridge
 */
data class NATBridgeStatus(
    val currentMode: NATBridgeMode,
    val isActive: Boolean,
    val activePortForwardingCount: Int,
    val totalPortForwardingRules: Int
)

/**
 * NATBridgeException - Custom exception for NAT bridge operations
 */
class NATBridgeException(message: String, cause: Throwable? = null) : Exception(message, cause)


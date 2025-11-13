package com.example.zcode.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * UserPreferences - Entity for storing user preferences in Room database
 *
 * Stores application settings like theme, transparency level, blur intensity, etc.
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,  // Single row of preferences

    // Theme settings
    val themeMode: String = "LIGHT",  // LIGHT, DARK, AMOLED, CUSTOM

    // Visual effects
    val blurIntensity: Float = 10f,  // 0-20 dp
    val transparencyLevel: Float = 0.95f,  // 0.0-1.0
    val glassmorphismEnabled: Boolean = true,

    // Network settings
    val natBridgeMode: String = "IPv4",  // IPv4, IPv6
    val showIPAddress: Boolean = true,

    // File explorer settings
    val defaultFolderPath: String = "/sdcard",
    val showHiddenFiles: Boolean = false,

    // Fastfetch settings
    val fastfetchTheme: String = "default",
    val showSystemInfo: Boolean = true,

    // General settings
    val fontSize: Float = 14f,
    val fontFamily: String = "monospace",
    val autoSave: Boolean = true,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


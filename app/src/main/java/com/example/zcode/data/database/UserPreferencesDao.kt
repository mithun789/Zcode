package com.example.zcode.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * UserPreferencesDao - Data Access Object for UserPreferences
 *
 * Provides database operations for reading and writing user preferences
 */
@Dao
interface UserPreferencesDao {

    /**
     * Get all user preferences as Flow for reactive updates
     */
    @Query("SELECT * FROM user_preferences LIMIT 1")
    fun getUserPreferences(): Flow<UserPreferences?>

    /**
     * Get user preferences synchronously
     */
    @Query("SELECT * FROM user_preferences LIMIT 1")
    suspend fun getUserPreferencesSync(): UserPreferences?

    /**
     * Insert new user preferences
     */
    @Insert
    suspend fun insertPreferences(preferences: UserPreferences)

    /**
     * Update existing user preferences
     */
    @Update
    suspend fun updatePreferences(preferences: UserPreferences)

    /**
     * Delete user preferences
     */
    @Delete
    suspend fun deletePreferences(preferences: UserPreferences)

    /**
     * Update theme mode
     */
    @Query("UPDATE user_preferences SET themeMode = :themeMode, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateThemeMode(themeMode: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update blur intensity
     */
    @Query("UPDATE user_preferences SET blurIntensity = :blurIntensity, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateBlurIntensity(blurIntensity: Float, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update transparency level
     */
    @Query("UPDATE user_preferences SET transparencyLevel = :transparencyLevel, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateTransparencyLevel(transparencyLevel: Float, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update glassmorphism enabled status
     */
    @Query("UPDATE user_preferences SET glassmorphismEnabled = :enabled, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateGlassmorphismEnabled(enabled: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update font size
     */
    @Query("UPDATE user_preferences SET fontSize = :fontSize, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateFontSize(fontSize: Float, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update font family
     */
    @Query("UPDATE user_preferences SET fontFamily = :fontFamily, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateFontFamily(fontFamily: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update auto save
     */
    @Query("UPDATE user_preferences SET autoSave = :autoSave, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateAutoSave(autoSave: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update NAT bridge mode
     */
    @Query("UPDATE user_preferences SET natBridgeMode = :mode, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateNATBridgeMode(mode: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update show IP address
     */
    @Query("UPDATE user_preferences SET showIPAddress = :showIPAddress, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateShowIPAddress(showIPAddress: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update default folder path
     */
    @Query("UPDATE user_preferences SET defaultFolderPath = :defaultFolderPath, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateDefaultFolderPath(defaultFolderPath: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update show hidden files
     */
    @Query("UPDATE user_preferences SET showHiddenFiles = :showHiddenFiles, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateShowHiddenFiles(showHiddenFiles: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update fastfetch theme
     */
    @Query("UPDATE user_preferences SET fastfetchTheme = :fastfetchTheme, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateFastfetchTheme(fastfetchTheme: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update show system info
     */
    @Query("UPDATE user_preferences SET showSystemInfo = :showSystemInfo, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateShowSystemInfo(showSystemInfo: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update terminal font size
     */
    @Query("UPDATE user_preferences SET terminalFontSize = :terminalFontSize, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateTerminalFontSize(terminalFontSize: Float, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update terminal theme
     */
    @Query("UPDATE user_preferences SET terminalTheme = :terminalTheme, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateTerminalTheme(terminalTheme: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update word wrap
     */
    @Query("UPDATE user_preferences SET wordWrap = :wordWrap, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateWordWrap(wordWrap: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update default distro
     */
    @Query("UPDATE user_preferences SET defaultDistro = :defaultDistro, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateDefaultDistro(defaultDistro: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update auto update packages
     */
    @Query("UPDATE user_preferences SET autoUpdatePackages = :autoUpdatePackages, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateAutoUpdatePackages(autoUpdatePackages: Boolean, updatedAt: Long = System.currentTimeMillis())

    /**
     * Update enable root access
     */
    @Query("UPDATE user_preferences SET enableRootAccess = :enableRootAccess, updatedAt = :updatedAt WHERE id = 1")
    suspend fun updateEnableRootAccess(enableRootAccess: Boolean, updatedAt: Long = System.currentTimeMillis())
}


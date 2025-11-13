package com.example.zcode.data.manager

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.zcode.data.database.AppDatabase
import com.example.zcode.data.database.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ThemeMode - Enum representing available theme modes
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    AMOLED,
    NORD,
    DRACULA,
    MONOKAI,
    SOLARIZED_LIGHT,
    SOLARIZED_DARK,
    CUSTOM
}

/**
 * ThemeManager - Manages theme switching and persistence
 *
 * Handles:
 * - Theme switching (Light, Dark, AMOLED)
 * - Theme preference persistence using Room database
 * - Color scheme management based on selected theme
 * - Real-time theme updates via Flow
 */
@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    private val dao = database.userPreferencesDao()

    /**
     * Get current theme as Flow for reactive updates
     *
     * @return Flow of ThemeMode that updates whenever theme changes
     */
    fun getCurrentThemeMode(): Flow<ThemeMode> {
        return dao.getUserPreferences().map { preferences ->
            preferences?.let {
                try {
                    ThemeMode.valueOf(it.themeMode)
                } catch (e: Exception) {
                    ThemeMode.DARK
                }
            } ?: ThemeMode.DARK
        }
    }

    /**
     * Set theme mode and persist to database
     *
     * @param themeMode The new theme mode to set
     */
    suspend fun setThemeMode(themeMode: ThemeMode) {
        try {
            val themeModeString = themeMode.name
            dao.updateThemeMode(themeModeString)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set theme mode: ${e.message}", e)
        }
    }

    /**
     * Get color scheme based on theme mode
     *
     * @param themeMode The theme mode to get colors for
     * @return ColorScheme for the given theme
     */
    fun getColorScheme(themeMode: ThemeMode): ColorScheme {
        return when (themeMode) {
            ThemeMode.LIGHT -> getLightColorScheme()
            ThemeMode.DARK -> getDarkColorScheme()
            ThemeMode.AMOLED -> getAmoledColorScheme()
            ThemeMode.NORD -> getNordColorScheme()
            ThemeMode.DRACULA -> getDraculaColorScheme()
            ThemeMode.MONOKAI -> getMonokaiColorScheme()
            ThemeMode.SOLARIZED_LIGHT -> getSolarizedLightColorScheme()
            ThemeMode.SOLARIZED_DARK -> getSolarizedDarkColorScheme()
            ThemeMode.CUSTOM -> getDarkColorScheme()
        }
    }

    /**
     * Get light theme color scheme
     */
    private fun getLightColorScheme(): ColorScheme = lightColorScheme(
        primary = Color(0xFF006C54),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF8EF8D8),
        onPrimaryContainer = Color(0xFF002019),

        secondary = Color(0xFF4A635B),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFCCE8DE),
        onSecondaryContainer = Color(0xFF061F18),

        tertiary = Color(0xFF396373),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFBCEAFD),
        onTertiaryContainer = Color(0xFF001F2A),

        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),

        background = Color(0xFFFBFDF9),
        onBackground = Color(0xFF1A1C1A),

        surface = Color(0xFFFBFDF9),
        onSurface = Color(0xFF1A1C1A),
        surfaceVariant = Color(0xFFDCE4DD),
        onSurfaceVariant = Color(0xFF45504A),

        outline = Color(0xFF75807B)
    )

    /**
     * Get dark theme color scheme
     */
    private fun getDarkColorScheme(): ColorScheme = darkColorScheme(
        primary = Color(0xFF70D8B8),
        onPrimary = Color(0xFF003729),
        primaryContainer = Color(0xFF00513D),
        onPrimaryContainer = Color(0xFF8EF8D8),

        secondary = Color(0xFFB0CDC2),
        onSecondary = Color(0xFF1D352D),
        secondaryContainer = Color(0xFF334C43),
        onSecondaryContainer = Color(0xFFCCE8DE),

        tertiary = Color(0xFFA0CEDF),
        onTertiary = Color(0xFF003D51),
        tertiaryContainer = Color(0xFF1E4F63),
        onTertiaryContainer = Color(0xFFBCEAFD),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),

        background = Color(0xFF1A1C1A),
        onBackground = Color(0xFFE2E3DF),

        surface = Color(0xFF1A1C1A),
        onSurface = Color(0xFFE2E3DF),
        surfaceVariant = Color(0xFF45504A),
        onSurfaceVariant = Color(0xFFC0C9C1),

        outline = Color(0xFF8A9387)
    )

    /**
     * Get AMOLED theme color scheme - True black for OLED displays
     */
    private fun getAmoledColorScheme(): ColorScheme = darkColorScheme(
        primary = Color(0xFF70D8B8),
        onPrimary = Color(0xFF003729),
        primaryContainer = Color(0xFF00513D),
        onPrimaryContainer = Color(0xFF8EF8D8),

        secondary = Color(0xFFB0CDC2),
        onSecondary = Color(0xFF1D352D),
        secondaryContainer = Color(0xFF334C43),
        onSecondaryContainer = Color(0xFFCCE8DE),

        tertiary = Color(0xFFA0CEDF),
        onTertiary = Color(0xFF003D51),
        tertiaryContainer = Color(0xFF1E4F63),
        onTertiaryContainer = Color(0xFFBCEAFD),

        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),

        background = Color(0xFF000000),  // True black for OLED
        onBackground = Color(0xFFE2E3DF),

        surface = Color(0xFF000000),     // True black for OLED
        onSurface = Color(0xFFE2E3DF),
        surfaceVariant = Color(0xFF45504A),
        onSurfaceVariant = Color(0xFFC0C9C1),

        outline = Color(0xFF8A9387)
    )

    /**
     * Get Nord theme color scheme
     */
    private fun getNordColorScheme(): ColorScheme = darkColorScheme(
        primary = Color(0xFF88C0D0),
        onPrimary = Color(0xFF2E3440),
        primaryContainer = Color(0xFF5E81AC),
        onPrimaryContainer = Color(0xFFD8DEE9),

        secondary = Color(0xFF81A1C1),
        onSecondary = Color(0xFF2E3440),
        secondaryContainer = Color(0xFF5E81AC),
        onSecondaryContainer = Color(0xFFECEFF4),

        background = Color(0xFF2E3440),
        onBackground = Color(0xFFECEFF4),

        surface = Color(0xFF3B4252),
        onSurface = Color(0xFFECEFF4),
        surfaceVariant = Color(0xFF434C5E),
        onSurfaceVariant = Color(0xFFD8DEE9),

        error = Color(0xFFBF616A),
        onError = Color(0xFFECEFF4)
    )

    /**
     * Get Dracula theme color scheme
     */
    private fun getDraculaColorScheme(): ColorScheme = darkColorScheme(
        primary = Color(0xFFBD93F9),
        onPrimary = Color(0xFF282A36),
        primaryContainer = Color(0xFF6272A4),
        onPrimaryContainer = Color(0xFFF8F8F2),

        secondary = Color(0xFF8BE9FD),
        onSecondary = Color(0xFF282A36),
        secondaryContainer = Color(0xFF44475A),
        onSecondaryContainer = Color(0xFFF8F8F2),

        background = Color(0xFF282A36),
        onBackground = Color(0xFFF8F8F2),

        surface = Color(0xFF44475A),
        onSurface = Color(0xFFF8F8F2),
        surfaceVariant = Color(0xFF6272A4),
        onSurfaceVariant = Color(0xFFF8F8F2),

        error = Color(0xFFFF5555),
        onError = Color(0xFFF8F8F2)
    )

    /**
     * Get Monokai theme color scheme
     */
    private fun getMonokaiColorScheme(): ColorScheme = darkColorScheme(
        primary = Color(0xFFA6E22E),
        onPrimary = Color(0xFF272822),
        primaryContainer = Color(0xFF75715E),
        onPrimaryContainer = Color(0xFFF8F8F2),

        secondary = Color(0xFF66D9EF),
        onSecondary = Color(0xFF272822),
        secondaryContainer = Color(0xFF75715E),
        onSecondaryContainer = Color(0xFFF8F8F2),

        background = Color(0xFF272822),
        onBackground = Color(0xFFF8F8F2),

        surface = Color(0xFF3E3D32),
        onSurface = Color(0xFFF8F8F2),
        surfaceVariant = Color(0xFF75715E),
        onSurfaceVariant = Color(0xFFF8F8F2),

        error = Color(0xFFF92672),
        onError = Color(0xFFF8F8F2)
    )

    /**
     * Get Solarized Light theme color scheme
     */
    private fun getSolarizedLightColorScheme(): ColorScheme = lightColorScheme(
        primary = Color(0xFF268BD2),
        onPrimary = Color(0xFFFDF6E3),
        primaryContainer = Color(0xFF93A1A1),
        onPrimaryContainer = Color(0xFF002B36),

        secondary = Color(0xFF2AA198),
        onSecondary = Color(0xFFFDF6E3),
        secondaryContainer = Color(0xFFEEE8D5),
        onSecondaryContainer = Color(0xFF073642),

        background = Color(0xFFFDF6E3),
        onBackground = Color(0xFF073642),

        surface = Color(0xFFEEE8D5),
        onSurface = Color(0xFF073642),
        surfaceVariant = Color(0xFF93A1A1),
        onSurfaceVariant = Color(0xFF002B36),

        error = Color(0xFFDC322F),
        onError = Color(0xFFFDF6E3)
    )

    /**
     * Get Solarized Dark theme color scheme
     */
    private fun getSolarizedDarkColorScheme(): ColorScheme = darkColorScheme(
        primary = Color(0xFF268BD2),
        onPrimary = Color(0xFF002B36),
        primaryContainer = Color(0xFF586E75),
        onPrimaryContainer = Color(0xFFFDF6E3),

        secondary = Color(0xFF2AA198),
        onSecondary = Color(0xFF002B36),
        secondaryContainer = Color(0xFF073642),
        onSecondaryContainer = Color(0xFFEEE8D5),

        background = Color(0xFF002B36),
        onBackground = Color(0xFFFDF6E3),

        surface = Color(0xFF073642),
        onSurface = Color(0xFFFDF6E3),
        surfaceVariant = Color(0xFF586E75),
        onSurfaceVariant = Color(0xFFEEE8D5),

        error = Color(0xFFDC322F),
        onError = Color(0xFFFDF6E3)
    )

    /**
     * Get blur intensity setting
     */
    suspend fun getBlurIntensity(): Float {
        return dao.getUserPreferencesSync()?.blurIntensity ?: 10f
    }

    /**
     * Set blur intensity and persist to database
     *
     * @param intensity Blur intensity value (0-20)
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setBlurIntensity(intensity: Float) {
        try {
            val clampedIntensity = intensity.coerceIn(0f, 20f)
            dao.updateBlurIntensity(clampedIntensity)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set blur intensity: ${e.message}", e)
        }
    }

    /**
     * Get transparency level setting
     */
    suspend fun getTransparencyLevel(): Float {
        return dao.getUserPreferencesSync()?.transparencyLevel ?: 0.95f
    }

    /**
     * Set transparency level and persist to database
     *
     * @param level Transparency level (0.0-1.0)
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setTransparencyLevel(level: Float) {
        try {
            val clampedLevel = level.coerceIn(0f, 1f)
            dao.updateTransparencyLevel(clampedLevel)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set transparency level: ${e.message}", e)
        }
    }

    /**
     * Check if glassmorphism effects are enabled
     */
    suspend fun isGlassmorphismEnabled(): Boolean {
        return dao.getUserPreferencesSync()?.glassmorphismEnabled ?: true
    }

    /**
     * Set glassmorphism enabled status
     *
     * @param enabled Whether glassmorphism should be enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setGlassmorphismEnabled(enabled: Boolean) {
        try {
            dao.updateGlassmorphismEnabled(enabled)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set glassmorphism status: ${e.message}", e)
        }
    }

    /**
     * Get font size setting
     */
    suspend fun getFontSize(): Float {
        return dao.getUserPreferencesSync()?.fontSize ?: 14f
    }

    /**
     * Set font size and persist to database
     *
     * @param fontSize Font size value
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setFontSize(fontSize: Float) {
        try {
            dao.updateFontSize(fontSize)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set font size: ${e.message}", e)
        }
    }

    /**
     * Get font family setting
     */
    suspend fun getFontFamily(): String {
        return dao.getUserPreferencesSync()?.fontFamily ?: "monospace"
    }

    /**
     * Set font family and persist to database
     *
     * @param fontFamily Font family name
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setFontFamily(fontFamily: String) {
        try {
            dao.updateFontFamily(fontFamily)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set font family: ${e.message}", e)
        }
    }

    /**
     * Get auto save setting
     */
    suspend fun getAutoSave(): Boolean {
        return dao.getUserPreferencesSync()?.autoSave ?: true
    }

    /**
     * Set auto save and persist to database
     *
     * @param autoSave Auto save enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setAutoSave(autoSave: Boolean) {
        try {
            dao.updateAutoSave(autoSave)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set auto save: ${e.message}", e)
        }
    }

    /**
     * Get NAT bridge mode setting
     */
    suspend fun getNATBridgeMode(): String {
        return dao.getUserPreferencesSync()?.natBridgeMode ?: "IPv4"
    }

    /**
     * Set NAT bridge mode and persist to database
     *
     * @param mode NAT bridge mode
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setNATBridgeMode(mode: String) {
        try {
            dao.updateNATBridgeMode(mode)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set NAT bridge mode: ${e.message}", e)
        }
    }

    /**
     * Get show IP address setting
     */
    suspend fun getShowIPAddress(): Boolean {
        return dao.getUserPreferencesSync()?.showIPAddress ?: true
    }

    /**
     * Set show IP address and persist to database
     *
     * @param showIPAddress Show IP address enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setShowIPAddress(showIPAddress: Boolean) {
        try {
            dao.updateShowIPAddress(showIPAddress)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set show IP address: ${e.message}", e)
        }
    }

    /**
     * Get default folder path setting
     */
    suspend fun getDefaultFolderPath(): String {
        return dao.getUserPreferencesSync()?.defaultFolderPath ?: "/sdcard"
    }

    /**
     * Set default folder path and persist to database
     *
     * @param defaultFolderPath Default folder path
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setDefaultFolderPath(defaultFolderPath: String) {
        try {
            dao.updateDefaultFolderPath(defaultFolderPath)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set default folder path: ${e.message}", e)
        }
    }

    /**
     * Get show hidden files setting
     */
    suspend fun getShowHiddenFiles(): Boolean {
        return dao.getUserPreferencesSync()?.showHiddenFiles ?: false
    }

    /**
     * Set show hidden files and persist to database
     *
     * @param showHiddenFiles Show hidden files enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setShowHiddenFiles(showHiddenFiles: Boolean) {
        try {
            dao.updateShowHiddenFiles(showHiddenFiles)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set show hidden files: ${e.message}", e)
        }
    }

    /**
     * Get fastfetch theme setting
     */
    suspend fun getFastfetchTheme(): String {
        return dao.getUserPreferencesSync()?.fastfetchTheme ?: "default"
    }

    /**
     * Set fastfetch theme and persist to database
     *
     * @param fastfetchTheme Fastfetch theme name
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setFastfetchTheme(fastfetchTheme: String) {
        try {
            dao.updateFastfetchTheme(fastfetchTheme)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set fastfetch theme: ${e.message}", e)
        }
    }

    /**
     * Get show system info setting
     */
    suspend fun getShowSystemInfo(): Boolean {
        return dao.getUserPreferencesSync()?.showSystemInfo ?: true
    }

    /**
     * Set show system info and persist to database
     *
     * @param showSystemInfo Show system info enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setShowSystemInfo(showSystemInfo: Boolean) {
        try {
            dao.updateShowSystemInfo(showSystemInfo)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set show system info: ${e.message}", e)
        }
    }

    /**
     * Get terminal font size setting
     */
    suspend fun getTerminalFontSize(): Float {
        return dao.getUserPreferencesSync()?.terminalFontSize ?: 12f
    }

    /**
     * Set terminal font size and persist to database
     *
     * @param terminalFontSize Terminal font size value
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setTerminalFontSize(terminalFontSize: Float) {
        try {
            dao.updateTerminalFontSize(terminalFontSize)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set terminal font size: ${e.message}", e)
        }
    }

    /**
     * Get terminal theme setting
     */
    suspend fun getTerminalTheme(): String {
        return dao.getUserPreferencesSync()?.terminalTheme ?: "default"
    }

    /**
     * Set terminal theme and persist to database
     *
     * @param terminalTheme Terminal theme name
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setTerminalTheme(terminalTheme: String) {
        try {
            dao.updateTerminalTheme(terminalTheme)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set terminal theme: ${e.message}", e)
        }
    }

    /**
     * Get word wrap setting
     */
    suspend fun getWordWrap(): Boolean {
        return dao.getUserPreferencesSync()?.wordWrap ?: true
    }

    /**
     * Set word wrap and persist to database
     *
     * @param wordWrap Word wrap enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setWordWrap(wordWrap: Boolean) {
        try {
            dao.updateWordWrap(wordWrap)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set word wrap: ${e.message}", e)
        }
    }

    /**
     * Get default distro setting
     */
    suspend fun getDefaultDistro(): String {
        return dao.getUserPreferencesSync()?.defaultDistro ?: "Ubuntu"
    }

    /**
     * Set default distro and persist to database
     *
     * @param defaultDistro Default Linux distribution
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setDefaultDistro(defaultDistro: String) {
        try {
            dao.updateDefaultDistro(defaultDistro)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set default distro: ${e.message}", e)
        }
    }

    /**
     * Get auto update packages setting
     */
    suspend fun getAutoUpdatePackages(): Boolean {
        return dao.getUserPreferencesSync()?.autoUpdatePackages ?: false
    }

    /**
     * Set auto update packages and persist to database
     *
     * @param autoUpdatePackages Auto update packages enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setAutoUpdatePackages(autoUpdatePackages: Boolean) {
        try {
            dao.updateAutoUpdatePackages(autoUpdatePackages)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set auto update packages: ${e.message}", e)
        }
    }

    /**
     * Get enable root access setting
     */
    suspend fun getEnableRootAccess(): Boolean {
        return dao.getUserPreferencesSync()?.enableRootAccess ?: false
    }

    /**
     * Get user preferences synchronously
     *
     * @return UserPreferences object or null if not found
     */
    suspend fun getUserPreferencesSync(): UserPreferences? {
        return dao.getUserPreferencesSync()
    }

    /**
     * Set enable root access and persist to database
     *
     * @param enableRootAccess Enable root access enabled
     * @throws ThemeManagerException if database operation fails
     */
    suspend fun setEnableRootAccess(enableRootAccess: Boolean) {
        try {
            dao.updateEnableRootAccess(enableRootAccess)
        } catch (e: Exception) {
            throw ThemeManagerException("Failed to set enable root access: ${e.message}", e)
        }
    }
}

/**
 * ThemeManagerException - Custom exception for ThemeManager operations
 */
class ThemeManagerException(message: String, cause: Throwable? = null) : Exception(message, cause)


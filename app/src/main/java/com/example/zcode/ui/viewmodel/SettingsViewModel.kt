package com.example.zcode.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcode.data.manager.ThemeManager
import com.example.zcode.data.manager.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsUiState - UI state for Settings screen
 */
data class SettingsUiState(
    val currentTheme: ThemeMode = ThemeMode.LIGHT,
    val blurIntensity: Float = 10f,
    val transparencyLevel: Float = 0.95f,
    val glassmorphismEnabled: Boolean = true,

    // General settings
    val fontSize: Float = 14f,
    val fontFamily: String = "monospace",
    val autoSave: Boolean = true,

    // Network settings
    val natBridgeMode: String = "IPv4",
    val showIPAddress: Boolean = true,

    // File explorer settings
    val defaultFolderPath: String = "/sdcard",
    val showHiddenFiles: Boolean = false,

    // Fastfetch settings
    val fastfetchTheme: String = "default",
    val showSystemInfo: Boolean = true,

    // Terminal settings
    val terminalFontSize: Float = 12f,
    val terminalTheme: String = "default",
    val wordWrap: Boolean = true,

    // Linux settings
    val defaultDistro: String = "Ubuntu",
    val autoUpdatePackages: Boolean = false,
    val enableRootAccess: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * SettingsViewModel - ViewModel for Settings screen
 *
 * Manages theme and preferences state using ThemeManager
 * Provides reactive updates using StateFlow
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        initializeSettings()
    }

    private fun initializeSettings() {
        viewModelScope.launch {
            try {
                // Collect theme mode updates
                themeManager.getCurrentThemeMode().collect { theme ->
                    _uiState.update { it.copy(currentTheme = theme) }
                }

                // Load other preferences
                val preferences = themeManager.getUserPreferencesSync()
                preferences?.let { prefs ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            blurIntensity = prefs.blurIntensity,
                            transparencyLevel = prefs.transparencyLevel,
                            glassmorphismEnabled = prefs.glassmorphismEnabled,
                            fontSize = prefs.fontSize,
                            fontFamily = prefs.fontFamily,
                            autoSave = prefs.autoSave,
                            natBridgeMode = prefs.natBridgeMode,
                            showIPAddress = prefs.showIPAddress,
                            defaultFolderPath = prefs.defaultFolderPath,
                            showHiddenFiles = prefs.showHiddenFiles,
                            fastfetchTheme = prefs.fastfetchTheme,
                            showSystemInfo = prefs.showSystemInfo,
                            terminalFontSize = prefs.terminalFontSize,
                            terminalTheme = prefs.terminalTheme,
                            wordWrap = prefs.wordWrap,
                            defaultDistro = prefs.defaultDistro,
                            autoUpdatePackages = prefs.autoUpdatePackages,
                            enableRootAccess = prefs.enableRootAccess
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to load settings: ${e.message}") }
            }
        }
    }

    /**
     * Change theme mode
     *
     * @param themeMode The new theme to set
     */
    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                themeManager.setThemeMode(themeMode)
                _uiState.update { it.copy(isLoading = false, currentTheme = themeMode) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to change theme: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Update blur intensity
     *
     * @param intensity New blur intensity value
     */
    fun setBlurIntensity(intensity: Float) {
        viewModelScope.launch {
            try {
                themeManager.setBlurIntensity(intensity)
                _uiState.update { it.copy(blurIntensity = intensity, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update blur: ${e.message}")
                }
            }
        }
    }

    /**
     * Update transparency level
     *
     * @param level New transparency level value
     */
    fun setTransparencyLevel(level: Float) {
        viewModelScope.launch {
            try {
                themeManager.setTransparencyLevel(level)
                _uiState.update { it.copy(transparencyLevel = level, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update transparency: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle glassmorphism effects
     *
     * @param enabled Whether to enable glassmorphism
     */
    fun setGlassmorphismEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setGlassmorphismEnabled(enabled)
                _uiState.update { it.copy(glassmorphismEnabled = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle glassmorphism: ${e.message}")
                }
            }
        }
    }

    /**
     * Update font size
     *
     * @param fontSize New font size value
     */
    fun setFontSize(fontSize: Float) {
        viewModelScope.launch {
            try {
                themeManager.setFontSize(fontSize)
                _uiState.update { it.copy(fontSize = fontSize, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update font size: ${e.message}")
                }
            }
        }
    }

    /**
     * Update font family
     *
     * @param fontFamily New font family
     */
    fun setFontFamily(fontFamily: String) {
        viewModelScope.launch {
            try {
                themeManager.setFontFamily(fontFamily)
                _uiState.update { it.copy(fontFamily = fontFamily, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update font family: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle auto save
     *
     * @param enabled Whether to enable auto save
     */
    fun setAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setAutoSave(enabled)
                _uiState.update { it.copy(autoSave = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle auto save: ${e.message}")
                }
            }
        }
    }

    /**
     * Update NAT bridge mode
     *
     * @param mode New NAT bridge mode
     */
    fun setNATBridgeMode(mode: String) {
        viewModelScope.launch {
            try {
                themeManager.setNATBridgeMode(mode)
                _uiState.update { it.copy(natBridgeMode = mode, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update NAT bridge mode: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle show IP address
     *
     * @param enabled Whether to show IP address
     */
    fun setShowIPAddress(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setShowIPAddress(enabled)
                _uiState.update { it.copy(showIPAddress = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle IP address display: ${e.message}")
                }
            }
        }
    }

    /**
     * Update default folder path
     *
     * @param path New default folder path
     */
    fun setDefaultFolderPath(path: String) {
        viewModelScope.launch {
            try {
                themeManager.setDefaultFolderPath(path)
                _uiState.update { it.copy(defaultFolderPath = path, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update default folder: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle show hidden files
     *
     * @param enabled Whether to show hidden files
     */
    fun setShowHiddenFiles(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setShowHiddenFiles(enabled)
                _uiState.update { it.copy(showHiddenFiles = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle hidden files: ${e.message}")
                }
            }
        }
    }

    /**
     * Update fastfetch theme
     *
     * @param theme New fastfetch theme
     */
    fun setFastfetchTheme(theme: String) {
        viewModelScope.launch {
            try {
                themeManager.setFastfetchTheme(theme)
                _uiState.update { it.copy(fastfetchTheme = theme, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update fastfetch theme: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle show system info
     *
     * @param enabled Whether to show system info
     */
    fun setShowSystemInfo(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setShowSystemInfo(enabled)
                _uiState.update { it.copy(showSystemInfo = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle system info: ${e.message}")
                }
            }
        }
    }

    /**
     * Update terminal font size
     *
     * @param fontSize New terminal font size
     */
    fun setTerminalFontSize(fontSize: Float) {
        viewModelScope.launch {
            try {
                themeManager.setTerminalFontSize(fontSize)
                _uiState.update { it.copy(terminalFontSize = fontSize, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update terminal font size: ${e.message}")
                }
            }
        }
    }

    /**
     * Update terminal theme
     *
     * @param theme New terminal theme
     */
    fun setTerminalTheme(theme: String) {
        viewModelScope.launch {
            try {
                themeManager.setTerminalTheme(theme)
                _uiState.update { it.copy(terminalTheme = theme, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update terminal theme: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle word wrap
     *
     * @param enabled Whether to enable word wrap
     */
    fun setWordWrap(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setWordWrap(enabled)
                _uiState.update { it.copy(wordWrap = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle word wrap: ${e.message}")
                }
            }
        }
    }

    /**
     * Update default Linux distribution
     *
     * @param distro New default distribution
     */
    fun setDefaultDistro(distro: String) {
        viewModelScope.launch {
            try {
                themeManager.setDefaultDistro(distro)
                _uiState.update { it.copy(defaultDistro = distro, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update default distribution: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle auto update packages
     *
     * @param enabled Whether to auto update packages
     */
    fun setAutoUpdatePackages(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setAutoUpdatePackages(enabled)
                _uiState.update { it.copy(autoUpdatePackages = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle auto update packages: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle enable root access
     *
     * @param enabled Whether to enable root access
     */
    fun setEnableRootAccess(enabled: Boolean) {
        viewModelScope.launch {
            try {
                themeManager.setEnableRootAccess(enabled)
                _uiState.update { it.copy(enableRootAccess = enabled, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to toggle root access: ${e.message}")
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


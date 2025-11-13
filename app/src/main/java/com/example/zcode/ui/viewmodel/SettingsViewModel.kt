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

    /**
     * Initialize settings from database
     */
    private fun initializeSettings() {
        viewModelScope.launch {
            try {
                // Initialize default preferences if needed
                themeManager.initializeDefaultPreferences()

                // Collect theme mode updates
                themeManager.getCurrentThemeMode().collect { theme ->
                    _uiState.update { it.copy(currentTheme = theme) }
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
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


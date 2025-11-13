package com.example.zcode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zcode.data.manager.ThemeMode
import com.example.zcode.ui.viewmodel.SettingsViewModel


/**
 * SettingsScreen - Application settings
 *
 * Provides UI for theme selection, preferences, and app configuration
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            item {
                SettingsHeader()
            }

            item {
                ThemeSelector(
                    currentTheme = uiState.currentTheme,
                    onThemeSelected = { viewModel.setThemeMode(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                VisualEffectsSection(
                    blurIntensity = uiState.blurIntensity,
                    transparencyLevel = uiState.transparencyLevel,
                    glassmorphismEnabled = uiState.glassmorphismEnabled,
                    onBlurIntensityChange = { viewModel.setBlurIntensity(it) },
                    onTransparencyChange = { viewModel.setTransparencyLevel(it) },
                    onGlassmorphismToggle = { viewModel.setGlassmorphismEnabled(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                if (uiState.errorMessage != null) {
                    ErrorBanner(
                        message = uiState.errorMessage!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
        }

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * SettingsHeader - Header section for settings
 */
@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Customize your Zcode experience",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * ThemeSelector - Theme selection UI
 *
 * @param currentTheme Currently selected theme
 * @param onThemeSelected Callback when theme is selected
 */
@Composable
private fun ThemeSelector(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.AMOLED).forEach { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = currentTheme == theme,
                    onClick = { onThemeSelected(theme) }
                )
            }
        }
    }
}

/**
 * ThemeCard - Individual theme selection card
 */
@Composable
private fun ThemeCard(
    theme: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        color = when (theme) {
            ThemeMode.LIGHT -> Color(0xFFFBFDF9)
            ThemeMode.DARK -> Color(0xFF1A1C1A)
            ThemeMode.AMOLED -> Color(0xFF000000)
            ThemeMode.NORD -> Color(0xFF2E3440)
            ThemeMode.DRACULA -> Color(0xFF282A36)
            ThemeMode.MONOKAI -> Color(0xFF272822)
            ThemeMode.SOLARIZED_LIGHT -> Color(0xFFFDF6E3)
            ThemeMode.SOLARIZED_DARK -> Color(0xFF002B36)
            ThemeMode.CUSTOM -> Color(0xFF4A4A4A)
        },
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (theme == ThemeMode.LIGHT) Color.Black else Color.White
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * VisualEffectsSection - Controls for visual effects
 */
@Composable
private fun VisualEffectsSection(
    blurIntensity: Float,
    transparencyLevel: Float,
    glassmorphismEnabled: Boolean,
    onBlurIntensityChange: (Float) -> Unit,
    onTransparencyChange: (Float) -> Unit,
    onGlassmorphismToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Visual Effects",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Blur Intensity Slider
        SliderWithLabel(
            label = "Blur Intensity",
            value = blurIntensity,
            onValueChange = onBlurIntensityChange,
            valueRange = 0f..20f,
            steps = 19
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Transparency Slider
        SliderWithLabel(
            label = "Transparency",
            value = transparencyLevel,
            onValueChange = onTransparencyChange,
            valueRange = 0f..1f,
            steps = 19
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Glassmorphism Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Glassmorphism Effects",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = glassmorphismEnabled,
                onCheckedChange = onGlassmorphismToggle
            )
        }
    }
}

/**
 * SliderWithLabel - Slider with label and value display
 */
@Composable
private fun SliderWithLabel(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = String.format(java.util.Locale.US, "%.2f", value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * ErrorBanner - Displays error messages
 */
@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}


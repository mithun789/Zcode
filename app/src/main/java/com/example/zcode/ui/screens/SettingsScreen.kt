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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zcode.data.manager.ThemeManager
import com.example.zcode.data.manager.ThemeMode
import com.example.zcode.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)


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
                GeneralSettingsSection(
                    fontSize = uiState.fontSize,
                    fontFamily = uiState.fontFamily,
                    autoSave = uiState.autoSave,
                    onFontSizeChange = { viewModel.setFontSize(it) },
                    onFontFamilyChange = { viewModel.setFontFamily(it) },
                    onAutoSaveToggle = { viewModel.setAutoSave(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                NetworkSettingsSection(
                    natBridgeMode = uiState.natBridgeMode,
                    showIPAddress = uiState.showIPAddress,
                    onNATBridgeModeChange = { viewModel.setNATBridgeMode(it) },
                    onShowIPAddressToggle = { viewModel.setShowIPAddress(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                FileExplorerSettingsSection(
                    defaultFolderPath = uiState.defaultFolderPath,
                    showHiddenFiles = uiState.showHiddenFiles,
                    onDefaultFolderChange = { viewModel.setDefaultFolderPath(it) },
                    onShowHiddenFilesToggle = { viewModel.setShowHiddenFiles(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                FastfetchSettingsSection(
                    fastfetchTheme = uiState.fastfetchTheme,
                    showSystemInfo = uiState.showSystemInfo,
                    onFastfetchThemeChange = { viewModel.setFastfetchTheme(it) },
                    onShowSystemInfoToggle = { viewModel.setShowSystemInfo(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                TerminalSettingsSection(
                    terminalFontSize = uiState.terminalFontSize,
                    terminalTheme = uiState.terminalTheme,
                    wordWrap = uiState.wordWrap,
                    onTerminalFontSizeChange = { viewModel.setTerminalFontSize(it) },
                    onTerminalThemeChange = { viewModel.setTerminalTheme(it) },
                    onWordWrapToggle = { viewModel.setWordWrap(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                LinuxSettingsSection(
                    defaultDistro = uiState.defaultDistro,
                    autoUpdatePackages = uiState.autoUpdatePackages,
                    enableRootAccess = uiState.enableRootAccess,
                    onDefaultDistroChange = { viewModel.setDefaultDistro(it) },
                    onAutoUpdatePackagesToggle = { viewModel.setAutoUpdatePackages(it) },
                    onEnableRootAccessToggle = { viewModel.setEnableRootAccess(it) }
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

        // Group themes in rows for better organization
        val themeRows = listOf(
            listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.AMOLED),
            listOf(ThemeMode.NORD, ThemeMode.DRACULA, ThemeMode.MONOKAI),
            listOf(ThemeMode.SOLARIZED_LIGHT, ThemeMode.SOLARIZED_DARK)
        )

        themeRows.forEach { rowThemes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowThemes.forEach { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = currentTheme == theme,
                        onClick = { onThemeSelected(theme) },
                        modifier = Modifier.weight(1f)
                    )
                }
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
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
                    text = theme.name.replace("_", " "),
                    style = MaterialTheme.typography.labelMedium,
                    color = when (theme) {
                        ThemeMode.LIGHT -> Color.Black
                        ThemeMode.SOLARIZED_LIGHT -> Color(0xFF073642)
                        else -> Color.White
                    }
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
 * SectionHeader - Header for settings sections
 */
@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * SettingToggle - Toggle switch for boolean settings
 */
@Composable
private fun SettingToggle(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * FontFamilySelector - Dropdown for font family selection
 */
@Composable
private fun FontFamilySelector(
    selectedFont: String,
    onFontSelected: (String) -> Unit
) {
    val fontFamilies = listOf("monospace", "sans-serif", "serif", "cursive", "fantasy")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedFont,
            onValueChange = {},
            readOnly = true,
            label = { Text("Font Family") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fontFamilies.forEach { font ->
                DropdownMenuItem(
                    text = { Text(font) },
                    onClick = {
                        onFontSelected(font)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * NATBridgeModeSelector - Dropdown for NAT bridge mode selection
 */
@Composable
private fun NATBridgeModeSelector(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    val modes = listOf("IPv4", "IPv6", "Dual Stack")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedMode,
            onValueChange = {},
            readOnly = true,
            label = { Text("NAT Bridge Mode") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            modes.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode) },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * FastfetchThemeSelector - Dropdown for fastfetch theme selection
 */
@Composable
private fun FastfetchThemeSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("default", "minimal", "detailed", "colorful", "ascii")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedTheme,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fastfetch Theme") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themes.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme) },
                    onClick = {
                        onThemeSelected(theme)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * TerminalThemeSelector - Dropdown for terminal theme selection
 */
@Composable
private fun TerminalThemeSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("default", "dark", "light", "solarized-dark", "solarized-light", "dracula", "monokai")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedTheme,
            onValueChange = {},
            readOnly = true,
            label = { Text("Terminal Theme") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            themes.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme) },
                    onClick = {
                        onThemeSelected(theme)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * LinuxDistroSelector - Dropdown for Linux distribution selection
 */
@Composable
private fun LinuxDistroSelector(
    selectedDistro: String,
    onDistroSelected: (String) -> Unit
) {
    val distros = listOf("Ubuntu", "Debian", "Fedora", "Arch Linux", "Alpine", "CentOS", "openSUSE")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedDistro,
            onValueChange = {},
            readOnly = true,
            label = { Text("Default Distribution") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            distros.forEach { distro ->
                DropdownMenuItem(
                    text = { Text(distro) },
                    onClick = {
                        onDistroSelected(distro)
                        expanded = false
                    }
                )
            }
        }
    }
}
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
 * GeneralSettingsSection - General application preferences
 */
@Composable
private fun GeneralSettingsSection(
    fontSize: Float,
    fontFamily: String,
    autoSave: Boolean,
    onFontSizeChange: (Float) -> Unit,
    onFontFamilyChange: (String) -> Unit,
    onAutoSaveToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "General",
            icon = Icons.Filled.Settings
        )

        // Font Size Slider
        SliderWithLabel(
            label = "Font Size",
            value = fontSize,
            onValueChange = onFontSizeChange,
            valueRange = 10f..24f,
            steps = 13
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Font Family Dropdown
        FontFamilySelector(
            selectedFont = fontFamily,
            onFontSelected = onFontFamilyChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Auto Save Toggle
        SettingToggle(
            title = "Auto Save",
            subtitle = "Automatically save changes",
            checked = autoSave,
            onCheckedChange = onAutoSaveToggle
        )
    }
}
@Composable
private fun NetworkSettingsSection(
    natBridgeMode: String,
    showIPAddress: Boolean,
    onNATBridgeModeChange: (String) -> Unit,
    onShowIPAddressToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "Network",
            icon = Icons.Filled.Wifi
        )

        // NAT Bridge Mode
        NATBridgeModeSelector(
            selectedMode = natBridgeMode,
            onModeSelected = onNATBridgeModeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show IP Address Toggle
        SettingToggle(
            title = "Show IP Address",
            subtitle = "Display current IP address in network monitor",
            checked = showIPAddress,
            onCheckedChange = onShowIPAddressToggle
        )
    }
}

/**
 * FileExplorerSettingsSection - File explorer preferences
 */
@Composable
private fun FileExplorerSettingsSection(
    defaultFolderPath: String,
    showHiddenFiles: Boolean,
    onDefaultFolderChange: (String) -> Unit,
    onShowHiddenFilesToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "File Explorer",
            icon = Icons.Filled.Folder
        )

        // Default Folder Path
        OutlinedTextField(
            value = defaultFolderPath,
            onValueChange = onDefaultFolderChange,
            label = { Text("Default Folder") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show Hidden Files Toggle
        SettingToggle(
            title = "Show Hidden Files",
            subtitle = "Display files and folders starting with '.'",
            checked = showHiddenFiles,
            onCheckedChange = onShowHiddenFilesToggle
        )
    }
}

/**
 * FastfetchSettingsSection - Fastfetch display preferences
 */
@Composable
private fun FastfetchSettingsSection(
    fastfetchTheme: String,
    showSystemInfo: Boolean,
    onFastfetchThemeChange: (String) -> Unit,
    onShowSystemInfoToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "Fastfetch",
            icon = Icons.Filled.Info
        )

        // Fastfetch Theme Dropdown
        FastfetchThemeSelector(
            selectedTheme = fastfetchTheme,
            onThemeSelected = onFastfetchThemeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show System Info Toggle
        SettingToggle(
            title = "Show System Info",
            subtitle = "Display detailed system information",
            checked = showSystemInfo,
            onCheckedChange = onShowSystemInfoToggle
        )
    }
}

/**
 * TerminalSettingsSection - Terminal emulator preferences
 */
@Composable
private fun TerminalSettingsSection(
    terminalFontSize: Float,
    terminalTheme: String,
    wordWrap: Boolean,
    onTerminalFontSizeChange: (Float) -> Unit,
    onTerminalThemeChange: (String) -> Unit,
    onWordWrapToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "Terminal",
            icon = Icons.Filled.Terminal
        )

        // Terminal Font Size Slider
        SliderWithLabel(
            label = "Terminal Font Size",
            value = terminalFontSize,
            onValueChange = onTerminalFontSizeChange,
            valueRange = 8f..20f,
            steps = 11
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terminal Theme Dropdown
        TerminalThemeSelector(
            selectedTheme = terminalTheme,
            onThemeSelected = onTerminalThemeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Word Wrap Toggle
        SettingToggle(
            title = "Word Wrap",
            subtitle = "Wrap long lines in terminal",
            checked = wordWrap,
            onCheckedChange = onWordWrapToggle
        )
    }
}

/**
 * LinuxSettingsSection - Linux environment preferences
 */
@Composable
private fun LinuxSettingsSection(
    defaultDistro: String,
    autoUpdatePackages: Boolean,
    enableRootAccess: Boolean,
    onDefaultDistroChange: (String) -> Unit,
    onAutoUpdatePackagesToggle: (Boolean) -> Unit,
    onEnableRootAccessToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionHeader(
            title = "Linux Environments",
            icon = Icons.Filled.Computer
        )

        // Default Distribution Dropdown
        LinuxDistroSelector(
            selectedDistro = defaultDistro,
            onDistroSelected = onDefaultDistroChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Auto Update Packages Toggle
        SettingToggle(
            title = "Auto Update Packages",
            subtitle = "Automatically update packages in Linux environments",
            checked = autoUpdatePackages,
            onCheckedChange = onAutoUpdatePackagesToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Enable Root Access Toggle
        SettingToggle(
            title = "Enable Root Access",
            subtitle = "Allow root access in Linux environments (advanced)",
            checked = enableRootAccess,
            onCheckedChange = onEnableRootAccessToggle
        )
    }
}
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


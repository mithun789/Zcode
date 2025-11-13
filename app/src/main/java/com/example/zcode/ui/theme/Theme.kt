package com.example.zcode.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Zcode Color Palette - Material Design 3
 *
 * Light theme colors
 */
private val LightColorScheme = lightColorScheme(
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
 * Dark theme colors
 */
private val DarkColorScheme = darkColorScheme(
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
 * AMOLED theme colors - True black for OLED displays
 */
private val AmoledColorScheme = darkColorScheme(
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

    background = Color(0xFF000000),  // True black
    onBackground = Color(0xFFE2E3DF),

    surface = Color(0xFF000000),     // True black
    onSurface = Color(0xFFE2E3DF),
    surfaceVariant = Color(0xFF45504A),
    onSurfaceVariant = Color(0xFFC0C9C1),

    outline = Color(0xFF8A9387)
)

/**
 * ZcodeTheme - Material Design 3 theme for Zcode Terminal Emulator
 *
 * @param darkTheme Whether to use dark theme
 * @param amoledTheme Whether to use AMOLED theme (overrides darkTheme)
 * @param content The content to display with this theme
 */
@Composable
fun ZcodeTheme(
    darkTheme: Boolean = false,
    amoledTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        amoledTheme -> AmoledColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZcodeTypography,
        shapes = ZcodeShapes,
        content = content
    )
}


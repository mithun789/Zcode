package com.example.zcode.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Extended Color Themes for Zcode
 */

// Dracula Theme
private val DraculaBackground = Color(0xFF282A36)
private val DraculaForeground = Color(0xFFF8F8F2)
private val DraculaPurple = Color(0xFFBD93F9)
private val DraculaPink = Color(0xFFFF79C6)
private val DraculaGreen = Color(0xFF50FA7B)
private val DraculaOrange = Color(0xFFFFB86C)

val DraculaColorScheme = darkColorScheme(
    primary = DraculaPurple,
    secondary = DraculaPink,
    tertiary = DraculaGreen,
    background = DraculaBackground,
    surface = Color(0xFF44475A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DraculaForeground,
    onSurface = DraculaForeground
)

// Monokai Theme
private val MonokaiBackground = Color(0xFF272822)
private val MonokaiForeground = Color(0xFFF8F8F2)
private val MonokaiPink = Color(0xFFF92672)
private val MonokaiGreen = Color(0xFFA6E22E)
private val MonokaiYellow = Color(0xFFE6DB74)
private val MonokaiPurple = Color(0xFFAE81FF)

val MonokaiColorScheme = darkColorScheme(
    primary = MonokaiPink,
    secondary = MonokaiGreen,
    tertiary = MonokaiPurple,
    background = MonokaiBackground,
    surface = Color(0xFF3E3D32),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = MonokaiForeground,
    onSurface = MonokaiForeground
)

// Solarized Dark Theme
private val SolarizedBase03 = Color(0xFF002B36)
private val SolarizedBase02 = Color(0xFF073642)
private val SolarizedBase01 = Color(0xFF586E75)
private val SolarizedBase0 = Color(0xFF839496)
private val SolarizedBase1 = Color(0xFF93A1A1)
private val SolarizedBlue = Color(0xFF268BD2)
private val SolarizedCyan = Color(0xFF2AA198)
private val SolarizedGreen = Color(0xFF859900)

val SolarizedDarkColorScheme = darkColorScheme(
    primary = SolarizedBlue,
    secondary = SolarizedCyan,
    tertiary = SolarizedGreen,
    background = SolarizedBase03,
    surface = SolarizedBase02,
    onPrimary = SolarizedBase03,
    onSecondary = SolarizedBase03,
    onBackground = SolarizedBase0,
    onSurface = SolarizedBase1
)

// Nord Theme
private val NordPolarNight0 = Color(0xFF2E3440)
private val NordPolarNight1 = Color(0xFF3B4252)
private val NordSnowStorm0 = Color(0xFFECEFF4)
private val NordFrost1 = Color(0xFF88C0D0)
private val NordFrost2 = Color(0xFF81A1C1)
private val NordFrost3 = Color(0xFF5E81AC)
private val NordAurora1 = Color(0xFFBF616A)

val NordColorScheme = darkColorScheme(
    primary = NordFrost2,
    secondary = NordFrost1,
    tertiary = NordAurora1,
    background = NordPolarNight0,
    surface = NordPolarNight1,
    onPrimary = NordPolarNight0,
    onSecondary = NordPolarNight0,
    onBackground = NordSnowStorm0,
    onSurface = NordSnowStorm0
)

// Gruvbox Dark Theme
private val GruvboxBg = Color(0xFF282828)
private val GruvboxBg1 = Color(0xFF3C3836)
private val GruvboxFg = Color(0xFFEBDBB2)
private val GruvboxRed = Color(0xFFFB4934)
private val GruvboxGreen = Color(0xFFB8BB26)
private val GruvboxYellow = Color(0xFFFABD2F)
private val GruvboxBlue = Color(0xFF83A598)
private val GruvboxPurple = Color(0xFFD3869B)

val GruvboxColorScheme = darkColorScheme(
    primary = GruvboxBlue,
    secondary = GruvboxGreen,
    tertiary = GruvboxPurple,
    background = GruvboxBg,
    surface = GruvboxBg1,
    onPrimary = GruvboxBg,
    onSecondary = GruvboxBg,
    onBackground = GruvboxFg,
    onSurface = GruvboxFg
)

// One Dark Theme (Atom)
private val OneDarkBg = Color(0xFF282C34)
private val OneDarkBgHighlight = Color(0xFF2C323C)
private val OneDarkFg = Color(0xFFABB2BF)
private val OneDarkRed = Color(0xFFE06C75)
private val OneDarkGreen = Color(0xFF98C379)
private val OneDarkBlue = Color(0xFF61AFEF)
private val OneDarkPurple = Color(0xFFC678DD)

val OneDarkColorScheme = darkColorScheme(
    primary = OneDarkBlue,
    secondary = OneDarkGreen,
    tertiary = OneDarkPurple,
    background = OneDarkBg,
    surface = OneDarkBgHighlight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = OneDarkFg,
    onSurface = OneDarkFg
)

// Tokyo Night Theme
private val TokyoNightBg = Color(0xFF1A1B26)
private val TokyoNightBgDark = Color(0xFF16161E)
private val TokyoNightFg = Color(0xFFC0CAF5)
private val TokyoNightBlue = Color(0xFF7AA2F7)
private val TokyoNightCyan = Color(0xFF7DCFFF)
private val TokyoNightMagenta = Color(0xFFBB9AF7)
private val TokyoNightGreen = Color(0xFF9ECE6A)

val TokyoNightColorScheme = darkColorScheme(
    primary = TokyoNightBlue,
    secondary = TokyoNightCyan,
    tertiary = TokyoNightMagenta,
    background = TokyoNightBg,
    surface = TokyoNightBgDark,
    onPrimary = Color.White,
    onSecondary = TokyoNightBg,
    onBackground = TokyoNightFg,
    onSurface = TokyoNightFg
)

// Catppuccin Mocha Theme
private val CatppuccinBase = Color(0xFF1E1E2E)
private val CatppuccinMantle = Color(0xFF181825)
private val CatppuccinText = Color(0xFFCDD6F4)
private val CatppuccinBlue = Color(0xFF89B4FA)
private val CatppuccinPink = Color(0xFFF5C2E7)
private val CatppuccinMauve = Color(0xFFCBA6F7)
private val CatppuccinGreen = Color(0xFFA6E3A1)

val CatppuccinColorScheme = darkColorScheme(
    primary = CatppuccinBlue,
    secondary = CatppuccinPink,
    tertiary = CatppuccinMauve,
    background = CatppuccinBase,
    surface = CatppuccinMantle,
    onPrimary = CatppuccinBase,
    onSecondary = CatppuccinBase,
    onBackground = CatppuccinText,
    onSurface = CatppuccinText
)

/**
 * Theme enum for easy selection
 */
enum class ZcodeTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    AMOLED("AMOLED Black"),
    DRACULA("Dracula"),
    MONOKAI("Monokai"),
    SOLARIZED_DARK("Solarized Dark"),
    NORD("Nord"),
    GRUVBOX("Gruvbox"),
    ONE_DARK("One Dark"),
    TOKYO_NIGHT("Tokyo Night"),
    CATPPUCCIN("Catppuccin Mocha")
}


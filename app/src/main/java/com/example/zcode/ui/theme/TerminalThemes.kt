package com.example.zcode.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Terminal Color Schemes - Classic terminal themes
 */

// Solarized Light Terminal
object SolarizedLightTerminal {
    val background = Color(0xFFFDF6E3)
    val foreground = Color(0xFF657B83)
    val cursor = Color(0xFF586E75)
    val black = Color(0xFF073642)
    val red = Color(0xFFDC322F)
    val green = Color(0xFF859900)
    val yellow = Color(0xFFB58900)
    val blue = Color(0xFF268BD2)
    val magenta = Color(0xFFD33682)
    val cyan = Color(0xFF2AA198)
    val white = Color(0xFFEEE8D5)
}

// Solarized Dark Terminal
object SolarizedDarkTerminal {
    val background = Color(0xFF002B36)
    val foreground = Color(0xFF839496)
    val cursor = Color(0xFF93A1A1)
    val black = Color(0xFF073642)
    val red = Color(0xFFDC322F)
    val green = Color(0xFF859900)
    val yellow = Color(0xFFB58900)
    val blue = Color(0xFF268BD2)
    val magenta = Color(0xFFD33682)
    val cyan = Color(0xFF2AA198)
    val white = Color(0xFFEEE8D5)
}

// Dracula Terminal
object DraculaTerminal {
    val background = Color(0xFF282A36)
    val foreground = Color(0xFFF8F8F2)
    val cursor = Color(0xFFF8F8F2)
    val black = Color(0xFF21222C)
    val red = Color(0xFFFF5555)
    val green = Color(0xFF50FA7B)
    val yellow = Color(0xFFF1FA8C)
    val blue = Color(0xFFBD93F9)
    val magenta = Color(0xFFFF79C6)
    val cyan = Color(0xFF8BE9FD)
    val white = Color(0xFFF8F8F2)
}

// Monokai Terminal
object MonokaiTerminal {
    val background = Color(0xFF272822)
    val foreground = Color(0xFFF8F8F2)
    val cursor = Color(0xFFF8F8F0)
    val black = Color(0xFF272822)
    val red = Color(0xFFF92672)
    val green = Color(0xFFA6E22E)
    val yellow = Color(0xFFF4BF75)
    val blue = Color(0xFF66D9EF)
    val magenta = Color(0xFFAE81FF)
    val cyan = Color(0xFFA1EFE4)
    val white = Color(0xFFF8F8F2)
}

// Nord Terminal
object NordTerminal {
    val background = Color(0xFF2E3440)
    val foreground = Color(0xFFD8DEE9)
    val cursor = Color(0xFFD8DEE9)
    val black = Color(0xFF3B4252)
    val red = Color(0xFFBF616A)
    val green = Color(0xFFA3BE8C)
    val yellow = Color(0xFFEBCB8B)
    val blue = Color(0xFF81A1C1)
    val magenta = Color(0xFFB48EAD)
    val cyan = Color(0xFF88C0D0)
    val white = Color(0xFFE5E9F0)
}

// Gruvbox Dark Terminal
object GruvboxTerminal {
    val background = Color(0xFF282828)
    val foreground = Color(0xFFEBDBB2)
    val cursor = Color(0xFFFE8019)
    val black = Color(0xFF282828)
    val red = Color(0xFFCC241D)
    val green = Color(0xFF98971A)
    val yellow = Color(0xFFD79921)
    val blue = Color(0xFF458588)
    val magenta = Color(0xFFB16286)
    val cyan = Color(0xFF689D6A)
    val white = Color(0xFFA89984)
}

// One Dark Terminal
object OneDarkTerminal {
    val background = Color(0xFF282C34)
    val foreground = Color(0xFFABB2BF)
    val cursor = Color(0xFF528BFF)
    val black = Color(0xFF282C34)
    val red = Color(0xFFE06C75)
    val green = Color(0xFF98C379)
    val yellow = Color(0xFFE5C07B)
    val blue = Color(0xFF61AFEF)
    val magenta = Color(0xFFC678DD)
    val cyan = Color(0xFF56B6C2)
    val white = Color(0xFFABB2BF)
}

// Tokyo Night Terminal
object TokyoNightTerminal {
    val background = Color(0xFF1A1B26)
    val foreground = Color(0xFFC0CAF5)
    val cursor = Color(0xFFC0CAF5)
    val black = Color(0xFF15161E)
    val red = Color(0xFFF7768E)
    val green = Color(0xFF9ECE6A)
    val yellow = Color(0xFFE0AF68)
    val blue = Color(0xFF7AA2F7)
    val magenta = Color(0xFFBB9AF7)
    val cyan = Color(0xFF7DCFFF)
    val white = Color(0xFFA9B1D6)
}

// Catppuccin Mocha Terminal
object CatppuccinTerminal {
    val background = Color(0xFF1E1E2E)
    val foreground = Color(0xFFCDD6F4)
    val cursor = Color(0xFFF5E0DC)
    val black = Color(0xFF45475A)
    val red = Color(0xFFF38BA8)
    val green = Color(0xFFA6E3A1)
    val yellow = Color(0xFFF9E2AF)
    val blue = Color(0xFF89B4FA)
    val magenta = Color(0xFFF5C2E7)
    val cyan = Color(0xFF94E2D5)
    val white = Color(0xFFBAC2DE)
}

// Matrix (Green on Black)
object MatrixTerminal {
    val background = Color(0xFF000000)
    val foreground = Color(0xFF00FF00)
    val cursor = Color(0xFF00FF00)
    val black = Color(0xFF000000)
    val red = Color(0xFF00AA00)
    val green = Color(0xFF00FF00)
    val yellow = Color(0xFF88FF00)
    val blue = Color(0xFF00FFAA)
    val magenta = Color(0xFF00FFFF)
    val cyan = Color(0xFF00FFAA)
    val white = Color(0xFFAAFFAA)
}

// High Contrast (for accessibility)
object HighContrastTerminal {
    val background = Color.Black
    val foreground = Color.White
    val cursor = Color.Yellow
    val black = Color.Black
    val red = Color.Red
    val green = Color.Green
    val yellow = Color.Yellow
    val blue = Color.Blue
    val magenta = Color.Magenta
    val cyan = Color.Cyan
    val white = Color.White
}

/**
 * Terminal theme data class
 */
data class TerminalTheme(
    val name: String,
    val background: Color,
    val foreground: Color,
    val cursor: Color,
    val black: Color,
    val red: Color,
    val green: Color,
    val yellow: Color,
    val blue: Color,
    val magenta: Color,
    val cyan: Color,
    val white: Color
)

/**
 * Available terminal themes
 */
object TerminalThemes {
    val themes = listOf(
        TerminalTheme(
            "Dracula",
            DraculaTerminal.background,
            DraculaTerminal.foreground,
            DraculaTerminal.cursor,
            DraculaTerminal.black,
            DraculaTerminal.red,
            DraculaTerminal.green,
            DraculaTerminal.yellow,
            DraculaTerminal.blue,
            DraculaTerminal.magenta,
            DraculaTerminal.cyan,
            DraculaTerminal.white
        ),
        TerminalTheme(
            "Monokai",
            MonokaiTerminal.background,
            MonokaiTerminal.foreground,
            MonokaiTerminal.cursor,
            MonokaiTerminal.black,
            MonokaiTerminal.red,
            MonokaiTerminal.green,
            MonokaiTerminal.yellow,
            MonokaiTerminal.blue,
            MonokaiTerminal.magenta,
            MonokaiTerminal.cyan,
            MonokaiTerminal.white
        ),
        TerminalTheme(
            "Nord",
            NordTerminal.background,
            NordTerminal.foreground,
            NordTerminal.cursor,
            NordTerminal.black,
            NordTerminal.red,
            NordTerminal.green,
            NordTerminal.yellow,
            NordTerminal.blue,
            NordTerminal.magenta,
            NordTerminal.cyan,
            NordTerminal.white
        ),
        TerminalTheme(
            "Gruvbox",
            GruvboxTerminal.background,
            GruvboxTerminal.foreground,
            GruvboxTerminal.cursor,
            GruvboxTerminal.black,
            GruvboxTerminal.red,
            GruvboxTerminal.green,
            GruvboxTerminal.yellow,
            GruvboxTerminal.blue,
            GruvboxTerminal.magenta,
            GruvboxTerminal.cyan,
            GruvboxTerminal.white
        ),
        TerminalTheme(
            "One Dark",
            OneDarkTerminal.background,
            OneDarkTerminal.foreground,
            OneDarkTerminal.cursor,
            OneDarkTerminal.black,
            OneDarkTerminal.red,
            OneDarkTerminal.green,
            OneDarkTerminal.yellow,
            OneDarkTerminal.blue,
            OneDarkTerminal.magenta,
            OneDarkTerminal.cyan,
            OneDarkTerminal.white
        ),
        TerminalTheme(
            "Tokyo Night",
            TokyoNightTerminal.background,
            TokyoNightTerminal.foreground,
            TokyoNightTerminal.cursor,
            TokyoNightTerminal.black,
            TokyoNightTerminal.red,
            TokyoNightTerminal.green,
            TokyoNightTerminal.yellow,
            TokyoNightTerminal.blue,
            TokyoNightTerminal.magenta,
            TokyoNightTerminal.cyan,
            TokyoNightTerminal.white
        ),
        TerminalTheme(
            "Solarized Dark",
            SolarizedDarkTerminal.background,
            SolarizedDarkTerminal.foreground,
            SolarizedDarkTerminal.cursor,
            SolarizedDarkTerminal.black,
            SolarizedDarkTerminal.red,
            SolarizedDarkTerminal.green,
            SolarizedDarkTerminal.yellow,
            SolarizedDarkTerminal.blue,
            SolarizedDarkTerminal.magenta,
            SolarizedDarkTerminal.cyan,
            SolarizedDarkTerminal.white
        ),
        TerminalTheme(
            "Catppuccin",
            CatppuccinTerminal.background,
            CatppuccinTerminal.foreground,
            CatppuccinTerminal.cursor,
            CatppuccinTerminal.black,
            CatppuccinTerminal.red,
            CatppuccinTerminal.green,
            CatppuccinTerminal.yellow,
            CatppuccinTerminal.blue,
            CatppuccinTerminal.magenta,
            CatppuccinTerminal.cyan,
            CatppuccinTerminal.white
        ),
        TerminalTheme(
            "Matrix",
            MatrixTerminal.background,
            MatrixTerminal.foreground,
            MatrixTerminal.cursor,
            MatrixTerminal.black,
            MatrixTerminal.red,
            MatrixTerminal.green,
            MatrixTerminal.yellow,
            MatrixTerminal.blue,
            MatrixTerminal.magenta,
            MatrixTerminal.cyan,
            MatrixTerminal.white
        ),
        TerminalTheme(
            "High Contrast",
            HighContrastTerminal.background,
            HighContrastTerminal.foreground,
            HighContrastTerminal.cursor,
            HighContrastTerminal.black,
            HighContrastTerminal.red,
            HighContrastTerminal.green,
            HighContrastTerminal.yellow,
            HighContrastTerminal.blue,
            HighContrastTerminal.magenta,
            HighContrastTerminal.cyan,
            HighContrastTerminal.white
        )
    )

    fun getThemeByName(name: String): TerminalTheme? {
        return themes.find { it.name.equals(name, ignoreCase = true) }
    }
}


package com.example.zcode.terminal

import android.content.Context
import android.os.Build

/**
 * WelcomeScreen - Displays fastfetch-style system information on terminal startup
 */
object WelcomeScreen {

    fun getWelcomeMessage(context: Context): String {
        return buildString {
            appendLine(getAsciiArt())
            appendLine()
            appendLine(getSystemInfo(context))
            appendLine()
            appendLine("Type 'help' for available commands")
            appendLine("Type 'pkg install <package>' to install software")
            appendLine()
        }
    }

    private fun getAsciiArt(): String {
        return """
            ╔════════════════════════════════╗
            ║                                ║
            ║     ███████╗ ██████╗ ██████╗  ║
            ║     ╚══███╔╝██╔════╝██╔═══██╗ ║
            ║       ███╔╝ ██║     ██║   ██║ ║
            ║      ███╔╝  ██║     ██║   ██║ ║
            ║     ███████╗╚██████╗╚██████╔╝ ║
            ║     ╚══════╝ ╚═════╝ ╚═════╝  ║
            ║                                ║
            ║    Terminal Emulator v1.0      ║
            ║                                ║
            ╚════════════════════════════════╝
        """.trimIndent()
    }

    private fun getSystemInfo(context: Context): String {
        return buildString {
            appendLine("┌─────────────────────────────────┐")
            appendLine("│ 📱 Device Information          │")
            appendLine("├─────────────────────────────────┤")
            appendLine("│ OS:        Android ${Build.VERSION.RELEASE}")
            appendLine("│ Device:    ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("│ API:       ${Build.VERSION.SDK_INT}")
            appendLine("│ CPU:       ${Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"}")
            appendLine("│ Shell:     bash (Ubuntu)")
            appendLine("│ Terminal:  Zcode v1.0")
            appendLine("└─────────────────────────────────┘")
        }
    }

    fun getMinimalPrompt(): String {
        return """
            ╔══════════════════════════╗
            ║   Zcode Terminal v1.0    ║
            ║   Type commands below    ║
            ╚══════════════════════════╝
            
        """.trimIndent()
    }
}


package com.example.zcode.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView
import java.io.File

/**
 * TerminalScreen - Enhanced terminal emulator with Linux environment
 *
 * Provides a functional Linux shell environment with proper I/O handling
 */
@Composable
fun TerminalScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var terminalSession by remember { mutableStateOf<TerminalSession?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Get current theme colors - optimized for terminal
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cursorColor = MaterialTheme.colorScheme.primary

    // Initialize terminal session
    LaunchedEffect(Unit) {
        try {
            // Setup directories
            val homeDir = File(context.filesDir, "zcode")
            if (!homeDir.exists()) {
                homeDir.mkdirs()
            }

            // Get the best available shell
            val shellPath = com.termux.terminal.JNI.getAvailableShell()

            // Create terminal session with built-in command interpreter
            val session = TerminalSession(
                context = context,
                changeCallback = object : TerminalSession.SessionChangedCallback {
                    override fun onTextChanged(session: TerminalSession) {
                        // Terminal content changed - handled by TerminalView
                    }

                    override fun onTitleChanged(session: TerminalSession) {
                        // Terminal title changed
                    }

                    override fun onSessionFinished(session: TerminalSession) {
                        // Terminal session ended
                        terminalSession = null
                        isLoading = false
                    }

                    override fun onClipboardText(session: TerminalSession, text: String) {
                        // Clipboard operation
                    }

                    override fun onBell(session: TerminalSession) {
                        // Bell character received
                    }

                    override fun onColorsChanged(session: TerminalSession) {
                        // Color scheme changed
                    }
                }
            )

            terminalSession = session
            isLoading = false

        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
            isLoading = false
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            terminalSession?.finish()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                // Loading screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Starting Linux Environment...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Setting up shell and environment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            terminalSession == null -> {
                // Error screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Terminal Failed to Start",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Could not initialize shell environment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            else -> {
                // Terminal view
                TerminalView(
                    session = terminalSession,
                    modifier = Modifier.fillMaxSize(),
                    textColor = textColor,
                    backgroundColor = backgroundColor,
                    cursorColor = cursorColor,
                    fontSize = 14
                )
            }
        }
    }
}


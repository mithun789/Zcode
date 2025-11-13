package com.example.zcode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.zcode.terminal.CommandHistoryManager
import com.example.zcode.terminal.CommonCommands
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView
import kotlinx.coroutines.launch
import java.io.File

/**
 * Enhanced TerminalScreen with command history and auto-complete
 */
@Composable
fun TerminalScreenEnhanced(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var terminalSession by remember { mutableStateOf<TerminalSession?>(null) }
    var currentInput by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    // Get current theme colors - optimized for terminal
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cursorColor = MaterialTheme.colorScheme.primary

    // Initialize terminal session
    LaunchedEffect(Unit) {
        try {
            val filesDir = context.filesDir
            val homeDir = File(filesDir, "home")
            if (!homeDir.exists()) {
                homeDir.mkdirs()
            }

            val session = TerminalSession(
                context = context,
                changeCallback = object : TerminalSession.SessionChangedCallback {
                    override fun onTextChanged(session: TerminalSession) {}
                    override fun onTitleChanged(session: TerminalSession) {}
                    override fun onSessionFinished(session: TerminalSession) {
                        terminalSession = null
                    }
                    override fun onClipboardText(session: TerminalSession, text: String) {}
                    override fun onBell(session: TerminalSession) {}
                    override fun onColorsChanged(session: TerminalSession) {}
                }
            )

            terminalSession = session
            session.writeText("echo 'Zcode Terminal - Type commands below'\n")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Update suggestions when input changes
    LaunchedEffect(currentInput) {
        if (currentInput.isNotBlank()) {
            val prefix = currentInput.trim()
            suggestions = CommonCommands.getSuggestions(prefix)
            showSuggestions = suggestions.isNotEmpty()
        } else {
            showSuggestions = false
            suggestions = emptyList()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            terminalSession?.finish()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Terminal output
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TerminalView(
                session = terminalSession,
                textColor = textColor,
                backgroundColor = backgroundColor,
                cursorColor = cursorColor,
                fontSize = 14
            )
        }

        // Auto-complete suggestions
        if (showSuggestions && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                LazyColumn {
                    items(suggestions) { suggestion ->
                        Text(
                            text = suggestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentInput = suggestion
                                    terminalSession?.writeText(suggestion)
                                    showSuggestions = false
                                }
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Divider()
                    }
                }
            }
        }

        // Command input bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type command...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Send button
                Button(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            terminalSession?.writeText("$currentInput\n")
                            scope.launch {
                                // Add to history
                            }
                            currentInput = ""
                            showSuggestions = false
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}


package com.example.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.zcode.terminal.SessionManager
import com.example.zcode.ui.components.CompactExtraKeysRow
import com.example.zcode.ui.components.ExtraKeysRow
import com.example.zcode.ui.components.SessionTabs
import com.example.zcode.ui.theme.TerminalTheme
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView

/**
 * EnhancedTerminalScreen - Terminal screen with Termux-like features
 *
 * Features:
 * - Multiple session tabs
 * - Extra keys row for special characters
 * - Session management
 * - Full Termux-style UI/UX with Zcode theming
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTerminalScreen(
    modifier: Modifier = Modifier,
    onNavigateToLinux: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Session manager
    val sessionManager = remember { SessionManager(context) }
    val sessions by sessionManager.sessions.collectAsState()
    val activeSessionIndex by sessionManager.activeSessionIndex.collectAsState()
    val activeSession = sessionManager.getActiveSession()
    
    // UI State
    var showExtraKeys by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    var extraKeysCompact by remember { mutableStateOf(false) }
    
    // Initialize first session
    LaunchedEffect(Unit) {
        if (sessions.isEmpty()) {
            sessionManager.createNewSession(
                title = "Terminal 1",
                changeCallback = object : TerminalSession.SessionChangedCallback {
                    override fun onTextChanged(changedSession: TerminalSession) {
                        // Handle text change
                    }
                    
                    override fun onSessionFinished(finishedSession: TerminalSession) {
                        // Handle session finished
                    }
                }
            )
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Terminal")
                    }
                },
                actions = {
                    // Extra keys toggle
                    IconButton(onClick = { showExtraKeys = !showExtraKeys }) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = if (showExtraKeys) "Hide Extra Keys" else "Show Extra Keys",
                            tint = if (showExtraKeys) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Menu
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Linux Environments") },
                            onClick = {
                                showMenu = false
                                onNavigateToLinux()
                            },
                            leadingIcon = { Icon(Icons.Default.Computer, contentDescription = null) }
                        )
                        
                        DropdownMenuItem(
                            text = { Text(if (extraKeysCompact) "Expanded Keys" else "Compact Keys") },
                            onClick = {
                                extraKeysCompact = !extraKeysCompact
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.ViewCompact, contentDescription = null) }
                        )
                        
                        Divider()
                        
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Session tabs
            if (sessions.size > 1) {
                SessionTabs(
                    sessions = sessions,
                    activeSessionIndex = activeSessionIndex,
                    onSessionClick = { index ->
                        sessionManager.switchToSession(index)
                    },
                    onCloseSession = { index ->
                        sessionManager.closeSession(index)
                    },
                    onNewSession = {
                        sessionManager.createNewSession(
                            title = "Terminal ${sessions.size + 1}",
                            changeCallback = object : TerminalSession.SessionChangedCallback {
                                override fun onTextChanged(changedSession: TerminalSession) {}
                                override fun onSessionFinished(finishedSession: TerminalSession) {}
                            }
                        )
                    }
                )
            }
            
            // Terminal view
            Box(modifier = Modifier.weight(1f)) {
                if (activeSession != null) {
                    TerminalView(
                        session = activeSession.session,
                        modifier = Modifier.fillMaxSize(),
                        textColor = androidx.compose.ui.graphics.Color.White,
                        backgroundColor = androidx.compose.ui.graphics.Color.Black,
                        cursorColor = androidx.compose.ui.graphics.Color.Green,
                        fontSize = 14
                    )
                } else {
                    // No session - show empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Terminal,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Active Session",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                sessionManager.createNewSession(
                                    title = "Terminal 1",
                                    changeCallback = object : TerminalSession.SessionChangedCallback {
                                        override fun onTextChanged(changedSession: TerminalSession) {}
                                        override fun onSessionFinished(finishedSession: TerminalSession) {}
                                    }
                                )
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New Session")
                        }
                    }
                }
            }
            
            // Extra keys row
            if (showExtraKeys) {
                if (extraKeysCompact) {
                    CompactExtraKeysRow(
                        onKeyClick = { key ->
                            activeSession?.session?.write(key)
                        }
                    )
                } else {
                    ExtraKeysRow(
                        onKeyClick = { key ->
                            activeSession?.session?.write(key)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Session info display
 */
@Composable
private fun SessionInfoBar(
    session: SessionManager.SessionInfo,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.bodySmall
            )
            
            session.environment?.let { env ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = env.name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

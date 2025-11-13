package com.example.zcode.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcode.data.manager.ThemeManager
import com.example.zcode.linux.LinuxEnvironmentManager
import com.example.zcode.ui.theme.TerminalTheme
import com.example.zcode.ui.theme.TerminalThemes
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject
import kotlin.Result

/**
 * TerminalScreen - Enhanced terminal emulator with Linux environment support
 *
 * Provides a functional Linux shell environment with support for multiple distributions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    modifier: Modifier = Modifier,
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var terminalSession by remember { mutableStateOf<TerminalSession?>(null) }
    var showEnvironmentSelector by remember { mutableStateOf(false) }

    // Linux environment management
    val currentEnvironment by viewModel.currentEnvironment.collectAsState()
    val environments by viewModel.environments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val terminalTheme by viewModel.terminalTheme.collectAsState()

    // Get current theme colors - use terminal theme if available
    val themeColors = terminalTheme?.let { theme ->
        Triple(theme.foreground, theme.background, theme.cursor)
    } ?: run {
        // Fallback to system theme
        val isDarkTheme = isSystemInDarkTheme()
        Triple(
            if (isDarkTheme) Color.White else Color.Black, // textColor
            if (isDarkTheme) Color.Black else Color.White, // backgroundColor
            MaterialTheme.colorScheme.primary // cursorColor
        )
    }

    val (textColor, backgroundColor, cursorColor) = themeColors

    // Initialize terminal session
    LaunchedEffect(currentEnvironment) {
        try {
            // Clean up existing session
            terminalSession?.finish()
            terminalSession = null

            if (currentEnvironment != null) {
                // Create terminal session connected to the Linux environment
                terminalSession = createTerminalSessionForEnvironment(context, currentEnvironment!!, viewModel)
            } else {
                // No environment selected, use built-in terminal
                terminalSession = createBuiltInTerminalSession(context)
            }

        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
            terminalSession = createBuiltInTerminalSession(context)
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            terminalSession?.finish()
            currentEnvironment?.let { viewModel.stopEnvironment(it) }
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
                    // Environment selector
                    TextButton(
                        onClick = { showEnvironmentSelector = true }
                    ) {
                        Icon(Icons.Default.Computer, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentEnvironment?.distribution?.displayName ?: "Built-in",
                            maxLines = 1
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
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
                        currentEnvironment?.let { env ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Environment: ${env.distribution.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showEnvironmentSelector = true }) {
                            Icon(Icons.Default.Computer, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Environment")
                        }
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

    // Environment selector dialog
    if (showEnvironmentSelector) {
        EnvironmentSelectorDialog(
            environments = environments,
            currentEnvironment = currentEnvironment,
            onDismiss = { showEnvironmentSelector = false },
            onEnvironmentSelected = { environment ->
                viewModel.setCurrentEnvironment(environment)
                showEnvironmentSelector = false
            },
            onCreateNew = {
                showEnvironmentSelector = false
                // Navigate to Linux environments screen
                // This would need to be handled by the parent navigation
            }
        )
    }
}

/**
 * Create terminal session for a Linux environment
 */
private fun createTerminalSessionForEnvironment(
    context: android.content.Context,
    environment: LinuxEnvironmentManager.Environment,
    viewModel: TerminalViewModel
): TerminalSession {
    // Start the Linux environment process
    val processResult = runBlocking {
        viewModel.startEnvironmentAndGetProcess(environment)
    }

    return if (processResult.isSuccess) {
        val process = processResult.getOrNull()
        if (process != null) {
            // Connect to real Linux process
            TerminalSession(
                context = context,
                changeCallback = object : TerminalSession.SessionChangedCallback {
                    override fun onTextChanged(session: TerminalSession) {}
                    override fun onTitleChanged(session: TerminalSession) {}
                    override fun onSessionFinished(session: TerminalSession) {}
                    override fun onClipboardText(session: TerminalSession, text: String) {}
                    override fun onBell(session: TerminalSession) {}
                    override fun onColorsChanged(session: TerminalSession) {}
                },
                process = process
            )
        } else {
            // Process is null - show error
            val session = TerminalSession(
                context = context,
                changeCallback = object : TerminalSession.SessionChangedCallback {
                    override fun onTextChanged(session: TerminalSession) {}
                    override fun onTitleChanged(session: TerminalSession) {}
                    override fun onSessionFinished(session: TerminalSession) {}
                    override fun onClipboardText(session: TerminalSession, text: String) {}
                    override fun onBell(session: TerminalSession) {}
                    override fun onColorsChanged(session: TerminalSession) {}
                }
            )
            session.appendSystemMessage("Failed to connect to Linux environment.\nPossible issues: PRoot binary failed to download, insufficient permissions, or network error.\n\nTry creating a new environment or using the built-in terminal.")
            session
        }
    } else {
        // Error starting environment
        val session = TerminalSession(
            context = context,
            changeCallback = object : TerminalSession.SessionChangedCallback {
                override fun onTextChanged(session: TerminalSession) {}
                override fun onTitleChanged(session: TerminalSession) {}
                override fun onSessionFinished(session: TerminalSession) {}
                override fun onClipboardText(session: TerminalSession, text: String) {}
                override fun onBell(session: TerminalSession) {}
                override fun onColorsChanged(session: TerminalSession) {}
            }
        )
        session.appendSystemMessage("Error: ${processResult.exceptionOrNull()?.message ?: "Unknown error"}\n\nFailed to start Linux environment.\nCheck network connectivity and try again.")
        session
    }
}

/**
 * Create built-in terminal session (fallback)
 */
private fun createBuiltInTerminalSession(context: android.content.Context): TerminalSession {
    val session = TerminalSession(
        context = context,
        changeCallback = object : TerminalSession.SessionChangedCallback {
            override fun onTextChanged(session: TerminalSession) {}
            override fun onTitleChanged(session: TerminalSession) {}
            override fun onSessionFinished(session: TerminalSession) {}
            override fun onClipboardText(session: TerminalSession, text: String) {}
            override fun onBell(session: TerminalSession) {}
            override fun onColorsChanged(session: TerminalSession) {}
        }
    )

    session.appendSystemMessage("No Linux environment selected.\nTap the environment selector button to create or connect to a Linux environment.\n\nAvailable built-in commands: help, ls, pwd, cd, echo, mkdir, touch, rm, date, whoami, uname, clear")
    return session
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnvironmentSelectorDialog(
    environments: List<LinuxEnvironmentManager.Environment>,
    currentEnvironment: LinuxEnvironmentManager.Environment?,
    onDismiss: () -> Unit,
    onEnvironmentSelected: (LinuxEnvironmentManager.Environment?) -> Unit,
    onCreateNew: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Linux Environment") },
        text = {
            Column {
                Text(
                    text = "Choose a Linux environment to use:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Built-in option
                    item {
                        EnvironmentSelectorItem(
                            name = "Built-in Terminal",
                            description = "Basic command interpreter",
                            isSelected = currentEnvironment == null,
                            onSelect = { onEnvironmentSelected(null) }
                        )
                    }

                    // Linux environments
                    items(environments) { environment ->
                        EnvironmentSelectorItem(
                            name = environment.distribution.displayName,
                            description = "${environment.id} ${if (environment.isRunning) "(Running)" else ""}",
                            isSelected = environment.id == currentEnvironment?.id,
                            onSelect = { onEnvironmentSelected(environment) }
                        )
                    }

                    // Create new option
                    item {
                        Card(
                            onClick = onCreateNew,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Create New Environment",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Install a new Linux distribution",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnvironmentSelectorItem(
    name: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val linuxManager: LinuxEnvironmentManager,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _environments = MutableStateFlow<List<LinuxEnvironmentManager.Environment>>(emptyList())
    val environments: StateFlow<List<LinuxEnvironmentManager.Environment>> = _environments.asStateFlow()

    private val _currentEnvironment = MutableStateFlow<LinuxEnvironmentManager.Environment?>(null)
    val currentEnvironment: StateFlow<LinuxEnvironmentManager.Environment?> = _currentEnvironment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _terminalTheme = MutableStateFlow<TerminalTheme?>(null)
    val terminalTheme: StateFlow<TerminalTheme?> = _terminalTheme.asStateFlow()

    init {
        loadEnvironments()
        loadTerminalTheme()
    }

    private fun loadEnvironments() {
        viewModelScope.launch {
            _environments.value = linuxManager.environments.value
            _currentEnvironment.value = linuxManager.currentEnvironment.value
        }
    }

    private fun loadTerminalTheme() {
        viewModelScope.launch {
            try {
                val themeName = themeManager.getTerminalTheme()
                val theme = TerminalThemes.getThemeByName(themeName)
                _terminalTheme.value = theme ?: TerminalThemes.themes.first() // Default to first theme
            } catch (e: Exception) {
                // Use default theme on error
                _terminalTheme.value = TerminalThemes.themes.first()
            }
        }
    }

    fun setTerminalTheme(themeName: String) {
        viewModelScope.launch {
            try {
                themeManager.setTerminalTheme(themeName)
                loadTerminalTheme() // Reload theme
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun setCurrentEnvironment(environment: LinuxEnvironmentManager.Environment?) {
        viewModelScope.launch {
            linuxManager.setCurrentEnvironment(environment)
            _currentEnvironment.value = environment
        }
    }

    fun startEnvironment(environment: LinuxEnvironmentManager.Environment) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                linuxManager.startEnvironment(environment)
                loadEnvironments() // Refresh the list
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun stopEnvironment(environment: LinuxEnvironmentManager.Environment) {
        viewModelScope.launch {
            linuxManager.stopEnvironment(environment)
            loadEnvironments() // Refresh the list
        }
    }

    fun startEnvironmentAndGetProcess(environment: LinuxEnvironmentManager.Environment): Result<Process> {
        return try {
            val result = runBlocking {
                linuxManager.startEnvironment(environment)
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


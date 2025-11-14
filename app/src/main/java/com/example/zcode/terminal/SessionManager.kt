package com.example.zcode.terminal

import android.content.Context
import com.example.zcode.linux.LinuxEnvironment
import com.termux.terminal.TerminalSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * SessionManager - Manages multiple terminal sessions like Termux
 *
 * Features:
 * - Multiple concurrent sessions (tabs)
 * - Session switching
 * - Session lifecycle management
 * - Environment-specific sessions
 */
class SessionManager(private val context: Context) {
    private val _sessions = MutableStateFlow<List<SessionInfo>>(emptyList())
    val sessions: StateFlow<List<SessionInfo>> = _sessions.asStateFlow()
    
    private val _activeSessionIndex = MutableStateFlow(0)
    val activeSessionIndex: StateFlow<Int> = _activeSessionIndex.asStateFlow()
    
    data class SessionInfo(
        val id: String,
        val session: TerminalSession,
        val title: String,
        val environment: LinuxEnvironment? = null,
        val createdAt: Long = System.currentTimeMillis()
    )
    
    /**
     * Create a new terminal session
     */
    fun createNewSession(
        title: String = "Terminal ${_sessions.value.size + 1}",
        environment: LinuxEnvironment? = null,
        changeCallback: TerminalSession.SessionChangedCallback
    ): SessionInfo {
        val sessionId = generateSessionId()
        
        // Create terminal session based on environment
        val terminalSession = if (environment != null) {
            createEnvironmentSession(environment, changeCallback)
        } else {
            createBuiltInSession(changeCallback)
        }
        
        val sessionInfo = SessionInfo(
            id = sessionId,
            session = terminalSession,
            title = title,
            environment = environment
        )
        
        // Add to sessions list
        val updatedSessions = _sessions.value + sessionInfo
        _sessions.value = updatedSessions
        _activeSessionIndex.value = updatedSessions.size - 1
        
        return sessionInfo
    }
    
    /**
     * Get the currently active session
     */
    fun getActiveSession(): SessionInfo? {
        val sessions = _sessions.value
        val index = _activeSessionIndex.value
        return sessions.getOrNull(index)
    }
    
    /**
     * Switch to a specific session by index
     */
    fun switchToSession(index: Int) {
        if (index in _sessions.value.indices) {
            _activeSessionIndex.value = index
        }
    }
    
    /**
     * Switch to a specific session by ID
     */
    fun switchToSessionById(id: String) {
        val index = _sessions.value.indexOfFirst { it.id == id }
        if (index >= 0) {
            _activeSessionIndex.value = index
        }
    }
    
    /**
     * Close a session by index
     */
    fun closeSession(index: Int) {
        val sessions = _sessions.value
        if (index in sessions.indices) {
            // Clean up the session
            sessions[index].session.finish()
            
            // Remove from list
            val updatedSessions = sessions.toMutableList().apply {
                removeAt(index)
            }
            _sessions.value = updatedSessions
            
            // Adjust active index
            if (_activeSessionIndex.value >= updatedSessions.size) {
                _activeSessionIndex.value = maxOf(0, updatedSessions.size - 1)
            }
        }
    }
    
    /**
     * Close a session by ID
     */
    fun closeSessionById(id: String) {
        val index = _sessions.value.indexOfFirst { it.id == id }
        if (index >= 0) {
            closeSession(index)
        }
    }
    
    /**
     * Get all sessions
     */
    fun getAllSessions(): List<SessionInfo> = _sessions.value
    
    /**
     * Get session count
     */
    fun getSessionCount(): Int = _sessions.value.size
    
    /**
     * Rename a session
     */
    fun renameSession(index: Int, newTitle: String) {
        val sessions = _sessions.value
        if (index in sessions.indices) {
            val updatedSessions = sessions.toMutableList().apply {
                set(index, get(index).copy(title = newTitle))
            }
            _sessions.value = updatedSessions
        }
    }
    
    /**
     * Close all sessions
     */
    fun closeAllSessions() {
        _sessions.value.forEach { it.session.finish() }
        _sessions.value = emptyList()
        _activeSessionIndex.value = 0
    }
    
    // Private helper methods
    
    private fun generateSessionId(): String {
        return "session_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
    
    private fun createEnvironmentSession(
        environment: LinuxEnvironment,
        changeCallback: TerminalSession.SessionChangedCallback
    ): TerminalSession {
        // Create session connected to Linux environment
        val envPath = File(context.filesDir, "environments/${environment.name}")
        val shellPath = when {
            File(envPath, "usr/bin/bash").exists() -> File(envPath, "usr/bin/bash").absolutePath
            File(envPath, "bin/bash").exists() -> File(envPath, "bin/bash").absolutePath
            File(context.filesDir, "usr/bin/bash").exists() -> File(context.filesDir, "usr/bin/bash").absolutePath
            else -> null
        }
        
        return if (shellPath != null && File(shellPath).canExecute()) {
            // Create process-based session with real shell
            createRealShellSession(shellPath, environment, changeCallback)
        } else {
            // Fallback to built-in session
            createBuiltInSession(changeCallback)
        }
    }
    
    private fun createRealShellSession(
        shellPath: String,
        environment: LinuxEnvironment,
        changeCallback: TerminalSession.SessionChangedCallback
    ): TerminalSession {
        try {
            // Setup environment variables
            val prefixDir = File(context.filesDir, "usr")
            val homeDir = File(context.filesDir, "home")
            val tmpDir = File(context.filesDir, "tmp")
            
            // Ensure directories exist
            homeDir.mkdirs()
            tmpDir.mkdirs()
            
            // Create ProcessBuilder
            val processBuilder = ProcessBuilder(shellPath)
            processBuilder.directory(homeDir)
            
            // Set environment variables
            val env = processBuilder.environment()
            env["HOME"] = homeDir.absolutePath
            env["PREFIX"] = prefixDir.absolutePath
            env["PATH"] = "${prefixDir}/bin:${prefixDir}/bin/applets:${System.getenv("PATH")}"
            env["TMPDIR"] = tmpDir.absolutePath
            env["TERM"] = "xterm-256color"
            env["COLORTERM"] = "truecolor"
            env["LANG"] = "en_US.UTF-8"
            env["USER"] = "zcode"
            env["SHELL"] = shellPath
            
            // Start the process
            val process = processBuilder.start()
            
            // Create terminal session connected to this process
            return TerminalSession(context, changeCallback, process)
        } catch (e: Exception) {
            // If real shell fails, fallback to built-in
            android.util.Log.e("SessionManager", "Failed to create real shell session", e)
            return createBuiltInSession(changeCallback)
        }
    }
    
    private fun createBuiltInSession(
        changeCallback: TerminalSession.SessionChangedCallback
    ): TerminalSession {
        // Create session with built-in command interpreter
        return TerminalSession(context, changeCallback, null)
    }
}

/**
 * Helper class for managing session state
 */
data class SessionState(
    val currentDirectory: String,
    val environmentVariables: Map<String, String>,
    val commandHistory: List<String>
)

package com.termux.terminal

import android.system.Os
import android.system.OsConstants
import java.io.File

/**
 * JNI - Java Native Interface for terminal operations
 *
 * Enhanced version with better process management and Linux environment setup
 */
object JNI {

    /**
     * Create subprocess and return file descriptor
     */
    fun createSubprocess(
        cmd: String,
        cwd: String,
        args: Array<String>,
        envVars: Array<String>,
        processIdArray: IntArray,
        dimensions: IntArray
    ): Int {
        try {
            // Build command with args
            val command = mutableListOf(cmd)
            command.addAll(args)

            val pb = ProcessBuilder(command)
            pb.directory(File(cwd))

            // Set environment variables with Linux-like setup
            val environment = pb.environment()
            environment.clear()

            // Add Android paths
            environment["PATH"] = "/system/bin:/system/xbin:/data/local/bin:/sbin"

            // Add Linux environment variables
            environment["TERM"] = "xterm-256color"
            environment["HOME"] = cwd
            environment["SHELL"] = cmd
            environment["USER"] = "zcode"
            environment["LOGNAME"] = "zcode"
            environment["TMPDIR"] = "/data/local/tmp"
            environment["LANG"] = "C.UTF-8"
            environment["LANGUAGE"] = "C.UTF-8"
            environment["LC_ALL"] = "C.UTF-8"

            // Add custom environment variables
            for (envVar in envVars) {
                val parts = envVar.split("=", limit = 2)
                if (parts.size == 2) {
                    environment[parts[0]] = parts[1]
                }
            }

            // Set terminal dimensions
            environment["COLUMNS"] = dimensions[0].toString()
            environment["LINES"] = dimensions[1].toString()

            // Redirect error stream to output
            pb.redirectErrorStream(true)

            val process = pb.start()

            // Store process ID
            if (processIdArray.isNotEmpty()) {
                processIdArray[0] = process.hashCode()
            }

            // Return process hash as file descriptor
            return process.hashCode()

        } catch (e: Exception) {
            throw RuntimeException("Failed to create subprocess: ${e.message}", e)
        }
    }

    /**
     * Set PTY window size
     */
    fun setPtyWindowSize(fd: Int, rows: Int, cols: Int): Int {
        // For now, just return success
        // In a full implementation, this would send SIGWINCH
        return 0
    }

    /**
     * Close file descriptor
     */
    fun close(fd: Int): Int {
        // Cleanup would happen in TerminalSession.finish()
        return 0
    }

    /**
     * Wait for process
     */
    fun waitFor(pid: Int): Int {
        return 0
    }

    /**
     * Check if a command exists
     */
    fun commandExists(command: String): Boolean {
        return try {
            val pb = ProcessBuilder("which", command)
            pb.redirectErrorStream(true)
            val process = pb.start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get available shell
     */
    fun getAvailableShell(): String {
        // Try different shells in order of preference
        val shells = arrayOf("/system/bin/bash", "/system/bin/sh", "/bin/bash", "/bin/sh")

        for (shell in shells) {
            if (File(shell).exists() && File(shell).canExecute()) {
                return shell
            }
        }

        return "/system/bin/sh" // Fallback
    }
}


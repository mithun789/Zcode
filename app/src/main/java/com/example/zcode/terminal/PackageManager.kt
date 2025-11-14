package com.example.zcode.terminal

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * PackageManager - Wrapper for apt/pkg commands to install packages
 *
 * Provides real package management capabilities by executing actual apt/pkg binaries
 * from the Termux bootstrap environment.
 */
class PackageManager(private val context: Context) {
    
    companion object {
        private const val TAG = "PackageManager"
    }
    
    private val prefixDir = File(context.filesDir, "usr")
    private val homeDir = File(context.filesDir, "home")
    private val tmpDir = File(context.filesDir, "tmp")
    
    private val aptPath = File(prefixDir, "bin/apt")
    private val pkgPath = File(prefixDir, "bin/pkg")
    
    /**
     * Check if package manager is installed
     */
    fun isInstalled(): Boolean {
        return aptPath.exists() || pkgPath.exists()
    }
    
    /**
     * Execute apt command
     */
    suspend fun executeApt(
        args: List<String>,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        executePackageCommand("apt", args, onOutput, onError)
    }
    
    /**
     * Execute pkg command
     */
    suspend fun executePkg(
        args: List<String>,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        executePackageCommand("pkg", args, onOutput, onError)
    }
    
    /**
     * Execute a package manager command
     */
    private suspend fun executePackageCommand(
        command: String,
        args: List<String>,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val cmdPath = File(prefixDir, "bin/$command")
            
            if (!cmdPath.exists()) {
                val error = "Package manager '$command' not found. Please install Termux bootstrap first."
                onError(error)
                return@withContext Result.failure(Exception(error))
            }
            
            // Build command
            val fullCommand = mutableListOf(cmdPath.absolutePath)
            fullCommand.addAll(args)
            
            Log.d(TAG, "Executing: ${fullCommand.joinToString(" ")}")
            
            // Create process
            val processBuilder = ProcessBuilder(fullCommand)
            processBuilder.directory(homeDir)
            
            // Set environment
            val env = processBuilder.environment()
            env.clear()
            env["HOME"] = homeDir.absolutePath
            env["PREFIX"] = prefixDir.absolutePath
            env["TMPDIR"] = tmpDir.absolutePath
            env["PATH"] = "${prefixDir.absolutePath}/bin:/system/bin:/system/xbin"
            env["LD_LIBRARY_PATH"] = "${prefixDir.absolutePath}/lib"
            env["TERM"] = "xterm-256color"
            env["DEBIAN_FRONTEND"] = "noninteractive" // For non-interactive apt
            
            // Start process
            val process = processBuilder.start()
            
            // Read output in separate threads
            val outputReader = Thread {
                try {
                    BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            onOutput(line!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading output", e)
                }
            }
            
            val errorReader = Thread {
                try {
                    BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            onError(line!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading error stream", e)
                }
            }
            
            outputReader.start()
            errorReader.start()
            
            // Wait for process to complete
            val exitCode = process.waitFor()
            
            // Wait for readers to finish
            outputReader.join(1000)
            errorReader.join(1000)
            
            Log.d(TAG, "Command completed with exit code: $exitCode")
            
            Result.success(exitCode)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute package command", e)
            onError("Error: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Update package lists (apt update)
     */
    suspend fun updatePackages(
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(listOf("update"), onOutput, onError)
    }
    
    /**
     * Install a package (apt install)
     */
    suspend fun installPackage(
        packageName: String,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(
            listOf("install", "-y", packageName),
            onOutput,
            onError
        )
    }
    
    /**
     * Install multiple packages
     */
    suspend fun installPackages(
        packages: List<String>,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        val args = mutableListOf("install", "-y")
        args.addAll(packages)
        return executeApt(args, onOutput, onError)
    }
    
    /**
     * Remove a package (apt remove)
     */
    suspend fun removePackage(
        packageName: String,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(
            listOf("remove", "-y", packageName),
            onOutput,
            onError
        )
    }
    
    /**
     * Search for packages (apt search)
     */
    suspend fun searchPackages(
        query: String,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(listOf("search", query), onOutput, onError)
    }
    
    /**
     * List installed packages (apt list --installed)
     */
    suspend fun listInstalledPackages(
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(listOf("list", "--installed"), onOutput, onError)
    }
    
    /**
     * Show package info (apt show)
     */
    suspend fun showPackageInfo(
        packageName: String,
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(listOf("show", packageName), onOutput, onError)
    }
    
    /**
     * Upgrade all packages (apt upgrade)
     */
    suspend fun upgradePackages(
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(listOf("upgrade", "-y"), onOutput, onError)
    }
    
    /**
     * Clean package cache (apt clean)
     */
    suspend fun cleanCache(
        onOutput: (String) -> Unit,
        onError: (String) -> Unit
    ): Result<Int> {
        return executeApt(listOf("clean"), onOutput, onError)
    }
    
    /**
     * Check if a package is installed
     */
    suspend fun isPackageInstalled(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = executeApt(
                listOf("list", "--installed", packageName),
                onOutput = {},
                onError = {}
            )
            result.isSuccess && result.getOrNull() == 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get recommended packages to install for a good terminal experience
     */
    fun getRecommendedPackages(): List<RecommendedPackage> {
        return listOf(
            RecommendedPackage(
                name = "git",
                description = "Version control system",
                category = "Development"
            ),
            RecommendedPackage(
                name = "python",
                description = "Python programming language",
                category = "Development"
            ),
            RecommendedPackage(
                name = "nodejs",
                description = "JavaScript runtime",
                category = "Development"
            ),
            RecommendedPackage(
                name = "vim",
                description = "Text editor",
                category = "Editors"
            ),
            RecommendedPackage(
                name = "nano",
                description = "Simple text editor",
                category = "Editors"
            ),
            RecommendedPackage(
                name = "curl",
                description = "Command line tool for transferring data",
                category = "Network"
            ),
            RecommendedPackage(
                name = "wget",
                description = "Network downloader",
                category = "Network"
            ),
            RecommendedPackage(
                name = "openssh",
                description = "SSH client and server",
                category = "Network"
            ),
            RecommendedPackage(
                name = "htop",
                description = "Interactive process viewer",
                category = "System"
            ),
            RecommendedPackage(
                name = "tmux",
                description = "Terminal multiplexer",
                category = "System"
            ),
            RecommendedPackage(
                name = "tar",
                description = "Archive utility",
                category = "Utilities"
            ),
            RecommendedPackage(
                name = "zip",
                description = "Compression utility",
                category = "Utilities"
            ),
            RecommendedPackage(
                name = "unzip",
                description = "Decompression utility",
                category = "Utilities"
            )
        )
    }
    
    data class RecommendedPackage(
        val name: String,
        val description: String,
        val category: String
    )
}

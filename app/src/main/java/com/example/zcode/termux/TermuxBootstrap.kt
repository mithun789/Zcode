package com.example.zcode.termux

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

/**
 * TermuxBootstrap - Downloads and installs Termux bootstrap packages
 *
 * Provides full Linux environment with:
 * - bash shell
 * - apt package manager
 * - Core utilities (ls, cat, grep, etc.)
 * - gcc, python, git support (installable)
 */
class TermuxBootstrap(private val context: Context) {

    companion object {
        private const val TAG = "TermuxBootstrap"

        // Termux bootstrap URLs for different architectures
        private const val BOOTSTRAP_BASE_URL = "https://github.com/termux/termux-packages/releases/download/"
        private const val BOOTSTRAP_VERSION = "v0.118.0"

        // Architecture detection
        private val ARCH_MAP = mapOf(
            "aarch64" to "aarch64",
            "arm64" to "aarch64",
            "armv7l" to "arm",
            "arm" to "arm",
            "i686" to "i686",
            "x86_64" to "x86_64",
            "x86" to "i686"
        )
    }

    private val prefixDir: File = File(context.filesDir, "usr")
    private val homeDir: File = File(context.filesDir, "home")
    private val tmpDir: File = File(context.filesDir, "tmp")

    data class BootstrapProgress(
        val stage: Stage,
        val progress: Int,
        val message: String
    ) {
        enum class Stage {
            CHECKING, DOWNLOADING, EXTRACTING, CONFIGURING, COMPLETE, ERROR
        }
    }

    /**
     * Check if bootstrap is already installed
     */
    fun isInstalled(): Boolean {
        val bashBinary = File(prefixDir, "bin/bash")
        val aptBinary = File(prefixDir, "bin/apt")
        return bashBinary.exists() && aptBinary.exists() && bashBinary.canExecute()
    }

    /**
     * Get the architecture for bootstrap download
     */
    private fun getArchitecture(): String {
        val arch = System.getProperty("os.arch")?.lowercase() ?: ""
        return ARCH_MAP[arch] ?: "aarch64" // Default to aarch64 (most common for Android)
    }

    /**
     * Download and install Termux bootstrap
     */
    suspend fun installBootstrap(
        onProgress: (BootstrapProgress) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (isInstalled()) {
                onProgress(BootstrapProgress(
                    BootstrapProgress.Stage.COMPLETE,
                    100,
                    "Bootstrap already installed"
                ))
                return@withContext Result.success(Unit)
            }

            // Stage 1: Check and prepare directories
            onProgress(BootstrapProgress(
                BootstrapProgress.Stage.CHECKING,
                10,
                "Preparing directories..."
            ))

            prepareDirectories()

            // Stage 2: Download bootstrap
            val arch = getArchitecture()
            val bootstrapUrl = getBootstrapUrl(arch)

            onProgress(BootstrapProgress(
                BootstrapProgress.Stage.DOWNLOADING,
                20,
                "Downloading bootstrap for $arch..."
            ))

            val bootstrapZip = downloadBootstrap(bootstrapUrl) { progress ->
                onProgress(BootstrapProgress(
                    BootstrapProgress.Stage.DOWNLOADING,
                    20 + (progress * 0.4).toInt(),
                    "Downloading: $progress%"
                ))
            }

            // Stage 3: Extract bootstrap
            onProgress(BootstrapProgress(
                BootstrapProgress.Stage.EXTRACTING,
                60,
                "Extracting bootstrap packages..."
            ))

            extractBootstrap(bootstrapZip) { progress ->
                onProgress(BootstrapProgress(
                    BootstrapProgress.Stage.EXTRACTING,
                    60 + (progress * 0.3).toInt(),
                    "Extracting: $progress%"
                ))
            }

            // Stage 4: Configure environment
            onProgress(BootstrapProgress(
                BootstrapProgress.Stage.CONFIGURING,
                90,
                "Configuring environment..."
            ))

            configureEnvironment()

            // Clean up
            bootstrapZip.delete()

            onProgress(BootstrapProgress(
                BootstrapProgress.Stage.COMPLETE,
                100,
                "Bootstrap installed successfully!"
            ))

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Bootstrap installation failed", e)
            onProgress(BootstrapProgress(
                BootstrapProgress.Stage.ERROR,
                0,
                "Installation failed: ${e.message}"
            ))
            Result.failure(e)
        }
    }

    /**
     * Prepare necessary directories
     */
    private fun prepareDirectories() {
        prefixDir.mkdirs()
        homeDir.mkdirs()
        tmpDir.mkdirs()

        // Create standard Linux directories
        listOf("bin", "etc", "lib", "share", "tmp", "var").forEach {
            File(prefixDir, it).mkdirs()
        }
    }

    /**
     * Get bootstrap download URL for architecture
     */
    private fun getBootstrapUrl(arch: String): String {
        return "${BOOTSTRAP_BASE_URL}${BOOTSTRAP_VERSION}/bootstrap-${arch}.zip"
    }

    /**
     * Download bootstrap package
     */
    private suspend fun downloadBootstrap(
        url: String,
        onProgress: (Int) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val outputFile = File(context.cacheDir, "bootstrap.zip")

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()

        val fileLength = connection.contentLength
        val input = connection.inputStream
        val output = FileOutputStream(outputFile)

        val buffer = ByteArray(8192)
        var total = 0L
        var count: Int

        while (input.read(buffer).also { count = it } != -1) {
            total += count
            output.write(buffer, 0, count)

            if (fileLength > 0) {
                val progress = ((total * 100) / fileLength).toInt()
                onProgress(progress)
            }
        }

        output.flush()
        output.close()
        input.close()

        outputFile
    }

    /**
     * Extract bootstrap package
     */
    private suspend fun extractBootstrap(
        zipFile: File,
        onProgress: (Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        val buffer = ByteArray(8192)
        val zipInputStream = ZipInputStream(zipFile.inputStream())

        var entry = zipInputStream.nextEntry
        var count = 0
        val estimatedEntries = 500 // Rough estimate

        while (entry != null) {
            val file = File(prefixDir, entry.name)

            if (entry.isDirectory) {
                file.mkdirs()
            } else {
                file.parentFile?.mkdirs()
                val output = FileOutputStream(file)

                var len: Int
                while (zipInputStream.read(buffer).also { len = it } > 0) {
                    output.write(buffer, 0, len)
                }

                output.close()

                // Set executable permissions for binaries
                if (file.path.contains("/bin/") || file.path.contains("/libexec/")) {
                    file.setExecutable(true, false)
                }
            }

            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry

            count++
            val progress = ((count * 100) / estimatedEntries).coerceAtMost(100)
            onProgress(progress)
        }

        zipInputStream.close()
    }

    /**
     * Configure environment and create necessary files
     */
    private fun configureEnvironment() {
        // Create .bashrc
        val bashrc = File(homeDir, ".bashrc")
        bashrc.writeText("""
            # Zcode Terminal - Bash Configuration
            export PREFIX=${prefixDir.absolutePath}
            export HOME=${homeDir.absolutePath}
            export TMPDIR=${tmpDir.absolutePath}
            export PATH=${'$'}PREFIX/bin:${'$'}PATH
            export LD_LIBRARY_PATH=${'$'}PREFIX/lib
            
            # Aliases
            alias ll='ls -lah'
            alias la='ls -A'
            alias l='ls -CF'
            
            # Welcome message
            echo "Welcome to Zcode Terminal with full Linux environment!"
            echo "Type 'pkg install' to install packages"
            
        """.trimIndent())

        // Create profile
        val profile = File(homeDir, ".profile")
        profile.writeText("""
            # Zcode Terminal Profile
            export PREFIX=${prefixDir.absolutePath}
            export HOME=${homeDir.absolutePath}
            export TMPDIR=${tmpDir.absolutePath}
            
            if [ -f "${'$'}HOME/.bashrc" ]; then
                . "${'$'}HOME/.bashrc"
            fi
        """.trimIndent())

        // Create apt sources list
        val aptSourcesDir = File(prefixDir, "etc/apt/sources.list.d")
        aptSourcesDir.mkdirs()

        val sourcesList = File(prefixDir, "etc/apt/sources.list")
        sourcesList.writeText("""
            deb https://packages.termux.dev/apt/termux-main stable main
        """.trimIndent())

        Log.i(TAG, "Environment configured successfully")
    }

    /**
     * Get bash shell path
     */
    fun getBashPath(): String {
        return File(prefixDir, "bin/bash").absolutePath
    }

    /**
     * Get environment variables for bash
     */
    fun getEnvironment(): Array<String> {
        return arrayOf(
            "HOME=${homeDir.absolutePath}",
            "PREFIX=${prefixDir.absolutePath}",
            "TMPDIR=${tmpDir.absolutePath}",
            "TERM=xterm-256color",
            "PATH=${prefixDir.absolutePath}/bin:/system/bin:/system/xbin",
            "LD_LIBRARY_PATH=${prefixDir.absolutePath}/lib",
            "SHELL=${getBashPath()}"
        )
    }

    /**
     * Uninstall bootstrap (for cleanup)
     */
    suspend fun uninstallBootstrap(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            prefixDir.deleteRecursively()
            homeDir.deleteRecursively()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


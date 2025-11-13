package com.termux.terminal

import android.content.Context
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.LinkedBlockingQueue

/**
 * TerminalSession - Built-in command interpreter for Android
 *
 * Provides Linux-like commands without requiring system shell execution
 */
class TerminalSession(
    private val context: Context,
    private val changeCallback: SessionChangedCallback,
    private val process: Process? = null // Optional: connect to real process
) {

    var emulator: TerminalEmulator? = null
        private set

    private var mShellPid = -1
    private var mShellExitStatus = 0

    @Volatile
    private var mRunning = false

    private val inputQueue = LinkedBlockingQueue<String>()
    private var currentDirectory = ""
    private var isConnectedToRealProcess = false

    // Process streams for real shell execution
    private var processInputStream: InputStream? = null
    private var processOutputStream: OutputStream? = null
    private var processErrorStream: InputStream? = null

    // Built-in commands (only used when process is null)
    private val builtInCommands = mapOf<String, (List<String>) -> String>(
        "help" to ::cmdHelp,
        "ls" to ::cmdLs,
        "ll" to ::cmdLl,
        "pwd" to ::cmdPwd,
        "cd" to ::cmdCd,
        "echo" to ::cmdEcho,
        "cat" to ::cmdCat,
        "clear" to ::cmdClear,
        "mkdir" to ::cmdMkdir,
        "touch" to ::cmdTouch,
        "rm" to ::cmdRm,
        "cp" to ::cmdCp,
        "mv" to ::cmdMv,
        "date" to ::cmdDate,
        "whoami" to ::cmdWhoami,
        "uname" to ::cmdUname,
        "ps" to ::cmdPs,
        "df" to ::cmdDf,
        "free" to ::cmdFree,
        "neofetch" to ::cmdNeofetch,
        "fastfetch" to ::cmdNeofetch,
        "screenfetch" to ::cmdNeofetch,
        "logo" to ::cmdLogo,
        "grep" to ::cmdGrep,
        "find" to ::cmdFind,
        "tree" to ::cmdTree,
        "head" to ::cmdHead,
        "tail" to ::cmdTail,
        "wc" to ::cmdWc,
        "chmod" to ::cmdChmod,
        "env" to ::cmdEnv,
        "export" to ::cmdExport,
        "history" to ::cmdHistory,
        "apt" to ::cmdApt,
        "pkg" to ::cmdPkg
    )

    /**
     * Initialize the terminal session
     */
    init {
        initializeSession()
    }

    private fun initializeSession() {
        try {
            // Set safe default directory - app-specific directory
            val homeDir = java.io.File(context.filesDir, "home")
            if (!homeDir.exists()) {
                homeDir.mkdirs()
            }
            currentDirectory = homeDir.absolutePath
            
            // Create common directories
            val dirs = listOf("Documents", "Downloads", "Projects", "Scripts")
            dirs.forEach { dirName ->
                val dir = java.io.File(homeDir, dirName)
                if (!dir.exists()) dir.mkdirs()
            }
            
            // Create terminal emulator
            emulator = TerminalEmulator(
                changeCallback,
                80, // default columns
                24  // default rows
            )

            if (process != null) {
                // Connect to real process
                connectToProcess(process)
            } else {
                // Use built-in command interpreter
                mShellPid = android.os.Process.myPid()
                mRunning = true

                // Start input processing thread
                Thread { processInputLoop() }.start()

                // Send welcome message
                sendWelcomeMessage()
            }

        } catch (e: Exception) {
            throw RuntimeException("Failed to create terminal session", e)
        }
    }

    /**
     * Connect to a real process (Linux environment)
     */
    private fun connectToProcess(process: Process) {
        try {
            this.processInputStream = process.inputStream
            this.processOutputStream = process.outputStream
            this.processErrorStream = process.errorStream
            mShellPid = android.os.Process.myPid()
            mRunning = true
            isConnectedToRealProcess = true

            // Start threads to handle process I/O
            Thread { processOutputReader() }.start()
            Thread { processErrorReader() }.start()
            Thread { processInputWriter() }.start()

            // Send welcome message for Linux environment
            sendLinuxWelcomeMessage()

        } catch (e: Exception) {
            throw RuntimeException("Failed to connect to process", e)
        }
    }

    /**
     * Send welcome message with clean, Android-friendly design
     */
    private fun sendWelcomeMessage() {
        // No welcome message - just show prompt
        showPrompt()
    }

    /**
     * Send welcome message for Linux environment
     */
    private fun sendLinuxWelcomeMessage() {
        // No welcome message - just show prompt for real environment
        showPrompt()
    }

    /**
     * Read output from process stdout
     */
    private fun processOutputReader() {
        val buffer = ByteArray(4096)
        try {
            processInputStream?.let { input ->
                while (mRunning) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) break
                    if (bytesRead > 0) {
                        emulator?.append(buffer, bytesRead)
                        changeCallback.onTextChanged(this)
                    }
                }
            }
        } catch (e: IOException) {
            // Process ended or error
        } finally {
            mRunning = false
            changeCallback.onSessionFinished(this)
        }
    }

    /**
     * Read output from process stderr
     */
    private fun processErrorReader() {
        val buffer = ByteArray(4096)
        try {
            processErrorStream?.let { error ->
                while (mRunning) {
                    val bytesRead = error.read(buffer)
                    if (bytesRead == -1) break
                    if (bytesRead > 0) {
                        emulator?.append(buffer, bytesRead)
                        changeCallback.onTextChanged(this)
                    }
                }
            }
        } catch (e: IOException) {
            // Process ended or error
        }
    }

    /**
     * Write input to process stdin
     */
    private fun processInputWriter() {
        try {
            while (mRunning) {
                val input = inputQueue.take()
                if (!mRunning) break

                processOutputStream?.let { output ->
                    output.write((input + "\n").toByteArray())
                    output.flush()
                }
            }
        } catch (e: InterruptedException) {
            // Thread interrupted
        } catch (e: IOException) {
            // Process ended or error
        }
    }

    /**
     * Show command prompt with oh-my-posh style
     */
    private fun showPrompt() {
        val user = "user"
        val host = "zcode"
        val path = currentDirectory.substringAfterLast("/").let { 
            if (it.isEmpty()) "~" else it 
        }
        
        // oh-my-posh style: user@host:path❯ 
        val prompt = "$user@$host:$path❯ "
        appendOutput(prompt)
    }

    /**
     * Append output to terminal (for command results only, not prompts or errors)
     */
    private fun appendOutput(text: String) {
        emulator?.append(text.toByteArray(), text.length)
        changeCallback.onTextChanged(this)
    }

    /**
     * Append text that should be displayed but not processed as command input
     */
    fun appendSystemMessage(text: String) {
        val formattedText = if (text.startsWith("\n")) text else "\n$text\n"
        emulator?.append(formattedText.toByteArray(), formattedText.length)
        changeCallback.onTextChanged(this)
    }

    /**
     * Process input loop (only for built-in commands)
     */
    private fun processInputLoop() {
        while (mRunning) {
            try {
                val input = inputQueue.take()
                if (!mRunning) break

                if (!isConnectedToRealProcess) {
                    processCommand(input.trim())
                    showPrompt()
                }
                // If connected to real process, input is sent via processInputWriter instead

            } catch (e: InterruptedException) {
                break
            }
        }
    }

    /**
     * Process a command
     */
    private fun processCommand(input: String) {
        if (input.isEmpty()) return

        // Split command and arguments
        val parts = input.split("\\s+".toRegex())
        val command = parts[0].lowercase()
        val args = parts.drop(1)

        // Execute command
        try {
            if (!isConnectedToRealProcess && builtInCommands.containsKey(command)) {
                // Use built-in command
                val result = builtInCommands[command]?.invoke(args) ?: ""
                if (result.isNotEmpty()) {
                    appendOutput("\n$result")
                }
            } else if (!isConnectedToRealProcess) {
                // No real process and command not found in built-in
                appendOutput("\nCommand not found: $command")
            }
            // If connected to real process, do nothing - the process stdin/stdout will handle it

        } catch (e: Exception) {
            val error = "\nError executing command: ${e.message}"
            appendOutput(error)
        }
    }

    // Built-in command implementations

    private fun cmdHelp(args: List<String>): String {
        return """
Zcode Terminal - Linux-like Environment

File Operations:
  ls [dir]          - List files
  ll [dir]          - List files (detailed)
  pwd               - Show current directory
  cd <dir>          - Change directory
  cat <file>        - Display file contents
  mkdir <dir>       - Create directory
  touch <file>      - Create empty file
  rm <file/dir>     - Remove files/directories
  cp <src> <dst>    - Copy files
  mv <src> <dst>    - Move/rename files
  find <pattern>    - Find files by name
  grep <pattern>    - Search in files
  tree              - Show directory tree
  head <file>       - Show first lines
  tail <file>       - Show last lines
  wc <file>         - Count lines/words
  chmod <mode> <f>  - Change permissions

System Information:
  date              - Show current date/time
  whoami            - Show current user
  uname [-a]        - Show system info
  ps                - Show process list
  df                - Show disk usage
  free              - Show memory usage
  neofetch          - System info with logo
  env               - Show environment
  history           - Command history

Package Management:
  apt [command]     - Package manager
  pkg [command]     - Alias for apt

Utilities:
  echo [text]       - Print text
  clear             - Clear screen
  help              - Show this help

Tip: Type 'cd ~' for home directory
        """.trimIndent()
    }

    private fun cmdLs(args: List<String>): String {
        val dirPath = args.firstOrNull() ?: currentDirectory
        return try {
            val dir = File(dirPath)
            if (!dir.exists() || !dir.isDirectory) {
                return "ls: $dirPath: No such directory"
            }

            val files: List<File> = dir.listFiles()?.sortedWith(compareBy<File>({ !it.isDirectory }, { it.name })) ?: emptyList<File>()
            val output = mutableListOf<String>()

            files.forEach { file ->
                val type = if (file.isDirectory) "d" else "-"
                val size = if (file.isFile) formatFileSize(file.length()) else ""
                val name = file.name
                output.add("$type $size $name")
            }

            output.joinToString("\n")
        } catch (e: Exception) {
            "ls: Error listing directory: ${e.message}"
        }
    }

    private fun cmdPwd(args: List<String>): String = currentDirectory

    private fun cmdCd(args: List<String>): String {
        if (args.isEmpty() || args[0] == "~") {
            // Go to home directory
            val homeDir = File(context.filesDir, "home")
            currentDirectory = homeDir.absolutePath
            return ""
        }

        val newDir = when {
            args[0] == "-" -> {
                // TODO: Implement previous directory tracking
                return "cd: OLDPWD not set"
            }
            args[0].startsWith("~/") -> {
                val homeDir = File(context.filesDir, "home")
                File(homeDir, args[0].substring(2)).absolutePath
            }
            args[0].startsWith("/") -> args[0]
            else -> File(currentDirectory, args[0]).absolutePath
        }

        val dir = File(newDir)
        return if (dir.exists() && dir.isDirectory) {
            currentDirectory = dir.absolutePath
            ""
        } else {
            "cd: $newDir: No such directory"
        }
    }

    private fun cmdEcho(args: List<String>): String = args.joinToString(" ")

    private fun cmdCat(args: List<String>): String {
        if (args.isEmpty()) return "cat: missing file operand"

        return try {
            val file = File(if (args[0].startsWith("/")) args[0] else File(currentDirectory, args[0]).absolutePath)
            if (!file.exists()) return "cat: ${args[0]}: No such file"
            if (!file.isFile) return "cat: ${args[0]}: Is a directory"

            file.readText()
        } catch (e: Exception) {
            "cat: Error reading file: ${e.message}"
        }
    }

    private fun cmdClear(args: List<String>): String {
        emulator?.clearScreen()
        return ""
    }

    private fun cmdMkdir(args: List<String>): String {
        if (args.isEmpty()) return "mkdir: missing operand"

        return try {
            val dir = File(if (args[0].startsWith("/")) args[0] else File(currentDirectory, args[0]).absolutePath)
            if (dir.mkdirs()) {
                ""
            } else {
                "mkdir: cannot create directory '${args[0]}'"
            }
        } catch (e: Exception) {
            "mkdir: ${e.message}"
        }
    }

    private fun cmdTouch(args: List<String>): String {
        if (args.isEmpty()) return "touch: missing file operand"

        return try {
            val file = File(if (args[0].startsWith("/")) args[0] else File(currentDirectory, args[0]).absolutePath)
            if (file.createNewFile()) {
                ""
            } else {
                "touch: cannot create file '${args[0]}'"
            }
        } catch (e: Exception) {
            "touch: ${e.message}"
        }
    }

    private fun cmdRm(args: List<String>): String {
        if (args.isEmpty()) return "rm: missing operand"

        return try {
            val file = File(if (args[0].startsWith("/")) args[0] else File(currentDirectory, args[0]).absolutePath)
            if (!file.exists()) return "rm: cannot remove '${args[0]}': No such file or directory"

            if (file.deleteRecursively()) {
                ""
            } else {
                "rm: cannot remove '${args[0]}'"
            }
        } catch (e: Exception) {
            "rm: ${e.message}"
        }
    }

    private fun cmdCp(args: List<String>): String {
        if (args.size < 2) return "cp: missing destination file operand after '${args.firstOrNull() ?: ""}'"

        return try {
            val src = File(if (args[0].startsWith("/")) args[0] else File(currentDirectory, args[0]).absolutePath)
            val dst = File(if (args[1].startsWith("/")) args[1] else File(currentDirectory, args[1]).absolutePath)

            if (!src.exists()) return "cp: cannot stat '${args[0]}': No such file or directory"

            src.copyTo(dst, overwrite = true)
            ""
        } catch (e: Exception) {
            "cp: ${e.message}"
        }
    }

    private fun cmdMv(args: List<String>): String {
        if (args.size < 2) return "mv: missing destination file operand after '${args.firstOrNull() ?: ""}'"

        return try {
            val src = File(if (args[0].startsWith("/")) args[0] else File(currentDirectory, args[0]).absolutePath)
            val dst = File(if (args[1].startsWith("/")) args[1] else File(currentDirectory, args[1]).absolutePath)

            if (!src.exists()) return "mv: cannot stat '${args[0]}': No such file or directory"

            if (src.renameTo(dst)) {
                ""
            } else {
                "mv: cannot move '${args[0]}' to '${args[1]}'"
            }
        } catch (e: Exception) {
            "mv: ${e.message}"
        }
    }

    private fun cmdDate(args: List<String>): String = java.util.Date().toString()

    private fun cmdWhoami(args: List<String>): String = "zcode"

    private fun cmdUname(args: List<String>): String {
        return if (args.contains("-a")) {
            "Linux localhost 5.4.0-android12-9-00001-g1234567 #1 SMP PREEMPT Android"
        } else {
            "Linux"
        }
    }

    private fun cmdPs(args: List<String>): String {
        return """
PID   USER     TIME  COMMAND
1     root     00:00 init
${android.os.Process.myPid()}  u0_a123   00:00 com.example.zcode
        """.trimIndent()
    }

    private fun cmdDf(args: List<String>): String {
        return """
Filesystem     1K-blocks    Used Available Use% Mounted on
/dev/block/dm-0   52428800  12345678  40083122  24% /
/dev/block/dm-1    8388608    123456    8265152   2% /vendor
        """.trimIndent()
    }

    private fun cmdFree(args: List<String>): String {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024
        val usedMemory = totalMemory - freeMemory

        return """
              total        used        free      shared     buffers
Mem:         ${totalMemory}MB       ${usedMemory}MB       ${freeMemory}MB           0MB          0MB
-/+ buffers/cache:     ${usedMemory}MB       ${freeMemory}MB
Swap:            0MB          0MB          0MB
        """.trimIndent()
    }

    private fun cmdNeofetch(args: List<String>): String {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory() / 1024 / 1024
        val freeMemory = runtime.freeMemory() / 1024 / 1024
        val usedMemory = totalMemory - freeMemory

        // Get device info
        val deviceModel = android.os.Build.MODEL
        val androidVersion = android.os.Build.VERSION.RELEASE
        val apiLevel = android.os.Build.VERSION.SDK_INT
        val uptime = try {
            val uptimeMillis = android.os.SystemClock.elapsedRealtime()
            val seconds = uptimeMillis / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            "${days}d ${hours % 24}h ${minutes % 60}m"
        } catch (e: Exception) {
            "Unknown"
        }

        // Get screen width for responsive ASCII art
        val screenWidth = try {
            android.content.res.Resources.getSystem().displayMetrics.widthPixels
        } catch (e: Exception) {
            1080 // Default
        }
        
        // Determine ASCII art size based on screen width
        val isCompactScreen = screenWidth < 720
        
        // ASCII Logo for Zcode - responsive
        val logo = if (isCompactScreen) {
            """
   ###   ##  ###  ##  #####
     #  #  #  #  #  # #    
    #   #     #  #  # ##### 
   #    #  #  #  #  # #     
  ###    ##  ###  ##  #####
            """.trimIndent()
        } else {
            """
     ######   ####   #####  #####  #####
         ##  ##  ## ##   ## ##  ## ##   
       ###   ##     ##   ## ##  ## ##### 
      ##     ##  ## ##   ## ##  ## ##    
     ######   ####   #####  #####  #####
            """.trimIndent()
        }

        val logoLines = logo.lines()
        val infoLines = listOf(
            "${android.os.Build.USER}@${android.os.Build.DEVICE}",
            "----------------------------",
            "OS: Android $androidVersion (API $apiLevel)",
            "Device: $deviceModel",
            "Kernel: ${android.os.Build.VERSION.INCREMENTAL}",
            "Uptime: $uptime",
            "Memory: ${usedMemory}MB / ${totalMemory}MB",
            "Shell: Zcode Terminal v1.0",
            "CPU: ${Runtime.getRuntime().availableProcessors()} cores",
            "Screen: ${screenWidth}x${android.content.res.Resources.getSystem().displayMetrics.heightPixels}",
            ""
        )

        // Combine logo and info side by side or stacked based on screen size
        val result = if (isCompactScreen) {
            // Stack vertically for compact screens
            mutableListOf<String>().apply {
                addAll(logoLines)
                add("")
                addAll(infoLines)
            }
        } else {
            // Side by side for larger screens
            val combined = mutableListOf<String>()
            val maxLines = maxOf(logoLines.size, infoLines.size)
            
            for (i in 0 until maxLines) {
                val logoLine = logoLines.getOrElse(i) { "" }.padEnd(40)
                val infoLine = infoLines.getOrElse(i) { "" }
                combined.add("$logoLine  $infoLine")
            }
            combined
        }

        return result.joinToString("\n")
    }

    private fun cmdLogo(args: List<String>): String {
        // ASCII Logo for Zcode - compact version
        return """

 ######   ####   #####  #####  #####
     ##  ##  ## ##   ## ##  ## ##   
   ###   ##     ##   ## ##  ## ##### 
  ##     ##  ## ##   ## ##  ## ##    
 ######   ####   #####  #####  #####

 Welcome to Zcode Terminal
 Built-in Linux Environment

 Type 'help' for available commands
 Type 'neofetch' for system info
        """.trimIndent()
    }

    private fun cmdLl(args: List<String>): String {
        // Detailed listing (ls -la)
        val dirPath = args.firstOrNull() ?: currentDirectory
        return try {
            val dir = File(dirPath)
            if (!dir.exists() || !dir.isDirectory) {
                return "ll: $dirPath: No such directory"
            }

            val files: List<File> = dir.listFiles()?.sortedWith(compareBy<File>({ !it.isDirectory }, { it.name })) ?: emptyList<File>()
            val output = mutableListOf<String>()
            output.add("total ${files.size}")

            files.forEach { file ->
                val perms = if (file.isDirectory) "drwxr-xr-x" else "-rw-r--r--"
                val size = if (file.isFile) formatFileSize(file.length()).padStart(8) else "    <DIR>"
                val date = java.text.SimpleDateFormat("MMM dd HH:mm", java.util.Locale.US).format(java.util.Date(file.lastModified()))
                val name = file.name
                output.add("$perms  $size  $date  $name")
            }

            output.joinToString("\n")
        } catch (e: Exception) {
            "ll: Error listing directory: ${e.message}"
        }
    }

    private fun cmdGrep(args: List<String>): String {
        if (args.isEmpty()) return "grep: missing pattern"
        
        val pattern = args[0]
        val fileName = args.getOrNull(1) ?: return "grep: missing file"
        
        return try {
            val file = File(if (fileName.startsWith("/")) fileName else File(currentDirectory, fileName).absolutePath)
            if (!file.exists()) return "grep: $fileName: No such file"
            if (!file.isFile) return "grep: $fileName: Is a directory"
            
            val lines = file.readLines()
            val matches = lines.filter { it.contains(pattern, ignoreCase = true) }
            
            if (matches.isEmpty()) {
                "grep: no matches found"
            } else {
                matches.joinToString("\n")
            }
        } catch (e: Exception) {
            "grep: ${e.message}"
        }
    }

    private fun cmdFind(args: List<String>): String {
        if (args.isEmpty()) return "find: missing pattern"
        
        val pattern = args[0]
        val searchDir = File(currentDirectory)
        val results = mutableListOf<String>()
        
        fun searchRecursive(dir: File) {
            try {
                dir.listFiles()?.forEach { file ->
                    if (file.name.contains(pattern, ignoreCase = true)) {
                        results.add(file.absolutePath)
                    }
                    if (file.isDirectory && results.size < 50) {
                        searchRecursive(file)
                    }
                }
            } catch (e: Exception) {
                // Skip permission denied
            }
        }
        
        searchRecursive(searchDir)
        return if (results.isEmpty()) {
            "find: no files matching '$pattern'"
        } else {
            results.take(50).joinToString("\n") + if (results.size > 50) "\n... (${results.size - 50} more)" else ""
        }
    }

    private fun cmdTree(args: List<String>): String {
        val startDir = File(args.firstOrNull() ?: currentDirectory)
        if (!startDir.exists() || !startDir.isDirectory) {
            return "tree: ${startDir.path}: No such directory"
        }
        
        val output = mutableListOf<String>()
        output.add(startDir.name)
        
        fun printTree(dir: File, prefix: String = "", depth: Int = 0) {
            if (depth > 3) return // Limit depth
            
            try {
                val files = dir.listFiles()?.sortedWith(compareBy<File>({ !it.isDirectory }, { it.name })) ?: return
                val fileList = files.take(20) // Limit items
                
                fileList.forEachIndexed { index, file ->
                    val isLast = index == fileList.lastIndex
                    val marker = if (isLast) "└── " else "├── "
                    val icon = if (file.isDirectory) "📁 " else "📄 "
                    output.add("$prefix$marker$icon${file.name}")
                    
                    if (file.isDirectory) {
                        val newPrefix = prefix + if (isLast) "    " else "│   "
                        printTree(file, newPrefix, depth + 1)
                    }
                }
            } catch (e: Exception) {
                // Skip permission denied
            }
        }
        
        printTree(startDir)
        return output.joinToString("\n")
    }

    private fun cmdHead(args: List<String>): String {
        if (args.isEmpty()) return "head: missing file operand"
        
        val lines = args.find { it.startsWith("-n") }?.substringAfter("-n")?.toIntOrNull() ?: 10
        val fileName = args.firstOrNull { !it.startsWith("-") } ?: return "head: missing file"
        
        return try {
            val file = File(if (fileName.startsWith("/")) fileName else File(currentDirectory, fileName).absolutePath)
            if (!file.exists()) return "head: $fileName: No such file"
            
            file.readLines().take(lines).joinToString("\n")
        } catch (e: Exception) {
            "head: ${e.message}"
        }
    }

    private fun cmdTail(args: List<String>): String {
        if (args.isEmpty()) return "tail: missing file operand"
        
        val lines = args.find { it.startsWith("-n") }?.substringAfter("-n")?.toIntOrNull() ?: 10
        val fileName = args.firstOrNull { !it.startsWith("-") } ?: return "tail: missing file"
        
        return try {
            val file = File(if (fileName.startsWith("/")) fileName else File(currentDirectory, fileName).absolutePath)
            if (!file.exists()) return "tail: $fileName: No such file"
            
            file.readLines().takeLast(lines).joinToString("\n")
        } catch (e: Exception) {
            "tail: ${e.message}"
        }
    }

    private fun cmdWc(args: List<String>): String {
        if (args.isEmpty()) return "wc: missing file operand"
        
        val fileName = args[0]
        return try {
            val file = File(if (fileName.startsWith("/")) fileName else File(currentDirectory, fileName).absolutePath)
            if (!file.exists()) return "wc: $fileName: No such file"
            
            val lines = file.readLines()
            val lineCount = lines.size
            val wordCount = lines.sumOf { it.split("\\s+".toRegex()).size }
            val charCount = file.length()
            
            "$lineCount $wordCount $charCount $fileName"
        } catch (e: Exception) {
            "wc: ${e.message}"
        }
    }

    private fun cmdChmod(args: List<String>): String {
        if (args.size < 2) return "chmod: missing operand"
        return "chmod: file permissions are managed by Android system"
    }

    private fun cmdEnv(args: List<String>): String {
        return """
HOME=${File(context.filesDir, "home").absolutePath}
USER=zcode
SHELL=zcode-shell
PATH=/system/bin:/system/xbin
TERM=xterm-256color
PWD=$currentDirectory
LANG=en_US.UTF-8
        """.trimIndent()
    }

    private fun cmdExport(args: List<String>): String {
        if (args.isEmpty()) return cmdEnv(emptyList())
        return "export: environment variables set (simulated)"
    }

    private fun cmdHistory(args: List<String>): String {
        // TODO: Implement command history tracking
        return "history: command history feature coming soon"
    }

    private fun cmdApt(args: List<String>): String {
        val command = args.firstOrNull() ?: return """
Zcode Package Manager (simulated apt)

Usage: apt <command> [package]

Commands:
  update            Update package list
  upgrade           Upgrade all packages
  install <pkg>     Install a package
  remove <pkg>      Remove a package
  search <term>     Search for packages
  list              List installed packages

Note: This is a simulated environment.
For real package management, consider:
- Using Termux app for full Ubuntu packages
- Installing Linux Deploy for complete distro
- Using proot-distro for containerized Linux

Available built-in packages:
  python, nodejs, git, vim, nano, gcc
  (simulated - not actually installed)
        """.trimIndent()
        
        return when (command) {
            "update" -> "Reading package lists... Done\nAll packages are up to date."
            "upgrade" -> "0 upgraded, 0 newly installed, 0 to remove"
            "install" -> {
                val pkg = args.getOrNull(1) ?: return "apt install: missing package name"
                "Package '$pkg' not available in simulated environment.\n" +
                "Tip: Install Termux for real package management."
            }
            "list" -> "Built-in commands available. Type 'help' to see them."
            "search" -> {
                val term = args.getOrNull(1) ?: return "apt search: missing search term"
                "Searching for '$term'... No packages found (simulated)"
            }
            else -> "apt: unknown command '$command'\nTry 'apt' without arguments for usage."
        }
    }

    private fun cmdPkg(args: List<String>): String = cmdApt(args)

    /**
     * Format file size
     */
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "${bytes}B"
            bytes < 1024 * 1024 -> "${bytes / 1024}K"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)}M"
            else -> "${bytes / (1024 * 1024 * 1024)}G"
        }
    }

    /**
     * Write input to the terminal
     */
    fun write(data: ByteArray, offset: Int = 0, count: Int = data.size) {
        if (!mRunning) return

        try {
            val text = String(data, offset, count).trim()
            if (text.isNotEmpty()) {
                inputQueue.put(text)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    /**
     * Send text input to the terminal
     */
    fun writeText(text: String) {
        write(text.toByteArray())
    }

    /**
     * Update terminal size (columns and rows)
     */
    fun updateSize(columns: Int, rows: Int) {
        emulator?.updateSize(columns, rows)
    }

    /**
     * Finish the session and cleanup
     */
    fun finish() {
        mRunning = false
        changeCallback.onSessionFinished(this)
    }

    /**
     * Check if session is still running
     */
    fun isRunning(): Boolean = mRunning

    /**
     * Get shell PID
     */
    fun getPid(): Int = mShellPid

    /**
     * Get shell exit status
     */
    fun getExitStatus(): Int = mShellExitStatus

    /**
     * Get current working directory
     */
    fun getCwd(): String = currentDirectory

    /**
     * Callback interface for session changes
     */
    interface SessionChangedCallback {
        fun onTextChanged(session: TerminalSession)
        fun onTitleChanged(session: TerminalSession)
        fun onSessionFinished(session: TerminalSession)
        fun onClipboardText(session: TerminalSession, text: String)
        fun onBell(session: TerminalSession)
        fun onColorsChanged(session: TerminalSession)
    }
}


package com.example.zcode.terminal

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * CommandHistory - Database for storing terminal command history
 */
@Entity(tableName = "command_history")
data class CommandHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val command: String,
    val timestamp: Long = System.currentTimeMillis(),
    val workingDirectory: String = ""
)

@Dao
interface CommandHistoryDao {
    @Query("SELECT * FROM command_history ORDER BY timestamp DESC LIMIT 100")
    fun getRecentCommands(): Flow<List<CommandHistoryEntity>>

    @Query("SELECT * FROM command_history ORDER BY timestamp DESC")
    suspend fun getAllCommands(): List<CommandHistoryEntity>

    @Query("SELECT DISTINCT command FROM command_history WHERE command LIKE :prefix || '%' ORDER BY timestamp DESC LIMIT 10")
    suspend fun getCommandSuggestions(prefix: String): List<String>

    @Insert
    suspend fun insertCommand(command: CommandHistoryEntity)

    @Query("DELETE FROM command_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOldCommands(cutoffTime: Long)

    @Query("DELETE FROM command_history")
    suspend fun clearHistory()
}

/**
 * CommandHistoryManager - Manages command history and suggestions
 */
class CommandHistoryManager(private val dao: CommandHistoryDao) {

    private val historyList = mutableListOf<String>()
    private var currentIndex = -1

    suspend fun addCommand(command: String, workingDir: String = "") {
        if (command.isNotBlank()) {
            dao.insertCommand(
                CommandHistoryEntity(
                    command = command.trim(),
                    workingDirectory = workingDir
                )
            )
            historyList.add(0, command.trim())
            currentIndex = -1

            // Cleanup old commands (older than 30 days)
            val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            dao.deleteOldCommands(thirtyDaysAgo)
        }
    }

    suspend fun getSuggestions(prefix: String): List<String> {
        return if (prefix.isBlank()) {
            emptyList()
        } else {
            dao.getCommandSuggestions(prefix)
        }
    }

    suspend fun loadHistory() {
        val commands = dao.getAllCommands()
        historyList.clear()
        historyList.addAll(commands.map { it.command })
        currentIndex = -1
    }

    fun getPreviousCommand(): String? {
        if (historyList.isEmpty()) return null

        currentIndex++
        if (currentIndex >= historyList.size) {
            currentIndex = historyList.size - 1
        }

        return historyList.getOrNull(currentIndex)
    }

    fun getNextCommand(): String? {
        if (historyList.isEmpty()) return null

        currentIndex--
        if (currentIndex < -1) {
            currentIndex = -1
            return ""
        }

        return historyList.getOrNull(currentIndex) ?: ""
    }

    fun resetIndex() {
        currentIndex = -1
    }

    suspend fun clearHistory() {
        dao.clearHistory()
        historyList.clear()
        currentIndex = -1
    }
}

/**
 * Common Unix/Linux commands for suggestions
 */
object CommonCommands {
    val commands = listOf(
        "ls", "cd", "pwd", "cat", "echo", "mkdir", "rm", "cp", "mv", "touch",
        "grep", "find", "chmod", "chown", "ps", "kill", "df", "du", "free",
        "top", "uname", "whoami", "date", "clear", "exit", "history", "man",
        "tar", "zip", "unzip", "wget", "curl", "ssh", "scp", "ping", "ifconfig",
        "netstat", "apt", "pkg", "nano", "vi", "vim", "less", "more", "head", "tail"
    )

    fun getSuggestions(prefix: String): List<String> {
        return if (prefix.isBlank()) {
            emptyList()
        } else {
            commands.filter { it.startsWith(prefix, ignoreCase = true) }
        }
    }
}


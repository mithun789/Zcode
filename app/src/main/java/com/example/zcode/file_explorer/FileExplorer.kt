package com.example.zcode.file_explorer

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * FileExplorer - Handles file browsing and operations
 *
 * Provides functionality to:
 * - Browse directories
 * - Perform file operations (copy, move, delete)
 * - Get file properties
 * - Search files
 * - Manage bookmarks
 */
class FileExplorer(private val context: Context) {

    private val bookmarks = mutableSetOf<String>()
    private var currentPath = Environment.getExternalStorageDirectory().absolutePath

    /**
     * Get current directory path
     */
    fun getCurrentPath(): String = currentPath

    /**
     * List files in directory
     *
     * @param path Directory path
     * @param showHidden Whether to show hidden files
     * @return List of file items
     */
    suspend fun listFiles(
        path: String = currentPath,
        showHidden: Boolean = false
    ): List<FileItem> = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            if (!directory.isDirectory) {
                throw IllegalArgumentException("Not a directory: $path")
            }

            val files = directory.listFiles() ?: emptyArray()

            files
                .filter { showHidden || !it.name.startsWith(".") }
                .map { file ->
                    FileItem(
                        name = file.name,
                        path = file.absolutePath,
                        isDirectory = file.isDirectory,
                        size = file.length(),
                        lastModified = file.lastModified(),
                        canRead = file.canRead(),
                        canWrite = file.canWrite()
                    )
                }
                .sortedWith(compareBy({ !it.isDirectory }, { it.name }))
        } catch (e: Exception) {
            throw FileExplorerException("Failed to list files: ${e.message}", e)
        }
    }

    /**
     * Navigate to directory
     *
     * @param path Directory path to navigate to
     */
    suspend fun navigateToDirectory(path: String) = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            if (!directory.isDirectory) {
                throw IllegalArgumentException("Not a directory: $path")
            }
            if (!directory.canRead()) {
                throw SecurityException("Permission denied: $path")
            }

            currentPath = path
        } catch (e: Exception) {
            throw FileExplorerException("Failed to navigate: ${e.message}", e)
        }
    }

    /**
     * Navigate to parent directory
     */
    suspend fun navigateToParent() = withContext(Dispatchers.IO) {
        try {
            val parent = File(currentPath).parentFile
            if (parent != null && parent.canRead()) {
                currentPath = parent.absolutePath
                true
            } else {
                false
            }
        } catch (e: Exception) {
            throw FileExplorerException("Failed to navigate to parent: ${e.message}", e)
        }
    }

    /**
     * Copy file
     *
     * @param sourcePath Source file path
     * @param destinationPath Destination file path
     */
    suspend fun copyFile(sourcePath: String, destinationPath: String) = withContext(Dispatchers.IO) {
        try {
            val source = File(sourcePath)
            val destination = File(destinationPath)

            if (!source.exists()) throw FileNotFoundException("Source file not found: $sourcePath")
            if (!source.canRead()) throw SecurityException("Cannot read source file: $sourcePath")

            source.inputStream().use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            throw FileExplorerException("Failed to copy file: ${e.message}", e)
        }
    }

    /**
     * Move file
     *
     * @param sourcePath Source file path
     * @param destinationPath Destination file path
     */
    suspend fun moveFile(sourcePath: String, destinationPath: String) = withContext(Dispatchers.IO) {
        try {
            val source = File(sourcePath)
            val destination = File(destinationPath)

            if (!source.exists()) throw FileNotFoundException("Source file not found: $sourcePath")
            if (!source.renameTo(destination)) {
                throw FileExplorerException("Failed to move file: $sourcePath", null)
            }
        } catch (e: Exception) {
            throw FileExplorerException("Failed to move file: ${e.message}", e)
        }
    }

    /**
     * Delete file or directory
     *
     * @param path File or directory path to delete
     * @param recursive Whether to delete directory recursively
     */
    suspend fun deleteFile(path: String, recursive: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (!file.exists()) throw FileNotFoundException("File not found: $path")

            when {
                file.isFile -> file.delete()
                file.isDirectory && recursive -> file.deleteRecursively()
                file.isDirectory -> throw FileExplorerException("Directory not empty, use recursive=true", null)
                else -> false
            }
        } catch (e: Exception) {
            throw FileExplorerException("Failed to delete file: ${e.message}", e)
        }
    }

    /**
     * Create directory
     *
     * @param path Directory path
     */
    suspend fun createDirectory(path: String) = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            if (directory.exists()) throw FileExplorerException("Directory already exists: $path", null)
            directory.mkdirs()
        } catch (e: Exception) {
            throw FileExplorerException("Failed to create directory: ${e.message}", e)
        }
    }

    /**
     * Get file properties
     *
     * @param path File path
     * @return File item with properties
     */
    suspend fun getFileProperties(path: String): FileItem = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            FileItem(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                size = file.length(),
                lastModified = file.lastModified(),
                canRead = file.canRead(),
                canWrite = file.canWrite()
            )
        } catch (e: Exception) {
            throw FileExplorerException("Failed to get file properties: ${e.message}", e)
        }
    }

    /**
     * Search files by name
     *
     * @param searchQuery Query string
     * @param startPath Path to start searching from
     * @param maxResults Maximum results to return
     */
    suspend fun searchFiles(
        searchQuery: String,
        startPath: String = currentPath,
        maxResults: Int = 100
    ): List<FileItem> = withContext(Dispatchers.IO) {
        try {
            val results = mutableListOf<FileItem>()
            val startDir = File(startPath)

            fun search(directory: File) {
                if (results.size >= maxResults) return

                directory.listFiles()?.forEach { file ->
                    if (file.name.contains(searchQuery, ignoreCase = true)) {
                        results.add(
                            FileItem(
                                name = file.name,
                                path = file.absolutePath,
                                isDirectory = file.isDirectory,
                                size = file.length(),
                                lastModified = file.lastModified(),
                                canRead = file.canRead(),
                                canWrite = file.canWrite()
                            )
                        )
                    }

                    if (file.isDirectory && results.size < maxResults) {
                        search(file)
                    }
                }
            }

            search(startDir)
            results
        } catch (e: Exception) {
            throw FileExplorerException("Failed to search files: ${e.message}", e)
        }
    }

    /**
     * Add bookmark
     *
     * @param path Path to bookmark
     */
    fun addBookmark(path: String) {
        bookmarks.add(path)
    }

    /**
     * Remove bookmark
     *
     * @param path Bookmarked path to remove
     */
    fun removeBookmark(path: String) {
        bookmarks.remove(path)
    }

    /**
     * Get all bookmarks
     */
    fun getBookmarks(): List<String> = bookmarks.toList()

    /**
     * Clear all bookmarks
     */
    fun clearBookmarks() {
        bookmarks.clear()
    }
}

/**
 * FileItem - Represents a file or directory
 */
data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = 0,
    val canRead: Boolean = true,
    val canWrite: Boolean = true
) {
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }
}

/**
 * FileExplorerException - Custom exception for file explorer operations
 */
class FileExplorerException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * FileNotFoundException - Exception when file is not found
 */
class FileNotFoundException(message: String) : Exception(message)


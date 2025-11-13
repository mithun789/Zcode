package com.example.zcode.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * FilesScreen - Working file explorer with directory navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(
    onOpenInTerminal: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Initialize safe home directory
    val safeHomeDir = remember {
        val homeDir = File(context.filesDir, "home")
        if (!homeDir.exists()) {
            homeDir.mkdirs()
            // Create common directories
            listOf("Documents", "Downloads", "Projects", "Scripts").forEach { dirName ->
                File(homeDir, dirName).mkdirs()
            }
        }
        homeDir.absolutePath
    }
    
    // Start in safe home directory instead of /sdcard
    var currentPath by remember { mutableStateOf(safeHomeDir) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var createType by remember { mutableStateOf("file") } // "file" or "folder"
    var showDeleteDialog by remember { mutableStateOf<File?>(null) }
    var showRenameDialog by remember { mutableStateOf<File?>(null) }
    var files by remember { mutableStateOf<List<FileItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load files when path changes
    LaunchedEffect(currentPath) {
        try {
            val dir = File(currentPath)
            if (dir.exists() && dir.canRead()) {
                files = dir.listFiles()?.map { file ->
                    FileItem(
                        name = file.name,
                        path = file.absolutePath,
                        isDirectory = file.isDirectory,
                        size = if (file.isFile) file.length() else 0,
                        lastModified = file.lastModified(),
                        canRead = file.canRead(),
                        canWrite = file.canWrite()
                    )
                }?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
                errorMessage = null
            } else {
                errorMessage = "Cannot access directory"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            files = emptyList()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenInTerminal?.invoke(currentPath) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Open in Terminal"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            // Header with current path
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(
                    onClick = {
                        val parent = File(currentPath).parentFile
                        if (parent != null && parent.exists()) {
                            currentPath = parent.absolutePath
                        }
                    },
                    enabled = File(currentPath).parent != null
                ) {
                    Icon(Icons.Default.ArrowBack, "Go back")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "File Explorer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentPath,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                // Quick nav buttons
                IconButton(onClick = { currentPath = safeHomeDir }) {
                    Icon(Icons.Default.Home, "Home")
                }
                IconButton(onClick = { 
                    // Allow access to external storage if needed
                    val externalDir = context.getExternalFilesDir(null)
                    if (externalDir != null && externalDir.exists()) {
                        currentPath = externalDir.absolutePath
                    }
                }) {
                    Icon(Icons.Default.Storage, "Storage")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage ?: "",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // File list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(files) { file ->
                FileItemRow(
                    file = file,
                    onClick = {
                        if (file.isDirectory) {
                            currentPath = file.path
                        }
                    }
                )
                Divider()
            }

            // Show message if empty
            if (files.isEmpty() && errorMessage == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Empty directory",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Status bar
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${files.size} items",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${files.count { it.isDirectory }} folders, ${files.count { !it.isDirectory }} files",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            }
        }
    }
}

@Composable
fun FileItemRow(
    file: FileItem,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
            contentDescription = if (file.isDirectory) "Folder" else "File",
            tint = if (file.isDirectory)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (file.isDirectory) FontWeight.Bold else FontWeight.Normal
            )
            Row {
                if (!file.isDirectory) {
                    Text(
                        text = formatFileSize(file.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = dateFormat.format(Date(file.lastModified)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Permissions indicator
        Row {
            if (file.canRead) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = "Readable",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
            if (file.canWrite) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Writable",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val canRead: Boolean,
    val canWrite: Boolean
)


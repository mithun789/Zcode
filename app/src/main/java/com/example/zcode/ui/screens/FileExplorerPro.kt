package com.example.zcode.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
 * FileExplorerPro - Full-featured file explorer with CRUD operations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerPro(
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
    
    var currentPath by remember { mutableStateOf(safeHomeDir) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dialog states
    var showCreateDialog by remember { mutableStateOf(false) }
    var createType by remember { mutableStateOf("file") }
    var showDeleteDialog by remember { mutableStateOf<File?>(null) }
    var showRenameDialog by remember { mutableStateOf<File?>(null) }
    var showOptionsMenu by remember { mutableStateOf<File?>(null) }

    // Load files when path changes
    LaunchedEffect(currentPath) {
        try {
            val dir = File(currentPath)
            if (dir.exists() && dir.canRead()) {
                files = dir.listFiles()?.sortedWith(
                    compareBy({ !it.isDirectory }, { it.name.lowercase() })
                ) ?: emptyList()
                errorMessage = null
            } else {
                errorMessage = "Cannot access: $currentPath"
                // Fallback to safe home directory
                if (!dir.canRead()) {
                    currentPath = safeHomeDir
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "File Explorer",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = currentPath,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val parent = File(currentPath).parent
                        if (parent != null && parent != currentPath) {
                            currentPath = parent
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Home button - goes to safe home directory
                    IconButton(onClick = { currentPath = safeHomeDir }) {
                        Icon(Icons.Default.Home, "Home")
                    }
                    
                    // External storage button
                    IconButton(onClick = { 
                        val externalDir = context.getExternalFilesDir(null)
                        if (externalDir != null && externalDir.exists()) {
                            currentPath = externalDir.absolutePath
                        }
                    }) {
                        Icon(Icons.Default.Storage, "Storage")
                    }

                    // Create new menu
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Add, "Create")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("New File") },
                            onClick = {
                                createType = "file"
                                showCreateDialog = true
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.InsertDriveFile, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("New Folder") },
                            onClick = {
                                createType = "folder"
                                showCreateDialog = true
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.CreateNewFolder, null) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenInTerminal?.invoke(currentPath) }
            ) {
                Icon(Icons.Default.Terminal, "Open in Terminal")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Error message
            errorMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Quick access buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { currentPath = "/sdcard" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.SdCard, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("SD Card")
                }
                Button(
                    onClick = { currentPath = "/sdcard/Download" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Downloads")
                }
                Button(
                    onClick = { currentPath = "/sdcard/Documents" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Description, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Docs")
                }
            }

            HorizontalDivider()

            // File list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(files) { file ->
                    FileItemWithActions(
                        file = file,
                        onNavigate = {
                            if (file.isDirectory) {
                                currentPath = file.absolutePath
                            }
                        },
                        onDelete = { showDeleteDialog = file },
                        onRename = { showRenameDialog = file },
                        onShowOptions = { showOptionsMenu = file }
                    )
                }
            }
        }
    }

    // Create dialog
    if (showCreateDialog) {
        CreateItemDialog(
            type = createType,
            currentPath = currentPath,
            onDismiss = { showCreateDialog = false },
            onCreated = {
                showCreateDialog = false
                // Refresh file list
                currentPath = currentPath // Trigger recomposition
            }
        )
    }

    // Delete confirmation
    showDeleteDialog?.let { file ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete ${if (file.isDirectory) "Folder" else "File"}?") },
            text = { Text("Delete: ${file.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    try {
                        if (file.deleteRecursively()) {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            currentPath = currentPath // Refresh
                        } else {
                            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    showDeleteDialog = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Rename dialog
    showRenameDialog?.let { file ->
        RenameDialog(
            file = file,
            onDismiss = { showRenameDialog = null },
            onRenamed = {
                showRenameDialog = null
                currentPath = currentPath // Refresh
            }
        )
    }
}

@Composable
fun FileItemWithActions(
    file: File,
    onNavigate: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onShowOptions: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onNavigate)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = if (file.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${formatFileSize(file.length())} • ${formatDate(file.lastModified())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Actions menu
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, "Options")
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Rename") },
                    onClick = {
                        onRename()
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDelete()
                        showMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, null) }
                )
            }
        }
    }
}

@Composable
fun CreateItemDialog(
    type: String,
    currentPath: String,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New $type") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    val newFile = File(currentPath, name)
                    if (type == "folder") {
                        if (newFile.mkdirs()) {
                            Toast.makeText(context, "Folder created", Toast.LENGTH_SHORT).show()
                            onCreated()
                        } else {
                            Toast.makeText(context, "Failed to create folder", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (newFile.createNewFile()) {
                            Toast.makeText(context, "File created", Toast.LENGTH_SHORT).show()
                            onCreated()
                        } else {
                            Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameDialog(
    file: File,
    onDismiss: () -> Unit,
    onRenamed: () -> Unit
) {
    val context = LocalContext.current
    var newName by remember { mutableStateOf(file.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("New name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    val newFile = File(file.parent, newName)
                    if (file.renameTo(newFile)) {
                        Toast.makeText(context, "Renamed", Toast.LENGTH_SHORT).show()
                        onRenamed()
                    } else {
                        Toast.makeText(context, "Rename failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}




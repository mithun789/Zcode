package com.example.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import kotlinx.coroutines.launch

/**
 * FileEditorScreen - Simple text file editor
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileEditorScreen(
    filePath: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val file = remember { File(filePath) }

    var content by remember { mutableStateOf("") }
    var isModified by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load file content
    LaunchedEffect(filePath) {
        try {
            if (file.exists() && file.canRead()) {
                content = file.readText()
            } else {
                errorMessage = "Cannot read file"
            }
        } catch (e: Exception) {
            errorMessage = "Error loading file: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = file.parent ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isModified) {
                            showSaveDialog = true
                        } else {
                            onClose()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Close")
                    }
                },
                actions = {
                    if (isModified) {
                        Text(
                            text = "Modified",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                try {
                                    isSaving = true
                                    file.writeText(content)
                                    isModified = false
                                    errorMessage = "File saved successfully"
                                } catch (e: Exception) {
                                    errorMessage = "Error saving: ${e.message}"
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        enabled = isModified && !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, "Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error/Status message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (errorMessage!!.contains("success", ignoreCase = true))
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { errorMessage = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // File stats
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lines: ${content.count { it == '\n' } + 1}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Characters: ${content.length}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Size: ${file.length()} bytes",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Text editor
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    isModified = true
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text("Enter file content...")
                }
            )
        }
    }

    // Save confirmation dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("Do you want to save changes before closing?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        try {
                            file.writeText(content)
                            showSaveDialog = false
                            onClose()
                        } catch (e: Exception) {
                            errorMessage = "Error saving: ${e.message}"
                            showSaveDialog = false
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSaveDialog = false
                    onClose()
                }) {
                    Text("Discard")
                }
            }
        )
    }
}


package com.example.zcode.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zcode.termux.TermuxBootstrap
import kotlinx.coroutines.launch

/**
 * BootstrapInstallerScreen - Install Termux bootstrap for full Linux environment
 */
@Composable
fun BootstrapInstallerScreen(
    onInstallComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bootstrap = remember { TermuxBootstrap(context) }

    var isInstalled by remember { mutableStateOf(bootstrap.isInstalled()) }
    var isInstalling by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }
    var statusMessage by remember { mutableStateOf("") }
    var currentStage by remember { mutableStateOf<TermuxBootstrap.BootstrapProgress.Stage?>(null) }

    LaunchedEffect(Unit) {
        isInstalled = bootstrap.isInstalled()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Full Linux Environment",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isInstalled) {
            // Already installed
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Bootstrap Installed!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "You now have full bash shell with apt package manager",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onInstallComplete,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Terminal")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                bootstrap.uninstallBootstrap()
                                isInstalled = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Uninstall Bootstrap")
                    }
                }
            }
        } else if (isInstalling) {
            // Installing
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStage) {
                        TermuxBootstrap.BootstrapProgress.Stage.DOWNLOADING -> {
                            Icon(Icons.Default.Download, null, Modifier.size(48.dp))
                        }
                        TermuxBootstrap.BootstrapProgress.Stage.EXTRACTING -> {
                            Icon(Icons.Default.FolderZip, null, Modifier.size(48.dp))
                        }
                        TermuxBootstrap.BootstrapProgress.Stage.CONFIGURING -> {
                            Icon(Icons.Default.Settings, null, Modifier.size(48.dp))
                        }
                        else -> {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$progress%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            // Not installed - show install button
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "What you'll get:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureRow(
                        icon = Icons.Default.Terminal,
                        text = "Full bash shell (not basic sh)"
                    )
                    FeatureRow(
                        icon = Icons.Default.Download,
                        text = "apt package manager"
                    )
                    FeatureRow(
                        icon = Icons.Default.Code,
                        text = "Install gcc, python, git, nano, vim"
                    )
                    FeatureRow(
                        icon = Icons.Default.Storage,
                        text = "Linux utilities & core packages"
                    )
                    FeatureRow(
                        icon = Icons.Default.Language,
                        text = "Full POSIX environment"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "⚠️ Download size: ~50MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isInstalling = true
                            scope.launch {
                                bootstrap.installBootstrap { progressUpdate ->
                                    progress = progressUpdate.progress
                                    statusMessage = progressUpdate.message
                                    currentStage = progressUpdate.stage

                                    if (progressUpdate.stage == TermuxBootstrap.BootstrapProgress.Stage.COMPLETE) {
                                        isInstalling = false
                                        isInstalled = true
                                    } else if (progressUpdate.stage == TermuxBootstrap.BootstrapProgress.Stage.ERROR) {
                                        isInstalling = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Install Full Linux Environment")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "After installation, your terminal will have full Linux capabilities!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


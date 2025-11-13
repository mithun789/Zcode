package com.example.zcode.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zcode.linux.LinuxEnvironmentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.lang.Process

/**
 * LinuxEnvironmentsScreen - Manage Linux environments
 *
 * Allows users to create, manage, and switch between different Linux distributions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinuxEnvironmentsScreen(
    modifier: Modifier = Modifier,
    viewModel: LinuxEnvironmentsViewModel = hiltViewModel(),
    onEnvironmentSelected: (LinuxEnvironmentManager.Environment) -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }

    val environments by viewModel.environments.collectAsState()
    val currentEnvironment by viewModel.currentEnvironment.collectAsState()
    val installationProgress by viewModel.installationProgress.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Linux Environments") },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Environment")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Installation progress
            installationProgress?.let { (message, progress) ->
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Current environment indicator
            currentEnvironment?.let { env ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Current",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Environment",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "${env.distribution.displayName} (${env.id})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (env.isRunning) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                Text("Running")
                            }
                        }
                    }
                }
            }

            // Environments list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (environments.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Computer,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Linux Environments",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create your first Linux environment to get started",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { showCreateDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create Environment")
                            }
                        }
                    }
                } else {
                    items(environments) { environment ->
                        EnvironmentCard(
                            environment = environment,
                            isCurrent = environment.id == currentEnvironment?.id,
                            onSelect = {
                                viewModel.setCurrentEnvironment(environment)
                                onEnvironmentSelected(environment)
                            },
                            onStart = {
                                scope.launch {
                                    viewModel.startEnvironment(environment)
                                }
                            },
                            onStop = {
                                viewModel.stopEnvironment(environment)
                            },
                            onDelete = {
                                scope.launch {
                                    viewModel.deleteEnvironment(environment)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Create environment dialog
    if (showCreateDialog) {
        CreateEnvironmentDialog(
            distributions = viewModel.getAvailableDistributions(),
            onDismiss = { showCreateDialog = false },
            onCreate = { distribution, name ->
                scope.launch {
                    val result = viewModel.createEnvironment(distribution, name)
                    result.onSuccess { env ->
                        viewModel.setCurrentEnvironment(env)
                        onEnvironmentSelected(env)
                    }
                    result.onFailure { error ->
                        // Handle error (could show snackbar)
                    }
                }
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun EnvironmentCard(
    environment: LinuxEnvironmentManager.Environment,
    isCurrent: Boolean,
    onSelect: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Distribution icon
                Icon(
                    Icons.Default.Computer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = environment.distribution.displayName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text("Current")
                            }
                        }
                    }
                    Text(
                        text = environment.id,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Status indicators
                Row {
                    if (environment.isRunning) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Text("Running")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (environment.isInstalled) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Installed",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select")
                }

                if (environment.isRunning) {
                    OutlinedButton(
                        onClick = onStop,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Stop")
                    }
                } else {
                    Button(
                        onClick = onStart,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start")
                    }
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Environment") },
            text = {
                Text("Are you sure you want to delete the ${environment.distribution.displayName} environment? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEnvironmentDialog(
    distributions: List<LinuxEnvironmentManager.Distribution>,
    onDismiss: () -> Unit,
    onCreate: (LinuxEnvironmentManager.Distribution, String?) -> Unit
) {
    var selectedDistribution by remember { mutableStateOf<LinuxEnvironmentManager.Distribution?>(null) }
    var environmentName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Linux Environment") },
        text = {
            Column {
                Text(
                    text = "Choose a Linux distribution to create:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(distributions) { distribution ->
                        DistributionCard(
                            distribution = distribution,
                            isSelected = selectedDistribution == distribution,
                            onSelect = { selectedDistribution = distribution }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = environmentName,
                    onValueChange = { environmentName = it },
                    label = { Text("Environment Name (optional)") },
                    placeholder = { Text("Leave empty for auto-generated name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedDistribution?.let { dist ->
                        onCreate(dist, environmentName.takeIf { it.isNotBlank() })
                    }
                },
                enabled = selectedDistribution != null
            ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DistributionCard(
    distribution: LinuxEnvironmentManager.Distribution,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = distribution.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Architecture: ${distribution.architecture}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                Icons.Default.Computer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * ViewModel for Linux Environments Screen
 */
@HiltViewModel
class LinuxEnvironmentsViewModel @Inject constructor(
    private val linuxManager: LinuxEnvironmentManager
) : androidx.lifecycle.ViewModel() {

    val environments = linuxManager.environments
    val currentEnvironment = linuxManager.currentEnvironment
    val installationProgress = linuxManager.installationProgress

    fun setCurrentEnvironment(environment: LinuxEnvironmentManager.Environment?) {
        linuxManager.setCurrentEnvironment(environment)
    }

    suspend fun createEnvironment(
        distribution: LinuxEnvironmentManager.Distribution,
        name: String? = null
    ): Result<LinuxEnvironmentManager.Environment> {
        return linuxManager.createEnvironment(distribution, name)
    }

    suspend fun startEnvironment(environment: LinuxEnvironmentManager.Environment): Result<Process> {
        return linuxManager.startEnvironment(environment)
    }

    fun stopEnvironment(environment: LinuxEnvironmentManager.Environment) {
        linuxManager.stopEnvironment(environment)
    }

    suspend fun deleteEnvironment(environment: LinuxEnvironmentManager.Environment): Result<Unit> {
        return linuxManager.deleteEnvironment(environment)
    }

    fun getAvailableDistributions(): List<LinuxEnvironmentManager.Distribution> {
        return linuxManager.getAvailableDistributions()
    }
}

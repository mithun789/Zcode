package com.example.zcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zcode.ui.screens.SettingsScreen
import com.example.zcode.ui.screens.TerminalScreen
import com.example.zcode.ui.screens.FilesScreen
import com.example.zcode.ui.screens.SystemInfoScreen
import com.example.zcode.ui.screens.LinuxEnvironmentsScreen
import com.example.zcode.ui.theme.ZcodeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZcodeTheme {
                ZcodeApp()
            }
        }
    }
}

@Composable
fun ZcodeApp() {
    val navController = rememberNavController()
    var currentScreen by remember { mutableStateOf<NavigationItem>(NavigationItem.Terminal) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            ZcodeBottomNavigation(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    currentScreen = screen
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        ZcodeNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ZcodeBottomNavigation(
    currentScreen: NavigationItem,
    onScreenSelected: (NavigationItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val screens = listOf(
            NavigationItem.Terminal,
            NavigationItem.Linux,
            NavigationItem.Files,
            NavigationItem.Network,
            NavigationItem.SystemInfo,
            NavigationItem.Settings
        )

        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(screen.label, maxLines = 1) },
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun ZcodeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "terminal",
        modifier = modifier
    ) {
        composable(NavigationItem.Terminal.route) {
            TerminalScreen()
        }
        composable(NavigationItem.Linux.route) {
            LinuxEnvironmentsScreen()
        }
        composable(NavigationItem.Files.route) {
            com.example.zcode.ui.screens.FileExplorerPro(
                onOpenInTerminal = { directory ->
                    // Navigate to terminal tab
                    navController.navigate(NavigationItem.Terminal.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    // Terminal will open at the specified directory
                }
            )
        }
        composable(NavigationItem.Network.route) {
            com.example.zcode.ui.screens.NetworkMonitorScreen()
        }
        composable(NavigationItem.SystemInfo.route) {
            SystemInfoScreen()
        }
        composable(NavigationItem.Settings.route) {
            SettingsScreen()
        }
    }
}

sealed class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Terminal : NavigationItem(
        route = "terminal",
        label = "Terminal",
        icon = Icons.Filled.Code
    )

    data object Linux : NavigationItem(
        route = "linux",
        label = "Linux",
        icon = Icons.Filled.Computer
    )

    data object Files : NavigationItem(
        route = "files",
        label = "Files",
        icon = Icons.Filled.Folder
    )

    data object Network : NavigationItem(
        route = "network",
        label = "Network",
        icon = Icons.Filled.Wifi
    )

    data object SystemInfo : NavigationItem(
        route = "system_info",
        label = "System",
        icon = Icons.Filled.Info
    )

    data object Settings : NavigationItem(
        route = "settings",
        label = "Settings",
        icon = Icons.Filled.Settings
    )
}

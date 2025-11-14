package com.example.zcode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zcode.terminal.SessionManager

/**
 * SessionTabs - Tab bar for managing multiple terminal sessions (like Termux)
 *
 * Features:
 * - Display multiple session tabs
 * - Switch between sessions
 * - Close sessions
 * - Create new sessions
 * - Scrollable tab bar for many sessions
 */
@Composable
fun SessionTabs(
    sessions: List<SessionManager.SessionInfo>,
    activeSessionIndex: Int,
    onSessionClick: (Int) -> Unit,
    onCloseSession: (Int) -> Unit,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Scrollable tabs
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                sessions.forEachIndexed { index, session ->
                    SessionTab(
                        session = session,
                        isActive = index == activeSessionIndex,
                        onClick = { onSessionClick(index) },
                        onClose = { onCloseSession(index) },
                        modifier = Modifier.width(140.dp)
                    )
                }
            }
            
            // New session button
            IconButton(
                onClick = onNewSession,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Session",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Individual session tab
 */
@Composable
private fun SessionTab(
    session: SessionManager.SessionInfo,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        color = if (isActive) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        },
        tonalElevation = if (isActive) 4.dp else 0.dp,
        shadowElevation = if (isActive) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Session title
            Text(
                text = session.title,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                color = if (isActive) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )
            
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Session",
                    modifier = Modifier.size(16.dp),
                    tint = if (isActive) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )
            }
        }
    }
}

/**
 * Compact version for small screens - shows only active session and controls
 */
@Composable
fun CompactSessionTabs(
    sessions: List<SessionManager.SessionInfo>,
    activeSessionIndex: Int,
    onPreviousSession: () -> Unit,
    onNextSession: () -> Unit,
    onNewSession: () -> Unit,
    onCloseSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSession = sessions.getOrNull(activeSessionIndex)
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Previous session button
            IconButton(
                onClick = onPreviousSession,
                enabled = activeSessionIndex > 0
            ) {
                Text("◀", fontSize = 16.sp)
            }
            
            // Current session indicator
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = activeSession?.title ?: "No Session",
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = "${activeSessionIndex + 1}/${sessions.size}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Next session button
            IconButton(
                onClick = onNextSession,
                enabled = activeSessionIndex < sessions.size - 1
            ) {
                Text("▶", fontSize = 16.sp)
            }
            
            // New session button
            IconButton(onClick = onNewSession) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Session",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Close session button
            IconButton(
                onClick = onCloseSession,
                enabled = sessions.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Session",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Session info badge showing environment type
 */
@Composable
fun SessionEnvironmentBadge(
    environmentName: String?,
    modifier: Modifier = Modifier
) {
    if (environmentName != null) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = environmentName,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

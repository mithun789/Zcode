package com.example.zcode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ExtraKeysRow - Termux-style extra keys row for terminal input
 *
 * Provides quick access to special keys like Ctrl, Alt, ESC, Tab, arrows, etc.
 * This is a key feature of Termux that makes mobile terminal usage much easier.
 */
@Composable
fun ExtraKeysRow(
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    if (!visible) return
    
    var ctrlPressed by remember { mutableStateOf(false) }
    var altPressed by remember { mutableStateOf(false) }
    var shiftPressed by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // First row: Special keys and modifiers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // ESC key
            ExtraKey(
                text = "ESC",
                onClick = {
                    onKeyClick("\u001B") // ESC character
                },
                modifier = Modifier.weight(1f)
            )
            
            // TAB key
            ExtraKey(
                text = "TAB",
                onClick = {
                    onKeyClick("\t")
                },
                modifier = Modifier.weight(1f)
            )
            
            // CTRL modifier (toggle)
            ExtraKey(
                text = "CTRL",
                onClick = {
                    ctrlPressed = !ctrlPressed
                    if (ctrlPressed) {
                        altPressed = false
                        shiftPressed = false
                    }
                },
                isPressed = ctrlPressed,
                modifier = Modifier.weight(1f)
            )
            
            // ALT modifier (toggle)
            ExtraKey(
                text = "ALT",
                onClick = {
                    altPressed = !altPressed
                    if (altPressed) {
                        ctrlPressed = false
                        shiftPressed = false
                    }
                },
                isPressed = altPressed,
                modifier = Modifier.weight(1f)
            )
            
            // SHIFT modifier (toggle)
            ExtraKey(
                text = "⇧",
                onClick = {
                    shiftPressed = !shiftPressed
                    if (shiftPressed) {
                        ctrlPressed = false
                        altPressed = false
                    }
                },
                isPressed = shiftPressed,
                modifier = Modifier.weight(0.7f)
            )
            
            // Dash
            ExtraKey(
                text = "-",
                onClick = {
                    handleKeyWithModifiers("-", ctrlPressed, altPressed, shiftPressed, onKeyClick)
                    resetModifiers { ctrlPressed = false; altPressed = false; shiftPressed = false }
                },
                modifier = Modifier.weight(0.6f)
            )
            
            // Slash
            ExtraKey(
                text = "/",
                onClick = {
                    handleKeyWithModifiers("/", ctrlPressed, altPressed, shiftPressed, onKeyClick)
                    resetModifiers { ctrlPressed = false; altPressed = false; shiftPressed = false }
                },
                modifier = Modifier.weight(0.6f)
            )
            
            // Pipe
            ExtraKey(
                text = "|",
                onClick = {
                    handleKeyWithModifiers("|", ctrlPressed, altPressed, shiftPressed, onKeyClick)
                    resetModifiers { ctrlPressed = false; altPressed = false; shiftPressed = false }
                },
                modifier = Modifier.weight(0.6f)
            )
        }
        
        // Second row: Arrow keys and navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Up arrow
            ExtraKey(
                text = "↑",
                onClick = {
                    onKeyClick("\u001B[A") // Up arrow ANSI code
                },
                modifier = Modifier.weight(1f)
            )
            
            // Down arrow
            ExtraKey(
                text = "↓",
                onClick = {
                    onKeyClick("\u001B[B") // Down arrow ANSI code
                },
                modifier = Modifier.weight(1f)
            )
            
            // Left arrow
            ExtraKey(
                text = "←",
                onClick = {
                    onKeyClick("\u001B[D") // Left arrow ANSI code
                },
                modifier = Modifier.weight(1f)
            )
            
            // Right arrow
            ExtraKey(
                text = "→",
                onClick = {
                    onKeyClick("\u001B[C") // Right arrow ANSI code
                },
                modifier = Modifier.weight(1f)
            )
            
            // Home
            ExtraKey(
                text = "HOME",
                onClick = {
                    onKeyClick("\u001B[H")
                },
                modifier = Modifier.weight(1f)
            )
            
            // End
            ExtraKey(
                text = "END",
                onClick = {
                    onKeyClick("\u001B[F")
                },
                modifier = Modifier.weight(1f)
            )
            
            // Page Up
            ExtraKey(
                text = "PgUp",
                onClick = {
                    onKeyClick("\u001B[5~")
                },
                modifier = Modifier.weight(1f)
            )
            
            // Page Down
            ExtraKey(
                text = "PgDn",
                onClick = {
                    onKeyClick("\u001B[6~")
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual key button in the extra keys row
 */
@Composable
private fun ExtraKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPressed: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            },
            contentColor = if (isPressed) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSecondary
            }
        ),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isPressed) 0.dp else 2.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isPressed) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

/**
 * Handle key input with modifier keys (Ctrl, Alt, Shift)
 */
private fun handleKeyWithModifiers(
    key: String,
    ctrlPressed: Boolean,
    altPressed: Boolean,
    shiftPressed: Boolean,
    onKeyClick: (String) -> Unit
) {
    when {
        ctrlPressed -> {
            // Ctrl + key
            val char = key.firstOrNull()?.uppercaseChar()
            if (char != null && char in 'A'..'Z') {
                val controlChar = (char.code - 'A'.code + 1).toChar()
                onKeyClick(controlChar.toString())
            } else {
                onKeyClick(key)
            }
        }
        altPressed -> {
            // Alt + key sends ESC + key
            onKeyClick("\u001B$key")
        }
        shiftPressed -> {
            // Shift + key (uppercase or symbol)
            onKeyClick(key.uppercase())
        }
        else -> {
            // Normal key
            onKeyClick(key)
        }
    }
}

/**
 * Reset modifier keys after use
 */
private fun resetModifiers(reset: () -> Unit) {
    reset()
}

/**
 * Compact version of extra keys (single row for small screens)
 */
@Composable
fun CompactExtraKeysRow(
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val keys = listOf(
            "ESC" to "\u001B",
            "TAB" to "\t",
            "↑" to "\u001B[A",
            "↓" to "\u001B[B",
            "←" to "\u001B[D",
            "→" to "\u001B[C",
            "-" to "-",
            "/" to "/",
            "|" to "|",
            "~" to "~"
        )
        
        keys.forEach { (text, value) ->
            ExtraKey(
                text = text,
                onClick = { onKeyClick(value) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

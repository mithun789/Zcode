package com.termux.view

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.termux.terminal.TerminalSession
import kotlinx.coroutines.delay

/**
 * TerminalView - Responsive terminal with smooth UI/UX and automatic resolution handling
 */
@Composable
fun TerminalView(
    session: TerminalSession?,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    cursorColor: Color = Color.Green,
    fontSize: Int = 14
) {
    // Get device configuration for responsive design
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val isLandscape = screenWidth > screenHeight
    val isTablet = screenWidth >= 600
    
    // Calculate responsive values
    val responsiveFontSize = remember(screenWidth) {
        when {
            isTablet -> fontSize + 4 // Larger font for tablets
            screenWidth < 360 -> fontSize - 2 // Smaller for compact phones
            else -> fontSize
        }
    }
    
    val responsivePadding = remember(isTablet) {
        if (isTablet) 16.dp else 8.dp
    }
    
    val inputPadding = remember(isTablet) {
        if (isTablet) 12.dp else 8.dp
    }
    
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    var outputText by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    var shouldScrollToBottom by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    // Smooth animated padding for better UX
    val animatedBottomPadding by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 8.dp,
        animationSpec = tween(durationMillis = 200),
        label = "inputPadding"
    )

    // Update terminal output with optimized rendering
    LaunchedEffect(session) {
        while (true) {
            session?.emulator?.let { emulator ->
                try {
                    val screen = emulator.getScreen()
                    val lines = mutableListOf<String>()
                    val maxRows = emulator.getRows()
                    val maxCols = emulator.getColumns()
                    
                    for (row in 0 until maxRows) {
                        val lineBuilder = StringBuilder()
                        for (col in 0 until maxCols) {
                            val char = screen.getChar(col, row)
                            lineBuilder.append(char)
                        }
                        val line = lineBuilder.toString()
                        // Trim trailing spaces for cleaner output
                        if (line.isNotBlank() || lines.isNotEmpty()) {
                            lines.add(line.trimEnd())
                        }
                    }
                    val newOutput = lines.joinToString("\n")
                    if (newOutput != outputText) {
                        outputText = newOutput
                        shouldScrollToBottom = true
                    }
                } catch (_: Exception) {
                    // Ignore errors during rendering
                }
            }
            delay(80) // Optimized update frequency
        }
    }

    // Smooth scroll to bottom animation
    LaunchedEffect(shouldScrollToBottom) {
        if (shouldScrollToBottom) {
            delay(30)
            scrollState.animateScrollTo(
                value = scrollState.maxValue,
                animationSpec = tween(durationMillis = 150)
            )
            shouldScrollToBottom = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .imePadding()
            .clickable(enabled = !isFocused) {
                focusRequester.requestFocus()
                keyboardController?.show()
                isFocused = true
            }
    ) {
        // Terminal output area with responsive padding
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(responsivePadding)
        ) {
            Column {
                Text(
                    text = outputText.ifEmpty { "Zcode Terminal\nType commands below\n$ " },
                    color = textColor,
                    fontSize = responsiveFontSize.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = (responsiveFontSize + 4).sp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Current input preview with blinking cursor
                if (inputText.isNotEmpty() || isFocused) {
                    Row {
                        Text(
                            text = inputText,
                            color = textColor,
                            fontSize = responsiveFontSize.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        // Simple cursor indicator
                        Text(
                            text = "_",
                            color = cursorColor,
                            fontSize = responsiveFontSize.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Input area with smooth animations and responsive design
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = responsivePadding,
                    vertical = animatedBottomPadding
                )
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = { newValue ->
                    inputText = newValue
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = responsiveFontSize.sp,
                    fontFamily = FontFamily.Monospace,
                    background = backgroundColor
                ),
                cursorBrush = SolidColor(cursorColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotEmpty()) {
                            session?.writeText("$inputText\n")
                            inputText = ""
                            shouldScrollToBottom = true
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = backgroundColor.copy(alpha = 0.9f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                    if (isTablet) 8.dp else 6.dp
                                )
                            )
                            .padding(horizontal = inputPadding, vertical = inputPadding * 0.7f)
                    ) {
                        if (inputText.isEmpty()) {
                            Text(
                                text = if (isTablet) "Type command and press Enter..." else "Type command...",
                                color = textColor.copy(alpha = 0.4f),
                                fontSize = responsiveFontSize.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }

    // Auto-focus with delay
    LaunchedEffect(Unit) {
        delay(400)
        focusRequester.requestFocus()
        isFocused = true
    }
}


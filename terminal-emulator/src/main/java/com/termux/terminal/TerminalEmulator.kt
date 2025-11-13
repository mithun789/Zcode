package com.termux.terminal

/**
 * TerminalEmulator - Handles terminal emulation and ANSI escape sequences
 *
 * Processes:
 * - ANSI/VT100 escape sequences
 * - Terminal buffer management
 * - Cursor positioning
 * - Text formatting and colors
 */
class TerminalEmulator(
    private val changeCallback: TerminalSession.SessionChangedCallback,
    initialColumns: Int,
    initialRows: Int
) {

    private var mColumns = initialColumns
    private var mRows = initialRows

    // Terminal buffer (scrollback + screen)
    private val mScreen = TerminalBuffer(mColumns, mRows)

    // Current cursor position
    private var mCursorRow = 0
    private var mCursorCol = 0

    // Current text style
    private var mForegroundColor = TextStyle.COLOR_INDEX_FOREGROUND
    private var mBackgroundColor = TextStyle.COLOR_INDEX_BACKGROUND
    private var mEffect = 0

    /**
     * Append data from shell output
     */
    fun append(bytes: ByteArray, count: Int) {
        for (i in 0 until count) {
            processByte(bytes[i])
        }
    }

    /**
     * Process individual byte from shell
     */
    private fun processByte(byte: Byte) {
        val char = byte.toInt().toChar()

        when {
            char == '\n' -> {
                // Newline
                moveCursorDown()
                mCursorCol = 0
            }
            char == '\r' -> {
                // Carriage return
                mCursorCol = 0
            }
            char == '\b' -> {
                // Backspace
                if (mCursorCol > 0) {
                    mCursorCol--
                }
            }
            char == '\t' -> {
                // Tab - move to next tab stop (every 8 columns)
                mCursorCol = ((mCursorCol / 8) + 1) * 8
                if (mCursorCol >= mColumns) {
                    mCursorCol = mColumns - 1
                }
            }
            char.code < 32 || char.code == 127 -> {
                // Control character - ignore for now
            }
            else -> {
                // Regular character
                setChar(mCursorCol, mCursorRow, char)
                mCursorCol++

                if (mCursorCol >= mColumns) {
                    mCursorCol = 0
                    moveCursorDown()
                }
            }
        }
    }

    /**
     * Set character at position
     */
    fun setChar(col: Int, row: Int, char: Char) {
        if (row >= 0 && row < mRows && col >= 0 && col < mColumns) {
            mScreen.setChar(col, row, char, mForegroundColor, mBackgroundColor, mEffect)
        }
    }

    /**
     * Move cursor down, scroll if necessary
     */
    fun moveCursorDown() {
        mCursorRow++
        if (mCursorRow >= mRows) {
            mCursorRow = mRows - 1
            mScreen.scrollDownOneLine()
        }
    }

    /**
     * Set cursor column
     */
    fun setCursorCol(col: Int) {
        mCursorCol = col.coerceIn(0, mColumns - 1)
    }

    /**
     * Set character at current cursor position
     */
    fun setCharAtCursor(char: Char) {
        setChar(mCursorCol, mCursorRow, char)
    }

    /**
     * Update terminal size
     */
    fun updateSize(newColumns: Int, newRows: Int) {
        if (mColumns == newColumns && mRows == newRows) return

        mColumns = newColumns
        mRows = newRows
        mScreen.resize(newColumns, newRows)

        // Adjust cursor if out of bounds
        if (mCursorRow >= mRows) mCursorRow = mRows - 1
        if (mCursorCol >= mColumns) mCursorCol = mColumns - 1
    }

    /**
     * Get screen buffer
     */
    fun getScreen(): TerminalBuffer = mScreen

    /**
     * Get cursor row
     */
    fun getCursorRow(): Int = mCursorRow

    /**
     * Get cursor column
     */
    fun getCursorCol(): Int = mCursorCol

    /**
     * Get terminal columns
     */
    fun getColumns(): Int = mColumns

    /**
     * Get terminal rows
     */
    fun getRows(): Int = mRows

    /**
     * Clear screen
     */
    fun clearScreen() {
        mScreen.clear()
        mCursorRow = 0
        mCursorCol = 0
    }
}

/**
 * TerminalBuffer - Manages the terminal screen buffer
 */
class TerminalBuffer(private var mColumns: Int, private var mRows: Int) {

    private var mLines = Array(mRows) { CharArray(mColumns) { ' ' } }
    private var mColors = Array(mRows) { IntArray(mColumns) { 0 } }

    fun setChar(col: Int, row: Int, char: Char, fg: Int, bg: Int, effect: Int) {
        if (row in 0 until mRows && col in 0 until mColumns) {
            mLines[row][col] = char
            mColors[row][col] = encodeColor(fg, bg, effect)
        }
    }

    fun getChar(col: Int, row: Int): Char {
        return if (row in 0 until mRows && col in 0 until mColumns) {
            mLines[row][col]
        } else ' '
    }

    fun scrollDownOneLine() {
        // Move all lines up
        for (i in 0 until mRows - 1) {
            mLines[i] = mLines[i + 1]
            mColors[i] = mColors[i + 1]
        }
        // Clear bottom line
        mLines[mRows - 1] = CharArray(mColumns) { ' ' }
        mColors[mRows - 1] = IntArray(mColumns) { 0 }
    }

    fun resize(newColumns: Int, newRows: Int) {
        val newLines = Array(newRows) { CharArray(newColumns) { ' ' } }
        val newColors = Array(newRows) { IntArray(newColumns) { 0 } }

        // Copy existing content
        val minRows = minOf(mRows, newRows)
        val minCols = minOf(mColumns, newColumns)
        for (i in 0 until minRows) {
            for (j in 0 until minCols) {
                newLines[i][j] = mLines[i][j]
                newColors[i][j] = mColors[i][j]
            }
        }

        mLines = newLines
        mColors = newColors
        mColumns = newColumns
        mRows = newRows
    }

    fun clear() {
        for (i in 0 until mRows) {
            for (j in 0 until mColumns) {
                mLines[i][j] = ' '
                mColors[i][j] = 0
            }
        }
    }

    fun getLine(row: Int): String {
        return if (row in 0 until mRows) {
            String(mLines[row])
        } else ""
    }

    private fun encodeColor(fg: Int, bg: Int, effect: Int): Int {
        return (fg and 0xFF) or ((bg and 0xFF) shl 8) or ((effect and 0xFF) shl 16)
    }
}

/**
 * TextStyle - Terminal text style constants
 */
object TextStyle {
    const val COLOR_INDEX_FOREGROUND = 256
    const val COLOR_INDEX_BACKGROUND = 257

    const val CHARACTER_ATTRIBUTE_BOLD = 1
    const val CHARACTER_ATTRIBUTE_ITALIC = 2
    const val CHARACTER_ATTRIBUTE_UNDERLINE = 4
}


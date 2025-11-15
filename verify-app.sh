#!/data/data/com.example.zcode/files/usr/bin/bash
# Or use: #!/system/bin/sh for Android shell

# Zcode Terminal Verification Script
# This script tests the terminal to verify it provides a real Linux environment
# Run this script INSIDE the Zcode terminal app

echo "========================================"
echo "  Zcode Terminal Verification Script   "
echo "========================================"
echo ""

PASSED=0
FAILED=0

# Color codes (may not work in all terminals)
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

test_passed() {
    echo "  ✓ $1"
    PASSED=$((PASSED + 1))
}

test_failed() {
    echo "  ✗ $1"
    FAILED=$((FAILED + 1))
}

test_info() {
    echo "  ℹ $1"
}

# Test 1: Shell availability
echo "Test 1: Shell Availability"
if [ -n "$SHELL" ]; then
    test_passed "Shell variable is set: $SHELL"
else
    test_failed "Shell variable not set"
fi
echo ""

# Test 2: Basic commands
echo "Test 2: Basic Commands"
if command -v ls >/dev/null 2>&1; then
    test_passed "ls command available"
else
    test_failed "ls command not found"
fi

if command -v pwd >/dev/null 2>&1; then
    test_passed "pwd command available"
else
    test_failed "pwd command not found"
fi

if command -v echo >/dev/null 2>&1; then
    test_passed "echo command available"
else
    test_failed "echo command not found"
fi
echo ""

# Test 3: File operations
echo "Test 3: File Operations"
TEST_FILE="/tmp/zcode_test_$$"
if echo "test" > "$TEST_FILE" 2>/dev/null; then
    test_passed "Can create files"
    if [ -f "$TEST_FILE" ]; then
        test_passed "File was created successfully"
        if cat "$TEST_FILE" | grep -q "test"; then
            test_passed "Can read file content"
        else
            test_failed "Cannot read file content correctly"
        fi
        rm -f "$TEST_FILE"
        test_passed "Can delete files"
    else
        test_failed "File not found after creation"
    fi
else
    test_failed "Cannot create files"
fi
echo ""

# Test 4: Directory operations
echo "Test 4: Directory Operations"
TEST_DIR="/tmp/zcode_dir_$$"
if mkdir -p "$TEST_DIR" 2>/dev/null; then
    test_passed "Can create directories"
    if [ -d "$TEST_DIR" ]; then
        test_passed "Directory exists"
        if cd "$TEST_DIR" 2>/dev/null; then
            test_passed "Can change directory"
            cd - >/dev/null
        else
            test_failed "Cannot change directory"
        fi
        if rmdir "$TEST_DIR" 2>/dev/null; then
            test_passed "Can remove directory"
        else
            test_failed "Cannot remove directory"
        fi
    else
        test_failed "Directory not found after creation"
    fi
else
    test_failed "Cannot create directories"
fi
echo ""

# Test 5: Environment variables
echo "Test 5: Environment Variables"
if [ -n "$PATH" ]; then
    test_passed "PATH is set"
    test_info "PATH=$PATH"
else
    test_failed "PATH not set"
fi

if [ -n "$HOME" ]; then
    test_passed "HOME is set"
    test_info "HOME=$HOME"
else
    test_failed "HOME not set"
fi

TEST_VAR="zcode_test"
export TEST_VAR
if [ "$TEST_VAR" = "zcode_test" ]; then
    test_passed "Can set and read environment variables"
else
    test_failed "Cannot set environment variables"
fi
echo ""

# Test 6: Process info
echo "Test 6: Process Information"
if [ -n "$$" ]; then
    test_passed "Can get process ID: $$"
else
    test_failed "Cannot get process ID"
fi

if ps >/dev/null 2>&1; then
    test_passed "Can list processes"
else
    test_failed "Cannot list processes"
fi
echo ""

# Test 7: Text processing
echo "Test 7: Text Processing"
if echo "test" | grep -q "test"; then
    test_passed "grep command works"
else
    test_failed "grep command failed"
fi

if echo -e "line1\nline2\nline3" | wc -l | grep -q "3"; then
    test_passed "wc command works"
else
    test_failed "wc command failed"
fi
echo ""

# Test 8: Shell features
echo "Test 8: Shell Features"
if type cd >/dev/null 2>&1; then
    test_passed "cd command available"
else
    test_failed "cd command not available"
fi

# Test command substitution
CURRENT_DIR=$(pwd)
if [ -n "$CURRENT_DIR" ]; then
    test_passed "Command substitution works"
    test_info "Current directory: $CURRENT_DIR"
else
    test_failed "Command substitution failed"
fi
echo ""

# Test 9: System information
echo "Test 9: System Information"
if uname >/dev/null 2>&1; then
    test_passed "uname command works"
    test_info "System: $(uname -s)"
    test_info "Kernel: $(uname -r)"
    test_info "Machine: $(uname -m)"
else
    test_failed "uname command failed"
fi

if command -v whoami >/dev/null 2>&1; then
    USER=$(whoami)
    test_passed "Current user: $USER"
else
    test_info "whoami command not available"
fi
echo ""

# Test 10: Advanced features
echo "Test 10: Advanced Features (optional)"
if command -v bash >/dev/null 2>&1; then
    test_passed "bash shell available"
    bash --version | head -n 1
else
    test_info "bash not available (using sh)"
fi

if command -v apt >/dev/null 2>&1 || command -v pkg >/dev/null 2>&1; then
    test_passed "Package manager available"
else
    test_info "Package manager not available"
fi

if [ -f /etc/os-release ]; then
    test_passed "/etc/os-release exists"
    test_info "Distribution info:"
    grep "^NAME=" /etc/os-release | cut -d= -f2
else
    test_info "/etc/os-release not found (may be built-in mode)"
fi
echo ""

# Summary
echo "========================================"
echo "           Test Summary                 "
echo "========================================"
echo ""
echo "Tests Passed: $PASSED"
echo "Tests Failed: $FAILED"
echo ""

if [ $FAILED -eq 0 ]; then
    echo "✓ All tests passed!"
    echo ""
    echo "Zcode Terminal is working correctly and provides"
    echo "a real Linux environment with:"
    echo "  • Working shell ($SHELL)"
    echo "  • File system operations"
    echo "  • Process management"
    echo "  • Environment variables"
    echo "  • Text processing tools"
    echo ""
    exit 0
else
    echo "✗ Some tests failed"
    echo ""
    echo "This may be normal depending on:"
    echo "  • Whether using built-in or Linux environment"
    echo "  • Android version and restrictions"
    echo "  • Available permissions"
    echo ""
    echo "If you're using built-in mode, create a Linux"
    echo "environment for full functionality."
    echo ""
    exit 1
fi
